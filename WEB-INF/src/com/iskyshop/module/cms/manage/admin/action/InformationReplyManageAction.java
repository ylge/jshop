package com.iskyshop.module.cms.manage.admin.action;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.domain.query.InformationReplyQueryObject;
import com.iskyshop.module.cms.service.IInformationReplyService;

@Controller
public class InformationReplyManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private IInformationReplyService informationreplyService;	
	/**
	 * InformationReply列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯回复列表", value = "/admin/information_reply.htm*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping("/admin/information_reply.htm")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/information_reply.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		InformationReplyQueryObject qo = new InformationReplyQueryObject(currentPage, mv, orderBy,
				orderType);
		IPageList pList = this.informationreplyService.list(qo);
		CommUtil.saveIPageList2ModelAndView("","",null, pList, mv);
		return mv;
	}

    
	@SecurityMapping(title = "资讯回复删除", value = "/admin/information_reply_del.htm*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping("/admin/information_reply_del.htm")
	public String delete(HttpServletRequest request,HttpServletResponse response,String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
			  InformationReply informationreply = this.informationreplyService.getObjById(Long.parseLong(id));
			  this.informationreplyService.delete(Long.parseLong(id));
			}
		}
		return "redirect:information_reply.htm?currentPage="+currentPage;
	}

}