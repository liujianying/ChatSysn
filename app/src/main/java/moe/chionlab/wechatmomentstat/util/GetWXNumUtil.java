package moe.chionlab.wechatmomentstat.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.eelly.sellerbuyer.net.BaseNetConfig;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.AppConfig;
import moe.chionlab.wechatmomentstat.Model.FriendItemData;
import moe.chionlab.wechatmomentstat.daemon.TaskService;
import moe.chionlab.wechatmomentstat.daemon.WXFriendDataApplication;
import moe.chionlab.wechatmomentstat.parser.Config;

/**
 * 获取微信号工具类
 *
 * @author 杨情红
 */
public class GetWXNumUtil {

    //微信号数据库保存的路径
    public static final String DB_EXDIR_PATH = Config.EXT_DIR + "/EnMicroMsg.db";


    //测试用的
    public static final String DB_PATH_TEST = "/data/data/com.tencent.mm/MicroMsg/ef6da0516102b7f987eff2988014d74d/EnMicroMsg.db";
    public static final String DB_PATH2 = "/mnt/sdcard2/EnMicroMsg.db";
    public static final String DB_PATH = "/data/data/com.tencent.mm/MicroMsg/ab0898d6632e2e8f44927cca73a01e4d/EnMicroMsg.db";


    /***
     * 微信uin：test,local环境用的：eellytest的uin
     */
    public static final String EELLY_TEST_UIN = AppConfig.EELLY_TEST_UIN;

    /****
     * 微信uin：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_UIN = AppConfig.EELLY_ONLINE_UIN;
    /***
     * 微信号：test,local环境用的：eellytest
     */
    public static final String EELLY_TEST_WXNUM = AppConfig.EELLY_TEST_WXNUM;

    /****
     * 微信号：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_WXNUM = AppConfig.EELLY_ONLINE_WXNUM;
    /***
     * 微信ID：test,local环境用的：eellytest
     */
    public static final String EELLY_TEST_WXID = AppConfig.EELLY_TEST_WXID;

    /****
     * 微信ID：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_WXID = AppConfig.EELLY_ONLINE_WXID;


    private static GetWXNumUtil instance;
    private SQLiteDatabase db;
    /****
     * 数据库密码
     */
    private String dbPassword;

    private GetWXNumUtil(Context context) {
        SQLiteDatabase.loadLibs(context);
    }

    public static GetWXNumUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (GetWXNumUtil.class) {
                if (instance == null) {
                    instance = new GetWXNumUtil(context);
                }
            }
        }
        return instance;
    }


    /****
     * 取得微信号
     *
     * @param userName 微信ID
     * @return
     */
    public String getWxNum(String userName) {

        if (TextUtils.isEmpty(userName)) {
            LogUtil.e("getWxNum()>>>userName is null");
            return "";
        }
        Cursor cursor = null;
        try {
            initDB();
            if (db == null) {
                return "";
            }

            //开始查询微信号
            cursor = db.rawQuery("select alias from rcontact where username=? ", new String[]{userName});
            //Cursor cursor = database.rawQuery("select username from rcontact", null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String alias = cursor.getString(cursor.getColumnIndex("alias"));
                    LogUtil.e("getWxNum()>>> get alias=" + alias);
                    return alias;
                }
            } else {
                LogUtil.e("getWxNum()>>> get alias no data");
            }
        } catch (Exception e) {
            e.printStackTrace();

            //TODO 删除数据库文件，重启任务服务
            File dbFile = new File(Config.EXT_DIR + "/EnMicroMsg.db");
            if (dbFile.exists()) {
                dbFile.delete();
            }

            //
            Intent service = new Intent(WXFriendDataApplication.context, TaskService.class);
            WXFriendDataApplication.context.stopService(service);
            WXFriendDataApplication.context.startService(service);
            return "";
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }

        }
        //
        return "";
    }

    /****
     * 取得微信号列表
     *
     * @return
     */
    public ArrayList<String> getWxNumList() {

        Cursor cursor = null;
        try {
            initDB();
            if (db == null) {
                return null;
            }
            //开始查询微信号
            //TODO SQL: select case when alias='' or alias is null then username else alias end  from rcontact where type=3 and username != 'weixin'
            cursor = db.rawQuery("select case when alias='' or alias is null then username else alias end  from rcontact where type=3 and username != 'weixin'", null);
            if (cursor != null && cursor.getCount() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    String alias = cursor.getString(0);
                    LogUtil.e("getWxNum()>>> get alias=" + alias);
                    if (!TextUtils.isEmpty(alias)) {
                        list.add(alias);
                    }
                }
                return list;
            } else {
                LogUtil.e("getWxNum()>>> get alias no data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }

        }
        //
        return null;
    }

    /****
     * 取得微信好友列表
     *
     * @return
     */
    public ArrayList<FriendItemData> getWxFriendList() {

        Cursor cursor = null;
        try {
            initDB();
            if (db == null) {
                return null;
            }
            //开始查询微信号
            //TODO SQL: select username, case when alias='' or alias is null then username else alias end alias  from rcontact where type=3 and username != 'weixin'
            //TODO SQL(增加头像): select rcontact.username,case when rcontact.alias='' or rcontact.alias is null then rcontact.username else rcontact.alias end alias, img_flag.reserved1 bigphoto,img_flag.reserved2 smallphoto from rcontact,img_flag  where rcontact.type!=0 and rcontact.type!=33 and rcontact.username != 'weixin' and rcontact.username =img_flag.username
            cursor = db.rawQuery("select rcontact.username,case when rcontact.alias='' or rcontact.alias is null then rcontact.username else rcontact.alias end alias, img_flag.reserved1 bigphoto,img_flag.reserved2 smallphoto from rcontact,img_flag  where rcontact.type!=0 and rcontact.type!=33 and rcontact.username != 'weixin' and rcontact.username =img_flag.username", null);
            if (cursor != null && cursor.getCount() > 0) {
                ArrayList<FriendItemData> list = new ArrayList<FriendItemData>();
                while (cursor.moveToNext()) {
                    String alias = cursor.getString(cursor.getColumnIndex("alias"));
                    LogUtil.e("getWxFriendList()>>> alias=" + alias);
                    if (!TextUtils.isEmpty(alias)) {
                        String username = cursor.getString(cursor.getColumnIndex("username"));
                        String bigphoto = cursor.getString(cursor.getColumnIndex("bigphoto"));
                        String smallphoto = cursor.getString(cursor.getColumnIndex("smallphoto"));
                        FriendItemData itemData = new FriendItemData();
                        itemData.setWx_id(username);
                        itemData.setWxnum(alias);
                        itemData.setMedium_avatar(bigphoto);
                        itemData.setSmall_avatar(smallphoto);
                        list.add(itemData);
                    }
                }
                return list;
            } else {
                LogUtil.e("getWxFriendList()>>> no data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }

        }
        //
        return null;
    }

    /***
     * 初始化数据库
     */
    private void initDB() {
        //获取数据库密码
        if (TextUtils.isEmpty(dbPassword)) {
            String eellyUin = EELLY_TEST_UIN;
            if (BaseNetConfig.isOnline(BaseNetConfig.getNetEnvironment(WXFriendDataApplication.context))) {
                eellyUin = EELLY_ONLINE_UIN;
            }
            String md5Str = getMessageDigest((getIMEI(WXFriendDataApplication.context) + eellyUin).getBytes());
            if (!TextUtils.isEmpty(md5Str)) {
                try {
                    dbPassword = md5Str.substring(0, 7);
                    Log.e("test", "wx  dbPassword=" + dbPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //TODO 要解密成功，必须要有下面的代码 HOOK
        if (db == null) {

            File databaseFile = new File(DB_EXDIR_PATH);
            if (!databaseFile.exists()) {
                LogUtil.e("getWxNum()>>> " + DB_EXDIR_PATH + " not exists....");
                return;
            }

            SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
                public void preKey(SQLiteDatabase database) {
                }

                public void postKey(SQLiteDatabase database) {
                    database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！
                }
            };
            db = SQLiteDatabase.openOrCreateDatabase(databaseFile, dbPassword, null, hook);
        }
    }

    /****
     * 取得当前用户ID
     *
     * @return
     */
    public String getWxID() {


        //
        if (BaseNetConfig.isOnline(BaseNetConfig.getNetEnvironment(WXFriendDataApplication.context))) {
            return EELLY_ONLINE_WXID;//changjiatongbu
        } else {
            return EELLY_TEST_WXID;//eellytest
        }


        //TODO 直接返回写死的微信ID
       /* Cursor cursor = null;
        try {
            initDB();
            if (db == null) {
                return "";
            }
            //
            String alias = BaseNetConfig.isOnline(BaseNetConfig.getNetEnvironment(WXFriendDataApplication.context))//
                    ? EELLY_ONLINE_WXNUM : EELLY_TEST_WXNUM;
            cursor = db.rawQuery("select username from rcontact where alias=? ", new String[]{alias});
            //Cursor cursor = database.rawQuery("select username from rcontact", null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String username = cursor.getString(cursor.getColumnIndex("username"));
                    LogUtil.e("getWxNum()>>> get username=" + username);
                    return username;
                }
            } else {
                LogUtil.e("getWxNum()>>> get username no data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }

        }
        //
        return "";*/
    }


    /****
     * 关闭数据库
     */
    public void closeDB() {
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
        }
    }


    /****
     * 获取IMEI
     *
     * @param context
     * @return
     */
    private String getIMEI(Context context) {
        if (context == null) {
            return "";
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /****
     * MD5
     *
     * @param bytes
     * @return
     */
    private String getMessageDigest(byte[] bytes) {
        char[] arrayOfChar1 = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(bytes);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i)
                    return new String(arrayOfChar2);
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
        }
        return null;
    }


}
