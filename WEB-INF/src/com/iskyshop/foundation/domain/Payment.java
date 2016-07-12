package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Payment.java
 * </p>
 * 
 * <p>
 * Description: 系统支付方式管理类，平台支付方式可以接受所有用户对平台的付款，
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
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "payment")
public class Payment extends IdEntity {
	// 以下是通用信息
	private boolean install;// 是否启用
	private String name;// 支付方式名称
	private String mark;// 支付方式标识代码,alipay为支付宝，
						// alipay_wap为支付宝手机网页支付，
						// alipay_app为支付宝APP版，99bill为快钱，
						// tenpay为财付通,balance为预存款，payafter为货到付款(V2.0版本增加商品是否支持货到付款属性，取消该付款方式)，
						// outline为线下支付(已经取消该付款方式，只接受在线付款、货到付款、预存款付款)
						// wx_app为手机端微信支付
						// wx_pay为手机端微信支付
	// 以下为支付宝信息
	private String safeKey; // 交易安全校验码
	private String partner;// 合作者身份ID
	private String seller_email; // 签约支付宝账号或卖家收款支付宝帐户
	private int interfaceType;// 选择接口类型，支付宝使用，0为即时到帐，1为担保支付，2为标准双接口
	@Column(precision = 12, scale = 2)
	private BigDecimal alipay_rate;// 支付宝手续费率
	@Column(columnDefinition = "LongText")
	private String app_private_key;// App支付宝商户密钥，
	@Column(columnDefinition = "LongText")
	private String app_public_key;// App支付宝商户公钥，
	// 以下为快钱信息
	private String merchantAcctId;// 快钱收款账户
	private String rmbKey;// 人民币网关密钥
	private String pid;// 快钱的合作伙伴的账户号
	// 以下为财付通信息,20120313之后审核成功的中介担保用户及所有即时到帐用户
	private String spname;
	private String tenpay_partner;
	private String tenpay_key;
	private int trade_mode;
	// 以下是网银在线信息
	private String chinabank_account;
	private String chinabank_key;
	// 以下是其他支付信息说明文字,如银行账户信息等
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;
	// 以下为paypal支付信息
	private String paypal_userId;// paypal商户Id
	private String currency_code;// paypal支付货币种类
	@Column(precision = 12, scale = 2)
	private BigDecimal poundage;// paypal支付手续费
	@OneToMany(mappedBy = "payment")
	private List<OrderForm> ofs = new ArrayList<OrderForm>();// 对应的订单，反向映射，便于获取订单信息
	private String wx_appid;// 微信支付appid
	private String wx_appSecret;// 微信支付AppSecret，申请开发者成功后微信生成的AppSecret
	private String wx_paySignKey;// 微信支付的paySignKey

	public Payment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Payment(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getWx_appid() {
		return wx_appid;
	}

	public void setWx_appid(String wx_appid) {
		this.wx_appid = wx_appid;
	}

	public String getWx_appSecret() {
		return wx_appSecret;
	}

	public void setWx_appSecret(String wx_appSecret) {
		this.wx_appSecret = wx_appSecret;
	}

	public String getWx_paySignKey() {
		return wx_paySignKey;
	}

	public void setWx_paySignKey(String wx_paySignKey) {
		this.wx_paySignKey = wx_paySignKey;
	}

	public String getApp_private_key() {
		return app_private_key;
	}

	public void setApp_private_key(String app_private_key) {
		this.app_private_key = app_private_key;
	}

	public String getApp_public_key() {
		return app_public_key;
	}

	public void setApp_public_key(String app_public_key) {
		this.app_public_key = app_public_key;
	}

	public String getPaypal_userId() {
		return paypal_userId;
	}

	public void setPaypal_userId(String paypal_userId) {
		this.paypal_userId = paypal_userId;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public BigDecimal getPoundage() {
		return poundage;
	}

	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
	}

	public BigDecimal getAlipay_rate() {
		return alipay_rate;
	}

	public void setAlipay_rate(BigDecimal alipay_rate) {
		this.alipay_rate = alipay_rate;
	}

	public List<OrderForm> getOfs() {
		return ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public boolean isInstall() {
		return install;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getSafeKey() {
		return safeKey;
	}

	public void setSafeKey(String safeKey) {
		this.safeKey = safeKey;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public int getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(int interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getMerchantAcctId() {
		return merchantAcctId;
	}

	public void setMerchantAcctId(String merchantAcctId) {
		this.merchantAcctId = merchantAcctId;
	}

	public String getRmbKey() {
		return rmbKey;
	}

	public void setRmbKey(String rmbKey) {
		this.rmbKey = rmbKey;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getChinabank_account() {
		return chinabank_account;
	}

	public void setChinabank_account(String chinabank_account) {
		this.chinabank_account = chinabank_account;
	}

	public String getChinabank_key() {
		return chinabank_key;
	}

	public void setChinabank_key(String chinabank_key) {
		this.chinabank_key = chinabank_key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpname() {
		return spname;
	}

	public void setSpname(String spname) {
		this.spname = spname;
	}

	public String getTenpay_partner() {
		return tenpay_partner;
	}

	public void setTenpay_partner(String tenpay_partner) {
		this.tenpay_partner = tenpay_partner;
	}

	public String getTenpay_key() {
		return tenpay_key;
	}

	public void setTenpay_key(String tenpay_key) {
		this.tenpay_key = tenpay_key;
	}

	public int getTrade_mode() {
		return trade_mode;
	}

	public void setTrade_mode(int trade_mode) {
		this.trade_mode = trade_mode;
	}

}
