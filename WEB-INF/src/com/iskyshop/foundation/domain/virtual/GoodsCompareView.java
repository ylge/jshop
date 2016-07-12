package com.iskyshop.foundation.domain.virtual;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>
 * Title: GoodsCompareView.java
 * </p>
 * 
 * <p>
 * Description: 商品对比栏信息管理类，用来封装商品对比栏数据，用在商品对比页
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
 * @date 2014-10-22
 * 
 * @version iskyshop_b2b2c 2015
 */
public class GoodsCompareView {
	private Long goods_id;// 商品id
	private String goods_img;// 商品图地址，使用中号尺寸图片
	private String goods_name;// 商品名称
	private BigDecimal goods_price;// 商品价格
	private String goods_url;// 商品地址
	private String goods_brand;// 商品品牌
	private String tax_invoice;// 是否支持增值税发票
	private String goods_cod;// 是否支持货到付款
	private BigDecimal goods_weight;// 商品重量
	private String well_evaluate;// 好评率
	private String middle_evaluate;// 中评率
	private String bad_evaluate;// 差评率
	private Map props = new HashMap();// 商品相关属性键值对，如：颜色-红,黄，蓝

	public Map getProps() {
		return props;
	}

	public void setProps(Map props) {
		this.props = props;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_img() {
		return goods_img;
	}

	public void setGoods_img(String goods_img) {
		this.goods_img = goods_img;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public BigDecimal getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_url() {
		return goods_url;
	}

	public void setGoods_url(String goods_url) {
		this.goods_url = goods_url;
	}

	public String getGoods_brand() {
		return goods_brand;
	}

	public void setGoods_brand(String goods_brand) {
		this.goods_brand = goods_brand;
	}

	public String getTax_invoice() {
		return tax_invoice;
	}

	public void setTax_invoice(String tax_invoice) {
		this.tax_invoice = tax_invoice;
	}

	public String getGoods_cod() {
		return goods_cod;
	}

	public void setGoods_cod(String goods_cod) {
		this.goods_cod = goods_cod;
	}

	public BigDecimal getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(BigDecimal goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getWell_evaluate() {
		return well_evaluate;
	}

	public void setWell_evaluate(String well_evaluate) {
		this.well_evaluate = well_evaluate;
	}

	public String getMiddle_evaluate() {
		return middle_evaluate;
	}

	public void setMiddle_evaluate(String middle_evaluate) {
		this.middle_evaluate = middle_evaluate;
	}

	public String getBad_evaluate() {
		return bad_evaluate;
	}

	public void setBad_evaluate(String bad_evaluate) {
		this.bad_evaluate = bad_evaluate;
	}

}
