package moe.chionlab.wechatmomentstat.parser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.eelly.sellerbuyer.net.BaseNetConfig;

import java.io.File;
import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.daemon.WXFriendDataApplication;
import moe.chionlab.wechatmomentstat.util.GetWXNumUtil;
import moe.chionlab.wechatmomentstat.util.LogUtil;
import moe.chionlab.wechatmomentstat.util.SnsInfoUtil;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * 微信朋友圈数据库操作类
 * <p>
 * Created by chiontang on 2/12/16.
 */
public class SnsReader {

    Class SnsDetail = null;
    Class SnsDetailParser = null;
    Class SnsObject = null;
    Parser parser = null;
    ArrayList<SnsInfo> snsList = new ArrayList<SnsInfo>();
    String currentUserId = "";

    public SnsReader(Class SnsDetail, Class SnsDetailParser, Class SnsObject) {
        this.SnsDetail = SnsDetail;
        this.SnsDetailParser = SnsDetailParser;
        this.SnsObject = SnsObject;
        this.parser = new Parser(SnsDetail, SnsDetailParser, SnsObject);
    }

    /****
     * 读取数据库数据
     *
     * @throws Throwable
     */
    public void run() throws Throwable {
        LogUtil.e("Querying Sns database.");
        queryDatabase();

        //TODO 不需要下面的操作
        //Task.saveToJSONFile(this.snsList, Config.EXT_DIR + "/all_sns.json", false);
    }

    public ArrayList<SnsInfo> getSnsList() {
        return this.snsList;
    }

    protected void queryDatabase() throws Throwable {
        String dbPath = Config.EXT_DIR + "/SnsMicroMsg.db";
        if (!new File(dbPath).exists()) {
            LogUtil.e("DB file not found");
            throw new Exception("DB file not found");
        }
        snsList.clear();
        SQLiteDatabase database = openOrCreateDatabase(dbPath, null);
        //getCurrentUserIdFromDatabase(database);

        //
        this.currentUserId = GetWXNumUtil.getInstance(WXFriendDataApplication.context).getWxID();
        LogUtil.e("Current userID=" + this.currentUserId);

        //TODO
        long lastTime = SnsInfoUtil.getLastTime(WXFriendDataApplication.context, currentUserId);
        Cursor cursor = database.query("SnsInfo", //
                new String[]{"SnsId", "userName", "createTime", "content", "attrBuf"}, " createTime>? ",//
                new String[]{lastTime + ""}, "", "", "createTime DESC", "");//
        while (cursor.moveToNext()) {
            addSnsInfoFromCursor(cursor);
        }
        cursor.close();
        database.close();
    }

    protected void getCurrentUserIdFromDatabase(SQLiteDatabase database) throws Throwable {
        //TODO 这里，直接写死了当前用户的id
        /*Cursor cursor = database.query("snsExtInfo2", new String[]{"userName"}, "ROWID=?", new String[]{"1"}, "", "", "", "1");
        if (cursor.moveToNext()) {
            this.currentUserId = cursor.getString(cursor.getColumnIndex("userName"));
        }
        cursor.close();
        LogUtil.e("Current userID=" + this.currentUserId);*/

        //
        if (BaseNetConfig.isOnline(BaseNetConfig.getNetEnvironment(WXFriendDataApplication.context))) {
            this.currentUserId = GetWXNumUtil.EELLY_ONLINE_WXID;//changjiatongbu
        } else {
            this.currentUserId = GetWXNumUtil.EELLY_TEST_WXID;//eellytest
        }

        LogUtil.e("Current userID=" + this.currentUserId);
    }

    protected void addSnsInfoFromCursor(Cursor cursor) throws Throwable {
        byte[] snsDetailBin = cursor.getBlob(cursor.getColumnIndex("content"));
        byte[] snsObjectBin = cursor.getBlob(cursor.getColumnIndex("attrBuf"));
        SnsInfo newSns = parser.parseSnsAllFromBin(snsDetailBin, snsObjectBin);

        //
        if (newSns == null) {
            return;
        }

        String wxnum = getWxNum(newSns);
        if (newSns != null) {
            if (!TextUtils.isEmpty(wxnum)) {
                newSns.wxnum = wxnum;
            } else {
                newSns.wxnum = newSns.authorId;
            }
        }

        for (int i = 0; i < snsList.size(); i++) {
            if (snsList.get(i).id.equals(newSns.id)) {
                return;
            }
        }

        if (newSns.authorId.equals(this.currentUserId)) {
            newSns.isCurrentUser = true;
        }

        for (int i = 0; i < newSns.comments.size(); i++) {
            if (newSns.comments.get(i).authorId.equals(this.currentUserId)) {
                newSns.comments.get(i).isCurrentUser = true;
            }
        }

        for (int i = 0; i < newSns.likes.size(); i++) {
            if (newSns.likes.get(i).userId.equals(this.currentUserId)) {
                newSns.likes.get(i).isCurrentUser = true;
            }
        }

        snsList.add(newSns);
        //newSns.print();
    }

    /****
     * @param newSns
     * @return
     */
    private String getWxNum(SnsInfo newSns) {
        if (newSns == null) {
            return "";
        }
        String wxnum = GetWXNumUtil.getInstance(WXFriendDataApplication.context).getWxNum(newSns.authorId);
        return TextUtils.isEmpty(wxnum) ? "" : wxnum;
    }


}
