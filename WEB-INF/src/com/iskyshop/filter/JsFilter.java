package com.iskyshop.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
/**
 * 
 * <p>
 * Title: JsFilter.java
 * </p>
 * 
 * <p>
 * Description:js脚本注入转义过滤器
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
 * @author jinxinzhe
 * 
 * @date 2014-12-4
 * 
 * @version iskyshop_b2b2c 2.0
 */
public class JsFilter implements Filter{

	 @Override
     public void destroy() {
    }

     @Override
     public void doFilter(ServletRequest req, ServletResponse res,
                       FilterChain chain) throws IOException, ServletException {
             HttpServletRequest request = (HttpServletRequest)req;
             JsRequest wrapRequest= new JsRequest(request,request.getParameterMap());
             chain.doFilter(wrapRequest, res);
    }

     @Override
     public void init(FilterConfig arg0) throws ServletException {
    }

}
