package com.iskyshop.module.sns.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_share")
public class UserShare extends IdEntity{
	private Long user_id;//分享用户的id
	private String user_name;//分享用户的name
	@Column(columnDefinition = "int default 1")
	private Long share_goods_id;//分享商品的id
	private String share_goods_name;//分享商品的名称
	private String share_goods_photo;//分享商品的主照片
	@Column(columnDefinition = "LongText")
	private String share_content;//分享的内容
	
	
	
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
	public Long getShare_goods_id() {
		return share_goods_id;
	}
	public void setShare_goods_id(Long share_goods_id) {
		this.share_goods_id = share_goods_id;
	}
	public String getShare_goods_name() {
		return share_goods_name;
	}
	public void setShare_goods_name(String share_goods_name) {
		this.share_goods_name = share_goods_name;
	}
	public String getShare_goods_photo() {
		return share_goods_photo;
	}
	public void setShare_goods_photo(String share_goods_photo) {
		this.share_goods_photo = share_goods_photo;
	}
	public String getShare_content() {
		return share_content;
	}
	public void setShare_content(String share_content) {
		share_content = HtmlFilterTools.delAllTag(share_content);
		this.share_content = share_content;
	}
	
	
	
}
