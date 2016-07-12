package com.iskyshop.module.cms.view.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.GoodsFloorTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.module.circle.view.tools.CircleViewTools;
import com.iskyshop.module.cms.domain.CmsIndexTemplate;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.domain.query.InformationQueryObject;
import com.iskyshop.module.cms.service.ICmsIndexTemplateService;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationService;
import com.iskyshop.module.cms.view.tools.CmsTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: InformationSellerManageAction.java
 * </p>
 * 
 * <p>
 * Description: 资讯前台控制器
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
 * @date 2014-12-4
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CmsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IInformationClassService informationClassService;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private ICmsIndexTemplateService cmsIndexTemplateService;
	@Autowired
	private CmsTools cmsTools;
	@Autowired
	private GoodsFloorTools gf_tools;
	@Autowired
	private CircleViewTools circleViewTools;

	@RequestMapping("/cms/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String classid) {
		ModelAndView mv = new JModelAndView("/cms/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 首页头部
		Map map = new HashMap();
		map.put("status", 20);
		map.put("recommend", 1);
		List<Information> informations = this.informationService
				.query("select obj from Information obj where obj.status=:status and obj.recommend=:recommend",
						map, -1, -1);
		if (informations.size() > 0) {
			mv.addObject("information", informations.get(0));
		}
		List<CmsIndexTemplate> templates = this.cmsIndexTemplateService
				.query("select obj from CmsIndexTemplate obj where obj.whether_show=1 order by sequence asc ",
						null, -1, -1);
		map.clear();
		map.put("status", 20);
		List<Information> hot_infors = this.informationService
				.query("select new Information(id,title) from Information obj where obj.status=:status  order by obj.clicktimes desc",
						map, 0,7);
		mv.addObject("hot_infors", hot_infors);
		mv.addObject("objs", templates);
		mv.addObject("cmsTools", cmsTools);
		mv.addObject("imageTools", imageTools);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("circleViewTools", circleViewTools);
		return mv;
	}

	@RequestMapping("/cms/list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author) {
		ModelAndView mv = new JModelAndView("/cms/list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv,
				"sequence,addTime", "desc");
		qo.addQuery("obj.status", new SysMap("status", 20), "=");
		if (title != null && !title.equals("")) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"),
					"LIKE");
			mv.addObject("title", title);
		}
		if (author != null && !author.equals("")) {
			qo.addQuery("obj.author", new SysMap("author", "%" + author + "%"),
					"LIKE", "or");
			mv.addObject("author", author);
		}
		String id = request.getParameter("id");
		mv.addObject("id", id);
		
		mv.addObject("imageTools", imageTools);
		// 热门话题
		if (!CommUtil.null2String(id).equals("")) {
			Map map = new HashMap();
			map.put("id", CommUtil.null2Long(id));
			List<InformationClass> informationClasses = this.informationClassService.query("select obj from InformationClass obj where obj.ic_pid=:id", map, -1, -1);
			List<Long> ids = new ArrayList<Long>();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}
			ids.add(CommUtil.null2Long(id));
			map.clear();
			map.put("ids", ids);
			List<Information> hot_infors = this.informationService
					.query("select new Information(id,title) from Information obj where obj.classid in (:ids) order by obj.clicktimes desc",
							map, 0, 6);
			mv.addObject("hot_infors", hot_infors);
			qo.addQuery("obj.classid in (:ids)", map);
		} else {
			List<Information> hot_infors = this.informationService
					.query("select new Information(id,title) from Information obj order by obj.clicktimes desc",
							null, 0, 6);
			mv.addObject("hot_infors", hot_infors);
		}
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		if (this.configService.getSysConfig().isZtc_status()) {
			List<Goods> ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 0, 6);
			}
			mv.addObject("ztc_goods", ztc_goods);
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 6);
			mv.addObject("ztc_goods", ztc_goods);
			
		}
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	@RequestMapping("/cms/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response, String title) {
		ModelAndView mv = new JModelAndView("/cms/top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<InformationClass> classes = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null order by obj.ic_sequence asc",
						null, 0, 18);
		mv.addObject("classes", classes);
		String op = CommUtil.null2String(request.getAttribute("id"));
		InformationClass informationClass = this.informationClassService.getObjById(CommUtil.null2Long(op));
		if(informationClass!=null&&informationClass.getIc_pid()!=null){
			op = CommUtil.null2String(informationClass.getIc_pid());
		}
		mv.addObject("title", title);
		mv.addObject("op", op);
		mv.addObject("cmsTools", cmsTools);
		return mv;
	}

	@RequestMapping("/cms/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("/cms/footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

}