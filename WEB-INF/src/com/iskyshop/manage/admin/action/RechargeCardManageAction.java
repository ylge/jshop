package com.iskyshop.manage.admin.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.iskyshop.foundation.domain.RechargeCard;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.RechargeCardQueryObject;
import com.iskyshop.foundation.service.IRechargeCardService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: RechargeCardManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统充值卡管理控制器，用来显示、添加、删除等充值卡信息操作
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
 * @date 2014-10-15
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class RechargeCardManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRechargeCardService rechargecardService;

	@SecurityMapping(title = "充值接口设置", value = "/admin/set_ofcard.htm*", rtype = "admin", rname = "接口设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping("/admin/set_ofcard.htm")
	public ModelAndView set_ofcard(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_ofcard_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "充值接口", value = "/admin/set_ofcard.htm*", rtype = "admin", rname = "接口设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping("/admin/set_ofcard_save.htm")
	public ModelAndView set_ofcard_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ofcard,
			String ofcard_userid, String ofcard_userpws,
			String ofcard_mobile_profit) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (CommUtil.null2String(id).equals("")) {
			config.setOfcard(CommUtil.null2Boolean(ofcard));
			config.setOfcard_userid(ofcard_userid);
			config.setOfcard_userpws(ofcard_userpws);
			config.setOfcard_mobile_profit(BigDecimal.valueOf(CommUtil
					.null2Double(ofcard_mobile_profit)));
			this.configService.save(config);
		} else {
			config.setOfcard(CommUtil.null2Boolean(ofcard));
			config.setOfcard_userid(ofcard_userid);
			config.setOfcard_userpws(ofcard_userpws);
			config.setOfcard_mobile_profit(BigDecimal.valueOf(CommUtil
					.null2Double(ofcard_mobile_profit)));
			this.configService.update(config);
		}
		mv.addObject("op_title", "充值接口保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_ofcard.htm");
		return mv;
	}

	/**
	 * RechargeCard列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "充值卡列表", value = "/admin/recharge_card_list.htm*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping("/admin/recharge_card_list.htm")
	public ModelAndView recharge_card_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String rc_sn, String rc_mark, String rc_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/recharge_card_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		RechargeCardQueryObject qo = new RechargeCardQueryObject(currentPage,
				mv, orderBy, orderType);
		if (!CommUtil.null2String(rc_sn).equals("")) {
			qo.addQuery(
					"obj.rc_sn",
					new SysMap("rc_sn", "%" + CommUtil.null2String(rc_sn) + "%"),
					"like");
		}
		if (!CommUtil.null2String(rc_mark).equals("")) {
			qo.addQuery("obj.rc_mark",
					new SysMap("rc_mark", CommUtil.null2String(rc_mark)), "=");
		}
		if (!CommUtil.null2String(rc_status).equals("")) {
			qo.addQuery("obj.rc_status",
					new SysMap("rc_status", CommUtil.null2Int(rc_status)), "=");
		}
		IPageList pList = this.rechargecardService.list(qo);
		mv.addObject("rc_sn", rc_sn);
		mv.addObject("rc_mark", rc_mark);
		mv.addObject("rc_status", rc_status);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * rechargecard添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "充值卡新增", value = "/admin/recharge_card_add.htm*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping("/admin/recharge_card_add.htm")
	public ModelAndView recharge_card_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/recharge_card_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * rechargecard保存管理
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "充值卡列表保存", value = "/admin/recharge_card_save.htm*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping("/admin/recharge_card_save.htm")
	public ModelAndView recharge_card_save(HttpServletRequest request,
			HttpServletResponse response, String type, String rc_count,
			String rc_prefix, String rc_amount, String rc_mark, String rc_sns)
			throws IOException {
		if (type.equals("auto")) {// 自动生成充值卡信息
			for (int i = 0; i < CommUtil.null2Int(rc_count); i++) {
				String rc_sn = CommUtil.null2String(rc_prefix)
						+ CommUtil.null2String(UUID.randomUUID()).replaceAll(
								"-", "");
				Map params = new HashMap();
				params.put("rc_sn", rc_sn);
				List<RechargeCard> rc_list = this.rechargecardService
						.query("select obj from RechargeCard obj where obj.rc_sn=:rc_sn",
								params, -1, -1);
				if (rc_list.size() == 0) {
					RechargeCard rc = new RechargeCard();
					rc.setAddTime(new Date());
					rc.setRc_amount(BigDecimal.valueOf(CommUtil
							.null2Double(rc_amount)));
					rc.setRc_mark(rc_mark);
					rc.setRc_pub_user_id(SecurityUserHolder.getCurrentUser()
							.getId());
					rc.setRc_pub_user_name(SecurityUserHolder.getCurrentUser()
							.getUserName());
					rc.setRc_sn(rc_sn);
					this.rechargecardService.save(rc);
				}
			}
		}
		if (type.equals("hand")) {// 手动录入充值卡信息
			for (String rc_sn : CommUtil.str2list(rc_sns)) {
				if (!CommUtil.null2String(rc_sn).equals("")) {
					Map params = new HashMap();
					params.put("rc_sn", rc_sn);
					List<RechargeCard> rc_list = this.rechargecardService
							.query("select obj from RechargeCard obj where obj.rc_sn=:rc_sn",
									params, -1, -1);
					if (rc_list.size() == 0) {
						RechargeCard rc = new RechargeCard();
						rc.setAddTime(new Date());
						rc.setRc_amount(BigDecimal.valueOf(CommUtil
								.null2Double(rc_amount)));
						rc.setRc_mark(rc_mark);
						rc.setRc_pub_user_id(SecurityUserHolder
								.getCurrentUser().getId());
						rc.setRc_pub_user_name(SecurityUserHolder
								.getCurrentUser().getUserName());
						rc.setRc_sn(rc_sn);
						this.rechargecardService.save(rc);
					}
				}
			}
		}
		if (type.equals("import")) {// 导入充值卡信息
			// 首先上传txt文件
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "card";
			Map map = CommUtil.saveFileToServer(request, "card_txt",
					saveFilePathName, "", null);
			if (map.get("fileName") != "") {
				String file_name = CommUtil.null2String(map.get("fileName"));
				// 开始读取文件
				String path = saveFilePathName + File.separator + file_name;
				// System.out.println(path);
				BufferedReader br = new BufferedReader(new FileReader(path));
				String rc_sn = br.readLine();// 一次读入一行，直到读入null为文件结束
				while (rc_sn != null) {
					// System.out.println(rc_sn);
					rc_sn = br.readLine(); // 接着读下一行
					if (!CommUtil.null2String(rc_sn).equals("")) {
						Map params = new HashMap();
						params.put("rc_sn", rc_sn);
						List<RechargeCard> rc_list = this.rechargecardService
								.query("select obj from RechargeCard obj where obj.rc_sn=:rc_sn",
										params, -1, -1);
						if (rc_list.size() == 0) {
							RechargeCard rc = new RechargeCard();
							rc.setAddTime(new Date());
							rc.setRc_amount(BigDecimal.valueOf(CommUtil
									.null2Double(rc_amount)));
							rc.setRc_mark(rc_mark);
							rc.setRc_pub_user_id(SecurityUserHolder
									.getCurrentUser().getId());
							rc.setRc_pub_user_name(SecurityUserHolder
									.getCurrentUser().getUserName());
							rc.setRc_sn(rc_sn);
							this.rechargecardService.save(rc);
						}
					}
				}
				// 删除读取完毕的文件
				br.close();
				CommUtil.deleteFile(path);
			}

		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/recharge_card_list.htm");
		mv.addObject("op_title", "充值卡保存成功");
		return mv;
	}

	@SecurityMapping(title = "充值卡列表删除", value = "/admin/recharge_card_del.htm*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping("/admin/recharge_card_del.htm")
	public String recharge_card_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				RechargeCard obj = this.rechargecardService.getObjById(CommUtil
						.null2Long(id));
				if (obj.getRc_status() == 0) {// 只能删除未被使用的卡号
					this.rechargecardService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:recharge_card_list.htm?currentPage=" + currentPage;
	}
}