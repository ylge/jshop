package com.iskyshop.module.circle.view.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.domain.CircleInvitation;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationService;

/**
 * 
 * <p>
 * Title: CircleViewTools.java
 * </p>
 * 
 * <p>
 * Description: 圈子前台显示工具类
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
 * @date 2014-12-3
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class CircleViewTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private IInvitationService invitationService;

	/**
	 * 判断当前用户是否关注了指定圈子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generUserAttention(String cid, String uid) {
		String ret = "false";
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		List<Map> map_list = new ArrayList<Map>();
		List<Map> temp_list = new ArrayList<Map>();
		if (user.getCircle_attention_info() != null
				&& !user.getCircle_attention_info().equals("")) {
			map_list = Json.fromJson(List.class,
					user.getCircle_attention_info());
			for (Map temp : map_list) {
				if (CommUtil.null2String(temp.get("id")).equals(cid)) {
					ret = "true";
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * 根据圈子id获取圈子图标
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Map genercircleImage(String cid) {
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(cid));
		if (obj != null && !CommUtil.null2String(obj.getPhotoInfo()).equals("")) {
			Map map = Json.fromJson(Map.class, obj.getPhotoInfo());
			return map;
		} else
			return new HashMap();
	}

	/**
	 * 根据圈子id获取圈子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Circle genercircleInfo(String cid) {
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(cid));
		return obj;
	}

	/**
	 * 根据圈子id获取用户图标
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generUserIcon(String uid) {
		String src = "";
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		SysConfig sc = this.configService.getSysConfig();
		src = sc.getMemberIcon().getPath() + "/" + sc.getMemberIcon().getName();
		if (user.getPhoto() != null) {
			src = user.getPhoto().getPath() + "/" + user.getPhoto().getName();
		}
		return src;
	}

	/**
	 * 根据帖子id和用户id获取当前用户是否点赞该帖子
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String generInvitationParise(String id, String uid) {
		String ret = "false";
		CircleInvitation invit = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		String temp = "," + uid + ",";
		if (invit.getPraiseInfo() != null && !invit.getPraiseInfo().equals("")) {
			if (invit.getPraiseInfo().indexOf(temp) >= 0) {
				ret = "true";
			}
		}
		return ret;
	}

	/**
	 * 清除帖子详情中的图片信息
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public String clearImages(String content) {
		content = content.replaceAll("<img.*/>", "");
		return content;
	}
}
