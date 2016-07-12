package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: OrderEnoughReduce.java
 * </p>
 * 
 * <p>
 * Description: 满就减实体类。
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
 * @author lixiaoyang
 * 
 * @date 2014-9-19
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "enough_reduce")
public class EnoughReduce extends IdEntity {
	private String ertitle;// 活动标题
	@Temporal(TemporalType.DATE)
	private Date erbegin_time;// 开始时间
	@Temporal(TemporalType.DATE)
	private Date erend_time;// 结束时间
	private int ersequence;// 活动序号

	@Column(columnDefinition = "int default 0")
	private int erstatus;// 审核状态 默认为0待审核 10为 审核通过 -10为审核未通过
							// 20为已结束。5为提交审核，此时商家不能再修改
	@Column(columnDefinition = "LongText")
	private String failed_reason;// 审核失败原因

	@Column(columnDefinition = "LongText")
	private String ercontent;// 活动说明
	private String ertag;// 活动的标识,满xxx减xxx
	private String store_id;// 对应的店铺id
	private String store_name;// 对应的店铺名字
	private int er_type;// 满就减类型，0为自营，1为商家
	@Column(columnDefinition = "LongText")
	private String ergoods_ids_json;// 活动商品json
	@Column(columnDefinition = "LongText")
	private String er_json;// 满、减金额的json

	public EnoughReduce() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EnoughReduce(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getFailed_reason() {
		return failed_reason;
	}

	public void setFailed_reason(String failed_reason) {
		this.failed_reason = failed_reason;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public int getEr_type() {
		return er_type;
	}

	public void setEr_type(int er_type) {
		this.er_type = er_type;
	}

	public String getErtitle() {
		return ertitle;
	}

	public void setErtitle(String ertitle) {
		this.ertitle = ertitle;
	}

	public Date getErbegin_time() {
		return erbegin_time;
	}

	public void setErbegin_time(Date erbegin_time) {
		this.erbegin_time = erbegin_time;
	}

	public Date getErend_time() {
		return erend_time;
	}

	public void setErend_time(Date erend_time) {
		this.erend_time = erend_time;
	}

	public int getErsequence() {
		return ersequence;
	}

	public void setErsequence(int ersequence) {
		this.ersequence = ersequence;
	}

	public int getErstatus() {
		return erstatus;
	}

	public void setErstatus(int erstatus) {
		this.erstatus = erstatus;
	}

	public String getErcontent() {
		return ercontent;
	}

	public void setErcontent(String ercontent) {
		this.ercontent = ercontent;
	}

	public String getErtag() {
		return ertag;
	}

	public void setErtag(String ertag) {
		this.ertag = ertag;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getErgoods_ids_json() {
		return ergoods_ids_json;
	}

	public void setErgoods_ids_json(String ergoods_ids_json) {
		this.ergoods_ids_json = ergoods_ids_json;
	}

	public String getEr_json() {
		return er_json;
	}

	public void setEr_json(String er_json) {
		this.er_json = er_json;
	}

}
