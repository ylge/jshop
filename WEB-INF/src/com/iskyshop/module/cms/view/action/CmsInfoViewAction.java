package com.iskyshop.module.cms.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonCompile;
import org.nutz.json.JsonFormat;
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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.domain.query.InformationReplyQueryObject;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationReplyService;
import com.iskyshop.module.cms.service.IInformationService;

/**
 * 
 * <p>
 * Title: CmsInfoViewAction.java
 * </p>
 * 
 * <p>
 * Description: 咨询详情页相关操作均在此控制器中处理
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
 * @author jy
 * 
 * @date 2015-2-3
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class CmsInfoViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IInformationReplyService replyService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IInformationClassService classService;
	@Autowired
	private ImageTools imageTools;

	@RequestMapping("/cms/detail.htm")
	public ModelAndView detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("/cms/detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information obj = this.informationService.getObjById(CommUtil
				.null2Long(id));
		boolean flag = false;
		if (obj != null) {
			User user = SecurityUserHolder.getCurrentUser();
			if (obj.getStatus() < 20) {
				if (user != null && user.getUserRole().equals("ADMIN")) {// 只有管理员可以查看为通过审核的资讯
					flag = true;
				}
			} else {
				flag = true;
			}
			if (flag) {
				obj.setClicktimes(obj.getClicktimes() + 1);
				mv.addObject("obj", obj);
				if (obj.getInfoIconData() == null
						|| obj.getInfoIconData().equals("")) {
					obj.setInfoIconData(Json.toJson(
							this.getNewInfoIconDataMap(), JsonFormat.compact()));
				}
				this.informationService.update(obj);
				Map map = getViewInfoIconDataMap(Json.fromJson(Map.class,
						obj.getInfoIconData()));
				mv.addObject("IconDataMap", map);
				InformationReplyQueryObject qo = new InformationReplyQueryObject();
				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				qo.setPageSize(12);
				qo.addQuery("obj.info_id", new SysMap("info_id", obj.getId()),
						"=");
				IPageList pList = this.replyService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
				mv.addObject("replies", pList.getResult());
				mv.addObject("count", pList.getRowCount());
				mv.addObject("className",
						this.classService.getObjById(obj.getClassid())
								.getIc_name());
				mv.addObject("imageTools", imageTools);
				// 热门话题
				Map map2 = new HashMap();
				map2.put("classid", obj.getClassid());
				List<Information> hot_infors = this.informationService
						.query("select new Information(id,title) from Information obj where obj.classid=:classid order by obj.clicktimes desc",
								map2, 0, 6);
				mv.addObject("hot_infors", hot_infors);
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
			}
		}
		if (!flag) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，资讯查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/cms/index.htm");
		}

		return mv;
	}

	@SecurityMapping(title = "选择表情", value = "/cms/info_icon.htm*", rtype = "buyer", rname = "资讯", rcode = "user_info", rgroup = "资讯")
	@RequestMapping("/cms/info_icon.htm")
	public void info_icon(HttpServletRequest request,
			HttpServletResponse response, String type, String user_id,
			String info_id) {
		Information information = informationService.getObjById(CommUtil
				.null2Long(info_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = null;
		Boolean ret = true;
		if (information.getInfoUserData() != null
				&& !information.getInfoUserData().equals("")) {
			map = Json.fromJson(Map.class, information.getInfoUserData());
			if (map.containsKey(user.getId().toString())) {
				ret = false;
			} else {
				map.put(user.getId(), type);
				information.setInfoUserData(Json.toJson(map,
						JsonFormat.compact()));
			}
		} else {
			map = new HashMap<Integer, String>();
			map.put(user.getId(), type);
			information.setInfoUserData(Json.toJson(map, JsonFormat.compact()));
		}

		if (ret) {
			if (information.getInfoIconData() != null
					&& !information.getInfoIconData().equals("")) {
				map = Json.fromJson(Map.class, information.getInfoIconData());
			} else {
				map = this.getNewInfoIconDataMap();
			}
			map.put(type, (Integer) map.get(type) + 1);
			information.setInfoIconData(Json.toJson(map, JsonFormat.compact()));
			this.informationService.save(information);
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

	public Map<String, Integer> getNewInfoIconDataMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("hehe", 0);
		map.put("kaixin", 0);
		map.put("deyi", 0);
		map.put("nanguo", 0);
		map.put("fennu", 0);
		map.put("gandong", 0);
		map.put("henzan", 0);
		return map;
	}

	public Map<String, Integer> getViewInfoIconDataMap(Map tempMap) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		float max = this.getInfoIconDataMax(tempMap);
		int countTemp = CommUtil.null2Int(tempMap.get("hehe"));
		map.put("hehe_count", countTemp);
		map.put("hehe_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("kaixin"));
		map.put("kaixin_count", countTemp);
		map.put("kaixin_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("deyi"));
		map.put("deyi_count", countTemp);
		map.put("deyi_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("nanguo"));
		map.put("nanguo_count", countTemp);
		map.put("nanguo_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("fennu"));
		map.put("fennu_count", countTemp);
		map.put("fennu_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("gandong"));
		map.put("gandong_count", countTemp);
		map.put("gandong_price", (int) (countTemp / max * 100));
		countTemp = CommUtil.null2Int(tempMap.get("henzan"));
		map.put("henzan_count", countTemp);
		map.put("henzan_price", (int) (countTemp / max * 100));
		return map;

	}

	public int getInfoIconDataMax(Map tempMap) {
		int max = 0;
		int countTemp = CommUtil.null2Int(tempMap.get("hehe"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("kaixin"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("deyi"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("nanguo"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("fennu"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("gandong"));
		if (countTemp > max)
			max = countTemp;
		countTemp = CommUtil.null2Int(tempMap.get("henzan"));
		if (countTemp > max)
			max = countTemp;
		return max;
	}
}
