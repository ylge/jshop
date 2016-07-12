package com.iskyshop.foundation.domain;

import java.util.Date;

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
 * Title: ZTCGoldLog.java
 * </p>
 * 
 * <p>
 * Description: 直通车金币日志,记录所有直通金币日志
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ztc_gold_log")
public class ZTCGoldLog extends IdEntity {
	private Long zgl_goods_id;// 日志商品id
	private String goods_name;
	private String store_name;
	private String user_name;
	private int zgl_gold;// 金币数量
	private int zgl_type;// 0为增加，1为减少
	private String zgl_content;// 描述

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Long getZgl_goods_id() {
		return zgl_goods_id;
	}

	public void setZgl_goods_id(Long zgl_goods_id) {
		this.zgl_goods_id = zgl_goods_id;
	}

	public ZTCGoldLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ZTCGoldLog(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getZgl_gold() {
		return zgl_gold;
	}

	public void setZgl_gold(int zgl_gold) {
		this.zgl_gold = zgl_gold;
	}

	public int getZgl_type() {
		return zgl_type;
	}

	public void setZgl_type(int zgl_type) {
		this.zgl_type = zgl_type;
	}

	public String getZgl_content() {
		return zgl_content;
	}

	public void setZgl_content(String zgl_content) {
		this.zgl_content = zgl_content;
	}
}
