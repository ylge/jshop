package com.iskyshop.view.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.view.web.tools.ActivityViewTools;

/**
 * 
 * <p>
 * Title: ActivityViewAction.java
 * </p>
 * 
 * <p>
 * Description:商城活动前台管理控制控制器
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
 * @date 2014-9-24
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class ActivityViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/activity/index.htm")
	public ModelAndView activity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("activity.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> acts = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
						params, -1, -1);
		if (acts.size()>0) {
			if (id == null) {
				id = CommUtil.null2String(acts.get(0).getId());
				mv.addObject("op", "true");
			}
			Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
			if (act != null) {
				if (act.getAc_status() == 1) {
					ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(
							currentPage, mv, "addTime", "desc");
					qo.setPageSize(20);
					qo.addQuery("obj.ag_status", new SysMap("ag_status", 1), "=");// 审核是否通过
					qo.addQuery("obj.act.id", new SysMap("act_id", act.getId()),
							"=");
					qo.addQuery("obj.ag_goods.goods_status", new SysMap(
							"goods_status", 0), "=");// 商品状态为上架
					IPageList pList = this.activityGoodsService.list(qo);
					CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
					mv.addObject("act", act);
					mv.addObject("activityViewTools", activityViewTools);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "活动尚未开启");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
				}
			}else{
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，活动查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
			mv.addObject("acts", acts);
		}else{
			mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启任何商城活动");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		return mv;
	}

	@RequestMapping("/buygift/index.htm")
	public ModelAndView buygift(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("buygift.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(id));
		if (bg != null) {
			if (bg.getGift_status() == 10) {
				mv.addObject("obj", bg);
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，活动查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，活动查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		return mv;
	}

	@RequestMapping("/enoughreduce/index.htm")
	public ModelAndView enoughreduce(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("enoughreduceview.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		EnoughReduce er = this.enoughReduceService.getObjById(CommUtil
				.null2Long(id));
		if (er == null || er.getErstatus() != 10) {
			return null;
		}
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, null, null);
		qo.addQuery("obj.order_enough_reduce_id", new SysMap(
				"order_enough_reduce_id", id), "=");
		qo.setPageSize(20);
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("currentPage", currentPage);
		mv.addObject("user", user);
		mv.addObject("er", er);
		return mv;
	}
}
