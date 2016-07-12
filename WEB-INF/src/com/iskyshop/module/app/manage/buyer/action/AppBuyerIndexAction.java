package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MobileBuyerIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端用户中心
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
 * @date 2014-7-25
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppBuyerIndexAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;

	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private OrderFormTools orderformTools;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;

	/**
	 * 用户中心首页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/index.htm")
	public void buyer_index(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		Map map_list = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		String balance = CommUtil.null2String(user.getAvailableBalance());
		if (balance.equals("")) {
			balance = "0";
		}
		String level_name = integralViewTools.query_user_level_name(CommUtil
				.null2String(user.getId()));
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		String photo_url = url + "/"
				+ this.configService.getSysConfig().getMemberIcon().getPath()
				+ "/"
				+ this.configService.getSysConfig().getMemberIcon().getName();
		if (user.getPhoto() != null) {
			photo_url = url + "/" + user.getPhoto().getPath() + "/"
					+ user.getPhoto().getName();
		}
		map_list.put("photo_url", photo_url);
		map_list.put("level_name", level_name);
		map_list.put("balance", balance);
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("status", 0);
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		List<CouponInfo> couponinfos = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.user.id=:user_id and obj.status=:status and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
						params, -1, -1);
		map_list.put("coupon", CommUtil.null2String(couponinfos.size()));
		map_list.put("integral", CommUtil.null2String(user.getIntegral()));
		map_list.put("ret", "true");
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

	/**
	 * 手机端用户优惠券
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/coupon.htm")
	public void buyer_coupon(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount,
			String selectCount) {
		Map json_map = new HashMap();
		List coupon_list = new ArrayList();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("status", 0);
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		List<CouponInfo> couponinfos = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.user.id=:user_id and obj.status=:status and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
						params, CommUtil.null2Int(beginCount),
						CommUtil.null2Int(selectCount));
		for (CouponInfo ci : couponinfos) {
			Map map = new HashMap();
			map.put("coupon_sn", ci.getCoupon_sn());
			map.put("coupon_addTime", ci.getAddTime());
			String status = "未使用";
			if (ci.getStatus() == 1) {
				status = "已使用";
			}
			if (ci.getStatus() == -1) {
				status = "已过期";
			}
			map.put("coupon_status", status);
			map.put("coupon_amount", ci.getCoupon().getCoupon_amount());
			map.put("coupon_order_amount", ci.getCoupon()
					.getCoupon_order_amount());
			map.put("coupon_beginTime", ci.getCoupon().getCoupon_begin_time());
			map.put("coupon_endTime", ci.getCoupon().getCoupon_end_time());
			map.put("coupon_id", ci.getId());
			map.put("coupon_name", ci.getCoupon().getCoupon_name());
			map.put("coupon_info", "优惠" + ci.getCoupon().getCoupon_amount()
					+ "元");
			coupon_list.add(map);
		}
		json_map.put("coupon_list", coupon_list);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机端用户收藏的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/favorite.htm")
	public void buyer_favorite(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount,
			String selectCount) {
		Map json_map = new HashMap();
		List objs = new ArrayList();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("type", 0);
		List<Favorite> favs = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.type=:type",
						params, CommUtil.null2Int(beginCount),
						CommUtil.null2Int(selectCount));

		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (Favorite fav : favs) {
			Map map = new HashMap();
			map.put("goods_id", fav.getGoods_id());
			String goods_main_photo = url
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (fav.getGoods_photo() != null) {// 商品主图片
				goods_main_photo = url
						+ "/"
						+ fav.getGoods_photo()
						+ "_small."
						+ fav.getGoods_photo().substring(
								fav.getGoods_photo().lastIndexOf(".") + 1);
			}
			map.put("goods_photo", goods_main_photo);
			map.put("id", fav.getGoods_id());
			map.put("name", fav.getGoods_name());
			map.put("price", fav.getGoods_current_price());
			map.put("addTime", fav.getAddTime());
			objs.add(map);
		}
		json_map.put("coupon_list", objs);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机端用户的消息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/message.htm")
	public void buyer_message(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount,
			String selectCount) {
		Map json_map = new HashMap();
		List msg_list = new ArrayList();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		List<Message> msgs = this.messageService
				.query("select obj from Message obj where obj.toUser.id=:user_id order by addTime desc",
						params, CommUtil.null2Int(beginCount),
						CommUtil.null2Int(selectCount));
		for (Message obj : msgs) {
			Map map = new HashMap();
			map.put("title", obj.getTitle());
			map.put("content", obj.getContent());
			map.put("addTime", obj.getAddTime());
			map.put("fromUser", obj.getFromUser().getUserName());
			msg_list.add(map);
		}
		json_map.put("msg_list", msg_list);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 判断用户是否绑定手机了,返回code=100,绑定了手机，-100为未绑定
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 */
	@RequestMapping("/app/buyer/hasphone.htm")
	public void hasphone(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		Map json_map = new HashMap();
		int code = -100;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user.getMobile() != null && !user.getMobile().equals("")) {
			code = 100;
			json_map.put("mobile", user.getMobile());
		}
		json_map.put("code", code);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机短信发送
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/app/buyer/account_mobile_sms.htm")
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String mobile, String user_id,
			String token) throws UnsupportedEncodingException {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user.getApp_login_token().equals(token.toLowerCase())) {
			int ret = 0;
			String code = CommUtil.randomInt(6);
			String content = "尊敬的" + user.getUserName() + "您好，您在试图修改"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "用户绑定手机，手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().isSmsEnbale()) {
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					VerifyCode mvc = this.mobileverifycodeService
							.getObjByProperty(null, "mobile", mobile);
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.update(mvc);
					ret = 100;
				} else {
					ret = -100;
				}
			} else {
				ret = -200;
			}
			Map json_map = new HashMap();
			json_map.put("code", ret);
			String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机号码绑定
	 * 
	 * @param request
	 * @param response
	 * @param mobile_verify_code
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/buyer/account_mobile_save.htm")
	public void account_mobile_save(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,
			String mobile, String user_id, String token) throws Exception {
		int code = 0;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			if (user.getApp_login_token().equals(token.toLowerCase())) {
				VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(
						null, "mobile", mobile);
				if (mvc != null
						&& mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
					user.setMobile(mobile);
					this.userService.update(user);
					this.mobileverifycodeService.delete(mvc.getId());
					code = 100;
					// 绑定成功后发送手机短信提醒
					String content = "尊敬的" + user.getUserName() + "您好，您于"
							+ CommUtil.formatLongDate(new Date()) + "绑定手机号成功。["
							+ this.configService.getSysConfig().getTitle()
							+ "]";
					this.msgTools.sendSMS(user.getMobile(), content);
				} else {// 验证码错误，手机绑定失败
					code = -100;
				}
			}
		}
		Map json_map = new HashMap();
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
