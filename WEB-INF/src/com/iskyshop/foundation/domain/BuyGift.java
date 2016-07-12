package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
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
 * Title: BuyGift.java
 * </p>
 * 
 * <p>
 * Description:满就送实体类。使用json管理参与满就送商品及赠品。每个店铺只能开启一个满就送。
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
 * @date 2014-9-22
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "buygift")
public class BuyGift extends IdEntity{
		private Long store_id;//开启满就送店铺
		private String store_name;//店铺名称
		@Column(columnDefinition="int default 0")
		private int gift_type;//0为自营满就送 1为第三方满就送
		@Column(precision = 12, scale = 2)
		private BigDecimal  condition_amount;//满足满就送条件金额 需要大于此金额才可满足满就送条件
		@Column(columnDefinition="int default 0")
		private int gift_status;//审核状态 默认为0待审核  10为 审核通过  -10为审核未通过  20为已结束。
		@Column(columnDefinition = "LongText")
		private String failed_reason;//审核失败原因
		@Column(columnDefinition = "LongText")
		private String goods_info;//[{"goods_id":"1" ,"goods_name":"鳄鱼褐色纯皮裤腰带",store_domainPath这个是店铺二级域名路径 goods_domainPath商品二级域名路径
												//"goods_main_photo":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_price":"54.30"}] 参加的商品
		@Column(columnDefinition = "LongText")
		private String gift_info;//[{"goods_id":"1" ,"goods_name":"鳄鱼纯棉袜子", store_domainPath这个是店铺二级域名路径 goods_domainPath商品二级域名路径
										    //"goods_main_photo":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"
										   //,"goods_price":"3.30","goods_count":"200","storegoods_count":"0或1"}] 赠品goods_count为赠送数量 需要小于当前库存。
											// storegoods_count为0时则 使用 goods_count为赠送数量 即在goods_count 确认后会同时扣除当前商品的库存  如库存为200
											//赠送数量100 当前商品库存变为100 storegoods_count为1时 使用商品当前库存为赠送数量 如库存为200 则赠送个数为200 当
											//正常出售1个赠送商品 后库存为199 赠送数量也为199  赠送数与库存数同步 此时没有goods_count
		private Date beginTime;// 开始时间
		private Date endTime;// 结束时间

		public BuyGift() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public BuyGift(Long id, Date addTime) {
			super(id, addTime);
			// TODO Auto-generated constructor stub
		}
		public String getStore_name() {
			return store_name;
		}
		public void setStore_name(String store_name) {
			this.store_name = store_name;
		}
		public Long getStore_id() {
			return store_id;
		}
		public void setStore_id(Long store_id) {
			this.store_id = store_id;
		}
		public int getGift_type() {
			return gift_type;
		}
		public void setGift_type(int gift_type) {
			this.gift_type = gift_type;
		}
		public BigDecimal getCondition_amount() {
			return condition_amount;
		}
		public void setCondition_amount(BigDecimal condition_amount) {
			this.condition_amount = condition_amount;
		}
		public int getGift_status() {
			return gift_status;
		}
		public void setGift_status(int gift_status) {
			this.gift_status = gift_status;
		}
		public String getFailed_reason() {
			return failed_reason;
		}
		public void setFailed_reason(String failed_reason) {
			this.failed_reason = failed_reason;
		}
		public String getGoods_info() {
			return goods_info;
		}
		public void setGoods_info(String goods_info) {
			this.goods_info = goods_info;
		}
		public String getGift_info() {
			return gift_info;
		}
		public void setGift_info(String gift_info) {
			this.gift_info = gift_info;
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
