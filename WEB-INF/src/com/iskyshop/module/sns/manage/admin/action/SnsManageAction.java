package com.iskyshop.module.sns.manage.admin.action;

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
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.module.sns.domain.UserDynamic;
import com.iskyshop.module.sns.domain.UserShare;
import com.iskyshop.module.sns.domain.query.UserDynamicQueryObject;
import com.iskyshop.module.sns.domain.query.UserShareQueryObject;
import com.iskyshop.module.sns.service.IUserDynamicService;
import com.iskyshop.module.sns.service.IUserShareService;
import com.iskyshop.module.sns.view.tools.SnsTools;

/**
 * 
 * <p>
 * Title: SnsManageAction.java
 * </p>
 * 
 * <p>
 * Description:超级后台用户动态与分享内容列表
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
 * @author jy
 * 
 * @date 2015-01-16
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SnsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserShareService usershareService;
	@Autowired
	private IUserDynamicService dynamicService;
	@Autowired
	private SnsTools snsTools;

	/**
	 * sns分享列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "sns分享列表", value = "/admin/sns_share_list.htm*", rtype = "admin", rname = "分享列表", rcode = "sns_share", rgroup = "会员")
	@RequestMapping("/admin/sns_share_list.htm")
	public ModelAndView sns_share_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/sns_share_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserShareQueryObject qo = new UserShareQueryObject(currentPage, mv,
				"addTime", "desc");
		IPageList pList = this.usershareService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "sns分享删除", value = "/admin/sns_share_del.htm*", rtype = "admin", rname = "分享列表", rcode = "sns_share", rgroup = "会员")
	@RequestMapping("/admin/sns_share_del.htm")
	public String sns_share_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String share_id) {
		UserShare userShare = this.usershareService.getObjById(CommUtil.null2Long(share_id));
		if(userShare!=null){
			this.usershareService.delete(CommUtil.null2Long(share_id));
		}
		
		return "redirect:sns_share_list.htm?currentPage="+currentPage;
	}
	
	/**
	 * sns动态列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "sns动态列表", value = "/admin/sns_dynamic_list.htm*", rtype = "admin", rname = "动态列表", rcode = "sns_hot", rgroup = "会员")
	@RequestMapping("/admin/sns_dynamic_list.htm")
	public ModelAndView sns_dynamic_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/sns_dynamic_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserDynamicQueryObject qo = new UserDynamicQueryObject(currentPage, mv,
				"addTime", "desc");
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("snsTools",snsTools);
		return mv;
	}
	
	@SecurityMapping(title = "sns动态删除", value = "/admin/sns_dynamic_del.htm*", rtype = "admin", rname = "动态列表", rcode = "sns_hot", rgroup = "会员")
	@RequestMapping("/admin/sns_dynamic_del.htm")
	public String sns_dynamic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String dynamic_id) {
		UserDynamic userDynamic = this.dynamicService.getObjById(CommUtil.null2Long(dynamic_id));
		if(userDynamic!=null){
			this.dynamicService.delete(CommUtil.null2Long(dynamic_id));
		}
		return "redirect:sns_dynamic_list.htm?currentPage="+currentPage;
	}

}
