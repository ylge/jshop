package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsTypePropertyQueryObject;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: GoodsTypePropertySellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家类型属性管理类，主要用于商家自己添加类型属性，只用于商品详情页显示，不适用于列表页筛选
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
 * @author liying
 * 
 * @date 2015-6-15
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsTypePropertySellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsTypePropertyService gtpService;
	@Autowired
	private IUserService userService;

	/**
	 * 类型属性列表页，显示商家的类型属性
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品类型属性列表", value = "/seller/gtp_list.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping("/seller/gtp_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/gtp_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GoodsTypePropertyQueryObject qo = new GoodsTypePropertyQueryObject(
				currentPage, mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsTypeProperty.class, mv);
		qo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.gtpService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 类型属性添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "类型属性添加", value = "/seller/gtp_add.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping("/seller/gtp_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/gtp_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 类型属性保存管理，count为新增属性数据的条数（新增时为了方便可以一次新增多条数据）；
	 * 
	 * @param currentPage
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "类型属性保存", value = "/seller/gtp_save.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping("/seller/gtp_save.htm")
	public String save(HttpServletRequest request,
			HttpServletResponse response, String count,
			String currentPage) {
		GoodsTypeProperty goodsTypeProperty = null;
		WebForm wf = new WebForm();
		//保存
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			int sequence = CommUtil.null2Int(request
					.getParameter("gtp_sequence_" + i));
			String name = CommUtil.null2String(request.getParameter("gtp_name_"
					+ i));
			String value = CommUtil.null2String(request
					.getParameter("gtp_value_" + i));
			if (!name.equals("") && !value.equals("")) {
				GoodsTypeProperty gtp = new GoodsTypeProperty();
				String id = CommUtil.null2String(request.getParameter("gtp_id_"
						+ i));
				if (id.equals("")) {
					gtp = new GoodsTypeProperty();
				} else {
					gtp = this.gtpService.getObjById(Long
							.parseLong(id));
				}
				gtp.setAddTime(new Date());
				gtp.setStore_id(user.getStore().getId());
				gtp.setName(name);
				gtp.setSequence(sequence);
				gtp.setValue(value);;
				if (id.equals("")) {
					this.gtpService.save(gtp);
				} else {
					this.gtpService.update(gtp);
				}
		}
		}
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/gtp_list.htm");
		request.getSession(false).setAttribute("op_title", "添加成功");
	
		return "redirect:/seller/success.htm";
	}
	
	/**
	 * 商家商品类型属性删除管理
	 * 
	 * @param mulitId
	 * @param request
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品属性删除", value = "/seller/gtp_del.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping("/seller/gtp_del.htm")
	public String delete(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsTypeProperty obj = this.gtpService
						.getObjById(Long.parseLong(id));
				if (obj != null) {
					if (obj.getStore_id().equals(user.getStore().getId())) {
						this.gtpService.delete(Long.parseLong(id));
					}
				}
			}
		}
		
		return "redirect:gtp_list.htm?currentPage=" + currentPage;
	}
	
	/**
	 * 商品类型属性列表页ajax更新，实现修改过功能
	 * 
	 * @param id
	 * @param fieldName
	 * @param value
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "属性Ajax更新", value = "/seller/gtp_update_ajax.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping("/seller/gtp_update_ajax.htm")
	public void gtp_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsTypeProperty gtp = this.gtpService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsTypeProperty.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(gtp);
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
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.gtpService.update(gtp);
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
	
}