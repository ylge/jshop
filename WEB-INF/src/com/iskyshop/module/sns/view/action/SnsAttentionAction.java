package com.iskyshop.module.sns.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.service.ISnsAttentionService;
/**
 * 
* <p>Title: SnsAttentionAction.java</p>

* <p>Description: 个人主页 关注及取消关注功能</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2014-12-18

* @version iskyshop_b2b2c_2015
 */
@Controller
public class SnsAttentionAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISnsAttentionService snsAttentionService;
	
	@SecurityMapping(title = "sns关注买家", value = "/sns/attention_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/sns/attention_save.htm")
	public void attention_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int ret = 1;
		User user = this.userService.getObjById(CommUtil.null2Long(id));
		if (user != null && SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("fromUser", SecurityUserHolder.getCurrentUser().getId());
			params.put("toUser", user.getId());
			List<SnsAttention> list = this.snsAttentionService
					.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser and obj.toUser.id=:toUser",
							params, -1, -1);
			if (list.size() == 0) {
				SnsAttention sa = new SnsAttention();
				sa.setFromUser(SecurityUserHolder.getCurrentUser());
				sa.setToUser(user);
				sa.setAddTime(new Date());
				this.snsAttentionService.save(sa);
				ret = 0;
			}
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

	@SecurityMapping(title = "sns取消关注", value = "/sns/attention_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/sns/attention_cancel.htm")
	public void attention_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int ret = 1;
		User user = this.userService.getObjById(CommUtil.null2Long(id));
		if (user != null && SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("fromUser", SecurityUserHolder.getCurrentUser().getId());
			params.put("toUser", user.getId());
			List<SnsAttention> list = this.snsAttentionService
					.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser and obj.toUser.id=:toUser",
							params, -1, -1);
			for (SnsAttention sa : list) {
				this.snsAttentionService.delete(sa.getId());
			}
			ret = 0;
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
