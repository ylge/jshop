package com.iskyshop.module.weixin.view.action;

import java.util.ArrayList;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.User;
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
 * Title: WapFreeGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: wap前台0元试用控制器
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
public class WeixinFreeGoodsViewAction {
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

	@RequestMapping("/wap/free/index.htm")
	public ModelAndView freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String cls) {
		ModelAndView mv = new JModelAndView("wap/free_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map map = new HashMap();
		map.put("freeStatus", 5);
		String query = "select obj from FreeGoods obj where obj.freeStatus=:freeStatus order by addTime desc";
		if (cls != null && !cls.equals("")) {
			mv.addObject("cls_id", cls);
			map.put("class_id", CommUtil.null2Long(cls));
			query = "select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.class_id=:class_id order by addTime desc";
		}
		List<FreeGoods> objs = this.freegoodsService.query(query, map, 0, 12);
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", freeTools);
		mv.addObject("objs", objs);
		return mv;
	}

	@RequestMapping("/wap/free/data.htm")
	public ModelAndView freegoods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String cls) {
		ModelAndView mv = new JModelAndView("wap/free_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map map = new HashMap();
		map.put("freeStatus", 5);
		String query = "select obj from FreeGoods obj where obj.freeStatus=:freeStatus order by addTime desc";
		if (cls != null && !cls.equals("")) {
			mv.addObject("cls_id", cls);
			map.put("class_id", CommUtil.null2Long(cls));
			query = "select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.class_id=:class_id order by addTime desc";
		}
		List<FreeGoods> objs = this.freegoodsService.query(query, map,
				CommUtil.null2Int(begin_count), 12);
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", freeTools);
		mv.addObject("objs", objs);
		return mv;
	}

	@RequestMapping("/wap/free/view.htm")
	public ModelAndView free_view(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/free_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoods obj = this.freegoodsService
				.getObjById(CommUtil.null2Long(id));
		if (obj == null) {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查看的商品已经下架");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
			return mv;
		}
		if (obj.getFreeStatus() == -5 || obj.getFreeStatus() == 0) {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		mv.addObject("freeTools", freeTools);
		mv.addObject("obj", obj);
		return mv;
	}


	@SecurityMapping(title = "0元试用申请", value = "/wap/free_apply.htm*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping("/wap/free_apply.htm")
	public ModelAndView free_apply(String id, HttpServletRequest request,
			HttpServletResponse response,String addr_id) {
		ModelAndView mv = new JModelAndView("wap/free_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
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
		if (fals1.size() > 0 || fals2.size() > 0) {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/free/index.htm");
		} else {
			List<Address> addrs = new ArrayList<Address>();
			if(addr_id!=null&&!addr_id.equals("")){
				Address address = this.addressService.getObjById(CommUtil.null2Long(addr_id));
				addrs.add(address);
			}else{
				addrs = this.addressService
						.query("select obj from Address obj where obj.user.id=:user_id order by obj.default_val desc,obj.addTime desc",
								params, 0, 1);
			}
			if(addrs.size()>0){
				mv.addObject("addr_id", addrs.get(0).getId());
			}
			mv.addObject("addrs", addrs);
			String apply_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("apply_session",
					apply_session);
			mv.addObject("apply_session", apply_session);
			mv.addObject("id", id);
		}
		return mv;
	}

	@SecurityMapping(title = "0元试用申请", value = "/wap/free_apply.htm*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping("/wap/free_apply_save.htm")
	public ModelAndView free_apply_save(String id, HttpServletRequest request,
			HttpServletResponse response, String apply_reason,
			String apply_session, String addr_id) {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String apply_session1 = (String) request.getSession(false)
				.getAttribute("apply_session");
		if (apply_session1.equals(apply_session)) {
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
			if (fals1.size() > 0 || fals2.size() > 0) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/free/index.htm");
			} else {
				FreeGoods fg = this.freegoodsService.getObjById(CommUtil
						.null2Long(id));
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
							+ "/wap/free/index.htm");
				} else {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/free/index.htm");
				}

			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}
}
