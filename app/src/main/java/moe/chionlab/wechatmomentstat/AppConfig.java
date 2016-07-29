package moe.chionlab.wechatmomentstat;

/**
 * 应用的配置类
 *
 * @author 杨情红
 */
public class AppConfig {

    /****
     * 是否打印日志
     */
    public static boolean debug = true;
    /*****************************TODO 时间的配置start***************************************************/
    /****
     * 同步朋友圈数据的间隔时间
     */
    public static final int TASK_UPLOAD_WX_MOMENT_PERIOD = 3 * 60000;
    /****
     * TODO 同步微信好友的间隔时间
     */
    //public static int TASK_FRIEND_PERIOD = 2 * 60 * 60000;//2小时
    public static final int TASK_UPLOAD_WX_FRIEND_PERIOD = 120000;//5分钟

    /****
     * 上传朋友圈最新数据的分页：每页数量
     */
    public static final int UPLOAD_WX_MOMENT_PAGE_COUNT = 20;
    /****
     * 上传好友数据的分页：每页数量
     */
    public static final int UPLOAD_WX_FRIEND_PAGE_COUNT = 30;
    /*****************************TODO 时间的配置end***************************************************/

    /*****************************TODO 微信号的配置start***************************************************/
    /***
     * 微信uin：test,local环境用的：eellytest的uin
     */
    public static final String EELLY_TEST_UIN = "34241526";

    /****
     * 微信uin：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_UIN = "343735180";
    /***
     * 微信号：test,local环境用的：eellytest
     */
    public static final String EELLY_TEST_WXNUM = "eellytest";

    /****
     * 微信号：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_WXNUM = "changjiatongbu";
    /***
     * 微信ID：test,local环境用的：eellytest
     */
    public static final String EELLY_TEST_WXID = "wxid_cprqjw4os3pj12";

    /****
     * 微信ID：online环境用的：changjiatongbu
     */
    public static final String EELLY_ONLINE_WXID = "wxid_bknwx1lx58p322";


    /*****************************TODO 微信号的配置END***************************************************/


}
