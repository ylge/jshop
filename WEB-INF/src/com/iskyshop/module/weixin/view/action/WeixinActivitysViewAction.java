package com.iskyshop.module.weixin.view.action;

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

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.view.web.tools.GoodsViewTools;
/**
 * 
 * 
* <p>Title:WapActivitysViewAction.java</p>

* <p>Description:移动端活动列表以及参加活动商品列表 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jinxinzhe

* @date 2014年8月20日

* @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinActivitysViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired 
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	
	/**
	 * 活动列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/activitys.htm")
	public ModelAndView activitys(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/activitys.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time order by addTime desc", params, 0,12);
		mv.addObject("activitys", activitys);
		return mv;
	}
	
	/**
	 * 活动列表ajax加载数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/activitys_ajax.htm")
	public ModelAndView activitys_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView("wap/activitys_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time order by addTime desc", params, CommUtil.null2Int(begin_count),6);
		mv.addObject("activitys", activitys);
		return mv;
	}
	
	@RequestMapping("/wap/activitys_goods.htm")
	public ModelAndView activitys_goods(HttpServletRequest request,
			HttpServletResponse response, String act_id){
		ModelAndView mv = new JModelAndView("wap/activitys_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.clear();
		params.put("ag_status", 1);
		params.put("goods_status", 0);
		params.put("act_id", CommUtil.null2Long(act_id));
		List<ActivityGoods> activitygoods = this.activityGoodsService
				.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
						+ "and obj.act.id=:act_id order by addTime desc", params, 0,12);
		mv.addObject("activitygoods", activitygoods);
		mv.addObject("act_id", act_id);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}
	
	@RequestMapping("/wap/activitys_goods_ajax.htm")
	public ModelAndView activitys_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String act_id, String begin_count){
		ModelAndView mv = new JModelAndView("wap/activitys_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.clear();
		params.put("ag_status", 1);
		params.put("goods_status", 0);
		params.put("act_id", CommUtil.null2Long(act_id));
		List<ActivityGoods> activitygoods = this.activityGoodsService
				.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
						+ "and obj.act.id=:act_id order by addTime desc", params,CommUtil.null2Int(begin_count),6);
		mv.addObject("activitygoods", activitygoods);
		return mv;
	}
	
	
}
