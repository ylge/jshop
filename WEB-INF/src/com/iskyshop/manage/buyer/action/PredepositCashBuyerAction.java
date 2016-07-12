package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.PredepositCash;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PredepositCashQueryObject;
import com.iskyshop.foundation.service.IPredepositCashService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: PredepositCashBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户中心提现管理控制器，用户可以在这里申请提现，查看提现明细、提现列表
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
 * @date 2015-3-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class PredepositCashBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPredepositCashService predepositCashService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/buyer_cash.htm")
	public ModelAndView buyer_cash(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_cash.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isDeposit()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		} else {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
			mv.addObject("availableBalance",
					CommUtil.null2Double(user.getAvailableBalance()));
		}

		return mv;
	}

	@SecurityMapping(title = "提现管理保存", value = "/buyer/buyer_cash_save.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/buyer_cash_save.htm")
	public void buyer_cash_save(HttpServletRequest request,
			HttpServletResponse response, String cash_payment,
			String cash_amount, String cash_userName, String cash_bank,
			String cash_account, String cash_info) {
		int ret = 100;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2Double(cash_amount) <= CommUtil.null2Double(user
				.getAvailableBalance())) {
			PredepositCash obj = new PredepositCash();
			obj.setCash_payment(cash_payment);
			obj.setCash_account(cash_account);
			obj.setCash_userName(cash_userName);
			obj.setCash_bank(cash_bank);
			obj.setCash_amount(BigDecimal.valueOf(CommUtil
					.null2Double(cash_amount)));
			obj.setCash_info(cash_info);
			obj.setCash_sn("cash"
					+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
					+ SecurityUserHolder.getCurrentUser().getId());
			obj.setAddTime(new Date());
			obj.setCash_user(SecurityUserHolder.getCurrentUser());
			this.predepositCashService.save(obj);
			// 保存提现日志
			PredepositLog log = new PredepositLog();
			log.setAddTime(new Date());
			log.setPd_log_amount(obj.getCash_amount());
			log.setPd_log_info("申请提现");
			log.setPd_log_user(obj.getCash_user());
			log.setPd_op_type("提现");
			log.setPd_type("可用预存款");
			this.predepositLogService.save(log);
		} else {
			ret = -100;
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

	@SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash_list.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/buyer_cash_list.htm")
	public ModelAndView buyer_cash_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_cash_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isDeposit()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		} else {
			PredepositCashQueryObject qo = new PredepositCashQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.cash_user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			IPageList pList = this.predepositCashService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}

	@SecurityMapping(title = "会员提现详情", value = "/buyer/buyer_cash_view.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/buyer_cash_view.htm")
	public ModelAndView buyer_cash_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_cash_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			PredepositCash obj = this.predepositCashService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}
}
