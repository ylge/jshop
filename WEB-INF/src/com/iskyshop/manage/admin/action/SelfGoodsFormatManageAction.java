package com.iskyshop.manage.admin.action;

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
 * Title: GoodsFormatManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品版式管理控制器，用来管理自营商品的版式
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
 * @date 2014-10-29
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SelfGoodsFormatManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsFormatService goodsFormatService;

	@SecurityMapping(title = "商品版式列表", value = "/admin/goods_format_list.htm*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping("/admin/goods_format_list.htm")
	public ModelAndView goods_format_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goods_format_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormatQueryObject qo = new GoodsFormatQueryObject();
		qo.addQuery("obj.gf_cat", new SysMap("gf_cat", 1), "=");
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		IPageList pList = this.goodsFormatService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "商品版式添加", value = "/admin/goods_format_add.htm*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping("/admin/goods_format_add.htm")
	public ModelAndView goods_format_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_format_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "商品版式编辑", value = "/admin/goods_format_edit.htm*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping("/admin/goods_format_edit.htm")
	public ModelAndView goods_format_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_format_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "商品版式保存", value = "/admin/goods_format_save.htm*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping("/admin/goods_format_save.htm")
	public ModelAndView goods_format_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		if (CommUtil.null2String(id).equals("")) {
			GoodsFormat obj = wf.toPo(request, GoodsFormat.class);
			obj.setAddTime(new Date());
			obj.setGf_cat(1);
			this.goodsFormatService.save(obj);
		} else {
			GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
					.null2Long(id));
			GoodsFormat gf = (GoodsFormat) wf.toPo(request, obj);
			this.goodsFormatService.update(gf);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/goods_format_list.htm?currentPage=" + currentPage);
		mv.addObject("op_title", "商品版式保存成功");
		return mv;
	}

	@SecurityMapping(title = "商品版式删除", value = "/admin/goods_format_delete.htm*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping("/admin/goods_format_delete.htm")
	public String goods_format_delete(HttpServletRequest request,
			String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsFormat obj = this.goodsFormatService.getObjById(CommUtil
						.null2Long(id));
				if (obj.getGf_cat() == 1) {
					this.goodsFormatService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:goods_format_list.htm?currentPage=" + currentPage;
	}
}
