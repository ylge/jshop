package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: AppActivityViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机App活动商品专题页控制器
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
 * @date 2015-1-21
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppActivityViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;

	/**
	 * 手机App活动商品专题页请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/activity_index.htm")
	public void activity_index(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		Map json_map = new HashMap();
		int code = 100;// -100没有任何数据，100，当前存在数据
		SysConfig sc = this.configService.getSysConfig();
		List<Map> datas = new ArrayList<Map>();
		int begin = 0;
		if (begin_count != null) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<ActivityGoods> actgoods = this.activityGoodsService
				.query("select obj from ActivityGoods obj where obj.act.ac_begin_time<=:ac_begin_time and obj.act.ac_end_time>:ac_end_time and obj.act.ac_status=:ac_status order by obj.addTime desc",
						params, begin, 10);
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}

		if (actgoods.size() > 0) {
			for (ActivityGoods ag : actgoods) {
				Map map = new HashMap();
				map.put("id", ag.getId());
				map.put("goods_id", ag.getAg_goods().getId());
				map.put("name", ag.getAg_goods().getGoods_name());
				if (ag.getAg_goods().getGoods_main_photo() != null) {
					map.put("img", url + "/"
							+ ag.getAg_goods().getGoods_main_photo().getPath()
							+ "/"
							+ ag.getAg_goods().getGoods_main_photo().getName()
							+ "_small."
							+ ag.getAg_goods().getGoods_main_photo().getExt());
				} else {
					map.put("img", url + "/" + sc.getGoodsImage().getPath()
							+ "/" + sc.getGoodsImage().getName());
				}
				map.put("salenum", ag.getAg_goods().getGoods_salenum());
				double low_price = CommUtil.mul(ag.getAg_goods()
						.getGoods_current_price(), ag.getAct().getAc_rebate3());
				double price1 = CommUtil.mul(ag.getAg_goods()
						.getGoods_current_price(), ag.getAct().getAc_rebate());
				double price2 = CommUtil.mul(ag.getAg_goods()
						.getGoods_current_price(), ag.getAct().getAc_rebate1());
				double price3 = CommUtil.mul(ag.getAg_goods()
						.getGoods_current_price(), ag.getAct().getAc_rebate2());
				map.put("low_price", CommUtil.formatMoney(low_price));
				map.put("price1", CommUtil.formatMoney(price1));
				map.put("price2", CommUtil.formatMoney(price2));
				map.put("price3", CommUtil.formatMoney(price3));
				datas.add(map);
			}
			code = 100;
			json_map.put("content", actgoods.get(0).getAct().getAc_content());
		} else {
			code = -100;
		}
		json_map.put("code", code);
		json_map.put("datas", datas);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	private void send_json(String json, HttpServletResponse response) {
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
