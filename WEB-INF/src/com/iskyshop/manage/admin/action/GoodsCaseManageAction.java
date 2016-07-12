package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCase;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.query.GoodsCaseQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IGoodsCaseService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: GoodsCaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台橱窗管理控制器，用来管理首页等页面橱窗展示，首页橱窗展示位置在推荐商品通栏的tab页
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
 * @date 2014-9-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class GoodsCaseManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsCaseService goodscaseService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;

	/**
	 * GoodsCase列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "橱窗列表", value = "/admin/goods_case_list.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_list.htm")
	public ModelAndView goods_case_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_case_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsCaseQueryObject qo = new GoodsCaseQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.goodscaseService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goodscase_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * goodscase添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "橱窗添加", value = "/admin/goods_case_add.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_add.htm")
	public ModelAndView goods_case_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_case_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * goodscase编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "橱窗编辑", value = "/admin/goods_case_edit.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_edit.htm")
	public ModelAndView goods_case_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_case_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsCase goodscase = this.goodscaseService.getObjById(Long
					.parseLong(id));
			List list = (List) Json.fromJson(goodscase.getCase_content());
			if(list!=null){
				mv.addObject("count", list.size());
			}else{
				mv.addObject("count", 0);
			}
			mv.addObject("obj", goodscase);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodscase保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "橱窗保存", value = "/admin/goods_case_save.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_save.htm")
	public ModelAndView goods_case_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String case_name,
			String display, String sequence, String case_id, String case_content) {
		GoodsCase goodscase = null;
		if (id.equals("")) {
			goodscase = new GoodsCase();
			goodscase.setAddTime(new Date());
		} else {
			goodscase = this.goodscaseService.getObjById(Long.parseLong(id));
		}
		goodscase.setDisplay(CommUtil.null2Int(display));
		goodscase.setCase_name(case_name);
		goodscase.setSequence(CommUtil.null2Int(sequence));
		goodscase.setCase_id(case_id);

		if (case_content != null && !case_content.equals("")) {
			List list = new ArrayList();
			for (String str : case_content.split(",")) {
				if (str != null && !str.equals("")) {
					list.add(CommUtil.null2Long(str));
				}
			}
			Map map = new HashMap();
			map.put("ids", list);
			goodscase.setCase_content(Json.toJson(list, JsonFormat.compact()));
		}

		if (id.equals("")) {
			this.goodscaseService.save(goodscase);
		} else
			this.goodscaseService.update(goodscase);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存橱窗成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "橱窗删除", value = "/admin/goods_case_del.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_del.htm")
	public String goods_case_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsCase goodscase = this.goodscaseService.getObjById(Long
						.parseLong(id));
				this.goodscaseService.delete(Long.parseLong(id));
			}
		}
		return "redirect:goods_case_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "橱窗Ajax更新", value = "/admin/goods_case_del.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_ajax.htm")
	public void goods_case_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsCase obj = this.goodscaseService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsCase.class.getDeclaredFields();
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
		this.goodscaseService.update(obj);
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

	@SecurityMapping(title = "橱窗商品添加", value = "/admin/goods_case_goods.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_goods.htm")
	public ModelAndView goods_case_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String goods_ids) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_case_goods.html",
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
		} else if (id != null && !id.equals("")) {
			List<Goods> goods_list = new ArrayList<Goods>();
			GoodsCase goodscase = this.goodscaseService.getObjById(CommUtil
					.null2Long(id));
			List list = (List) Json.fromJson(goodscase.getCase_content());
			if(list!=null){
				for (Object obj : list) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(obj));
					goods_list.add(goods);
				}
			}
			mv.addObject("goods_list", goods_list);
		}

		mv.addObject("goods_ids", goods_ids);
		mv.addObject("id", id);
		return mv;
	}

	@SecurityMapping(title = "商品分类异步加载", value = "/admin/goods_case_gc.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_gc.htm")
	public ModelAndView goods_case_gc(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_case_gc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "商品加载", value = "/admin/goods_case_goods_load.htm*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping("/admin/goods_case_goods_load.htm")
	public ModelAndView goods_case_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goods_case_goods_load.html",
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
				+ "/admin/goods_case_goods_load.htm", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name, pList, mv);
		return mv;
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