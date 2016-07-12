package com.iskyshop.module.circle.domain;

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
 * Title: CircleClass.java
 * </p>
 * 
 * <p>
 * Description: 圈子分类管理类
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "circle_class")
public class CircleClass extends IdEntity {
	private String className;// 类别名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 索引
	@Column(columnDefinition = "int default 0")
	private int recommend;// 默认为0,1为推荐,推荐后在圈子列表热门分类显示
	@Column(columnDefinition = "int default 0")
	private int nav_index;// 默认为0,1为在圈子导航显示

	public int getNav_index() {
		return nav_index;
	}

	public void setNav_index(int nav_index) {
		this.nav_index = nav_index;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
