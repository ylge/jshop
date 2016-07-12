package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.ShipTools;

/**
 * 
 * 
 * <p>
 * Title: ReturnSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商品退货管理，查看商品的退货申请。以及对退货的一些操作。
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
 * @date 2014年5月15日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class ReturnSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private ShipTools ShipTools;

	/**
	 * return列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "退货列表", value = "/seller/return.htm*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping("/seller/return.htm")
	public ModelAndView seller_return(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,
			String return_service_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/return.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(
				currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 1), "=");
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
		}
		if (name != null && !name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + name
					+ "%"), "like");
		}
		if (return_service_id != null && !return_service_id.equals("")) {
			qo.addQuery("obj.return_service_id", new SysMap(
					"return_service_id", return_service_id), "=");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReturnGoodsLog.class, mv);
		IPageList pList = this.returngoodslogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);
		mv.addObject("return_service_id", return_service_id);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("store", user.getStore());
		return mv;
	}

	/**
	 * return_check查看
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "退货列表查看", value = "/seller/return_check.htm*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping("/seller/return_check.htm")
	public ModelAndView return_check(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/return_check.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returngoodslogService.getObjById(CommUtil
				.null2Long(id));
		if (obj.getGoods_return_status().equals("7")) {
			TransInfo transInfo = this.ShipTools.query_Ordership_getData(CommUtil
					.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
			Map map = Json
					.fromJson(HashMap.class, obj.getReturn_express_info());
			mv.addObject("express_company_name",
					map.get("express_company_name"));
		}
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * return_check_save保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "退货列表查看保存", value = "/seller/return_check_save.htm*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping("/seller/return_check_save.htm")
	public void return_check_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String goods_return_status,
			String self_address) {
		Map json = new HashMap();
		json.put("op_title", "审核为未通过");
		ReturnGoodsLog obj = this.returngoodslogService.getObjById(Long
				.parseLong(id));
		obj.setGoods_return_status(goods_return_status);
		obj.setSelf_address(self_address);
		this.returngoodslogService.update(obj);
		User user = userService.getObjById(obj.getUser_id());
		String msg_content = "订单号：" + obj.getReturn_service_id()
				+ "退货申请审核未通过，请在'退货/退款'-'查看返修/退换记录'中提交退货物流信息。";
		if (goods_return_status.equals("6")) {
			msg_content = "订单号：" + obj.getReturn_service_id()
					+ "退货申请审核通过，请在'退货/退款'-'查看返修/退换记录'中提交退货物流信息。";

			OrderForm return_of = this.orderformService.getObjById(obj
					.getReturn_order_id());
			List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString()
						.equals(CommUtil.null2String(obj.getGoods_id()))) {
					m.put("goods_return_status", 6);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			return_of.setGoods_info(Json.toJson(new_maps));
			this.orderformService.update(return_of);
			json.put("op_title", "审核为通过");
		}
		// 发送系统站内信
		Message msg = new Message();
		msg.setAddTime(new Date());
		msg.setStatus(0);
		msg.setType(0);
		msg.setContent(msg_content);
		msg.setFromUser(SecurityUserHolder.getCurrentUser());
		msg.setToUser(user);
		this.messageService.save(msg);
		json.put("ret", true);
		json.put("url", CommUtil.getURL(request)
				+ "/seller/return.htm?currentPage=" + currentPage);
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * self_return_confirm保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "确认退货收货", value = "/seller/return_confirm.htm*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping("/seller/return_confirm.htm")
	public ModelAndView return_confirm(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ReturnGoodsLog obj = this.returngoodslogService.getObjById(Long
				.parseLong(id));
		obj.setGoods_return_status("10");
		this.returngoodslogService.update(obj);
		OrderForm return_of = this.orderformService.getObjById(obj
				.getReturn_order_id());
		List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of
				.getGoods_info());
		List<Map> new_maps = new ArrayList<Map>();
		Map gls = new HashMap();
		for (Map m : maps) {
			if (m.get("goods_id").toString()
					.equals(CommUtil.null2String(obj.getGoods_id()))) {
				m.put("goods_return_status", 8);
				gls.putAll(m);
			}
			new_maps.add(m);
		}
		return_of.setGoods_info(Json.toJson(new_maps));
		this.orderformService.update(return_of);
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "确认退货成功!");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/return.htm");
		return mv;
	}

	public void return_json(String json, HttpServletResponse response) {
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
