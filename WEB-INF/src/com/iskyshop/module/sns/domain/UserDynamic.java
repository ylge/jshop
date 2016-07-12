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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_dynamic")
public class UserDynamic extends IdEntity{
	private Long user_id;//发布用户的id
	private String user_name;//发布用户的name
	@Column(columnDefinition = "LongText")
	private String dynamic_content;//动态的内容
	@Column(columnDefinition = "LongText")
	private String img_info;//动态中的图片信息，json管理
	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getDynamic_content() {
		return dynamic_content;
	}
	public void setDynamic_content(String dynamic_content) {
		dynamic_content = HtmlFilterTools.delAllTag(dynamic_content);
		this.dynamic_content = dynamic_content;
	}
	public String getImg_info() {
		return img_info;
	}
	public void setImg_info(String img_info) {
		this.img_info = img_info;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	
	

}
