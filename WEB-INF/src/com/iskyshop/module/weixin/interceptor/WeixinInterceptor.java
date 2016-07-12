package com.iskyshop.module.weixin.interceptor;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.foundation.service.ISysConfigService;


public class WeixinInterceptor implements HandlerInterceptor {
	
	@Autowired
	private ISysConfigService configService;

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception exc)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj, ModelAndView mv) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj) throws Exception {
		// TODO Auto-generated method stub
		boolean ret = false;
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String token = configService.getSysConfig().getWeixin_token();
		String signature = request.getParameter("signature");
		String[] str = { token, timestamp, nonce };
		Arrays.sort(str);
		String sort_str = str[0]+str[1]+str[2];
		String mark = DigestUtils.shaHex(sort_str);
		if(mark.equals(signature)){
			ret = true;
		}
		return ret;
	}

}
