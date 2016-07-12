package com.iskyshop.module.app.manage.seller.action;

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

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.seller.tools.TransportTools;

/**
 * 
 * <p>
 * Title: AppSellerExpressViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商家发货相关（常用物流，发货地址）设置
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-3-30
 * 
 * @version 1.0
 */
@Controller
public class AppSellerExpressViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;

	/**
	 * 常用快递公司配置
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/seller/ecc_set.htm")
	public void ecc_set(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("company_status", 0);
		List<ExpressCompany> ecs = this.expressCompanyService
				.query("select obj from ExpressCompany obj where obj.company_status=:company_status order by obj.company_sequence asc",
						params, -1, -1);
		List list = new ArrayList();
		for (ExpressCompany expressCompany : ecs) {
			Map map = new HashMap();
			map.put("id", expressCompany.getId());
			map.put("company_name", expressCompany.getCompany_name());

			params.clear();
			params.put("ecc_type", 0);
			params.put("ecc_store_id", store.getId());
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
					.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
							params, -1, -1);
			int ret = 0;
			int ecc_default = 0;
			for (ExpressCompanyCommon ecc : eccs) {
				if (ecc.getEcc_ec_id().equals(expressCompany.getId())) {
					ret = 1;
					if (ecc.getEcc_default() == 1) {
						ecc_default = 1;
					}
				}
			}
			map.put("ecc_common", ret);
			map.put("ecc_default", ecc_default);
			list.add(map);
		}
		json_map.put("express_company_list", list);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 常用物流保存
	 * 
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping("/app/seller/ecc_save.htm")
	public void ecc_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String ids) {
		Map json_map = new HashMap();
		String[] ec_ids = ids.split(",");
		Map params = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		params.put("ecc_type", 0);
		params.put("ecc_store_id", store.getId());
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
				.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
						params, -1, -1);

		for (ExpressCompanyCommon ecc : eccs) {// 首先删除邮件保存但是此次没有勾选的常用快递物流
			boolean delete = true;
			for (String ec_id : ec_ids) {
				if (!CommUtil.null2String(ec_id).equals("")) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(ec_id))) {
						delete = false;
					}
				}
			}
			if (delete) {
				this.expressCompanyCommonService.delete(ecc.getId());
			}
		}
		for (String ec_id : ec_ids) {
			if (!CommUtil.null2String(ec_id).equals("")) {
				params.clear();
				params.put("ecc_ec_id", CommUtil.null2Long(ec_id));
				params.put("ecc_type", 0);
				params.put("ecc_store_id", store.getId());
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_ec_id=:ecc_ec_id and  obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
								params, -1, -1);
				if (eccs.size() == 0) {
					ExpressCompany ec = this.expressCompanyService
							.getObjById(CommUtil.null2Long(ec_id));
					ExpressCompanyCommon ecc = new ExpressCompanyCommon();
					ecc.setAddTime(new Date());
					ecc.setEcc_code(ec.getCompany_mark());
					ecc.setEcc_ec_id(ec.getId());
					ecc.setEcc_name(ec.getCompany_name());
					ecc.setEcc_store_id(store.getId());
					ecc.setEcc_template(ec.getCompany_template());
					ecc.setEcc_template_heigh(ec.getCompany_template_heigh());
					ecc.setEcc_template_width(ec.getCompany_template_width());
					ecc.setEcc_template_offset(ec.getCompany_template_offset());
					ecc.setEcc_type(0);
					ecc.setEcc_ec_type(ec.getCompany_type());
					this.expressCompanyCommonService.save(ecc);
				}
			}
		}
		json_map.put("ret", 100);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 设置默认快递公司
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 */
	@RequestMapping("/app/seller/ecc_default.htm")
	public void ecc_default(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id) {
		boolean ret = true;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("ecc_default", 0);
		params.put("ecc_type", 0);
		params.put("ecc_ec_id", CommUtil.null2Long(id));
		params.put("ecc_store_id", store.getId());
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
				.query("select obj from ExpressCompanyCommon obj where obj.ecc_default=:ecc_default and obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id and obj.ecc_ec_id=:ecc_ec_id",
						params, -1, -1);
		if (eccs.size() > 0) {
			ExpressCompanyCommon obj = eccs.get(0);
			if (!CommUtil.null2String(id).equals("")
					&& obj.getEcc_store_id().equals(store.getId())) {
				params.clear();
				params.put("ecc_default", 1);
				params.put("ecc_type", 0);
				params.put("ecc_store_id", store.getId());
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_default=:ecc_default and obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
								params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					ecc.setEcc_default(0);
					this.expressCompanyCommonService.update(ecc);
				}
				obj.setEcc_default(1);
				ret = this.expressCompanyCommonService.update(obj);
			} else {
				ret = false;
			}
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 取消默认快递公司
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 */
	@RequestMapping("/app/seller/ecc_default_cancle.htm")
	public void ecc_default_cancle(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id) {
		boolean ret = true;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("ecc_ec_id", CommUtil.null2Long(id));
		params.put("ecc_store_id", store.getId());
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
				.query("select obj from ExpressCompanyCommon obj where  obj.ecc_store_id=:ecc_store_id and obj.ecc_ec_id=:ecc_ec_id",
						params, -1, -1);
		if (eccs.size() > 0) {
			ExpressCompanyCommon obj = eccs.get(0);
			obj.setEcc_default(0);
			ret = this.expressCompanyCommonService.update(obj);
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

	private void send_json(String json, HttpServletResponse response) {
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
