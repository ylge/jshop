package com.iskyshop.foundation.domain.virtual;

/**
 * 
 * <p>
 * Title: IntegralGoodsOrderInfo.java
 * </p>
 * 
 * <p>
 * Description:
 * 积分订单虚拟数据管理类，该类用来封装积分订单的每一个条兑换商品信息及积分订单整体积分等信息，用来前端显示，目前用在买家中心显示积分订单位置处
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
 * @date 2014-10-14
 * 
 * @version iskyshop_b2b2c 2015
 */
public class IntegralGoodsOrderView {
	private int igo_total_integral;// 总共消费积分
	private String igo_goods_name;// 积分商品名称
	private Long igo_goods_id;// 积分商品id
	private Long igo_order_id;// 所在积分兑换订单的id
	private String igo_goods_img;// 积分商品图片的地址信息

	public Long getIgo_goods_id() {
		return igo_goods_id;
	}

	public void setIgo_goods_id(Long igo_goods_id) {
		this.igo_goods_id = igo_goods_id;
	}

	public String getIgo_goods_img() {
		return igo_goods_img;
	}

	public void setIgo_goods_img(String igo_goods_img) {
		this.igo_goods_img = igo_goods_img;
	}

	public int getIgo_total_integral() {
		return igo_total_integral;
	}

	public void setIgo_total_integral(int igo_total_integral) {
		this.igo_total_integral = igo_total_integral;
	}

	public String getIgo_goods_name() {
		return igo_goods_name;
	}

	public void setIgo_goods_name(String igo_goods_name) {
		this.igo_goods_name = igo_goods_name;
	}

	public Long getIgo_order_id() {
		return igo_order_id;
	}

	public void setIgo_order_id(Long igo_order_id) {
		this.igo_order_id = igo_order_id;
	}

}
