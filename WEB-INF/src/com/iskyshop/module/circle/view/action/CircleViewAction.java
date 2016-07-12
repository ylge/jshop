package com.iskyshop.module.circle.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
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
import com.iskyshop.module.circle.service.IInvitationService;
import com.iskyshop.module.circle.view.tools.CircleViewTools;

/**
 * 
 * <p>
 * Title: CircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子控制器，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
public class CircleViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private ICircleClassService circleclassService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private CircleViewTools circleViewTools;

	/**
	 * 圈子头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/head.htm")
	public ModelAndView circle_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("circle/circle_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 圈子头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/nav.htm")
	public ModelAndView circle_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("circle/circle_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map map = new HashMap();
		map.put("nav_index", 1);
		List<CircleClass> class_list = this.circleclassService
				.query("select obj from CircleClass obj where obj.nav_index=:nav_index order by sequence asc",
						map, 0, 11);
		mv.addObject("class_list", class_list);
		return mv;
	}

	/**
	 * 圈子首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/index.htm")
	public ModelAndView circle_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("circle/circle_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("recommend", 1);
		List<Circle> circle_recommend = this.circleService
				.query("select obj from Circle obj where obj.recommend=:recommend order by obj.addTime desc",
						params, 0, 8);
		params.clear();
		params.put("status", 5);
		List<Circle> circle_hot = this.circleService
				.query("select obj from Circle obj where obj.status=:status order by obj.attention_count desc",
						params, 0, 6);
		List<CircleInvitation> invitation_hot = this.invitationService
				.query("select obj from CircleInvitation obj  order by obj.reply_count desc",
						null, 0, 5);
		params.clear();
		params.put("recommend", 1);
		List<CircleClass> ccs = this.circleclassService
				.query("select obj from CircleClass obj where obj.recommend=:recommend ",
						params, -1, -1);
		if (ccs.size() > 0) {
			params.clear();
			params.put("status", 5);
			params.put("cid", ccs.get(0).getId());
			List<Circle> switch_first = this.circleService
					.query("select obj from Circle obj where obj.class_id =:cid and obj.status=:status ",
							params, 0, 9);
			mv.addObject("switch_first", switch_first);
		}
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = Json.fromJson(List.class,
						user.getCircle_attention_info());
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			params.clear();
			params.put("user_id", user.getId());
			List<CircleInvitation> invis = this.invitationService
					.query("select obj.id from CircleInvitation obj where obj.user_id=:user_id",
							params, -1, -1);
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("ccs", ccs);
		mv.addObject("invitation_hot", invitation_hot);
		mv.addObject("circle_atten_count", circle_atten_count);
		mv.addObject("invi_count", invi_count);
		mv.addObject("circle_hot", circle_hot);
		mv.addObject("circle_recommend", circle_recommend);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/index_switch.htm")
	public ModelAndView index_switch(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		ModelAndView mv = new JModelAndView("circle/circle_index_switch.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("status", 5);
		params.put("cid", CommUtil.null2Long(cid));
		List<Circle> objs = this.circleService
				.query("select obj from Circle obj where obj.class_id =:cid and obj.status=:status ",
						params, 0, 9);
		mv.addObject("objs", objs);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/list.htm")
	public ModelAndView circle_list(HttpServletRequest request,
			HttpServletResponse response, String class_id, String currentPage) {
		ModelAndView mv = new JModelAndView("circle/search_circle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CircleQueryObject qo = new CircleQueryObject(currentPage, mv,
				"addTime", "desc");
		if (class_id != null && !class_id.equals("")) {
			qo.addQuery("obj.class_id",
					new SysMap("class_id", CommUtil.null2Long(class_id)), "=");
			mv.addObject("class_id", class_id);
		}
		IPageList pList = this.circleService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("circleViewTools", circleViewTools);
		// 推荐圈子
		Map params = new HashMap();
		params.put("recommend", 1);
		params.put("status", 5);
		List<Circle> recommends = this.circleService
				.query("select obj from Circle obj where obj.recommend=:recommend and obj.status=:status",
						params, 0, 5);
		mv.addObject("recommends", recommends);
		mv.addObject("circleViewTools", circleViewTools);
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = Json.fromJson(List.class,
						user.getCircle_attention_info());
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			params.clear();
			params.put("user_id", user.getId());
			List<CircleInvitation> invis = this.invitationService
					.query("select obj.id from CircleInvitation obj where obj.user_id=:user_id",
							params, -1, -1);
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("circle_atten_count", circle_atten_count);
		mv.addObject("invi_count", invi_count);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/detail.htm")
	public ModelAndView circle_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("circle/circle_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getStatus() == 5) {
				Map img_map = Json.fromJson(Map.class, obj.getPhotoInfo());
				User owner = this.userService.getObjById(obj.getUser_id());
				Map params = new HashMap();
				params.put("status", 5);
				params.put("recommend", 1);
				List<Circle> recommends = this.circleService
						.query("select obj from Circle obj where obj.status=:status and obj.recommend=:recommend",
								params, 0, 8);
				if (recommends.size() == 0) {
					params.clear();
					params.put("status", 5);
					recommends = this.circleService
							.query("select obj from Circle obj where obj.status=:status order by obj.attention_count desc",
									params, 0, 8);
				}
				mv.addObject("recommends", recommends);
				mv.addObject("owner", owner);
				mv.addObject("obj", obj);
				mv.addObject("circleViewTools", circleViewTools);
			} else if (obj.getStatus() == 0) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index.htm");
				mv.addObject("op_title", "该圈子未经过审核，暂不可查看");
			} else if (obj.getStatus() == -1) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index.htm");
				mv.addObject("op_title", "该圈子违反平台相关规定，现已下线");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", "您所访问的地址不存在");
		}
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/circle_invitation_list.htm")
	public ModelAndView circle_invitation_list(HttpServletRequest request,
			HttpServletResponse response, String cid, String currentPage,
			String type) {
		ModelAndView mv = new JModelAndView(
				"circle/circle_invitation_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Circle cir = this.circleService.getObjById(CommUtil.null2Long(cid));

		InvitationQueryObject qo = new InvitationQueryObject(currentPage, mv,
				"", "");
		qo.addQuery("obj.circle_id",
				new SysMap("circle_id", CommUtil.null2Long(cid)), "=");
		if (type != null && !type.equals("") && type.equals("jing")) {
			qo.addQuery("obj.invitaion_perfect", new SysMap(
					"invitaion_perfect", 1), "=");
			mv.addObject("type", type);
		}
		if (type != null && !type.equals("") && type.equals("all")) {
			qo.addQuery("obj.invitaion_top", new SysMap("invitaion_top", 1),
					"!=");
			mv.addObject("type", type);
		}
		qo.setOrderBy("invitaion_perfect desc,obj.addTime");
		qo.setOrderType("desc");
		qo.setPageSize(20);
		IPageList pList = this.invitationService.list(qo);
		String url = CommUtil.getURL(request)
				+ "/circle/circle_invitation_list.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));

		if (type != null && !type.equals("") && !type.equals("jing")
				&& pList.getCurrentPage() <= 1) {
			Map params = new HashMap();
			params.put("circle_id", cir.getId());
			List<CircleInvitation> top = this.invitationService
					.query("select obj from CircleInvitation obj where obj.invitaion_top=1 and obj.circle_id =:circle_id",
							params, -1, -1);
			if (top.size() > 0) {
				mv.addObject("top", top.get(0));
			}
		}
		return mv;
	}

	/**
	 * 关注圈子请求
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "用户圈子关注", value = "/circle/pay_attention.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_circle", rgroup = "圈子访问")
	@RequestMapping("/circle/pay_attention.htm")
	public void pay_attention(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		int code = 100;// 100关注成功，-100，关注失败，已经关注该圈子
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(cid));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = new HashMap();
		List<Map> map_list = new ArrayList<Map>();
		if (user.getCircle_attention_info() != null
				&& !user.getCircle_attention_info().equals("")) {
			map_list = Json.fromJson(List.class,
					user.getCircle_attention_info());
			for (Map temp : map_list) {
				if (CommUtil.null2String(temp.get("id")).equals(cid)) {
					code = -100;// 已经关注该圈子
					break;
				}
			}
		}
		if (code == 100) {
			map.put("id", obj.getId());
			map.put("name", obj.getTitle());
			map_list.add(map);
			String temp_json = Json.toJson(map_list, JsonFormat.compact());
			user.setCircle_attention_info(temp_json);
			this.userService.update(user);
			obj.setAttention_count(obj.getAttention_count() + 1);
			this.circleService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 取消关注圈子请求
	 * 
	 * @param request
	 * @param response
	 */

	@SecurityMapping(title = "用户取消圈子关注", value = "/circle/cancle_attention.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_circle", rgroup = "圈子访问")
	@RequestMapping("/circle/cancle_attention.htm")
	public void cancle_attention(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		int code = 100;// 100取消成功，-100取消失败，没有关注该圈子
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Circle obj = this.circleService.getObjById(CommUtil.null2Long(cid));
		Map map = new HashMap();
		List<Map> map_list = new ArrayList<Map>();
		List<Map> temp_list = new ArrayList<Map>();
		if (user.getCircle_attention_info() != null
				&& !user.getCircle_attention_info().equals("")) {
			map_list = Json.fromJson(List.class,
					user.getCircle_attention_info());
			for (Map temp : map_list) {
				if (!CommUtil.null2String(temp.get("id")).equals(cid)) {
					temp_list.add(temp);
					code = -100;
				} else {
					code = 100;
				}
			}
			if (temp_list.size() > 0) {
				String temp_json = Json.toJson(temp_list, JsonFormat.compact());
				user.setCircle_attention_info(temp_json);
			} else {
				user.setCircle_attention_info(null);
			}
			this.userService.update(user);
			obj.setAttention_count(obj.getAttention_count() - 1);
			this.circleService.update(obj);
		} else {
			code = -100;
		}
		System.out.println("code：" + code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/circle/search_list.htm")
	public ModelAndView search_list(HttpServletRequest request,
			HttpServletResponse response, String keyword, String type,
			String currentPage) {
		ModelAndView mv = new JModelAndView("circle/search_circle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (type == null || type.equals("")) {
			type = "circle";
		}
		if (type.equals("circle")) {
			if (!keyword.equals("")) {
				CircleQueryObject qo = new CircleQueryObject(currentPage, mv,
						"addTime", "desc");
				qo.addQuery("obj.status", new SysMap("status", 5), "=");
				qo.setPageSize(15);
				if (keyword != null && !keyword.equals("")) {
					qo.addQuery(
							"obj.title",
							new SysMap("title", "%"
									+ CommUtil.null2String(keyword) + "%"),
							"like");
				}
				IPageList pList = this.circleService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			}
			// 推荐圈子
			Map params = new HashMap();
			params.put("recommend", 1);
			params.put("status", 5);
			List<Circle> recommends = this.circleService
					.query("select obj from Circle obj where obj.recommend=:recommend and obj.status=:status",
							params, 0, 5);
			mv.addObject("recommends", recommends);
		} else if (type.equals("invitation")) {
			mv = new JModelAndView("circle/search_invitation.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if (!keyword.equals("")) {
				InvitationQueryObject qo = new InvitationQueryObject(
						currentPage, mv, "addTime", "desc");
				qo.setPageSize(15);
				if (keyword != null && !keyword.equals("")) {
					qo.addQuery(
							"obj.title",
							new SysMap("title", "%"
									+ CommUtil.null2String(keyword) + "%"),
							"like");
				}
				IPageList pList = this.invitationService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			}
			// 热门讨论帖子
			Map params = new HashMap();
			List<CircleInvitation> hots = this.invitationService
					.query("select obj from CircleInvitation obj order by obj.reply_count desc",
							params, 0, 10);
			mv.addObject("hots", hots);
		}
		Map params = new HashMap();
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = Json.fromJson(List.class,
						user.getCircle_attention_info());
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			params.clear();
			params.put("user_id", user.getId());
			List<CircleInvitation> invis = this.invitationService
					.query("select obj.id from CircleInvitation obj where obj.user_id=:user_id",
							params, -1, -1);
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("circle_atten_count", circle_atten_count);
		mv.addObject("invi_count", invi_count);
		mv.addObject("type", type);
		mv.addObject("keyword", keyword);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	/**
	 * 圈子错误导向（例如系统未开启圈子功能时的导向）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/error.htm")
	public ModelAndView circle_error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("circle/error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "系统未开启圈子功能");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}
}