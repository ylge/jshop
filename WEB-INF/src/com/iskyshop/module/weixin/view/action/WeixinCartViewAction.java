package com.iskyshop.module.weixin.view.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.buyer.tools.CartTools;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;
import com.iskyshop.manage.seller.tools.CombinTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.BuyGiftViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.GroupViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * 
 * <p>
 * Title:WapCartViewAction.java
 * </p>
 * 
 * <p>
 * Description:移动端用户中心购物车控制器
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
 * @date 2014年8月20日
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinCartViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IIntegralGoodsOrderService igorderService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityTools;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private DeliveryAddressTools DeliveryAddressTools;
	@Autowired
	private AreaViewTools areaViewTools;

	/**
	 * 设置购物车的规格文字说明，将原有规格的名字替换成自定义的
	 * 
	 * @param goods
	 * @param obj
	 * @param gsp_ids
	 */
	void setGoodsCartSpec(Goods goods, GoodsCart obj, String[] gsp_ids) {
		String spec_info = "";
		List<Map> goods_specs_info = goods.getGoods_specs_info() == null ? new ArrayList<Map>()
				: (List<Map>) Json.fromJson(goods.getGoods_specs_info());
		for (String gsp_id : gsp_ids) {
			GoodsSpecProperty spec_property = this.goodsSpecPropertyService
					.getObjById(CommUtil.null2Long(gsp_id));
			if (spec_property != null) {
				obj.getGsps().add(spec_property);
				spec_info += spec_property.getSpec().getName() + "：";
				if (goods_specs_info.size() > 0) {
					for (Map map : goods_specs_info) {
						if (CommUtil.null2Long(map.get("id")).equals(
								spec_property.getId())) {
							spec_info += map.get("name").toString();
						}
					}
				} else {
					spec_info += spec_property.getValue();
				}
				spec_info += "<br>";
			}
		}
		obj.setSpec_info(spec_info);
	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            添加到购物车的商品id
	 * @param count
	 *            添加到购物车的商品数量
	 * @param price
	 *            添加到购物车的商品的价格,该逻辑会更加gsp再次计算实际价格，避免用户在前端篡改
	 * @param gsp
	 *            商品的属性值，这里传递id值，如12,1,21
	 * @param buy_type
	 *            购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids
	 *            组合搭配中配件id
	 */
	@RequestMapping("/wap/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String suit_gsp, String buy_type,
			String combin_ids) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (CommUtil.null2String(gsp).equals("")) {// 从商品列表页添加到购车，生成默认的gsp信息
			gsp = this.generic_default_gsp(goods);
		}
		int goods_inventory = CommUtil.null2Int(this.generic_default_info(
				goods, gsp).get("count"));// 计算商品库存信息
		Map json_map = new HashMap();
		int next = 0;// -3库存不足, -1添加购物车失败，0普通商品添加,1组合配件添加，2组合套装添加
		if (goods.getF_sale_type() == 0 && goods_inventory > 0) {// 非F码商品且库存大于0，正常加入购物车
			String cart_session_id = "";
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("cart_session_id")) {
						cart_session_id = CommUtil.null2String(cookie
								.getValue());
					}
				}
			}
			if (cart_session_id.equals("")) {
				cart_session_id = UUID.randomUUID().toString();
				Cookie cookie = new Cookie("cart_session_id", cart_session_id);
				cookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(cookie);
			}
			List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
			List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
			List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
			User user = SecurityUserHolder.getCurrentUser();
			Map cart_map = new HashMap();
			if (user != null) {
				user = userService.getObjById(user.getId());
				if (!cart_session_id.equals("")) {
					cart_map.clear();
					cart_map.put("cart_session_id", cart_session_id);
					cart_map.put("cart_status", 0);
					carts_cookie = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
									cart_map, -1, -1);
					// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
					if (user.getStore() != null) {
						for (GoodsCart gc : carts_cookie) {
							if (gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
								if (gc.getGoods().getGoods_store().getId()
										.equals(user.getStore().getId())) {
									this.goodsCartService.delete(gc.getId());
								}
							}
						}
					}
					cart_map.clear();
					cart_map.put("user_id", user.getId());
					cart_map.put("cart_status", 0);
					carts_user = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
									cart_map, -1, -1);

				} else {
					cart_map.clear();
					cart_map.put("user_id", user.getId());
					cart_map.put("cart_status", 0);
					carts_user = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
									cart_map, -1, -1);

				}
			} else {
				if (!cart_session_id.equals("")) {
					cart_map.clear();
					cart_map.put("cart_session_id", cart_session_id);
					cart_map.put("cart_status", 0);
					carts_cookie = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
									cart_map, -1, -1);

				}
			}
			// 将cookie购物车与用户user购物车合并，去重
			if (user != null) {
				for (GoodsCart ugc : carts_user) {
					carts_list.add(ugc);
				}
				for (GoodsCart cookie : carts_cookie) {
					boolean add = true;
					for (GoodsCart gc2 : carts_user) {
						if (cookie.getGoods().getId()
								.equals(gc2.getGoods().getId())) {
							if (cookie.getSpec_info()
									.equals(gc2.getSpec_info())) {
								add = false;
								this.goodsCartService.delete(cookie.getId());
							}
						}
					}
					if (add) {// 将cookie_cart转变为user_cart
						cookie.setCart_session_id(null);
						cookie.setUser(user);
						this.goodsCartService.update(cookie);
						carts_list.add(cookie);
					}
				}
			} else {
				for (GoodsCart cookie : carts_cookie) {
					carts_list.add(cookie);
				}
			}
			String temp_gsp = gsp;
			if ("parts".equals(buy_type)) {
				if (combin_ids != null && !combin_ids.equals("")) {
					next = 1;
				}

			}
			if ("suit".equals(buy_type)) {
				if (combin_ids != null && !combin_ids.equals("")) {
					next = 2;
				}
			}

			GoodsCart obj = new GoodsCart();
			boolean add = true;
			String[] gsp_ids = CommUtil.null2String(temp_gsp).split(",");
			Arrays.sort(gsp_ids);
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (!"combin".equals(gc.getCart_type())// 排除套装中商品
							&& gc.getGoods().getId().toString().equals(id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (!"combin".equals(gc.getCart_type())
							&& gc.getGoods().getId().toString().equals(id)) {
						add = false;
					}
				}
			}
			if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				if (price == null || price.equals("")) {
					price = this.generGspgoodsPrice(temp_gsp, id);
				}
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				setGoodsCartSpec(goods, obj, gsp_ids);
				if (user == null) {
					obj.setCart_session_id(cart_session_id);
				} else {
					obj.setUser(user);
				}
				this.goodsCartService.save(obj);
			}
			if (next == 1) {// 组合配件商品添加
				String part_ids[] = combin_ids.split(",");
				for (String part_id : part_ids) {
					if (!part_id.equals("")) {
						Goods part_goods = this.goodsService
								.getObjById(CommUtil.null2Long(part_id));
						GoodsCart part_cart = new GoodsCart();
						boolean part_add = true;
						part_cart.setAddTime(new Date());
						String temp_gsp_parts = null;
						temp_gsp_parts = this.generic_default_gsp(part_goods);
						String[] part_gsp_ids = CommUtil.null2String(
								temp_gsp_parts).split(",");
						Arrays.sort(part_gsp_ids);
						for (GoodsCart gc : carts_list) {
							if (part_gsp_ids != null && part_gsp_ids.length > 0
									&& gc.getGsps() != null
									&& gc.getGsps().size() > 0) {
								String[] gsp_ids1 = new String[gc.getGsps()
										.size()];
								for (int i = 0; i < gc.getGsps().size(); i++) {
									gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
											.getGsps().get(i).getId()
											.toString()
											: "";
								}
								Arrays.sort(gsp_ids1);
								if (gc.getGoods().getId().toString()
										.equals(part_id)
										&& Arrays
												.equals(part_gsp_ids, gsp_ids1)) {
									part_add = false;
								}
							} else {
								if (gc.getGoods().getId().toString()
										.equals(part_id)) {
									part_add = false;
								}
							}
						}
						if (part_add) {// 排除购物车中没有重复商品后添加该商品到购物车
							part_cart.setAddTime(new Date());
							part_cart.setCount(CommUtil.null2Int(1));
							String part_price = this.generGspgoodsPrice(
									temp_gsp_parts, part_id);
							part_cart.setPrice(BigDecimal.valueOf(CommUtil
									.null2Double(part_price)));
							part_cart.setGoods(part_goods);
							setGoodsCartSpec(part_goods, part_cart,
									part_gsp_ids);
							if (user == null) {
								part_cart.setCart_session_id(cart_session_id);
							} else {
								part_cart.setUser(user);
							}
							this.goodsCartService.save(part_cart);
						}
					}
				}
			}
			if (next == 2) {// 组合套装商品添加
				Map suit_map = null;
				Map params = new HashMap();
				params.put("main_goods_id", CommUtil.null2Long(id));
				params.put("combin_type", 0);// 组合套装
				params.put("combin_status", 1);
				List<CombinPlan> suits = this.combinplanService
						.query("select obj from CombinPlan obj where obj.main_goods_id=:main_goods_id and obj.combin_type=:combin_type and obj.combin_status=:combin_status",
								params, -1, -1);
				for (CombinPlan plan : suits) {
					List<Map> map_list = (List<Map>) Json.fromJson(plan
							.getCombin_plan_info());
					for (Map temp_map : map_list) {
						String ids = this.goodsViewTools
								.getCombinPlanGoodsIds(temp_map);
						if (ids.contains(combin_ids)) {
							suit_map = temp_map;
							break;
						}
					}
				}
				String combin_mark = "combin" + UUID.randomUUID();
				if (suit_map != null) {
					String suit_ids = "";// 主套装购物车中其他套装购物车id，（包含自己的id）
					List<Map> goods_list = (List<Map>) suit_map
							.get("goods_list");
					for (Map good_map : goods_list) {
						Goods suit_goods = this.goodsService
								.getObjById(CommUtil.null2Long(good_map
										.get("id")));
						GoodsCart cart = new GoodsCart();
						cart.setAddTime(new Date());
						cart.setGoods(suit_goods);
						String temp_gsp_parts = null;
						if (suit_gsp != null && !suit_gsp.equals("")) {
							String temp1[] = suit_gsp.split("gsp");
							for (String parts_gsp_ids : temp1) {
								if (parts_gsp_ids.indexOf("_"
										+ CommUtil.null2String(good_map
												.get("id")) + "_") >= 0) {
									temp_gsp_parts = parts_gsp_ids
											.replace(
													"_"
															+ CommUtil
																	.null2String(good_map
																			.get("id"))
															+ "_", "");
									break;
								}
							}
						}
						String[] part_gsp_ids = CommUtil.null2String(
								temp_gsp_parts).split(",");
						setGoodsCartSpec(suit_goods, cart, part_gsp_ids);
						cart.setCombin_mark(combin_mark);
						cart.setCart_type("combin");
						cart.setPrice(BigDecimal.valueOf(CommUtil
								.null2Double(suit_goods
										.getGoods_current_price())));
						cart.setCount(1);
						if (user == null) {
							cart.setCart_session_id(cart_session_id);
						} else {
							cart.setUser(user);
						}
						this.goodsCartService.save(cart);
						suit_ids = suit_ids + ","
								+ CommUtil.null2String(cart.getId());
					}
					suit_ids = suit_ids + ","
							+ CommUtil.null2String(obj.getId());
					obj.setCombin_suit_ids(suit_ids);// 更新套装主购物车id信息(包括自己的id)
					obj.setCombin_main(1);
					obj.setCount(CommUtil.null2Int(count));
					obj.setPrice(BigDecimal.valueOf(CommUtil
							.null2Double(suit_map.get("plan_goods_price"))));
					obj.setCombin_mark(combin_mark);
					obj.setCart_type("combin");
					String temp_gsp_parts = null;
					if (!suit_gsp.equals("")) {
						String temp1[] = suit_gsp.split("gsp");
						for (String parts_gsp_ids : temp1) {
							if (parts_gsp_ids.indexOf("_"
									+ CommUtil.null2String(obj.getGoods()
											.getId()) + "_") >= 0) {
								temp_gsp_parts = parts_gsp_ids.replace("_"
										+ obj.getGoods().getId() + "_", "");
								break;
							}
						}
					}
					String[] part_gsp_ids = CommUtil
							.null2String(temp_gsp_parts).split(",");
					obj.getGsps().removeAll(obj.getGsps());
					setGoodsCartSpec(goods, obj, part_gsp_ids);
					suit_map.put("suit_count", CommUtil.null2Int(count));
					String suit_all_price = CommUtil.formatMoney(CommUtil.mul(
							CommUtil.null2Int(count), CommUtil
									.null2Double(suit_map
											.get("plan_goods_price"))));
					suit_map.put("suit_all_price", suit_all_price);// 套装整体价格=套装单价*数量
					obj.setCombin_suit_info(Json.toJson(suit_map,
							JsonFormat.compact()));
					this.goodsCartService.update(obj);
				} else {
					next = -1;
				}
			}
		} else {// F码商品不允许直接添加到购物车
			next = -1;
			if (goods_inventory == 0) {
				next = -3;
			}
		}
		List<GoodsCart> carts = this.cart_calc(request);
		json_map.put("count", carts.size());
		json_map.put("code", next);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 移动端用户查看购物车
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	// @SecurityMapping(title = "查看购物车", value = "/wap/goods_cart1.htm*",
	// rtype = "buyer", rname = "购物流程0", rcode = "wap_goods_cart", rgroup =
	// "移动端购物")
	@RequestMapping("/wap/goods_cart1.htm")
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/goods_cart1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.cart_calc(request);
		Date date = new Date();
		if (carts.size() > 0) {
			Set<Long> set = new HashSet<Long>();
			List<GoodsCart> native_goods = new ArrayList<GoodsCart>();
			Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
			Map<Long, String> erString = new HashMap<Long, String>();
			for (GoodsCart cart : carts) {
				if (cart.getGoods().getOrder_enough_give_status() == 1
						&& cart.getGoods().getBuyGift_id() != null) {
					BuyGift bg = this.buyGiftService.getObjById(cart.getGoods()
							.getBuyGift_id());
					if (bg.getBeginTime().before(date)) {
						set.add(cart.getGoods().getBuyGift_id());
					} else {
						native_goods.add(cart);
					}
				} else if (cart.getGoods().getEnough_reduce() == 1) {// 满就减
					String er_id = cart.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(er_id));
					if (er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er.getId())) {
							ermap.get(er.getId()).add(cart);
						} else {
							List<GoodsCart> list = new ArrayList<GoodsCart>();
							list.add(cart);
							ermap.put(er.getId(), list);
							Map map = (Map) Json.fromJson(er.getEr_json());
							double k = 0;
							String str = "";
							for (Object key : map.keySet()) {
								if (k == 0) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
								}
								if (Double.parseDouble(key.toString()) < k) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
								}
							}
							erString.put(er.getId(), str);
						}
					} else {
						native_goods.add(cart);
					}
				} else {
					native_goods.add(cart);
				}
			}
			mv.addObject("erString", erString);
			System.out.println(erString.size());
			mv.addObject("er_goods", ermap);// 满就减
			Map<String, List<GoodsCart>> separate_carts = this
					.separateCombin(native_goods);// 传入没有分离组合活动商品的购物车
			mv.addObject("cart", (List<GoodsCart>) separate_carts.get("normal"));// 无活动的商品购物车
			mv.addObject("combin_carts",
					(List<GoodsCart>) separate_carts.get("combin"));// 组合套装商品购物车
			// 将有活动的商品分组(满就送)
			if (set.size() > 0) {
				Map<Long, List<GoodsCart>> map = new HashMap<Long, List<GoodsCart>>();
				for (Long id : set) {
					map.put(id, new ArrayList<GoodsCart>());
				}
				for (GoodsCart cart : carts) {
					if (cart.getGoods().getOrder_enough_give_status() == 1
							&& cart.getGoods().getBuyGift_id() != null) {
						if (map.containsKey(cart.getGoods().getBuyGift_id())) {
							map.get(cart.getGoods().getBuyGift_id()).add(cart);
						}
					}
				}
				mv.addObject("ac_goods", map);
			}
		} else {
			mv = new JModelAndView("wap/goods_cart1_none.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("combinTools", combinTools);
		return mv;
	}

	@RequestMapping("/wap/goods_cart_del.htm")
	public String goods_cart_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (id != null && !id.equals("")) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil
						.null2Long(id));
				gc.getGsps().clear();
				this.goodsCartService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:wap/goods_cart0.htm";
	}

	/**
	 * 购物车列表相关调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/wap/goods_count_adjust.htm")
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count, String gcs) {
		List<GoodsCart> carts = this.cart_calc(request);
		String code = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00;// 单位GoodsCart总价钱
		double total_price = 0.00;// 购物车总价钱
		Goods goods = null;
		if (gc_id != null && !gc_id.equals("")) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil
					.null2Long(gc_id));
			if (gc.getId().toString().equals(gc_id)) {
				goods = gc.getGoods();
				if (goods.getGroup_buy() == 2) {
					GroupGoods gg = new GroupGoods();
					for (GroupGoods gg1 : goods.getGroup_goods_list()) {
						if (gg1.getGg_goods().getId().equals(goods.getId())) {
							gg = gg1;
						}
					}
					if (gg.getGg_count() >= CommUtil.null2Int(count)) {
						gc.setCount(CommUtil.null2Int(count));
						gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(gg
								.getGg_price())));
						this.goodsCartService.update(gc);
						gc_price = CommUtil.mul(gg.getGg_price(), count);
					} else {
						code = "300";
					}
				} else {
					if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
						if (gc.getId().toString().equals(gc_id)) {
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gc.getPrice(), count);
						}
					} else {
						code = "200";
					}
				}
			}
		}
		total_price = this.calCartPrice(carts, gcs);
		Map map = new HashMap();
		map.put("count", count);
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("code", code);
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

	/**
	 * 移动端用户对选定购物车进行结算
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车", value = "/wap/goods_cart1.htm*", rtype = "buyer", rname = "移动端购物流程1", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/goods_cart2.htm")
	public ModelAndView goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String gcs, String giftids,
			String day_value, String addr_id) {
		ModelAndView mv = new JModelAndView("wap/goods_cart2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (gcs == null || gcs.equals("")) {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，请重新进入购物车");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			return mv;
		}
		List<GoodsCart> carts = this.cart_calc(request);
		boolean flag = true;
		if (carts.size() > 0) {
			for (GoodsCart gc : carts) {
				if (!gc.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					flag = false;
					break;
				}
			}
		}
		boolean goods_cod = true;// 默认支持货到付款，这样有一款产品不支持货到付款，这个订单就不支持货到付款
		int tax_invoice = 1;// 默认可以开具增值税发票，只要存在一款产品不支持增值税发票，整体订单不可以开具增值税发票
		if (flag && carts.size() > 0) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Address> addrs = this.addressService
					.query("select obj from Address obj where obj.user.id=:user_id order by obj.default_val desc,obj.addTime desc",
							params, 0, 1);
			mv.addObject("addrs", addrs);
			if (addrs.size() == 1) {
				mv.addObject("addr_id", addrs.get(0).getId());
			}
			String cart_session = CommUtil.randomString(32);
			request.getSession(false)
					.setAttribute("cart_session", cart_session);
			Set<Long> set = new HashSet<Long>();
			Date date = new Date();
			Map erpMap = this.calEnoughReducePrice(carts, gcs);
			mv.addObject("cart_session", cart_session);
			mv.addObject("transportTools", transportTools);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("order_goods_price", erpMap.get("all"));
			mv.addObject("order_er_price", erpMap.get("reduce"));
			List map_list = new ArrayList();
			List<Object> store_list = new ArrayList<Object>();
			for (GoodsCart gc : carts) {
				if (gc.getGoods().getGoods_type() == 1) {
					store_list.add(gc.getGoods().getGoods_store().getId());
				} else {
					store_list.add("self");
				}
			}
			HashSet hs = new HashSet(store_list);
			store_list.removeAll(store_list);
			store_list.addAll(hs);
			String[] gc_ids = CommUtil.null2String(gcs).split(",");
			List<Goods> ac_goodses = new ArrayList<Goods>();
			if (giftids != null && !giftids.equals("")) {
				String[] gift_ids = giftids.split(",");
				for (String gift_id : gift_ids) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(gift_id));
					if (goods != null) {
						ac_goodses.add(goods);
					}
				}
			}
			boolean ret = false;
			if (ac_goodses.size() > 0) {
				ret = true;
			}
			for (Object sl : store_list) {
				if (sl != "self" && !sl.equals("self")) {// 商家商品
					List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
					List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
					Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
					Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
					Map<Long, String> erString = new HashMap<Long, String>();
					for (Goods g : ac_goodses) {
						if (g.getGoods_type() == 1
								&& g.getGoods_store().getId().toString()
										.equals(sl.toString())) {
							gift_map.put(g, new ArrayList<GoodsCart>());
						}
					}
					for (GoodsCart gc : carts) {
						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")
									&& CommUtil.null2Long(gc_id).equals(
											gc.getId())) {
								if (gc.getGoods().getGoods_store() != null) {
									if (gc.getGoods().getGoods_store().getId()
											.equals(sl)) {
										if (ret
												&& gift_map.size() > 0
												&& gc.getGoods()
														.getOrder_enough_give_status() == 1
												&& gc.getGoods()
														.getBuyGift_id() != null) {
											BuyGift bg = this.buyGiftService
													.getObjById(gc.getGoods()
															.getBuyGift_id());
											if (bg.getBeginTime().before(date)) {
												for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
														.entrySet()) {
													if (entry
															.getKey()
															.getBuyGift_id()
															.equals(gc
																	.getGoods()
																	.getBuyGift_id())) {
														entry.getValue()
																.add(gc);
													} else {
														gc_list.add(gc);
													}
												}
											} else {
												gc_list.add(gc);
											}
										} else if (gc.getGoods()
												.getEnough_reduce() == 1) {

											String er_id = gc
													.getGoods()
													.getOrder_enough_reduce_id();
											EnoughReduce er = this.enoughReduceService
													.getObjById(CommUtil
															.null2Long(er_id));
											if (er.getErbegin_time().before(
													date)) {
												if (ermap.containsKey(er
														.getId())) {
													ermap.get(er.getId()).add(
															gc);
												} else {
													List<GoodsCart> list = new ArrayList<GoodsCart>();
													list.add(gc);
													ermap.put(er.getId(), list);
													Map map = (Map) Json
															.fromJson(er
																	.getEr_json());
													double k = 0;
													String str = "";
													for (Object key : map
															.keySet()) {
														if (k == 0) {
															k = Double
																	.parseDouble(key
																			.toString());
															str = "活动商品购满"
																	+ k
																	+ "元，即可享受满减";
														}
														if (Double
																.parseDouble(key
																		.toString()) < k) {
															k = Double
																	.parseDouble(key
																			.toString());
															str = "活动商品购满"
																	+ k
																	+ "元，即可享受满减";
														}
													}

													erString.put(er.getId(),
															str);
												}
											} else {
												gc_list.add(gc);
											}

										} else {
											gc_list.add(gc);
										}
										amount_gc_list.add(gc);
									}
								}
							}
						}
					}
					if ((gc_list != null && gc_list.size() > 0)
							|| (gift_map != null && gift_map.size() > 0)
							|| (ermap != null && ermap.size() > 0)) {
						Map map = new HashMap();
						Map ergcMap = this.calEnoughReducePrice(amount_gc_list,
								gcs);// 满减相关信息
						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}
						if (ermap.size() > 0) {
							map.put("er_goods", ermap);
							map.put("erString", ergcMap.get("erString"));
						}
						map.put("store_id", sl);
						// System.out.println("-----------store:"+this.storeService.getObjById(CommUtil.null2Long(sl)));
						map.put("store", this.storeService.getObjById(CommUtil
								.null2Long(sl)));
						map.put("store_goods_price",
								this.calCartPrice(amount_gc_list, gcs));
						map.put("store_enough_reduce", ergcMap.get("reduce"));
						map.put("gc_list", gc_list);
						map_list.add(map);
					}
					for (GoodsCart gc : gc_list) {
						if (gc.getGoods().getGoods_cod() == -1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
							goods_cod = false;
						}
						if (gc.getGoods().getTax_invoice() == 0) {// 只要存在一件不支持开具增值税发票的商品，整个订单就不允许开具增值税发票
							tax_invoice = 0;
						}
					}

				} else {// 自营商品
					List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
					List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
					Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
					Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
					Map<Long, String> erString = new HashMap<Long, String>();
					for (Goods g : ac_goodses) {
						if (g.getGoods_type() == 0) {
							gift_map.put(g, new ArrayList<GoodsCart>());
						}
					}
					for (GoodsCart gc : carts) {
						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")
									&& CommUtil.null2Long(gc_id).equals(
											gc.getId())) {
								if (gc.getGoods().getGoods_store() == null) {
									if (ret
											&& gift_map.size() > 0
											&& gc.getGoods()
													.getOrder_enough_give_status() == 1
											&& gc.getGoods().getBuyGift_id() != null) {
										BuyGift bg = this.buyGiftService
												.getObjById(gc.getGoods()
														.getBuyGift_id());
										if (bg.getBeginTime().before(date)) {
											for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
													.entrySet()) {
												if (entry
														.getKey()
														.getBuyGift_id()
														.equals(gc
																.getGoods()
																.getBuyGift_id())) {
													entry.getValue().add(gc);
												} else {
													gc_list.add(gc);
												}
											}
										} else {
											gc_list.add(gc);
										}
									} else if (gc.getGoods().getEnough_reduce() == 1) {

										String er_id = gc.getGoods()
												.getOrder_enough_reduce_id();
										EnoughReduce er = this.enoughReduceService
												.getObjById(CommUtil
														.null2Long(er_id));
										if (er.getErbegin_time().before(date)) {
											if (ermap.containsKey(er.getId())) {
												ermap.get(er.getId()).add(gc);
											} else {
												List<GoodsCart> list = new ArrayList<GoodsCart>();
												list.add(gc);
												ermap.put(er.getId(), list);
												Map map = (Map) Json
														.fromJson(er
																.getEr_json());
												double k = 0;
												String str = "";
												for (Object key : map.keySet()) {
													if (k == 0) {
														k = Double
																.parseDouble(key
																		.toString());
														str = "活动商品购满" + k
																+ "元，即可享受满减";
													}
													if (Double.parseDouble(key
															.toString()) < k) {
														k = Double
																.parseDouble(key
																		.toString());
														str = "活动商品购满" + k
																+ "元，即可享受满减";
													}
												}

												erString.put(er.getId(), str);
											}
										} else {
											gc_list.add(gc);
										}

									} else {
										gc_list.add(gc);
									}
									amount_gc_list.add(gc);
								}
							}
						}
					}
					if ((gc_list != null && gc_list.size() > 0)
							|| (gift_map != null && gift_map.size() > 0)
							|| (ermap != null && ermap.size() > 0)) {
						Map map = new HashMap();
						Map ergcMap = this.calEnoughReducePrice(amount_gc_list,
								gcs);// 满减相关信息
						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}
						if (ermap.size() > 0) {
							map.put("er_goods", ermap);
							map.put("erString", ergcMap.get("erString"));
						}
						map.put("store_id", sl);
						// System.out.println("========store:"+this.storeService.getObjById(CommUtil.null2Long(sl)));
						map.put("store_goods_price",
								this.calCartPrice(amount_gc_list, gcs));
						map.put("store_enough_reduce", ergcMap.get("reduce"));
						map.put("gc_list", gc_list);
						map_list.add(map);
					}
					for (GoodsCart gc : gc_list) {
						if (gc.getGoods().getGoods_cod() == -1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
							goods_cod = false;
						}
						if (gc.getGoods().getTax_invoice() == 0) {// 只要存在一件不支持开具增值税发票的商品，整个订单就不允许开具增值税发票
							tax_invoice = 0;
						}
					}
				}
			}
			// 生成7天时间区间
			List days = new ArrayList();
			List day_list = new ArrayList();
			for (int i = 0; i < 7; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, i);
				days.add(CommUtil.formatTime("MM-dd", cal.getTime())
						+ this.generic_day(cal.get(Calendar.DAY_OF_WEEK)));
				day_list.add(CommUtil.formatTime("MM-dd", cal.getTime())
						+ this.generic_day(cal.get(Calendar.DAY_OF_WEEK)));
			}
			// 比较当日时间段
			Calendar cal = Calendar.getInstance();
			mv.addObject(
					"before_time1",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime(
									"yyyy-MM-dd 15:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			mv.addObject(
					"before_time2",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime(
									"yyyy-MM-dd 19:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			mv.addObject(
					"before_time3",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime(
									"yyyy-MM-dd 22:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			mv.addObject("days", days);
			mv.addObject("day_list", day_list);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("cartTools", cartTools);
			mv.addObject("transportTools", transportTools);
			mv.addObject("userTools", this.userTools);
			mv.addObject("map_list", map_list);
			mv.addObject("gcs", gcs);
			mv.addObject("goods_cod", goods_cod);
			mv.addObject("tax_invoice", tax_invoice);
			if (addr_id != null && !addr_id.equals("")) {
				mv.addObject("addr_id", addr_id);
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}

		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "订单地址管理", value = "/wap/choose_address.htm*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/choose_address.htm")
	public ModelAndView choose_address(HttpServletRequest request,
			HttpServletResponse response, String gcs, String addr_id,
			String type) {
		ModelAndView mv = new JModelAndView("wap/goods_cart2_address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addrs = this.addressService
				.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
						params, -1, -1);
		mv.addObject("addrs", addrs);
		mv.addObject("gcs", gcs);
		mv.addObject("areaViewTools", areaViewTools);
		String cart_session = (String) request.getSession(false).getAttribute(
				"cart_session");
		mv.addObject("cart_session", cart_session);
		mv.addObject("addr_id", addr_id);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "添加购物车成功页", value = "/wap/goods_cart0.htm*", rtype = "buyer", rname = "移动端购物车添加成功", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/goods_cart0.htm")
	public ModelAndView goods_cart0(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/goods_cart0.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @param gcs
	 * @param giftids
	 * @return
	 */
	@SecurityMapping(title = "确认购物车第3步", value = "/wap/goods_cart3.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/goods_cart3.htm")
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String gcs, String delivery_time,
			String delivery_type, String delivery_id, String delivery_address,
			String payType, String gifts) throws Exception {
		ModelAndView mv = new JModelAndView("wap/goods_cart3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String cart_session1 = (String) request.getSession(false).getAttribute(
				"cart_session");
		OrderForm main_order = null;
		User buyer = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2String(cart_session1).equals(cart_session)
				&& !CommUtil.null2String(store_id).equals("")) {
			List<GoodsCart> order_carts = new ArrayList<GoodsCart>();
			Address addr = this.addressService.getObjById(CommUtil
					.null2Long(addr_id));
			Date date = new Date();
			String[] gc_ids = gcs.split(",");
			String[] gift_ids = gifts.split(",");
			List<Goods> gift_goods = new ArrayList<Goods>();
			for (String gid : gift_ids) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(gid));
				if (goods != null) {
					BuyGift bg = this.buyGiftService.getObjById(CommUtil
							.null2Long(goods.getBuyGift_id()));
					if (bg != null && bg.getBeginTime().before(date)) {
						gift_goods.add(goods);
					}
				}
			}
			for (String gc_id : gc_ids) {
				if (!gc_id.equals("")) {
					GoodsCart car = this.goodsCartService.getObjById(CommUtil
							.null2Long(gc_id));
					order_carts.add(car);
				}
			}
			for (String gc_id : gc_ids) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil
						.null2Long(gc_id));
				if (gc != null && gc.getGoods().getGoods_cod() == -1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
					if (!payType.equals("online")) {// 订单不支持货到付款，用户从页面前端恶意篡改支付方式为货到付款
						mv = new JModelAndView("wap/error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "您恶意篡改支付方式，订单已经失效");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index.htm");
						return mv;
					}
				}
			}
			// 验证购物车中商品是否有效
			boolean verify_goods = this.cartTools
					.verify_goods_available(order_carts);
			if (!verify_goods) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "部分商品已失效，已自动删除失效商品");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
			if (order_carts.size() > 0 && addr != null) {
				// 验证购物车中是否存在库存为0的商品
				boolean inventory_very = true;
				for (GoodsCart gc : order_carts) {
					if (gc.getCount() == 0) {
						inventory_very = false;
					}
					int goods_inventory = CommUtil.null2Int(this
							.generic_default_info(gc.getGoods(),
									gc.getCart_gsp()).get("count"));// 计算商品库存信息
					if (goods_inventory == 0 || goods_inventory < gc.getCount()) {
						inventory_very = false;
					}
				}
				if (inventory_very) {
					if (payType.equals("payafter")) {// 使用货到付款
						mv = new JModelAndView("wap/payafter_pay.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						String pay_session = CommUtil.randomString(32);
						request.getSession(false).setAttribute("pay_session",
								pay_session);
						mv.addObject("paymentTools", this.paymentTools);
						mv.addObject("pay_session", pay_session);
					}
					double all_of_price = 0;
					request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
					String store_ids[] = store_id.split(",");
					List<Map> child_order_maps = new ArrayList<Map>();
					int whether_gift_in = 0;// 判断是否有满就送 子订单中包含赠品
											// 则主订单whether_gift变为1
					String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss",
							new Date());
					for (int i = 0; i < store_ids.length; i++) {// 根据店铺id，保存多个子订单
						String sid = store_ids[i];
						Store store = null;
						List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
						List<Map> map_list = new ArrayList<Map>();
						if (sid != "self" && !sid.equals("self")) {
							store = this.storeService.getObjById(CommUtil
									.null2Long(sid));
						}
						for (GoodsCart gc : order_carts) {
							if (gc.getGoods().getGoods_type() == 1) {// 商家商品
								boolean add = false;
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& gc.getId().equals(
													CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
										add = true;
										break;
									}
								}
								if (add) {
									if (gc.getGoods().getGoods_store().getId()
											.equals(CommUtil.null2Long(sid))) {
										String goods_type = "";
										if ("combin" == gc.getCart_type()
												|| "combin".equals(gc
														.getCart_type())) {
											if (gc.getCombin_main() == 1) {
												goods_type = "combin";
											}
										}
										if ("group" == gc.getCart_type()
												|| "group".equals(gc
														.getCart_type())) {
											goods_type = "group";
										}
										final String genId = SecurityUserHolder
												.getCurrentUser().getId()
												+ UUID.randomUUID().toString()
												+ ".html";
										final String goodsId = gc.getGoods()
												.getId().toString();
										String uploadFilePath = this.configService
												.getSysConfig()
												.getUploadFilePath();
										final String saveFilePathName = request
												.getSession()
												.getServletContext()
												.getRealPath("/")
												+ uploadFilePath
												+ File.separator
												+ "snapshoot"
												+ File.separator + genId;
										File file = new File(request
												.getSession()
												.getServletContext()
												.getRealPath("/")
												+ uploadFilePath
												+ File.separator + "snapshoot");
										if (!file.exists()) {
											file.mkdir();
										}
										final String url = CommUtil
												.getURL(request);
										Thread t = new Thread(new Runnable() {
											public void run() {
												HttpClient client = new HttpClient();
												HttpMethod method = new GetMethod(
														url + "/goods_"
																+ goodsId
																+ ".htm");
												try {
													client.executeMethod(method);
												} catch (HttpException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												String tempString = "";
												try {
													tempString = method
															.getResponseBodyAsString();
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												method.releaseConnection();
												BufferedWriter writer = null;
												try {
													writer = new BufferedWriter(
															new FileWriter(
																	saveFilePathName));
												} catch (IOException e1) {
													e1.printStackTrace();
												}
												try {
													writer.append(tempString);
													writer.flush();// 需要及时清掉流的缓冲区，万一文件过大就有可能无法写入了
													writer.close();
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										});
										t.start();
										Map json_map = new HashMap();
										json_map.put("goods_id", gc.getGoods()
												.getId());
										json_map.put("goods_name", gc
												.getGoods().getGoods_name());
										json_map.put("goods_choice_type", gc
												.getGoods()
												.getGoods_choice_type());
										json_map.put("goods_type", goods_type);
										json_map.put("goods_count",
												gc.getCount());
										json_map.put("goods_price",
												gc.getPrice());// 商品单价
										json_map.put(
												"goods_all_price",
												CommUtil.mul(gc.getPrice(),
														gc.getCount()));// 商品总价
										json_map.put("goods_commission_price",
												this.getGoodscartCommission(gc));// 设置该商品总佣金
										json_map.put("goods_commission_rate",
												gc.getGoods().getGc()
														.getCommission_rate());// 设置该商品的佣金比例
										json_map.put(
												"goods_payoff_price",
												CommUtil.subtract(
														CommUtil.mul(
																gc.getPrice(),
																gc.getCount()),
														this.getGoodscartCommission(gc)));// 该商品结账价格=该商品总价格-商品总佣金
										json_map.put("goods_gsp_val",
												gc.getSpec_info());
										json_map.put("goods_gsp_ids", gc
												.getCart_gsp() == null ? ""
												: gc.getCart_gsp());
										json_map.put("goods_snapshoot",
												CommUtil.getURL(request) + "/"
														+ uploadFilePath
														+ "/snapshoot/" + genId);
										if (gc.getGoods().getGoods_main_photo() != null) {
											json_map.put(
													"goods_mainphoto_path",
													gc.getGoods()
															.getGoods_main_photo()
															.getPath()
															+ "/"
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getName()
															+ "_small."
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getExt());
										} else {
											json_map.put(
													"goods_mainphoto_path",
													this.configService
															.getSysConfig()
															.getGoodsImage()
															.getPath()
															+ "/"
															+ this.configService
																	.getSysConfig()
																	.getGoodsImage()
																	.getName());
										}
										String goods_domainPath = CommUtil
												.getURL(request)
												+ "/goods_"
												+ gc.getGoods().getId()
												+ ".htm";
										String store_domainPath = CommUtil
												.getURL(request)
												+ "/store_"
												+ gc.getGoods()
														.getGoods_store()
														.getId() + ".htm";
										if (this.configService.getSysConfig()
												.isSecond_domain_open()
												&& gc.getGoods()
														.getGoods_store()
														.getStore_second_domain() != ""
												&& gc.getGoods()
														.getGoods_type() == 1) {
											String store_second_domain = "http://"
													+ gc.getGoods()
															.getGoods_store()
															.getStore_second_domain()
													+ "."
													+ CommUtil
															.generic_domain(request);
											goods_domainPath = store_second_domain
													+ "/goods_"
													+ gc.getGoods().getId()
													+ ".htm";
											store_domainPath = store_second_domain;
										}
										json_map.put("goods_domainPath",
												goods_domainPath);// 商品二级域名路径
										json_map.put("store_domainPath",
												store_domainPath);// 店铺二级域名路径
										// 设置商品组合套装信息
										if (goods_type.equals("combin")) {
											json_map.put("combin_suit_info",
													gc.getCombin_suit_info());
										}
										map_list.add(json_map);
										gc_list.add(gc);
									}
								}
							} else {// 自营商品
								boolean add = false;
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& gc.getId().equals(
													CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
										add = true;
										break;
									}
								}
								if (add) {
									if (sid == "self" || sid.equals("self")) {
										String goods_type = "";
										if ("combin" == gc.getCart_type()
												|| "combin".equals(gc
														.getCart_type())) {
											if (gc.getCombin_main() == 1) {
												goods_type = "combin";
											}

										}
										if ("group" == gc.getCart_type()
												|| "group".equals(gc
														.getCart_type())) {
											goods_type = "group";
										}
										final String genId = SecurityUserHolder
												.getCurrentUser().getId()
												+ UUID.randomUUID().toString()
												+ ".html";
										final String goodsId = gc.getGoods()
												.getId().toString();
										String uploadFilePath = this.configService
												.getSysConfig()
												.getUploadFilePath();
										final String saveFilePathName = request
												.getSession()
												.getServletContext()
												.getRealPath("/")
												+ uploadFilePath
												+ File.separator
												+ "snapshoot"
												+ File.separator + genId;
										File file = new File(request
												.getSession()
												.getServletContext()
												.getRealPath("/")
												+ uploadFilePath
												+ File.separator + "snapshoot");
										if (!file.exists()) {
											file.mkdir();
										}
										final String url = CommUtil
												.getURL(request);
										Thread t = new Thread(new Runnable() {
											public void run() {
												HttpClient client = new HttpClient();
												HttpMethod method = new GetMethod(
														url + "/goods_"
																+ goodsId
																+ ".htm");
												try {
													client.executeMethod(method);
												} catch (HttpException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												String tempString = "";
												try {
													tempString = method
															.getResponseBodyAsString();
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												method.releaseConnection();
												BufferedWriter writer = null;
												try {
													writer = new BufferedWriter(
															new FileWriter(
																	saveFilePathName));
												} catch (IOException e1) {
													e1.printStackTrace();
												}
												try {
													writer.append(tempString);
													writer.flush();// 需要及时清掉流的缓冲区，万一文件过大就有可能无法写入了
													writer.close();
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										});
										t.start();
										Map json_map = new HashMap();
										json_map.put("goods_id", gc.getGoods()
												.getId());
										json_map.put("goods_name", gc
												.getGoods().getGoods_name());
										json_map.put("goods_choice_type", gc
												.getGoods()
												.getGoods_choice_type());
										json_map.put("goods_type", goods_type);
										json_map.put("goods_count",
												gc.getCount());
										json_map.put("goods_price",
												gc.getPrice());// 商品单价
										json_map.put(
												"goods_all_price",
												CommUtil.mul(gc.getPrice(),
														gc.getCount()));// 商品总价
										json_map.put("goods_gsp_val",
												gc.getSpec_info());
										json_map.put("goods_gsp_ids", gc
												.getCart_gsp() == null ? ""
												: gc.getCart_gsp());
										json_map.put("goods_snapshoot",
												CommUtil.getURL(request) + "/"
														+ uploadFilePath
														+ "/snapshoot/" + genId);
										if (gc.getGoods().getGoods_main_photo() != null) {
											json_map.put(
													"goods_mainphoto_path",
													gc.getGoods()
															.getGoods_main_photo()
															.getPath()
															+ "/"
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getName()
															+ "_small."
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getExt());
										} else {
											json_map.put(
													"goods_mainphoto_path",
													this.configService
															.getSysConfig()
															.getGoodsImage()
															.getPath()
															+ "/"
															+ this.configService
																	.getSysConfig()
																	.getGoodsImage()
																	.getName());
										}
										json_map.put("goods_domainPath",
												CommUtil.getURL(request)
														+ "/goods_"
														+ gc.getGoods().getId()
														+ ".htm");// 商品二级域名路径
										// 设置商品组合套装信息
										if (goods_type.equals("combin")) {
											json_map.put("combin_suit_info",
													gc.getCombin_suit_info());
										}
										map_list.add(json_map);
										gc_list.add(gc);
									}
								}
							}
						}
						// 赠品信息
						List<Map> gift_map = new ArrayList<Map>();
						for (int j = 0; gift_goods.size() > 0
								&& j < gift_goods.size(); j++) {
							if (gift_goods.get(j).getGoods_type() == 1) {
								if (gift_goods.get(j).getGoods_store() != null
										&& gift_goods.get(j).getGoods_store()
												.getId().toString().equals(sid)) {
									Map map = new HashMap();
									map.put("goods_id", gift_goods.get(j)
											.getId());
									map.put("goods_name", gift_goods.get(j)
											.getGoods_name());
									map.put("goods_main_photo",
											gift_goods.get(j)
													.getGoods_main_photo()
													.getPath()
													+ "/"
													+ gift_goods
															.get(j)
															.getGoods_main_photo()
															.getName()
													+ "_small."
													+ gift_goods
															.get(j)
															.getGoods_main_photo()
															.getExt());
									map.put("goods_price", gift_goods.get(j)
											.getGoods_current_price());
									String goods_domainPath = CommUtil
											.getURL(request)
											+ "/goods_"
											+ gift_goods.get(j).getId()
											+ ".htm";
									if (this.configService.getSysConfig()
											.isSecond_domain_open()
											&& gift_goods.get(j)
													.getGoods_store()
													.getStore_second_domain() != ""
											&& gift_goods.get(j)
													.getGoods_type() == 1) {
										String store_second_domain = "http://"
												+ gift_goods
														.get(j)
														.getGoods_store()
														.getStore_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request);
										goods_domainPath = store_second_domain
												+ "/goods_"
												+ gift_goods.get(j).getId()
												+ ".htm";
									}
									map.put("goods_domainPath",
											goods_domainPath);// 商品二级域名路径
									map.put("buyGify_id", gift_goods.get(j)
											.getBuyGift_id());
									gift_map.add(map);
								}
							} else {
								if (sid.equals("self") || sid == "self") {
									Map map = new HashMap();
									map.put("goods_id", gift_goods.get(j)
											.getId());
									map.put("goods_name", gift_goods.get(j)
											.getGoods_name());
									map.put("goods_main_photo",
											gift_goods.get(j)
													.getGoods_main_photo()
													.getPath()
													+ "/"
													+ gift_goods
															.get(j)
															.getGoods_main_photo()
															.getName()
													+ "_small."
													+ gift_goods
															.get(j)
															.getGoods_main_photo()
															.getExt());
									map.put("goods_price", gift_goods.get(j)
											.getGoods_current_price());
									String goods_domainPath = CommUtil
											.getURL(request)
											+ "/goods_"
											+ gift_goods.get(j).getId()
											+ ".htm";
									if (this.configService.getSysConfig()
											.isSecond_domain_open()
											&& gift_goods.get(j)
													.getGoods_store()
													.getStore_second_domain() != ""
											&& gift_goods.get(j)
													.getGoods_type() == 1) {
										String store_second_domain = "http://"
												+ gift_goods
														.get(j)
														.getGoods_store()
														.getStore_second_domain()
												+ "."
												+ CommUtil
														.generic_domain(request);
										goods_domainPath = store_second_domain
												+ "/goods_"
												+ gift_goods.get(j).getId()
												+ ".htm";
									}
									map.put("goods_domainPath",
											goods_domainPath);// 商品二级域名路径
									map.put("buyGify_id", gift_goods.get(j)
											.getBuyGift_id());
									gift_map.add(map);
								}
							}
						}
						double goods_amount = this.calCartPrice(gc_list, gcs);// 订单中商品价格
						List<SysMap> sms = this.transportTools
								.query_cart_trans(gc_list, CommUtil
										.null2String(addr.getArea().getId()));
						String transport = request.getParameter("transport_"
								+ sid);
						if (CommUtil.null2String(transport).indexOf("平邮") < 0
								&& CommUtil.null2String(transport)
										.indexOf("快递") < 0
								&& CommUtil.null2String(transport).indexOf(
										"EMS") < 0) {
							transport = "快递";
						}
						double ship_price = 0.00;
						for (SysMap sm : sms) {
							if (CommUtil.null2String(sm.getKey()).indexOf(
									transport) >= 0) {
								ship_price = CommUtil
										.null2Double(sm.getValue());// 订单物流运费
							}
						}
						double totalPrice = CommUtil.add(goods_amount,
								ship_price);// 订单总价
						all_of_price = all_of_price + totalPrice;// 总订单价格
						double commission_amount = this
								.getOrderCommission(gc_list);// 订单总体佣金
						Map ermap = this.calEnoughReducePrice(gc_list, gcs);
						String er_json = (String) ermap.get("er_json");
						double all_goods = Double.parseDouble(ermap.get("all")
								.toString());
						double reduce = Double.parseDouble(ermap.get("reduce")
								.toString());
						OrderForm of = new OrderForm();
						of.setAddTime(new Date());

						String order_store_id = "0";
						if (sid != "self" && !sid.equals("self")) {
							order_store_id = CommUtil
									.null2String(store.getId());
						}
						of.setOrder_id(SecurityUserHolder.getCurrentUser()
								.getId() + order_suffix + order_store_id);
						// 设置收货地址信息
						of.setReceiver_Name(addr.getTrueName());
						of.setReceiver_area(addr.getArea().getParent()
								.getParent().getAreaName()
								+ addr.getArea().getParent().getAreaName()
								+ addr.getArea().getAreaName());
						of.setReceiver_area_info(addr.getArea_info());
						of.setReceiver_mobile(addr.getMobile());
						of.setReceiver_telephone(addr.getTelephone());
						of.setReceiver_zip(addr.getZip());
						of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
						of.setEnough_reduce_info(er_json);
						of.setTransport(transport);
						of.setOrder_status(10);
						of.setUser_id(buyer.getId().toString());
						of.setUser_name(buyer.getUserName());
						of.setGoods_info(Json.toJson(map_list,
								JsonFormat.compact()));// 设置商品信息json数据
						of.setMsg(request.getParameter("msg_" + sid));
						of.setInvoiceType(CommUtil.null2Int(request
								.getParameter("invoiceType")));
						of.setInvoice(request.getParameter("invoice"));
						of.setShip_price(BigDecimal.valueOf(ship_price));
						of.setGoods_amount(BigDecimal.valueOf(all_goods));
						of.setTotalPrice(BigDecimal.valueOf(totalPrice));
						String coupon_id = request.getParameter("coupon_id_"
								+ sid);
						if (!("").equals(coupon_id)) {
							CouponInfo ci = this.couponInfoService
									.getObjById(CommUtil.null2Long(coupon_id));
							if (ci != null) {
								if (SecurityUserHolder.getCurrentUser().getId()
										.equals(ci.getUser().getId())) {
									ci.setStatus(1);
									this.couponInfoService.update(ci);
									Map coupon_map = new HashMap();
									coupon_map.put("couponinfo_id", ci.getId());
									coupon_map.put("couponinfo_sn",
											ci.getCoupon_sn());
									coupon_map.put("coupon_amount", ci
											.getCoupon().getCoupon_amount());
									double rate = CommUtil.div(ci.getCoupon()
											.getCoupon_amount(), goods_amount);
									coupon_map.put("coupon_goods_rate", rate);
									of.setCoupon_info(Json.toJson(coupon_map,
											JsonFormat.compact()));
									of.setTotalPrice(BigDecimal
											.valueOf(CommUtil.subtract(of
													.getTotalPrice(), ci
													.getCoupon()
													.getCoupon_amount())));
								}
							}
						}
						if (sid.equals("self") || sid == "self") {
							of.setOrder_form(1);// 平台自营商品订单
						} else {
							of.setCommission_amount(BigDecimal
									.valueOf(commission_amount));// 该订单总体佣金费用
							of.setOrder_form(0);// 商家商品订单
							of.setStore_id(store.getId().toString());
							of.setStore_name(store.getStore_name());
						}
						of.setOrder_type("weixin");// 设置为wap和微信端订单
						of.setDelivery_time(delivery_time);
						if (gift_map.size() > 0) {
							of.setGift_infos(Json.toJson(gift_map,
									JsonFormat.compact()));
							of.setWhether_gift(1);
							whether_gift_in++;
						}
						of.setDelivery_type(0);
						if (CommUtil.null2Int(delivery_type) == 1
								&& delivery_id != null
								&& !delivery_id.equals("")) {// 自提点信息，使用json管理
							of.setDelivery_type(1);
							DeliveryAddress deliveryAddr = this.deliveryaddrService
									.getObjById(CommUtil.null2Long(delivery_id));
							String service_time = "全天";
							if (deliveryAddr.getDa_service_type() == 1) {
								service_time = deliveryAddr.getDa_begin_time()
										+ "点至" + deliveryAddr.getDa_end_time()
										+ "点";
							}
							Map params = new HashMap();
							params.put("id", deliveryAddr.getId());
							params.put("da_name", deliveryAddr.getDa_name());
							params.put("da_content",
									deliveryAddr.getDa_content());
							params.put("da_contact_user",
									deliveryAddr.getDa_contact_user());
							params.put("da_tel", deliveryAddr.getDa_tel());
							params.put("da_address", deliveryAddr.getDa_area()
									.getParent().getParent().getAreaName()
									+ deliveryAddr.getDa_area().getParent()
											.getAreaName()
									+ deliveryAddr.getDa_area().getAreaName()
									+ deliveryAddr.getDa_address());
							params.put("da_service_day",
									this.DeliveryAddressTools
											.query_service_day(deliveryAddr
													.getDa_service_day()));
							params.put("da_service_time", service_time);
							of.setDelivery_address_id(deliveryAddr.getId());
							of.setDelivery_info(Json.toJson(params,
									JsonFormat.compact()));
						}
						if (i == store_ids.length - 1) {
							of.setOrder_main(1);// 同时购买多个商家商品，最后一个订单为主订单，其他的作为子订单，以json信息保存，用在买家中心统一显示大订单，统一付款
							if (whether_gift_in > 0) {
								of.setWhether_gift(1);
							}
							if (child_order_maps.size() > 0) {
								of.setChild_order_detail(Json.toJson(
										child_order_maps, JsonFormat.compact()));
							}
						}
						boolean flag = this.orderFormService.save(of);
						if (i == store_ids.length - 1) {
							main_order = of;
							mv.addObject("order", of);// 将主订单信息封装到前台视图中
						}
						if (flag) {
							// 如果是多个店铺的订单同时提交，则记录子订单信息到主订单中，用在买家中心统一显示及统一付款
							if (store_ids.length > 1) {
								Map order_map = new HashMap();
								order_map.put("order_id", of.getId());
								order_map.put("order_goods_info",
										of.getGoods_info());
								child_order_maps.add(order_map);
							}
							for (GoodsCart gc : gc_list) {// 删除已经提交订单的购物车信息
								if (gc.getCart_type() != null
										&& gc.getCart_type().equals("combin")
										&& gc.getCombin_main() == 1) {// 购物车提交订单时如果为组合套装购物车，只提交组合套装主购物车，删除主购物车同时删除该套装中其他购物车
									Map combin_map = new HashMap();
									combin_map.put("combin_mark",
											gc.getCombin_mark());
									combin_map.put("combin_main", 1);
									List<GoodsCart> suits = this.goodsCartService
											.query("select obj from GoodsCart obj where obj.combin_mark=:combin_mark and obj.combin_main!=:combin_main",
													combin_map, -1, -1);
									for (GoodsCart suit : suits) {
										this.goodsCartService.delete(suit
												.getId());
									}
								}
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& CommUtil.null2Long(gc_id)
													.equals(gc.getId())) {
										this.goodsCartService
												.delete(gc.getId());
									}
								}
							}
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							ofl.setOf(of);
							ofl.setLog_info("提交订单");
							ofl.setLog_user(SecurityUserHolder.getCurrentUser());
							this.orderFormLogService.save(ofl);

						}
					}
					// 在循环外，给买家只发送一次短信邮件
					if (main_order.getOrder_form() == 0) {
						this.msgTools.sendEmailCharge(CommUtil.getURL(request),
								"email_tobuyer_order_submit_ok_notify",
								buyer.getEmail(), null,
								CommUtil.null2String(main_order.getId()),
								main_order.getStore_id());
						this.msgTools.sendSmsCharge(CommUtil.getURL(request),
								"sms_tobuyer_order_submit_ok_notify",
								buyer.getMobile(), null,
								CommUtil.null2String(main_order.getId()),
								main_order.getStore_id());
					} else {
						this.msgTools.sendEmailFree(CommUtil.getURL(request),
								"email_tobuyer_order_submit_ok_notify",
								buyer.getEmail(), null,
								CommUtil.null2String(main_order.getId()));
						this.msgTools.sendSmsFree(CommUtil.getURL(request),
								"sms_tobuyer_order_submit_ok_notify",
								buyer.getMobile(), null,
								CommUtil.null2String(main_order.getId()));
					}
					mv.addObject("all_of_price",
							CommUtil.formatMoney(all_of_price));
					mv.addObject("paymentTools", paymentTools);
					List<Payment> payments = new ArrayList<Payment>();
					Map params = new HashMap();
					params.put("mark", "wx_pay");
					payments = this.paymentService.query(
							"select obj from Payment obj where obj.mark=:mark",
							params, -1, -1);
					Payment payment = null;
					if (payments.size() > 0) {
						payment = payments.get(0);
						mv.addObject("appid", payment.getWx_appid());
					}

				} else {// 验证库存不成功，返回购物车，并给出提示！
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品库存为0，请删除后再提交订单");
					mv.addObject("url", CommUtil.getURL(request)
							+ "wap/goods_cart1.htm");
				}
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单信息错误");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经失效");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}

		return mv;
	}

	@SecurityMapping(title = "订单加载自提点", value = "/wap/cart_delivery.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/cart_delivery.htm")
	public ModelAndView cart_delivery(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String currentPage,
			String deliver_area_id) {
		ModelAndView mv = new JModelAndView("wap/cart_delivery.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map paras = new HashMap();
		StringBuffer query = new StringBuffer(
				"select obj from DeliveryAddress obj where 1=1 ");
		if (deliver_area_id != null && !deliver_area_id.equals("")) {
			Area deliver_area = this.areaService.getObjById(CommUtil
					.null2Long(deliver_area_id));
			Set<Long> ids = this.genericIds(deliver_area);
			paras.put("ids", ids);
			query.append("and obj.da_area.id in(:ids) ");
			mv.addObject("deliver_area_id", deliver_area_id);
		} else {
			if (addr_id != null && !addr_id.equals("")) {
				Address addr = this.addressService.getObjById(CommUtil
						.null2Long(addr_id));
				paras.put("da_area_id", addr.getArea().getParent().getId());
				query.append("and obj.da_area.parent.id=:da_area_id ");
				mv.addObject("area", addr.getArea().getParent());
			}
		}
		paras.put("da_status", 10);
		query.append("and obj.da_status=:da_status order by addTime desc");
		List<DeliveryAddress> objs = this.deliveryaddrService.query(
				query.toString(), paras, -1, -1);
		mv.addObject("objs", objs);
		return mv;
	}

	private String generic_day(int day) {
		String[] list = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四",
				"星期五", "星期六" };
		return list[day - 1];
	}

	private Map calEnoughReducePrice(List<GoodsCart> carts, String gcs) {
		Map<Long, String> erString = new HashMap<Long, String>();
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		Map erid_goodsids = new HashMap();
		Date date = new Date();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				all_price = CommUtil.add(all_price,
						CommUtil.mul(gc.getCount(), gc.getPrice()));
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(er_id));
					if (er.getErstatus() == 10
							&& er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er_id)) {
							double last_price = (double) ermap.get(er_id);
							ermap.put(
									er_id,
									CommUtil.add(
											last_price,
											CommUtil.mul(gc.getCount(),
													gc.getPrice())));
							((List) erid_goodsids.get(er_id)).add(gc.getGoods()
									.getId());
						} else {
							ermap.put(er_id,
									CommUtil.mul(gc.getCount(), gc.getPrice()));
							List list = new ArrayList();
							list.add(gc.getGoods().getId());
							erid_goodsids.put(er_id, list);
						}
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						all_price = CommUtil.add(all_price,
								CommUtil.mul(gc.getCount(), gc.getPrice()));
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods()
									.getOrder_enough_reduce_id();
							EnoughReduce er = this.enoughReduceService
									.getObjById(CommUtil.null2Long(er_id));
							if (er.getErstatus() == 10
									&& er.getErbegin_time().before(date)) {
								if (ermap.containsKey(er_id)) {
									double last_price = (double) ermap
											.get(er_id);
									ermap.put(er_id, CommUtil.add(
											last_price,
											CommUtil.mul(gc.getCount(),
													gc.getPrice())));
									((List) erid_goodsids.get(er_id)).add(gc
											.getGoods().getId());
								} else {
									ermap.put(
											er_id,
											CommUtil.mul(gc.getCount(),
													gc.getPrice()));
									List list = new ArrayList();
									list.add(gc.getGoods().getId());
									erid_goodsids.put(er_id, list);
								}
							}
						}
					}
				}
			}
		}
		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil
					.null2Long(er_id));
			String erjson = er.getEr_json();
			double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
			Map fromJson = (Map) Json.fromJson(erjson);
			double reduce = 0;
			String erstr = "";
			for (Object enough : fromJson.keySet()) {
				if (er_money >= CommUtil.null2Double(enough)) {
					reduce = CommUtil.null2Double(fromJson.get(enough));
					erstr = "活动商品已购满" + enough + "元,已减" + reduce + "元";
					erid_goodsids.put("enouhg_" + er_id, enough);
				}
			}
			erString.put(er.getId(), erstr);
			erid_goodsids.put("all_" + er_id, er_money);
			erid_goodsids.put("reduce_" + er_id, reduce);

			all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
		}
		Map prices = new HashMap();
		prices.put("er_json", Json.toJson(erid_goodsids, JsonFormat.compact()));
		prices.put("erString", erString);

		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("all", CommUtil.null2Double(bd2));// 商品总价

		double er = Math.round(all_enough_reduce * 100) / 100.0;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("reduce", CommUtil.null2Double(erbd2));// 满减价格

		double af = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("after", CommUtil.null2Double(afbd2));// 减后价格

		return prices;
	}

	private double getGoodscartCommission(GoodsCart gc) {
		double commission_price = CommUtil.mul(gc.getGoods().getGc()
				.getCommission_rate(),
				CommUtil.mul(gc.getPrice(), gc.getCount()));
		return commission_price;
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getOrderCommission(List<GoodsCart> gcs) {
		double commission_price = 0.00;
		for (GoodsCart gc : gcs) {
			commission_price = commission_price
					+ this.getGoodscartCommission(gc);
		}
		return commission_price;
	}

	@SecurityMapping(title = "订单支付详情", value = "/wap/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/order_pay_view.htm")
	public ModelAndView order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/order_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		if (of != null && of.getUser_id().equals(user.getId().toString())) {
			if (of.getOrder_status() == 10) {
				boolean exist = this.orderFormTools.order_goods_exist(id);
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", "wx_pay");
				payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						params, -1, -1);
				Payment payment = null;
				if (payments.size() > 0) {
					payment = payments.get(0);
					mv.addObject("appid", payment.getWx_appid());
				}
				if (!exist) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品已被删除，请重新下单");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/buyer/order_list.htm");
					return mv;
				}
				// 验证订单中商品库存是否充足
				boolean inventory_very = this.orderFormTools
						.order_goods_InventoryVery(id, of);
				if (inventory_very) {
					mv.addObject("of", of);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("orderFormTools", this.orderFormTools);
					mv.addObject("url", CommUtil.getURL(request));
				} else {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品库存不足，请重新下单");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/buyer/order_list.htm");
				}
			} else if (of.getOrder_status() < 10) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经取消");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已付款");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/order_list");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已失效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "移动端订单支付详情", value = "/wap/order_pay.htm*", rtype = "buyer", rname = "移动端订单支付详情", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay.htm")
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if ("wx_pay".equals(payType)) {
			String type = "goods";
			if (order.getOrder_cat() == 2) {
				type = "group";// 订单属性为生活类团购
			}
			try {
				response.sendRedirect("/weixin/pay/wx_pay.htm?id=" + order_id
						+ "&showwxpaytitle=1&type=" + type);
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
		if (order != null && order.getOrder_status() == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			} else {
				boolean exist = this.orderFormTools.order_goods_exist(order_id);
				if (!exist) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品已被删除，请重新下单");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/buyer/order_list.htm");
					return mv;
				}
				boolean inventory_very = this.orderFormTools
						.order_goods_InventoryVery(order_id, order);
				if (inventory_very) {
					// 给订单添加支付方式 ,
					List<Payment> payments = new ArrayList<Payment>();
					Map params = new HashMap();
					params.put("mark", payType);
					payments = this.paymentService.query(
							"select obj from Payment obj where obj.mark=:mark",
							params, -1, -1);
					order.setPayment(payments.get(0));
					order.setPayType("online");
					this.orderFormService.update(order);
					List<Map> cmaps = this.orderFormTools.queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : cmaps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
						if (child_order.getOrder_status() == 10) {
							child_order.setPayment(payments.get(0));
							child_order.setPayType("online");
							this.orderFormService.update(child_order);
						}
					}
					if (payType.equals("balance")) {// 使用预存款支付
						mv = new JModelAndView("wap/balance_pay.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						double order_total_price = CommUtil.null2Double(order
								.getTotalPrice());
						if (!CommUtil
								.null2String(order.getChild_order_detail())
								.equals("")) {
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(map
												.get("order_id")));
								order_total_price = order_total_price
										+ CommUtil.null2Double(child_order
												.getTotalPrice());
							}
						}
						mv.addObject("order_total_price", order_total_price);
					} else {// 使用在线支付
						mv = new JModelAndView("wap/line_pay.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("payType", payType);
						mv.addObject("url", CommUtil.getURL(request));
						mv.addObject("payTools", payTools);
						String type = "goods";
						if (order.getOrder_cat() == 2) {
							type = "group";// 订单属性为生活类团购
						}
						mv.addObject("type", type);
						mv.addObject("payment_id", order.getPayment().getId());
					}
					mv.addObject("order", order);
					mv.addObject("order_id", order.getId());
				} else {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品库存不足，请重新下单");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/index.htm");
				}
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，付款失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "移动端订单预付款支付", value = "/wap/order_pay_balance.htm*", rtype = "buyer", rname = "移动端预存款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay_balance.htm")
	public ModelAndView order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg)
			throws Exception {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null && order.getOrder_status() < 20) {// 订单不为空且订单状态为未付款才可以正常使用预存款付款
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/order_list.htm");
				return mv;
			}
			// 验证订单中商品库存是否充足
			boolean inventory_very = this.orderFormTools
					.order_goods_InventoryVery(order_id, order);
			if (!inventory_very) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
				return mv;
			}
			Map params = new HashMap();
			params.put("mark", "balance");
			List<Payment> payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			double order_total_price = CommUtil.null2Double(order
					.getTotalPrice());
			if (!CommUtil.null2String(order.getChild_order_detail()).equals("")
					&& order.getOrder_cat() != 2) {
				order_total_price = this.orderFormTools
						.query_order_price(CommUtil.null2String(order.getId()));
			}
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
				order.setPay_msg(pay_msg);
				order.setOrder_status(20);
				if (payments.size() > 0) {
					order.setPayment(payments.get(0));
					order.setPayTime(new Date());
				}
				User buyer = this.userService.getObjById(CommUtil
						.null2Long(order.getUser_id()));
				boolean ret = this.orderFormService.update(order);
				if (order.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(order.getStore_id()));
					this.msgTools.sendEmailCharge(CommUtil.getURL(request),
							"email_toseller_balance_pay_ok_notify", store
									.getUser().getEmail(), null, CommUtil
									.null2String(order.getId()), order
									.getStore_id());
					this.msgTools.sendEmailCharge(CommUtil.getURL(request),
							"email_tobuyer_balance_pay_ok_notify",
							user.getEmail(), null,
							CommUtil.null2String(order.getId()),
							order.getStore_id());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request),
							"sms_toseller_balance_pay_ok_notify", store
									.getUser().getMobile(), null, CommUtil
									.null2String(order.getId()), order
									.getStore_id());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request),
							"sms_tobuyer_balance_pay_ok_notify",
							buyer.getMobile(), null,
							CommUtil.null2String(order.getId()),
							order.getStore_id());
				} else {
					this.msgTools.sendEmailFree(CommUtil.getURL(request),
							"email_tobuyer_balance_pay_ok_notify",
							user.getEmail(), null,
							CommUtil.null2String(order.getId()));
					this.msgTools.sendSmsFree(CommUtil.getURL(request),
							"sms_tobuyer_balance_pay_ok_notify",
							buyer.getMobile(), null,
							CommUtil.null2String(order.getId()));
				}
				if (ret) {
					// 预存款付款成功后，执行子订单状态改变及发送提醒信息
					if (order.getOrder_main() == 1
							&& !CommUtil.null2String(
									order.getChild_order_detail()).equals("")
							&& order.getOrder_cat() != 2) {
						List<Map> maps = this.orderFormTools
								.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map
											.get("order_id")));
							child_order.setOrder_status(20);
							if (payments.size() > 0) {
								child_order.setPayment(payments.get(0));
								child_order.setPayTime(new Date());
							}
							this.orderFormService.update(child_order);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							if (child_order.getOrder_form() == 0) {
								this.msgTools.sendEmailCharge(CommUtil
										.getURL(request),
										"email_toseller_balance_pay_ok_notify",
										store.getUser().getEmail(), null,
										CommUtil.null2String(child_order
												.getId()), child_order
												.getStore_id());
								this.msgTools.sendSmsCharge(CommUtil
										.getURL(request),
										"sms_toseller_balance_pay_ok_notify",
										store.getUser().getMobile(), null,
										CommUtil.null2String(child_order
												.getId()), child_order
												.getStore_id());
							}
						}
					}
					// 如果是团购订单，则需呀执行团购订单相关流程及发送团购码
					if (order.getOrder_cat() == 2) {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, this.configService.getSysConfig()
								.getAuto_order_return());
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						order.setReturn_shipTime(CommUtil.formatDate(latertime));
						Map map = this.orderFormTools.queryGroupInfo(order
								.getGroup_info());
						int count = CommUtil.null2Int(map.get("goods_count")
								.toString());
						String goods_id = map.get("goods_id").toString();
						GroupLifeGoods goods = this.groupLifeGoodsService
								.getObjById(CommUtil.null2Long(goods_id));
						goods.setGroup_count(goods.getGroup_count()
								- CommUtil.null2Int(count));
						this.groupLifeGoodsService.update(goods);
						int i = 0;
						List<String> code_list = new ArrayList();// 存放团购消费码
						String codes = "";
						while (i < count) {
							GroupInfo info = new GroupInfo();
							info.setAddTime(new Date());
							info.setLifeGoods(goods);
							info.setPayment(payments.get(0));
							info.setUser_id(user.getId());
							info.setUser_name(user.getUserName());
							info.setOrder_id(order.getId());
							info.setGroup_sn(user.getId()
									+ CommUtil.formatTime("yyyyMMddHHmmss" + i,
											new Date()));
							Calendar ca2 = Calendar.getInstance();
							ca2.add(ca2.DATE, this.configService.getSysConfig()
									.getGrouplife_order_return());
							SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String latertime2 = bartDateFormat2.format(ca2
									.getTime());
							info.setRefund_Time(CommUtil.formatDate(latertime2));
							this.groupInfoService.save(info);
							codes = codes + info.getGroup_sn() + " ";
							code_list.add(info.getGroup_sn());
							i++;
						}
						if (order.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(order.getStore_id()));
							PayoffLog plog = new PayoffLog();
							plog.setPl_sn("pl"
									+ CommUtil.formatTime("yyyyMMddHHmmss",
											new Date())
									+ store.getUser().getId());
							plog.setPl_info("团购码生成成功");
							plog.setAddTime(new Date());
							plog.setSeller(store.getUser());
							plog.setO_id(CommUtil.null2String(order.getId()));
							plog.setOrder_id(order.getOrder_id().toString());
							plog.setCommission_amount(BigDecimal
									.valueOf(CommUtil.null2Double("0.00")));// 该订单总佣金费用
							// 将订单中group_info（{}）转换为List<Map>([{}])
							List<Map> Map_list = new ArrayList<Map>();
							Map group_map = this.orderFormTools
									.queryGroupInfo(order.getGroup_info());
							Map_list.add(group_map);
							plog.setGoods_info(Json.toJson(Map_list,
									JsonFormat.compact()));
							plog.setOrder_total_price(order.getTotalPrice());// 该订单总商品金额
							plog.setTotal_amount(BigDecimal.valueOf(CommUtil
									.add(CommUtil.subtract(
											order.getGoods_amount(),
											order.getCommission_amount()),
											order.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
							this.payoffLogService.save(plog);
							store.setStore_sale_amount(BigDecimal
									.valueOf(CommUtil.add(
											order.getTotalPrice(),
											store.getStore_sale_amount())));// 店铺本次结算总销售金额
							// 团购消费码，没有佣金，店铺总佣金不变
							store.setStore_payoff_amount(BigDecimal
									.valueOf(CommUtil.add(
											order.getTotalPrice(),
											store.getStore_payoff_amount())));// 店铺本次结算总佣金
							this.storeService.update(store);
						}
						// 增加系统总销售金额、消费码没有佣金，系统总佣金不变
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
								order.getTotalPrice(), sc.getPayoff_all_sale())));
						this.configService.update(sc);
						// 更新lucene索引
						String goods_lucene_path = System
								.getProperty("iskyshopb2b2c.root")
								+ File.separator
								+ "luence"
								+ File.separator
								+ "grouplifegoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()),
								luceneVoTools.updateLifeGoodsIndex(goods));
						String msg_content = "恭喜您成功购买团购"
								+ map.get("goods_name") + ",团购消费码分别为：" + codes
								+ "您可以到用户中心-我的生活购中查看消费码的使用情况";
						// 发送系统站内信给买家
						Message tobuyer_msg = new Message();
						tobuyer_msg.setAddTime(new Date());
						tobuyer_msg.setStatus(0);
						tobuyer_msg.setType(0);
						tobuyer_msg.setContent(msg_content);
						tobuyer_msg.setFromUser(this.userService
								.getObjByProperty(null, "userName", "admin"));
						tobuyer_msg.setToUser(user);
						this.messageService.save(tobuyer_msg);
						// 付款成功，发送短信团购消费码
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_groupInfo_sms(request, order,
									user.getMobile(),
									"sms_tobuyer_online_ok_send_groupinfo",
									code_list, user.getId().toString(), goods
											.getUser().getId().toString());
						}
					}
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.subtract(user.getAvailableBalance(),
									order_total_price)));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_op_type("消费");
					log.setPd_log_amount(BigDecimal.valueOf(-CommUtil
							.null2Double(order.getTotalPrice())));
					log.setPd_log_info(order.getOrder_id() + "订单购物减少可用预存款");
					log.setPd_type("可用预存款");
					this.predepositLogService.save(log);
					// 执行库存减少,如果是团购商品，团购库存同步减少
					if (order.getOrder_cat() != 2) {
						orderFormTools.updateGoodsInventory(order);
					}
				}
				// 记录支付日志
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("预付款支付");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(order);
				this.orderFormLogService.save(ofl);
				mv.addObject("op_title", "预付款支付成功");
				if (order.getOrder_cat() == 2) {
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/buyer/group_list.htm");
				} else {
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/buyer/order_list.htm");
				}
			} else {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "可用余额不足，支付失败");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/order_list.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，支付失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "手机订单货到付款", value = "/wap/order_pay_payafter.htm*", rtype = "buyer", rname = "移动端货到付款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay_payafter.htm")
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/wap/buyer/order_list.htm");
				return mv;
			}
			// 验证订单中商品库存是否充足
			boolean inventory_very = this.orderFormTools
					.order_goods_InventoryVery(order_id, order);
			if (!inventory_very) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
				return mv;
			}
			order.setPay_msg(pay_msg);
			order.setPay_msg(pay_msg);
			order.setPayTime(new Date());
			order.setPayType("payafter");
			order.setOrder_status(16);
			this.orderFormService.update(order);
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					child_order.setOrder_status(16);
					child_order.setPayType("payafter");
					child_order.setPayTime(new Date());
					child_order.setPay_msg(pay_msg);
					this.orderFormService.update(child_order);
					// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(child_order.getStore_id()));
					if (this.configService.getSysConfig().isSmsEnbale()
							&& child_order.getOrder_form() == 0) {
						this.send_sms(request, child_order, store.getUser()
								.getMobile(),
								"sms_toseller_payafter_pay_ok_notify");
					}
					if (this.configService.getSysConfig().isEmailEnable()
							&& child_order.getOrder_form() == 0) {
						this.send_email(request, child_order, store.getUser()
								.getEmail(),
								"email_toseller_payafter_pay_ok_notify");
					}
				}
			}
			if (order.getOrder_cat() != 2) {
				orderFormTools.updateGoodsInventory(order);// 如果该订单为货到付款订单，下订单同时更新主订单（子订单）库存信息，货到付款可认定为支付完成，等待发货。
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);
			order.getGoods_info();
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/order_list.htm");
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/order_list.htm");
		}
		return mv;
	}

	/**
	 * 获得购物车总体商品价格
	 * 
	 * @param request
	 * @param response
	 */
	private double getcartsPrice(List<GoodsCart> carts) {
		double all_price = 0.0;
		for (GoodsCart gc : carts) {
			all_price = CommUtil.add(all_price,
					CommUtil.mul(gc.getCount(), gc.getPrice()));
		}
		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return CommUtil.null2Double(bd2);
	}

	/**
	 * 根据商品信息，计算该商品默认的规格信息，以各个规格值的第一个为默认值
	 * 
	 * @param goods
	 *            商品
	 * @return 默认规格id组合，如1,2
	 */
	private String generic_default_gsp(Goods goods) {
		String gsp = "";
		if (goods != null) {
			List<GoodsSpecification> specs = this.goodsViewTools
					.generic_spec(CommUtil.null2String(goods.getId()));
			for (GoodsSpecification spec : specs) {
				// System.out.println(spec.getName());
				for (GoodsSpecProperty prop : goods.getGoods_specs()) {
					if (prop.getSpec().getId().equals(spec.getId())) {
						gsp = prop.getId() + "," + gsp;
						break;
					}
				}
			}
		}
		// System.out.println(gsp);
		return gsp;
	}

	/**
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp) {
		double price = 0;
		Map map = new HashMap();
		int count = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if ("spec".equals(goods.getInventory_type())) {
				if (gsp != null && !gsp.equals("")) {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							goods.getGoods_inventory_detail());
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2
				&& SecurityUserHolder.getCurrentUser() != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods
					.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil
					.null2String(SecurityUserHolder.getCurrentUser().getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			price = CommUtil.mul(rebate, price);
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}

	/**
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * 
	 * @param request
	 * @param response
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				if (gc.getCart_type() == null || gc.getCart_type().equals("")) {// 普通商品处理
					all_price = CommUtil.add(all_price,
							CommUtil.mul(gc.getCount(), gc.getPrice()));
				} else if (gc.getCart_type().equals("combin")) {// 组合套装商品处理
					if (gc.getCombin_main() == 1) {
						Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
						all_price = CommUtil.add(all_price,
								map.get("suit_all_price"));
					}
				}
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					if (ermap.containsKey(er_id)) {
						double last_price = (double) ermap.get(er_id);
						ermap.put(
								er_id,
								CommUtil.add(
										last_price,
										CommUtil.mul(gc.getCount(),
												gc.getPrice())));
					} else {
						ermap.put(er_id,
								CommUtil.mul(gc.getCount(), gc.getPrice()));
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						if (gc.getCart_type() != null
								&& gc.getCart_type().equals("combin")
								&& gc.getCombin_main() == 1) {
							Map map = (Map) Json.fromJson(gc
									.getCombin_suit_info());
							all_price = CommUtil.add(all_price,
									map.get("suit_all_price"));
						} else {
							all_price = CommUtil.add(all_price,
									CommUtil.mul(gc.getCount(), gc.getPrice()));
						}
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods()
									.getOrder_enough_reduce_id();
							if (ermap.containsKey(er_id)) {
								double last_price = (double) ermap.get(er_id);
								ermap.put(
										er_id,
										CommUtil.add(
												last_price,
												CommUtil.mul(gc.getCount(),
														gc.getPrice())));
							} else {
								ermap.put(
										er_id,
										CommUtil.mul(gc.getCount(),
												gc.getPrice()));
							}
						}
					}
				}
			}
		}

		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil
					.null2Long(er_id));
			if (er.getErstatus() == 10
					&& er.getErbegin_time().before(new Date())) {// 活动通过审核且正在进行
				String erjson = er.getEr_json();
				double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
				Map fromJson = (Map) Json.fromJson(erjson);
				double reduce = 0;
				for (Object enough : fromJson.keySet()) {
					if (er_money >= CommUtil.null2Double(enough)) {
						reduce = CommUtil.null2Double(fromJson.get(enough));
					}
				}
				all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
			}
		}
		double d2 = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		return CommUtil.null2Double(CommUtil.formatMoney(d2));
	}

	/**
	 * 根据商品规格获取价格
	 * 
	 * @param request
	 * @param response
	 */
	private String generGspgoodsPrice(String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		User user = SecurityUserHolder.getCurrentUser();
		if (goods.getActivity_status() == 2) {
			if (user != null) {
				Map map = this.activityTools.getActivityGoodsInfo(
						CommUtil.null2String(goods.getId()),
						CommUtil.null2String(user.getId()));
				if (goods.getInventory_type().equals("spec")) {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							goods.getGoods_inventory_detail());
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil.null2String(temp.get("id"))
								.split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
					price=CommUtil.mul(price, map.get("rate"))/10;	
				}else{
				price = CommUtil.null2Double(map.get("rate_price"));
			}
			}
		} else {
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		return CommUtil.null2String(price);
	}

	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */
	private List<GoodsCart> cart_calc(HttpServletRequest request) {
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 0) {// 该商品为商家商品

						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		} else {
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		}
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart cookie : carts_cookie) {
				carts_list.add(cookie);
			}
		}
		return carts_list;
	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext()
					.getRealPath("")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					request.getRealPath("/") + "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(order.getStore_id()));
				context.put("seller", store.getUser());
			}
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			String replace_content = writer.toString();
			this.msgTools.sendEmail(email, subject, replace_content);
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.setVariable("buyer", buyer);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(order.getStore_id()));
				context.setVariable("seller", store.getUser());
			}
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			this.msgTools.sendSMS(mobile, content);
		}
	}

	private void send_groupInfo_sms(HttpServletRequest request,
			OrderForm order, String mobile, String mark, List<String> codes,
			String buyer_id, String seller_id) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append(codes.get(i) + ",");
		}
		String code = sb.toString();
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("buyer",
					this.userService.getObjById(CommUtil.null2Long(buyer_id)));
			context.setVariable("seller",
					this.userService.getObjById(CommUtil.null2Long(seller_id)));
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			Map map = Json.fromJson(Map.class, order.getGroup_info());
			context.setVariable("group_info", map.get("goods_name"));
			context.setVariable("code", code);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			this.msgTools.sendSMS(mobile, content);
		}
	}

	// 分离组合销售活动购物车,只显示主体套装商品购物车
	private Map<String, List<GoodsCart>> separateCombin(List<GoodsCart> carts) {
		Map<String, List<GoodsCart>> map = new HashMap<String, List<GoodsCart>>();
		List<GoodsCart> normal_carts = new ArrayList<GoodsCart>();
		List<GoodsCart> combin_carts = new ArrayList<GoodsCart>();
		for (GoodsCart cart : carts) {
			if (cart.getCart_type() != null
					&& cart.getCart_type().equals("combin")) {
				if (cart.getCombin_main() == 1) {
					combin_carts.add(cart);
				}
			} else {
				normal_carts.add(cart);
			}
		}
		map.put("combin", combin_carts);
		map.put("normal", normal_carts);
		return map;
	}

	private Set<Long> genericIds(Area area) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(area.getId());
		for (Area child : area.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@RequestMapping("/wap/f_code_cart.htm")
	@SecurityMapping(title = "wap端F码购物第一步", value = "/wap/f_code_cart.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	public ModelAndView f_code_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String gsp) {
		ModelAndView mv = new JModelAndView("wap/f_code_cart.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getF_sale_type() == 0) {
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该商品不需要F码购买");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			} else {
				if (CommUtil.null2String(gsp).equals("")) {
					gsp = this.generic_default_gsp(goods);
				}
				int goods_inventory = goods_inventory = CommUtil.null2Int(this
						.generic_default_info(goods, gsp).get("count"));// 计算该规格商品的库存量
				if (goods_inventory > 0) {
					String[] gsp_ids = CommUtil.null2String(gsp).split(",");
					String spec_info = "";
					List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
					for (String gsp_id : gsp_ids) {
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService
								.getObjById(CommUtil.null2Long(gsp_id));
						// System.out.println(spec_property.getSpec().getName());
						boolean add = false;
						for (GoodsSpecProperty temp_gsp : goods
								.getGoods_specs()) {// 检查传入的规格信息是否是该商品对应的规格信息,杜绝用户私自修改传递参数
							if (temp_gsp.getId().equals(spec_property.getId())) {
								add = true;
							}
						}
						for (GoodsSpecProperty temp_gsp : specs) {
							if (temp_gsp.getSpec().getId()
									.equals(spec_property.getSpec().getId())) {
								add = false;
							}
						}
						if (add)
							specs.add(spec_property);
					}
					// System.out.println(this.goodsViewTools.generic_spec(goods_id).size()
					// + "   " + specs.size());
					if (this.goodsViewTools.generic_spec(goods_id).size() == specs
							.size()) {// 这里判断传入的规格数量和商品本身具有的是否一致,杜绝用户私自修改传递参数
						for (GoodsSpecProperty spec : specs) {
							spec_info = spec.getSpec().getName() + ":"
									+ spec.getValue() + " " + spec_info;
						}
						String price = this.generGspgoodsPrice(gsp, goods_id);
						mv.addObject("spec_info", spec_info);
						mv.addObject("price", price);
						mv.addObject("obj", goods);
						mv.addObject("gsp", gsp);
						mv.addObject("goodsViewTools", this.goodsViewTools);
					} else {
						mv = new JModelAndView("wap/error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "规格参数错误");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/wap/index.htm");
					}
				} else {
					mv = new JModelAndView("wap/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "商品默认规格无库存，请选择其他规格购买");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/wap/goods.htm?id=" + goods.getId());
				}

			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	/**
	 * 验证F码信息，并返回到页面中
	 * 
	 * @param request
	 * @param response
	 * @param f_code
	 * @param goods_id
	 */
	@SecurityMapping(title = "wap端F码验证", value = "/wap/f_code_validate.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/f_code_validate.htm")
	public void f_code_validate(HttpServletRequest request,
			HttpServletResponse response, String f_code, String goods_id) {
		boolean ret = false;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
		for (Map map : list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code)
					&& CommUtil.null2Int(map.get("status")) == 0) {// 存在该F码且是未使用的则验证成功
				ret = true;
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

	@SecurityMapping(title = "wap端F码完成验证进入订单提交", value = "/wap/f_code_validate.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/add_f_code_goods_cart.htm")
	public void add_f_code_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String f_code,
			String gsp) {
		boolean ret = false;
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		List<Map> f_code_list = Json.fromJson(List.class,
				goods.getGoods_f_code());
		for (Map map : f_code_list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code)
					&& CommUtil.null2Int(map.get("status")) == 0) {// 存在该F码且是未使用的则验证成功
				ret = true;
			}
		}
		if (ret) {
			List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
			List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
			List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
			Map cart_map = new HashMap();
			User user = userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			cart_map.clear();
			cart_map.put("user_id", user.getId());
			cart_map.put("cart_status", 0);
			carts_user = this.goodsCartService
					.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
							cart_map, -1, -1);
			// 将cookie购物车与用户user购物车合并，去重
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
			GoodsCart obj = new GoodsCart();
			boolean add = true;
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						add = false;
					}
				}
			}
			if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(1);
				String price = this.generGspgoodsPrice(gsp, goods_id);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				String spec_info = "";
				for (String gsp_id : gsp_ids) {
					GoodsSpecProperty spec_property = this.goodsSpecPropertyService
							.getObjById(CommUtil.null2Long(gsp_id));
					obj.getGsps().add(spec_property);
					if (spec_property != null) {
						spec_info = spec_property.getSpec().getName() + ":"
								+ spec_property.getValue() + " " + spec_info;
					}
				}
				obj.setUser(user);
				obj.setSpec_info(spec_info);
				ret = this.goodsCartService.save(obj);
				if (ret) {// 确定F码商品已经添加到购物车，作废F码
					for (Map map : f_code_list) {
						if (CommUtil.null2String(map.get("code"))
								.equals(f_code)
								&& CommUtil.null2Int(map.get("status")) == 0) {// 存在该F码且是未使用的则验证成功
							map.put("status", 1);// 设置该F码已经被使用
							break;
						}
					}
					goods.setGoods_f_code(Json.toJson(f_code_list,
							JsonFormat.compact()));
					this.goodsService.update(goods);
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

	/**
	 * 修改默认收货地址
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/wap/set_default_addr.htm")
	public void set_default_addr(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String ret = "";
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> add = this.addressService
				.query("select obj from Address obj where obj.default_val is 1 and obj.user.id=:user_id",
						map, -1, -1);
		if (add.size() == 1) {
			add.get(0).setDefault_val(0);
			this.addressService.update(add.get(0));

		}
		Address addr = this.addressService.getObjById(CommUtil.null2Long(id));
		addr.setDefault_val(1);
		this.addressService.update(addr);
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
