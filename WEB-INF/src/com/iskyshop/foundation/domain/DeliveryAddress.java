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

/**
 * 
 * <p>
 * Title: DeliveryAddress.java
 * </p>
 * 
 * <p>
 * Description:自提点管理类，系统开放自提点注册，所有用户只要拥有营业执照、正规运营场所经过审核后均可以加入自提点，
 * 运营商将从快递费中抽取部分分给自提点，按照自提数量进行利润分配
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
 * @author erikzhang,jy
 * 
 * @date 2014-9-24
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "delivery_address")
public class DeliveryAddress extends IdEntity {
	private int da_type;// 自提点类型，0-商城自提点，1-第三方申请自提点
	private Long da_user_id;// 第三方自提点所对应用户的id
	private String da_user_name;// 第三方自提点所对应的用户名
	private String da_name;// 自提点名称
	@Column(columnDefinition = "LongText")
	private String da_content;// 自提点说明
	private String da_tel;// 自提点联系电话
	private String da_contact_user;// 自提点联系人姓名
	@ManyToOne(fetch = FetchType.LAZY)
	private Area da_area;// 自提点地址
	private String da_address;// 自提点详细地址
	private int da_status;// 0-待审核自提点，4-审核未通过,5-自提点暂停服务，10-自提点正常服务
	private String da_service_day;// 自提点服务日期
	private int da_service_type;// 服务时间，0-全天，1-非全天
	private String da_begin_time;// 自提点开始服务时间
	private String da_end_time;// 自提点结束服务时间

	public DeliveryAddress(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}
	
	public DeliveryAddress() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDa_contact_user() {
		return da_contact_user;
	}

	public void setDa_contact_user(String da_contact_user) {
		this.da_contact_user = da_contact_user;
	}

	public String getDa_user_name() {
		return da_user_name;
	}

	public void setDa_user_name(String da_user_name) {
		this.da_user_name = da_user_name;
	}

	public String getDa_service_day() {
		return da_service_day;
	}

	public void setDa_service_day(String da_service_day) {
		this.da_service_day = da_service_day;
	}

	public int getDa_service_type() {
		return da_service_type;
	}

	public void setDa_service_type(int da_service_type) {
		this.da_service_type = da_service_type;
	}

	public String getDa_begin_time() {
		return da_begin_time;
	}

	public void setDa_begin_time(String da_begin_time) {
		this.da_begin_time = da_begin_time;
	}

	public String getDa_end_time() {
		return da_end_time;
	}

	public void setDa_end_time(String da_end_time) {
		this.da_end_time = da_end_time;
	}

	public int getDa_type() {
		return da_type;
	}

	public void setDa_type(int da_type) {
		this.da_type = da_type;
	}

	public Long getDa_user_id() {
		return da_user_id;
	}

	public void setDa_user_id(Long da_user_id) {
		this.da_user_id = da_user_id;
	}

	public String getDa_name() {
		return da_name;
	}

	public void setDa_name(String da_name) {
		this.da_name = da_name;
	}

	public String getDa_content() {
		return da_content;
	}

	public void setDa_content(String da_content) {
		this.da_content = da_content;
	}

	public String getDa_tel() {
		return da_tel;
	}

	public void setDa_tel(String da_tel) {
		this.da_tel = da_tel;
	}

	public Area getDa_area() {
		return da_area;
	}

	public void setDa_area(Area da_area) {
		this.da_area = da_area;
	}

	public String getDa_address() {
		return da_address;
	}

	public void setDa_address(String da_address) {
		this.da_address = da_address;
	}

	public int getDa_status() {
		return da_status;
	}

	public void setDa_status(int da_status) {
		this.da_status = da_status;
	}

}
