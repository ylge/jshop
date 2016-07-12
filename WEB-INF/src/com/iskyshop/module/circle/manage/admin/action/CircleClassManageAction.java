package com.iskyshop.module.circle.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.module.circle.domain.CircleClass;
import com.iskyshop.module.circle.domain.query.CircleClassQueryObject;
import com.iskyshop.module.circle.service.ICircleClassService;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * 
 * <p>
 * Title: CircleClassManageAction.java
 * </p>
 * 
 * <p>
 * Description:圈子类型管理类
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CircleClassManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICircleClassService circleclassService;

	/**
	 * CircleClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_list.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/circleclass_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CircleClassQueryObject qo = new CircleClassQueryObject(currentPage, mv,
				"sequence", "asc");
		IPageList pList = this.circleclassService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * circleclass添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_add.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/circleclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * circleclass编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_edit.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/circleclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			CircleClass circleclass = this.circleclassService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", circleclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * circleclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_save.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		CircleClass circleclass = null;
		if (id.equals("")) {
			circleclass = wf.toPo(request, CircleClass.class);
			circleclass.setAddTime(new Date());
		} else {
			CircleClass obj = this.circleclassService.getObjById(Long
					.parseLong(id));
			circleclass = (CircleClass) wf.toPo(request, obj);
		}

		if (id.equals("")) {
			this.circleclassService.save(circleclass);
		} else
			this.circleclassService.update(circleclass);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", "/admin/circleclass_list.htm");
		mv.addObject("add_url", "/admin/circleclass_add.htm");
		mv.addObject("op_title", "类型保存成功");
		return mv;
	}

	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_del.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CircleClass circleclass = this.circleclassService
						.getObjById(Long.parseLong(id));
				this.circleclassService.delete(Long.parseLong(id));
			}
		}
		return "redirect:circleclass_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "圈子类型列表", value = "/admin/circleclass_ajax.htm*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping("/admin/circleclass_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		CircleClass obj = this.circleclassService
				.getObjById(Long.parseLong(id));
		if (fieldName.equals("recommend")) {
			if (obj.getRecommend() == 1) {
				obj.setRecommend(0);
			} else {
				obj.setRecommend(1);
			}
		}
		if (fieldName.equals("nav_index")) {
			if (obj.getNav_index() == 1) {
				obj.setNav_index(0);
			} else {
				obj.setNav_index(1);
			}
		}
		this.circleclassService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			if (fieldName.equals("recommend")) {
				writer.print(obj.getRecommend());
			} else {
				writer.print(obj.getNav_index());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}