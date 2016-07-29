package com.eelly.seller.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {

	/** 应用包名 */
	public static String packageInfo_packageName = "";

	/** 应用版本名 */
	public static String packageInfo_versionName = "";

	/** 应用版本号 */
	public static int packageInfo_versionCode = 0;

	public static String accessToken = "";
	public static long differTime = 0L;

	/**
	 * token错误状态码
	 */
	public static final int TOKEN_ERROR_CODE = 231501;

	/**
	 * token过期状态码
	 */
	public static final int TOKEN_EXPIRE_CODE = 231502;

	public static final int TOKEN_EXPIRE_CODE1 = 231305;

	public static final int TOKEN_EXPIRE_CODE2 = 210003;

	public static final int TOKEN_EXPIRE_CODE3 = 210001;

	public static final int TOKEN_EXPIRE_CODE4 = 200001;

	/**
	 * 需要登录状态码
	 */
	public static final int NEED_LOGIN_CODE = 220001;

	/**
	 * 没有权限状态码
	 */
	public static final int NO_PERMISSION_CODE = 200009;

	/**
	 * 客服电话
	 */
	public static final String SERVICE_TELEPHONE = "4006688038";

	/**
	 * 买家app下载地址
	 */
	public static final String BUYER_APP_URL = "http://pifaquan.eelly.com/index.php?m=api&c=download&a=buyerApp";

	/**
	 * 开店学堂URL
	 */
	public static final String EELLY_SCHOOL_URL = "/eellyschool/index.html?isInApp=1";

	/**
	 * 绑定微商城教程URL
	 */
	public static final String EELLY_BIND_MICROSHOP_URL = "/eellyschool/detailsindex.html?id=3&from=manage&isInApp=1";

	/**
	 * 对象
	 */
	public static final String OBJECT = "object";

	/**
	 * 对象数组
	 */
	public static final String OBJECTS = "objects";

	/**
	 * Flag
	 */
	public static final String FLAG = "flag";

	/**
	 * param1
	 */
	public static final String PARAM1 = "param1";

	/**
	 * 获取数据列表的类型-客户
	 */
	public static final String CONDITION_CUSTOMER = "1";

	public static final int ACCESS_TOKEN_INVALID = 707;

	/**
	 * 获取数据列表的类型-等级
	 */
	public static final String CONDITION_GRADE = "2";

	/**
	 * 推送新款中的选择客户等级
	 */
	public static final String CUSTOMER_GROUP_SELECT_GRADE = "5";

	/**
	 * 客户分组-编辑等级分组
	 */
	public static final String CUSTOMER_EDIT_GRADE = "1";

	/**
	 * 客户分组-标签分组
	 */
	public static final String CUSTOMER_GROUP_TAG = "2";

	/**
	 * 客户分组-地区分组
	 */
	public static final String CUSTOMER_GROUP_REGION = "3";

	/**
	 * 客户分组-全部客户
	 */
	public static final String CUSTOMER_GROUP_ALL = "4";

	/**
	 * 添加客户的类型
	 */
	public static final String CUSTOMER_ADD_TYPE = "addtype";

	/**
	 * 编辑客户
	 */
	public static final String EDIT_CUSTOMER = "1";

	/**
	 * 自定义添加客户
	 */
	public static final String CUSTOME_ADD_CUSTOMER = "2";

	/**
	 * 客户导入
	 */
	public static final String IMPORT_CUSTOMER = "3";

	/**
	 * 根据客户id请求客户信息后添加到客户
	 */
	public static final String ADD_CUSTOMER_FOR_REQUEST = "4";

	/**
	 * 按时间排序
	 */
	public static final int SORT_TIME = 1;

	/**
	 * 按价格排序
	 */
	public static final int SORT_PRICE = 2;

	/**
	 * 推送新款,选择商品排序方式-降序
	 */
	public static final int PUSH_NEWSTYLE_DESC = 1;

	/**
	 * 推送新款,选择商品排序方式-升序
	 */
	public static final int PUSH_NEWSTYLE_ASC = 2;

	/**
	 * 选择推送新款
	 */
	public static final int TYPE_SELECT_STYLE = 1;

	/**
	 * 推送新款
	 */
	public static final int TYPE_PUSH_STYLE = 2;

	/**
	 * 进货记录编辑类型(编辑或添加)
	 */
	public static final String EDIT_RECORD_TYPE = "type";

	/**
	 * 编辑
	 */
	public static final String TYPE_EDIT = "1";

	/**
	 * 添加
	 */
	public static final String TYPE_ADD = "2";

	/**
	 * 显示进货记录详情
	 */
	public static final String TYPE_PURCHASE_RECORD_DETAIL = "3";

	/**
	 * 推送新款的商品
	 */
	public static final String PUSH_STYLE_GOODS = "2";

	/**
	 * 添加进货记录的商品
	 */
	public static final String ADD_PURCHASE_RECORD_GOODS = "1";

	/**
	 * 商品列表
	 */
	public static final String CUSTOMER_GOODS_LIST = "goodsList";

	/**
	 * 客户列表
	 */
	public static final String CUSTOMER_LIST = "customerlist";

	/** 已选的客户列表 **/
	public static final String SELECTED_CUSTOMER_LIST = "select_customerlist";

	/**
	 * 客户对象
	 */
	public static final String CUSTOMER_OBJ = "customer";

	/**
	 * 消息编码
	 */
	public static final int MSG_WHAT = 0x123;

	/**
	 * 商城客户
	 */
	public static final int WALL_CONTACT = 0;

	/**
	 * 非商城客户
	 */
	public static final int ISNOT_WALL_CONTACT = 1;

	/**
	 * 已添加客户
	 */
	public static final int CONTACT_ADDED = 2;

	/**
	 * 客户id
	 */
	public static final String CUSTOMER_ID = "customerid";

	/**
	 * 来自商城客户
	 */
	public static final int FOMR_SHOP = 1;

	/**
	 * 来自自定义添加客户
	 */
	public static final int FOMR_CUSTOM_ADD_CONTACT = 2;

	/**
	 * 来自手机通讯录
	 */
	public static final int FOMR_ADDRESS_BOOK = 3;

	/**
	 * 来自搜索添加客户
	 */
	public static final int FOMR_SEARCH_ADD_CONTACT = 4;

	/**
	 * 来自扫描二维码
	 */
	public static final int FOMR_QRCODE_ADD_CONTACT = 5;

	/**
	 * 推送新款的选择全部联系人
	 */
	public static final String CUSTOMER_GROUP_SELECT_CUSTOMER = "2";

	/** 手机号码 */
	public static final String PHONE_NUMBER = "phone_number";

	/** 手机验证码 */
	public static final String PHONE_CAPTCHA = "phone_captcha";

	/** 用户信息存储文件名 **/
	public static final String SHARED_USERINFO = "user_info";

	public static final String SHARE_REASON = "reason";

	public static final String SHARE_ENTITY_STATUS = "status";

	public static final String SHARE_REALNAME = "realName";

	/** 用户信息UID **/
	public static final String SHARED_UID = "uid";

	/** 用户信息user_id **/
	public static final String SHARED_USERID = "user_id";

	/** 用户信息mobile **/
	public static final String SHARED_MOBILE = "mobile";

	/** 用户信息userName **/
	public static final String SHARED_USERNAME = "userName";

	/** 用户信息nickName **/
	public static final String SHARED_NICKNAME = "nickName";

	/** 用户信息portrait **/
	public static final String SHARED_PORTRAIT = "portrait";

	/** 用户信息storeName **/
	public static final String SHARED_STORENAME = "storeName";

	/** 用户信息rank **/
	public static final String SHARED_RANK = "rank";

	/** 是否有运费模板 **/
	public static final String FRIGHT_TEMPLATE = "fright_temp";

	/** 用户信息storeStatus **/
	public static final String SHARED_STORESTATUS = "storeStatus";

	/** 用户信息store isopen **/
	public static final String SHARED_STORE_IS_OPEN = "store_is_open";

	/** 用户信息store hasAppStore **/
	public static final String SHARED_STORE_HAS_STORE = "hasAppStore";

	/** 用户信息store vision **/
	public static final String SHARED_STORE_VISION = "storeVision";

	/** 用户信息tokenExpired **/
	public static final String SHARED_TOKENEXPIRED = "tokenExpired";

	/** 用户信息tokenKey **/
	public static final String SHARED_TOKENKEY = "tokenKey";

	/** 商品id **/
	public static final String GOODS_ID = "goods_id";

	/** 类别名 **/
	public static final String CATEGORY_NAME = "category_name";

	/** 类别id **/
	public static final String CATEGORY_ID = "category_id";

	/** 类别id **/
	public static final String CATEGORY_ID2 = "category_id2";

	/** 找回密码-用户信息 */
	public static final String FIND_PASSWORD_INFO = "find_password_info";

	/** 价格区间 可选价格 */
	public static final List<Integer> PRICE_RANGES = Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120, 150, 180, 200, 250, 300, 400, 500,
			600, 700, 800, 900, 1000);

	/** 订单数据 */
	public static final String ORDER_DATA = "order_data";

	/** 订单sn */
	public static final String ORDER_SN = "order_sn";

	/** 订单索引 */
	public static final String ORDER_POSITION = "order_position";

	/** 操作订单 */
	public static final int EDIT_ORDER_DATA = 160;

	/** 店铺数据 */
	public static final String STORE_DATA = "store_data";

	/** 店铺头像 发生变化时置值 */
	public static String STORE_PORTRAIT = "";

	/** 店铺名称 发生变化时置值 */
	public static String STORE_NAME = "";

	/** 设置里的事件 */
	public static final String SETTING_EVENT = "setting_event";

	/**
	 * 图片预览 跳到预览ViewPagerActivity 传Arraylist<String>
	 */
	public static final String IMAGE_URLS = "image_url";

	/** 推送绑定flag */
	public static final String BIND_FLAG = "bind_flag";

	/** 推送绑定appid */
	public static final String BIND_APPID = "bind_appid";

	/** 推送绑定userId */
	public static final String BIND_USERID = "bind_userId";

	/** 推送绑定channelId */
	public static final String BIND_CHANNELID = "bind_channelId";

	/** 推送绑定requestId */
	public static final String BIND_REQUESTID = "bind_requestId";

	/** 退出程序或是登出的时候，发送该广播 */
	public static final String EXIT = "com.eelly.selly.close";

	/** 登录广播 */
	public static final String BROADCAST_LOGIN = "com.eelly.selly.broadcast.login";

	/** 推送——存储的交易通知的数量 */
	public static final String PUSH_DEAL_MESSAGE = "push_deal_message";

	/** 线下客户 **/
	public static int OFFLINE_CUSTOMER = 0;

	/**
	 * 密码管理标示
	 */
	public static String PASSWORD_MANAGER = "flag";

	public static String SHOWFLAG = "false";

	public static final String FROMPWDMANAGER = "fromManager";

	public static final String MOBILENUM = "mobilenum";

	public static final String EMAIL = "email";

	/**
	 * 支付密码
	 */
	public static final String PAY_PASS = "payPass";

	/**
	 * 支付密码绑定手机
	 */
	public static final String PAY_PHONE = "phone";

	/**
	 * 支付密码-密保
	 */
	public static final String PASS_PROTECT = "passProtect";

	/**
	 * 根据用户登录次数获取对应的等级图片下标值
	 * 
	 * @param userRank
	 * @return
	 */
	public static int getUserImageLevel(int userRank) {
		int level = 1;
		if (userRank <= 50) {// 过客
			return level;
		}
		if (userRank <= 200) {// 新客
			level = 2;
		} else if (userRank <= 400) {// 微客
			level = 3;
		} else if (userRank <= 700) {// 小客
			level = 4;
		} else if (userRank <= 1100) {// 中客
			level = 5;
		} else if (userRank <= 2000) {// 大客
			level = 6;
		} else if (userRank <= 4000) {// 超客
			level = 7;
		} else if (userRank > 4000) {// 超客
			level = 8;
		}
		return level;
	}

	/**
	 * 根据店铺分数获取对应店铺等级图标
	 * 
	 * @param storeRank
	 * @return
	 */
	public static int getStoreImageLevel(int storeRank) {
		int level = 0;
		if (storeRank <= 0) {
			return level;
		} else if (storeRank <= 1) {// 1个红星
			level = 1;
		}
		if (storeRank <= 5) {// 2个红星
			level = 2;
		} else if (storeRank <= 10) {// 3个红星
			level = 3;
		} else if (storeRank <= 15) {// 4个红星
			level = 4;
		} else if (storeRank <= 25) {// 5个红星
			level = 5;
		} else if (storeRank <= 50) {// 1个蓝钻
			level = 6;
		} else if (storeRank <= 100) {// 2个蓝钻
			level = 7;
		} else if (storeRank <= 200) {// 3个蓝钻
			level = 8;
		} else if (storeRank <= 500) {// 4个蓝钻
			level = 9;
		} else if (storeRank <= 1000) {// 5个蓝钻
			level = 10;
		} else if (storeRank <= 2000) {// 1个蓝冠
			level = 11;
		} else if (storeRank <= 5000) {// 2个蓝冠
			level = 12;
		} else if (storeRank <= 10000) {// 3个蓝冠
			level = 13;
		} else if (storeRank <= 20000) {// 4个蓝冠
			level = 14;
		} else if (storeRank <= 50000) {// 5个蓝冠
			level = 15;
		} else if (storeRank <= 100000) {// 1个黄冠
			level = 16;
		} else if (storeRank <= 200000) {// 2个黄冠
			level = 17;
		} else if (storeRank <= 500000) {// 3个黄冠
			level = 18;
		} else if (storeRank <= 1000000) {// 4个黄冠
			level = 19;
		} else {// 5个黄冠
			level = 20;
		}
		return level;
	}

	/**
	 * 根据等级级别获取对应图标
	 * 
	 * @param degree
	 * @return
	 */
	public static int getGradeImageLevel(int degree) {
		if (degree >= 5)
			return 5;
		return degree;
	}

}
