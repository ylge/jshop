package com.iskyshop.foundation.domain;

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
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: ComplaintGoods.java
 * </p>
 * 
 * <p>
 * Description:投诉商品管理类，用来描述投诉的商品信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "complaint_goods")
public class ComplaintGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 被投诉的商品
	@Column(columnDefinition = "LongText")
	private String content;// 投诉该商品意见
	@ManyToOne(fetch = FetchType.LAZY)
	private Complaint complaint;// 对应的投诉

	public ComplaintGoods(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public ComplaintGoods() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}
}
