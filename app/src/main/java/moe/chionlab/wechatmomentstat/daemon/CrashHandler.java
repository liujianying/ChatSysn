package moe.chionlab.wechatmomentstat.daemon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * 全局异常捕获类：
 * 主要用于收集应用崩溃信息，并上传到服务器，方便改善应用
 * UncaughtException处理类,当程序发生Uncaught异常的时候,
 * 有该类来接管程序,并记录发送错误报告.
 *
 * 需要权限
 *  android.permission.WRITE_EXTERNAL_STORAGE
 *  android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
 *  android.permission.INTERNET
 * <pre>
 *
 * @author
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 程序的Context对象
    private Context mContext;

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用来存储设备信息和异常信息
    private LinkedHashMap<String, String> infos = new LinkedHashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    // 日志文件保存的目录
    private String crashDirPath;

    // 日志文件保存的目录
    private File crashDir;

    // 日志文件名的前缀
    private String filePrefix;

    // 日志文件名的后缀：相当于文件类型
    private String fileSuffix;

    // 上传到服务器的逻辑回调
    private CrashCallback crashCallback;
    // 屏幕的宽度
    private int screenWidth;
    // 屏幕的高度
    private int screenHeight;

    // CrashHandler实例
    private static CrashHandler instance;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /****
     * 初始化
     *
     * @param context
     * @param crashDirPath 日志文件保存的目录
     * @param filePrefix   日志文件名的前缀,如 "crash"
     * @param fileSuffix   日志文件名的后缀：相当于文件类型,如".log"
     * @param callback     上传到服务器的逻辑接口：主要写怎么上传到服务器
     *                     <p>
     *                     <pre>
     *                     生成的文件名为：crashyyyy-MM-dd HH-mm-ss.log
     *                     </pre>
     */
    public void init(Context context, String crashDirPath, String filePrefix, String fileSuffix, CrashCallback callback) {
        this.mContext = context;
        // 获取系统默认的UncaughtException处理器
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 日志文件保存的目录
        this.crashDirPath = crashDirPath;
        if (!TextUtils.isEmpty(crashDirPath)) {
            this.crashDir = new File(crashDirPath);
            if (!this.crashDir.exists()) {
                boolean isOK = this.crashDir.mkdirs();
                Log.e(TAG, "mkdirs crash dir is :" + isOK);
            }
        } else {
            throw new RuntimeException("the crash directory is null");
        }

        // 日志文件名的前缀
        this.filePrefix = filePrefix;
        // 日志文件名的后缀：相当于文件类型
        this.fileSuffix = fileSuffix;

        // 上传到服务器的逻辑回调
        this.crashCallback = callback;

        this.screenWidth = getScreenWidth(context);
        this.screenHeight = getScreenHeight(context);

        // TODO 开始打开一个子线程，检查日志目录，上传崩溃日志，并删除上传成功的日志文件。
        startUploadCrashInfo();
    }

    /****
     * 开始上传崩溃日志文件到服务器
     */
    private void startUploadCrashInfo() {
        // 没有设置crashCallback,下面的操作也没有必要执行了
        if (crashCallback == null) {
            return;
        }
        // 目录为null
        if (this.crashDir == null) {
            Log.e(TAG, "the crash dir is null");
            return;
        }
        // 目录不存在
        if (!this.crashDir.exists()) {
            Log.e(TAG, "the crash dir is not existed");
            return;
        }

        // 开始打开一个子线程，检查日志目录，上传崩溃日志，并删除上传成功的日志文件。
        new Thread() {
            public void run() {

                // TODO 使用了fileFilter
                // File[] files = crashDir.listFiles();
                File[] files = getCrashFiles();
                if (files == null || files.length < 1) {
                    return;
                }
                for (File file : files) {
                    // TODO 使用了fileFilter
                    // if (isCrashFile(file)) {// is crash file
                    String crashInfo = read(file);
                    if (TextUtils.isEmpty(crashInfo)) {
                        continue;
                    }
                    if (crashCallback != null) {
                        crashCallback.upload(crashDir, file, crashInfo);
                    }
                    // }
                }
            }

            ;
        }.start();
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,应用出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String appName = pi.applicationInfo.loadLabel(pm).toString();
                String packageName = context.getPackageName();
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("appName", appName);
                infos.put("packageName", packageName);
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "an error occured when collect package info", e);
        }

        infos.put("OSVersionCode", Build.VERSION.CODENAME);
        infos.put("OSVersionName", Build.VERSION.RELEASE);
        infos.put("OSSDKVersion", Build.VERSION.SDK_INT + "");
        infos.put("screenWidth", this.screenWidth + "");
        infos.put("screenHeight", this.screenHeight + "");

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                boolean isAccessible = field.isAccessible();
                if (!isAccessible) {
                    field.setAccessible(true);
                }

                infos.put(field.getName(), field.get(null) + "");

                if (!isAccessible) {
                    field.setAccessible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
        // 增加其他信息
        infos.put("otherInfo", "begin:");
        if (crashCallback != null) {
            crashCallback.addOtherCrashInfo(infos);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();

        // 拼接崩溃时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("崩溃时间 =" + sdf.format(new Date()) + "\n");

        // 拼接应用信息和android系统信息
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        // 拼接错误信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append("崩溃日志内容=" + result);
        // 将上面拼接的信息写到本地文件中
        try {
            // 生成文件名
            String time = dateFormatter.format(new Date());
            String fileName = this.filePrefix + time + this.fileSuffix;
            if (this.crashDir == null) {
                Log.e(TAG, "the crash dir is null");
                return null;
            }
            // 目录不存在，则创建
            if (!this.crashDir.exists()) {
                if (!crashDir.mkdirs()) {
                    return null;
                }
            }
            File file = new File(crashDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 将输入流解析成字符串
     *
     * @param file 输入流
     * @return String
     */
    public String read(File file) {
        if (file == null) {
            return "";
        }
        try {
            FileInputStream is = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            return baos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /****
     * 是否是崩溃日志文件
     *
     * @param file
     * @return
     */
    public boolean isCrashFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            if (!TextUtils.isEmpty(fileName) && fileName.indexOf(filePrefix) == 0 && fileName.endsWith(fileSuffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取崩溃日志文件集合
     *
     * @return
     */
    private File[] getCrashFiles() {
        if (this.crashDir == null) {
            return null;
        }
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return isCrashFile(pathname);
            }
        };
        return this.crashDir.listFiles(filter);
    }

    /****
     * 删除单个崩溃日志文件
     *
     * @param file
     * @return 删除是否成功
     */
    public boolean delCrashFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            if (!TextUtils.isEmpty(fileName) && fileName.indexOf(filePrefix) == 0 && fileName.endsWith(fileSuffix)) {
                boolean isOk = file.delete();
                if (isOk) {
                    Log.e(TAG, "file is deleted:   " + file.getAbsolutePath());
                }
                return isOk;
            }
        }
        return false;
    }

    /**
     * 删除所有日志文件
     */
    public void delAllCrashFiles() {
        if (crashDir == null || !crashDir.exists() || !crashDir.isDirectory()) {
            return;
        }
        File[] files = crashDir.listFiles();
        for (File file : files) {
            if (isCrashFile(file)) {
                boolean isOk = file.delete();
                if (isOk) {
                    Log.e(TAG, "file is deleted:   " + file.getAbsolutePath());
                }
            }
        }// end of for

    }// end of method

    /****
     * 上传崩溃日志到服务器的接口
     *
     * @author
     */
    public interface CrashCallback {
        /****
         * 上传崩溃日志到服务器
         *
         * @param crashDir  崩溃日志文件所在的目录
         * @param crashFile 崩溃日志文件
         * @param crashInfo 崩溃日志文件中的内容
         */
        public void upload(File crashDir, File crashFile, String crashInfo);

        /****
         * 增加其他信息
         *
         * @param map
         */
        public void addOtherCrashInfo(LinkedHashMap<String, String> map);
    }

    /**
     * 得到屏幕的高度
     *
     * @param context
     */
    private int getScreenHeight(Context context) {
        if (context == null) {
            return -1;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到屏幕的宽度
     *
     * @param context
     */
    private int getScreenWidth(Context context) {
        if (context == null) {
            return -1;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public CrashCallback getCrashCallback() {
        return crashCallback;
    }

    public void setCrashCallback(CrashCallback crashCallback) {
        this.crashCallback = crashCallback;
    }

    public String getCrashDirPath() {
        return crashDirPath;
    }

    public File getCrashDir() {
        return crashDir;
    }

}
