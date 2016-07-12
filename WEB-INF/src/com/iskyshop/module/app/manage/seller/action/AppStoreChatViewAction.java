package com.iskyshop.module.app.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.module.chatting.domain.Chatting;
import com.iskyshop.module.chatting.domain.ChattingLog;
import com.iskyshop.module.chatting.service.IChattingLogService;
import com.iskyshop.module.chatting.service.IChattingService;

/**
 * 
 * <p>
 * Title: AppStoreChatViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商家聊天
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-4-14
 * 
 * @version 1.0
 */
@Controller
public class AppStoreChatViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IChattingService chattingService;
	@Autowired
	private IChattingLogService chattinglogService;

	/**
	 * 获取所有timestamp之后的用户未读聊天列表
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param timestamp
	 */
	@RequestMapping("/app/seller/chat_index.htm")
	public void chat_index(HttpServletRequest request,
			HttpServletResponse response, String user_id, String timestamp) {
		timestamp = timestamp.replace(",", " ");
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		json_map.put("username", user.getUsername());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.store_id=:store_id",
						params, -1, -1);
		List list = new ArrayList();
		json_map.put("ret", -100);
		json_map.put("timestamp", CommUtil.formatLongDate(new Date()));
		Date date = CommUtil.formatDate(timestamp);
		for (Chatting chatting : chattings) {

			params.clear();
			params.put("chatting_id", chatting.getId());
			params.put("store_read", 0);// 商家客服未读信息
			List<ChattingLog> logs;
			int unread_count = 0;
			if (timestamp != null && !timestamp.equals("")) {
				params.put("addTime", date);
				logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read  and DATE_FORMAT(obj.addTime,'%Y-%m-%d') >= DATE_FORMAT( :addTime,'%Y-%m-%d') order by addTime desc",
								params, 0, 1);
				List countlist = this.chattinglogService
						.query("select count(obj.id) from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read  and DATE_FORMAT(obj.addTime,'%Y-%m-%d') >= DATE_FORMAT( :addTime,'%Y-%m-%d') order by addTime desc",
								params, -1, -1);
				unread_count = CommUtil.null2Int(countlist.get(0));
			} else {
				logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read order by addTime desc",
								params, 0, 1);
				List countlist = this.chattinglogService
						.query("select count(obj.id) from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read order by addTime desc",
								params, 0, 1);
				unread_count = CommUtil.null2Int(countlist.get(0));
			}
			if (logs.size() > 0) {
				List chat_list = new ArrayList();
				json_map.put("ret", 100);
				Map map = new HashMap();
				map.put("user_id", chatting.getUser_id());
				map.put("user_name", chatting.getUser_name());
				User chatuser = this.userService.getObjById(CommUtil
						.null2Long(chatting.getUser_id()));
				String photo_url = CommUtil.getURL(request)
						+ "/"
						+ this.configService.getSysConfig().getMemberIcon()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getMemberIcon()
								.getName();
				if (chatuser.getPhoto() != null) {
					photo_url = CommUtil.getURL(request) + "/"
							+ chatuser.getPhoto().getPath() + "/"
							+ chatuser.getPhoto().getName();
				}
				map.put("user_img", photo_url);
				for (ChattingLog chattingLog : logs) {
					Map chatmap = new HashMap();
					chatmap.put("id", chattingLog.getId());
					chatmap.put("content", chattingLog.getContent());
					chatmap.put("addTime", chattingLog.getAddTime());
					chatmap.put("store_id", chattingLog.getStore_id());
					chatmap.put("user_id", chattingLog.getUser_id());
					chat_list.add(chatmap);
				}
				map.put("unread_count", unread_count);
				map.put("chat_list", chat_list);
				list.add(map);
			}
		}
		json_map.put("chatlist", list);

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 未读聊天记录查询
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param timestamp
	 */
	@RequestMapping("/app/seller/chat_log.htm")
	public void chat_log(HttpServletRequest request,
			HttpServletResponse response, String user_id, String timestamp,
			String select_count, String talkingto_user_id) {
		timestamp = timestamp.replace(",", " ");
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("user_id", CommUtil.null2Long(talkingto_user_id));
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.store_id=:store_id and obj.user_id=:user_id",
						params, -1, -1);
		json_map.put("ret", -100);
		if (chattings.size() > 0) {
			Chatting chatting = chattings.get(0);
			Date date = CommUtil.formatDate(timestamp);
			params.clear();
			params.put("chatting_id", chatting.getId());
			params.put("store_read", 0);// 商家客服未读信息
			List<ChattingLog> logs;

			if (timestamp != null && !timestamp.equals("")) {
				params.put("addTime", date);
				logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read  and DATE_FORMAT(obj.addTime,'%Y-%m-%d') <= DATE_FORMAT( :addTime,'%Y-%m-%d') order by id desc",
								params, 0, CommUtil.null2Int(select_count));
			} else {
				logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read order by id desc",
								params, 0, CommUtil.null2Int(select_count));
			}
			if (logs.size() > 0) {
				json_map.put("ret", 100);

				List chat_list = new ArrayList();
				for (ChattingLog chattingLog : logs) {
					Map chatmap = new HashMap();
					chatmap.put("id", chattingLog.getId());
					chatmap.put("content", chattingLog.getContent());
					chatmap.put("addTime", chattingLog.getAddTime());
					chatmap.put("store_id", chattingLog.getStore_id());
					chatmap.put("user_id", chattingLog.getUser_id());
					chat_list.add(chatmap);

					chattingLog.setStore_read(1);
					this.chattinglogService.update(chattingLog);
				}
				json_map.put("chat_list", chat_list);
			}

		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 聊天发送
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param talkingto_user_id
	 * @param chatting_content
	 */
	@RequestMapping("/app/seller/chat_send.htm")
	public void chat_send(HttpServletRequest request,
			HttpServletResponse response, String user_id,
			String talkingto_user_id, String chatting_content, String device) {
		if (device != null && device.equals("iOS")) {
			chatting_content = CommUtil.convert(chatting_content, "utf-8");
		}

		Map json_map = new HashMap();
		json_map.put("ret", -100);

		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("user_id", CommUtil.null2Long(talkingto_user_id));
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.store_id=:store_id and obj.user_id=:user_id",
						params, -1, -1);
		if (chattings.size() > 0) {
			Chatting chatting = chattings.get(0);
			ChattingLog log = new ChattingLog();
			log.setAddTime(new Date());
			log.setContent(chatting_content);
			log.setChatting(chatting);
			log.setStore_id(user.getId());
			log.setStore_read(1);
			if (this.chattinglogService.save(log)) {
				json_map.put("ret", 100);
				json_map.put("id", log.getId());
				json_map.put("content", log.getContent());
				json_map.put("addTime", log.getAddTime());
				json_map.put("store_id", log.getStore_id());
				json_map.put("user_id", log.getUser_id());
			}
		}

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 轮询新的未读消息
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param timestamp
	 * @param select_count
	 * @param talkingto_user_id
	 */
	@RequestMapping("/app/seller/chatting_logs_refresh.htm")
	public void chatting_logs_refresh(HttpServletRequest request,
			HttpServletResponse response, String user_id, String timestamp,
			String talkingto_user_id) {
		timestamp = timestamp.replace(",", " ");
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("user_id", CommUtil.null2Long(talkingto_user_id));
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.store_id=:store_id and obj.user_id=:user_id",
						params, -1, -1);
		json_map.put("ret", -100);
		if (chattings.size() > 0) {
			Chatting chatting = chattings.get(0);
			Date date = CommUtil.formatDate(timestamp);

			params.clear();
			params.put("chatting_id", chatting.getId());
			params.put("store_read", 0);// 商家客服未读信息
			List<ChattingLog> logs = new ArrayList<ChattingLog>();

			if (timestamp != null && !timestamp.equals("")) {
				params.put("addTime", date);
				logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read  and DATE_FORMAT(obj.addTime,'%Y-%m-%d') >= DATE_FORMAT( :addTime,'%Y-%m-%d') order by id asc",
								params, -1, -1);
			}
			if (logs.size() > 0) {
				json_map.put("ret", 100);

				List chat_list = new ArrayList();
				for (ChattingLog chattingLog : logs) {
					Map chatmap = new HashMap();
					chatmap.put("id", chattingLog.getId());
					chatmap.put("content", chattingLog.getContent());
					chatmap.put("addTime", chattingLog.getAddTime());
					chatmap.put("store_id", chattingLog.getStore_id());
					chatmap.put("user_id", chattingLog.getUser_id());
					chat_list.add(chatmap);

					chattingLog.setStore_read(1);
					this.chattinglogService.update(chattingLog);
				}
				json_map.put("chat_list", chat_list);
			}

		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	private void send_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
