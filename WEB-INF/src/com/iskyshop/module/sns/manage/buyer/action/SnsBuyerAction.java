package com.iskyshop.module.sns.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.domain.CircleInvitation;
import com.iskyshop.module.circle.domain.query.InvitationQueryObject;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationReplyService;
import com.iskyshop.module.circle.service.IInvitationService;
import com.iskyshop.module.circle.view.tools.CircleViewTools;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.domain.UserDynamic;
import com.iskyshop.module.sns.domain.UserShare;
import com.iskyshop.module.sns.domain.query.SnsAttentionQueryObject;
import com.iskyshop.module.sns.domain.query.UserShareQueryObject;
import com.iskyshop.module.sns.service.ISnsAttentionService;
import com.iskyshop.module.sns.service.IUserDynamicService;
import com.iskyshop.module.sns.service.IUserShareService;
import com.iskyshop.module.sns.view.tools.SnsFreeTools;
import com.iskyshop.module.sns.view.tools.SnsTools;
import com.iskyshop.view.web.tools.EvaluateViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: SnsBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户SNS功能控制器
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
 * @date 2014-11-21
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SnsBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISnsAttentionService snsAttentionService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private EvaluateViewTools evaluateViewTools;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private SnsFreeTools freeTools;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private SnsTools snsTools;
	@Autowired
	private CircleViewTools circleViewTools;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IUserShareService userShareService;
	@Autowired
	private IUserDynamicService dynamicService;
	@Autowired
	private IInvitationReplyService invitationReplyService;

	/**
	 * 用户查看自己的个人主页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns首页", value = "/buyer/my_sns_index.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_index.htm")
	public ModelAndView my_sns_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		// 加载关注人信息
		Map params = new HashMap();
		params.put("fromUser", CommUtil.null2Long(user.getId()));
		List<SnsAttention> tempSnss = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser order by obj.addTime desc",
						params, 0, 10);
		List<Map<String, String>> userAttsList = new ArrayList<Map<String, String>>();
		for (SnsAttention sns : tempSnss) {
			Map map = new HashMap<String, String>();
			map.put("user_id", sns.getToUser().getId());
			map.put("user_name", sns.getToUser().getUserName());
			map.put("sns_time", sns.getAddTime());
			if (sns.getToUser().getPhoto() != null) {
				map.put("user_photo", sns.getToUser().getPhoto().getPath()
						+ "/" + sns.getToUser().getPhoto().getName());
			}
			userAttsList.add(map);
		}
		mv.addObject("userAttsList", userAttsList);
		// 加载粉丝信息
		params.clear();
		params.put("toUser", CommUtil.null2Long(user.getId()));
		tempSnss = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.toUser.id=:toUser order by obj.addTime desc",
						params, 0, 10);
		List<Map<String, String>> userFansList = new ArrayList<Map<String, String>>();
		for (SnsAttention sns : tempSnss) {
			Map map = new HashMap<String, String>();
			map.put("user_id", sns.getFromUser().getId());
			map.put("user_name", sns.getFromUser().getUserName());
			map.put("sns_time", sns.getAddTime());
			if (sns.getFromUser().getPhoto() != null) {
				map.put("user_photo", sns.getFromUser().getPhoto().getPath()
						+ "/" + sns.getFromUser().getPhoto().getName());
			}
			userFansList.add(map);
		}
		mv.addObject("userFansList", userFansList);
		// 加载分享商品
		mv.addObject("userShare", snsTools.querylastUserShare(user.getId()));
		// 加载收藏信息
		mv.addObject("fav", snsTools.queryLastUserFav(user.getId()));
		// 加载评价与晒单
		params.clear();
		params.put("user_id", user.getId());
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is null or obj.evaluate_photos='') order by obj.addTime desc",
						params, 0, 2);
		mv.addObject("evas", evas);
		params.put("user_id", user.getId());
		List<Evaluate> evaPhotos = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is not null and obj.evaluate_photos!='') order by obj.addTime desc",
						params, 0, 2);
		mv.addObject("evaPhotos", evaPhotos);
		// 我的圈子和帖子信息
		params.clear();
		params.put("user_id", user.getId());
		List<CircleInvitation> invitations = this.invitationService
				.query("select obj from CircleInvitation obj where obj.user_id=:user_id order by addTime desc",
						params, 0, 1);
		if (invitations.size() > 0) {
			mv.addObject("invi", invitations.get(0));
		}
		if (user.getCircle_attention_info() != null
				&& !user.getCircle_attention_info().equals("")) {
			Set<Long> ids = new HashSet<Long>();
			List<Map> maps = Json.fromJson(List.class,
					user.getCircle_attention_info());
			for (Map map : maps) {
				ids.add(CommUtil.null2Long(map.get("id")));
			}
			if (!ids.isEmpty()) {
				params.clear();
				params.put("ids", ids);
				List<Circle> circles = this.circleService.query(
						"select obj from Circle obj where obj.id in(:ids)",
						params, 0, 3);
				mv.addObject("circles", circles);
				mv.addObject("circleViewTools", circleViewTools);
			}
		}
		// 加载动态信息
		params.clear();
		params.put("user_id", user.getId());
		List<UserDynamic> userDynamics = this.dynamicService
				.query("select obj from UserDynamic obj where obj.user_id =:user_id order by obj.addTime desc",
						params, 0, 1);
		if (userDynamics.size() > 0) {
			mv.addObject("userDynamics", userDynamics.get(0));
		}
		mv.addObject("snsTools", snsTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns头部", value = "/buyer/my_sns_head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_head.htm")
	public ModelAndView my_sns_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		int attsCount = this.snsTools.queryAtts(user.getId().toString());
		int fansCount = this.snsTools.queryFans(user.getId().toString());
		int favsCount = this.snsTools.queryfavCount(user.getId().toString());
		mv.addObject("attsCount", attsCount);
		mv.addObject("fansCount", fansCount);
		mv.addObject("favsCount", favsCount);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "买家sns导航", value = "/buyer/my_sns_head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_nav.htm")
	public ModelAndView my_sns_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		return mv;
	}

	@SecurityMapping(title = "买家sns开启访问权限", value = "/buyer/sns_lock_on.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_lock_on.htm")
	public void sns_lock_on(HttpServletRequest request,
			HttpServletResponse response) {
		int ret = 1;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user.setWhether_attention(0);
			this.userService.update(user);
			ret = 0;
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

	@SecurityMapping(title = "买家sns关闭访问权限", value = "/buyer/sns_lock_off.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_lock_off.htm")
	public void sns_lock_off(HttpServletRequest request,
			HttpServletResponse response) {
		int ret = 1;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user.setWhether_attention(1);
			this.userService.update(user);
			ret = 0;
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

	@SecurityMapping(title = "买家sns分享列表", value = "/buyer/my_sns_share.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_share.htm")
	public ModelAndView my_sns_share(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_share.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		UserShareQueryObject qo = new UserShareQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.setPageSize(15);
		IPageList pList = this.userShareService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_share.htm",
				"", param, pList, mv);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "买家sns分享删除", value = "/buyer/my_sns_share_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_share_del.htm")
	public void my_sns_share_del(HttpServletRequest request,
			HttpServletResponse response, String share_id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		boolean ret = false;
		UserShare userShare = this.userShareService.getObjById(CommUtil
				.null2Long(share_id));
		if (userShare.getUser_id().equals(user.getId())) {
			ret = this.userShareService.delete(userShare.getId());
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

	@SecurityMapping(title = "买家sns收藏", value = "/buyer/my_sns_fav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_fav.htm")
	public ModelAndView my_sns_fav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_fav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Favorite> favorites = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id = :user_id and obj.type=0 order by obj.addTime desc",
						params, 0, 10);
		mv.addObject("favorites", favorites);
		return mv;
	}

	@SecurityMapping(title = "买家sns收藏ajax", value = "/buyer/sns_ajax_favs.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_ajax_favs.htm")
	public ModelAndView sns_ajax_favs(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/sns_ajax_favs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Favorite> favorites = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id = :user_id and obj.type=0 order by obj.addTime desc",
						params, begin, end);
		mv.addObject("favorites", favorites);
		return mv;
	}

	@SecurityMapping(title = "买家sns评价", value = "/buyer/my_sns_evas.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_evas.htm")
	public ModelAndView my_sns_evas(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_evas.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is null or obj.evaluate_photos='') order by obj.addTime desc",
						params, 0, 10);
		mv.addObject("evas", evas);
		return mv;
	}

	@SecurityMapping(title = "买家sns评价ajax", value = "/buyer/sns_ajax_evas.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_ajax_evas.htm")
	public ModelAndView sns_ajax_evas(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/sns_ajax_evas.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and (obj.evaluate_photos is null or obj.evaluate_photos='') order by obj.addTime desc",
						params, begin, end);
		mv.addObject("evas", evas);
		return mv;
	}

	/**
	 * 晒单
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns晒单", value = "/buyer/my_sns_evaps.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_evaps.htm")
	public ModelAndView my_sns_evaps(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_evaps.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is not null and obj.evaluate_photos!='') order by obj.addTime desc",
						params, 0, 10);
		mv.addObject("evas", evas);
		mv.addObject("evaluateViewTools", evaluateViewTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns晒单ajax", value = "/buyer/sns_ajax_evas.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_ajax_evaps.htm")
	public ModelAndView sns_ajax_evaps(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/sns_ajax_evaps.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and (obj.evaluate_photos is not null and obj.evaluate_photos!='') order by obj.addTime desc",
						params, begin, end);
		mv.addObject("evas", evas);
		mv.addObject("evaluateViewTools", evaluateViewTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns", value = "/buyer/my_sns_cons.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_cons.htm")
	public ModelAndView my_sns_cons(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_cons.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Consult> cons = this.consultService
				.query("select obj from Consult obj where obj.consult_user_id = :user_id",
						params, 0, 10);
		mv.addObject("cons", cons);
		mv.addObject("freeTools", freeTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns", value = "/buyer/sns_ajax_cons.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/sns_ajax_cons.htm")
	public ModelAndView sns_ajax_cons(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/sns_ajax_cons.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Consult> cons = this.consultService
				.query("select obj from Consult obj where obj.consult_user_id = :user_id",
						params, begin, end);
		mv.addObject("cons", cons);
		mv.addObject("freeTools", freeTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns关注人", value = "/buyer/my_sns_atts.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_atts.htm")
	public ModelAndView my_sns_atts(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_atts.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.fromUser.id", new SysMap("user_id", user.getId()), "=");
		qo.setPageSize(16);
		IPageList pList = this.snsAttentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_atts.htm", "",
				param, pList, mv);
		mv.addObject("snsTools", snsTools);
		return mv;
	}

	@SecurityMapping(title = "买家sns粉丝", value = "/buyer/my_sns_fans.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_fans.htm")
	public ModelAndView my_sns_fans(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_fans.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.toUser.id", new SysMap("user_id", user.getId()), "=");
		qo.setPageSize(16);
		IPageList pList = this.snsAttentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_fans.htm", "",
				param, pList, mv);
		mv.addObject("snsTools", snsTools);
		return mv;
	}

	/**
	 * 个人中心-我的圈子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_circle.htm")
	public ModelAndView my_sns_circle(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_circle_atten.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (type != null && !type.equals("")) {// 我创建的圈子
			mv = new JModelAndView(
					"user/default/usercenter/sns/my_sns_circle.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			List<Map> maps = new ArrayList<Map>();
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_create_info() != null) {
				maps = Json.fromJson(List.class, user.getCircle_create_info());
			}
			if (maps.size() > 0) {
				List<Circle> cirs = new ArrayList<Circle>();
				for (Map map : maps) {
					Circle cir = this.circleService.getObjById(CommUtil
							.null2Long(map.get("id")));
					cirs.add(cir);
				}
				InvitationQueryObject qo = new InvitationQueryObject(
						currentPage, mv, "addTime", "desc");
				qo.addQuery("obj.circle_id", new SysMap("circle_id", cirs
						.get(0).getId()), "=");
				qo.addQuery("obj.invitaion_top",
						new SysMap("invitaion_top", 1), "!=");// 排除置顶帖子
				qo.setOrderBy("invitaion_perfect");
				qo.setPageSize(20);
				IPageList pList = this.invitationService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				if (currentPage == null || CommUtil.null2Int(currentPage) == 1) {
					// 单独查询置顶帖子
					Map params = new HashMap();
					params.put("invitaion_top", 1);
					List<CircleInvitation> tops = this.invitationService
							.query("select obj from CircleInvitation obj where obj.invitaion_top=:invitaion_top",
									params, 0, 1);
					if (tops.size() > 0) {
						mv.addObject("top", tops.get(0));
					}
				}
				mv.addObject("cirs", cirs);
			}
			mv.addObject("type", type);
		} else {// 我关注的圈子
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> circle_list = Json.fromJson(List.class,
						user.getCircle_attention_info());
				List<Circle> objs = new ArrayList<Circle>();
				List<Map> remove_maps = new ArrayList<Map>();
				for (Map map : circle_list) {
					Circle temp = this.circleService.getObjById(CommUtil
							.null2Long(map.get("id")));
					if (temp != null) {
						objs.add(temp);
					} else {
						remove_maps.add(map);
					}
				}
				if (remove_maps.size() > 0) {// 如果用户关注的圈子中有被删除的，将用户的关注圈子信息更新（将被删除的圈子信息删除）
					circle_list.removeAll(remove_maps);
					if (circle_list.size() > 0) {
						user.setCircle_attention_info(Json.toJson(circle_list,
								JsonFormat.compact()));
					} else {
						user.setCircle_attention_info(null);
					}
					this.userService.update(user);
				}
				mv.addObject("objs", objs);
			}
		}
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	/**
	 * 个人中心-我的圈子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle_invitation.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_circle_invitation.htm")
	public ModelAndView my_sns_circle_invitation(HttpServletRequest request,
			HttpServletResponse response, String cid, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_circle_invitation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Circle cir = this.circleService.getObjById(CommUtil.null2Long(cid));
		InvitationQueryObject qo = new InvitationQueryObject(currentPage, mv,
				"", "");
		qo.addQuery("obj.circle_id", new SysMap("circle_id", cir.getId()), "=");
		qo.setOrderBy("invitaion_perfect desc,obj.addTime");
		qo.setOrderType("desc");
		qo.setPageSize(20);
		IPageList pList = this.invitationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("cir", cir);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	/**
	 * 个人中心-我的圈子-管理我创建圈子中的所有帖子（置顶、加精、删除）
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle_invitation_operate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_circle_invitation_operate.htm")
	public String my_sns_circle_invitation_operate(HttpServletRequest request,
			HttpServletResponse response, String operate, String id,
			String cid, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_circle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CircleInvitation obj = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		if (operate.equals("top")) {
			Map params = new HashMap();
			params.put("invitaion_top", 1);
			params.put("circle_id", CommUtil.null2Long(cid));
			List<CircleInvitation> objs = this.invitationService
					.query("select obj from CircleInvitation obj where obj.invitaion_top=:invitaion_top and obj.circle_id=:circle_id",
							params, -1, -1);
			for (CircleInvitation temp_obj : objs) {
				temp_obj.setInvitaion_top(0);
				this.invitationService.update(temp_obj);
			}
			obj.setInvitaion_top(1);
			this.invitationService.update(obj);
		}
		if (operate.equals("perfect")) {
			obj.setInvitaion_perfect(1);
			this.invitationService.update(obj);
		}
		if (operate.equals("delete")) {
			// 删除帖子所有回复
			Map params = new HashMap();
			params.put("id", obj.getId());
			List reply_ids = this.invitationReplyService
					.query("select obj.id from CircleInvitationReply obj where obj.invitation_id=:id",
							params, -1, -1);
			List dele_ids = new ArrayList();
			for (Object temp_id : reply_ids) {
				dele_ids.add(CommUtil.null2Long(temp_id));
			}
			this.invitationReplyService.batchDelete(dele_ids);
			Circle cir = this.circleService.getObjById(obj.getCircle_id());
			cir.setInvitation_count(cir.getInvitation_count() - 1);
			this.circleService.update(cir);
			this.invitationService.delete(obj.getId());
		}
		if (operate.equals("cancle_top")) {
			obj.setInvitaion_top(0);
			this.invitationService.update(obj);
		}
		if (operate.equals("cancle_perfect")) {
			obj.setInvitaion_perfect(0);
			this.invitationService.update(obj);
		}
		mv.addObject("circleViewTools", circleViewTools);
		return "redirect:/buyer/my_sns_circle_invitation.htm?type=my_circle&currentPage="
				+ currentPage + "&cid=" + cid;
	}

	/**
	 * 个人中心-我的帖子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "我的帖子", value = "/buyer/my_sns_invitation.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/my_sns_invitation.htm")
	public ModelAndView my_sns_invitation(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sns/my_sns_invitation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InvitationQueryObject qo = new InvitationQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.invitationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
}
