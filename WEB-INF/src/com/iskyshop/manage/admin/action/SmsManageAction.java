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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.SmsGoldLogQueryObject;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISmsGoldLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: SmsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 短消息管理类，平台可设置系统短信邮件是否免费为商家所使用，或者由商家购买使用
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
 * @date 2014-10-31
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SmsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISmsGoldLogService smsGoldLogService;

	@SecurityMapping(title = "短消息设置", value = "/admin/sms_set.htm*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping("/admin/sms_set.htm")
	public ModelAndView sms_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/sms_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "短消息设置保存", value = "/admin/sms_set_save.htm*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping("/admin/sms_set_save.htm")
	public ModelAndView sms_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ztc_status,
			String ztc_price, String ztc_goods_view) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		mv.addObject("op_title", "短消息设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/sms_set.htm");
		return mv;
	}

	@SecurityMapping(title = "短消息购买记录", value = "/admin/sms_gold_log.htm*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping("/admin/sms_gold_log.htm")
	public ModelAndView sms_gold_log(HttpServletRequest request,
			HttpServletResponse response, String log_type, String currentPage,
			String store_name, String log_status) {
		ModelAndView mv = new JModelAndView("admin/blue/sms_gold_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SmsGoldLogQueryObject qo = new SmsGoldLogQueryObject(currentPage, mv,
				"addTime", "desc");
		if (store_name != null && !store_name.equals("")) {
			qo.addQuery("obj.store_name", new SysMap("store_name", "%"
					+ CommUtil.null2String(store_name) + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (log_status != null && !log_status.equals("")) {
			qo.addQuery("obj.log_status",
					new SysMap("log_status", CommUtil.null2Int(log_status)),
					"=");
			mv.addObject("log_status", log_status);
		}
		if (log_type != null && !log_type.equals("")) {
			qo.addQuery("obj.log_type", new SysMap("log_type", log_type), "=");
			mv.addObject("log_type", log_type);
		} else {
			qo.addQuery("obj.log_type", new SysMap("log_type", "sms"), "=");
		}
		IPageList pList = this.smsGoldLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
