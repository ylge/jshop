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
 * Title: GoodsLog.java
 * </p>
 * 
 * <p>
 * Description: 商品日志，记录商品每一天的信息，用于统计分析
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
 * @date 2014-11-6
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodslog")
public class GoodsLog extends IdEntity {
	private Long goods_id;// 商品id
	private String goods_name;// 商品名称
	private Long img_id;// 商品图片id
	private Long gc_id;// 商品对应的大分类id
	private Long goods_brand_id;// 商品品牌id
	@Column(precision = 12, scale = 2)
	private BigDecimal price;// 当日价格
	@Column(columnDefinition = "int default 0")
	private int log_form;// 日志种类，0为自营，1为商家
	private long store_id;// 对应的商家id
	private String store_name;// 对应的商家名称
	@Column(columnDefinition = "int default 0")
	private int goods_click;// 商品当天浏点击量
	@Column(columnDefinition = "LongText")
	private String goods_click_from;// 点击量的来源，例如搜索，广告，推荐等，json管理
	@Column(columnDefinition = "int default 0")
	private int goods_collect;// 商品当天收藏次数
	private String preferential;// 当天使用的优惠活动
	@Column(columnDefinition = "LongText")
	private String preferential_info;// 优惠活动信息，json管理
	@Column(columnDefinition = "int default 0")
	private int goods_salenum;// 商品售出数量
	@Column(columnDefinition = "LongText")
	private String goods_order_type;// 订单类型，web，Android，ios，json管理
	@Column(columnDefinition = "LongText")
	private String goods_sale_info;// 售出的商品 规格与数量，json管理

	public GoodsLog(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public GoodsLog() {
		super();
		// TODO Auto-generated constructor stub
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

	public Long getImg_id() {
		return img_id;
	}

	public void setImg_id(Long img_id) {
		this.img_id = img_id;
	}

	public Long getGc_id() {
		return gc_id;
	}

	public void setGc_id(Long gc_id) {
		this.gc_id = gc_id;
	}

	public Long getGoods_brand_id() {
		return goods_brand_id;
	}

	public void setGoods_brand_id(Long goods_brand_id) {
		this.goods_brand_id = goods_brand_id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getLog_form() {
		return log_form;
	}

	public void setLog_form(int log_form) {
		this.log_form = log_form;
	}

	public long getStore_id() {
		return store_id;
	}

	public void setStore_id(long store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public int getGoods_click() {
		return goods_click;
	}

	public void setGoods_click(int goods_click) {
		this.goods_click = goods_click;
	}

	public String getGoods_click_from() {
		return goods_click_from;
	}

	public void setGoods_click_from(String goods_click_from) {
		this.goods_click_from = goods_click_from;
	}

	public int getGoods_collect() {
		return goods_collect;
	}

	public void setGoods_collect(int goods_collect) {
		this.goods_collect = goods_collect;
	}

	public String getPreferential() {
		return preferential;
	}

	public void setPreferential(String preferential) {
		this.preferential = preferential;
	}

	public String getPreferential_info() {
		return preferential_info;
	}

	public void setPreferential_info(String preferential_info) {
		this.preferential_info = preferential_info;
	}

	public int getGoods_salenum() {
		return goods_salenum;
	}

	public void setGoods_salenum(int goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public String getGoods_order_type() {
		return goods_order_type;
	}

	public void setGoods_order_type(String goods_order_type) {
		this.goods_order_type = goods_order_type;
	}

	public String getGoods_sale_info() {
		return goods_sale_info;
	}

	public void setGoods_sale_info(String goods_sale_info) {
		this.goods_sale_info = goods_sale_info;
	}

}
