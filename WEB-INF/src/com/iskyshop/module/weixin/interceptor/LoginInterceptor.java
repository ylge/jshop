package com.iskyshop.module.weixin.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.weixin.view.tools.Base64Tools;

public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private IUserService userService;
	@Autowired
	private Base64Tools base64Tools;

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		

	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		String openid = request.getParameter("openid");
		String url = request.getParameter("url");
		if(openid!=null&&url!=null){
			request.getSession(false).setAttribute("his_url",
					url + "&openid=" + openid);
		}
		boolean ret = true;
			if (openid != null && !openid.equals("")&&SecurityUserHolder.getCurrentUser() == null) {
					Map params = new HashMap();
					params.put("openid", openid);
					List<User> user = this.userService
							.query("select obj from User obj where obj.openId=:openid",
									params, -1, -1);
					if (user.size() == 1) {
						String userName = user.get(0).getUserName();
						String password = user.get(0).getPassword();
						if (userName != null && !userName.equals("")) {
							String userMark = this.base64Tools.decodeStr(user
									.get(0).getUserMark());
							response.sendRedirect("/iskyshop_login.htm?username="
									+ userName
									+ "&password="
									+ password
									+ "&encode=true");
						}
					}
			}
		return ret;
	}

}
