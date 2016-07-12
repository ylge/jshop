package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IIntegralGoodsCartService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: IntegralViewAction.java
 * </p>
 * 
 * <p>
 * Description: app积分兑换
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
 * @author lixiaoyang
 * 
 * @date 2015-1-12
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppIntegralViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralGoodsCartService integralGoodsCartService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IPredepositLogService predepositLogService;

	/**
	 * app积分商城首页
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/integral_index.htm")
	public void integral_index(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (user_id != null) {
			if (verify && user_id != null && !user_id.equals("")
					&& token != null && !token.equals("")) {
				String url = CommUtil.getURL(request);
				if (!"".equals(CommUtil.null2String(this.configService
						.getSysConfig().getImageWebServer()))) {
					url = this.configService.getSysConfig().getImageWebServer();
				}
				User user = this.userService.getObjById(CommUtil
						.null2Long(user_id));
				if (user != null
						&& user.getApp_login_token()
								.equals(token.toLowerCase())) {
					json_map.put("username", user.getUsername());// 用户名
					String photo_url = url
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getPath()
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getName();
					if (user.getPhoto() != null) {
						photo_url = url + "/" + user.getPhoto().getPath() + "/"
								+ user.getPhoto().getName();
					}
					json_map.put("user_img", photo_url);// 用户头像
					json_map.put("integral", user.getIntegral());// 积分
					json_map.put("user_level", integralViewTools
							.query_user_level(user.getId().toString()));// 用户等级0—铜牌会员1—银牌会员2—金牌会员3—超级会员
					json_map.put("user_level_name", integralViewTools
							.query_user_level_name(user.getId().toString()));// 用户等级名字
				}
			}
		}
		// 加载积分首页积分商品信息
		Map params = new HashMap();
		params.put("recommend", true);// 加载推荐商品
		params.put("show", true);
		List<IntegralGoods> recommend_igs = new ArrayList<IntegralGoods>();

		recommend_igs = this.integralGoodsService
				.query("select obj from IntegralGoods obj where obj.ig_recommend=:recommend and obj.ig_show=:show order by obj.ig_sequence asc",
						params, 0, 16);
		List recommend_list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (IntegralGoods integralGoods : recommend_igs) {
			Map integral_map = new HashMap();
			integral_map.put("ig_id", integralGoods.getId());
			integral_map.put("ig_goods_name", integralGoods.getIg_goods_name());
			integral_map.put("ig_goods_integral",
					integralGoods.getIg_goods_integral());
			integral_map.put("ig_user_level", integralGoods.getIg_user_Level());

			integral_map.put("ig_goods_img", url + "/"
					+ integralGoods.getIg_goods_img().getPath() + "/"
					+ integralGoods.getIg_goods_img().getName() + "_small."
					+ integralGoods.getIg_goods_img().getExt());

			recommend_list.add(integral_map);
		}
		json_map.put("recommend_igs", recommend_list);// 推荐
		json_map.put("ret", CommUtil.null2String(verify));
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
	 * app积分商城列表
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/integral_list.htm")
	public void integral_list(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String begincount, String selectcount, String rang_begin,
			String rang_end) {
		Map map = new HashMap();
		if (user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {

			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				map.put("integral", user.getIntegral());// 积分
				map.put("user_level", integralViewTools.query_user_level(user
						.getId().toString()));// 用户等级0—铜牌会员1—银牌会员2—金牌会员3—超级会员
				map.put("user_level_name", integralViewTools
						.query_user_level_name(user.getId().toString()));// 用户等级名字
			}
		}
		Map params = new HashMap();
		params.put("show", true);
		List<IntegralGoods> recommend_igs = new ArrayList<IntegralGoods>();

		if (rang_begin != null && !rang_begin.equals("") && rang_end != null
				&& !rang_end.equals("")) {
			params.put("rang_begin", CommUtil.null2Int(rang_begin));
			params.put("rang_end", CommUtil.null2Int(rang_end));

			recommend_igs = this.integralGoodsService
					.query("select obj from IntegralGoods obj where obj.ig_goods_integral<=:rang_end and obj.ig_goods_integral>:rang_begin and obj.ig_show=:show order by obj.ig_sequence asc",
							params, CommUtil.null2Int(begincount),
							CommUtil.null2Int(selectcount));
		} else {
			recommend_igs = this.integralGoodsService
					.query("select obj from IntegralGoods obj where obj.ig_show=:show order by obj.ig_sequence asc",
							params, CommUtil.null2Int(begincount),
							CommUtil.null2Int(selectcount));
		}

		List recommend_list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (IntegralGoods integralGoods : recommend_igs) {
			Map integral_map = new HashMap();
			integral_map.put("ig_id", integralGoods.getId());
			integral_map.put("ig_goods_name", integralGoods.getIg_goods_name());
			integral_map.put("ig_goods_integral",
					integralGoods.getIg_goods_integral());
			integral_map.put("ig_user_level", integralGoods.getIg_user_Level());

			integral_map.put("ig_goods_img", url + "/"
					+ integralGoods.getIg_goods_img().getPath() + "/"
					+ integralGoods.getIg_goods_img().getName() + "_small."
					+ integralGoods.getIg_goods_img().getExt());

			recommend_list.add(integral_map);
		}
		map.put("recommend_igs", recommend_list);// 推荐

		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
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
	 * app积分 商品明细
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/integral_goods.htm")
	public void integral_goods(HttpServletRequest request,
			HttpServletResponse response, String ig_id) {
		Map map = new HashMap();
		IntegralGoods integralGoods = this.integralGoodsService
				.getObjById(CommUtil.null2Long(ig_id));
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		map.put("ig_id", integralGoods.getId());
		map.put("ig_goods_name", integralGoods.getIg_goods_name());
		map.put("ig_goods_integral", integralGoods.getIg_goods_integral());
		map.put("ig_user_level", integralGoods.getIg_user_Level());
		map.put("ig_goods_img", url + "/"
				+ integralGoods.getIg_goods_img().getPath() + "/"
				+ integralGoods.getIg_goods_img().getName());
		map.put("ig_goods_price",
				CommUtil.null2Double(integralGoods.getIg_goods_price()));
		map.put("ig_goods_count", integralGoods.getIg_goods_count());// 库存

		if (integralGoods.isIg_limit_type()) {// 限购
			map.put("ig_limit_count",
					"每名会员最多兑换" + integralGoods.getIg_limit_count() + "件");
			map.put("ig_limit_count_choose", integralGoods.getIg_limit_count());
		} else {
			map.put("ig_limit_count", "无限制");
		}
		if (integralGoods.isIg_time_type()) {// 时间
			map.put("ig_time",
					CommUtil.formatShortDate(integralGoods.getIg_begin_time())
							+ "至"
							+ CommUtil.formatShortDate(integralGoods
									.getIg_end_time()));
		} else {
			map.put("ig_time", "无限制");
		}

		if (integralGoods.getIg_transfee_type() == 0) {// 运费
			map.put("transfee", "卖家承担");
		} else {
			map.put("transfee",
					CommUtil.null2Double(integralGoods.getIg_transfee()) + "元");
		}

		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
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

	@RequestMapping("/app/integral_introduce.htm")
	public ModelAndView integral_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("app/simple_goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoods integralGoods = this.integralGoodsService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", integralGoods.getIg_content());
		return mv;
	}

	@RequestMapping("/app/exchange1.htm")
	public void integral_exchange1(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String id, String exchange_count) {
		Map map = new HashMap();
		if (user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {

				IntegralGoods obj = this.integralGoodsService
						.getObjById(CommUtil.null2Long(id));
				int exchange_status = 0;// -1为数量不足，-2为限制兑换，-3为积分不足，-4为兑换时间已过,-5为会员等级不够，0为正常兑换
				if (obj != null) {
					if (exchange_count == null || exchange_count.equals("")) {
						exchange_count = "1";
					}
					if (obj.getIg_goods_count() < CommUtil
							.null2Int(exchange_count)) {
						exchange_status = -1;
						map.put("exchange_info", "库存数量不足，重新选择兑换数量");
					}
					if (obj.isIg_limit_type()
							&& obj.getIg_limit_count() < CommUtil
									.null2Int(exchange_count)) {
						exchange_status = -2;
						map.put("exchange_info",
								"限制最多兑换" + obj.getIg_limit_count()
										+ "，重新选择兑换数量");
					}
					int cart_total_integral = obj.getIg_goods_integral()
							* CommUtil.null2Int(exchange_count);
					if (user.getIntegral() < cart_total_integral) {
						exchange_status = -3;
						map.put("exchange_info", "您的积分不足");
					}
					if (obj.isIg_time_type()) {
						if (obj.getIg_begin_time() != null
								&& obj.getIg_end_time() != null) {
							if ((obj.getIg_begin_time().after(new Date()) || obj
									.getIg_end_time().before(new Date()))) {
								exchange_status = -4;
								map.put("exchange_info", "兑换已经截止");
							}
						}
					}
					if (this.integralViewTools.query_user_level(CommUtil
							.null2String(user.getId())) < obj
							.getIg_user_Level()) {
						exchange_status = -5;
						map.put("exchange_info", "您的会员等级不够");
					}
				}

				if (exchange_status == 0) {
					Map params = new HashMap();
					params.put("user_id", user_id);
					params.put("id", CommUtil.null2Long(id));
					List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
							.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.goods.id=:id",
									params, -1, -1);

					if (integral_goods_cart.size() == 0) {
						IntegralGoodsCart gc = new IntegralGoodsCart();
						gc.setAddTime(new Date());
						gc.setCount(CommUtil.null2Int(exchange_count));
						gc.setGoods(obj);
						gc.setTrans_fee(obj.getIg_transfee());
						gc.setIntegral(CommUtil.null2Int(exchange_count)
								* obj.getIg_goods_integral());
						gc.setUser_id(user_id);
						this.integralGoodsCartService.save(gc);
					}
				}
				map.put("exchange_status", exchange_status);
			}
		}

		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
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

	@RequestMapping("/app/integral_cartlist_size.htm")
	public void integral_cartlist_size(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		Map json = new HashMap();
		if (user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				json.put("user_integral", user.getIntegral());
				Map params = new HashMap();
				params.put("user_id", user_id);
				List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
						.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id",
								params, -1, -1);
				json.put("cart_list_size", integral_goods_cart.size());
			}
		}

		json.put("ret", "true");
		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/integral_cartlist.htm")
	public void integral_cartlist(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		boolean verify = true;
		Map json = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				json.put("user_integral", user.getIntegral());
				Map params = new HashMap();
				params.put("user_id", user_id);
				List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
						.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id",
								params, -1, -1);
				double total_ship = 0;
				List list = new ArrayList();
				for (IntegralGoodsCart integralGoodsCart : integral_goods_cart) {
					Map map = new HashMap();

					map.put("id", integralGoodsCart.getId());
					map.put("count", integralGoodsCart.getCount());
					map.put("trans_fee", integralGoodsCart.getTrans_fee());
					map.put("integral", integralGoodsCart.getIntegral());

					IntegralGoods integralGoods = integralGoodsCart.getGoods();

					map.put("ig_goods_name", integralGoods.getIg_goods_name());
					map.put("ig_goods_img", url + "/"
							+ integralGoods.getIg_goods_img().getPath() + "/"
							+ integralGoods.getIg_goods_img().getName()
							+ "_small."
							+ integralGoods.getIg_goods_img().getExt());
					list.add(map);
				}
				json.put("cart_list", list);
			}
		}

		json.put("ret", CommUtil.null2String(verify));
		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/integral_cart_cal.htm")
	public void integral_cart_cal(HttpServletRequest request,
			HttpServletResponse response, String cart_ids) {

		Map json_map = new HashMap();

		List<IntegralGoodsCart> list = new ArrayList<IntegralGoodsCart>();
		int length = 0;
		if (cart_ids != null && !cart_ids.equals("")) {
			String ids[] = cart_ids.split(",");
			length = ids.length;
			for (String id : ids) {
				IntegralGoodsCart integralGoodsCart = this.integralGoodsCartService
						.getObjById(CommUtil.null2Long(id));
				list.add(integralGoodsCart);
			}
		}
		json_map.putAll(cal_price(list));
		json_map.put("size", length);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	private Map cal_price(List<IntegralGoodsCart> list) {
		// TODO Auto-generated method stub
		double all_integral = 0;
		double all_shipfee = 0;
		for (IntegralGoodsCart integralGoodsCart : list) {
			all_integral += integralGoodsCart.getIntegral();
			all_shipfee += CommUtil.null2Double(integralGoodsCart
					.getTrans_fee());
		}
		Map map = new HashMap();
		map.put("all_integral", all_integral);
		map.put("all_shipfee", all_shipfee);
		return map;
	}

	@RequestMapping("/app/integral_count_adjust.htm")
	public void integral_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_id, String count) {
		boolean verify = true;
		Map map = new HashMap();
		int code = 0;
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				IntegralGoodsCart igc = this.integralGoodsCartService
						.getObjById(CommUtil.null2Long(cart_id));
				if (igc != null) {
					IntegralGoodsCart integralgc = igc;
					if (integralgc.getGoods().getIg_goods_count() > CommUtil
							.null2Int(count)) {
						if ((integralgc.getGoods().isIg_limit_type() && integralgc
								.getGoods().getIg_limit_count() >= CommUtil
								.null2Int(count))
								|| !integralgc.getGoods().isIg_limit_type()) {
							integralgc.setCount(CommUtil.null2Int(count));
							integralgc.setIntegral(integralgc.getGoods()
									.getIg_goods_integral()
									* CommUtil.null2Int(count));
							this.integralGoodsCartService.update(integralgc);
							code = 100;
						} else {
							code = -300;
						}
					} else {
						code = -100;
					}
				} else {
					code = -200;
				}
			}
		}
		map.put("code", code);
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/integral_cart_del.htm")
	public void integral_cart_del(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_ids) {
		boolean verify = true;
		Map map = new HashMap();
		int code = 0;
		String dele_cart_ids = "";
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				if (cart_ids != null && !cart_ids.equals("")) {
					String ids[] = cart_ids.split(",");
					for (String id : ids) {
						Map params = new HashMap();
						params.put("user_id", user_id);
						params.put("id", CommUtil.null2Long(id));
						List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
								.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.id=:id",
										params, -1, -1);
						if (integral_goods_cart.size() > 0) {
							this.integralGoodsCartService.delete(CommUtil
									.null2Long(id));
							dele_cart_ids += id + ",";
							code = 100;
						}
					}
				}
			}
		}
		map.put("code", code);
		map.put("dele_cart_ids", dele_cart_ids);
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/exchange2.htm")
	public void integral_exchange2(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_ids, String addr_id, String igo_msg, String igo_payment) {
		boolean verify = true;
		Map map = new HashMap();
		int code = 0;
		String dele_cart_ids = "";
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& user.getApp_login_token().equals(token.toLowerCase())) {
				String url = CommUtil.getURL(request);
				if (!"".equals(CommUtil.null2String(this.configService
						.getSysConfig().getImageWebServer()))) {
					url = this.configService.getSysConfig().getImageWebServer();
				}
				if (cart_ids != null && !cart_ids.equals("")) {
					String ids[] = cart_ids.split(",");
					List<IntegralGoodsCart> igcs = new ArrayList<IntegralGoodsCart>();
					for (String id : ids) {
						IntegralGoodsCart integralGoodsCart = this.integralGoodsCartService
								.getObjById(CommUtil.null2Long(id));
						igcs.add(integralGoodsCart);
					}
					Map price = this.cal_price(igcs);
					int total_integral = Math.round(CommUtil.null2Float(price
							.get("all_integral")));
					if (total_integral < user.getIntegral()) {// 用户有足够积分
						double trans_fee = CommUtil.null2Double(price
								.get("all_shipfee"));
						Address addr = addr = this.addressService
								.getObjById(CommUtil.null2Long(addr_id));

						IntegralGoodsOrder order = new IntegralGoodsOrder();
						order.setAddTime(new Date());
						// 设置收货地址信息
						order.setReceiver_Name(addr.getTrueName());
						order.setReceiver_area(addr.getArea().getParent()
								.getParent().getAreaName()
								+ addr.getArea().getParent().getAreaName()
								+ addr.getArea().getAreaName());
						order.setReceiver_area_info(addr.getArea_info());
						order.setReceiver_mobile(addr.getMobile());
						order.setReceiver_telephone(addr.getTelephone());
						order.setReceiver_zip(addr.getZip());
						List<Map> json_list = new ArrayList<Map>();
						for (IntegralGoodsCart gc : igcs) {
							Map json_map = new HashMap();
							json_map.put("id", gc.getGoods().getId());
							json_map.put("ig_goods_name", gc.getGoods()
									.getIg_goods_name());
							json_map.put("ig_goods_price", gc.getGoods()
									.getIg_goods_price());
							json_map.put("ig_goods_count", gc.getCount());
							json_map.put("ig_goods_img", url + "/"
									+ gc.getGoods().getIg_goods_img().getPath()
									+ "/"
									+ gc.getGoods().getIg_goods_img().getName()
									+ "_small."
									+ gc.getGoods().getIg_goods_img().getExt());
							json_list.add(json_map);
						}
						String json = Json.toJson(json_list,
								JsonFormat.compact());
						order.setGoods_info(json);// 商品信息json
						order.setIgo_msg(igo_msg);
						order.setIgo_order_sn("igo"
								+ CommUtil.formatTime("yyyyMMddHHmmss",
										new Date()) + user.getId());
						order.setIgo_user(user);
						order.setIgo_trans_fee(BigDecimal.valueOf(trans_fee));
						order.setIgo_total_integral(total_integral);
						if (trans_fee == 0) {
							order.setIgo_status(20);// 无运费订单，默认状态为已付款
							order.setIgo_pay_time(new Date());
							order.setIgo_payment("no_fee");
							this.integralGoodsOrderService.save(order);
							for (IntegralGoodsCart igc : igcs) {
								IntegralGoods goods = igc.getGoods();
								goods.setIg_goods_count(goods
										.getIg_goods_count() - igc.getCount());
								goods.setIg_exchange_count(goods
										.getIg_exchange_count()
										+ igc.getCount());
								this.integralGoodsService.update(goods);
							}
							code = 200;// 支付成功
						} else {
							order.setIgo_status(0);// 有运费订单，默认状态为未付款
							order.setIgo_payment(igo_payment);
							this.integralGoodsOrderService.save(order);
							code = 100;// 待支付
						}
						map.put("order_sn", order.getIgo_order_sn());
						map.put("order_id", order.getId());
						for (IntegralGoodsCart igc : igcs) {
							this.integralGoodsCartService.delete(igc.getId());
						}
						// 用户积分减少
						user.setIntegral(user.getIntegral()
								- order.getIgo_total_integral());
						this.userService.update(user);
						// 记录日志
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("兑换商品消耗积分");
						log.setIntegral(-order.getIgo_total_integral());
						log.setIntegral_user(user);
						log.setType("integral_order");
						this.integralLogService.save(log);
					} else {
						code = -200;// 积分不足
					}
				} else {
					code = -100;// 购物车为空
				}
			}
		}
		map.put("code", code);
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
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
