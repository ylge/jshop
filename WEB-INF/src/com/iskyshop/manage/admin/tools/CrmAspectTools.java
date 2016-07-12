package com.iskyshop.manage.admin.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.CustomerRelMana;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.ICustomerRelManaService;

/**
 * 
 * <p>
 * Title: CrmAspectTools.java
 * </p>
 * 
 * <p>
 * Description:crm的aop
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
 * @date 2014-11-4
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Aspect
@Component
public class CrmAspectTools {
	@Autowired
	private ICustomerRelManaService customerRelManaService;
	private static final String ORDER_AOP = "execution(* com.iskyshop.foundation.service.impl.OrderFormServiceImpl.*(..))";
	private static final String CONSULT_AOP = "execution(* com.iskyshop.foundation.service.impl.ConsultServiceImpl.*(..))";
	private static final String FAVORITE_AOP = "execution(* com.iskyshop.foundation.service.impl.FavoriteServiceImpl.*(..))";

	@After(value = ORDER_AOP)
	public void order_aop(JoinPoint point) {
		if (point.getArgs().length > 0) {
			if (point.getSignature().getName().equals("save")) {
				OrderForm of = (OrderForm) point.getArgs()[0];
				if (of != null && !of.getId().toString().equals("")) {
					if (of.getOrder_cat() == 0) {
						CustomerRelMana crm = new CustomerRelMana();
						crm.setAddTime(new Date());
						crm.setCus_type(0);
						crm.setUser_id(CommUtil.null2Long(of.getUser_id()));
						crm.setUserName(of.getUser_name());
						crm.setWhether_self(of.getOrder_form());
						if (of.getOrder_form() == 0) {
							crm.setStore_id(CommUtil.null2Long(of.getStore_id()));
						}
						List<Map> list = Json.fromJson(List.class,
								of.getGoods_info());
						if (list.size() > 0) {
							crm.setGoods_id(CommUtil.null2Long(list.get(0).get(
									"goods_id")));
							crm.setGoods_name(list.get(0).get("goods_name")
									.toString());
						}
						this.customerRelManaService.save(crm);
					}
				}
			}
		}
	}

	@After(value = CONSULT_AOP)
	public void consult_aop(JoinPoint point) {
		if (point.getArgs().length > 0) {
			if (point.getSignature().getName().equals("save")) {
				Consult c = (Consult) point.getArgs()[0];
				if (c != null && !c.getId().toString().equals("")) {
					CustomerRelMana crm = new CustomerRelMana();
					crm.setAddTime(new Date());
					crm.setCus_type(1);
					crm.setUser_id(c.getConsult_user_id());
					crm.setUserName(c.getConsult_user_name());
					crm.setWhether_self(c.getWhether_self());
					if (c.getWhether_self() == 0) {
						crm.setStore_id(c.getStore_id());
					}
					List<Map> list = Json.fromJson(List.class,
							c.getGoods_info());
					if (list.size() > 0) {
						crm.setGoods_id(CommUtil.null2Long(list.get(0).get(
								"goods_id")));
						crm.setGoods_name(list.get(0).get("goods_name")
								.toString());
					}
					this.customerRelManaService.save(crm);
				}
			}
		}
	}

	@After(value = FAVORITE_AOP)
	public void favorite_aop(JoinPoint point) {
		if (point.getArgs().length > 0) {
			if (point.getSignature().getName().equals("save")) {
				Favorite f = (Favorite) point.getArgs()[0];
				if (f != null && !f.getId().toString().equals("")) {
					if (f.getType() == 0) {
						CustomerRelMana crm = new CustomerRelMana();
						crm.setAddTime(new Date());
						crm.setCus_type(2);
						crm.setUser_id(f.getUser_id());
						crm.setUserName(f.getUser_name());
						if (f.getGoods_type() == 0) {
							crm.setWhether_self(1);
						} else {
							crm.setWhether_self(0);
						}
						if (f.getGoods_type() == 1) {
							crm.setStore_id(f.getGoods_store_id());
						}
						crm.setGoods_id(f.getGoods_id());
						crm.setGoods_name(f.getGoods_name());
						this.customerRelManaService.save(crm);
					}
				}
			}
		}
	}
}
