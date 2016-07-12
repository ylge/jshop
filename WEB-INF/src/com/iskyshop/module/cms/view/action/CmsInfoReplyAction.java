package com.iskyshop.module.cms.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.domain.query.InformationReplyQueryObject;
import com.iskyshop.module.cms.service.IInformationReplyService;
import com.iskyshop.module.cms.service.IInformationService;
import com.iskyshop.module.sns.domain.UserDynamic;
import com.mysql.jdbc.Field;

/**
 * 
* <p>Title: CmsInfoReplyAction.java</p>

* <p>Description: 资讯回复控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2015-2-6

* @version iskyshop_b2b2c_2015
 */
@Controller
public class CmsInfoReplyAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IInformationReplyService replyService;
	@Autowired
	private IInformationService informationService;
	
	
	@SecurityMapping(title = "资讯回复保存", value = "/cms/reply_save.htm*", rtype = "buyer", rname = "资讯", rcode = "user_info", rgroup = "资讯")
	@RequestMapping("/cms/reply_save.htm")
	public void reply_save(HttpServletRequest request,
			HttpServletResponse response,String content,String info_id){
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		InformationReply reply = new InformationReply();
		reply.setInfo_id(CommUtil.null2Long(info_id));
		reply.setAddTime(new Date());
		reply.setUserId(user.getId());
		reply.setUserName(user.getUserName());
		reply.setContent(content);
		Accessory acc = user.getPhoto();
		if(acc==null){
			reply.setUserPhoto("resources/style/system/front/default/images/usercenter/base_person.jpg");
		}else{
			reply.setUserPhoto(user.getPhoto().getPath()+"/"+user.getPhoto().getName());
		}
		
		boolean ret = this.replyService.save(reply);
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
	
	@RequestMapping("/cms/reply_ajax.htm")
	public ModelAndView reply_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage,String info_id) {
		ModelAndView mv = new JModelAndView("/cms/reply_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information information = this.informationService.getObjById(CommUtil.null2Long(info_id));
		mv.addObject("information", information);

		InformationReplyQueryObject qo = new InformationReplyQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(12);
		qo.addQuery("obj.info_id",new SysMap("info_id",information.getId()), "=");
		IPageList pList = this.replyService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("replies", pList.getResult());
		return mv;
	}
	
	
	
}
