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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAddressService;
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
 * Title: FreeViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前台0元试用控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
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
public class FreeViewAction {
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
	private IAddressService addressService;

	@RequestMapping("/free/index.htm")
	public ModelAndView freegoods_index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String cls) {
		ModelAndView mv = new JModelAndView("free_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.setPageSize(20);
		if (cls != null && !cls.equals("")) {
			qo.addQuery("obj.class_id",
					new SysMap("class_id", CommUtil.null2Long(cls)), "=");
			mv.addObject("cls_id", cls);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeGoods.class, mv);
		qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
		qo.setPageSize(6);
		IPageList pList = this.freegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		List<FreeGoods> hot_fgs = this.freegoodsService
				.query("select obj from FreeGoods obj where obj.freeStatus=5 order by obj.apply_count desc",
						null, 0, 6);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", freeTools);
		mv.addObject("hots", hot_fgs);
		return mv;
	}

	@RequestMapping("/free/view.htm")
	public ModelAndView free_view(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("free_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoods obj = this.freegoodsService
				.getObjById(CommUtil.null2Long(id));
		if (obj == null) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查看的商品已经下架");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		if (obj.getFreeStatus() == -5 || obj.getFreeStatus() == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} else {
			if(obj.getEndTime().before(new Date())){
				obj.setFreeStatus(10);
				this.freegoodsService.update(obj);
				Goods goods = this.goodsService.getObjById(obj.getGoods_id());
				if(goods!=null){
					goods.setWhether_free(0);
					this.goodsService.update(goods);
				}
				mv = new JModelAndView("error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您查看的商品已经下架");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}else{
				List<FreeGoods> hot_fgs = this.freegoodsService
						.query("select obj from FreeGoods obj where obj.freeStatus=5 order by obj.apply_count desc",
								null, 0, 6);
				mv.addObject("hots", hot_fgs);
				mv.addObject("freeTools", freeTools);
				mv.addObject("obj", obj);
			}
		}
		return mv;
	}

	@RequestMapping("/free/logs.htm")
	public ModelAndView free_logs(String id, HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("free_logs_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(currentPage,
				mv, "evaluate_time", "desc");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 1),
				"=");
		qo.addQuery("obj.freegoods_id",
				new SysMap("freegoods_id", CommUtil.null2Long(id)), "=");
		qo.setPageSize(20);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeApplyLog.class, mv);
		IPageList pList = this.freeapplylogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/free/logs.htm", "", params,
				pList, mv);
		mv.addObject("freeTools", freeTools);
		mv.addObject("id", id);
		return mv;
	}

	@SecurityMapping(title = "0元试用申请", value = "/free_apply.htm*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping("/free_apply.htm")
	public ModelAndView free_apply(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("free_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FreeGoods fg = this.freegoodsService.getObjById(CommUtil.null2Long(id));
		Map params = new HashMap();
		params.put("user_id", user.getId());
		params.put("id", CommUtil.null2Long(id));
		List<FreeApplyLog> fals1 = this.freeapplylogService
				.query("select obj from FreeApplyLog obj where obj.user_id=:user_id and obj.freegoods_id=:id",
						params, -1, -1);
		params.clear();
		params.put("user_id", user.getId());
		List<FreeApplyLog> fals2 = this.freeapplylogService
				.query("select obj from FreeApplyLog obj where obj.user_id=:user_id and obj.evaluate_status=0",
						params, -1, -1);
		// 用户申请过当前0元试用与尚有未评价0元试用则不可申请
		if (fals1.size() > 0 || fals2.size() > 0||fg.getFreeStatus()!=5||fg.getCurrent_count()==0||fg.getEndTime().before(new Date())) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
			if(fg.getFreeStatus()!=5||fg.getEndTime().before(new Date())){
				mv.addObject("op_title", "此0元试用已结束");
				if(fg.getEndTime().before(new Date())){
					fg.setFreeStatus(10);
					this.freegoodsService.update(fg);
				}
			}
			if(fg.getCurrent_count()==0){
				mv.addObject("op_title", "此0元试用库存不足");
			}
			mv.addObject("url", CommUtil.getURL(request) + "/free/index.htm");
		} else {
			List<Address> addrs = this.addressService
					.query("select obj from Address obj where obj.user.id=:user_id order by obj.default_val desc,obj.addTime desc",
							params, -1, -1);
			mv.addObject("addrs", addrs);
			String apply_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("apply_session",
					apply_session);
			mv.addObject("apply_session", apply_session);
			mv.addObject("id", id);
		}
		return mv;
	}

	@SecurityMapping(title = "0元试用申请", value = "/free_apply.htm*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping("/free_apply_save.htm")
	public ModelAndView free_apply_save(String id, HttpServletRequest request,
			HttpServletResponse response, String apply_reason,
			String apply_session, String addr_id) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String apply_session1 = (String) request.getSession(false)
				.getAttribute("apply_session");
		if (apply_session1.equals(apply_session)) {
			FreeGoods fg = this.freegoodsService.getObjById(CommUtil
					.null2Long(id));
			User user = SecurityUserHolder.getCurrentUser();
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("id", CommUtil.null2Long(id));
			List<FreeApplyLog> fals1 = this.freeapplylogService
					.query("select obj from FreeApplyLog obj where obj.user_id=:user_id and obj.freegoods_id=:id",
							params, -1, -1);
			params.clear();
			params.put("user_id", user.getId());
			List<FreeApplyLog> fals2 = this.freeapplylogService
					.query("select obj from FreeApplyLog obj where obj.user_id=:user_id and obj.evaluate_status=0",
							params, -1, -1);
			// 用户申请过当前0元试用与尚有未评价0元试用则不可申请
			if (fals1.size() > 0 || fals2.size() > 0||fg.getFreeStatus()!=5||fg.getCurrent_count()==0||fg.getEndTime().before(new Date())) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
				if(fg.getFreeStatus()!=5||fg.getEndTime().before(new Date())){
					mv.addObject("op_title", "此0元试用已结束");
					if(fg.getEndTime().before(new Date())){
						fg.setFreeStatus(10);
						this.freegoodsService.update(fg);
					}
				}
				if(fg.getCurrent_count()==0){
					mv.addObject("op_title", "此0元试用库存不足");
				}
				mv.addObject("url", CommUtil.getURL(request)
						+ "/free/index.htm");
			} else {
				Address addr = this.addressService.getObjById(CommUtil
						.null2Long(addr_id));
				if (fg != null) {
					FreeApplyLog fal = new FreeApplyLog();
					fal.setAddTime(new Date());
					fal.setFreegoods_id(fg.getId());
					fal.setWhether_self(fg.getFreeType());
					fal.setStore_id(fg.getStore_id());
					fal.setFreegoods_name(fg.getFree_name());
					// 设置收货地址信息
					fal.setReceiver_Name(addr.getTrueName());
					fal.setReceiver_area(addr.getArea().getParent().getParent()
							.getAreaName()
							+ addr.getArea().getParent().getAreaName()
							+ addr.getArea().getAreaName());
					fal.setReceiver_area_info(addr.getArea_info());
					fal.setReceiver_mobile(addr.getMobile());
					fal.setReceiver_telephone(addr.getTelephone());
					fal.setReceiver_zip(addr.getZip());
					fal.setUser_id(user.getId());
					fal.setUser_name(user.getUserName());
					this.freeapplylogService.save(fal);
					fg.setApply_count(fg.getApply_count() + 1);
					this.freegoodsService.update(fg);
					fal.setApply_reason(CommUtil.filterHTML(apply_reason));
					mv.addObject("op_title", "申请成功，请耐心等待审核");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/free/index.htm");
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/free/index.htm");
				}

			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
}
