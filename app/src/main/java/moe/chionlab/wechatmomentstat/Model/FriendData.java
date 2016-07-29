package moe.chionlab.wechatmomentstat.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 上传的微信好友数据
 *
 * @author 杨情红
 */
public class FriendData implements Serializable {

    /****
     * 当前用户的微信号
     */
    private String serviceNum;
    /****
     * 操作（start 开始 end结束）
     */
    private String operation;
    /****
     * 好友总数
     */
    private int count;
    /***
     * 好友列表数据
     */
    private ArrayList<FriendItemData> params;

    public String getServiceNum() {
        return serviceNum;
    }

    public void setServiceNum(String serviceNum) {
        this.serviceNum = serviceNum;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<FriendItemData> getParams() {
        return params;
    }

    public void setParams(ArrayList<FriendItemData> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "FriendData{" +
                "serviceNum='" + serviceNum + '\'' +
                ", operation='" + operation + '\'' +
                ", count=" + count +
                ", params=" + params +
                '}';
    }
}
