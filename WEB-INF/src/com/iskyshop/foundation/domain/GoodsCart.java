package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * <p>
 * Title: GoodsCart.java
 * </p>
 * <p>
 * Description: * 商城购物车类， ，购物车信息直接保存到数据库中
 * ，未登录用户根据随机唯一Id保存（包括手机端），已经登录的用户根据User来保存
 * ，未登录用户购物车间隔1天自动删除（包括手机端），已经登录用户购物车保存7天 ，7天未提交为订单自动删除,购物车信息存在及时性，不加入缓存管理
 * 
 * </p>
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
 * @date 2014-4-25
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodscart")
public class GoodsCart extends IdEntity {
	@ManyToOne
	private Goods goods;// 对应的商品
	private int count;// 数量
	@Column(precision = 12, scale = 2)
	private BigDecimal price;// 价格
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "gsp_id"))
	private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();// 对应的规格
	@Column(columnDefinition = "LongText")
	private String spec_info;// 规格内容
	private String cart_type;// 默认为空，组合销售时候为"combin"
	private String cart_gsp;// 购物车中商品规格id
	@Column(columnDefinition = "int default 0")
	private int combin_main;// 1为套装主购物车，0为其他套装购物车
	private String combin_mark;// 组合套装标识，购买套装时，一个套装中的套装标识为相同
	private String combin_suit_ids;// 组合套装购物车id（包括自己）,当购物车为套装主购物车时有值
	@Column(columnDefinition = "LongText")
	private String combin_suit_info;// 套装购物车详情，使用json管理，{"suit_count":2,"plan_goods_price":"429","all_goods_price":"236.00","goods_list":[
									// {"id":92,"price":25.0,"inventory":465765,"store_price":25.0,"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048","img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg","url":"http://localhost/goods_92.htm"}],"suit_all_price":"429.00"}
	private String combin_version;// 组合套装版本，当选择同一个主商品的套装时，分为[套装1]、[套装2]
	@ManyToOne
	private User user;// 对应的购物车用户
	private String cart_session_id;// 未登录用户会话Id
	private String cart_mobile_id;// 手机端未登录用户会话Id
	@Column(columnDefinition = "LongText")
	private String gift_info;// 买就送赠品信息
								// 采用json管理{"goods_id":"1","goods_name";"阿迪达斯运动鞋"}
	@Column(columnDefinition = "int default 0")
	private int whether_choose_gift;// 是否选择了赠品 默认为0 未选择 1为已选择
	@Column(columnDefinition = "int default 0")
	private int cart_status;// 用户购物车状态，0表示没有提交为订单，1表示已经提交为订单，已经提交为订单信息的不再为缓存购物车，同时定时器也不进行删除操作

	public String getCombin_version() {
		return combin_version;
	}

	public void setCombin_version(String combin_version) {
		this.combin_version = combin_version;
	}

	public GoodsCart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GoodsCart(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getCart_gsp() {
		return cart_gsp;
	}

	public void setCart_gsp(String cart_gsp) {
		this.cart_gsp = cart_gsp;
	}

	public int getWhether_choose_gift() {
		return whether_choose_gift;
	}

	public void setWhether_choose_gift(int whether_choose_gift) {
		this.whether_choose_gift = whether_choose_gift;
	}

	public String getCombin_suit_info() {
		return combin_suit_info;
	}

	public void setCombin_suit_info(String combin_suit_info) {
		this.combin_suit_info = combin_suit_info;
	}

	public int getCombin_main() {
		return combin_main;
	}

	public void setCombin_main(int combin_main) {
		this.combin_main = combin_main;
	}

	public String getCombin_mark() {
		return combin_mark;
	}

	public void setCombin_mark(String combin_mark) {
		this.combin_mark = combin_mark;
	}

	public String getCombin_suit_ids() {
		return combin_suit_ids;
	}

	public void setCombin_suit_ids(String combin_suit_ids) {
		this.combin_suit_ids = combin_suit_ids;
	}

	public String getGift_info() {
		return gift_info;
	}

	public void setGift_info(String gift_info) {
		this.gift_info = gift_info;
	}

	public int getCart_status() {
		return cart_status;
	}

	public void setCart_status(int cart_status) {
		this.cart_status = cart_status;
	}

	public String getCart_mobile_id() {
		return cart_mobile_id;
	}

	public void setCart_mobile_id(String cart_mobile_id) {
		this.cart_mobile_id = cart_mobile_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCart_session_id() {
		return cart_session_id;
	}

	public void setCart_session_id(String cart_session_id) {
		this.cart_session_id = cart_session_id;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<GoodsSpecProperty> getGsps() {
		return gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCart_type() {
		return cart_type;
	}

	public void setCart_type(String cart_type) {
		this.cart_type = cart_type;
	}
}
