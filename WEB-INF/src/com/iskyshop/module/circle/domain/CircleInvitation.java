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
 * Title: Invitation.java
 * </p>
 * 
 * <p>
 * Description: 用户帖子管理类，用户加入相应的圈子之后可以发布相关帖子
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "invitation")
public class CircleInvitation extends IdEntity {
	private String title;// 帖子标题
	private String type;// 帖子类型，分为“原创”、“转载”
	private long circle_id;// 所属圈子
	private String circle_name;// 所属圈子名称
	private long user_id;// 发帖人
	private String userName;// 发帖人姓名
	@Column(columnDefinition = "LongText")
	private String content;// 帖子详情
	@Column(columnDefinition = "LongText")
	private String photoInfo;// 帖子图片信息,多图片，使用json管理[{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"},{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"},{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"},{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"}]
	@Column(columnDefinition = "LongText")
	private String praiseInfo;// 帖子点赞用户信息，记录用户id，使用逗号间隔1,2,3,4,5,6,7,
	@Column(columnDefinition = "int default 0")
	private int praise_count;// 帖子点赞数量
	@Column(columnDefinition = "int default 0")
	private int reply_count;// 帖子总回复数量
	@Column(columnDefinition = "int default 0")
	private int invitaion_top;// 帖子是否置顶，1为置顶，一个圈子中只有一个置顶帖子
	@Column(columnDefinition = "int default 0")
	private int invitaion_perfect;// 帖子是否加精，1为加精，一个圈子中可有多个加精帖子
	@Column(columnDefinition = "LongText")
	private String item_info;// 帖子的附加信息，包括附加商品、店铺，使用json管理，一个帖子最多附件7个商品、店铺信息,格式为：[{"item_id":1,"item_type":"goods","item_img":"xxxx","item_url":"xxxx"}]

	public String getItem_info() {
		return item_info;
	}

	public void setItem_info(String item_info) {
		this.item_info = item_info;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getInvitaion_top() {
		return invitaion_top;
	}

	public void setInvitaion_top(int invitaion_top) {
		this.invitaion_top = invitaion_top;
	}

	public int getInvitaion_perfect() {
		return invitaion_perfect;
	}

	public void setInvitaion_perfect(int invitaion_perfect) {
		this.invitaion_perfect = invitaion_perfect;
	}

	public int getReply_count() {
		return reply_count;
	}

	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}

	public int getPraise_count() {
		return praise_count;
	}

	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}

	public String getPraiseInfo() {
		return praiseInfo;
	}

	public void setPraiseInfo(String praiseInfo) {
		this.praiseInfo = praiseInfo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCircle_name() {
		return circle_name;
	}

	public void setCircle_name(String circle_name) {
		this.circle_name = circle_name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCircle_id() {
		return circle_id;
	}

	public void setCircle_id(long circle_id) {
		this.circle_id = circle_id;
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
		content = HtmlFilterTools.delScriptTag(content);
		this.content = content;
	}

	public String getPhotoInfo() {
		return photoInfo;
	}

	public void setPhotoInfo(String photoInfo) {
		this.photoInfo = photoInfo;
	}

}
