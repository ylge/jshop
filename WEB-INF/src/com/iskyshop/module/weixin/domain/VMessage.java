package com.iskyshop.module.weixin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: VMessage.java
 * </p>
 * 
 * <p>
 * Description: 微信信息管理类，系统可以接受用户发送的微信信息，并进行回复
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
 * @date 2014-12-20
 * 
 * @version iskyshop_b2b2c v2.0 2015版 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "iskyshop_vmessage")
public class VMessage extends IdEntity {
	private String FromUserName;// 消息发送方姓名
	@Column(columnDefinition = "LongText")
	private String content;// 微信信息内容
	@Column(columnDefinition = "LongText")
	private String reply;// 回复信息内容
	private String MsgType;// 微信消息类型
	@Column(columnDefinition = "int default 0")
	private int status;// 消息状态，0为未回复，1为已回复

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
