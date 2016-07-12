package com.iskyshop.module.app.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: AppPushUser.java
 * </p>
 * 
 * <p>
 * Description: 推送，绑定用户信息
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
 * @author lixiaoyang
 * 
 * @date 2015-2-7
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "app_push_user")
public class AppPushUser extends IdEntity {
	private String app_id;// 设备id
	private String app_type;// 设备类别，Android，ios
	private String app_channelId;// 设备频道id
	private String user_id;// 用户id，若果没登陆即为空
	private String app_userRole;// App用户角色，预留，区分买家app和商家app,"buyer"为买家，seller为商家

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public String getApp_userRole() {
		return app_userRole;
	}

	public void setApp_userRole(String app_userRole) {
		this.app_userRole = app_userRole;
	}

	public String getApp_channelId() {
		return app_channelId;
	}

	public void setApp_channelId(String app_channelId) {
		this.app_channelId = app_channelId;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

}
