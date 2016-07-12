package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.MessageQueryObject;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MessageBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户中心站内短信控制器
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
 * @date 2014-10-15
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class MessageBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "站内短信", value = "/buyer/message.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message.htm")
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/message.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		MessageQueryObject qo = new MessageQueryObject();
		if (CommUtil.null2String(type).equals("sys")
				|| CommUtil.null2String(type).equals("my")
				|| CommUtil.null2String(type).equals("")) {
			if (CommUtil.null2String(type).equals("")) {// 默认是我收到的其他用户发送的短信
				qo.addQuery("obj.toUser.id", new SysMap("user_id",
						SecurityUserHolder.getCurrentUser().getId()), "=");
				qo.addQuery("obj.type", new SysMap("type", 1), "=");
			}
			if (CommUtil.null2String(type).equals("my")) {// my表示我发送出去的短信
				qo.addQuery("obj.fromUser.id", new SysMap("user_id",
						SecurityUserHolder.getCurrentUser().getId()), "=");
			}
			if (CommUtil.null2String(type).equals("sys")) {// sys表示我收的到的系统站内信
				qo.addQuery("obj.toUser.id", new SysMap("user_id",
						SecurityUserHolder.getCurrentUser().getId()), "=");
				qo.addQuery("obj.type", new SysMap("type", 0), "=");
			}
			qo.addQuery("obj.parent.id is null", null);
			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			qo.setCurrentPage(CommUtil.null2Int(currentPage));
			IPageList pList = this.messageService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			this.cal_msg_info(mv);
			if (!CommUtil.null2String(type).equals("my")
					&& !CommUtil.null2String(type).equals("sys")) {
				type = "";
			}
			mv.addObject("type", type);
		}else{
			mv = new JModelAndView(
					"error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request)+"/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "站内短信删除", value = "/buyer/message_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message_del.htm")
	public String message_del(HttpServletRequest request,
			HttpServletResponse response, String type, String mulitId) {
		String[] ids = mulitId.split(",");
		User user = SecurityUserHolder.getCurrentUser();
		for (String id : ids) {
			if (!id.equals("")) {
				Message msg = this.messageService.getObjById(CommUtil
						.null2Long(id));
				if ((msg.getFromUser() != null && msg.getFromUser().getId()
						.equals(user.getId()))
						|| (msg.getToUser() != null && msg.getToUser().getId()
								.equals(user.getId()))) {// 只允许删除自己发送的和收到的站内短信
					this.messageService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:message.htm?type=" + type;
	}

	@SecurityMapping(title = "站内短信查看", value = "/buyer/message_info.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message_info.htm")
	public ModelAndView message_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/message_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Message obj = this.messageService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getToUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				obj.setStatus(1);// 标注短信已读
				this.messageService.update(obj);
			}
			if (obj.getFromUser() != null
					&& obj.getFromUser()
							.getId()
							.equals(SecurityUserHolder.getCurrentUser().getId())) {
				obj.setReply_status(0);// 标注短信回复已读
				this.messageService.update(obj);
			}
			MessageQueryObject qo = new MessageQueryObject();
			qo.setCurrentPage(CommUtil.null2Int(currentPage));
			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			qo.addQuery("obj.parent.id", new SysMap("parent_id", obj.getId()),
					"=");
			IPageList pList = this.messageService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("obj", obj);
			this.cal_msg_info(mv);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，站内信查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/message.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "站内短信发送", value = "/buyer/message_send.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message_send.htm")
	public ModelAndView message_send(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/message_send.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		this.cal_msg_info(mv);
		if (userName != null && !userName.equals("")) {
			mv.addObject("userName", userName);
		}
		return mv;
	}

	@SecurityMapping(title = "站内短信保存", value = "/buyer/message_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message_save.htm")
	public void message_save(HttpServletRequest request,
			HttpServletResponse response, String users, String content) {
		String[] userNames = users.split(",");
		for (String userName : userNames) {
			User toUser = this.userService.getObjByProperty(null, "userName",
					userName);
			if (toUser != null) {
				Message msg = new Message();
				msg.setAddTime(new Date());
				Whitelist whiteList = new Whitelist();
				content = content.replaceAll("\n", "iskyshop_br");
				msg.setContent(Jsoup.clean(content, whiteList.basic())
						.replaceAll("iskyshop_br", "\n"));
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(toUser);
				msg.setType(1);
				this.messageService.save(msg);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "站内短信回复", value = "/buyer/message_reply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/message_reply.htm")
	public void message_reply(HttpServletRequest request,
			HttpServletResponse response, String pid,
			String content) {
		Message parent = this.messageService.getObjById(Long.parseLong(pid));
		boolean ret = false;
		if (!parent.getFromUser().getUserRole().equalsIgnoreCase("admin")) {
			Message reply = new Message();
			reply.setAddTime(new Date());
			reply.setContent(content);
			reply.setFromUser(SecurityUserHolder.getCurrentUser());
			reply.setToUser(parent.getFromUser());
			reply.setType(1);
			reply.setParent(parent);
			reply.setMsg_cat(1);// 设置该信息为回复信息
			this.messageService.save(reply);
			if (!parent.getFromUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				parent.setReply_status(1);
			}
			ret = this.messageService.update(parent);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "发送信息用户验证", value = "/buyer/message_validate_user.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/message_validate_user.htm")
	public void message_validate_user(HttpServletRequest request,
			HttpServletResponse response, String users) {
		String[] userNames = users.replaceAll("，", ",").trim().split(",");
		boolean ret = true;
		for (String userName : userNames) {
			if (!userName.trim().equals("")) {
				User user = this.userService.getObjByProperty(null, "userName",
						userName.trim());
				if (user == null) {
					ret = false;
				} else {
					if (user.getUserRole().equalsIgnoreCase("admin")) {
						ret = false;
					}
				}
			}
			if (ret
					&& userName.equals(SecurityUserHolder.getCurrentUser()
							.getUserName())) {
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cal_msg_info(ModelAndView mv) {
		Map params = new HashMap();
		params.put("status", 0);
		params.put("type", 1);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		// 查询当前用户收到的未读站内信
		List<Message> user_msgs = this.messageService
				.query("select obj from Message obj where obj.status=:status and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
						params, -1, -1);
		// 查询系统发送给当前用户且未读的站内信
		params.clear();
		params.put("status", 0);
		params.put("type", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Message> sys_msgs = this.messageService
				.query("select obj from Message obj where obj.status=:status and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
						params, -1, -1);
		// 查询当前用户发送且有新回复的站内信
		params.clear();
		params.put("msg_cat", 0);
		params.put("reply_status", 1);
		params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Message> replys = this.messageService
				.query("select obj from Message obj where obj.msg_cat=:msg_cat and obj.fromUser.id=:from_user_id and obj.reply_status=:reply_status",
						params, -1, -1);
		mv.addObject("user_msgs", user_msgs);
		mv.addObject("sys_msgs", sys_msgs);
		mv.addObject("reply_msgs", replys);
	}
}
