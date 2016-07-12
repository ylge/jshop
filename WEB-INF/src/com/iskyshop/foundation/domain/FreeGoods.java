package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
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
 * Title: FreeGoods.java
 * </p>
 * 
 * <p>
 * Description:0元试用商品分为自营与第三方。0元试用商品为免费商品。
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
 * @author jinxinzhe
 * 
 * @date 2014-11-11
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "freegoods")
public class FreeGoods extends IdEntity {
	private Long goods_id;// 商品的id
	private String free_name;// 0元试用的名称
	@OneToOne
	private Accessory free_acc;// 0元试用主图片
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	@Column(columnDefinition = "int default 0")
	private int freeType;// 0为第三方 1为自营
	@Column(columnDefinition = "int default 0")
	private int freeStatus;// 0为待审核 5为审核通过进行中 10为结束 -5为审核未通过
	@Column(columnDefinition = "int default 0")
	private int default_count;// 发布数量
	@Column(columnDefinition = "int default 0")
	private int current_count;// 当前数量
	@Column(columnDefinition = "int default 0")
	private int apply_count;// 申请者人数
	private Long class_id;// 所属的分类id
	private Long store_id;// 所属的店铺id
	@Column(columnDefinition = "LongText")
	private String free_details; // 0元试用详情
	private String goods_name;// 商品名称
	private String failed_reason;// 审核失败时的失败原因
	@Column(columnDefinition = "int default 0")
	private int weixin_recommend;// 微商城推荐， 1为推荐，推荐后在微商城首页显示
	@Temporal(TemporalType.DATE)
	private Date weixin_recommendTime;// 微商城推荐时间，

	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 手机推荐时间，

	public int getMobile_recommend() {
		return mobile_recommend;
	}

	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public FreeGoods(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public FreeGoods(Long id, Date endTime, String free_name, Accessory free_acc) {
		super.setId(id);
		this.setEndTime(endTime);
		this.setFree_name(free_name);
		this.setFree_acc(free_acc);
		// TODO Auto-generated constructor stub
	}

	public FreeGoods() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getWeixin_recommend() {
		return weixin_recommend;
	}

	public void setWeixin_recommend(int weixin_recommend) {
		this.weixin_recommend = weixin_recommend;
	}

	public Date getWeixin_recommendTime() {
		return weixin_recommendTime;
	}

	public void setWeixin_recommendTime(Date weixin_recommendTime) {
		this.weixin_recommendTime = weixin_recommendTime;
	}

	public String getFailed_reason() {
		return failed_reason;
	}

	public void setFailed_reason(String failed_reason) {
		this.failed_reason = failed_reason;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getFree_details() {
		return free_details;
	}

	public void setFree_details(String free_details) {
		this.free_details = free_details;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public Long getClass_id() {
		return class_id;
	}

	public void setClass_id(Long class_id) {
		this.class_id = class_id;
	}

	public String getFree_name() {
		return free_name;
	}

	public void setFree_name(String free_name) {
		this.free_name = free_name;
	}

	public Accessory getFree_acc() {
		return free_acc;
	}

	public void setFree_acc(Accessory free_acc) {
		this.free_acc = free_acc;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
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

	public int getFreeType() {
		return freeType;
	}

	public void setFreeType(int freeType) {
		this.freeType = freeType;
	}

	public int getFreeStatus() {
		return freeStatus;
	}

	public void setFreeStatus(int freeStatus) {
		this.freeStatus = freeStatus;
	}

	public int getDefault_count() {
		return default_count;
	}

	public void setDefault_count(int default_count) {
		this.default_count = default_count;
	}

	public int getCurrent_count() {
		return current_count;
	}

	public void setCurrent_count(int current_count) {
		this.current_count = current_count;
	}

	public int getApply_count() {
		return apply_count;
	}

	public void setApply_count(int apply_count) {
		this.apply_count = apply_count;
	}

}
