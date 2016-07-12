package com.iskyshop.view.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: DynamicUrlViewAction.java
 * </p>
 * 
 * <p>
 * Description:动态url处理控制器，该控制器用来处理相关动态url，比如商品url，前端页面中仅仅有商品id，
 * 无法判断商品url是通过二级域名访问还是顶级域名访问，此时就需要使用该控制的一个方法完成url跳转
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
 * @author erikzhang
 * 
 * @date 2015-2-4
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class DynamicUrlViewAction {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 商品url定向，前端只有商品id，不知道是否开启二级域名，需要通过该url来定向商品的url
	 * 
	 * @param request
	 *            输入请求
	 * @param response
	 *            输出信息
	 * @param id
	 *            商品id
	 */
	@RequestMapping("/goods_dynamic")
	public void goods_dynamic(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		String goods_url = CommUtil.getURL(request) + "/goods_" + id + ".htm";
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& !CommUtil.null2String(
						obj.getGoods_store().getStore_second_domain()).equals(
						"")) {
			goods_url = "http://"
					+ obj.getGoods_store().getStore_second_domain() + "."
					+ CommUtil.generic_domain(request) + "/goods_" + id
					+ ".htm";
		}
		try {
			response.sendRedirect(goods_url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				response.sendRedirect(CommUtil.getURL(request) + "/404.htm");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}
}
