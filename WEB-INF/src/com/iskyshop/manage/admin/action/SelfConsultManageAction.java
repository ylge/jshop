package com.iskyshop.manage.admin.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
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
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: ConsultSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营咨询管理
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
 * @date 2014-10-29
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SelfConsultManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private MsgTools msgTools;

	@SecurityMapping(title = "自营商品咨询列表", value = "/admin/consult_self.htm*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping("/admin/consult_self.htm")
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage,
			String consult_user_userName) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/consult_self_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		if (!CommUtil.null2String(reply).equals("")) {
			qo.addQuery("obj.reply",
					new SysMap("reply", CommUtil.null2Boolean(reply)), "=");
		}
		if (consult_user_userName != null && !consult_user_userName.equals("")) {
			qo.addQuery(
					"obj.consult_user_name",
					new SysMap("consult_user_name", CommUtil.null2String(
							consult_user_userName).trim()), "=");
		}
		qo.addQuery("obj.whether_self", new SysMap("whether_self", 1), "=");
		qo.setPageSize(2);
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("consult_user_userName", consult_user_userName);
		return mv;
	}

	@SecurityMapping(title = "自营商品咨询回复", value = "/admin/consult_self_reply.htm*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping("/admin/consult_self_reply.htm")
	public ModelAndView consult_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/consult_self_reply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "自营商品咨询回复保存", value = "/admin/consult_reply_self_save.htm*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping("/admin/consult_reply_self_save.htm")
	public String consult_reply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String consult_reply,
			String currentPage) throws Exception {
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		obj.setConsult_reply(consult_reply);
		obj.setReply_time(new Date());
		obj.setReply_user_id(user.getId());
		obj.setReply_user_name("平台运营商");
		obj.setReply(true);
		this.consultService.update(obj);
		if (this.configService.getSysConfig().isEmailEnable()
				&& obj.getConsult_user_id() != null) {
			Map map = new HashMap();
			map.put("buyer_id", CommUtil.null2String(obj.getConsult_user_id()));
			List<Map> maps = CommUtil.Json2List(obj.getGoods_info());
			for (Map m : maps) {
				map.put("goods_id", m.get("goods_id").toString());
			}
			String json = Json.toJson(map);
			this.msgTools.sendEmailFree(CommUtil.getURL(request),
					"email_tobuyer_cousult_reply_notify", this.userService
							.getObjById(obj.getConsult_user_id()).getEmail(),
					json, null);
		}
		return "redirect:consult_self.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "自营商品咨询删除", value = "/admin/consult_self_del.htm*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping("/admin/consult_self_del.htm")
	public String consult_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String consult_reply,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.consultService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:consult_self.htm?currentPage=" + currentPage;
	}
}
