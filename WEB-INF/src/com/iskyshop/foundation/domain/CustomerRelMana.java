package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: CustomerRelMana.java
 * </p>
 * 
 * <p>
 * Description:CRM管理类。定时器每天0时记录每个卖家下过订单的、收藏过的商品的、咨询过商品的用户
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
 * @author jinxinzhe
 * 
 * @date 2014-11-5
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "customerrelmana")
public class CustomerRelMana extends IdEntity {

	private Long store_id;// 所属卖家店铺id
	private String userName;// 买家的用户名
	private Long user_id;// 买家的用户id;
	@Column(columnDefinition = "int default 0")
	private int whether_self;// 0为第三方经销商 1为自营商品
	@Column(columnDefinition = "int default 0")
	private int cus_type;// 0为下过订单 1为咨询过的 2为收藏过
	private Long goods_id;// 商品的id;
	private String goods_name;// 商品的名称;
	@Column(columnDefinition = "int default 0")
	private int whether_send_email;// 是否发送过邮件 0为没发过 1为发过
	@Column(columnDefinition = "int default 0")
	private int whether_send_message;// 是否发送过短信 0为没发过 1为发过

	public CustomerRelMana(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public CustomerRelMana() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getWhether_send_email() {
		return whether_send_email;
	}

	public void setWhether_send_email(int whether_send_email) {
		this.whether_send_email = whether_send_email;
	}

	public int getWhether_send_message() {
		return whether_send_message;
	}

	public void setWhether_send_message(int whether_send_message) {
		this.whether_send_message = whether_send_message;
	}

	public int getWhether_self() {
		return whether_self;
	}

	public void setWhether_self(int whether_self) {
		this.whether_self = whether_self;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getCus_type() {
		return cus_type;
	}

	public void setCus_type(int cus_type) {
		this.cus_type = cus_type;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

}
