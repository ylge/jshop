package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EnoughReduceQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.QueryTools;

/**
 * 
 * <p>
 * Title: EnoughReduceSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营自营满就减控制器
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
 * @date 2014-9-22
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfEnoughReduceManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IEnoughReduceService enoughreduceService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private QueryTools queryTools;

	/**
	 * EnoughReduce列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动列表", value = "/admin/enoughreduce_self_list.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_list.htm")
	public ModelAndView enoughreduce_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ertitle, String erstatus,
			String erbegin_time, String erend_time) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/enoughreduce_self_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		EnoughReduceQueryObject qo = new EnoughReduceQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.er_type", new SysMap("er_type", 0), "=");
		if (orderBy != null && !"".equals(orderBy)) {
			qo.setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if (orderType != null && !"".equals(orderType)) {
			qo.setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
		if (ertitle != null && !"".equals(ertitle)) {
			qo.addQuery("obj.ertitle", new SysMap("ertitle", "%" + ertitle
					+ "%"), "like");
			mv.addObject("ertitle", ertitle);
		}
		if (erstatus != null && !"".equals(erstatus)) {
			qo.addQuery("obj.erstatus",
					new SysMap("erstatus", CommUtil.null2Int(erstatus)), "=");
			mv.addObject("erstatus", erstatus);
		}

		if (erbegin_time != null && !erbegin_time.equals("")) {
			qo.addQuery("DATE_FORMAT(obj.erbegin_time,'%Y-%m-%d')", new SysMap(
					"erbegin_time", erbegin_time), ">=");
			mv.addObject("erbegin_time", erbegin_time);
		}
		if (erend_time != null && !erend_time.equals("")) {
			qo.addQuery("DATE_FORMAT(obj.erend_time,'%Y-%m-%d')", new SysMap(
					"erend_time", erend_time), "<=");
			mv.addObject("erend_time", erend_time);
		}

		qo.setOrderBy("addTime");
		qo.setOrderType("desc");

		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,EnoughReduce.class,mv);
		IPageList pList = this.enoughreduceService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/enoughreduce_self_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * enoughreduce添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营满就减活动添加", value = "/admin/enoughreduce_self_add.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_add.htm")
	public ModelAndView enoughreduce_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/enoughreduce_self_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * enoughreduce编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营满就减活动修改", value = "/admin/enoughreduce_self_edit.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_edit.htm")
	public ModelAndView enoughreduce_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/enoughreduce_self_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			EnoughReduce enoughreduce = this.enoughreduceService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", enoughreduce);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * enoughreduce保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动保存", value = "/admin/enoughreduce_self_save.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_save.htm")
	public ModelAndView enoughreduce_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String count) {
		WebForm wf = new WebForm();
		EnoughReduce enoughreduce = null;
		if (id.equals("")) {
			enoughreduce = wf.toPo(request, EnoughReduce.class);
			enoughreduce.setAddTime(new Date());
			enoughreduce.setEr_type(0);
		} else {
			EnoughReduce obj = this.enoughreduceService.getObjById(Long
					.parseLong(id));
			enoughreduce = (EnoughReduce) wf.toPo(request, obj);
		}

		TreeMap<Double, Double> jsonmap = new TreeMap<Double, Double>();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			String enoughMoney = CommUtil.null2String(request
					.getParameter("enoughMoney_" + i));
			String reduceMoney = CommUtil.null2String(request
					.getParameter("reduceMoney_" + i));
			if (enoughMoney != null && !"".equals(enoughMoney)
					&& reduceMoney != null && !"".equals(reduceMoney)) {
				jsonmap.put(CommUtil.null2Double(new BigDecimal(enoughMoney)),
						CommUtil.null2Double(new BigDecimal(reduceMoney)));
			}
		}
		enoughreduce.setEr_json(Json.toJson(jsonmap, JsonFormat.compact()));
		String ertag = "";
		Iterator<Double> it = jsonmap.keySet().iterator();
		while (it.hasNext()) {
			double key = it.next();
			double value = jsonmap.get(key);
			ertag += "满" + key + "减" + value + ",";
		}
		ertag = ertag.substring(0, ertag.length() - 1);
		enoughreduce.setErtag(ertag);
		enoughreduce.setErstatus(10);
		enoughreduce.setErgoods_ids_json("[]");
		if (id.equals("")) {
			this.enoughreduceService.save(enoughreduce);
		} else
			this.enoughreduceService.update(enoughreduce);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存满就减活动成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	@SecurityMapping(title = "自营满就减活动删除", value = "/admin/enoughreduce_self_del.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_del.htm")
	public String enoughreduce_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				EnoughReduce enoughreduce = this.enoughreduceService
						.getObjById(Long.parseLong(id));
				// 释放绑定的商品
				String goods_json = enoughreduce.getErgoods_ids_json();
				if (goods_json != null && !goods_json.equals("")) {
					List<String> goods_id_list = (List) Json
							.fromJson(goods_json);
					for (String goods_id : goods_id_list) {
						Goods ergood = this.goodsService.getObjById(CommUtil
								.null2Long(goods_id));
						if(ergood!=null){
						ergood.setEnough_reduce(0);
						ergood.setOrder_enough_reduce_id(null);
						this.goodsService.update(ergood);
						}
					}
				}

				this.enoughreduceService.delete(Long.parseLong(id));
			}
		}
		return "redirect:enoughreduce_self_list.htm?currentPage=" + currentPage;
	}
	/**
	 * 活动商品添加
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动商品列表", value = "/admin/enoughreduce_self_goods_list.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_goods_list.htm")
	public ModelAndView enoughreduce_self_goods_list(
			HttpServletRequest request, HttpServletResponse response,
			String er_id, String currentPage, String orderBy, String orderType,
			String class_id, String brand_id, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/enoughreduce_self_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		EnoughReduce er = this.enoughreduceService.getObjById(CommUtil
				.null2Long(er_id));
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);

		if (er.getErstatus() != 20) {
			Map para = new HashMap();
			String hql = "1=1";
			if (er.getErgoods_ids_json() != null
					&& !er.getErgoods_ids_json().equals("")) {
				para.put("ids", genericIds(er.getErgoods_ids_json()));
				hql += " or (obj.id in (:ids)";
			}

			qo.addQuery("obj.goods_type", new SysMap("goods_type", 0), "=");
			this.queryTools.shieldGoodsStatus(qo, null);// 没参加任何活动的商品
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery("obj.goods_name", new SysMap("obj_goods_name", "%"
						+ goods_name + "%"), "like");
				mv.addObject("goods_name", goods_name);
				hql += "and obj.goods_name like :goods_name";
				para.put("goods_name", "%" + goods_name + "%");
			}
			if (class_id != null && !class_id.equals("")) {
				Map map = new HashMap();
				map.put("class_id", CommUtil.null2Long(class_id));
				qo.addQuery(
						"1=1 and (obj.gc.id=:class_id or obj.gc.parent.id=:class_id or obj.gc.parent.parent.id=:class_id)",
						map);
				mv.addObject("class_id", class_id);
				hql += "and (obj.gc.id=:class_id or obj.gc.parent.id=:class_id or obj.gc.parent.parent.id=:class_id)";
				para.put("class_id", CommUtil.null2Long(class_id));
			}
			if (brand_id != null && !brand_id.equals("")) {
				qo.addQuery("obj.goods_brand.id", new SysMap("obj_goods_brand",
						CommUtil.null2Long(brand_id)), "=");
				mv.addObject("brand_id", brand_id);
				hql += "and obj.goods_brand.id = :obj_goods_brand";
				para.put("obj_goods_brand", CommUtil.null2Long(brand_id));
			}
			hql += ")";
			if (er.getErgoods_ids_json().length() > 2) {
				qo.addQuery(hql, para);// 或者id被记录下来的
			}
		} else {
			if (er.getErgoods_ids_json().length() > 2) {

				Map para = new HashMap();
				para.put("ids", genericIds(er.getErgoods_ids_json()));
				qo.addQuery("obj.id in (:ids)", para);// id被记录下来的
				if (goods_name != null && !goods_name.equals("")) {
					qo.addQuery("obj.goods_name", new SysMap("obj_goods_name",
							"%" + goods_name + "%"), "like");
					mv.addObject("goods_name", goods_name);
				}
				if (class_id != null && !class_id.equals("")) {
					Map map = new HashMap();
					map.put("class_id", CommUtil.null2Long(class_id));
					qo.addQuery(
							"1=1 and (obj.gc.id=:class_id or obj.gc.parent.id=:class_id or obj.gc.parent.parent.id=:class_id)",
							map);
					mv.addObject("class_id", class_id);
				}
				if (brand_id != null && !brand_id.equals("")) {
					qo.addQuery("obj.goods_brand.id", new SysMap(
							"obj_goods_brand", CommUtil.null2Long(brand_id)),
							"=");
					mv.addObject("brand_id", brand_id);
				}
			} else {
				qo = null;
			}
		}

		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("er", er);
		return mv;
	}
	@SecurityMapping(title = "满就减商品AJAX更新", value = "/admin/enoughreduce_goods_ajax.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_goods_ajax.htm")
	public void enoughreduce_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String er_id)
			throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		EnoughReduce er = this.enoughreduceService.getObjById(Long
				.parseLong(er_id));
		int flag = obj.getEnough_reduce();
		boolean data = false;
		obj.setEnough_reduce(flag);
		String json = er.getErgoods_ids_json();
		List jsonlist;
		if (json != null && !"".equals(json)) {
			jsonlist = (List) Json.fromJson(json);
		} else {
			jsonlist = new ArrayList();
		}
		if (flag == 0) {
			data = true;
			if (obj.getCombin_status() == 0 && obj.getGroup_buy() == 0
					&& obj.getGoods_type() == 0
					&& obj.getActivity_status() == 0
					&& obj.getF_sale_type() == 0
					&& obj.getAdvance_sale_type() == 0
					&& obj.getOrder_enough_give_status() == 0) {
				obj.setEnough_reduce(1);
				obj.setOrder_enough_reduce_id(er_id);
				jsonlist.add(id);
				er.setErgoods_ids_json(Json.toJson(jsonlist,
						JsonFormat.compact()));
			}
		} else {
			data = false;
			obj.setEnough_reduce(0);
			obj.setOrder_enough_reduce_id("");
			if (jsonlist.contains(id)) {
				jsonlist.remove(id);
			}
			er.setErgoods_ids_json(Json.toJson(jsonlist, JsonFormat.compact()));
		}
		this.enoughreduceService.update(er);
		this.goodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自营满就减活动商品批量管理", value = "/admin/enoughreduce_goods_admin.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_goods_admin.htm")
	public String enoughreduce_goods_admin(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String er_id, String type) {
		EnoughReduce enoughreduce = this.enoughreduceService.getObjById(Long
				.parseLong(er_id));
		String goods_json = enoughreduce.getErgoods_ids_json();
		List goods_id_list = null;
		if (goods_json != null && !"".equals(goods_json)) {
			goods_id_list = (List) Json.fromJson(goods_json);
		} else {
			goods_id_list = new ArrayList();
		}

		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods ergood = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				if (ergood.getEnough_reduce() == 0
						|| ergood.getOrder_enough_reduce_id().equals(er_id)) {
					if (type.equals("add")) {
						if (ergood.getCombin_status() == 0
								&& ergood.getGroup_buy() == 0
								&& ergood.getGoods_type() == 0
								&& ergood.getActivity_status() == 0
								&& ergood.getF_sale_type() == 0
								&& ergood.getAdvance_sale_type() == 0
								&& ergood.getOrder_enough_give_status() == 0) {
							goods_id_list.add(id);
							ergood.setEnough_reduce(1);
							ergood.setOrder_enough_reduce_id(er_id);
						}
					} else {
						if (goods_id_list.contains(id)) {
							goods_id_list.remove(id);
						}
						ergood.setEnough_reduce(0);
						ergood.setOrder_enough_reduce_id("");
					}
				}
				this.goodsService.update(ergood);
			}
		}
		enoughreduce.setErgoods_ids_json(Json.toJson(goods_id_list,
				JsonFormat.compact()));
		this.enoughreduceService.update(enoughreduce);

		return "redirect:enoughreduce_self_goods_list.htm?currentPage="
				+ currentPage + "&er_id=" + er_id;
	}

	@SecurityMapping(title = "自营满就减活动ajax", value = "/admin/enoughreduce_self_ajax.htm*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping("/admin/enoughreduce_self_ajax.htm")
	public void enoughreduce_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		EnoughReduce obj = this.enoughreduceService.getObjById(Long
				.parseLong(id));
		Field[] fields = EnoughReduce.class.getDeclaredFields();
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
		this.enoughreduceService.update(obj);
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

	private Set<Long> genericIds(String str) {
		Set<Long> ids = new HashSet<Long>();
		List list = (List) Json.fromJson(str);
		for (Object object : list) {
			ids.add(CommUtil.null2Long(object));
		}
		return ids;
	}
}