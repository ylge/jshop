package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
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
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.ExpressCompanyQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.kuaidi100.domain.query.ExpressInfoQueryObject;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 商城平台快递公司管理，通过快递公司代码查询对应的订单信息数据
 * 
 * @since V1.3
 * @author www.iskyshop.com erikzhang
 * 
 */
@Controller
public class ExpressCompanyManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private OrderFormTools orderFormTools;

	/**
	 * 系统集成“快递100”数据接口，支持多种快递在线查询显示，需要向“快递100”申请id后才可以使用
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "快递设置", value = "/admin/set_kuaidi.htm*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping("/admin/set_kuaidi.htm")
	public ModelAndView set_kuaidi(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_kuaidi.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param kuaidi_id
	 * @return
	 */
	@SecurityMapping(title = "保存快递设置", value = "/admin/set_kuaidi_save.htm*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping("/admin/set_kuaidi_save.htm")
	public ModelAndView set_kuaidi_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig config = null;
		if (id.equals("")) {
			config = wf.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.configService.save(config);
		} else {
			this.configService.update(config);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "快递设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_kuaidi.htm");
		return mv;
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
	@SecurityMapping(title = "快递公司列表", value = "/admin/express_company_list.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_list.htm")
	public ModelAndView express_company_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ExpressCompanyQueryObject qo = new ExpressCompanyQueryObject(
				currentPage, mv, "company_sequence", "asc");
		IPageList pList = this.expressCompanyService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/expresscompany_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司添加", value = "/admin/express_company_add.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_add.htm")
	public ModelAndView express_company_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * expresscompany编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "快递公司编辑", value = "/admin/express_company_edit.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_edit.htm")
	public ModelAndView express_company_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ExpressCompany expresscompany = this.expressCompanyService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", expresscompany);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * expresscompany保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "快递公司保存", value = "/admin/express_company_save.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_save.htm")
	public ModelAndView express_company_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		ExpressCompany expresscompany = null;
		if (id.equals("")) {
			expresscompany = wf.toPo(request, ExpressCompany.class);
			expresscompany.setAddTime(new Date());
		} else {
			ExpressCompany obj = this.expressCompanyService.getObjById(Long
					.parseLong(id));
			expresscompany = (ExpressCompany) wf.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "express_template";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(request, "company_template_acc",
					saveFilePathName, "", null);
			if (map.get("fileName") != "") {
				String company_template = uploadFilePath + "/express_template/"
						+ CommUtil.null2String(map.get("fileName"));
				System.out.println(company_template);
				expresscompany.setCompany_template(company_template);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.expressCompanyService.save(expresscompany);
		} else
			this.expressCompanyService.update(expresscompany);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/express_company_list.htm");
		mv.addObject("op_title", "保存快递公司成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/express_company_add.htm?currentPage=" + currentPage);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司删除", value = "/admin/express_company_del.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_del.htm")
	public String express_company_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ExpressCompany ec = this.expressCompanyService.getObjById(Long
						.parseLong(id));
				this.expressCompanyService.delete(Long.parseLong(id));
			}
		}
		return "redirect:express_company_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "快递公司Ajax更新数据", value = "/admin/express_company_ajax.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_ajax.htm")
	public void express_company_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		ExpressCompany obj = this.expressCompanyService.getObjById(Long
				.parseLong(id));
		Field[] fields = ExpressCompany.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.expressCompanyService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/admin/express_company_mark.htm")
	public void express_company_mark(HttpServletRequest request,
			HttpServletResponse response, String company_mark, String id) {
		Map params = new HashMap();
		params.put("company_mark", company_mark.trim());
		params.put("id", CommUtil.null2Long(id));
		List<ExpressCompany> ecs = this.expressCompanyService
				.query("select obj from ExpressCompany obj where obj.company_mark=:company_mark and obj.id!=:id",
						params, -1, -1);
		boolean ret = true;
		if (ecs.size() > 0) {
			ret = false;
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

	@SecurityMapping(title = "运单模板编辑", value = "/admin/express_company_template_edit.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_template_edit.htm")
	public ModelAndView express_company_template_edit(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_template_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ExpressCompany obj = this.expressCompanyService.getObjById(CommUtil
					.null2Long(id));
			Map offset_map = Json.fromJson(Map.class,
					CommUtil.null2String(obj.getCompany_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "运单模板打印测试", value = "/admin/express_company_template_print.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_template_print.htm")
	public ModelAndView express_company_template_print(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_template_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ExpressCompany expresscompany = this.expressCompanyService
					.getObjById(Long.parseLong(id));
			Map offset_map = Json.fromJson(Map.class, CommUtil
					.null2String(expresscompany.getCompany_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", expresscompany);
		}
		return mv;
	}

	@SecurityMapping(title = "运单模板保存", value = "/admin/express_company_template_print.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_template_save.htm")
	public void express_company_template_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ExpressCompany obj = this.expressCompanyService.getObjById(CommUtil
				.null2Long(id));
		boolean ret = true;
		if (obj != null) {
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
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			obj.setCompany_template_offset(Json.toJson(map,
					JsonFormat.compact()));
			System.out.println("map:"+map);
			this.expressCompanyService.update(obj);
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

	@SecurityMapping(title = "运单模板保存成功", value = "/admin/express_company_template_success.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_template_success.htm")
	public ModelAndView express_company_template_success(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/express_company_list.htm");
		mv.addObject("op_title", "快递运单模板保存成功");
		return mv;
	}

	@SecurityMapping(title = "运单模板加载", value = "/admin/express_company_template_load.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_template_load.htm")
	public ModelAndView express_company_template_load(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_template_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ExpressCompany expresscompany = this.expressCompanyService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", expresscompany);
		}
		return mv;
	}

	@SecurityMapping(title = "快递推送列表", value = "/admin/express_info_list.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_info_list.htm")
	public ModelAndView express_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_express_id, String order_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_info_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressInfoQueryObject qo = new ExpressInfoQueryObject(currentPage, mv,
				"addTime", "desc");
		if (!CommUtil.null2String(order_express_id).equals("")) {
			qo.addQuery("obj.order_express_id", new SysMap("order_express_id",
					CommUtil.null2String(order_express_id)), "=");
		}
		if (!CommUtil.null2String(order_status).equals("")) {
			qo.addQuery(
					"obj.order_status",
					new SysMap("order_status", CommUtil.null2Int(order_status)),
					"=");
		}
		IPageList pList = this.expressInfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_express_id", order_express_id);
		mv.addObject("order_status", order_status);
		return mv;
	}

	@SecurityMapping(title = "快递推送详情", value = "/admin/express_info_view.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_info_view.htm")
	public ModelAndView express_info_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_info_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("obj",
				this.expressInfoService.getObjById(CommUtil.null2Long(id)));
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}
}