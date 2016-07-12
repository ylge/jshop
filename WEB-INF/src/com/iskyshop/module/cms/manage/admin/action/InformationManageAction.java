package com.iskyshop.module.cms.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GoodsBrandQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.GoodsFloorTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.module.circle.domain.CircleClass;
import com.iskyshop.module.circle.domain.query.CircleQueryObject;
import com.iskyshop.module.circle.service.ICircleClassService;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.view.tools.CircleViewTools;
import com.iskyshop.module.cms.domain.CmsIndexTemplate;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.domain.query.CmsIndexTemplateQueryObject;
import com.iskyshop.module.cms.domain.query.InformationQueryObject;
import com.iskyshop.module.cms.service.ICmsIndexTemplateService;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationService;
import com.iskyshop.module.cms.view.tools.CmsTools;

/**
 * 
 * <p>
 * Title: InformationManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯管理；审核，发布，
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
 * @date 2014-12-4
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class InformationManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IInformationClassService informationClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private ICmsIndexTemplateService cmsIndexTemplateService;
	@Autowired
	private GoodsFloorTools gf_tools;
	@Autowired
	private CmsTools cmsTools;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IFreeGoodsService freeGoodsService;
	@Autowired
	private IFreeClassService freeClassService;
	@Autowired
	private ICircleClassService circleClassService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private CircleViewTools circleViewTools;

	/**
	 * 资讯首页头部编辑列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯首页头部编辑列表", value = "/admin/information_head_list.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_head_list.htm")
	public ModelAndView information_head_list(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String title, String author, String classid) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_head_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv,
				"sequence,addTime", "desc");
		qo.addQuery("obj.status", new SysMap("status", 20), "=");
		if (title != null && !title.equals("")) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"),
					"LIKE");
			mv.addObject("title", title);
		}
		if (author != null && !author.equals("")) {
			qo.addQuery("obj.author", new SysMap("author", "%" + author + "%"),
					"LIKE");
			mv.addObject("author", author);
		}
		if (classid != null && !classid.equals("")) {
			Map map = new HashMap();
			map.put("id", CommUtil.null2Long(classid));
			List<InformationClass> informationClasses = this.informationClassService
					.query("select obj from InformationClass obj where obj.ic_pid=:id",
							map, -1, -1);
			List<Long> ids = new ArrayList<Long>();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}
			ids.add(CommUtil.null2Long(classid));
			map.clear();
			map.put("ids", ids);
			qo.addQuery("obj.classid in (:ids)", map);
			mv.addObject("classid", classid);
		}
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<InformationClass> infoclass = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null",
						null, -1, -1);
		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	/**
	 * 资讯首页头部编辑保存
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯首页头部编辑保存", value = "/admin/information_head_save.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_head_save.htm")
	public ModelAndView information_head_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = null;
		Information infor = this.informationService.getObjById(CommUtil
				.null2Long(id));
		if (infor != null) {
			Map map = new HashMap();
			map.put("status", 20);
			map.put("recommend", 1);
			List<Information> informations = this.informationService
					.query("select obj from Information obj where obj.status=:status and obj.recommend=:recommend",
							map, -1, -1);
			for (Information information : informations) {
				information.setRecommend(0);
				this.informationService.update(information);
			}
			infor.setRecommend(1);
			this.informationService.update(infor);
			mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/cms_template_list.htm");
			mv.addObject("op_title", "设置首页头部成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/admin/information_head_list.htm?currentPage="
					+ currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/cms_template_list.htm");
			mv.addObject("op_title", "参数错误！");
		}
		return mv;
	}

	// @SecurityMapping(title = "资讯保存首页设置", value =
	// "/admin/information_control_data.htm*", rtype = "admin", rname = "资讯管理",
	// rcode = "information_admin", rgroup = "网站")
	// @RequestMapping("/admin/information_control_data.htm")
	// public void control_data(HttpServletRequest request, HttpServletResponse
	// response,
	// String id,String type,String position)
	// throws ClassNotFoundException {
	//
	// response.setContentType("text/plain");
	// response.setHeader("Cache-Control", "no-cache");
	// response.setCharacterEncoding("UTF-8");
	// PrintWriter writer;
	// try {
	// writer = response.getWriter();
	// writer.print();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	/**
	 * Information列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯列表", value = "/admin/information_list.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_list.htm")
	public ModelAndView information_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new JModelAndView("admin/blue/information_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv,
				"sequence,addTime", "desc");
		qo.addQuery("obj.status", new SysMap("status", 20), "=");
		if (title != null && !title.equals("")) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"),
					"LIKE");
			mv.addObject("title", title);
		}
		if (author != null && !author.equals("")) {
			qo.addQuery("obj.author", new SysMap("author", "%" + author + "%"),
					"LIKE");
			mv.addObject("author", author);
		}
		if (!CommUtil.null2String(classid).equals("")) {
			Map map = new HashMap();
			map.put("id", CommUtil.null2Long(classid));
			List<InformationClass> informationClasses = this.informationClassService
					.query("select obj from InformationClass obj where obj.ic_pid=:id",
							map, -1, -1);
			List<Long> ids = new ArrayList<Long>();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}
			ids.add(CommUtil.null2Long(classid));
			map.clear();
			map.put("ids", ids);
			qo.addQuery("obj.classid in (:ids)", map);
			mv.addObject("classid", classid);
		}
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<InformationClass> infoclass = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null",
						null, -1, -1);
		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	@SecurityMapping(title = "资讯待审核列表", value = "/admin/information_verifylist.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_verifylist.htm")
	public ModelAndView information_verifylist(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_verifylist.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.type", new SysMap("type", 1), "=");
		qo.addQuery("obj.status", new SysMap("status", 10), "=");
		if (title != null && !title.equals("")) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"),
					"LIKE");
			mv.addObject("title", title);
		}
		if (author != null && !author.equals("")) {
			qo.addQuery("obj.author", new SysMap("author", "%" + author + "%"),
					"LIKE");
			mv.addObject("author", author);
		}
		if (classid != null && !classid.equals("")) {
			qo.addQuery("obj.classid",
					new SysMap("classid", CommUtil.null2Long(classid)), "=");
			mv.addObject("classid", classid);
		}
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<InformationClass> infoclass = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null",
						null, -1, -1);
		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	/**
	 * information添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯添加", value = "/admin/information_add.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_add.htm")
	public ModelAndView information_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/information_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<InformationClass> infoclass = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null",
						null, -1, -1);
		mv.addObject("infoclass", infoclass);
		mv.addObject("currentPage", currentPage);
		mv.addObject("imageTools", imageTools);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	/**
	 * information编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯编辑", value = "/admin/information_edit.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_edit.htm")
	public ModelAndView information_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/information_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Information information = this.informationService.getObjById(Long
					.parseLong(id));
			List<InformationClass> infoclass = this.informationClassService
					.query("select obj from InformationClass obj where obj.ic_pid is null",
							null, -1, -1);
			mv.addObject("infoclass", infoclass);
			mv.addObject("obj", information);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
			mv.addObject("imageTools", imageTools);
			mv.addObject("cmsTools", cmsTools);
		}
		return mv;
	}

	/**
	 * information保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯保存", value = "/admin/information_save.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_save.htm")
	public ModelAndView information_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Information information = null;
		if (id.equals("")) {
			information = wf.toPo(request, Information.class);
			information.setAddTime(new Date());
			User user = SecurityUserHolder.getCurrentUser();
			information.setAuthor(user.getUsername());
			information.setAuthor_id(user.getId());
			information.setType(0);
			information.setStatus(20);
		} else {
			Information obj = this.informationService.getObjById(Long
					.parseLong(id));
			information = (Information) wf.toPo(request, obj);
		}

		// 封面图片
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "information_cover";
		Map map = new HashMap();
		try {
			String fileName = "";
			Accessory photo = null;
			if (information.getCover() != null && information.getCover() != 0) {
				photo = this.accessoryService
						.getObjById(information.getCover());
				fileName = photo.getName();
			}
			map = CommUtil.saveFileToServer(request, "cover", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/information_cover");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					information.setCover(photo.getId());
				}
			} else {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/information_cover");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.informationService.save(information);
		} else
			this.informationService.update(information);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存资讯成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "资讯审核", value = "/admin/information_verify.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_verify.htm")
	public ModelAndView information_verify(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_verify.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Information obj = this.informationService.getObjById(Long
					.parseLong(id));
			mv.addObject("className",
					this.informationClassService.getObjById(obj.getClassid())
							.getIc_name());
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("imageTools", imageTools);
		}
		return mv;
	}

	@SecurityMapping(title = "资讯审核保存", value = "/admin/information_verify_save.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_verify_save.htm")
	public String verify_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String failreason) {
		if (id != null && !id.equals("")) {
			Information information = this.informationService.getObjById(Long
					.parseLong(id));
			information.setStatus(CommUtil.null2Int(status));
			if (CommUtil.null2Int(status) == 10) {
				information.setFailreason("");
			} else {
				information.setFailreason(failreason);
			}
			this.informationService.update(information);
		}
		return "redirect:information_verifylist.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯删除", value = "/admin/information_del.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_del.htm")
	public String information_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Information information = this.informationService
						.getObjById(Long.parseLong(id));
				this.informationService.delete(Long.parseLong(id));
			}
		}
		return "redirect:information_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯置顶", value = "/admin/stick.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/stick.htm")
	public String stick(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/information_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Information information = this.informationService.getObjById(Long
					.parseLong(id));
			if (information.getSequence() == -10) {
				information.setSequence(-0);
			} else {
				information.setSequence(-10);
			}
		}
		return "redirect:information_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯ajax", value = "/admin/information_ajax.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Information obj = this.informationService
				.getObjById(Long.parseLong(id));
		Field[] fields = Information.class.getDeclaredFields();
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
		this.informationService.update(obj);
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

	@SecurityMapping(title = "资讯商品", value = "/admin/information_goods.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_goods.htm")
	public ModelAndView information_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime",
				"desc");
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.goods_type", new SysMap("obj_goods_type", 0), "=");
		qo.setPageSize(5);
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = this.genericIds(this.goodsClassService
					.getObjById(CommUtil.null2Long(gc_id)));
			Map paras = new HashMap();
			paras.put("ids", ids);
			qo.addQuery("obj.gc.id in (:ids)", paras);
			mv.addObject("gc_id", gc_id);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		IPageList pList = this.goodsService.list(qo);
		String photo_url = CommUtil.getURL(request)
				+ "/admin/information_goods.htm";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages()));
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "商品图片", value = "/admin/information_goods_imgs.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/information_goods_imgs.htm")
	public ModelAndView information_goods_imgs(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String gc_id, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_goods_imgs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		List list = new ArrayList();
		if (goods.getGoods_main_photo() != null) {
			list.add(goods.getGoods_main_photo());
			list.addAll(goods.getGoods_photos());
		}
		mv.addObject("photos", list);
		mv.addObject("goods_id", goods_id);
		return mv;
	}

	@SecurityMapping(title = "资讯预览", value = "/admin/info_preview.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/info_preview.htm")
	public ModelAndView info_preview(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("/cms/detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information info = this.informationService.getObjById(CommUtil
				.null2Long(id));
		if (info != null) {
			mv.addObject("obj", info);
			Map map = new HashMap();
			map.put("addTime", info.getAddTime());
			List<Information> before = this.informationService
					.query("select obj from Information obj where obj.addTime>:addTime and obj.status=20 order by addTime asc",
							map, 0, 1);
			if (before.size() > 0) {
				mv.addObject("before", before.get(0));
			}
			List<Information> after = this.informationService
					.query("select obj from Information obj where obj.addTime<:addTime and obj.status=20 order by addTime desc",
							map, 0, 1);
			if (after.size() > 0) {
				mv.addObject("after", after.get(0));
			}
			mv.addObject("className",
					this.informationClassService.getObjById(info.getClassid())
							.getIc_name());
			List<InformationClass> infoclass = this.informationClassService
					.query("select obj from InformationClass obj order by ic_sequence asc",
							null, -1, -1);
			mv.addObject("infoclass", infoclass);
			List<Information> hotinfo = this.informationService
					.query("select obj from Information obj where obj.status=20 order by sequence asc",
							null, 0, 5);
			mv.addObject("hotinfo", hotinfo);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，资讯查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/cms/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "资讯首页管理", value = "/admin/cms_template_list.htm*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping("/admin/cms_template_list.htm")
	public ModelAndView cms_template_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		CmsIndexTemplateQueryObject qo = new CmsIndexTemplateQueryObject(
				currentPage, mv, "sequence", "asc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, CmsIndexTemplate.class, mv);
		IPageList pList = this.cmsIndexTemplateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/cms_template_list.htm", "", params, pList, mv);
		Map map = new HashMap();
		map.put("status", 20);
		map.put("recommend", 1);
		List<Information> informations = this.informationService
				.query("select obj from Information obj where obj.status=:status and obj.recommend=:recommend",
						map, -1, -1);
		if (informations.size() > 0) {
			mv.addObject("information", informations.get(0));
		}
		return mv;
	}

	/**
	 * cmsindextemplate编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "楼层编辑", value = "/admin/cms_template_edit.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_edit.htm")
	public ModelAndView cms_template_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/cms_template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", cmsindextemplate);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	@SecurityMapping(title = "楼层保存", value = "/admin/cms_template_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_save.htm")
	public ModelAndView cms_template_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		CmsIndexTemplate cmsindextemplate = null;
		if (id.equals("")) {
			cmsindextemplate = wf.toPo(request, CmsIndexTemplate.class);
			cmsindextemplate.setAddTime(new Date());
		} else {
			CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
					.parseLong(id));
			cmsindextemplate = (CmsIndexTemplate) wf.toPo(request, obj);
		}

		if (id.equals("")) {
			this.cmsIndexTemplateService.save(cmsindextemplate);
		} else
			this.cmsIndexTemplateService.update(cmsindextemplate);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存楼层成功成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 资讯首页楼层添加
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯首页楼层添加", value = "/admin/cms_template_add.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_add.htm")
	public ModelAndView cms_template_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/cms_template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "资讯首页楼层添加", value = "/admin/cms_template_draw.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_draw.htm")
	public ModelAndView cms_template_draw(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_draw.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", cmsindextemplate);
			mv.addObject("gf_tools", this.gf_tools);
			mv.addObject("imageTools", imageTools);
			mv.addObject("circleViewTools", circleViewTools);
		}
		return mv;
	}

	@SecurityMapping(title = "资讯首页楼层删除", value = "/admin/cms_template_del.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_del.htm")
	public String cms_template_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
						.getObjById(Long.parseLong(id));
				this.cmsIndexTemplateService.delete(Long.parseLong(id));
			}
		}
		return "redirect:cms_template_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/admin/cms_template_class.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_class.htm")
	public ModelAndView cms_template_class(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_class.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
				.getObjById(Long.parseLong(id));
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/admin/cms_template_class_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_class_load.htm")
	public ModelAndView cms_template_class_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_class_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		mv.addObject("gc", gc);
		return mv;
	}

	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/admin/cms_template_class_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_class_save.htm")
	public String cms_template_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		List gf_gc_list = new ArrayList();
		String[] id_list = ids.split(",pid:");
		for (String t_id : id_list) {
			String[] c_id_list = t_id.split(",");
			Map map = new HashMap();
			for (int i = 0; i < c_id_list.length; i++) {
				String c_id = c_id_list[i];
				if (c_id.indexOf("cid") < 0) {
					map.put("pid", c_id);
				} else {
					map.put("gc_id" + i, c_id.substring(4));
				}
			}
			map.put("gc_count", c_id_list.length - 1);
			if (!map.get("pid").toString().equals(""))
				gf_gc_list.add(map);
		}
		// System.out.println(Json.toJson(gf_gc_list, JsonFormat.compact()));
		obj.setFloor_info2(Json.toJson(gf_gc_list, JsonFormat.compact()));
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板商品", value = "/admin/cms_template_goods.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_goods.htm")
	public ModelAndView cms_template_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String count) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
				.getObjById(Long.parseLong(id));
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		return mv;
	}

	@SecurityMapping(title = "楼层模板商品", value = "/admin/cms_template_goods_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_goods_load.htm")
	public ModelAndView cms_template_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String page, String module_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new JModelAndView("admin/blue/" + page + ".html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime",
				"desc");
		qo.setPageSize(14);
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = this.genericIds(this.goodsClassService
					.getObjById(CommUtil.null2Long(gc_id)));
			Map paras = new HashMap();
			paras.put("ids", ids);
			qo.addQuery("obj.gc.id in (:ids)", paras);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_goods_load.htm", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name, pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}

	@SecurityMapping(title = "楼层模板商品", value = "/admin/cms_template_goods_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_goods_save.htm")
	public String cms_template_goods_save(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		String[] id_list = ids.split(",");
		Map map = new HashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		// System.out.println(Json.toJson(map, JsonFormat.compact()));
		if ("goods-class".equals(obj.getType())) {
			obj.setFloor_info1(Json.toJson(map, JsonFormat.compact()));
		}
		if ("goods".equals(obj.getType())) {
			obj.setFloor_info1(Json.toJson(map, JsonFormat.compact()));
		}
		if ("info-info-goods-brand".equals(obj.getType())) {
			obj.setFloor_info3(Json.toJson(map, JsonFormat.compact()));
		}
		if ("goods-free-circle".equals(obj.getType())) {
			obj.setFloor_info1(Json.toJson(map, JsonFormat.compact()));
		}
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板资讯", value = "/admin/cms_template_info.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_info.htm")
	public ModelAndView cms_template_info(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String count, String type) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
				.getObjById(Long.parseLong(id));
		List<InformationClass> ics = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null order by obj.ic_sequence asc",
						null, -1, -1);
		mv.addObject("ics", ics);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		mv.addObject("type", type);
		mv.addObject("imageTools", imageTools);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	@SecurityMapping(title = "楼层模板资讯", value = "/admin/cms_template_info_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_info_load.htm")
	public ModelAndView cms_template_info_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ic_id,
			String info_name, String page, String module_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_info_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new JModelAndView("admin/blue/" + page + ".html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.setPageSize(14);
		if (!CommUtil.null2String(ic_id).equals("")) {
			Set<Long> ids = this.genericIds(this.informationClassService
					.getObjById(CommUtil.null2Long(ic_id)));
			Map paras = new HashMap();
			paras.put("ids", ids);
			qo.addQuery("obj.classid in (:ids)", paras);
		}
		if (!CommUtil.null2String(info_name).equals("")) {
			qo.addQuery("obj.title",
					new SysMap("title", "%" + info_name + "%"), "like");
		}
		qo.addQuery("obj.status", new SysMap("status", 20), "=");
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_info_load.htm", "", "&ic_id=" + ic_id
				+ "&info_name=" + info_name, pList, mv);
		mv.addObject("module_id", module_id);
		mv.addObject("imageTools", imageTools);
		return mv;
	}

	@SecurityMapping(title = "楼层模板资讯", value = "/admin/cms_template_info_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_info_save.htm")
	public String cms_template_info_save(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids, String type) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		String[] id_list = ids.split(",");
		Map map = new HashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("info" + i, id_list[i]);
			}
		}
		// System.out.println(Json.toJson(map, JsonFormat.compact()));
		if ("floor_info1".equals(type)) {
			obj.setFloor_info1(Json.toJson(map, JsonFormat.compact()));
		}
		if ("floor_info2".equals(type)) {
			obj.setFloor_info2(Json.toJson(map, JsonFormat.compact()));
		}
		if ("floor_info3".equals(type)) {
			obj.setFloor_info3(Json.toJson(map, JsonFormat.compact()));
		}
		if ("floor_info4".equals(type)) {
			obj.setFloor_info4(Json.toJson(map, JsonFormat.compact()));
		}
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板品牌", value = "/admin/cms_template_brand.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_brand.htm")
	public ModelAndView goods_floor_brand(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_brand.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject("1", mv,
				"sequence", "asc");
		qo.addQuery("obj.audit", new SysMap("audit", 1), "=");
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_brand_load.htm", "", "", pList, mv);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}

	@SecurityMapping(title = "楼层模板品牌保存", value = "/admin/cms_template_brand_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_brand_save.htm")
	public String goods_floor_brand_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		String[] id_list = ids.split(",");
		Map map = new HashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("brand_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info4(Json.toJson(map, JsonFormat.compact()));
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板品牌加载", value = "/admin/cms_template_brand_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_brand_load.htm")
	public ModelAndView cms_template_brand_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_brand_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv,
				"sequence", "asc");
		qo.addQuery("obj.audit", new SysMap("audit", 1), "=");
		if (!CommUtil.null2String(name).equals("")) {
			qo.addQuery("obj.name",
					new SysMap("name", "%" + name.trim() + "%"), "like");
		}
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_brand_load.htm", "",
				"&name=" + CommUtil.null2String(name), pList, mv);
		return mv;
	}

	@SecurityMapping(title = "楼层模板0元试用", value = "/admin/cms_template_free.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_free.htm")
	public ModelAndView cms_template_free(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String count) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_free.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
				.getObjById(Long.parseLong(id));
		List<FreeClass> fcs = this.freeClassService.query(
				"select obj from FreeClass obj  order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("fcs", fcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		return mv;
	}

	@SecurityMapping(title = "楼层模板0元试用", value = "/admin/cms_template_free_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_free_save.htm")
	public String cms_template_free_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		String[] id_list = ids.split(",");
		Map map = new HashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("free_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info2(Json.toJson(map, JsonFormat.compact()));
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板0元试用", value = "/admin/cms_template_free_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_free_load.htm")
	public ModelAndView cms_template_free_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name,
			String page, String fc_id, String free_name, String module_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_free_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new JModelAndView("admin/blue/" + page + ".html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.setPageSize(14);
		if (!CommUtil.null2String(fc_id).equals("")) {
			Map paras = new HashMap();
			paras.put("fc_id", CommUtil.null2Long(fc_id));
			qo.addQuery("obj.class_id=:fc_id", paras);
		}
		if (!CommUtil.null2String(free_name).equals("")) {
			qo.addQuery("obj.free_name", new SysMap("free_name", "%"
					+ free_name + "%"), "like");
		}
		qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
		IPageList pList = this.freeGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_free_load.htm", "", "&fc_id=" + fc_id
				+ "&free_name=" + free_name, pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}

	@SecurityMapping(title = "楼层模板圈子", value = "/admin/cms_template_circle.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_circle.htm")
	public ModelAndView cms_template_circle(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String count) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_circle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CmsIndexTemplate cmsindextemplate = this.cmsIndexTemplateService
				.getObjById(Long.parseLong(id));
		List<CircleClass> ccs = this.circleClassService.query(
				"select obj from CircleClass obj  order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("ccs", ccs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	@SecurityMapping(title = "楼层模板圈子", value = "/admin/cms_template_circle_save.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_circle_save.htm")
	public String cms_template_circle_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.cmsIndexTemplateService.getObjById(Long
				.parseLong(id));
		String[] id_list = ids.split(",");
		Map map = new HashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("circle_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info3(Json.toJson(map, JsonFormat.compact()));
		this.cmsIndexTemplateService.update(obj);
		return "redirect:cms_template_draw.htm?id=" + id;
	}

	@SecurityMapping(title = "楼层模板圈子", value = "/admin/cms_template_circle_load.htm*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping("/admin/cms_template_circle_load.htm")
	public ModelAndView cms_template_circle_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name,
			String page, String cc_id, String circle_name, String module_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/cms_template_circle_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new JModelAndView("admin/blue/" + page + ".html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		CircleQueryObject qo = new CircleQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.setPageSize(14);
		if (!CommUtil.null2String(cc_id).equals("")) {
			Map paras = new HashMap();
			paras.put("cc_id", CommUtil.null2Long(cc_id));
			qo.addQuery("obj.class_id=:cc_id", paras);
		}
		if (!CommUtil.null2String(circle_name).equals("")) {
			qo.addQuery("obj.title", new SysMap("circle_name", "%"
					+ circle_name + "%"), "like");
		}
		qo.addQuery("obj.status", new SysMap("status", 5), "=");
		IPageList pList = this.circleService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/cms_template_free_load.htm", "", "&cc_id=" + cc_id
				+ "&circle_name=" + circle_name, pList, mv);
		mv.addObject("module_id", module_id);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	private Set<Long> genericIds(InformationClass ic) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ic.getId());
		Map params = new HashMap();
		params.put("pid", ic.getId());
		List<InformationClass> ics = this.informationClassService.query(
				"select obj from InformationClass obj where obj.ic_pid=:pid",
				params, -1, -1);
		for (InformationClass child : ics) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}