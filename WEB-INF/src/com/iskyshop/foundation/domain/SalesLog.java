package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: SalesLog.java
 * </p>
 * 
 * <p>
 * Description: 促销方式购买记录。
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
 * @author hezeng
 * 
 * @date 2014-9-19
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sales_log")
public class SalesLog extends IdEntity {
	private Date begin_time;// 开始时间
	private Date end_time;// 结束时间
	private int gold;// 花费金币
	private Long store_id;// 对应的店铺id
	private int sales_type;// 促销方式 0为满就减 1为组合销售 2为满就送 3为满就减 4为团购套餐
	private String sales_info;// 促销方式其他信息

	public SalesLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SalesLog(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getSales_info() {
		return sales_info;
	}

	public void setSales_info(String sales_info) {
		this.sales_info = sales_info;
	}

	public int getSales_type() {
		return sales_type;
	}

	public void setSales_type(int sales_type) {
		this.sales_type = sales_type;
	}

	public Date getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(Date begin_time) {
		this.begin_time = begin_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

}
