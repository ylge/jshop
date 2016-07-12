package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: StoreAdjuseInfo.java</p>

* <p>Description: 此类用来记录商家申请调整自己店铺相关信息的操作，一个商家同一时间只能拥有一个申请，</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2015-1-5

 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_adjust_info")
public class StoreAdjustInfo extends IdEntity{
	private Long store_id;//店铺id；
	private String store_name;//店铺名称
	@Column(columnDefinition = "int default 0")
	private int apply_status;//0-已成功提交申请，5-申请被拒绝
	private String adjust_type;//类型，gc-调整经营类目，
	private String adjust_store_grade;//所调整的店铺等级
	private Long adjust_storeGrade_id;//所调整的店铺等级id
	private String adjust_gc_main;//所调整的主营类目
	private Long adjust_gc_main_id;//所调整的主营类目id
	@Column(columnDefinition = "LongText")
	private String adjust_gc_info;//所要调整的经营类目信息，
	
	
	
	public Long getAdjust_storeGrade_id() {
		return adjust_storeGrade_id;
	}
	public Long getAdjust_gc_main_id() {
		return adjust_gc_main_id;
	}
	public void setAdjust_storeGrade_id(Long adjust_storeGrade_id) {
		this.adjust_storeGrade_id = adjust_storeGrade_id;
	}
	public void setAdjust_gc_main_id(Long adjust_gc_main_id) {
		this.adjust_gc_main_id = adjust_gc_main_id;
	}
	public String getAdjust_store_grade() {
		return adjust_store_grade;
	}
	public void setAdjust_store_grade(String adjust_store_grade) {
		this.adjust_store_grade = adjust_store_grade;
	}
	public String getAdjust_gc_main() {
		return adjust_gc_main;
	}
	public void setAdjust_gc_main(String adjust_gc_main) {
		this.adjust_gc_main = adjust_gc_main;
	}
	public Long getStore_id() {
		return store_id;
	}
	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public int getApply_status() {
		return apply_status;
	}
	public void setApply_status(int apply_status) {
		this.apply_status = apply_status;
	}
	public String getAdjust_type() {
		return adjust_type;
	}
	public void setAdjust_type(String adjust_type) {
		this.adjust_type = adjust_type;
	}
	public String getAdjust_gc_info() {
		return adjust_gc_info;
	}
	public void setAdjust_gc_info(String adjust_gc_info) {
		this.adjust_gc_info = adjust_gc_info;
	}
	
	
}
