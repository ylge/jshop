package com.iskyshop.module.app.manage.seller.action;

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
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: AppSellerPayoffLogViewAction.java
 * </p>
 * 
 * <p>
 * Description: 结算账单
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-5-13
 * 
 * @version 1.0
 */
@Controller
public class AppSellerPayoffLogViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPayoffLogService payoffLogService;

	/**
	 * 结算账单列表
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param begin_count
	 * @param end_count
	 * @param status
	 */
	@RequestMapping("/app/seller/payoffLog_list.htm")
	public void payoffLog_list(HttpServletRequest request,
			HttpServletResponse response, String user_id, String begin_count,
			String select_count, String status) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map para = new HashMap();
		para.put("user_id", user.getId());
		// 结算账单状态，0为未结算，1为可结算、3为结算中，6为已结算已完成。
		int payoff_type = CommUtil.null2Int(status);
		para.put("status", payoff_type);
		List<PayoffLog> list = this.payoffLogService
				.query("select obj from PayoffLog obj where obj.seller.id=:user_id and obj.status=:status order by addTime",
						para, CommUtil.null2Int(begin_count),
						CommUtil.null2Int(select_count));
		List payofflog_list = new ArrayList();
		for (PayoffLog obj : list) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("pl_sn", obj.getPl_sn());
			map.put("payoff_type", obj.getPayoff_type());
			if (payoff_type < 3) {
				map.put("addTime", obj.getAddTime());
			} else if (payoff_type == 3) {
				map.put("addTime", obj.getApply_time());
			} else if (payoff_type == 6) {
				map.put("addTime", obj.getComplete_time());
			}
			map.put("total_amount", obj.getTotal_amount());

			payofflog_list.add(map);
		}
		json_map.put("payofflog_list", payofflog_list);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 账单详情
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 */
	@RequestMapping("/app/seller/payoffLog_detail.htm")
	public void payoffLog_detail(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id) {
		Map json_map = new HashMap();
		PayoffLog payoffLog = this.payoffLogService.getObjById(CommUtil
				.null2Long(id));
		json_map.put("pl_sn", payoffLog.getPl_sn());
		json_map.put("addTime", payoffLog.getAddTime());
		json_map.put("pl_info", payoffLog.getPl_info());
		json_map.put("o_id", payoffLog.getO_id());
		int payoff_type = payoffLog.getStatus();
		json_map.put("o_id", payoff_type);
		if (payoff_type >= 3) {
			json_map.put("apply_time", payoffLog.getApply_time());
		}
		if (payoff_type == 6) {
			json_map.put("complete_time", payoffLog.getComplete_time());
			json_map.put("reality_amount", payoffLog.getReality_amount());
			json_map.put("payoff_remark", payoffLog.getPayoff_remark());
		}
		json_map.put("payoff_type", payoff_type);
		json_map.put("order_total_price", payoffLog.getOrder_total_price());
		json_map.put("commission_amount", payoffLog.getCommission_amount());
		json_map.put("total_amount", payoffLog.getTotal_amount());

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
