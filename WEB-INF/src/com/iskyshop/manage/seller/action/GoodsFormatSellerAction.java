package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.GoodsFormat;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.query.GoodsFormatQueryObject;
import com.iskyshop.foundation.service.IGoodsFormatService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: GoodsFormatSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商品顶部、底部版式管理控制器
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
 * @date 2014-10-19
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class GoodsFormatSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsFormatService goodsFormatService;

	@SecurityMapping(title = "卖家商品版式列表", value = "/seller/goods_format_list.htm*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_list.htm")
	public ModelAndView goods_format_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_format_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormatQueryObject qo = new GoodsFormatQueryObject();
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		qo.addQuery("obj.gf_store_id",
				new SysMap("gf_store_id", store.getId()), "=");
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		IPageList pList = this.goodsFormatService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "卖家商品版式添加", value = "/seller/goods_format_add.htm*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_add.htm")
	public ModelAndView goods_format_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_format_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "卖家商品版式编辑", value = "/seller/goods_format_edit.htm*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_edit.htm")
	public ModelAndView goods_format_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_format_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "卖家商品版式保存", value = "/seller/goods_format_save.htm*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_save.htm")
	public void goods_format_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		WebForm wf = new WebForm();
		if (CommUtil.null2String(id).equals("")) {
			GoodsFormat obj = wf.toPo(request, GoodsFormat.class);
			obj.setAddTime(new Date());
			Store store = this.userService.getObjById(
					SecurityUserHolder.getCurrentUser().getId()).getStore();
			obj.setGf_store_id(store.getId());
			ret = this.goodsFormatService.save(obj);
		} else {
			GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
					.null2Long(id));
			GoodsFormat gf = (GoodsFormat) wf.toPo(request, obj);
			ret = this.goodsFormatService.update(gf);
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

	@SecurityMapping(title = "卖家商品版式删除", value = "/seller/goods_format_delete.htm*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_delete.htm")
	public String goods_format_delete(HttpServletRequest request, String id,
			String currentPage) {
		if (!id.equals("")) {
			GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
					.null2Long(id));
			Store store = this.userService.getObjById(
					SecurityUserHolder.getCurrentUser().getId()).getStore();
			if (obj.getGf_store_id().equals(store.getId())) {
				this.goodsFormatService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:goods_format_list.htm?currentPage=" + currentPage;
	}
}
