package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: Consult.java
 * </p>
 * 
 * <p>
 * Description:产品咨询管理类,用来管理用户对卖家商品的咨询及卖家对咨询的回复
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "consult")
public class Consult extends IdEntity {
	private Long goods_id;
	@Column(columnDefinition = "LongText")
	private String goods_info;// /[{"goods_id":"1"
								// ,"goods_name":"鳄鱼褐色纯皮裤腰带",store_domainPath这个是店铺二级域名路径
								// goods_domainPath商品二级域名路径
								// "goods_main_photo":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_price":"54.30"}]
	@Column(columnDefinition = "int default 0")
	private int whether_self;// 是否为自营商品咨询 0为第三方 1为自营
	private Long store_id;// 咨询商品对应的store id
	private String store_name;// 店铺名称
	private String consult_type;// 咨询类型，分为:产品咨询、库存及配送,支付及发票、售后咨询、促销活动
	@Column(columnDefinition = "LongText")
	private String consult_content;// 咨询内容
	private boolean reply;// 是否回复
	@Column(columnDefinition = "LongText")
	private String consult_reply;// 回复内容
	private Long consult_user_id;// 咨询用户id
	private String consult_user_name;// 咨询用户名
	private Long reply_user_id;// 回复用户id
	private String reply_user_name;// 回复用户名
	private Date reply_time;// 回复时间
	private String consult_email;// 回复人email
	@Column(columnDefinition = "int default 0")
	private int unsatisfy;// 不满意数量
	@Column(columnDefinition = "int default 0")
	private int satisfy;// 满意数

	public Consult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Consult(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

	public int getWhether_self() {
		return whether_self;
	}

	public void setWhether_self(int whether_self) {
		this.whether_self = whether_self;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public int getUnsatisfy() {
		return unsatisfy;
	}

	public void setUnsatisfy(int unsatisfy) {
		this.unsatisfy = unsatisfy;
	}

	public int getSatisfy() {
		return satisfy;
	}

	public void setSatisfy(int satisfy) {
		this.satisfy = satisfy;
	}

	public Long getConsult_user_id() {
		return consult_user_id;
	}

	public void setConsult_user_id(Long consult_user_id) {
		this.consult_user_id = consult_user_id;
	}

	public String getConsult_user_name() {
		return consult_user_name;
	}

	public void setConsult_user_name(String consult_user_name) {
		this.consult_user_name = consult_user_name;
	}

	public Long getReply_user_id() {
		return reply_user_id;
	}

	public void setReply_user_id(Long reply_user_id) {
		this.reply_user_id = reply_user_id;
	}

	public String getReply_user_name() {
		return reply_user_name;
	}

	public void setReply_user_name(String reply_user_name) {
		this.reply_user_name = reply_user_name;
	}

	public String getConsult_type() {
		return consult_type;
	}

	public void setConsult_type(String consult_type) {
		this.consult_type = consult_type;
	}

	public boolean isReply() {
		return reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public String getConsult_email() {
		return consult_email;
	}

	public void setConsult_email(String consult_email) {
		this.consult_email = consult_email;
	}

	public String getConsult_content() {
		return consult_content;
	}

	public void setConsult_content(String consult_content) {
		consult_content = HtmlFilterTools.delAllTag(consult_content);
		this.consult_content = consult_content;
	}

	public String getConsult_reply() {
		return consult_reply;
	}

	public void setConsult_reply(String consult_reply) {
		consult_reply = HtmlFilterTools.delAllTag(consult_reply);
		this.consult_reply = consult_reply;
	}

	public Date getReply_time() {
		return reply_time;
	}

	public void setReply_time(Date reply_time) {
		this.reply_time = reply_time;
	}
}
