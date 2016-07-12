package com.iskyshop.module.sns.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.sns.domain.UserShare;
import com.iskyshop.module.sns.service.IUserShareService;

/**
 * 
 * <p>
 * Title: SnsUserShareAction.java
 * </p>
 * 
 * <p>
 * Description: 前台分享控制器
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
 * @date 2014-12-12
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SnsUserShareAction {
	@Autowired
	private IUserShareService userShareService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "商品分享", value = "/share_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/share_goods.htm")
	public void share_goods(HttpServletResponse response, String share_content,
			String share_goods_id, String share_goods_name,
			String share_goods_photo) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		UserShare userShare = new UserShare();
		userShare.setAddTime(new Date());
		userShare.setUser_name(user.getUserName());
		userShare.setUser_id(user.getId());
		userShare.setShare_content(share_content);
		userShare.setShare_goods_id(CommUtil.null2Long(share_goods_id));
		userShare.setShare_goods_name(share_goods_name);
		userShare.setShare_goods_photo(share_goods_photo);
		boolean ret = this.userShareService.save(userShare);
		if (ret) {
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
}
