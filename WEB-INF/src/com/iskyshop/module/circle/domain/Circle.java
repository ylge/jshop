package com.iskyshop.module.circle.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: Circle.java
 * </p>
 * 
 * <p>
 * Description: 圈子管理类，用户进入相应的圈子后可以发布帖子
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
 * @date 2014-11-18
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "circle")
public class Circle extends IdEntity {
	private String title;// 圈子名称
	@Column(columnDefinition = "LongText")
	private String photoInfo;// 圈子图标信息，使用json管理{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"}
	@Column(columnDefinition = "int default 0")
	private int status;// 圈子状态，平台审核该圈子的状态，0为未审核，5为审核通过，用户可以在该圈子发帖，-1为审核失败
	private long class_id;// 圈子所属分类id
	private String class_name;// 圈子所属分类名称
	private long user_id;// 圈子创建人id（圈子管理员id）,平台可以管理圈子，如圈子中存在违规帖子
	private String userName;// 圈子创建人姓名
	@Column(columnDefinition = "LongText")
	private String content;// 圈子说明
	@Column(columnDefinition = "int default 0")
	private int attention_count;// 总关注人数
	@Column(columnDefinition = "int default 0")
	private int invitation_count;// 总帖子数量
	@Column(columnDefinition = "LongText")
	private String refuseMsg;// 圈子创建审核拒绝理由
	@Column(columnDefinition = "int default 0")
	private int recommend;// 是否推荐，1为推荐

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getRefuseMsg() {
		return refuseMsg;
	}

	public void setRefuseMsg(String refuseMsg) {
		this.refuseMsg = refuseMsg;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPhotoInfo() {
		return photoInfo;
	}

	public void setPhotoInfo(String photoInfo) {
		this.photoInfo = photoInfo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getClass_id() {
		return class_id;
	}

	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public int getAttention_count() {
		return attention_count;
	}

	public void setAttention_count(int attention_count) {
		this.attention_count = attention_count;
	}

	public int getInvitation_count() {
		return invitation_count;
	}

	public void setInvitation_count(int invitation_count) {
		this.invitation_count = invitation_count;
	}

}
