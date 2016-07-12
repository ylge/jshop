package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: StoreSlide.java
 * </p>
 * 
 * <p>
 * Description: 店铺幻灯类,用来显示店铺首页大幻灯信息
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_slide")
public class StoreSlide extends IdEntity {
	private String url;
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory acc;
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;
	@Column(columnDefinition = "int default 0")
	private int slide_type;// 0为店铺设置默认幻灯，1为店铺装修中设置的模块幻灯

	public int getSlide_type() {
		return slide_type;
	}

	public void setSlide_type(int slide_type) {
		this.slide_type = slide_type;
	}

	public StoreSlide() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StoreSlide(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Accessory getAcc() {
		return acc;
	}

	public void setAcc(Accessory acc) {
		this.acc = acc;
	}
}
