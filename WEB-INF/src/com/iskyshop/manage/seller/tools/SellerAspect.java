package com.iskyshop.manage.seller.tools;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
* <p>Title: SellerAspect.java</p>

* <p>Description: 商家相关操作的前切面，当店铺到期关闭时只可使用“交易管理”</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2014-12-30

* @version iskyshop_b2b2c_2015
 */
@Aspect
@Component
public class SellerAspect {
	@Autowired
	private IUserService userService;
	
	private static final String AOP = "execution(* com.iskyshop.manage.seller.action..*.*(..))&&args(request,response,..)";
	private static final String URL = "order_ship,order_confirm,order,ship_address,group_code,transport_list,ecc_set,error,index";
	
	@Before(value = AOP)
	public void seller_aspect(JoinPoint joinPoint,HttpServletRequest request,HttpServletResponse response){
		if(SecurityUserHolder.getCurrentUser()!=null){
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int store_status = (user.getStore() == null ? 0 : user.getStore()
					.getStore_status());
			if(store_status!=15){
				String[] urls = URL.split(",");
				Boolean ret = false;
				for (String url : urls) {
					if(joinPoint.getSignature().getName().equals(url)){
						ret = true;
						break;
					}			
				}
				if(!ret){
					request.getSession().setAttribute("op_title","该操作不能执行");
					request.getSession().setAttribute("url","index.htm");
					try {
						response.sendRedirect("error.htm");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
