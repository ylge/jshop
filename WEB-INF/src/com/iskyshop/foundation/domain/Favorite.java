package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
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
 * Title: Favorite.java
 * </p>
 * 
 * <p>
 * Description:用户收藏类,用来描述用户收藏的商品、收藏的店铺
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "favorite")
public class Favorite extends IdEntity {
	private int type;// 收藏类型，0为商品收藏、1为店铺收藏
	// 收藏的商品信息
	private String goods_name;// 商品名称
	private Long goods_id;// 商品iD
	private String goods_photo;// 商品图片
	private int goods_type;// 商品类型
	private Long goods_store_id;// 商品对应店铺
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_current_price;// 收藏时候的商品价格,若发送降价通知则更新为降价后的价格
	private String goods_photo_ext;// 商品图片后缀
	private String goods_store_second_domain;// 商品店铺二级域名
	// 收藏的店铺信息
	private String store_name;// 店铺名称
	private Long store_id;// 店主ID
	private String store_photo;// 店铺logo
	private String store_second_domain;// 店铺二级域名
	private String store_addr;// 店铺详细地址
	private String store_ower;// 店主真实姓名
	// 收藏人信息
	private String user_name;
	private Long user_id;

	public Favorite(Long id, int type, String goods_name, Long goods_id,
			String goods_photo, int goods_type, Long goods_store_id,
			BigDecimal goods_current_price, String goods_photo_ext,
			String goods_store_second_domain, String user_name, Long user_id) {
		super();
		super.setId(id);
		this.type = type;
		this.goods_name = goods_name;
		this.goods_id = goods_id;
		this.goods_photo = goods_photo;
		this.goods_type = goods_type;
		this.goods_store_id = goods_store_id;
		this.goods_current_price = goods_current_price;
		this.goods_photo_ext = goods_photo_ext;
		this.goods_store_second_domain = goods_store_second_domain;
		this.user_name = user_name;
		this.user_id = user_id;
	}

	public Favorite(Long id, int type, String store_name, Long store_id,
			String store_photo, String store_second_domain, String store_addr,
			String store_ower, String user_name, Long user_id) {
		super();
		super.setId(id);
		this.type = type;
		this.store_name = store_name;
		this.store_id = store_id;
		this.store_photo = store_photo;
		this.store_second_domain = store_second_domain;
		this.store_addr = store_addr;
		this.store_ower = store_ower;
		this.user_name = user_name;
		this.user_id = user_id;
	}

	public String getStore_ower() {
		return store_ower;
	}

	public void setStore_ower(String store_ower) {
		this.store_ower = store_ower;
	}

	public String getStore_second_domain() {
		return store_second_domain;
	}

	public void setStore_second_domain(String store_second_domain) {
		this.store_second_domain = store_second_domain;
	}

	public String getStore_addr() {
		return store_addr;
	}

	public void setStore_addr(String store_addr) {
		this.store_addr = store_addr;
	}

	public String getGoods_store_second_domain() {
		return goods_store_second_domain;
	}

	public void setGoods_store_second_domain(String goods_store_second_domain) {
		this.goods_store_second_domain = goods_store_second_domain;
	}

	public String getGoods_photo_ext() {
		return goods_photo_ext;
	}

	public void setGoods_photo_ext(String goods_photo_ext) {
		this.goods_photo_ext = goods_photo_ext;
	}

	public BigDecimal getGoods_current_price() {
		return goods_current_price;
	}

	public void setGoods_current_price(BigDecimal goods_current_price) {
		this.goods_current_price = goods_current_price;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public Long getGoods_store_id() {
		return goods_store_id;
	}

	public void setGoods_store_id(Long goods_store_id) {
		this.goods_store_id = goods_store_id;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_photo() {
		return goods_photo;
	}

	public void setGoods_photo(String goods_photo) {
		this.goods_photo = goods_photo;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getStore_photo() {
		return store_photo;
	}

	public void setStore_photo(String store_photo) {
		this.store_photo = store_photo;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Favorite() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Favorite(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
