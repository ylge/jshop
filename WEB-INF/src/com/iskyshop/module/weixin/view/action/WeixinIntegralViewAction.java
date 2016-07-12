package com.iskyshop.module.weixin.view.action;

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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PredepositLog;
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
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: WapIntegralViewAction.java
 * </p>
 * 
 * <p>
 * Description:wap积分商城控制器,用来控制积分商城所有前端展示、兑换、订单信息
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
 * @author jinxinzhe
 * 
 * @date 2014-1-4
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class WeixinIntegralViewAction {
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
	private OrderFormTools orderFormTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IPredepositLogService predepositLogService;

	@RequestMapping("/wap/integral/index.htm")
	public ModelAndView integral(HttpServletRequest request,
			HttpServletResponse response, String begin, String end,
			String rank, String ig_user_Level) {
		ModelAndView mv = new JModelAndView("wap/integral.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			Map params = new HashMap();
			String sql = "";
			params.put("ig_show", true);
			if (!CommUtil.null2String(ig_user_Level).equals("")
					&& CommUtil.null2String(ig_user_Level).equals("0")) {
				params.put("ig_user_Level", 0);
				sql = " and obj.ig_user_Level=:ig_user_Level";
			} else if (!CommUtil.null2String(ig_user_Level).equals("")
					&& CommUtil.null2String(ig_user_Level).equals("1")) {
				params.put("ig_user_Level", 1);
				sql = " and obj.ig_user_Level=:ig_user_Level";
			} else if (!CommUtil.null2String(ig_user_Level).equals("")
					&& CommUtil.null2String(ig_user_Level).equals("2")) {
				params.put("ig_user_Level", 2);
				sql = " and obj.ig_user_Level=:ig_user_Level";
			} else if (!CommUtil.null2String(ig_user_Level).equals("")
					&& CommUtil.null2String(ig_user_Level).equals("3")) {
				params.put("ig_user_Level", 3);
				sql = " and obj.ig_user_Level=:ig_user_Level";
			} else {
				params.put("ig_user_Level", 0);
				sql = " and obj.ig_user_Level=:ig_user_Level";
			}
			if (!CommUtil.null2String(begin).equals("")
					&& CommUtil.null2String(begin).equals("2000")) {
				params.put("begin", 2000);
				sql = sql + " and obj.ig_goods_integral>=:begin";
			}
			if (!CommUtil.null2String(begin).equals("")
					&& CommUtil.null2String(begin).equals("4000")) {
				params.put("begin", 4000);
				sql = sql + " and obj.ig_goods_integral>=:begin";
			}
			if (!CommUtil.null2String(begin).equals("")
					&& CommUtil.null2String(begin).equals("6000")) {
				params.put("begin", 6000);
				sql = sql + " and obj.ig_goods_integral>=:begin";
			}
			if (!CommUtil.null2String(begin).equals("")
					&& CommUtil.null2String(begin).equals("10000")) {
				params.put("begin", 10000);
				sql = sql + " and obj.ig_goods_integral>=:begin";
			}
			if (!CommUtil.null2String(end).equals("")
					&& CommUtil.null2String(end).equals("1999")) {
				params.put("end", 1999);
				sql = sql + " and obj.ig_goods_integral<=:end";
			}
			if (!CommUtil.null2String(end).equals("")
					&& CommUtil.null2String(end).equals("3999")) {
				params.put("end", 3999);
				sql = sql + " and obj.ig_goods_integral<=:end";
			}
			if (!CommUtil.null2String(end).equals("")
					&& CommUtil.null2String(end).equals("4999")) {
				params.put("end", 4999);
				sql = sql + " and obj.ig_goods_integral<=:end";
			}
			if (!CommUtil.null2String(end).equals("")
					&& CommUtil.null2String(end).equals("9999")) {
				params.put("end", 9999);
				sql = sql + " and obj.ig_goods_integral<=:end";
			}
			sql = sql + " order by obj.addTime desc";
			List<IntegralGoods> integralGoods = this.integralGoodsService
					.query("select obj from IntegralGoods obj where obj.ig_show=:ig_show"
							+ sql, params, -1, 6);
			mv.addObject("integralGoods", integralGoods);
			mv.addObject("integralViewTools", integralViewTools);
			if (SecurityUserHolder.getCurrentUser() != null) {
				mv.addObject("user",
						this.userService.getObjById(SecurityUserHolder
								.getCurrentUser().getId()));
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("end", end);
		mv.addObject("begin", begin);
		mv.addObject("ig_user_Level", ig_user_Level);
		mv.addObject("rank", rank);
		return mv;
	}

	@RequestMapping("/wap/integral/integral_data.htm")
	public ModelAndView intergral_data(HttpServletRequest request,
			HttpServletResponse response, String begin, String end,
			String rank, String ig_user_Level, String begin_count) {
		ModelAndView mv = new JModelAndView("wap/integral_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		String sql = "";
		params.put("ig_show", true);
		if (!CommUtil.null2String(ig_user_Level).equals("")
				&& CommUtil.null2String(ig_user_Level).equals("0")) {
			params.put("ig_user_Level", 0);
			sql = " and obj.ig_user_Level=:ig_user_Level";
		} else if (!CommUtil.null2String(ig_user_Level).equals("")
				&& CommUtil.null2String(ig_user_Level).equals("1")) {
			params.put("ig_user_Level", 1);
			sql = " and obj.ig_user_Level=:ig_user_Level";
		} else if (!CommUtil.null2String(ig_user_Level).equals("")
				&& CommUtil.null2String(ig_user_Level).equals("2")) {
			params.put("ig_user_Level", 2);
			sql = " and obj.ig_user_Level=:ig_user_Level";
		} else if (!CommUtil.null2String(ig_user_Level).equals("")
				&& CommUtil.null2String(ig_user_Level).equals("3")) {
			params.put("ig_user_Level", 3);
			sql = " and obj.ig_user_Level=:ig_user_Level";
		} else {
			params.put("ig_user_Level", 0);
			sql = " and obj.ig_user_Level=:ig_user_Level";
		}
		if (!CommUtil.null2String(begin).equals("")
				&& CommUtil.null2String(begin).equals("2000")) {
			params.put("begin", 2000);
			sql = sql + " and obj.ig_goods_integral>=:begin";
		}
		if (!CommUtil.null2String(begin).equals("")
				&& CommUtil.null2String(begin).equals("4000")) {
			params.put("begin", 4000);
			sql = sql + " and obj.ig_goods_integral>=:begin";
		}
		if (!CommUtil.null2String(begin).equals("")
				&& CommUtil.null2String(begin).equals("6000")) {
			params.put("begin", 6000);
			sql = sql + " and obj.ig_goods_integral>=:begin";
		}
		if (!CommUtil.null2String(begin).equals("")
				&& CommUtil.null2String(begin).equals("10000")) {
			params.put("begin", 10000);
			sql = sql + " and obj.ig_goods_integral>=:begin";
		}
		if (!CommUtil.null2String(end).equals("")
				&& CommUtil.null2String(end).equals("1999")) {
			params.put("end", 1999);
			sql = sql + " and obj.ig_goods_integral<=:end";
		}
		if (!CommUtil.null2String(end).equals("")
				&& CommUtil.null2String(end).equals("3999")) {
			params.put("end", 3999);
			sql = sql + " and obj.ig_goods_integral<=:end";
		}
		if (!CommUtil.null2String(end).equals("")
				&& CommUtil.null2String(end).equals("4999")) {
			params.put("end", 4999);
			sql = sql + " and obj.ig_goods_integral<=:end";
		}
		if (!CommUtil.null2String(end).equals("")
				&& CommUtil.null2String(end).equals("9999")) {
			params.put("end", 9999);
			sql = sql + " and obj.ig_goods_integral<=:end";
		}
		sql = sql + " order by obj.addTime desc";
		List<IntegralGoods> integralGoods = this.integralGoodsService.query(
				"select obj from IntegralGoods obj where obj.ig_show=:ig_show"
						+ sql, params, CommUtil.null2Int(begin_count), 6);
		mv.addObject("integralGoods", integralGoods);
		mv.addObject("integralViewTools", integralViewTools);
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		mv.addObject("end", end);
		mv.addObject("begin", begin);
		mv.addObject("ig_user_Level", ig_user_Level);
		mv.addObject("begin_count", begin_count);
		mv.addObject("rank", rank);
		return mv;
	}

	@RequestMapping("/wap/integral/view.htm")
	public ModelAndView integral_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/integral_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoods obj = this.integralGoodsService.getObjById(CommUtil
					.null2Long(id));
			if (obj != null) {
				obj.setIg_click_count(obj.getIg_click_count() + 1);
				this.integralGoodsService.update(obj);
				List<IntegralGoodsCart> gcs = this.integralGoodsCartService
						.query("select obj from IntegralGoodsCart obj order by obj.addTime desc",
								null, 0, 20);
				mv.addObject("gcs", gcs);
				mv.addObject("obj", obj);
				if (SecurityUserHolder.getCurrentUser() != null) {
					mv.addObject("user", this.userService
							.getObjById(SecurityUserHolder.getCurrentUser()
									.getId()));
				}
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不存在该商品，参数错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/integral/index.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/integral/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分兑换第一步", value = "/wap/integral/exchange1.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/exchange1.htm")
	public ModelAndView integral_exchange1(HttpServletRequest request,
			HttpServletResponse response, String id, String exchange_count) {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoods obj = this.integralGoodsService.getObjById(CommUtil
					.null2Long(id));
			int exchange_status = 0;// -1为数量不足，-2为限制兑换，-3为积分不足，-4为兑换时间已过,-5为会员等级不够，0为正常兑换
			if (obj != null) {
				if (exchange_count == null || exchange_count.equals("")) {
					exchange_count = "1";
				}
				if (obj.getIg_goods_count() < CommUtil.null2Int(exchange_count)) {
					exchange_status = -1;
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "库存数量不足，重新选择兑换数量");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/integral/view.htm?id=" + id);
				}
				if (obj.isIg_limit_type()
						&& obj.getIg_limit_count() < CommUtil
								.null2Int(exchange_count)) {
					exchange_status = -2;
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "限制最多兑换" + obj.getIg_limit_count()
							+ "，重新选择兑换数量");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/integral/view.htm?id=" + id);
				}
				int cart_total_integral = obj.getIg_goods_integral()
						* CommUtil.null2Int(exchange_count);
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				if (user.getIntegral() < cart_total_integral) {
					exchange_status = -3;
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的积分不足");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/integral/view.htm?id=" + id);
				}
				if (obj.isIg_time_type()) {
					if (obj.getIg_begin_time() != null
							&& obj.getIg_end_time() != null) {
						if ((obj.getIg_begin_time().after(new Date()) || obj
								.getIg_end_time().before(new Date()))) {
							exchange_status = -4;
							mv = new JModelAndView("wap/error.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "兑换已经过期");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/wap/integral/view.htm?id=" + id);
						}
					}
				}
				if (this.integralViewTools.query_user_level(CommUtil
						.null2String(user.getId())) < obj.getIg_user_Level()) {
					exchange_status = -5;
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的会员等级不够");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/integral/view.htm?id=" + id);
				}
			}

			if (exchange_status == 0) {
				Map map = new HashMap();
				map.put("user_id", SecurityUserHolder.getCurrentUser().getId()
						.toString());
				List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
						.query(""
								+ "select obj from IntegralGoodsCart obj where obj.user_id=:user_id",
								map, -1, -1);
				if (integral_goods_cart == null) {
					integral_goods_cart = new ArrayList<IntegralGoodsCart>();
				}
				boolean add = obj == null ? false : true;
				IntegralGoodsCart same = new IntegralGoodsCart();
				for (IntegralGoodsCart igc : integral_goods_cart) {
					if (igc.getGoods().getId().toString().equals(id)) {
						add = false;
						same = igc;
						break;
					}
				}
				User user = SecurityUserHolder.getCurrentUser();
				if (add) {
					IntegralGoodsCart gc = new IntegralGoodsCart();
					gc.setAddTime(new Date());
					gc.setCount(CommUtil.null2Int(exchange_count));
					gc.setGoods(obj);
					gc.setTrans_fee(obj.getIg_transfee());
					gc.setIntegral(CommUtil.null2Int(exchange_count)
							* obj.getIg_goods_integral());
					gc.setUser_id(user.getId().toString());
					this.integralGoodsCartService.save(gc);
					integral_goods_cart.add(gc);
				} else {
					IntegralGoodsCart gc = same;
					gc.setAddTime(new Date());
					gc.setCount(CommUtil.null2Int(exchange_count)
							+ gc.getCount());
					gc.setTrans_fee(obj.getIg_transfee());
					gc.setIntegral(gc.getCount() * obj.getIg_goods_integral());
					this.integralGoodsCartService.update(gc);
				}
				mv.addObject("op_title", "积分购物车添加成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/integral/integral_cart.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分兑换购物车", value = "/wap/integral/integral_cart.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/integral_cart.htm")
	public ModelAndView integral_cart(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/integral_exchange1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map map = new HashMap();
		map.put("user_id", user.getId().toString());
		map.put("ig_show", true);
		List<IntegralGoodsCart> integral_goods_cart = this.integralGoodsCartService
				.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.goods.ig_show=:ig_show",
						map, -1, -1);
		map.put("ig_show", false);
		List<IntegralGoodsCart> integral_goods_cart_false = this.integralGoodsCartService
				.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.goods.ig_show=:ig_show",
						map, -1, -1);
		int total_integral = 0;
		BigDecimal ship_price = new BigDecimal("0");
		for (IntegralGoodsCart igc : integral_goods_cart) {
			total_integral = total_integral + igc.getIntegral();
			if (igc.getGoods().getIg_transfee_type() == 1) {
				ship_price = ship_price.add(igc.getTrans_fee());
			}
		}
		mv.addObject("total_integral", total_integral);
		mv.addObject("ship_price", ship_price);
		mv.addObject("integral_cart", integral_goods_cart);
		mv.addObject("integral_cart_false", integral_goods_cart_false);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "积分兑换删除购物车", value = "/wap/integral/cart_remove.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/cart_remove.htm")
	public void integral_cart_remove(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = SecurityUserHolder.getCurrentUser();
		Map map1 = new HashMap();
		map1.put("user_id", user.getId().toString());
		this.integralGoodsCartService.delete(CommUtil.null2Long(id));
		List<IntegralGoodsCart> igcs = this.integralGoodsCartService
				.query("select obj from IntegralGoodsCart obj where obj.user_id=:user_id",
						map1, -1, -1);
		int total_integral = 0;
		BigDecimal ship_price = new BigDecimal("0");
		for (IntegralGoodsCart igc : igcs) {
			total_integral = total_integral + igc.getIntegral();
			if (igc.getGoods().getIg_transfee_type() == 1) {
				ship_price = ship_price.add(igc.getTrans_fee());
			}
		}
		Map map = new HashMap();
		map.put("status", 100);
		map.put("total_integral", total_integral);
		map.put("ship_price", ship_price);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "积分兑换第二步", value = "/wap/integral/exchange2.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/exchange2.htm")
	public ModelAndView integral_exchange2(HttpServletRequest request,
			HttpServletResponse response, String id, String exchange_count) {
		ModelAndView mv = new JModelAndView("wap/integral_exchange2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			Map map = new HashMap();
			map.put("user_id", SecurityUserHolder.getCurrentUser().getId()
					.toString());
			map.put("ig_show", true);
			List<IntegralGoodsCart> igcs = this.integralGoodsCartService
					.query(""
							+ "select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.goods.ig_show=:ig_show",
							map, -1, -1);
			if (igcs.size() > 0) {
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				Map params = new HashMap();
				params.put("user_id", SecurityUserHolder.getCurrentUser()
						.getId());
				List<Address> addrs = this.addressService
						.query("select obj from Address obj where obj.user.id=:user_id",
								params, -1, -1);
				if (addrs.size() >= 1) {
					mv.addObject("addrs", addrs);
				}
				params.put("default_val", 1);
				List<Address> addrs_default_val = this.addressService
						.query("select obj from Address obj where obj.user.id=:user_id and obj.default_val=:default_val",
								params, -1, -1);
				if (addrs_default_val.size() > 0) {
					mv.addObject("addrs_default_val", addrs_default_val.get(0));
				}
				mv.addObject("igcs",
						igcs == null ? new ArrayList<IntegralGoodsCart>()
								: igcs);
				int total_integral = 0;
				double trans_fee = 0;
				for (IntegralGoodsCart igc : igcs) {
					total_integral = total_integral + igc.getIntegral();
					trans_fee = CommUtil.null2Double(igc.getTrans_fee())
							+ trans_fee;
				}
				if (user.getIntegral() < total_integral) {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换积分不足");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/integral/exchange1.htm");
					return mv;
				}
				mv.addObject("trans_fee", trans_fee);
				mv.addObject("total_integral", total_integral);
				String integral_order_session = CommUtil.randomString(32);
				mv.addObject("integral_order_session", integral_order_session);
				request.getSession(false).setAttribute(
						"integral_order_session", integral_order_session);
				map.clear();
				map.put("user_id", user.getId());
				List<Address> addresses = this.addressService
						.query("select obj from Address obj where obj.user.id=:user_id",
								map, -1, -1);
				mv.addObject("addresses", addresses);
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "兑换购物车为空");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/integral/index.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分兑换第三步", value = "/wap/integral/exchange3.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/exchange3.htm")
	public ModelAndView integral_exchange3(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String igo_msg,
			String integral_order_session) {
		ModelAndView mv = new JModelAndView("wap/integral_exchange3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			List<IntegralGoodsCart> igcs = this.integralGoodsCartService
					.query(""
							+ "select obj from IntegralGoodsCart obj where obj.user_id="
							+ SecurityUserHolder.getCurrentUser().getId()
									.toString(), null, -1, -1);
			String integral_order_session1 = CommUtil.null2String(request
					.getSession(false).getAttribute("integral_order_session"));
			if (integral_order_session1.equals("")) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "表单已经过期");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/integral/index.htm");
			} else {
				if (integral_order_session1.equals(integral_order_session
						.trim())) {
					if (igcs != null) {
						int total_integral = 0;
						double trans_fee = 0;
						for (IntegralGoodsCart igc : igcs) {
							total_integral = total_integral + igc.getIntegral();
							trans_fee = CommUtil
									.null2Double(igc.getTrans_fee())
									+ trans_fee;
						}
						IntegralGoodsOrder order = new IntegralGoodsOrder();
						Address addr = this.addressService.getObjById(CommUtil
								.null2Long(addr_id));
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
							json_map.put("ig_goods_img",
									CommUtil.getURL(request)
											+ "/"
											+ gc.getGoods().getIg_goods_img()
													.getPath()
											+ "/"
											+ gc.getGoods().getIg_goods_img()
													.getName()
											+ "_small."
											+ gc.getGoods().getIg_goods_img()
													.getExt());
							json_list.add(json_map);
						}
						String json = Json.toJson(json_list,
								JsonFormat.compact());
						order.setGoods_info(json);// 商品信息json
						order.setIgo_msg(igo_msg);
						User user = this.userService
								.getObjById(SecurityUserHolder.getCurrentUser()
										.getId());
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
							request.getSession(false).removeAttribute(
									"integral_goods_cart");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/wap/buyer/integral_order_list.htm");
							mv.addObject("order", order);
						} else {
							order.setIgo_status(0);// 有运费订单，默认状态为未付款
							this.integralGoodsOrderService.save(order);
							mv = new JModelAndView(
									"wap/integral_exchange4.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("obj", order);
							mv.addObject("paymentTools", paymentTools);
						}
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
						request.getSession(false).removeAttribute(
								"integral_goods_cart");
					} else {
						mv = new JModelAndView("wap/error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换购物车为空");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/wap/integral/index.htm");
					}
				} else {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "参数错误，订单提交失败");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/index.htm");
				}
			}

		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分兑换选择支付方式", value = "/wap/integral/order_pay.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/order_pay.htm")
	public ModelAndView integral_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType,
			String integral_order_id) {
		ModelAndView mv = null;
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(integral_order_id));
		if ("wx_pay".equals(payType)) {
			String type = "integral";
			try {
				response.sendRedirect("/weixin/pay/wx_pay.htm?id="
						+ integral_order_id + "&showwxpaytitle=1&type=" + type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			}
		}
		if (order.getIgo_status() == 0) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/integral/index.htm");
			} else {
				// 给订单添加支付方式
				order.setIgo_payment(payType);
				this.integralGoodsOrderService.update(order);
				if (payType.equals("balance")) {
					mv = new JModelAndView("wap/integral_balance_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("paymentTools", paymentTools);
				} else {
					mv = new JModelAndView("wap/line_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", payTools);
					mv.addObject("type", "integral");
					Map params = new HashMap();
					params.put("install", true);
					params.put("mark", payType);
					List<Payment> payments = this.paymentService
							.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
									params, -1, -1);
					mv.addObject("payment_id", payments.size() > 0 ? payments
							.get(0).getId() : new Payment());
				}
				mv.addObject("integral_order_id", integral_order_id);
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单不能进行付款！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/integral/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分兑换预存款支付", value = "/wap/integral/order_pay_balance.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/order_pay_balance.htm")
	public ModelAndView integral_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType,
			String integral_order_id, String igo_pay_msg) {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(integral_order_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (order.getIgo_user().getId() == user.getId()) {
			if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil
					.null2Double(order.getIgo_trans_fee())) {
				order.setIgo_pay_msg(igo_pay_msg);
				order.setIgo_status(20);
				order.setIgo_payment("balance");
				order.setIgo_pay_time(new Date());
				boolean ret = this.integralGoodsOrderService.update(order);
				if (ret) {
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.subtract(user.getAvailableBalance(),
									order.getIgo_trans_fee())));
					this.userService.update(user);
					// 执行库存减少
					List<IntegralGoods> igs = this.orderFormTools
							.query_integral_all_goods(CommUtil
									.null2String(order.getId()));
					for (IntegralGoods obj : igs) {
						int count = this.orderFormTools
								.query_integral_one_goods_count(order,
										CommUtil.null2String(obj.getId()));
						obj.setIg_goods_count(obj.getIg_goods_count() - count);
						obj.setIg_exchange_count(obj.getIg_exchange_count()
								+ count);
						this.integralGoodsService.update(obj);
					}
				}
				// 记录预存款日志
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(user);
				log.setPd_log_amount(order.getIgo_trans_fee());
				log.setPd_op_type("消费");
				log.setPd_type("可用预存款");
				log.setPd_log_info("订单" + order.getIgo_order_sn()
						+ "兑换礼品减少可用预存款");
				this.predepositLogService.save(log);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/integral_order_list.htm");
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "可用余额不足，支付失败！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/integral_order_list.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/integral_order_list.htm");
		}

		return mv;
	}

	@RequestMapping("/wap/integral/order_finish.htm")
	public ModelAndView integral_order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("wap/integral_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "积分兑换去支付", value = "/wap/integral/order_pay_view.htm*", rtype = "buyer", rname = "wap端积分兑换", rcode = "wap_integral_exchange", rgroup = "wap端积分兑换")
	@RequestMapping("/wap/integral/order_pay_view.htm")
	public ModelAndView integral_order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/integral_exchange4.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getIgo_status() == 0) {
			mv.addObject("obj", obj);
			mv.addObject("paymentTools", this.paymentTools);
			mv.addObject("url", CommUtil.getURL(request));
		} else if (obj.getIgo_status() < 0) {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经取消！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/integral_order_list.htm");
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经付款！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/integral_order_list.htm");
		}
		return mv;
	}

	@RequestMapping("/wap/integral/adjust_count.htm")
	public void integral_adjust_count(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String count) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = new HashMap();
		map.put("user_id", user.getId().toString());
		map.put("ig_show", true);
		List<IntegralGoodsCart> igcs = this.integralGoodsCartService
				.query(""
						+ "select obj from IntegralGoodsCart obj where obj.user_id=:user_id and obj.goods.ig_show=:ig_show",
						map, -1, -1);
		IntegralGoodsCart obj = null;
		int old_num = 0;
		int num = CommUtil.null2Int(count);
		for (IntegralGoodsCart igc : igcs) {
			if (igc.getGoods().getId().toString().equals(goods_id)) {
				IntegralGoods ig = igc.getGoods();
				old_num = igc.getCount();
				if (num > ig.getIg_goods_count()) {
					num = ig.getIg_goods_count();
				}
				if (ig.isIg_limit_type() && ig.getIg_limit_count() < num) {
					num = ig.getIg_limit_count();
				}
				igc.setCount(num);
				igc.setIntegral(igc.getGoods().getIg_goods_integral()
						* CommUtil.null2Int(num));
				this.integralGoodsCartService.update(igc);
				obj = igc;
				break;
			}
		}

		int total_integral = 0;
		for (IntegralGoodsCart igc : igcs) {
			total_integral = total_integral + igc.getIntegral();
		}
		// 判断积分
		if (total_integral > user.getIntegral()) {
			for (IntegralGoodsCart igc : igcs) {
				if (igc.getGoods().getId().toString().equals(goods_id)) {
					num = old_num;
					IntegralGoods ig = igc.getGoods();
					igc.setCount(num);
					igc.setIntegral(igc.getGoods().getIg_goods_integral()
							* CommUtil.null2Int(num));
					this.integralGoodsCartService.update(igc);
					obj = igc;
					break;
				}
			}
			total_integral = 0;
			for (IntegralGoodsCart igc : igcs) {
				total_integral = total_integral + igc.getIntegral();
			}
		}
		request.getSession(false).setAttribute("integral_goods_cart", igcs);
		map.clear();
		map.put("total_integral", total_integral);
		map.put("integral", obj.getIntegral());
		map.put("count", num);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
