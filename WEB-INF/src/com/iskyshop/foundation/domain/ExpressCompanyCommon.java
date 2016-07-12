package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: ExpressCompanyCommon.java
 * </p>
 * 
 * <p>
 * Description:常用物流公司，根据系统统一设置的物流公司信息，自营、商家均可以设置自己的常用物流公司信息，发货时调用常用物流公司信息
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
 * @date 2014-11-18
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ec_common")
public class ExpressCompanyCommon extends IdEntity {
	private String ecc_name;// 常用物流公司名称
	private String ecc_code;// 常用物流公司代码
	@Column(columnDefinition = "int default 0")
	private int ecc_default;// 是否为默认物流公司，1为默认，只允许有一个默认物流公司
	@Column(columnDefinition = "int default 0")
	private int ecc_type;// 常用物流公司类型，0为商家的常用物流公司，1为自营的常用物流公司
	private Long ecc_store_id;// 常用物流公司所属的商家
	@Column(columnDefinition = "LongText")
	private String ecc_template;// 常用物流公司的模板路径
	@Column(columnDefinition = "int default 0")
	private int ecc_template_width;// 物流模板宽度,单位为毫米
	@Column(columnDefinition = "int default 0")
	private int ecc_template_heigh;// 物流模板高度,单位为毫米
	private Long ecc_ec_id;// 对应的物流公司id
	@Column(columnDefinition = "LongText")
	private String ecc_template_offset;// 快递模板各个参数的偏移量，用来定位模板上的相关信息
	private String ecc_ec_type;// 对应的快递类型 对应ExpressCompany中的company_type
	@Column(columnDefinition = "int default 0")
    private int ecc_from_type;//模板来源类型,0为直接使用系统模板，1为自建模板
	
	public ExpressCompanyCommon() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ExpressCompanyCommon(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getEcc_from_type() {
		return ecc_from_type;
	}

	public void setEcc_from_type(int ecc_from_type) {
		this.ecc_from_type = ecc_from_type;
	}

	public String getEcc_ec_type() {
		return ecc_ec_type;
	}

	public void setEcc_ec_type(String ecc_ec_type) {
		this.ecc_ec_type = ecc_ec_type;
	}

	public int getEcc_default() {
		return ecc_default;
	}

	public void setEcc_default(int ecc_default) {
		this.ecc_default = ecc_default;
	}

	public int getEcc_template_width() {
		return ecc_template_width;
	}

	public void setEcc_template_width(int ecc_template_width) {
		this.ecc_template_width = ecc_template_width;
	}

	public int getEcc_template_heigh() {
		return ecc_template_heigh;
	}

	public void setEcc_template_heigh(int ecc_template_heigh) {
		this.ecc_template_heigh = ecc_template_heigh;
	}

	public String getEcc_name() {
		return ecc_name;
	}

	public void setEcc_name(String ecc_name) {
		this.ecc_name = ecc_name;
	}

	public String getEcc_code() {
		return ecc_code;
	}

	public void setEcc_code(String ecc_code) {
		this.ecc_code = ecc_code;
	}

	public int getEcc_type() {
		return ecc_type;
	}

	public void setEcc_type(int ecc_type) {
		this.ecc_type = ecc_type;
	}

	public Long getEcc_store_id() {
		return ecc_store_id;
	}

	public void setEcc_store_id(Long ecc_store_id) {
		this.ecc_store_id = ecc_store_id;
	}

	public String getEcc_template() {
		return ecc_template;
	}

	public void setEcc_template(String ecc_template) {
		this.ecc_template = ecc_template;
	}

	public Long getEcc_ec_id() {
		return ecc_ec_id;
	}

	public void setEcc_ec_id(Long ecc_ec_id) {
		this.ecc_ec_id = ecc_ec_id;
	}

	public String getEcc_template_offset() {
		if (!CommUtil.null2String(ecc_template_offset).equals(""))
			return ecc_template_offset;
		else
			return "";
	}

	public void setEcc_template_offset(String ecc_template_offset) {
		this.ecc_template_offset = ecc_template_offset;
	}

}
