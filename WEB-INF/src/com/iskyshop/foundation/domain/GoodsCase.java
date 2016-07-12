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
 * Title: GoodsCase.java
 * </p>
 * 
 * <p>
 * Description: 橱窗展示管理类，用来管理首页等页面中推荐商品、广告、图片等橱窗信息，更加方便用户灵活控制页面信息
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
 * @date 2014-9-16
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_case")
public class GoodsCase extends IdEntity {
	private String case_name;// 橱窗名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 排序
	@Column(columnDefinition = "int default 0")
	private int display;
	private String case_id;// 橱窗标识，如index_top为首页顶部橱窗，页面显示橱窗时作为参数传递
	@Column(columnDefinition = "LongText")
	private String case_content;// 橱窗信息，使用json管理

	public GoodsCase() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GoodsCase(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getCase_id() {
		return case_id;
	}

	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}

	public int getDisplay() {
		return display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getCase_name() {
		return case_name;
	}

	public void setCase_name(String case_name) {
		this.case_name = case_name;
	}

	public String getCase_content() {
		return case_content;
	}

	public void setCase_content(String case_content) {
		this.case_content = case_content;
	}

}
