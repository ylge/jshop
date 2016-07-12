package com.iskyshop.module.app.view.action;

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

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
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
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.FreeTools;

/**
 * 
 * <p>
 * Title: AppFreeViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机app零元购
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
 * @author lixiaoyang
 * 
 * @date 2015-1-19
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppFreeViewAction {
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
	@Autowired
	private IUserService userService;

	/**
	 * 首页推荐
	 * 
	 * @param request
	 * @param response
	 * @param class_id
	 * @param begincount
	 * @param selectcount
	 */
	@RequestMapping("/app/free_index.htm")
	public void freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String class_id, String begincount,
			String selectcount) {
		Map map = new HashMap();
		map.put("freeStatus", 5);
		StringBuffer query = new StringBuffer(
				"select obj from FreeGoods obj where obj.freeStatus=:freeStatus ");
		if (class_id != null && !class_id.equals("")) {
			map.put("class_id", CommUtil.null2Long(class_id));
			query.append(" and obj.class_id=:class_id");
		}
		List<FreeGoods> fgs = this.freegoodsService.query(query.toString(),
				map, CommUtil.null2Int(begincount),
				CommUtil.null2Int(selectcount));
		map.clear();
		List<Map> fgs_map = new ArrayList<Map>();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (FreeGoods fg : fgs) {
			Map f = new HashMap();
			f.put("free_id", fg.getId());
			f.put("free_name", fg.getFree_name());
			f.put("free_acc", url + "/" + fg.getFree_acc().getPath() + "/"
					+ fg.getFree_acc().getName());
			Goods goods = this.goodsService.getObjById(fg.getGoods_id());
			String price = "";
			if (goods != null) {
				price = goods.getGoods_current_price().toString();
			}
			f.put("free_price", price);
			f.put("free_count", fg.getApply_count());
			fgs_map.add(f);
		}
		map.put("free_list", fgs_map);
		map.put("ret", "true");
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	/**
	 * 列表
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/freeclass.htm")
	public void freeclass_list(HttpServletRequest request,
			HttpServletResponse response) {
		Map map = new HashMap();
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		List<Map> fcl_map = new ArrayList<Map>();
		for (FreeClass fc : fcls) {
			Map f = new HashMap();
			f.put("class_id", fc.getId());
			f.put("class_name", fc.getClassName());
			fcl_map.add(f);
		}
		map.put("freeclass_list", fcl_map);
		map.put("ret", "true");
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	/**
	 * 详情
	 * 
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/free_view.htm")
	public void free_view(String id, HttpServletRequest request,
			HttpServletResponse response) {
		Map map = new HashMap();
		FreeGoods obj = this.freegoodsService
				.getObjById(CommUtil.null2Long(id));
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		if (obj != null && obj.getFreeStatus() == 5) {
			map.put("free_id", obj.getId());
			map.put("free_name", obj.getFree_name());
			Goods goods = this.goodsService.getObjById(obj.getGoods_id());
			String price = "";
			if (goods != null) {
				map.put("free_acc", url + "/"
						+ goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName() + "_middle."
						+ goods.getGoods_main_photo().getExt());
				price = goods.getGoods_current_price().toString();
			}
			map.put("default_count", obj.getDefault_count());
			map.put("current_count", obj.getCurrent_count());
			map.put("free_price", price);
			map.put("apply_count", obj.getApply_count());
			map.put("free_details", obj.getFree_details());
			map.put("endTime", CommUtil.formatLongDate(obj.getEndTime()));
		}
		map.put("ret", "true");
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	/**
	 * 评价
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @param begincount
	 * @param selectcount
	 */
	@RequestMapping("/app/free_logs.htm")
	public void free_logs(String id, HttpServletRequest request,
			HttpServletResponse response, String begincount, String selectcount) {
		Map params = new HashMap();
		params.put("evaluate_status", 1);
		params.put("freegoods_id", CommUtil.null2Long(id));
		List<FreeApplyLog> fals = this.freeapplylogService
				.query("select obj from FreeApplyLog obj where obj.evaluate_status=:evaluate_status and obj.freegoods_id=:freegoods_id",
						params, CommUtil.null2Int(begincount),
						CommUtil.null2Int(selectcount));
		List<Map> maps = new ArrayList<Map>();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (FreeApplyLog fal : fals) {
			Map map = new HashMap();
			User user = freeTools.queryEvaluteUser(fal.getUser_id().toString());
			map.put("user_id", fal.getUser_id());
			map.put("user_name", fal.getUser_name());
			map.put("evaluate_time", fal.getEvaluate_time());
			map.put("use_experience", fal.getUse_experience());
			if (user.getPhoto() != null) {
				map.put("user_photo", url + "/" + user.getPhoto().getPath()
						+ "/" + user.getPhoto().getName());
			} else {
				map.put("user_photo", "");
			}

			maps.add(map);
		}
		Map map = new HashMap();
		map.put("eva_list", maps);
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/free_apply.htm")
	public void free_apply(String id, HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		Map map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null
				&& user.getApp_login_token().equals(token.toLowerCase())) {

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
			if (fals1.size() == 0 && fals2.size() == 0) {
				map.put("code", 100);
			} else {
				map.put("code", -100);
			}
		}
		map.put("ret", "true");
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/free_apply_save.htm")
	public void free_apply_save(String id, HttpServletRequest request,
			HttpServletResponse response, String apply_reason, String addr_id,
			String user_id, String token) {
		int code = 0;
		Map json = new HashMap();
		if (user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
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
				if (fals1.size() == 0 && fals2.size() == 0) {
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
						fal.setReceiver_area(addr.getArea().getParent()
								.getParent().getAreaName()
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
						code = 100;
						json.put("order_id", fal.getId());
					}
				} else {
					code = -100;
				}
			}
		}
		json.put("code", code);
		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * web详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/free_introduce.htm")
	public ModelAndView free_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("app/simple_goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoods obj = this.freegoodsService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj.getFree_details());
		return mv;
	}

	private void send_json(String json, HttpServletResponse response) {
		System.out.println("json:" + json);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
