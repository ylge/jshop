package com.iskyshop.manage.buyer.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.FreeTools;
import com.iskyshop.manage.buyer.tools.ShipTools;

/**
 * 
 * <p>
 * Title: FreeBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心0元试用中心
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
 * @date 2014-11-18
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class FreeBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IFreeClassService freeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private IFreeApplyLogService freeapplylogService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ShipTools shipTools;

	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_logs.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapply_logs.htm")
	public ModelAndView freeapply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/freeapplylog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery(
				"obj.user_id",
				new SysMap("user_id", CommUtil.null2Long(SecurityUserHolder
						.getCurrentUser().getId())), "=");
		if (status != null && status.equals("yes")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", 5), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("waiting")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", 0), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("no")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", -5), "=");
			mv.addObject("status", status);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeApplyLog.class, mv);
		IPageList pList = this.freeapplylogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapply_log_info.htm")
	public ModelAndView freeapply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/freeapplylog_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FreeApplyLog fal = this.freeapplylogService.getObjById(CommUtil
				.null2Long(id));
		if (fal != null && fal.getUser_id().equals(user.getId())) {
			mv.addObject("obj", fal);
			mv.addObject("shipTools", shipTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "此0元试用申请无效");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/freeapply_logs.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapplylog_save.htm")
	public void freeapplylog_save(HttpServletRequest request,
			HttpServletResponse response, String id, String use_experience) {
		User user = SecurityUserHolder.getCurrentUser();
		FreeApplyLog fal = this.freeapplylogService.getObjById(CommUtil
				.null2Long(id));
		if (fal.getUser_id().equals(user.getId())) {
			fal.setUse_experience(use_experience);
			fal.setEvaluate_time(new Date());
			fal.setEvaluate_status(1);
			this.freeapplylogService.save(fal);
		}
	}
}
