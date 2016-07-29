package moe.chionlab.wechatmomentstat.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * 不需要做任务事
 * DO NOT do anything in this Service!<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class GuardService2 extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
}
