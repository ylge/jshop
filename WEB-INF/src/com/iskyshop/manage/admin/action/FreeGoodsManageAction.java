package com.iskyshop.manage.admin.action;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.FreeTools;

/**
 * 
 * <p>
 * Title: FreeGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 第三方申请的0元试用商品审核
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
 * @date 2014年11月12日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class FreeGoodsManageAction {
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

	/**
	 * FreeGoods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品列表", value = "/admin/freegoods_list.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/freegoods_list.htm")
	public ModelAndView freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name, String beginTime,
			String endTime, String cls, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/freegoods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		if (free_name != null && !free_name.equals("")) {
			qo.addQuery("obj.free_name", new SysMap("free_name", "%"
					+ free_name + "%"), "like");
			mv.addObject("free_name", free_name);
		}
		if (cls != null && !cls.equals("")) {
			qo.addQuery("obj.class_id",
					new SysMap("class_id", CommUtil.null2Long(cls)), "=");
			mv.addObject("cls_id", cls);
		}
		if (status != null && status.equals("going")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("finish")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 10), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("failed")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", -5), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("waiting")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 0), "=");
			mv.addObject("status", status);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeGoods.class, mv);
		IPageList pList = this.freegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", freeTools);
		return mv;
	}

	/**
	 * freegoods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用商品审核", value = "/admin/freegoods_add.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/freegoods_edit.htm")
	public ModelAndView freegoods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/freegoods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeGoods freegoods = this.freegoodsService.getObjById(Long
					.parseLong(id));
			Goods goods = this.goodsService.getObjById(freegoods.getGoods_id());
			List<FreeClass> fcls = this.freeClassService.query(
					"select obj from FreeClass obj", null, -1, -1);
			mv.addObject("goods", goods);
			mv.addObject("fcls", fcls);
			mv.addObject("obj", freegoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * freegoods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/admin/freegoods_add.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/freegoods_save.htm")
	public ModelAndView freegoods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_ur, String status,
			String failed_reason) {
		WebForm wf = new WebForm();
		FreeGoods freegoods = null;
		if (id.equals("")) {
			freegoods = wf.toPo(request, FreeGoods.class);
			freegoods.setAddTime(new Date());
		} else {
			FreeGoods obj = this.freegoodsService
					.getObjById(Long.parseLong(id));
			freegoods = (FreeGoods) wf.toPo(request, obj);
		}
		freegoods.setFreeStatus(CommUtil.null2Int(status));
		freegoods.setFailed_reason(failed_reason);
		if (id.equals("")) {
			this.freegoodsService.save(freegoods);
		} else
			this.freegoodsService.update(freegoods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/freegoods_list.htm");
		mv.addObject("op_title", "审核0元试用成功");
		return mv;
	}

	@SecurityMapping(title = "0元试用商品申请列表", value = "/admin/freegoods_add.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/freeapply_logs.htm")
	public ModelAndView freeapply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String userName, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/freeapply_logs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.freegoods_id",
				new SysMap("freegoods_id", CommUtil.null2Long(id)), "=");
		if (userName != null && !userName.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("userName", "%" + userName
					+ "%"), "like");
			mv.addObject("userName", userName);
		}
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
		CommUtil.saveIPageList2ModelAndView(url + "/admin/freeapply_logs.htm",
				"", params, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "0元试用商品申请详情", value = "/admin/apply_log_info.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/apply_log_info.htm")
	public ModelAndView apply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/apply_log_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeApplyLog freeapplylog = this.freeapplylogService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", freeapplylog);
		}
		return mv;
	}

	@SecurityMapping(title = "0元试用活动关闭", value = "/admin/free_close.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/free_close.htm")
	public String free_close(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		FreeGoods fg = this.freegoodsService.getObjById(CommUtil.null2Long(id));
		fg.setFreeStatus(10);
		this.freegoodsService.update(fg);
		return "redirect:freegoods_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "0元试用活动删除", value = "/admin/freegoods_del.htm*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping("/admin/freegoods_del.htm")
	public String freegoods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				FreeGoods fg = this.freegoodsService.getObjById(CommUtil
						.null2Long(id));
				if (fg != null) {
					this.freegoodsService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:freegoods_list.htm?currentPage=" + currentPage;
	}
}