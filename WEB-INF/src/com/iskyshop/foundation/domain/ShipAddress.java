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
 * Title: ShipAddress.java
 * </p>
 * 
 * <p>
 * Description:发货地址管理类，用来管理发货地址，无论自营商品、商家商品在设置发货时，需要选择发货地址及快递信息才可以设置发货，
 * 该地址用在快递跟踪订阅、快递运单打印
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
 * @date 2014-11-11
 * 
 * @version iskyshop_b2b2c 2.0
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ship_address")
public class ShipAddress extends IdEntity {
	private String sa_name;// 发货地址名称
	private Long sa_area_id;// 发货地址id，对应系统区域管理类Area
	@Column(columnDefinition = "int default 0")
	private int sa_type;// 发货地址类型，0为商家发货地址，1为管理员发货地址,默认为0
	private Long sa_user_id;// 发货地址的用户
	private String sa_user_name;// 添加发货地址用户名
	private String sa_addr;// 发货地址的详细信息，指的是完整地址
	@Column(columnDefinition = "int default 0")
	private int sa_default;// 是否默认发货地址,1为默认
	@Column(columnDefinition = "int default 0")
	private int sa_sequence;// 发货地址序号，按照升序排列
	private String sa_user;// 发货人姓名
	private String sa_telephone;// 发货人联系电话
	private String sa_company;// 发货公司
	private String sa_zip;// 发货人区号或者邮编

	public ShipAddress() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShipAddress(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getSa_zip() {
		return sa_zip;
	}

	public void setSa_zip(String sa_zip) {
		this.sa_zip = sa_zip;
	}

	public String getSa_user() {
		return sa_user;
	}

	public void setSa_user(String sa_user) {
		this.sa_user = sa_user;
	}

	public String getSa_telephone() {
		return sa_telephone;
	}

	public void setSa_telephone(String sa_telephone) {
		this.sa_telephone = sa_telephone;
	}

	public String getSa_company() {
		return sa_company;
	}

	public void setSa_company(String sa_company) {
		this.sa_company = sa_company;
	}

	public String getSa_name() {
		return sa_name;
	}

	public void setSa_name(String sa_name) {
		this.sa_name = sa_name;
	}

	public int getSa_default() {
		return sa_default;
	}

	public void setSa_default(int sa_default) {
		this.sa_default = sa_default;
	}

	public int getSa_sequence() {
		return sa_sequence;
	}

	public void setSa_sequence(int sa_sequence) {
		this.sa_sequence = sa_sequence;
	}

	public Long getSa_area_id() {
		return sa_area_id;
	}

	public void setSa_area_id(Long sa_area_id) {
		this.sa_area_id = sa_area_id;
	}

	public int getSa_type() {
		return sa_type;
	}

	public void setSa_type(int sa_type) {
		this.sa_type = sa_type;
	}

	public Long getSa_user_id() {
		return sa_user_id;
	}

	public void setSa_user_id(Long sa_user_id) {
		this.sa_user_id = sa_user_id;
	}

	public String getSa_user_name() {
		return sa_user_name;
	}

	public void setSa_user_name(String sa_user_name) {
		this.sa_user_name = sa_user_name;
	}

	public String getSa_addr() {
		return sa_addr;
	}

	public void setSa_addr(String sa_addr) {
		this.sa_addr = sa_addr;
	}

}
