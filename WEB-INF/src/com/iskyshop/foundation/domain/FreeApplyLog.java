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
 * Title: FreeApplyLog.java
 * </p>
 * 
 * <p>
 * Description:0元试用商品申请日志
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
 * @date 2014-11-12
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "freeapplylog")
public class FreeApplyLog extends IdEntity {
	private Long user_id;// 申请者的id
	private String user_name;// 申请者名称
	private Long store_id;// 卖家的店铺id
	private Long freegoods_id;// 申请的相关0元试用id
	private String freegoods_name;// 0元试用名称
	@Column(columnDefinition = "LongText")
	private String apply_reason;// 申请原因
	@Column(columnDefinition = "int default 0")
	private int apply_status;// 0为待审核 5为申请通过 等待收货 -5申请失败 -10为过期作废
	@Column(columnDefinition = "LongText")
	private String express_info;// 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String shipCode;// 快递单号
	private String receiver_Name;// 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	private String receiver_area;// 收货人地区,例如：辽宁省沈阳市铁西区
	private String receiver_area_info;// 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip;// 收货人邮政编码
	private String receiver_telephone;// 收货人联系电话
	private String receiver_mobile;// 收货人手机号码
	@Column(columnDefinition = "LongText")
	private String use_experience;// 使用体验
	@Column(columnDefinition = "int default 0")
	private int evaluate_status;// 是否已评价使用体验 默认为0未评价 1为已评价 2不可评价
	private Long ship_addr_id;// 发货地址Id
	@Column(columnDefinition = "LongText")
	private String ship_addr;// 发货详细地址
	private Date evaluate_time;// 评价的时间
	@Column(columnDefinition = "int default 0")
	private int whether_self;// 是否为自营申请

	public FreeApplyLog(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public FreeApplyLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFreegoods_name() {
		return freegoods_name;
	}

	public void setFreegoods_name(String freegoods_name) {
		this.freegoods_name = freegoods_name;
	}

	public int getWhether_self() {
		return whether_self;
	}

	public void setWhether_self(int whether_self) {
		this.whether_self = whether_self;
	}

	public Date getEvaluate_time() {
		return evaluate_time;
	}

	public void setEvaluate_time(Date evaluate_time) {
		this.evaluate_time = evaluate_time;
	}

	public Long getShip_addr_id() {
		return ship_addr_id;
	}

	public void setShip_addr_id(Long ship_addr_id) {
		this.ship_addr_id = ship_addr_id;
	}

	public String getShip_addr() {
		return ship_addr;
	}

	public void setShip_addr(String ship_addr) {
		this.ship_addr = ship_addr;
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
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

	public Long getFreegoods_id() {
		return freegoods_id;
	}

	public void setFreegoods_id(Long freegoods_id) {
		this.freegoods_id = freegoods_id;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public int getApply_status() {
		return apply_status;
	}

	public void setApply_status(int apply_status) {
		this.apply_status = apply_status;
	}

	public String getExpress_info() {
		return express_info;
	}

	public void setExpress_info(String express_info) {
		this.express_info = express_info;
	}

	public String getReceiver_Name() {
		return receiver_Name;
	}

	public void setReceiver_Name(String receiver_Name) {
		this.receiver_Name = receiver_Name;
	}

	public String getReceiver_area() {
		return receiver_area;
	}

	public void setReceiver_area(String receiver_area) {
		this.receiver_area = receiver_area;
	}

	public String getReceiver_area_info() {
		return receiver_area_info;
	}

	public void setReceiver_area_info(String receiver_area_info) {
		this.receiver_area_info = receiver_area_info;
	}

	public String getReceiver_zip() {
		return receiver_zip;
	}

	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}

	public String getReceiver_telephone() {
		return receiver_telephone;
	}

	public void setReceiver_telephone(String receiver_telephone) {
		this.receiver_telephone = receiver_telephone;
	}

	public String getReceiver_mobile() {
		return receiver_mobile;
	}

	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}

	public String getUse_experience() {
		return use_experience;
	}

	public void setUse_experience(String use_experience) {
		this.use_experience = use_experience;
	}

	public int getEvaluate_status() {
		return evaluate_status;
	}

	public void setEvaluate_status(int evaluate_status) {
		this.evaluate_status = evaluate_status;
	}

}
