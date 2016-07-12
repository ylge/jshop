package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ExpressCompanyCommonQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.seller.tools.TransportTools;

/**
 * 
 * <p>
 * Title: ExpressCompanyManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商家快递公司相关管理
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
 * @date 2014-10-29
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class ExpressCompanyCommonSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private TransportTools transportTools;

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司配置", value = "/seller/ecc_set.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_set.htm")
	public ModelAndView ecc_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("company_status", 0);
		List<ExpressCompany> ecs = this.expressCompanyService
				.query("select obj from ExpressCompany obj where obj.company_status=:company_status order by obj.company_sequence asc",
						params, -1, -1);
		mv.addObject("ecs", ecs);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 */
	@SecurityMapping(title = "常用快递公司列表", value = "/seller/ecc_list.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_save.htm")
	public void ecc_save(HttpServletRequest request,
			HttpServletResponse response, String ids) {
		boolean ret = true;
		String[] ec_ids = ids.split(",");
		Map params = new HashMap();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
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

	/**
	 * ExpressCompany列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司列表", value = "/seller/ecc_list.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_list.htm")
	public ModelAndView ecc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommonQueryObject qo = new ExpressCompanyCommonQueryObject(
				currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.ecc_type", new SysMap("ecc_type", 0), "=");
		qo.addQuery("obj.ecc_store_id",
				new SysMap("ecc_store_id", store.getId()), "=");
		qo.setPageSize(25);
		IPageList pList = this.expressCompanyCommonService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * ExpressCompany删除
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司删除", value = "/seller/ecc_delete.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_delete.htm")
	public String ecc_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ExpressCompanyCommon ecc = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (ecc.getEcc_store_id().equals(store.getId())) {
			this.expressCompanyCommonService.delete(CommUtil.null2Long(id));
		}
		return "redirect:ecc_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "设置默认快递公司", value = "/seller/ecc_default.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_default.htm")
	public void ecc_default(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (!CommUtil.null2String(id).equals("")
				&& obj.getEcc_store_id().equals(store.getId())) {
			Map params = new HashMap();
			params.put("ecc_default", 1);
			params.put("ecc_type", 0);
			params.put("ecc_store_id", store.getId());
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
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

	@SecurityMapping(title = "取消默认快递公司", value = "/seller/ecc_default_cancle.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_default_cancle.htm")
	public void ecc_default_cancle(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (!CommUtil.null2String(id).equals("")
				&& obj.getEcc_store_id().equals(store.getId())) {
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

	@SecurityMapping(title = "运费单打印测试", value = "/seller/ecc_print_view.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_print_view.htm")
	public ModelAndView ecc_print_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_print_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			Map offset_map = Json.fromJson(Map.class,
					obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法打印");
			} else {
				mv.addObject("offset_map", offset_map);
				mv.addObject("obj", obj);
			}
		}
		return mv;
	}

	@SecurityMapping(title = "运费单打印设置", value = "/seller/ecc_print_set.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_print_set.htm")
	public ModelAndView ecc_print_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_print_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			Map offset_map = Json.fromJson(Map.class,
					obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法设置");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/ecc_list.htm");
			} else {
				mv.addObject("obj", obj);
				mv.addObject("offset_map", offset_map);
			}
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "快递单偏移量保存", value = "/seller/ecc_print_set_save.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_print_set_save.htm")
	public void ecc_print_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String left_offset,
			String top_offset) {
		boolean ret = true;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			Map offset_map = Json.fromJson(Map.class,
					obj.getEcc_template_offset());
			if (offset_map.get("receipt_user_left") != null) {
				Iterator it = offset_map.keySet().iterator();
				while (it.hasNext()) {
					String key = CommUtil.null2String(it.next());
					if (key.indexOf("_left") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						System.out.println(key + "  " + value);
						value = value + CommUtil.null2Float(left_offset);
						offset_map.put(key, CommUtil.null2String(value));
					}
					if (key.indexOf("_top") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						System.out.println(key + "  " + value);
						value = value + CommUtil.null2Float(top_offset);
						offset_map.put(key, CommUtil.null2String(value));
					}
				}
				obj.setEcc_template_offset(Json.toJson(offset_map,
						JsonFormat.compact()));
				ret = this.expressCompanyCommonService.update(obj);
			}
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

	@SecurityMapping(title = "自建物流模板配置", value = "/seller/ecc_create.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_create.htm")
	public ModelAndView ecc_create(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_create.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "自建物流模板保存", value = "/seller/ecc_template_save.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_template_save.htm")
	public ModelAndView ecc_template_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ecc_template_width, String ecc_template_heigh) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(Long.parseLong(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "ecc_template";
			Map map = new HashMap();
			try {
				map = CommUtil.saveFileToServer(request, "ecc_template_acc",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					String company_template = uploadFilePath + "/ecc_template/"
							+ CommUtil.null2String(map.get("fileName"));
					obj.setEcc_template(company_template);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			obj.setEcc_from_type(1);// 设置为自建模板
			obj.setEcc_template_offset(null);
			obj.setEcc_template_heigh(CommUtil.null2Int(ecc_template_heigh));
			obj.setEcc_template_width(CommUtil.null2Int(ecc_template_width));
			this.expressCompanyCommonService.update(obj);
			ModelAndView mv = new JModelAndView(
					"user/default/sellercenter/seller_success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm?currentPage=" + currentPage);
			mv.addObject("op_title", "自建物流模板保存成功");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm");
			return mv;
		}
	}

	@SecurityMapping(title = "恢复系统默认物流", value = "/seller/ecc_bind_defalut_template.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_bind_defalut_template.htm")
	public ModelAndView ecc_bind_defalut_template(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(Long.parseLong(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			ExpressCompany ec = this.expressCompanyService.getObjById(obj
					.getEcc_ec_id());
			obj.setEcc_code(ec.getCompany_mark());
			obj.setEcc_name(ec.getCompany_name());
			obj.setEcc_store_id(store.getId());
			obj.setEcc_template(ec.getCompany_template());
			obj.setEcc_template_heigh(ec.getCompany_template_heigh());
			obj.setEcc_template_width(ec.getCompany_template_width());
			obj.setEcc_template_offset(ec.getCompany_template_offset());
			obj.setEcc_from_type(0);// 恢复为系统默认模板
			obj.setEcc_ec_type(ec.getCompany_type());
			this.expressCompanyCommonService.update(obj);
			ModelAndView mv = new JModelAndView(
					"user/default/sellercenter/seller_success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm?currentPage=" + currentPage);
			mv.addObject("op_title", "恢复系统默认模板成功");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html?currentPage="
							+ currentPage, configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm?currentPage=" + currentPage);
			return mv;
		}

	}

	@SecurityMapping(title = "自建物流模板设计", value = "/seller/ecc_design.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_design.htm")
	public ModelAndView ecc_design(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ecc_design.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			Map offset_map = Json.fromJson(Map.class,
					CommUtil.null2String(obj.getEcc_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/ecc_list.htm?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "自建物流模板设计", value = "/seller/ecc_design.htm*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping("/seller/ecc_design_save.htm")
	public void ecc_design_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		boolean ret = true;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null && obj.getEcc_store_id().equals(store.getId())) {
			Map map = new HashMap();
			java.util.Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				if (!paramName.equals("id")
						&& !CommUtil.null2String(value).equals("")
						&& !CommUtil.null2String(value).equals("null")) {
					map.put(paramName, value);
				}
			}
			System.out.println(Json.toJson(map, JsonFormat.compact()));
			obj.setEcc_template_offset(Json.toJson(map, JsonFormat.compact()));
			this.expressCompanyCommonService.update(obj);
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
}