package moe.chionlab.wechatmomentstat.Model;

/**
 * 上传的微信好友的item数据(好友的数据)
 *
 * @author 杨情红
 */
public class FriendItemData {

    /****
     * 微信内部使用的id
     */
    private String wx_id;
    /****
     * 微信号
     */
    private String wxnum;

    /***
     * 用户头像（96*96 px）
     */
    private String small_avatar;
    /***
     * 用户头像（640*640 px）
     */
    private String medium_avatar;

    public String getWx_id() {
        return wx_id;
    }

    public void setWx_id(String wx_id) {
        this.wx_id = wx_id;
    }

    public String getWxnum() {
        return wxnum;
    }

    public void setWxnum(String wxnum) {
        this.wxnum = wxnum;
    }

    public String getSmall_avatar() {
        return small_avatar;
    }

    public void setSmall_avatar(String small_avatar) {
        this.small_avatar = small_avatar;
    }

    public String getMedium_avatar() {
        return medium_avatar;
    }

    public void setMedium_avatar(String medium_avatar) {
        this.medium_avatar = medium_avatar;
    }

    @Override
    public String toString() {
        return "{" +
                "wx_id='" + wx_id + '\'' +
                ", wxnum='" + wxnum + '\'' +
                ", small_avatar='" + small_avatar + '\'' +
                ", medium_avatar='" + medium_avatar + '\'' +
                '}';
    }
}
