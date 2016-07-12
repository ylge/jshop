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
 * Title: GoodsFormat.java
 * </p>
 * 
 * <p>
 * Description: 商品版式管理类，商品详细信息可能需要共同的顶部、底部信息，通过版式加载，减去重复编辑商品信息共同内容的麻烦
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
 * @date 2014-10-18
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_format")
public class GoodsFormat extends IdEntity {
	private String gf_name;// 版式名称
	@Column(columnDefinition = "int default 0")
	private int gf_type;// 版式位置，0为顶部版式，1为底部版式
	@Column(columnDefinition = "LongText")
	private String gf_content;// 版式内容
	private Long gf_store_id;// 版式对应的店铺
	@Column(columnDefinition = "int default 0")
	private int gf_cat;// 版式分类，0为商铺版式，1为自营版式

	public GoodsFormat(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public GoodsFormat() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getGf_name() {
		return gf_name;
	}

	public void setGf_name(String gf_name) {
		this.gf_name = gf_name;
	}

	public int getGf_type() {
		return gf_type;
	}

	public void setGf_type(int gf_type) {
		this.gf_type = gf_type;
	}

	public String getGf_content() {
		return gf_content;
	}

	public void setGf_content(String gf_content) {
		this.gf_content = gf_content;
	}

	public int getGf_cat() {
		return gf_cat;
	}

	public void setGf_cat(int gf_cat) {
		this.gf_cat = gf_cat;
	}

	public Long getGf_store_id() {
		return gf_store_id;
	}

	public void setGf_store_id(Long gf_store_id) {
		this.gf_store_id = gf_store_id;
	}

}
