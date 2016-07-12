package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: SysConfig.java
 * </p>
 * 
 * <p>
 * Description: 系统配置管理类,包括系统基础信息、系统邮箱发送配置、手机短息发送配置、殴飞充值接口配置等所有系统基础相关内容
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sysconfig")
public class SysConfig extends IdEntity {
	private String title;// 商城标题
	private String keywords;// 商城SEO关键字
	private String description;// 商城SEO描述
	private String address;// 商城访问地址，填写商城网址
	private String poweredby;// 前端poweredby
	private String copyRight;// 版权信息
	private String meta_generator;// meta的generator信息
	private String meta_author;// meta的author信息
	private String company_name;// 公司简称，用在超级管理平台的左下角显示
	private String uploadFilePath;// 用户上传文件路径
	private String sysLanguage;// 系统语言
	private int integralRate;// 充值积分兑换比率
	private boolean smsEnbale;// 短信平台开启
	private String smsURL;// 短信平台发送地址
	private String smsUserName;// 短信平台用户名
	private String smsPassword;// 短信平台用户密码
	private String smsTest;// 短信测试发送账号
	private boolean emailEnable;// 邮件是否开启
	private String emailHost;// stmp服务器
	private int emailPort;// stmp端口
	private String emailUser;// 发件人
	private String emailUserName;// 邮箱用户名
	private String emailPws;// 邮箱密码
	private String emailTest;// 邮件发送测试
	private String websiteName;// 网站名称
	private String hotSearch;// 热门搜索
	@Column(columnDefinition = "varchar(255) default 'blue' ")
	private String websiteCss;// 当前网站平台样式，默认为蓝色
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory websiteLogo;// 网站logo
	@Column(columnDefinition = "LongText")
	private String codeStat;// 三方代码统计
	private boolean websiteState;// 网站状态(开/关)
	private boolean visitorConsult;// 游客咨询
	@Column(columnDefinition = "LongText")
	private String closeReason;// 网站关闭原因
	private String securityCodeType;// 验证码类型
	private boolean securityCodeRegister;// 前台注册验证
	private boolean securityCodeLogin;// 前台登陆验证
	private boolean securityCodeConsult;// 商品咨询验证
	private String imageSuffix;// 图片的后缀名
	private String imageWebServer;// 图片服务器地址
	private int imageFilesize;// 允许图片上传的最大值
	private int smallWidth;// 最小尺寸像素宽
	private int smallHeight;
	private int middleWidth;// 中尺寸像素宽
	private int middleHeight;
	private int bigWidth;// 大尺寸像素高
	private int bigHeight;
	private boolean integral;// 积分
	private boolean integralStore;// 开启积分商城
	private boolean voucher;// 代金券
	private boolean deposit;// 预存款
	private boolean groupBuy;// 团购
	@Column(columnDefinition = "int default 300")
	private int group_meal_gold;// 团购套餐价格 按每30天多少金币算,默认是300个金币每30天
	private boolean gold;// 金币
	@Column(columnDefinition = "int default 1")
	private int goldMarketValue;// 金币市值，默认是一个金币抵制1元
	private int memberRegister;// 会员注册(赠送积分)
	private int memberDayLogin;// 会员每日登陆(赠送积分)
	private int indentComment;// 订单评论(赠送积分)
	private int consumptionRatio;// 消费比例(赠送积分)
	private int everyIndentLimit;// 每个订单(赠送积分)
	private String imageSaveType;// 图片保存类型
	private int complaint_time;// 举报失效，以订单完成时间开始计算，单位为天
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory storeImage;// 默认店铺标志
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory goodsImage;// 默认商品图片
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory memberIcon;// 默认用户图标
	private boolean store_allow;// 允许店铺申请
	@Column(columnDefinition = "LongText")
	private String user_level;// 会员等级数据，使用json管理
	@Column(columnDefinition = "LongText")
	private String templates;// 店铺样式管理使用字符串管理
	@Column(columnDefinition = "LongText")
	private String store_payment;// 平台支付方式启用情况，使用json管理，如{"alipay":true,"99bill":false}
	@Column(columnDefinition = "LongText")
	private String share_code;// 百度分享代码
	private boolean ztc_status;// 直通车状态
	@Column(columnDefinition = "int default 0")
	private int ztc_goods_view;// 直通车商品显示方式，0为没有任何限制，1为按照商品分类显示，在商品分类搜索页中是否按照该页中分类进行显示，
	private int ztc_price;// 直通车起价，用户可以任意设定价格，必须大于该价格，价格越高排序也靠前
	@Column(columnDefinition = "bit default 0")
	private boolean second_domain_open;// 是否开通二级域名
	@Column(columnDefinition = "int default 0")
	@Lock
	private int domain_allow_count;// 店铺二级域名运行修改次数，0为无限制
	@Column(columnDefinition = "LongText")
	@Lock
	private String sys_domain;// 系统保留二级域名
	@Column(columnDefinition = "bit default 0")
	private boolean qq_login;// 是否允许QQ登录
	private String qq_login_id;// QQ登录Id
	private String qq_login_key;// QQ登录key
	@Column(columnDefinition = "LongText")
	private String qq_domain_code;// QQ域名验证信息
	@Column(columnDefinition = "bit default 0")
	private boolean sina_login;// 是否允许新浪微博登录
	private String sina_login_id;// 新浪微博Id
	private String sina_login_key;// 新浪微博key
	@Column(columnDefinition = "LongText")
	private String sina_domain_code;// 新浪微博域名验证信息
	private Date lucene_update;// 全文索引更新时间
	@Column(columnDefinition = "int default 0")
	private int combin_status;// 组合销套开启状态
	@Column(columnDefinition = "int default 50")
	private int combin_amount;// 组合销套餐费用，单位为金币/30天
	@Column(columnDefinition = "int default 5")
	private int combin_count;// 组合销售中的最大商品数量
	@Column(columnDefinition = "int default 1")
	private int combin_scheme_count;// 组合销售方案数量
	@OneToMany(mappedBy = "config")
	private List<Accessory> login_imgs = new ArrayList<Accessory>();// 登录页面的左侧图片
	@Column(columnDefinition = "LongText")
	private String service_telphone_list;// 平台客服电话，一行一个
	@Column(columnDefinition = "LongText")
	private String service_qq_list;// 平台客服QQ，一行一个
	@OneToOne
	private Accessory admin_login_logo;// 平台登录页的左上角Logo
	@OneToOne
	private Accessory admin_manage_logo;// 平台管理中心左上角的Logo
	@Column(columnDefinition = "int default 3")
	@Lock
	private int auto_order_notice;// 卖家发货后达到该时长，给买家发送即将自动确认收货的短信、邮件提醒
	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_confirm;// 卖家发货后，达到该时间系统自动确认收货

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_return;// 买家申请退货，到达该时间未能输入退货单号及物流公司，退货失败并且订单变为待评价，订单状态为49

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_evaluate;// 订单确认收货后到达该时间，系统自动管理订单评价功能
	@Column(columnDefinition = "int default 7")
	@Lock
	private int grouplife_order_return;// 生活类团购退货时效，如电影票，购买付款后，在该天数内可以申请退款
	@Column(columnDefinition = "LongText")
	private String kuaidi_id;// kuaidi100快递查询Id，卖家需自行向http://www.kuaidi100.com申请接口id，下个版本公司内部出版接口查询
	@Column(columnDefinition = "LongText")
	private String kuaidi_id2;// 快递100收费推送接口，能够快速查询系统快递信息
	@Column(columnDefinition = "int default 0")
	private int kuaidi_type;// 快递100类型，0为免费版快递查询，1为收费版快递查询，默认为免费版快递查询
	@Column(columnDefinition = "varchar(255)")
	private String currency_code;// 货币符号，默认为人民币¥
	@Lock
	@Column(columnDefinition = "int default 0")
	private int weixin_store;// 微信商城的状态，0为为开启，1为开启状态
	@OneToOne
	private Accessory weixin_qr_img;// 微信二维码图片
	@Lock
	private String weixin_account;// 微信账号
	@Lock
	private String weixin_token;// 微信token，申请开发者时自己填写的token
	@Lock
	private String weixin_appId;// 微信App Id，申请开发者成功后微信生成的AppId
	@Lock
	private String weixin_appSecret;// 微信AppSecret，申请开发者成功后微信生成的AppSecret
	@Lock
	@Column(columnDefinition = "LongText")
	private String weixin_welecome_content;// 关注微信时候的欢迎词
	@Lock
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_weixin_logo;// 平台微信商城ogo
	@Column(columnDefinition = "int default 1")
	private int payoff_count;// 月结算次数，可以设置为1次、2次、3次、4次
	private Date payoff_date;// 下次结算日期，每天0:00计算系统下次结算时间
	@Column(columnDefinition = "int default 0")
	private int payoff_mag_type;// 结算通知消息类型，0为系统默认消息，1为自定义消息
	@Column(columnDefinition = "LongText")
	private String payoff_mag_default;// 系统默认结算通知
	@Column(columnDefinition = "LongText")
	private String payoff_mag_auto;// 自定义结算通知
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_sale;// 系统总销售金额
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_commission;// 系统总销售佣金
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount;// 系统总结算金额(应结算)
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount_reality;// 系统总结算金额(实际结算)
	@Lock
	private boolean ofcard;// 是否开通殴飞充值接口
	@Lock
	private String ofcard_userid;// 殴飞充值接口用户名
	@Lock
	private String ofcard_userpws;// 殴飞充值接口密码
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ofcard_mobile_profit;// 手机充值的利润值，用户充值手机时候，系统会查询殴飞接口实际收费，根据殴飞的收费加上该利润值，作为用户缴纳的最终金额
	@Lock
	@Column(columnDefinition = "int default 0")
	private int app_download;// 是否启用App下载，默认为0不启用，1为启用
	@Lock
	private String android_download;// 安卓客户端下载地址
	private String android_version;// 安卓客户端版本号
	@Lock
	private String ios_download;// Iphone客户端下载地址
	private String ios_version;// Iphone客户端版本号
	@Lock
	@Column(columnDefinition = "int default 0")
	private int app_seller_download;// 是否启用商家App下载，默认为0不启用，1为启用
	@Lock
	private String android_seller_download;// 安卓商家客户端下载地址
	private String android_seller_version;// 安卓商家客户端版本号
	@Lock
	private String ios_seller_download;// Iphone商家客户端下载地址
	private String ios_seller_version;// Iphone商家客户端版本号
	@Lock
	@Column(columnDefinition = "int default 0")
	private int buygift_status;// 满就送开启状态 0为关闭 1为开启
	@Column(columnDefinition = "int default 300")
	private int buygift_meal_gold;// 满就送促销价格 按每30天多少金币算,默认是300个金币每30天
	@Lock
	@Column(columnDefinition = "int default 0")
	private int enoughreduce_status;// 满就减开启状态 0为关闭 1为开启
	@Column(columnDefinition = "int default 300")
	private int enoughreduce_meal_gold;// 满就送促销价格 按每30天多少金币算,默认是300个金币每30天
	@Column(columnDefinition = "int default 300")
	private int enoughreduce_max_count;// 第三方店铺最大满就送数量，默认1个
	@Column(columnDefinition = "int default 0")
	private int email_buy;// 邮件购买状态，0为商家不可购买邮件,1为商家可购买邮件
	@Column(columnDefinition = "int default 0")
	private int email_buy_cost;// 邮件购买金额，以100封邮件为单位
	@Column(columnDefinition = "int default 0")
	private int sms_buy;// 短信购买状态，0为商家不可购买短信,1为商家可购买短信
	@Column(columnDefinition = "int default 0")
	private int sms_buy_cost;// 短信购买金额，以100条短信为单位
	@Column(columnDefinition = "int default 0")
	private int whether_free;// 商城是否开启了0元试用0元试用 0为否 1为是
	@Column(columnDefinition = "int default 0")
	private int lucenen_queue;// lucene写索引是否启用队列，默认是发布商品后即刻写索引，效率较低，启用队列后效率高,但是会滞后写入,0：不启用，1：启用
	@Column(columnDefinition = "int default 0")
	private int sms_queue;// 短信发送是否启用队列，默认是不启用队列，相关发送短信的地方都及时发送短信，启用队列后效率高，但是会滞后发送,0：不启用，1：启用
	@Column(columnDefinition = "int default 0")
	private int email_queue;// 邮件发送是否启用队列，默认是不启用队列，相关发送邮件的地方都是及时发送邮件，启用队列后效率高，但是会滞后发送,0：不启用，1：启用
	@Column(columnDefinition = "int default 0")
	private int circle_open;// 系统圈子开启状态，1为开启
	@Column(columnDefinition = "int default 1")
	private int circle_count;// 用户可创建圈子数量，可以在平台中设置
	@Column(columnDefinition = "int default 0")
	private int circle_limit;// 用户创建圈子限制信息，（保存用户等级信息，只有用户等级大于等于该等级时才可以申请创建圈子，0为不限制，1为铜牌，2银牌，3金牌，4超级）
	@Column(columnDefinition = "int default 0")
	private int circle_audit;// 申请圈子是否需要审核，0为不需要审核，1为需要审核
	@Column(columnDefinition = "int default 0")
	private int publish_post_limit;// 用户发帖限制信息（保存用户等级信息，只有用户等级大于等于该等级时才可以发布帖子，0为不限制，1为铜牌，2银牌，3金牌，4超级）
	@Column(columnDefinition = "int default 0")
	private int publish_seller_limit;// 发帖身份限制，0为所有人可发帖，1为只能商家可发帖
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory welcome_img;// 欢迎词图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory qr_logo;// 二维码中心Logo图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory Website_ico;// 网站ICO图片
	@Column(columnDefinition = "int default 0")
	private int qr_login;// 是否开启二维码登录，默认为0不开启，1为开启，开启后使用app扫描二维码即可完成pc端登录
	private String apiKey;// 手机推送的API key,使用百度的推送服务,系统集成百度推送接口
	private String secretKey;// 手机推送的Secret key,使用百度的推送服务
	@Column(columnDefinition = "int default 30")
	private int evaluate_edit_deadline;// 评价修改期限 默认30天
	@Column(columnDefinition = "int default 180")
	private int evaluate_add_deadline;// 评价追加期限 默认180天

	public Accessory getWebsite_ico() {
		return Website_ico;
	}

	public void setWebsite_ico(Accessory website_ico) {
		Website_ico = website_ico;
	}

	public int getEvaluate_add_deadline() {
		return evaluate_add_deadline;
	}

	public void setEvaluate_add_deadline(int evaluate_add_deadline) {
		this.evaluate_add_deadline = evaluate_add_deadline;
	}

	public int getEvaluate_edit_deadline() {
		return evaluate_edit_deadline;
	}

	public void setEvaluate_edit_deadline(int evaluate_edit_deadline) {
		this.evaluate_edit_deadline = evaluate_edit_deadline;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public int getQr_login() {
		return qr_login;
	}

	public void setQr_login(int qr_login) {
		this.qr_login = qr_login;
	}

	public Accessory getQr_logo() {
		return qr_logo;
	}

	public void setQr_logo(Accessory qr_logo) {
		this.qr_logo = qr_logo;
	}

	public Accessory getWelcome_img() {
		return welcome_img;
	}

	public void setWelcome_img(Accessory welcome_img) {
		this.welcome_img = welcome_img;
	}

	public int getPublish_seller_limit() {
		return publish_seller_limit;
	}

	public void setPublish_seller_limit(int publish_seller_limit) {
		this.publish_seller_limit = publish_seller_limit;
	}

	public int getPublish_post_limit() {
		return publish_post_limit;
	}

	public void setPublish_post_limit(int publish_post_limit) {
		this.publish_post_limit = publish_post_limit;
	}

	public int getCircle_audit() {
		return circle_audit;
	}

	public void setCircle_audit(int circle_audit) {
		this.circle_audit = circle_audit;
	}

	public int getCircle_limit() {
		return circle_limit;
	}

	public void setCircle_limit(int circle_limit) {
		this.circle_limit = circle_limit;
	}

	public int getCircle_count() {
		return circle_count;
	}

	public void setCircle_count(int circle_count) {
		this.circle_count = circle_count;
	}

	public int getCircle_open() {
		return circle_open;
	}

	public void setCircle_open(int circle_open) {
		this.circle_open = circle_open;
	}

	public SysConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SysConfig(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getLucenen_queue() {
		return lucenen_queue;
	}

	public void setLucenen_queue(int lucenen_queue) {
		this.lucenen_queue = lucenen_queue;
	}

	public int getSms_queue() {
		return sms_queue;
	}

	public void setSms_queue(int sms_queue) {
		this.sms_queue = sms_queue;
	}

	public int getEmail_queue() {
		return email_queue;
	}

	public void setEmail_queue(int email_queue) {
		this.email_queue = email_queue;
	}

	public int getWhether_free() {
		return whether_free;
	}

	public void setWhether_free(int whether_free) {
		this.whether_free = whether_free;
	}

	public int getKuaidi_type() {
		return kuaidi_type;
	}

	public void setKuaidi_type(int kuaidi_type) {
		this.kuaidi_type = kuaidi_type;
	}

	public String getKuaidi_id2() {
		return kuaidi_id2;
	}

	public void setKuaidi_id2(String kuaidi_id2) {
		this.kuaidi_id2 = kuaidi_id2;
	}

	public int getEmail_buy_cost() {
		return email_buy_cost;
	}

	public void setEmail_buy_cost(int email_buy_cost) {
		this.email_buy_cost = email_buy_cost;
	}

	public int getSms_buy_cost() {
		return sms_buy_cost;
	}

	public void setSms_buy_cost(int sms_buy_cost) {
		this.sms_buy_cost = sms_buy_cost;
	}

	public int getEmail_buy() {
		return email_buy;
	}

	public void setEmail_buy(int email_buy) {
		this.email_buy = email_buy;
	}

	public int getSms_buy() {
		return sms_buy;
	}

	public void setSms_buy(int sms_buy) {
		this.sms_buy = sms_buy;
	}

	public int getEnoughreduce_max_count() {
		return enoughreduce_max_count;
	}

	public void setEnoughreduce_max_count(int enoughreduce_max_count) {
		this.enoughreduce_max_count = enoughreduce_max_count;
	}

	public int getCombin_status() {
		return combin_status;
	}

	public void setCombin_status(int combin_status) {
		this.combin_status = combin_status;
	}

	public int getEnoughreduce_status() {
		return enoughreduce_status;
	}

	public void setEnoughreduce_status(int enoughreduce_status) {
		this.enoughreduce_status = enoughreduce_status;
	}

	public int getEnoughreduce_meal_gold() {
		return enoughreduce_meal_gold;
	}

	public void setEnoughreduce_meal_gold(int enoughreduce_meal_gold) {
		this.enoughreduce_meal_gold = enoughreduce_meal_gold;
	}

	public int getBuygift_meal_gold() {
		return buygift_meal_gold;
	}

	public void setBuygift_meal_gold(int buygift_meal_gold) {
		this.buygift_meal_gold = buygift_meal_gold;
	}

	public int getBuygift_status() {
		return buygift_status;
	}

	public void setBuygift_status(int buygift_status) {
		this.buygift_status = buygift_status;
	}

	public int getCombin_scheme_count() {
		return combin_scheme_count;
	}

	public void setCombin_scheme_count(int combin_scheme_count) {
		this.combin_scheme_count = combin_scheme_count;
	}

	public Accessory getAdmin_login_logo() {
		return admin_login_logo;
	}

	public void setAdmin_login_logo(Accessory admin_login_logo) {
		this.admin_login_logo = admin_login_logo;
	}

	public Accessory getAdmin_manage_logo() {
		return admin_manage_logo;
	}

	public void setAdmin_manage_logo(Accessory admin_manage_logo) {
		this.admin_manage_logo = admin_manage_logo;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getAndroid_version() {
		return android_version;
	}

	public void setAndroid_version(String android_version) {
		this.android_version = android_version;
	}

	public String getIos_version() {
		return ios_version;
	}

	public void setIos_version(String ios_version) {
		this.ios_version = ios_version;
	}

	public String getPoweredby() {
		return poweredby;
	}

	public void setPoweredby(String poweredby) {
		this.poweredby = poweredby;
	}

	public String getMeta_generator() {
		return meta_generator;
	}

	public void setMeta_generator(String meta_generator) {
		this.meta_generator = meta_generator;
	}

	public String getMeta_author() {
		return meta_author;
	}

	public void setMeta_author(String meta_author) {
		this.meta_author = meta_author;
	}

	public int getApp_download() {
		return app_download;
	}

	public void setApp_download(int app_download) {
		this.app_download = app_download;
	}

	public String getAndroid_download() {
		return android_download;
	}

	public void setAndroid_download(String android_download) {
		this.android_download = android_download;
	}

	public String getIos_download() {
		return ios_download;
	}

	public void setIos_download(String ios_download) {
		this.ios_download = ios_download;
	}

	public int getGrouplife_order_return() {
		return grouplife_order_return;
	}

	public void setGrouplife_order_return(int grouplife_order_return) {
		this.grouplife_order_return = grouplife_order_return;
	}

	public BigDecimal getPayoff_all_amount_reality() {
		return payoff_all_amount_reality;
	}

	public void setPayoff_all_amount_reality(
			BigDecimal payoff_all_amount_reality) {
		this.payoff_all_amount_reality = payoff_all_amount_reality;
	}

	public BigDecimal getPayoff_all_sale() {
		return payoff_all_sale;
	}

	public void setPayoff_all_sale(BigDecimal payoff_all_sale) {
		this.payoff_all_sale = payoff_all_sale;
	}

	public BigDecimal getPayoff_all_commission() {
		return payoff_all_commission;
	}

	public void setPayoff_all_commission(BigDecimal payoff_all_commission) {
		this.payoff_all_commission = payoff_all_commission;
	}

	public BigDecimal getPayoff_all_amount() {
		return payoff_all_amount;
	}

	public void setPayoff_all_amount(BigDecimal payoff_all_amount) {
		this.payoff_all_amount = payoff_all_amount;
	}

	public BigDecimal getOfcard_mobile_profit() {
		return ofcard_mobile_profit;
	}

	public void setOfcard_mobile_profit(BigDecimal ofcard_mobile_profit) {
		this.ofcard_mobile_profit = ofcard_mobile_profit;
	}

	public boolean isOfcard() {
		return ofcard;
	}

	public void setOfcard(boolean ofcard) {
		this.ofcard = ofcard;
	}

	public String getOfcard_userid() {
		return ofcard_userid;
	}

	public void setOfcard_userid(String ofcard_userid) {
		this.ofcard_userid = ofcard_userid;
	}

	public String getOfcard_userpws() {
		return ofcard_userpws;
	}

	public void setOfcard_userpws(String ofcard_userpws) {
		this.ofcard_userpws = ofcard_userpws;
	}

	public Date getPayoff_date() {
		return payoff_date;
	}

	public void setPayoff_date(Date payoff_date) {
		this.payoff_date = payoff_date;
	}

	public int getPayoff_mag_type() {
		return payoff_mag_type;
	}

	public void setPayoff_mag_type(int payoff_mag_type) {
		this.payoff_mag_type = payoff_mag_type;
	}

	public String getPayoff_mag_default() {
		return payoff_mag_default;
	}

	public void setPayoff_mag_default(String payoff_mag_default) {
		this.payoff_mag_default = payoff_mag_default;
	}

	public String getPayoff_mag_auto() {
		return payoff_mag_auto;
	}

	public void setPayoff_mag_auto(String payoff_mag_auto) {
		this.payoff_mag_auto = payoff_mag_auto;
	}

	public int getGroup_meal_gold() {
		return group_meal_gold;
	}

	public void setGroup_meal_gold(int group_meal_gold) {
		this.group_meal_gold = group_meal_gold;
	}

	public int getPayoff_count() {
		return payoff_count;
	}

	public void setPayoff_count(int payoff_count) {
		this.payoff_count = payoff_count;
	}

	public Accessory getWeixin_qr_img() {
		return weixin_qr_img;
	}

	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}

	public String getWeixin_account() {
		return weixin_account;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public String getWeixin_token() {
		return weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_appId() {
		return weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public String getWeixin_welecome_content() {
		return weixin_welecome_content;
	}

	public void setWeixin_welecome_content(String weixin_welecome_content) {
		this.weixin_welecome_content = weixin_welecome_content;
	}

	public Accessory getStore_weixin_logo() {
		return store_weixin_logo;
	}

	public void setStore_weixin_logo(Accessory store_weixin_logo) {
		this.store_weixin_logo = store_weixin_logo;
	}

	public int getWeixin_store() {
		return weixin_store;
	}

	public void setWeixin_store(int weixin_store) {
		this.weixin_store = weixin_store;
	}

	public int getAuto_order_return() {
		return auto_order_return;
	}

	public void setAuto_order_return(int auto_order_return) {
		this.auto_order_return = auto_order_return;
	}

	public int getAuto_order_evaluate() {
		return auto_order_evaluate;
	}

	public void setAuto_order_evaluate(int auto_order_evaluate) {
		this.auto_order_evaluate = auto_order_evaluate;
	}

	public int getZtc_goods_view() {
		return ztc_goods_view;
	}

	public void setZtc_goods_view(int ztc_goods_view) {
		this.ztc_goods_view = ztc_goods_view;
	}

	public String getWebsiteCss() {
		return websiteCss;
	}

	public void setWebsiteCss(String websiteCss) {
		this.websiteCss = websiteCss;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public List<Accessory> getLogin_imgs() {
		return login_imgs;
	}

	public void setLogin_imgs(List<Accessory> login_imgs) {
		this.login_imgs = login_imgs;
	}

	public Date getLucene_update() {
		return lucene_update;
	}

	public void setLucene_update(Date lucene_update) {
		this.lucene_update = lucene_update;
	}

	public boolean isSina_login() {
		return sina_login;
	}

	public void setSina_login(boolean sina_login) {
		this.sina_login = sina_login;
	}

	public String getSina_login_id() {
		return sina_login_id;
	}

	public void setSina_login_id(String sina_login_id) {
		this.sina_login_id = sina_login_id;
	}

	public String getSina_login_key() {
		return sina_login_key;
	}

	public void setSina_login_key(String sina_login_key) {
		this.sina_login_key = sina_login_key;
	}

	public String getSina_domain_code() {
		return sina_domain_code;
	}

	public void setSina_domain_code(String sina_domain_code) {
		this.sina_domain_code = sina_domain_code;
	}

	public boolean isQq_login() {
		return qq_login;
	}

	public void setQq_login(boolean qq_login) {
		this.qq_login = qq_login;
	}

	public String getQq_login_id() {
		return qq_login_id;
	}

	public void setQq_login_id(String qq_login_id) {
		this.qq_login_id = qq_login_id;
	}

	public String getQq_login_key() {
		return qq_login_key;
	}

	public void setQq_login_key(String qq_login_key) {
		this.qq_login_key = qq_login_key;
	}

	public int getDomain_allow_count() {
		return domain_allow_count;
	}

	public void setDomain_allow_count(int domain_allow_count) {
		this.domain_allow_count = domain_allow_count;
	}

	public String getSys_domain() {
		return sys_domain;
	}

	public void setSys_domain(String sys_domain) {
		this.sys_domain = sys_domain;
	}

	public boolean isZtc_status() {
		return ztc_status;
	}

	public void setZtc_status(boolean ztc_status) {
		this.ztc_status = ztc_status;
	}

	public int getZtc_price() {
		return ztc_price;
	}

	public void setZtc_price(int ztc_price) {
		this.ztc_price = ztc_price;
	}

	public String getTemplates() {
		return templates;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}

	public boolean isStore_allow() {
		return store_allow;
	}

	public void setStore_allow(boolean store_allow) {
		this.store_allow = store_allow;
	}

	public Accessory getStoreImage() {
		return storeImage;
	}

	public void setStoreImage(Accessory storeImage) {
		this.storeImage = storeImage;
	}

	public Accessory getGoodsImage() {
		return goodsImage;
	}

	public void setGoodsImage(Accessory goodsImage) {
		this.goodsImage = goodsImage;
	}

	public Accessory getMemberIcon() {
		return memberIcon;
	}

	public void setMemberIcon(Accessory memberIcon) {
		this.memberIcon = memberIcon;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public int getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public String getEmailPws() {
		return emailPws;
	}

	public void setEmailPws(String emailPws) {
		this.emailPws = emailPws;
	}

	public String getSysLanguage() {
		return sysLanguage;
	}

	public void setSysLanguage(String sysLanguage) {
		this.sysLanguage = sysLanguage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSmsURL() {
		return smsURL;
	}

	public void setSmsURL(String smsURL) {
		this.smsURL = smsURL;
	}

	public String getSmsUserName() {
		return smsUserName;
	}

	public void setSmsUserName(String smsUserName) {
		this.smsUserName = smsUserName;
	}

	public String getSmsPassword() {
		return smsPassword;
	}

	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIntegralRate() {
		return integralRate;
	}

	public void setIntegralRate(int integralRate) {
		this.integralRate = integralRate;
	}

	public String getCopyRight() {
		return copyRight;
	}

	public void setCopyRight(String copyRight) {
		this.copyRight = copyRight;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getHotSearch() {
		return hotSearch;
	}

	public void setHotSearch(String hotSearch) {
		this.hotSearch = hotSearch;
	}

	public Accessory getWebsiteLogo() {
		return websiteLogo;
	}

	public void setWebsiteLogo(Accessory websiteLogo) {
		this.websiteLogo = websiteLogo;
	}

	public String getCodeStat() {
		return codeStat;
	}

	public void setCodeStat(String codeStat) {
		this.codeStat = codeStat;
	}

	public boolean isWebsiteState() {
		return websiteState;
	}

	public void setWebsiteState(boolean websiteState) {
		this.websiteState = websiteState;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public boolean isEmailEnable() {
		return emailEnable;
	}

	public void setEmailEnable(boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public String getEmailTest() {
		return emailTest;
	}

	public void setEmailTest(String emailTest) {
		this.emailTest = emailTest;
	}

	public boolean isSecurityCodeRegister() {
		return securityCodeRegister;
	}

	public void setSecurityCodeRegister(boolean securityCodeRegister) {
		this.securityCodeRegister = securityCodeRegister;
	}

	public boolean isSecurityCodeLogin() {
		return securityCodeLogin;
	}

	public void setSecurityCodeLogin(boolean securityCodeLogin) {
		this.securityCodeLogin = securityCodeLogin;
	}

	public boolean isSecurityCodeConsult() {
		return securityCodeConsult;
	}

	public void setSecurityCodeConsult(boolean securityCodeConsult) {
		this.securityCodeConsult = securityCodeConsult;
	}

	public boolean isVisitorConsult() {
		return visitorConsult;
	}

	public void setVisitorConsult(boolean visitorConsult) {
		this.visitorConsult = visitorConsult;
	}

	public String getImageSuffix() {
		return imageSuffix;
	}

	public void setImageSuffix(String imageSuffix) {
		this.imageSuffix = imageSuffix;
	}

	public int getImageFilesize() {
		return imageFilesize;
	}

	public void setImageFilesize(int imageFilesize) {
		this.imageFilesize = imageFilesize;
	}

	public int getSmallWidth() {
		return smallWidth;
	}

	public void setSmallWidth(int smallWidth) {
		this.smallWidth = smallWidth;
	}

	public int getSmallHeight() {
		return smallHeight;
	}

	public void setSmallHeight(int smallHeight) {
		this.smallHeight = smallHeight;
	}

	public int getMiddleWidth() {
		return middleWidth;
	}

	public void setMiddleWidth(int middleWidth) {
		this.middleWidth = middleWidth;
	}

	public int getMiddleHeight() {
		return middleHeight;
	}

	public void setMiddleHeight(int middleHeight) {
		this.middleHeight = middleHeight;
	}

	public int getBigWidth() {
		return bigWidth;
	}

	public void setBigWidth(int bigWidth) {
		this.bigWidth = bigWidth;
	}

	public int getBigHeight() {
		return bigHeight;
	}

	public void setBigHeight(int bigHeight) {
		this.bigHeight = bigHeight;
	}

	public String getImageSaveType() {
		return imageSaveType;
	}

	public void setImageSaveType(String imageSaveType) {
		this.imageSaveType = imageSaveType;
	}

	public String getSecurityCodeType() {
		return securityCodeType;
	}

	public void setSecurityCodeType(String securityCodeType) {
		this.securityCodeType = securityCodeType;
	}

	public boolean isIntegral() {
		return integral;
	}

	public void setIntegral(boolean integral) {
		this.integral = integral;
	}

	public boolean isIntegralStore() {
		return integralStore;
	}

	public void setIntegralStore(boolean integralStore) {
		this.integralStore = integralStore;
	}

	public boolean isVoucher() {
		return voucher;
	}

	public void setVoucher(boolean voucher) {
		this.voucher = voucher;
	}

	public boolean isDeposit() {
		return deposit;
	}

	public void setDeposit(boolean deposit) {
		this.deposit = deposit;
	}

	public boolean isGroupBuy() {
		return groupBuy;
	}

	public void setGroupBuy(boolean groupBuy) {
		this.groupBuy = groupBuy;
	}

	public boolean isGold() {
		return gold;
	}

	public void setGold(boolean gold) {
		this.gold = gold;
	}

	public int getGoldMarketValue() {
		return goldMarketValue;
	}

	public void setGoldMarketValue(int goldMarketValue) {
		this.goldMarketValue = goldMarketValue;
	}

	public int getMemberRegister() {
		return memberRegister;
	}

	public void setMemberRegister(int memberRegister) {
		this.memberRegister = memberRegister;
	}

	public int getMemberDayLogin() {
		return memberDayLogin;
	}

	public void setMemberDayLogin(int memberDayLogin) {
		this.memberDayLogin = memberDayLogin;
	}

	public int getIndentComment() {
		return indentComment;
	}

	public void setIndentComment(int indentComment) {
		this.indentComment = indentComment;
	}

	public int getConsumptionRatio() {
		return consumptionRatio;
	}

	public void setConsumptionRatio(int consumptionRatio) {
		this.consumptionRatio = consumptionRatio;
	}

	public int getEveryIndentLimit() {
		return everyIndentLimit;
	}

	public void setEveryIndentLimit(int everyIndentLimit) {
		this.everyIndentLimit = everyIndentLimit;
	}

	public boolean isSmsEnbale() {
		return smsEnbale;
	}

	public void setSmsEnbale(boolean smsEnbale) {
		this.smsEnbale = smsEnbale;
	}

	public String getSmsTest() {
		return smsTest;
	}

	public void setSmsTest(String smsTest) {
		this.smsTest = smsTest;
	}

	public String getUploadFilePath() {
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStore_payment() {
		return store_payment;
	}

	public void setStore_payment(String store_payment) {
		this.store_payment = store_payment;
	}

	public String getShare_code() {
		return share_code;
	}

	public void setShare_code(String share_code) {
		this.share_code = share_code;
	}

	public String getUser_level() {
		return user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public int getComplaint_time() {
		return complaint_time;
	}

	public void setComplaint_time(int complaint_time) {
		this.complaint_time = complaint_time;
	}

	public boolean isSecond_domain_open() {
		return second_domain_open;
	}

	public void setSecond_domain_open(boolean second_domain_open) {
		this.second_domain_open = second_domain_open;
	}

	public String getQq_domain_code() {
		return qq_domain_code;
	}

	public void setQq_domain_code(String qq_domain_code) {
		this.qq_domain_code = qq_domain_code;
	}

	public String getImageWebServer() {
		return imageWebServer;
	}

	public void setImageWebServer(String imageWebServer) {
		this.imageWebServer = imageWebServer;
	}

	public String getService_telphone_list() {
		return service_telphone_list;
	}

	public void setService_telphone_list(String service_telphone_list) {
		this.service_telphone_list = service_telphone_list;
	}

	public String getService_qq_list() {
		return service_qq_list;
	}

	public void setService_qq_list(String service_qq_list) {
		this.service_qq_list = service_qq_list;
	}

	public int getAuto_order_confirm() {
		return auto_order_confirm;
	}

	public void setAuto_order_confirm(int auto_order_confirm) {
		this.auto_order_confirm = auto_order_confirm;
	}

	public int getAuto_order_notice() {
		return auto_order_notice;
	}

	public void setAuto_order_notice(int auto_order_notice) {
		this.auto_order_notice = auto_order_notice;
	}

	public String getKuaidi_id() {
		return kuaidi_id;
	}

	public void setKuaidi_id(String kuaidi_id) {
		this.kuaidi_id = kuaidi_id;
	}

	public int getCombin_amount() {
		return combin_amount;
	}

	public void setCombin_amount(int combin_amount) {
		this.combin_amount = combin_amount;
	}

	public int getCombin_count() {
		return combin_count;
	}

	public void setCombin_count(int combin_count) {
		this.combin_count = combin_count;
	}

	public String getAndroid_seller_download() {
		return android_seller_download;
	}

	public void setAndroid_seller_download(String android_seller_download) {
		this.android_seller_download = android_seller_download;
	}

	public String getAndroid_seller_version() {
		return android_seller_version;
	}

	public void setAndroid_seller_version(String android_seller_version) {
		this.android_seller_version = android_seller_version;
	}

	public String getIos_seller_download() {
		return ios_seller_download;
	}

	public void setIos_seller_download(String ios_seller_download) {
		this.ios_seller_download = ios_seller_download;
	}

	public String getIos_seller_version() {
		return ios_seller_version;
	}

	public void setIos_seller_version(String ios_seller_version) {
		this.ios_seller_version = ios_seller_version;
	}

	public int getApp_seller_download() {
		return app_seller_download;
	}

	public void setApp_seller_download(int app_seller_download) {
		this.app_seller_download = app_seller_download;
	}

}