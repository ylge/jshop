package com.iskyshop.module.circle.manage.admin.action;

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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.Navigation;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.INavigationService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.domain.CircleClass;
import com.iskyshop.module.circle.domain.CircleInvitation;
import com.iskyshop.module.circle.domain.query.CircleQueryObject;
import com.iskyshop.module.circle.domain.query.InvitationQueryObject;
import com.iskyshop.module.circle.service.ICircleClassService;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationReplyService;
import com.iskyshop.module.circle.service.IInvitationService;

/**
 * 
 * <p>
 * Title: CircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子管理类，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CircleManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private ICircleClassService circleclassService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IInvitationReplyService invitationReplyService;
	@Autowired
	private INavigationService navigationService;

	/**
	 * 圈子设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子设置", value = "/admin/circle_set.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_set.htm")
	public ModelAndView circle_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/circle_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 圈子设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子设置", value = "/admin/circle_set_save.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_set_save.htm")
	public ModelAndView circle_set_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (sysConfig.getCircle_open() == 1) {
			Map params = new HashMap();
			params.put("url", "circle/index.htm");
			List<Navigation> navs = this.navigationService.query(
					"select obj from Navigation obj where obj.url=:url",
					params, -1, -1);
			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(10);
				nav.setSysNav(true);
				nav.setTitle("圈子");
				nav.setType("diy");
				nav.setUrl("circle/index.htm");
				nav.setOriginal_url("circle/index.htm");
				this.navigationService.save(nav);
			}
		}else{
			Map params = new HashMap();
			params.put("url", "circle/index.htm");
			List<Navigation> navs = this.navigationService.query(
					"select obj from Navigation obj where obj.url=:url",
					params, -1, -1);
			if (navs.size() == 0) {
				navs.get(0).setDisplay(false);
				this.navigationService.save(navs.get(0));
			}
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		mv.addObject("op_title", "圈子设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/circle_set.htm");
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子列表", value = "/admin/circle_list.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_list.htm")
	public ModelAndView circle_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status,
			String class_id, String title, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/circle_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CircleQueryObject qo = new CircleQueryObject(currentPage, mv,
				"addTime", "desc");
		if (title != null && !title.equals("")) {
			qo.addQuery(
					"obj.title",
					new SysMap("title", "%" + CommUtil.null2String(title) + "%"),
					"like");
			mv.addObject("title", title);
		}
		if (class_id != null && !class_id.equals("")) {
			qo.addQuery("obj.class_id",
					new SysMap("class_id", CommUtil.null2Long(class_id)), "=");
			mv.addObject("class_id", class_id);
		}
		if (status != null && !status.equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(status)), "=");
			mv.addObject("status", status);
		} else {
			qo.addQuery("obj.status", new SysMap("status", 0), "!=");
		}
		if (userName != null && !userName.equals("")) {
			qo.addQuery("obj.userName",
					new SysMap("userName", CommUtil.null2String(userName)), "=");
			mv.addObject("userName", userName);
		}
		mv.addObject("status", status);
		IPageList pList = this.circleService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		List<CircleClass> ccs = this.circleclassService.query(
				"select obj from CircleClass obj  order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("ccs", ccs);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子编辑", value = "/admin/circle_edit.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_edit.htm")
	public ModelAndView circle_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/circle_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * circleclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "圈子保存", value = "/admin/circle_save.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_save.htm")
	public ModelAndView circle_save(HttpServletRequest request,
			HttpServletResponse response, String id, String status,
			String refuseMsg) {
		Circle obj = this.circleService.getObjById(Long.parseLong(id));
		obj.setStatus(CommUtil.null2Int(status));
		this.circleService.update(obj);
		User fromuser = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		User touser = this.userService.getObjById(obj.getUser_id());
		if (status.equals("5")) {
			// 向圈主发站内信提示
			String content = "您申请的圈子“" + obj.getTitle() + "”已经审核通过，感谢您对"
					+ this.configService.getSysConfig().getTitle() + "的支持！";
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromuser);
			msg.setToUser(touser);
			msg.setType(0);
			this.messageService.save(msg);
		}
		if (status.equals("-1")) {
			// 向圈主发站内信提示
			String content = "您申请的圈子“" + obj.getTitle() + "”审核没有通过，拒绝理由：“"
					+ refuseMsg + "”！";
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromuser);
			msg.setToUser(touser);
			msg.setType(0);
			this.messageService.save(msg);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", "/admin/circle_list.htm");
		mv.addObject("op_title", "圈子审核成功");
		return mv;
	}

	@SecurityMapping(title = "圈子删除", value = "/admin/circle_del.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_del.htm")
	public String circle_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User fromuser = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		for (String id : ids) {
			if (!id.equals("")) {
				Circle circle = this.circleService.getObjById(Long
						.parseLong(id));
				if (circle != null) {
					// 更新圈主信息
					User touser = this.userService.getObjById(circle
							.getUser_id());
					List<Map> maps = Json.fromJson(List.class,
							touser.getCircle_create_info());
					for (Map map : maps) {
						if (CommUtil.null2String(map.get("id")).equals(id)) {
							maps.remove(map);
							break;
						}
					}
					touser.setCircle_create_info(Json.toJson(maps,
							JsonFormat.compact()));
					this.userService.update(touser);
					// 删除圈子中的所有帖子
					Map params = new HashMap();
					params.put("circle_id", circle.getId());
					List<CircleInvitation> objs = this.invitationService
							.query("select obj from CircleInvitation obj where obj.circle_id=:circle_id",
									params, -1, -1);
					for (CircleInvitation obj : objs) {
						this.invitationService.delete(obj.getId());
					}
					// 向圈主发站内信提示
					String content = "由于您创建的圈子“" + circle.getTitle()
							+ "”违反平台相关规定，管理员已经将其删除，如有疑问请联系平台管理员！";
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					msg.setFromUser(fromuser);
					msg.setTitle("您创建的圈子已经被删除");
					msg.setToUser(touser);
					msg.setType(0);
					this.messageService.save(msg);
				}
				this.circleService.delete(Long.parseLong(id));
			}
		}
		return "redirect:circle_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "圈子删除", value = "/admin/circle_ajax.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_ajax.htm")
	public void circle_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Circle obj = this.circleService.getObjById(Long.parseLong(id));
		if (fieldName.equals("recommend")) {
			if (obj.getRecommend() == 1) {
				obj.setRecommend(0);
			} else {
				obj.setRecommend(1);
			}
		}
		this.circleService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.getRecommend());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "帖子列表", value = "/admin/circle_invitation.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_invitation.htm")
	public ModelAndView circle_invitation(HttpServletRequest request,
			HttpServletResponse response, String cid, String user_name,
			String title) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/circle_invitation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InvitationQueryObject qo = new InvitationQueryObject(null, mv,
				"addTime", "desc");
		qo.addQuery("obj.circle_id",
				new SysMap("circle_id", CommUtil.null2Long(cid)), "=");
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery(
					"obj.userName",
					new SysMap("userName", "%"
							+ CommUtil.null2String(user_name) + "%"), "like");
			mv.addObject("user_name", user_name);
		}
		if (title != null && !title.equals("")) {
			qo.addQuery(
					"obj.title",
					new SysMap("title", "%" + CommUtil.null2String(title) + "%"),
					"like");
			mv.addObject("title", title);
		}
		IPageList pList = this.invitationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("cid", cid);
		return mv;
	}

	@SecurityMapping(title = "帖子删除", value = "/admin/circle_invitation_delete.htm*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping("/admin/circle_invitation_delete.htm")
	public String circle_invitation_delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String cid) {
		String[] ids = mulitId.split(",");
		List temp_ids = new ArrayList();
		for (String id : ids) {
			if (!id.equals("")) {
				Map params = new HashMap();
				params.put("id", CommUtil.null2Long(id));
				List reply_ids = this.invitationReplyService
						.query("select obj.id from CircleInvitationReply obj where obj.invitation_id=:id",
								params, -1, -1);
				List dele_ids = new ArrayList();
				for (Object temp_id : reply_ids) {
					dele_ids.add(CommUtil.null2Long(temp_id));
				}
				this.invitationReplyService.batchDelete(dele_ids);
				temp_ids.add(CommUtil.null2Long(id));
			}
		}
		this.invitationService.batchDelete(temp_ids);
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(cid));
		obj.setInvitation_count(obj.getInvitation_count() - temp_ids.size());
		this.circleService.update(obj);
		return "redirect:circle_invitation.htm?currentPage=" + currentPage
				+ "&cid=" + cid;
	}
}