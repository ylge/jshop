package com.iskyshop.module.sns.view.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
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
import com.iskyshop.module.circle.service.IInvitationService;
import com.iskyshop.module.circle.view.tools.CircleViewTools;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.domain.UserDynamic;
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
 * Title: SnsViewAction.java
 * </p>
 * 
 * <p>
 * Description:前台sns控制器
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
 * @author jinxinzhe,jy
 * 
 * @date 2014-11-21
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SnsOtherUserViewAction {
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
	private SnsTools snsTools;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private CircleViewTools circleViewTools;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IUserShareService userShareService;
	@Autowired
	private IUserDynamicService userdynamicService;	
	@Autowired
	private  IUserDynamicService dynamicService;


	/**
	 * 用户查看其他人的个人主页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/sns/other_sns.htm")
	public String other_sns(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		String url = "/sns/to_other_sns.htm?id=" + id;
		if (SecurityUserHolder.getCurrentUser() != null) {
			if (SecurityUserHolder.getCurrentUser().getId().toString()
					.equals(id)) {
				url = "/buyer/my_sns_index.htm";
			}
		}
		return "redirect:" + url;
	}

	/**
	 * 跳转到他人个人主页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/sns/to_other_sns.htm")
	public ModelAndView to_other_sns(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		ModelAndView mv = new JModelAndView("sns/other_sns_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(id));
		if (user != null) {
			if (user.getWhether_attention() == 0 ) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该用户禁止其他用户访问其主页");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
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
					map.put("user_photo", sns.getFromUser().getPhoto()
							.getPath()
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
			mv.addObject("uid", id);
			if (SecurityUserHolder.getCurrentUser() != null) {
				User currentUser = this.userService
						.getObjById(SecurityUserHolder.getCurrentUser().getId());
				mv.addObject("currentUser", currentUser);
			}
			//加载动态
			params.clear();
			params.put("user_id",user.getId());
			List<UserDynamic> dynamics = this.dynamicService.query("select obj from UserDynamic obj where obj.user_id =:user_id order by obj.addTime desc", params, 0,1);
			if(dynamics.size()>0){
				mv.addObject("userDynamics",dynamics.get(0));			
			}
			mv.addObject("snsTools", snsTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/sns/other_sns_lock.htm")
	public ModelAndView other_sns_lock(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("sns/sns_lock.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User otherUser = this.userService.getObjById(CommUtil.null2Long(id));
		mv.addObject("otherUser", otherUser);
		return mv;
	}

	/**
	 * 他人主页-头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/other_sns_head.htm")
	public ModelAndView other_sns_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("sns/sns_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String otherUser_id = CommUtil.null2String(request.getAttribute("uid"));
		User user = this.userService.getObjById(CommUtil
				.null2Long(otherUser_id));
		int attsCount = this.snsTools.queryAtts(otherUser_id);
		int fansCount = this.snsTools.queryFans(otherUser_id);
		int favsCount = this.snsTools.queryfavCount(otherUser_id);
		mv.addObject("attsCount", attsCount);
		mv.addObject("fansCount", fansCount);
		mv.addObject("favsCount", favsCount);
		mv.addObject("otherUser", user);
		mv.addObject("currentUser", SecurityUserHolder.getCurrentUser());
		mv.addObject("snsTools", snsTools);
		return mv;
	}

	/**
	 * 他人主页-导航
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/other_sns_nav.htm")
	public ModelAndView other_sns_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("sns/sns_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		String uid = CommUtil.null2String(request.getAttribute("uid"));
		mv.addObject("uid", uid);
		return mv;
	}

	/**
	 * 他人主页 - 动态
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/buyer/other_sns_dynamic.htm")
	public ModelAndView other_sns_dynamic(HttpServletRequest request,
			HttpServletResponse response,String other_id) {
		ModelAndView mv = new JModelAndView(
				"sns/other_sns_dynamic.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//查询用户动态
		Map params = new HashMap();
		params.put("user_id",CommUtil.null2Long(other_id));
		List<UserDynamic> userDynamics = this.userdynamicService.query("select obj from UserDynamic obj where obj.user_id =:user_id order by obj.addTime desc", params, 0,12);
		mv.addObject("userDynamics",userDynamics);
		mv.addObject("snsTools",snsTools);
		User otherUser = this.userService.getObjById(CommUtil
				.null2Long(other_id));
		mv.addObject("other_id",other_id);
		mv.addObject("otherUser",otherUser);
		// 加载关注人信息
		params.clear();
		params.put("fromUser", CommUtil.null2Long(other_id));
		List<SnsAttention> tempSnss = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser order by obj.addTime desc",
						params, 0, 6);
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
		params.put("toUser", CommUtil.null2Long(other_id));
		tempSnss = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.toUser.id=:toUser order by obj.addTime desc",
						params, 0, 6);
		List<Map<String, String>> userFansList = new ArrayList<Map<String, String>>();
		for (SnsAttention sns : tempSnss) {
			Map map = new HashMap<String, String>();
			map.put("user_id", sns.getFromUser().getId());
			map.put("user_name", sns.getFromUser().getUserName());
			map.put("sns_time", sns.getAddTime());
			if (sns.getFromUser().getPhoto() != null) {
				map.put("user_photo", sns.getFromUser().getPhoto()
						.getPath()
						+ "/" + sns.getFromUser().getPhoto().getName());
			}
			userFansList.add(map);
		}
		mv.addObject("userFansList", userFansList);
		return mv;
	}
	
	@RequestMapping("/buyer/ajax_dynamic.htm")
	public ModelAndView ajax_dynamic(HttpServletRequest request,
			HttpServletResponse response,String count,String otherId) {
		ModelAndView mv = new JModelAndView("sns/sns_ajax_dynamic.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User otherUser = this.userService.getObjById(CommUtil
				.null2Long(otherId));
		Map params = new HashMap();
		params.put("user_id",otherUser.getId());
		List<UserDynamic> userDynamics = this.userdynamicService.query("select obj from UserDynamic obj where obj.user_id =:user_id order by obj.addTime desc",
				params,CommUtil.null2Int(count),12);
		mv.addObject("userDynamics",userDynamics);
		mv.addObject("snsTools",snsTools);
		mv.addObject("otherUser",otherUser);
		return mv;
	}
	
	/**
	 * 他人主页-评价
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/other_sns_evas.htm")
	public ModelAndView other_sns_evas(HttpServletRequest request,
			HttpServletResponse response, String other_id) throws IOException {
		ModelAndView mv = new JModelAndView("sns/other_sns_evas.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(other_id));
		if (user != null && user.getWhether_attention() == 1) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<Evaluate> evas = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is null or obj.evaluate_photos='') order by obj.addTime desc",
							params, 0, 10);
			mv.addObject("evas", evas);
			mv.addObject("other_id", other_id);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 他人主页-评价ajax
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/ajax_evas.htm")
	public ModelAndView sns_ajax_evas(HttpServletRequest request,
			HttpServletResponse response, String size, String other_id) {
		ModelAndView mv = new JModelAndView("sns/sns_ajax_evas.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and (obj.evaluate_photos is null or obj.evaluate_photos='') order by obj.addTime desc",
						params, begin, end);
		mv.addObject("evas", evas);
		return mv;
	}

	/**
	 * 他人主页-晒单
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/other_sns_evaps.htm")
	public ModelAndView other_sns_evaps(HttpServletRequest request,
			HttpServletResponse response, String other_id) throws IOException {
		ModelAndView mv = new JModelAndView("sns/other_sns_evaps.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(other_id));
		if (user != null && user.getWhether_attention() == 1) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<Evaluate> evaps = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and  (obj.evaluate_photos is not null and obj.evaluate_photos!='') order by obj.addTime desc",
							params, 0, 10);
			mv.addObject("evaps", evaps);
			mv.addObject("other_id", other_id);
			mv.addObject("evaluateViewTools", evaluateViewTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/sns/ajax_evaps.htm")
	public ModelAndView sns_ajax_evaps(HttpServletRequest request,
			HttpServletResponse response, String size, String id) {
		ModelAndView mv = new JModelAndView("sns/sns_ajax_evaps.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(id));
		List<Evaluate> evaps = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_user.id = :user_id and obj.evaluate_status=0 and (obj.evaluate_photos is not null and obj.evaluate_photos!='') order by obj.addTime desc",
						params, begin, end);
		mv.addObject("evaps", evaps);
		mv.addObject("evaluateViewTools", evaluateViewTools);
		return mv;
	}

	/**
	 * 他人主页-分享
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping("/sns/other_sns_share.htm")
	public ModelAndView other_sns_share(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new JModelAndView("sns/other_sns_share.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		UserShareQueryObject qo = new UserShareQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user_id",
				new SysMap("user_id", CommUtil.null2Long(other_id)), "=");
		IPageList pList = this.userShareService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_share.htm",
				"", param, pList, mv);
		mv.addObject("currentPage", currentPage);
		mv.addObject("other_id", other_id);
		return mv;
	}

	/**
	 * 他人主页-圈子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@RequestMapping("/sns/other_circle.htm")
	public ModelAndView other_circle(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String other_id) {
		ModelAndView mv = new JModelAndView("/sns/other_circle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(other_id));
		if (user.getCircle_attention_info() != null) {
			List<Map> circle_list = Json.fromJson(List.class,
					user.getCircle_attention_info());
			List<Circle> objs = new ArrayList<Circle>();
			for (Map map : circle_list) {
				Circle temp = this.circleService.getObjById(CommUtil
						.null2Long(map.get("id")));
				objs.add(temp);
			}
			mv.addObject("objs", objs);
		}
		mv.addObject("circleViewTools", circleViewTools);
		mv.addObject("uid", other_id);
		return mv;
	}

	/**
	 * 他人主页-帖子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@RequestMapping("/sns/other_invitation.htm")
	public ModelAndView other_invitation(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new JModelAndView("/sns/other_invitation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		InvitationQueryObject qo = new InvitationQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user_id",
				new SysMap("user_id", CommUtil.null2Long(other_id)), "=");
		IPageList pList = this.invitationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("uid", other_id);
		return mv;
	}

	/**
	 * 他人主页-收藏
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/other_sns_fav.htm")
	public ModelAndView other_sns_fav(HttpServletRequest request,
			HttpServletResponse response, String other_id) {
		ModelAndView mv = new JModelAndView("sns/other_sns_favs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		List<Favorite> favorites = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id = :user_id and obj.type=0 order by obj.addTime desc",
						params, 0, 10);
		mv.addObject("favorites", favorites);
		mv.addObject("other_id", other_id);
		return mv;
	}

	/**
	 * 他人主页-收藏ajax
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sns/ajax_favs.htm")
	public ModelAndView sns_ajax_favs(HttpServletRequest request,
			HttpServletResponse response, String size, String other_id) {
		ModelAndView mv = new JModelAndView("sns/sns_ajax_favs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		List<Favorite> favorites = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id = :user_id and obj.type=0 order by obj.addTime desc",
						params, begin, end);
		mv.addObject("favorites", favorites);
		mv.addObject("other_id", other_id);
		return mv;
	}

	/**
	 * 他人主页-关注
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping("/sns/other_sns_atts.htm")
	public ModelAndView other_sns_atts(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new JModelAndView("/sns/other_sns_atts.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.fromUser.id",
				new SysMap("user_id", CommUtil.null2Long(other_id)), "=");
		IPageList pList = this.snsAttentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_atts.html",
				"", param, pList, mv);
		mv.addObject("other_id", other_id);
		mv.addObject("snsTools", snsTools);
		return mv;
	}

	/**
	 * 他人主页-粉丝
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping("/sns/other_sns_fans.htm")
	public ModelAndView other_sns_fans(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new JModelAndView("/sns/other_sns_fans.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.toUser.id",
				new SysMap("user_id", CommUtil.null2Long(other_id)), "=");
		IPageList pList = this.snsAttentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_fans.htm",
				"", param, pList, mv);
		mv.addObject("snsTools", snsTools);
		mv.addObject("other_id", other_id);
		return mv;
	}

}
