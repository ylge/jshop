package com.iskyshop.module.circle.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.pay.tenpay.ReponseHandlerForWx;

public class CircleInterceptor implements HandlerInterceptor {
	@Autowired
	private ISysConfigService configService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj) throws Exception {
		// TODO Auto-generated method stub
		boolean ret = false;
		if(configService.getSysConfig().getCircle_open()==1){
			ret = true;
		}else{
			response.sendRedirect(CommUtil.getURL(request) + "/404.htm");
		}
		return ret;
	}

}
