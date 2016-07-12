package com.iskyshop.module.cms.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cmsindextemplate")
public class CmsIndexTemplate extends IdEntity {
	//不同风格楼层中采用的不同展示方式。采用json管理 分为4种。
	//key的命名方式
	//商品id goods_id 名称 goods_name  价格goods_price 图片goods_acc
	//资讯id info_id 名称 info_name 简述info_synopsis 图片info_acc
	//品牌 brand_id brand_name brand_acc
	//0元试用  free_id free_name free_acc 
	//圈子  circle_id circle_name  circle_acc
	@Column(columnDefinition = "LongText")
	private String floor_info1;
	
	@Column(columnDefinition = "LongText")
	private String floor_info2;
	
	@Column(columnDefinition = "LongText")
	private String floor_info3;
	
	@Column(columnDefinition = "LongText")
	private String floor_info4;
	
	private String type;//不同类型的楼层风格分为5种 info-info,goods-class,goods,info-info-goods-brand,goods-free-circle
	private String title;//楼层标题
	@Column(columnDefinition = "int default 0")
	private int sequence;//排序
	@Column(columnDefinition = "int default 0")
	private int whether_show;//是否显示 0为否 1为是
	
	

	public int getWhether_show() {
		return whether_show;
	}
	public void setWhether_show(int whether_show) {
		this.whether_show = whether_show;
	}
	public String getFloor_info1() {
		return floor_info1;
	}
	public void setFloor_info1(String floor_info1) {
		this.floor_info1 = floor_info1;
	}
	public String getFloor_info2() {
		return floor_info2;
	}
	public void setFloor_info2(String floor_info2) {
		this.floor_info2 = floor_info2;
	}
	public String getFloor_info3() {
		return floor_info3;
	}
	public void setFloor_info3(String floor_info3) {
		this.floor_info3 = floor_info3;
	}
	public String getFloor_info4() {
		return floor_info4;
	}
	public void setFloor_info4(String floor_info4) {
		this.floor_info4 = floor_info4;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	

	

}
