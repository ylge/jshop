package com.iskyshop.kuaidi100.domain;

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
 * Title: ExpressInfo.java
 * </p>
 * 
 * <p>
 * Description:
 * 快递100收费接口推送信息接收类，该类接收快递100推送的信息，如果系统启用了快递100收费接口，用户查询信息会从该类中查询，不会再从快递100查询
 * ，该查询实时性高且性能更加良好
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
 * @date 2014-11-4
 * 
 * @version iskyshop_b2b2c 2015
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "express_info")
public class ExpressInfo extends IdEntity {
	private Long order_id;// 订单id
	private String order_express_name;//快递公司名称
	@Column(columnDefinition = "int default 0")
	private int order_type;//0为发货快递单，1为退货快递单
	@Column(columnDefinition = "LongText")
	private String order_express_id;// 快递单号
	@Column(columnDefinition = "LongText")
	private String order_express_info;// 订单的快递信息
	@Column(columnDefinition = "int default 0")
    private int order_status;//订单的快递状态，初次订阅为0，快递100回调推送后变为1,根据此状态快递查询快递100是否有回调推送

	
	public String getOrder_express_name() {
		return order_express_name;
	}

	public void setOrder_express_name(String order_express_name) {
		this.order_express_name = order_express_name;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public String getOrder_express_id() {
		return order_express_id;
	}

	public void setOrder_express_id(String order_express_id) {
		this.order_express_id = order_express_id;
	}

	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public String getOrder_express_info() {
		return order_express_info;
	}

	public void setOrder_express_info(String order_express_info) {
		this.order_express_info = order_express_info;
	}

}
