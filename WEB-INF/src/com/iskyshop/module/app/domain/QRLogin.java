package com.iskyshop.module.app.domain;

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
 * Title: QRLogin.java
 * </p>
 * 
 * <p>
 * Description:二维码登录管理类，系统支持app扫描二维码完成pc端自动登录机制
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
 * @date 2015-2-6
 * 
 * @version iskyshop_b2b2c 2015
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "qrlogin")
public class QRLogin extends IdEntity {
	@Column(columnDefinition = "LongText")
	private String qr_session_id;//二维码登录session Id
	@Column(columnDefinition = "LongText")
	private String user_id;//用户id

	public String getQr_session_id() {
		return qr_session_id;
	}

	public void setQr_session_id(String qr_session_id) {
		this.qr_session_id = qr_session_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
