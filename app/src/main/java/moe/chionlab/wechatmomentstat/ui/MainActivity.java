package moe.chionlab.wechatmomentstat.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eelly.seller.wechat.R;
import com.eelly.sellerbuyer.net.BaseNetConfig;

import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.common.Share;
import moe.chionlab.wechatmomentstat.daemon.TaskService;
import moe.chionlab.wechatmomentstat.parser.SnsStat;
import moe.chionlab.wechatmomentstat.parser.Task;
import moe.chionlab.wechatmomentstat.parser.WXDataApi;
import moe.chionlab.wechatmomentstat.util.GetWXNumUtil;


/****
 * 首页
 */
public class MainActivity extends AppCompatActivity {

    Task task = null;
    SnsStat snsStat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = new Task(this.getApplicationContext());
        setContentView(R.layout.activity_main);


        //
        InitializeSQLCipher();
        task.testRoot();


        ((RadioGroup) findViewById(R.id.rg_environment)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                String environment = radioButton.getText().toString().trim();
                if (!TextUtils.isEmpty(environment)) {
                    BaseNetConfig.setNetEnvironment(MainActivity.this, environment);
                }
            }
        });


        //
        ((Button) findViewById(R.id.setting_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 请求accesstoken
                getAccessToken();
            }
        });
        //
        ((Button) findViewById(R.id.launch_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new RunningTask().execute();


                Intent service = new Intent(MainActivity.this, TaskService.class);
                stopService(service);

                //
                startService(service);
            }
        });

        TextView descriptionHtmlTextView = (TextView) findViewById(R.id.description_html_textview);
        descriptionHtmlTextView.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionHtmlTextView.setText(Html.fromHtml(getResources().getString(R.string.description_html)));

    }


    /****
     * 请求accesstoken
     */
    private void getAccessToken() {
        ArrayList<SnsInfo> list = new ArrayList<SnsInfo>();
        SnsInfo info = new SnsInfo();
        info.id = "13123131";
        info.wxnum = "1111";
        info.authorId = "wx_id_1213xslfjsd";
        info.authorName = "衣联同步圈";
        info.mediaList = new ArrayList<>();
        info.mediaList.add("http://pica.nipic.com/2008-06-19/2008619135523588_2.jpg");
        info.mediaList.add("http://pic4.nipic.com/20090912/2727232_115623071663_2.jpg");
        info.mediaList.add("http://€.nipic.com/20090415/2378046_125609099_2.jpg");
        list.add(info);
        new WXDataApi(this).uploadData(list, null);
    }

    /****
     * 初始化获取微信加密数据库工具类
     */
    private void InitializeSQLCipher() {
        GetWXNumUtil.getInstance(this.getApplicationContext());
    }

    /****
     * 没有使用
     */
    @Deprecated
    class RunningTask extends AsyncTask<Void, Void, Void> {

        Throwable error = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                task.copySnsDB();
                task.initSnsReader();
                task.snsReader.run();
                snsStat = new SnsStat(task.snsReader.getSnsList());
            } catch (Throwable e) {
                this.error = e;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void voidParam) {
            super.onPostExecute(voidParam);
            ((Button) findViewById(R.id.launch_button)).setText(R.string.launch);
            ((Button) findViewById(R.id.launch_button)).setEnabled(true);
            if (this.error != null) {
                Toast.makeText(MainActivity.this, R.string.not_rooted, Toast.LENGTH_LONG).show();
                Log.e("wechatmomentstat", "exception", this.error);
                return;
            }
            Share.snsData = snsStat;
            Intent intent = new Intent(MainActivity.this, MomentStatActivity.class);
            startActivity(intent);
        }
    }



}
