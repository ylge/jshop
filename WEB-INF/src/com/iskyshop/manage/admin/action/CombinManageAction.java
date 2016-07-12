package com.iskyshop.manage.admin.action;

import java.io.File;

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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.CombinPlanQueryObject;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.seller.tools.CombinTools;

/**
 * 
 * <p>
 * Title: CombinManageAction.java
 * </p>
 * 
 * <p>
 * Description: 组合销售平台控制器
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
 * @author hezeng
 * 
 * @date 2014-9-19
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CombinManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;

	@SecurityMapping(title = "组合销售商品列表", value = "/admin/combin.htm*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping("/admin/combin.htm")
	public ModelAndView combin(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String combin_status, String beginTime, String endTime,
			String goods_name, String type, String combin_form) {
		ModelAndView mv = new JModelAndView("admin/blue/combin_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CombinPlanQueryObject qo = new CombinPlanQueryObject(currentPage, mv,
				"addTime", "desc");
		if (combin_status != null && !combin_status.equals("")) {
			qo.addQuery("obj.combin_status", new SysMap("combin_status",
					CommUtil.null2Int(combin_status)), "=");
			mv.addObject("combin_status", combin_status);
		}
		if (combin_form != null && !combin_form.equals("")) {
			qo.addQuery("obj.combin_form",
					new SysMap("combin_form", CommUtil.null2Int(combin_form)),
					"=");
			mv.addObject("combin_form", combin_form);
		}
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.main_goods_name", new SysMap("goods_name", "%"
					+ CommUtil.null2String(goods_name) + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		if (type != null && !type.equals("")) {
			qo.addQuery("obj.combin_type",
					new SysMap("type", CommUtil.null2Int(type)), "=");
			mv.addObject("type", type);
		} else {
			qo.addQuery("obj.combin_type", new SysMap("type", 0), "=");
		}
		qo.setPageSize(10);
		IPageList pList = this.combinplanService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("combinTools", combinTools);
		return mv;
	}

	@SecurityMapping(title = "组合销售审核通过", value = "/admin/combin_goods_audit.htm*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping("/admin/combin_goods_audit.htm")
	public String combin_goods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String type) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CombinPlan obj = this.combinplanService.getObjById(CommUtil
						.null2Long(id));
				obj.setCombin_status(1);// 审核通过
				this.combinplanService.update(obj);
			}
		}
		return "redirect:/admin/combin.htm?currentPage=" + currentPage
				+ "&type=" + type;
	}

	@SecurityMapping(title = "组合销售审核拒绝", value = "/admin/combin_goods_refuse.htm*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping("/admin/combin_goods_refuse.htm")
	public String combin_goods_refuse(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String refuse_msg, String type) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CombinPlan obj = this.combinplanService.getObjById(CommUtil
						.null2Long(id));
				obj.setCombin_status(-1);// 审核拒绝
				if (refuse_msg != null && !refuse_msg.equals("")) {
					obj.setCombin_refuse_msg(refuse_msg);
				}
				this.combinplanService.update(obj);
			}
		}
		return "redirect:/admin/combin.htm?currentPage=" + currentPage
				+ "&type=" + type;
	}
}