package com.iskyshop.module.cms.manage.admin.action;

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
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.domain.query.InformationClassQueryObject;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationReplyService;
import com.iskyshop.module.cms.service.IInformationService;
import com.iskyshop.module.cms.view.tools.CmsTools;

/**
 * 
 * <p>
 * Title: InformationClassManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯分类管理控制器，用来操作资讯分类信息，资讯分类使用两级管理
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
 * @date 2015-2-6
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class InformationClassManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IInformationClassService informationclassService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IInformationReplyService informationReplyService;
	@Autowired
	private CmsTools cmsTools;

	/**
	 * InformationClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯分类列表", value = "/admin/information_class_list.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_list.htm")
	public ModelAndView information_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_class_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationClassQueryObject qo = new InformationClassQueryObject(
				currentPage, mv, "ic_sequence", "asc");
		qo.addQuery("obj.ic_pid is null", null);
		IPageList pList = this.informationclassService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	/**
	 * informationclass添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯分类添加", value = "/admin/information_class_add.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_add.htm")
	public ModelAndView information_class_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage,String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_class_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
	//	List<InformationClass> ics=this.informationclassService.query("select obj from InformationClass obj", params, begin, max)
		mv.addObject("currentPage", currentPage);
		List<InformationClass> informationClasses = this.informationclassService.query("select obj from InformationClass obj where obj.ic_pid is null", null, -1, -1);
		mv.addObject("informationClasses", informationClasses);
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * informationclass编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯分类编辑", value = "/admin/information_class_edit.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_edit.htm")
	public ModelAndView information_class_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/information_class_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			InformationClass informationclass = this.informationclassService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", informationclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
			List<InformationClass> informationClasses = this.informationclassService.query("select obj from InformationClass obj where obj.ic_pid is null", null, -1, -1);
			mv.addObject("informationClasses", informationClasses);
		}
		return mv;
	}

	/**
	 * informationclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯分类保存", value = "/admin/information_class_save.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_save.htm")
	public ModelAndView information_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url,String pid) {
		WebForm wf = new WebForm();
		InformationClass informationclass = null;
		if (id.equals("")) {
			informationclass = wf.toPo(request, InformationClass.class);
			informationclass.setAddTime(new Date());
		} else {
			InformationClass obj = this.informationclassService.getObjById(Long
					.parseLong(id));
			informationclass = (InformationClass) wf.toPo(request, obj);
		}
		if(!CommUtil.null2String(pid).equals("")){
			informationclass.setIc_pid(CommUtil.null2Long(pid));
		}
		if (id.equals("")) {
			this.informationclassService.save(informationclass);
		} else
			this.informationclassService.update(informationclass);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)+"/admin/information_class_list.htm");
		mv.addObject("op_title", "保存资讯分类成功");
		if (add_url != null) {
			mv.addObject("add_url", CommUtil.getURL(request)+"/admin/information_class_add.htm?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "资讯分类删除", value = "/admin/information_class_del.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_del.htm")
	public String information_class_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				InformationClass informationclass = this.informationclassService
						.getObjById(Long.parseLong(id));
				Map map = new HashMap();
				map.put("id", informationclass.getId());
				List<InformationClass> informationClasses = this.informationclassService.query("select obj from InformationClass obj where obj.ic_pid=:id", map, -1, -1);
				for (InformationClass informationClass2 : informationClasses) {
					this.informationclassService.delete(informationClass2.getId());
					map.clear();
					map.put("id", informationClass2.getId());
					List<Information> informations = this.informationService.query("select obj from Information obj where obj.classid=:id", map, -1, -1);
					for (Information information : informations) {
						map.clear();
						map.put("id", information.getId());
						List<InformationReply> informationReplies = this.informationReplyService.query("select obj from InformationReplies obj where obj.info_id=:id", map, -1, -1);
						for (InformationReply informationReply : informationReplies) {
							this.informationReplyService.delete(informationReply.getId());
						}
						this.informationService.delete(information.getId());
					}
				}
				
				this.informationclassService.delete(Long.parseLong(id));
			}
		}
		return "redirect:information_class_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯分类ajax", value = "/admin/information_class_ajax.htm*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping("/admin/information_class_ajax.htm")
	public void information_class_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		InformationClass obj = this.informationclassService.getObjById(Long
				.parseLong(id));
		Field[] fields = InformationClass.class.getDeclaredFields();
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
		this.informationclassService.update(obj);
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