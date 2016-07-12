package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RechargeCard;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.RechargeCardQueryObject;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IRechargeCardService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: RechargeCardBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心充值卡管理控制器，用来列表显示充值卡使用记录，使用充值卡充值等等充值操作
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
 * @date 2014-10-16
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class RechargeCardBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRechargeCardService rechargeCardService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;

	@SecurityMapping(title = "充值卡使用记录", value = "/buyer/recharge_card.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/recharge_card.htm")
	public ModelAndView recharge_card(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/recharge_card.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		RechargeCardQueryObject qo = new RechargeCardQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		User user = SecurityUserHolder.getCurrentUser();
		qo.addQuery("obj.rc_user_id", new SysMap("rc_user_id", user.getId()),
				"=");
		IPageList pList = this.rechargeCardService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "充值卡充值", value = "/buyer/recharge_card_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/recharge_card_add.htm")
	public ModelAndView recharge_card_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/recharge_card_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "充值卡验证", value = "/buyer/recharge_card_validate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/recharge_card_validate.htm")
	public void recharge_card_validate(HttpServletRequest request,
			HttpServletResponse response, String card_sn) {
		Map params = new HashMap();
		params.put("rc_sn", card_sn);
		params.put("rc_status", 2);
		List<RechargeCard> rcs = this.rechargeCardService
				.query("select obj from RechargeCard obj where obj.rc_sn=:rc_sn and obj.rc_status<:rc_status",
						params, -1, -1);
		boolean ret = false;
		if (rcs.size() > 0) {
			ret = true;
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

	@SecurityMapping(title = "充值卡充值", value = "/buyer/recharge_card_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/recharge_card_save.htm")
	public void recharge_card_save(HttpServletRequest request,
			HttpServletResponse response, String card_sn) {
		Map params = new HashMap();
		params.put("rc_sn", card_sn);
		List<RechargeCard> rcs = this.rechargeCardService.query(
				"select obj from RechargeCard obj where obj.rc_sn=:rc_sn",
				params, -1, -1);
		boolean ret = false;
		if (rcs.size() > 0) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			RechargeCard rc = rcs.get(0);
			if (rc.getRc_status() == 0) {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), rc.getRc_amount())));
				boolean flag = this.userService.update(user);
				if (flag) {// 用户充值成功后改变充值卡状态
					rc.setRc_status(2);
					rc.setRc_time(new Date());
					rc.setRc_user_id(user.getId());
					rc.setRc_user_name(user.getUserName());
					rc.setRc_info(CommUtil.formatLongDate(new Date()) + "  "
							+ user.getUserName() + "充值使用");
					rc.setRc_ip(CommUtil.getIpAddr(request));
					ret = this.rechargeCardService.update(rc);
					// 增加收支明细日志
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_info("充值卡充值增加可用预存款" + rc.getRc_amount() + "元");
					log.setPd_log_user(user);
					log.setPd_type("可用预存款");
					log.setPd_op_type("充值");
					log.setPd_log_amount(rc.getRc_amount());
					this.predepositLogService.save(log);
				}
			}
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
}
