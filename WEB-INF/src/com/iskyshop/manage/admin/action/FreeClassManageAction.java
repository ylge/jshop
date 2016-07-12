package com.iskyshop.manage.admin.action;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.query.FreeClassQueryObject;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
/**
 * 
 * <p>
 * Title: FreeClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 添加0元试用商品的分类
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
 * @author jinxinzhe
 * 
 * @date 2014年11月12日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class FreeClassManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeClassService freeclassService;

	/**
	 * FreeClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类列表", value = "/admin/freeclass_list.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_list.htm")
	public ModelAndView freeclass_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/freeclass_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FreeClassQueryObject qo = new FreeClassQueryObject(currentPage, mv,
				"sequence", "asc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo,FreeClass.class,mv);
		IPageList pList = this.freeclassService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/freeclass_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * freeclass添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用分类添加", value = "/admin/freeclass_add.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_add.htm")
	public ModelAndView freeclass_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/freeclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * freeclass编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用分类编辑", value = "/admin/freeclass_edit.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_edit.htm")
	public ModelAndView freeclass_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/freeclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeClass freeclass = this.freeclassService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", freeclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * freeclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类保存", value = "/admin/freeclass_save.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_save.htm")
	public ModelAndView freeclass_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		FreeClass freeclass = null;
		if (id.equals("")) {
			freeclass = wf.toPo(request, FreeClass.class);
			freeclass.setAddTime(new Date());
		} else {
			FreeClass obj = this.freeclassService
					.getObjById(Long.parseLong(id));
			freeclass = (FreeClass) wf.toPo(request, obj);
		}

		if (id.equals("")) {
			this.freeclassService.save(freeclass);
		} else
			this.freeclassService.update(freeclass);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/freeclass_list.htm");
		mv.addObject("op_title", "保存分类成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/freeclass_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "0元试用分类删除", value = "/admin/freeclass_del.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_del.htm")
	public String freeclass_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.freeclassService.delete(Long.parseLong(id));
			}
		}
		return "redirect:freeclass_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "0元试用分类ajax", value = "/admin/freeclass_ajax.htm*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping("/admin/freeclass_ajax.htm")
	public void freeclass_ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		FreeClass obj = this.freeclassService.getObjById(Long.parseLong(id));
		Field[] fields = FreeClass.class.getDeclaredFields();
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
		this.freeclassService.update(obj);
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
	
	@RequestMapping("/admin/verify_freeclass_name.htm")
	public void verify_freeclass_name(HttpServletRequest request,
			HttpServletResponse response, String className, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("className", className);
		params.put("id", CommUtil.null2Long(id));
		List<FreeClass> fcs = this.freeclassService
				.query("select obj from FreeClass obj where obj.className=:className and obj.id!=:id",
						params, -1, -1);
		if (fcs != null && fcs.size() > 0) {
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
}