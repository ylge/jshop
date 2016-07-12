package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
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
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.query.StoreGradeQueryObject;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: StoreGradeManageAction.java
 * </p>
 * 
 * <p>
 * Description:店铺类型管理控制器，用来管理商城店铺类型信息
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
 * @date 2014-10-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class StoreGradeManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreGradeService storegradeService;
	@Autowired
	private IStoreService storeService;

	/**
	 * StoreGrade列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺类型列表", value = "/admin/storegrade_list.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_list.htm")
	public ModelAndView storegrade_list(HttpServletRequest request,
			HttpServletResponse response,String q_gradeName, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/store_grade_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreGradeQueryObject qo = new StoreGradeQueryObject(currentPage, mv,
				orderBy, orderType);
		if(!CommUtil.null2String(q_gradeName).equals("")){
			qo.addQuery("obj.gradeName", new SysMap("gradeName", "%"+q_gradeName+"%"), "like");
		}
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, StoreGrade.class, mv);
		IPageList pList = this.storegradeService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * storegrade添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺类型添加", value = "/admin/storegrade_add.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_add.htm")
	public ModelAndView storegrade_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_grade_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * storegrade编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺类型编辑", value = "/admin/storegrade_edit.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_edit.htm")
	public ModelAndView storegrade_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_grade_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			StoreGrade storegrade = this.storegradeService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", storegrade);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * storegrade保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "店铺类型保存", value = "/admin/storegrade_save.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_save.htm")
	public ModelAndView storegrade_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String main_limit,
			String add_funciton_ck, String goods_audit) {
		WebForm wf = new WebForm();
		StoreGrade storegrade = null;
		if (id.equals("")) {
			storegrade = wf.toPo(request, StoreGrade.class);
			storegrade.setAddTime(new Date());
		} else {
			StoreGrade obj = this.storegradeService.getObjById(Long
					.parseLong(id));
			storegrade = (StoreGrade) wf.toPo(request, obj);
		}
		if (!CommUtil.null2Boolean(add_funciton_ck)) {
			storegrade.setAdd_funciton(null);
		}
		storegrade.setMain_limit(CommUtil.null2Int(main_limit));
		storegrade.setGoods_audit(CommUtil.null2Int(goods_audit));
		if (id.equals("")) {
			this.storegradeService.save(storegrade);
		} else
			this.storegradeService.update(storegrade);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺类型成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "店铺类型删除", value = "/admin/storegrade_del.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_del.htm")
	public String storegrade_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Map map = new HashedMap();
				map.put("gid", Long.parseLong(id));
				List<Store> stores = this.storeService.query(
						"select new Store(id,addTime) from Store obj where obj.grade.id =:gid", map,-1,-1);
				if(stores.size()>0){
					continue;
				}
				StoreGrade storegrade = this.storegradeService.getObjById(Long
						.parseLong(id));
				this.storegradeService.delete(Long.parseLong(id));
			}
		}
		return "redirect:storegrade_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "店铺类型AJAX更新", value = "/admin/storegrade_ajax.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_ajax.htm")
	public void storegrade_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		StoreGrade obj = this.storegradeService.getObjById(Long.parseLong(id));
		Field[] fields = StoreGrade.class.getDeclaredFields();
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
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.storegradeService.update(obj);
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

	@SecurityMapping(title = "店铺类型模板设置", value = "/admin/storegrade_template.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_template.htm")
	public ModelAndView storegrade_template(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_grade_template.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("path", request.getSession().getServletContext().getRealPath("/"));
		mv.addObject("obj",
				this.storegradeService.getObjById(Long.parseLong(id)));
		mv.addObject("separator", File.separator);
		return mv;
	}

	@SecurityMapping(title = "店铺类型模板保存", value = "/admin/storegrade_template_save.htm*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping("/admin/storegrade_template_save.htm")
	public ModelAndView storegrade_template_save(HttpServletRequest request,
			HttpServletResponse response, String list_url, String id,
			String templates) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreGrade grade = this.storegradeService
				.getObjById(Long.parseLong(id));
		grade.setTemplates(templates);
		this.storegradeService.update(grade);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺类型模板成功");
		return mv;
	}
}