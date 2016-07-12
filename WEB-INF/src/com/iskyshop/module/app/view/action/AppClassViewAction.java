package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MobileClassViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台分类请求
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
 * @date 2014-7-14
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppClassViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 手机客户端商城首页分类请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/goodsclass.htm")
	public void goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map map_list = new HashMap();
		List list = new ArrayList();
		if (id == null || id.equals("")) {// 查询顶级分类
			Map params = new HashMap();
			params.put("display", true);
			List<GoodsClass> gcs = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
						.getSysConfig().getImageWebServer()))) {
					url = this.configService.getSysConfig().getImageWebServer();
				}
			for (GoodsClass gc : gcs) {
				Map gc_map = new HashMap();
				gc_map.put("id", gc.getId());
				gc_map.put("className", gc.getClassName());
				// 一级分类的推荐子分类，3个
				Map recommondmap = new HashMap();
				recommondmap.put("recommend", true);
				recommondmap.put("parentid", gc.getId());
				List<GoodsClass> recommondgcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:parentid and obj.recommend=:recommend order by obj.sequence asc",
								recommondmap, 0, 3);
				if (recommondgcs.size() > 0) {
					String recommend = "";
					for (GoodsClass goodsClass : recommondgcs) {
						recommend += goodsClass.getClassName() + "/";
					}
					recommend = recommend.substring(0, recommend.length() - 1);
					gc_map.put("recommend_children", recommend);
				}
				String path = "";
				if (gc.getApp_icon() != null && !gc.equals("")) {
					Accessory img = this.accessoryService.getObjById(CommUtil
							.null2Long(gc.getApp_icon()));
					if (img != null) {
						path = url + "/" + img.getPath() + "/" + img.getName();
					}
				} else {
					if (gc.getIcon_type() == 0) {
						path = url
								+ "/resources/style/common/images/icon/icon_"
								+ gc.getIcon_sys() + ".png";
					} else {
						path = url + "/" + gc.getIcon_acc().getPath() + "/"
								+ gc.getIcon_acc().getName();
					}
				}

				gc_map.put("icon_path", path);
				list.add(gc_map);
			}
		} else {// 查询子集分类
			GoodsClass parent = this.goodsClassService.getObjById(CommUtil
					.null2Long(id));
			for (GoodsClass gc : parent.getChilds()) {
				Map gc_map = new HashMap();
				gc_map.put("id", gc.getId());
				gc_map.put("className", gc.getClassName());
				list.add(gc_map);
			}
		}
		map_list.put("goodsclass_list", list);
		map_list.put("code", "true");
		String json = Json.toJson(map_list, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
