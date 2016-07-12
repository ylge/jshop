package com.iskyshop.manage.admin.action;

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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.query.ExpressCompanyCommonQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.seller.tools.TransportTools;

/**
 * 
 * <p>
 * Title: ExpressCompanyCommonManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 平台自营中的常用物流管理，用来处理自营中的常用信息，包括常用物流配置，常用物流模板设置，默认物流设置，常用物流模板打印偏移量配置等等内容
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
 * @date 2014-11-19
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SelfExpressCommonManageAction {
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
	private TransportTools transportTools;

	@SecurityMapping(title = "常用物流配置", value = "/admin/ecc_set.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_set.htm")
	public ModelAndView ecc_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_set.html",
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

	@SecurityMapping(title = "常用物流配置", value = "/admin/ecc_save.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_save.htm")
	public ModelAndView ecc_save(HttpServletRequest request,
			HttpServletResponse response, String ids) {
		ModelAndView mv = null;
		String[] ec_ids = ids.split(",");
		Map params = new HashMap();
		params.put("ecc_type", 1);
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
				.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type",
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
				params.put("ecc_type", 1);
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_ec_id=:ecc_ec_id and  obj.ecc_type=:ecc_type",
								params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					this.expressCompanyCommonService.delete(ecc.getId());
				}
				ExpressCompany ec = this.expressCompanyService
						.getObjById(CommUtil.null2Long(ec_id));
				ExpressCompanyCommon ecc = new ExpressCompanyCommon();
				ecc.setAddTime(new Date());
				ecc.setEcc_code(ec.getCompany_mark());
				ecc.setEcc_ec_id(ec.getId());
				ecc.setEcc_name(ec.getCompany_name());
				ecc.setEcc_template(ec.getCompany_template());
				ecc.setEcc_template_heigh(ec.getCompany_template_heigh());
				ecc.setEcc_template_width(ec.getCompany_template_width());
				ecc.setEcc_template_offset(ec.getCompany_template_offset());
				ecc.setEcc_type(1);
				ecc.setEcc_ec_type(ec.getCompany_type());
				this.expressCompanyCommonService.save(ecc);
			}
		}
		mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/ecc_list.htm");
		return mv;

	}

	@SecurityMapping(title = "常用物流列表", value = "/admin/ecc_list.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_list.htm")
	public ModelAndView ecc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommonQueryObject qo = new ExpressCompanyCommonQueryObject(
				currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.ecc_type", new SysMap("ecc_type", 1), "=");
		qo.setPageSize(25);
		IPageList pList = this.expressCompanyCommonService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	@SecurityMapping(title = "设置为默认物流", value = "/admin/ecc_default.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_default.htm")
	public void ecc_default(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (!CommUtil.null2String(id).equals("") && obj.getEcc_type() == 1) {
			Map params = new HashMap();
			params.put("ecc_default", 1);
			params.put("ecc_type", 1);
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
					.query("select obj from ExpressCompanyCommon obj where obj.ecc_default=:ecc_default and obj.ecc_type=:ecc_type",
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

	@SecurityMapping(title = "取消默认物流", value = "/admin/ecc_default_cancle.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_default_cancle.htm")
	public void ecc_default_cancle(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (!CommUtil.null2String(id).equals("") && obj.getEcc_type() == 1) {
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

	@SecurityMapping(title = "常用物流配置", value = "/admin/ecc_print_view.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_print_view.htm")
	public ModelAndView ecc_print_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_print_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
			Map offset_map = Json.fromJson(Map.class,
					CommUtil.null2String(obj.getEcc_template_offset()));
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new JModelAndView("admin/blue/error.html",
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

	@SecurityMapping(title = "常用物流打印偏移量设置", value = "/admin/ecc_print_set.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_print_set.htm")
	public ModelAndView ecc_print_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_print_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
			Map offset_map = Json.fromJson(Map.class,
					obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new JModelAndView("admin/blue/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法设置");
			} else {
				mv.addObject("obj", obj);
				mv.addObject("offset_map", offset_map);
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "常用物流打印偏移量配置保存", value = "/admin/ecc_print_set_save.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_print_set_save.htm")
	public ModelAndView ecc_print_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String left_offset,
			String top_offset) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
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
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/ecc_list.htm");
		mv.addObject("op_title", "运费打印模板偏移量保存成功");
		return mv;

	}

	@SecurityMapping(title = "自建物流模板", value = "/admin/ecc_create.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_create.htm")
	public ModelAndView ecc_create(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_create.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getEcc_type() == 1) {
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

	@SecurityMapping(title = "自建物流模板保存", value = "/admin/ecc_template_save.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_template_save.htm")
	public ModelAndView ecc_template_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ecc_template_width, String ecc_template_heigh) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(Long.parseLong(id));
		if (obj != null && obj.getEcc_type() == 1) {
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
			ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm");
			mv.addObject("op_title", "自建物流模板保存成功");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm");
			mv.addObject("op_title", "物流参数错误，无法设置");
			return mv;
		}
	}

	@SecurityMapping(title = "绑定系统默认物流", value = "/admin/ecc_bind_defalut_template.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_bind_defalut_template.htm")
	public ModelAndView ecc_bind_defalut_template(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(Long.parseLong(id));
		if (obj != null && obj.getEcc_type() == 1) {
			ExpressCompany ec = this.expressCompanyService.getObjById(obj
					.getEcc_ec_id());
			obj.setEcc_type(1);
			obj.setEcc_code(ec.getCompany_mark());
			obj.setEcc_name(ec.getCompany_name());
			obj.setEcc_template(ec.getCompany_template());
			obj.setEcc_template_heigh(ec.getCompany_template_heigh());
			obj.setEcc_template_width(ec.getCompany_template_width());
			obj.setEcc_template_offset(ec.getCompany_template_offset());
			obj.setEcc_from_type(0);// 恢复为系统默认模板
			obj.setEcc_ec_type(ec.getCompany_type());
			this.expressCompanyCommonService.update(obj);
			ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm?currentPage=" + currentPage);
			mv.addObject("op_title", "恢复系统默认模板成功");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm?currentPage=" + currentPage);
			mv.addObject("op_title", "物流参数错误，无法设置");
			return mv;
		}

	}

	@SecurityMapping(title = "自建物流模板保存", value = "/admin/ecc_design.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_design.htm")
	public ModelAndView ecc_design(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ecc_design.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getEcc_type() == 1) {
			Map offset_map = Json.fromJson(Map.class,
					CommUtil.null2String(obj.getEcc_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/ecc_list.htm?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "自建物流模板保存", value = "/admin/ecc_design_save.htm*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping("/admin/ecc_design_save.htm")
	public void ecc_design_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(id));
		boolean ret = true;
		if (obj != null && obj.getEcc_type() == 1) {
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
