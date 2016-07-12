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
 * Description: 用户帖子回复管理类，
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
 * @date 2014-12-9
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "invitation_replys")
public class CircleInvitationReply extends IdEntity {
	@Column(columnDefinition = "LongText")
	private String content;// 回复信息
	@Column(columnDefinition = "int default 0")
	private int level_count;// 楼层号
	private Long parent_id;// 上级回复信息id
	private Long invitation_id;// 对应的帖子id
	private Long user_id;// 对应的回复人id
	private String user_name;// 对应的回复人姓名
	private String user_photo;// 对应的回复人头像路径“upload/”
	@Column(columnDefinition = "int default 0")
	private int reply_count;// 回复数量，当作为上级回复信息时

	public int getReply_count() {
		return reply_count;
	}

	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}

	public int getLevel_count() {
		return level_count;
	}

	public void setLevel_count(int level_count) {
		this.level_count = level_count;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_photo() {
		return user_photo;
	}

	public void setUser_photo(String user_photo) {
		this.user_photo = user_photo;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delScriptTag(content);
		this.content = content;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public Long getInvitation_id() {
		return invitation_id;
	}

	public void setInvitation_id(Long invitation_id) {
		this.invitation_id = invitation_id;
	}

}
