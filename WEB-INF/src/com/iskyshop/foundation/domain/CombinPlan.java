package com.iskyshop.foundation.domain;

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
 * Title: CombinPlan.java
 * </p>
 * 
 * <p>
 * Description: 组合销售信息管理类，商家或者平台自营可以添加商品组合，每个组合信息对应多个个组合方案信息，每个组合方案中包含一组组合商品信息，
 * 商家添加组合信息需要平台审核，平台审核通过后组合商品在商品详细页中显示并可供用户购买
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
 * @date 2014-9-29
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "combinplan")
public class CombinPlan extends IdEntity {
	@Column(columnDefinition = "LongText")
	private String main_goods_info;// 组合销售主商品,使用json管理{"id":1,"name":"测试商品"}
	private Long main_goods_id;// 主体商品id
	private String main_goods_name;// 主体商品名称
	private Long store_id;// 组合对应的店铺id，只有combin_form为1时才有值
	@Column(columnDefinition = "LongText")
	private String combin_plan_info;// 组合信息，组合信息中包含多个组合方案信息，每个组合方案中包含一组组合商品信息，使用json管理[{"all_goods_price":567.0,"plan_goods_price":"500","goods_list":[{"id":7,"name":"123"},
									// {"id":8,"name":"测试商品5"}]},
									// {"all_goods_price":920.0,"plan_goods_price":"400","goods_list":[{"id":9,"name":"123"},
									// {"id":8,"name":"测试商品5"}]}]
	@Column(columnDefinition = "int default 0")
	private int combin_type;// 组合方式，0为组合套装，1为组合配件，购买组合套装时，订单价钱为组合销售价钱，购买组合配件时，用户可以任意选择该组合中的配件，且所有商品价格为商品的原价格
	@Column(columnDefinition = "int default 0")
	private int combin_status;// 组合状态，0为未审核，1为审核通过，-1为审核失败,-2已过期,-5商家手动下架，只有通过审核的组合才可以自由上架下架
	@Column(columnDefinition = "int default 0")
	private int combin_form;// 组合类型，0为平台组合，1为商家组合，商家组合需要平台审核
	private String combin_refuse_msg;// 审核失败信息
	private Date beginTime;// 组合开始时间
	private Date endTime;// 组合结束时间

	public CombinPlan() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CombinPlan(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getMain_goods_name() {
		return main_goods_name;
	}

	public void setMain_goods_name(String main_goods_name) {
		this.main_goods_name = main_goods_name;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getMain_goods_info() {
		return main_goods_info;
	}

	public void setMain_goods_info(String main_goods_info) {
		this.main_goods_info = main_goods_info;
	}

	public Long getMain_goods_id() {
		return main_goods_id;
	}

	public void setMain_goods_id(Long main_goods_id) {
		this.main_goods_id = main_goods_id;
	}

	public String getCombin_plan_info() {
		return combin_plan_info;
	}

	public void setCombin_plan_info(String combin_plan_info) {
		this.combin_plan_info = combin_plan_info;
	}

	public int getCombin_type() {
		return combin_type;
	}

	public void setCombin_type(int combin_type) {
		this.combin_type = combin_type;
	}

	public int getCombin_status() {
		return combin_status;
	}

	public void setCombin_status(int combin_status) {
		this.combin_status = combin_status;
	}

	public int getCombin_form() {
		return combin_form;
	}

	public void setCombin_form(int combin_form) {
		this.combin_form = combin_form;
	}

	public String getCombin_refuse_msg() {
		return combin_refuse_msg;
	}

	public void setCombin_refuse_msg(String combin_refuse_msg) {
		this.combin_refuse_msg = combin_refuse_msg;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
