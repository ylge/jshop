package com.iskyshop.manage.seller.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
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
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: ConsultSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家咨询管理器，显示所有卖家咨询信息，卖家在这里可以回复买家的咨询及其他咨询信息的操作
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
 * @date 2014-9-29
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class ConsultSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private GoodsViewTools GoodsViewTools;

	@SecurityMapping(title = "卖家咨询列表", value = "/seller/consult.htm*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping("/seller/consult.htm")
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/consult.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		if (!CommUtil.null2String(reply).equals("")) {
			qo.addQuery("obj.reply",
					new SysMap("reply", CommUtil.null2Boolean(reply)), "=");
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("GoodsViewTools", GoodsViewTools);
		return mv;
	}

	@SecurityMapping(title = "卖家咨询回复", value = "/seller/consult_reply.htm*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping("/seller/consult_reply.htm")
	public ModelAndView consult_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/consult_reply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "卖家咨询回复保存", value = "/seller/consult_reply_save.htm*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping("/seller/consult_reply_save.htm")
	public String consult_reply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String consult_reply,
			String currentPage) throws Exception {
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		obj.setConsult_reply(consult_reply);
		obj.setReply_time(new Date());
		obj.setReply_user_id(user.getId());
		obj.setReply_user_name(user.getUserName());
		obj.setReply(true);
		this.consultService.update(obj);
		if (this.configService.getSysConfig().isEmailEnable()
				&& obj.getConsult_user_id() != null) {
			Map map = new HashMap();
			map.put("buyer_id", CommUtil.null2String(obj.getConsult_user_id()));
			List<Map> maps = CommUtil.Json2List(obj.getGoods_info());
			for (Map m : maps) {
				map.put("goods_id", m.get("goods_id").toString());
			}
			String json = Json.toJson(map);
			this.msgTools.sendEmailCharge(CommUtil.getURL(request),
					"email_tobuyer_cousult_reply_notify", this.userService
							.getObjById(obj.getConsult_user_id()).getEmail(),
					json, null, CommUtil.null2String(user.getStore().getId()));
		}
		return "redirect:consult.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家咨询删除", value = "/seller/consult_del.htm*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping("/seller/consult_del.htm")
	public String consult_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Consult obj = this.consultService.getObjById(CommUtil
						.null2Long(id));
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				if (obj.getStore_id().equals(user.getStore().getId())) {
					this.consultService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:consult.htm?currentPage=" + currentPage;
	}
}
