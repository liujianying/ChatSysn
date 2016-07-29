package moe.chionlab.wechatmomentstat.parser;

import android.content.Context;
import android.util.Log;

import com.eelly.seller.common.net.NewBaseServerApi;
import com.eelly.sellerbuyer.net.ApiListener;
import com.eelly.sellerbuyer.net.NewApiRequest;
import com.eelly.sellerbuyer.net.ResponseParser;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.Model.FriendData;
import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.Model.UploadResult;
import moe.chionlab.wechatmomentstat.util.LogUtil;

/**
 * 微信上传数据的网络API
 *
 * @author 杨情红
 */
public class WXDataApi extends NewBaseServerApi {
    public WXDataApi(Context context) {
        super(context);
    }


    /****
     * 微信上传朋友圈的数据
     *
     * @param list
     * @param listener
     * @return
     */
    public NewApiRequest<?> uploadData(ArrayList<SnsInfo> list, ApiListener<UploadResult> listener) {
        try {
            NewApiRequest<UploadResult> apiRequest = newApiRequest(listener);
            apiRequest.setApp("Mall");
            apiRequest.setServerName("Store\\Service\\FocusMessagesService");
            apiRequest.setMethod("addCrawledData");

            Log.i("LogTag", mGson.toJson(list));

            //
            apiRequest.setArgs("{\"params\":" + mGson.toJson(list) + "}");

            return apiRequest.request(new ResponseParser<UploadResult>() {
                @Override
                public UploadResult parse(JsonElement je) {
                    LogUtil.e(je.toString());
                    return mGson.fromJson(je, UploadResult.class);
                }
            });
        } catch (OutOfMemoryError error) {

        }
        return null;
    }

    /****
     * 微信上传朋友圈的数据
     *
     * @param dataStr  json形式的字符串集合数据
     * @param listener
     * @return
     */
    public NewApiRequest<?> uploadData(String dataStr, ApiListener<UploadResult> listener) {
        try {
            NewApiRequest<UploadResult> apiRequest = newApiRequest(listener);
            apiRequest.setApp("Mall");
            apiRequest.setServerName("Store\\Service\\FocusMessagesService");
            apiRequest.setMethod("addCrawledData");

            //
            apiRequest.setArgs("{\"params\":" + dataStr + "}");

            return apiRequest.request(new ResponseParser<UploadResult>() {
                @Override
                public UploadResult parse(JsonElement je) {
                    return mGson.fromJson(je, UploadResult.class);
                }
            });
        } catch (OutOfMemoryError error) {

        }
        return null;
    }

    /****
     * 微信上传好友的数据
     *
     * @param data     好友的数据
     * @param listener
     * @return
     */
    public NewApiRequest<?> uploadFriendData(FriendData data, ApiListener<Boolean> listener) {
        try {
            NewApiRequest<Boolean> apiRequest = newApiRequest(listener);
            apiRequest.setApp("Mall");
            apiRequest.setServerName("Store\\Service\\FocusMessagesService");
            apiRequest.setMethod("addCrawledFriendsData");

            //
            apiRequest.setArgs(mGson.toJson(data));

            return apiRequest.request(new ResponseParser<Boolean>() {
                @Override
                public Boolean parse(JsonElement je) {
                    int status = 0;
                    try {
                        status = je.getAsJsonObject().get("status").getAsInt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return status == 1;
                }
            });

        } catch (OutOfMemoryError error) {
        }

        return null;

    }
}