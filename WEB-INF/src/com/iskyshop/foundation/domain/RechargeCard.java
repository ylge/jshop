package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
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
 * Title: RechargeCard.java
 * </p>
 * 
 * <p>
 * Description:
 * 系统充值卡管理类，用来管理系统充值卡，系统充值卡可以采用线下发行和线上自动生成等方式生成，每张充值卡对应一定的充值金额且只能使用一次
 * ，充值成功后用户的预存款增加
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
 * @date 2014-10-15
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "recharge_card")
public class RechargeCard extends IdEntity {
	private String rc_sn;// 充值卡号，可以自动生成，也可以手动输入
	@Column(precision = 12, scale = 2)
	private BigDecimal rc_amount;// 充值款金额，可以到“分”
	private String rc_mark;// 充值卡标识
	private String rc_pub_user_name;// 充值卡发布人姓名
	private Long rc_pub_user_id;// 充值卡发布人id
	private int rc_status;// 充值卡状态，0为未使用，1为已经领取，2为已经使用
	@Column(columnDefinition = "LongText")
	private String rc_info;// 充值卡领取信息
	private String rc_user_name;// 充值卡使用用户的用户名
	private Long rc_user_id;// 充值卡使用用户的id
	private Date rc_time;// 充值卡使用时间
	private String rc_ip;// 充值卡使用电脑的IP地址

	public RechargeCard() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RechargeCard(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getRc_user_name() {
		return rc_user_name;
	}

	public void setRc_user_name(String rc_user_name) {
		this.rc_user_name = rc_user_name;
	}

	public Long getRc_user_id() {
		return rc_user_id;
	}

	public void setRc_user_id(Long rc_user_id) {
		this.rc_user_id = rc_user_id;
	}

	public Date getRc_time() {
		return rc_time;
	}

	public void setRc_time(Date rc_time) {
		this.rc_time = rc_time;
	}

	public String getRc_ip() {
		return rc_ip;
	}

	public void setRc_ip(String rc_ip) {
		this.rc_ip = rc_ip;
	}

	public String getRc_mark() {
		return rc_mark;
	}

	public void setRc_mark(String rc_mark) {
		this.rc_mark = rc_mark;
	}

	public String getRc_sn() {
		return rc_sn;
	}

	public void setRc_sn(String rc_sn) {
		this.rc_sn = rc_sn;
	}

	public BigDecimal getRc_amount() {
		return rc_amount;
	}

	public void setRc_amount(BigDecimal rc_amount) {
		this.rc_amount = rc_amount;
	}

	public String getRc_pub_user_name() {
		return rc_pub_user_name;
	}

	public void setRc_pub_user_name(String rc_pub_user_name) {
		this.rc_pub_user_name = rc_pub_user_name;
	}

	public Long getRc_pub_user_id() {
		return rc_pub_user_id;
	}

	public void setRc_pub_user_id(Long rc_pub_user_id) {
		this.rc_pub_user_id = rc_pub_user_id;
	}

	public int getRc_status() {
		return rc_status;
	}

	public void setRc_status(int rc_status) {
		this.rc_status = rc_status;
	}

	public String getRc_info() {
		return rc_info;
	}

	public void setRc_info(String rc_info) {
		this.rc_info = rc_info;
	}

}
