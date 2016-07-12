package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.SubjectQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.SubjectTools;

/**
 * 
 * <p>
 * Title: SubjectManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台专题管理控制器
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
 * @author hezeng
 * 
 * @date 2014-11-11
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SubjectManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private SubjectTools SubjectTools;

	@SecurityMapping(title = "专题列表", value = "/admin/subject_list.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_list.htm")
	public ModelAndView subject_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SubjectQueryObject qo = new SubjectQueryObject(currentPage, mv,
				"sequence", "asc");
		IPageList pList = (IPageList) this.subjectService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "专题添加", value = "/admin/subject_list.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_add.htm")
	public ModelAndView subject_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "专题编辑", value = "/admin/subject_edit.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_edit.htm")
	public ModelAndView subject_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Subject subject = this.subjectService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", subject);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	@SecurityMapping(title = "专题保存", value = "/admin/subject_save.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_save.htm")
	public ModelAndView subject_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		WebForm wf = new WebForm();
		Subject subject = null;
		if (id.equals("")) {
			subject = wf.toPo(request, Subject.class);
			subject.setAddTime(new Date());
		} else {
			Subject obj = this.subjectService.getObjById(Long.parseLong(id));
			subject = (Subject) wf.toPo(request, obj);
		}
		// 标识图片
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "subject";
		Map map = new HashMap();
		try {
			String fileName = subject.getBanner() == null ? "" : subject
					.getBanner().getName();
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					subject.setBanner(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = subject.getBanner();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (id.equals("")) {
			this.subjectService.save(subject);
		} else
			this.subjectService.update(subject);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/subject_list.htm");
		mv.addObject("op_title", "专题保存成功");
		return mv;
	}

	@SecurityMapping(title = "专题删除", value = "/admin/subject_del.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_del.htm")
	public String subject_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Subject subject = this.subjectService.getObjById(Long
						.parseLong(id));
				this.subjectService.delete(Long.parseLong(id));
			}
		}
		return "redirect:subject_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "专题Ajax更新", value = "/admin/subject_list_ajax.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_list_ajax.htm")
	public void subject_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Subject obj = this.subjectService.getObjById(Long.parseLong(id));
		Field[] fields = Subject.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
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
				if (field.getName().equals("display")) {
					if (obj.getDisplay() == 1) {
						obj.setDisplay(0);
						val = obj.getDisplay();
					} else {
						obj.setDisplay(1);
						val = obj.getDisplay();
					}
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.subjectService.update(obj);
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

	@SecurityMapping(title = "专题设计", value = "/admin/subject_set.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_set.htm")
	public ModelAndView subject_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		if (obj.getSubject_detail() != null) {
			List<Map> objs = (List<Map>) Json.fromJson(obj.getSubject_detail());
			mv.addObject("objs", objs);
		}
		mv.addObject("SubjectTools", SubjectTools);
		return mv;
	}

	@SecurityMapping(title = "专题图片保存", value = "/admin/subject_img_upload.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_img_upload.htm")
	public void subject_img_upload(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		// 品牌标识图片
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "subject";
		Map map = new HashMap();
		String fileName = "";
		String path = CommUtil.getURL(request) + "/";
		Accessory img = null;
		if (img_id != null && !img_id.equals("undefined")) {
			img = this.accessoryService.getObjById(CommUtil.null2Long(img_id));
			fileName = img.getName();
		}
		try {
			map = CommUtil.saveFileToServer(request, "img", saveFilePathName,
					"", null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					List<Map> subject_detail = new ArrayList<Map>();
					if (obj.getSubject_detail() != null
							&& !obj.getSubject_detail().equals("")) {
						subject_detail = (List<Map>) Json.fromJson(obj
								.getSubject_detail());
					}
					Map temp_map = new HashMap();
					temp_map.put("type", "img");
					temp_map.put("id", photo.getId());
					temp_map.put("img_url",
							photo.getPath() + "/" + photo.getName());
					subject_detail.add(temp_map);
					obj.setSubject_detail(Json.toJson(subject_detail,
							JsonFormat.compact()));
					this.subjectService.update(obj);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = img;
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					if (obj.getSubject_detail() != null) {
						List<Map> subject_detail = (List<Map>) Json
								.fromJson(obj.getSubject_detail());
						for (Map temp_map : subject_detail) {// 更新json数据图片路径
							if (CommUtil.null2String(temp_map.get("id"))
									.equals(photo.getId().toString())) {
								temp_map.put("img_url", photo.getPath() + "/"
										+ photo.getName());
								break;
							}
						}
						obj.setSubject_detail(Json.toJson(subject_detail,
								JsonFormat.compact()));
						this.subjectService.update(obj);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * areaInfo:热点区域坐标信息 img_href：整体图片链接,设置专题详情图片链接或者设置热点链接
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param img_id
	 * @param img_href
	 * @param areaInfo
	 */
	@SecurityMapping(title = "专题设置图片链接", value = "/admin/subject_img_href.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_img_href.htm")
	public void subject_img_href(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id,
			String img_href) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> objs = (List<Map>) Json.fromJson(obj.getSubject_detail());
			for (Map temp_map : objs) {
				if (CommUtil.null2String(temp_map.get("type")).equals("img")
						&& CommUtil.null2String(temp_map.get("id")).equals(
								img_id)) {
					if (img_href != null && !img_href.equals("")) {// 设置图片链接
						if (img_href.indexOf("http://")<0) {
							img_href="http://"+img_href;
						}
						temp_map.put("img_href", img_href);
					}
					break;
				}
			}
			obj.setSubject_detail(Json.toJson(objs, JsonFormat.compact()));
			this.subjectService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "专题图片详情删除", value = "/admin/subject_img_dele.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_img_dele.htm")
	public void subject_img_dele(HttpServletRequest request,
			HttpServletResponse response, String id, String dele_id, String type) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = (List<Map>) Json.fromJson(obj
					.getSubject_detail());
			List<Map> temp_maps = new ArrayList<Map>();
			for (Map temp_map : subject_detail) {
				temp_maps.add(temp_map);
				if (CommUtil.null2String(temp_map.get("type")).equals(type)
						&& CommUtil.null2String(temp_map.get("id")).equals(
								dele_id)) {
					temp_maps.remove(temp_map);
				}
			}
			obj.setSubject_detail(Json.toJson(temp_maps, JsonFormat.compact()));
			this.subjectService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param img_id
	 * @param img_href
	 * @param areaInfo
	 */
	@SecurityMapping(title = "专题设置图片热点信息", value = "/admin/subject_img_coords.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_img_coords.htm")
	public void subject_img_coords(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id,
			String coords, String coords_url, String edit) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> objs = (List<Map>) Json.fromJson(obj.getSubject_detail());
			for (Map temp_map : objs) {
				if (CommUtil.null2String(temp_map.get("type")).equals("img")
						&& CommUtil.null2String(temp_map.get("id")).equals(
								img_id)) {
					if (coords != null && !coords.equals("")
							&& coords_url != null && !coords_url.equals("")) {
						if (coords_url.indexOf("http://")<0) {
							coords_url="http://"+coords_url;
						}
						if (coords_url.indexOf("]")>=0) {
							coords_url=coords_url.replaceAll("]", "&");
						}
						String areaInfo = "";
						if (temp_map.get("areaInfo") != null
								&& !temp_map.get("areaInfo").equals("")) {
							areaInfo = CommUtil.null2String(temp_map
									.get("areaInfo"));
							String temp_dele_info = "";
							if (areaInfo.indexOf(coords) >= 0) {// 热点链接编辑
								String areainfos[] = areaInfo.split("-");
								for (String info : areainfos) {
									if (info.indexOf(coords) >= 0) {
										temp_dele_info = info;
									}
								}
								areaInfo = areaInfo.replace(temp_dele_info, "");
							}
						}
						areaInfo = areaInfo + coords + "==" + coords_url + "-";
						temp_map.put("areaInfo", areaInfo);
					}
					break;
				}
			}
			obj.setSubject_detail(Json.toJson(objs, JsonFormat.compact()));
			this.subjectService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "专题详情热点删除", value = "/admin/subject_img_coords_dele.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_img_coords_dele.htm")
	public void subject_img_coords_dele(HttpServletRequest request,
			HttpServletResponse response, String coords, String url, String id) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		boolean ret = false;
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = (List<Map>) Json.fromJson(obj
					.getSubject_detail());
			coords = coords.replace(",", "_");
			String temp_info = coords + "==" + url + "-";
			List<Map> temp_maps = new ArrayList<Map>();
			for (Map temp_map : subject_detail) {
				if (temp_map.get("areaInfo") != null) {
					String areaInfo = CommUtil.null2String(temp_map
							.get("areaInfo"));
					areaInfo = areaInfo.replace(temp_info, "");
					temp_map.put("areaInfo", areaInfo);
					ret = true;
				}
				temp_maps.add(temp_map);
			}
			obj.setSubject_detail(Json.toJson(temp_maps, JsonFormat.compact()));
			this.subjectService.update(obj);
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

	@SecurityMapping(title = "专题详情商品分类异步加载", value = "/admin/subject_gc.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_gc.htm")
	public ModelAndView subject_gc(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_gc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "专题详情商品添加", value = "/admin/subject_goods.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_goods.htm")
	public ModelAndView subject_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String goods_ids) {
		ModelAndView mv = new JModelAndView("admin/blue/subject_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (goods_ids != null && !goods_ids.equals("")) {
			List<Goods> goods_list = new ArrayList<Goods>();
			String ids[] = goods_ids.split(",");
			for (String gid : ids) {
				if (!gid.equals("")) {
					Goods obj = this.goodsService.getObjById(CommUtil
							.null2Long(gid));
					goods_list.add(obj);
				}
			}
			mv.addObject("goods_list", goods_list);
		}
		mv.addObject("goods_ids", goods_ids);
		mv.addObject("id", id);
		return mv;
	}

	@SecurityMapping(title = "专题详情商品加载", value = "/admin/subject_goods_load.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_goods_load.htm")
	public ModelAndView subject_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/subject_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime",
				"desc");
		qo.setPageSize(7);
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
				+ "/admin/subject_goods_load.htm", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "专题详情商品保存", value = "/admin/subject_goods_save.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_goods_save.htm")
	public String subject_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids, String old_ids) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		List<Map> maps = new ArrayList<Map>();
		if (obj.getSubject_detail() != null
				&& !obj.getSubject_detail().equals("")) {
			maps = (List<Map>) Json.fromJson(obj.getSubject_detail());
		}
		if (old_ids != null && !old_ids.equals("")) {
			for (Map temp_map : maps) {
				if (CommUtil.null2String(temp_map.get("type")).equals("goods")
						&& CommUtil.null2String(temp_map.get("goods_ids"))
								.equals(old_ids)) {
					maps.remove(temp_map);
					break;
				}
			}
		}
		Map map = new HashMap();
		map.put("type", "goods");
		map.put("goods_ids", ids);
		maps.add(map);
		String json = Json.toJson(maps, JsonFormat.compact());
		obj.setSubject_detail(json);
		this.subjectService.update(obj);
		return "redirect:/admin/subject_set.htm?id=" + id;
	}

	@SecurityMapping(title = "专题商品删除", value = "/admin/subject_goods_dele.htm*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping("/admin/subject_goods_dele.htm")
	public void subject_goods_dele(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_ids,
			String type) {
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = (List<Map>) Json.fromJson(obj
					.getSubject_detail());
			List<Map> temp_maps = new ArrayList<Map>();
			for (Map temp_map : subject_detail) {
				temp_maps.add(temp_map);
				if (CommUtil.null2String(temp_map.get("type")).equals(type)
						&& CommUtil.null2String(temp_map.get("goods_ids"))
								.equals(goods_ids)) {
					temp_maps.remove(temp_map);
					break;
				}
			}
			obj.setSubject_detail(Json.toJson(temp_maps, JsonFormat.compact()));
			this.subjectService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
