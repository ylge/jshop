package com.iskyshop.manage.admin.tools;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sun.tools.jar.resources.jar;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.view.web.tools.BuyGiftViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MsgTools.java
 * </p>
 * 
 * <p>
 * Description: 订单解析工具，解析订单中json数据
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
 * @date 2014-5-4
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
@Component
public class OrderFormTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService gspService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IBuyGiftService buyGiftService;

	/**
	 * 解析订单商品信息json数据
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {

		List<Map> map_list = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			map_list = Json.fromJson(ArrayList.class, json);
		}
		return map_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品,包括子订单中的商品
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Goods> queryOfGoods(String of_id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(of_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		List<Goods> goods_list = new ArrayList<Goods>();
		for (Map map : map_list) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map
					.get("goods_id")));
			if (goods != null) {
				goods_list.add(goods);
			}
		}
		if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {// 查询子订单中的商品信息
			List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService
						.getObjById(CommUtil.null2Long(map.get("order_id")));
				map_list.clear();
				map_list = this.queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(map1.get("goods_id")));
					goods_list.add(goods);
				}
			}
		}
		return goods_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品的价格总和
	 * 
	 * @param order_id
	 * @return
	 */
	public double queryOfGoodsPrice(String order_id) {
		double price = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			price = price + CommUtil.null2Double(map.get("goods_all_price"));
		}
		return price;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id) {
		int count = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if (count == 0) {// 主订单无数量信息，继续从子订单中查询
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map1 : map_list) {
						if (CommUtil.null2String(map1.get("goods_id")).equals(
								goods_id)) {
							count = CommUtil.null2Int(map1.get("goods_count"));
							break;
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 根据订单id和商品id以及商品规格id查询该商品在该订单中的数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id,
			String goods_gsp_ids) {
		int count = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_gsp_ids")) != null) {
				if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)
						&& CommUtil.null2String(map.get("goods_gsp_ids"))
								.equals(goods_gsp_ids)) {
					count = CommUtil.null2Int(map.get("goods_count"));
					break;
				}
			} else {
				if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
					count = CommUtil.null2Int(map.get("goods_count"));
					break;
				}
			}
		}
		if (count == 0) {// 主订单无数量信息，继续从子订单中查询
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map1 : map_list) {
						if (CommUtil.null2String(map1.get("goods_gsp_ids")) != null) {
							if (CommUtil.null2String(map1.get("goods_id"))
									.equals(goods_id)
									&& CommUtil.null2String(
											map1.get("goods_gsp_ids")).equals(
											goods_gsp_ids)) {
								count = CommUtil.null2Int(map1
										.get("goods_count"));
								break;
							}
						} else {
							if (CommUtil.null2String(map1.get("goods_id"))
									.equals(goods_id)) {
								count = CommUtil.null2Int(map1
										.get("goods_count"));
								break;
							}
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的规格
	 * 
	 * @param order_id
	 * @return
	 */
	public List<GoodsSpecProperty> queryOfGoodsGsps(String order_id,
			String goods_id) {
		List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
		String goods_gsp_ids = "";
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		boolean add = false;
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
				break;
			}
		}
		String gsp_ids[] = goods_gsp_ids.split(",");
		Arrays.sort(gsp_ids);
		for (String id : gsp_ids) {
			if (!id.equals("")) {
				GoodsSpecProperty gsp = this.gspService.getObjById(CommUtil
						.null2Long(id));
				list.add(gsp);
				add = true;
			}
		}
		if (!add) {// 如果主订单中添加失败，则从子订单中添加
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map : map_list) {
						if (CommUtil.null2String(map.get("goods_id")).equals(
								goods_id)) {
							goods_gsp_ids = CommUtil.null2String(map
									.get("goods_gsp_ids"));
							break;
						}
					}
					String child_gsp_ids[] = goods_gsp_ids.split("/");
					for (String id : child_gsp_ids) {
						if (!id.equals("")) {
							GoodsSpecProperty gsp = this.gspService
									.getObjById(CommUtil.null2Long(id));
							list.add(gsp);
							add = true;
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * 解析订单物流信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public String queryExInfo(String json, String key) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return CommUtil.null2String(map.get(key));
	}

	/**
	 * 解析订单优惠券信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryCouponInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 解析生活类团购订单json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryGroupInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 根据订单id查询订单信息
	 * 
	 * @param id
	 * @return
	 */
	public OrderForm query_order(String id) {
		return this.orderFormService.getObjById(CommUtil.null2Long(id));
	}

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			order_status = order.getOrder_status();
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getOrder_status() < 30) {
						order_status = child_order.getOrder_status();
					}
				}
			}
		}
		return order_status;
	}

	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_order_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getTotalPrice());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = all_price
							+ CommUtil.null2Double(child_order.getTotalPrice());
				}

			}
		}
		return all_price;
	}

	public double query_order_goods(String order_id) {
		double all_goods = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_goods = CommUtil.null2Double(order.getGoods_amount());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_goods = all_goods
							+ CommUtil.null2Double(child_order
									.getGoods_amount());
				}
			}
		}
		return all_goods;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_suitinfo(String goods_info) {
		Map map = (Map) Json.fromJson(goods_info);
		return map;
	}

	public List<Map> query_order_giftinfo(String gift_info) {
	List<Map> map =  Json.fromJson(List.class,gift_info);
		return map;
	}
	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_order_suitgoods(Map suit_map) {
		List<Map> map_list = new ArrayList();
		if (suit_map != null && !suit_map.equals("")) {
			map_list = (List<Map>) suit_map.get("goods_list");
		}
		return map_list;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_order_suitgoods(Map suit_map,String goods_id) {
		List<Map> map_list = new ArrayList();
		if (suit_map != null && !suit_map.equals("")) {
			map_list = (List<Map>) suit_map.get("goods_list");
		}
		return map_list;
	}
	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (goods != null && goods.getGoods_type() == 1) {
				store = goods.getGoods_store();
			}
		}
		return store;
	}

	/**
	 * 解析订单中自提点信息
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_delivery(String delivery_info) {
		Map map = (Map) Json.fromJson(delivery_info);
		return map;
	}

	/**
	 * 查询订单中所以商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_goods_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		List<Map> list_map = new ArrayList<Map>();
		int count = 0;
		if (orderForm != null) {
			list_map = this.queryGoodsInfo(orderForm.getGoods_info());
			for (Map map : list_map) {
				count = count + CommUtil.null2Int(map.get("goods_count"));
			}
			if (orderForm.getOrder_main() == 1
					&& !CommUtil.null2String(orderForm.getChild_order_detail())
							.equals("")) {
				list_map = this.queryGoodsInfo(orderForm
						.getChild_order_detail());
				for (Map map : list_map) {
					List<Map> list_map1 = new ArrayList<Map>();
					list_map1 = this.queryGoodsInfo(map.get("order_goods_info")
							.toString());
					for (Map map2 : list_map1) {
						count = count
								+ CommUtil.null2Int(map2.get("goods_count"));
					}
				}
			}
		}
		return count;
	}

	/**
	 * 查询订单中所有团购数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_group_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		Map map = new HashMap();
		int count = 0;
		if (orderForm != null) {
			map = this.queryGroupInfo(orderForm.getGroup_info());
			count = CommUtil.null2Int(map.get("goods_count"));
		}
		return count;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_integral_goodsinfo(String json) {
		List<Map> maps = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			maps = Json.fromJson(List.class, json);
		}
		return maps;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_integral_count(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		if (igo != null) {
			List<Map> objs = Json.fromJson(List.class, igo.getGoods_info());
			int count = objs.size();
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * 查询积分订单中所有商品，返回IntegralGoods集合
	 * 
	 * @param order_id
	 * @return
	 */
	public List<IntegralGoods> query_integral_all_goods(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			IntegralGoods ig = this.integralGoodsService.getObjById(CommUtil
					.null2Long(obj.get("id")));
			if (ig != null) {
				objs.add(ig);
			}
		}
		return objs;
	}

	/**
	 * 查询积分订单中某商品的下单数量
	 * 
	 * @param order_id
	 * @return
	 */
	
	public int query_integral_one_goods_count(IntegralGoodsOrder igo,
			String ig_id) {
		int count = 0;
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			if (obj.get("id").equals(ig_id)) {
				count = CommUtil.null2Int(obj.get("ig_goods_count"));
				break;
			}
		}
		return count;
	}

	/**
	 * 查询订单中某件是否评价
	 * 
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public Evaluate query_order_evaluate(Object order_id, Object goods_id) {
		Map para = new HashMap();
		para.put("order_id", CommUtil.null2Long(order_id));
		para.put("goods_id", CommUtil.null2Long(goods_id));
		List<Evaluate> list = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.of.id=:order_id",
						para, -1, -1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 判断是否可修改评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_edit_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 判断是否可追加评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_add_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_add_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 计算今天到指定时间天数
	 * 
	 * @param date
	 * @return
	 */
	public int how_soon(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			long day = (end - begin) / 86400000;
			return CommUtil.null2Int(day);
		}
		return 999;
	}

	/**
	 * 验证订单中商品库存是否充足，是否可以支付订单，在选择支付方式请求中验证、在选择支付方式后支付中验证,返回true说明验证成功，
	 * 返回false说明验证失败
	 * 
	 * @param id
	 * @return
	 */
	public boolean order_goods_InventoryVery(String id, OrderForm of) {
		boolean inventory_very = true;
		List<Goods> goods_list = this.queryOfGoods(id);
		for (Goods obj : goods_list) {
			int order_goods_count = this.queryOfGoodsCount(id,
					CommUtil.null2String(obj.getId()));
			String order_goods_gsp_ids = "";
			List<Map> goods_maps = this.queryGoodsInfo(of.getGoods_info());
			for (Map obj_map : goods_maps) {
				if (CommUtil.null2String(obj_map.get("goods_id")).equals(
						obj.getId().toString())) {
					order_goods_gsp_ids = CommUtil.null2String(obj_map
							.get("goods_gsp_ids"));
					break;
				}
			}
			// 真实商品库存
			int real_goods_count = CommUtil.null2Int(this.generic_default_info(
					obj, order_goods_gsp_ids).get("count"));// 计算商品库存信息
			if (order_goods_count > real_goods_count) {
				inventory_very = false;
				break;
			}
		}
		return inventory_very;
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
			if (actGoods!=null) {
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
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}

	/**
	 * 验证订单中所有商品是否都存在，全部存在返回true,如果有商品被删除返回false
	 * 
	 * @param id
	 * @return
	 */
	public boolean order_goods_exist(String id) {
		boolean verify = true;
		List<Goods> objs = this.queryOfGoods(id);
		for (Goods obj : objs) {
			if (obj == null || obj.equals("")) {
				verify = false;
			}
		}
		return verify;
	}

	/**
	 * 1、说明：更新订单中所有商品信息，包括主订单中商品及子订单中商品，为支付成功后更新商品销量、库存等信息
	 * 2、更新内容：更新的信息有商品库存、商品索引信息、商品活动状态、商品库存预警信息等等。
	 * 3、调用该方法的场景：在线支付回调中、货到付款确认收货时,预存款支付成功时
	 * 
	 * @param order
	 */
	public void updateGoodsInventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		// 查询订单中所有商品，包括子订单和主订单
		try {
			List<Goods> goods_list = queryOfGoods(CommUtil.null2String(order
					.getId()));
			// 更新订单中信息
			List<Map> maps = queryGoodsInfo(order.getGoods_info());
			for (Map order_map : maps) {
				// 商品参数
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(order_map.get("goods_id")));
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				List<String> gsps_strs = new ArrayList<String>();
				int goods_count = 0;
				if (CommUtil.null2String(order_map.get("goods_gsp_ids")) != null) {
					goods_count = queryOfGoodsCount(
							CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()),
							CommUtil.null2String(order_map.get("goods_gsp_ids")));
					String gsp_ids[] = CommUtil.null2String(
							order_map.get("goods_gsp_ids")).split(",");
					for (String temp_gsp_id : gsp_ids) {
						if (!temp_gsp_id.equals("")) {
							GoodsSpecProperty gsp = this.gspService
									.getObjById(CommUtil.null2Long(temp_gsp_id));
							gsps.add(gsp);
						}
					}
				} else {
					goods_count = queryOfGoodsCount(
							CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
				}
				// 组合套装商品信息
				if (order_map.get("combin_suit_info") != null) {
					Map suit_info = Json.fromJson(Map.class, CommUtil
							.null2String(order_map.get("combin_suit_info")));
					int combin_count = CommUtil.null2Int(suit_info
							.get("suit_count"));
					List<Map> combin_goods = query_order_suitgoods(suit_info);
					for (Map temp_goods : combin_goods) {// 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
						for (Goods temp : goods_list) {
							if (!CommUtil.null2String(temp_goods.get("id"))
									.equals(temp.getId().toString())) {
								Goods com_goods = this.goodsService
										.getObjById(CommUtil
												.null2Long(temp_goods.get("id")));
								com_goods.setGoods_salenum(com_goods
										.getGoods_salenum() + combin_count);
								com_goods.setGoods_inventory(com_goods
										.getGoods_inventory() - combin_count);
								this.goodsService.update(com_goods);
							}
						}
					}
				}
				if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品更新
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId()
								.equals(goods.getGroup().getId())) {
							gg.setGg_count(gg.getGg_count() - goods_count);
							gg.setGg_selled_count(gg.getGg_selled_count()
									+ goods_count);
							if (gg.getGroup().getId()
									.equals(goods.getGroup().getId())
									&& gg.getGg_count() == 0) {
								goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
							}
							this.ggService.update(gg);
							// 更新lucene索引
							// 更新lucene索引
							if (goods.getGroup_buy() == 2) {
								String goods_lucene_path = System
										.getProperty("iskyshopb2b2c.root")
										+ File.separator
										+ "luence"
										+ File.separator + "goods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(CommUtil.null2String(goods
										.getId()), luceneVoTools
										.updateGroupGoodsIndex(goods
												.getGroup_goods_list()
												.get(goods
														.getGroup_goods_list()
														.size() - 1)));
							} else {
								String goods_lucene_path = System
										.getProperty("iskyshopb2b2c.root")
										+ File.separator
										+ "luence"
										+ File.separator + "goods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(
										CommUtil.null2String(goods.getId()),
										luceneVoTools.updateGoodsIndex(goods));
							}
						}
					}
				}
				// 普通商品更新
				String spectype = "";
				for (GoodsSpecProperty gsp : gsps) {
					gsps_strs.add(gsp.getId().toString());
					spectype += gsp.getSpec().getName() + ":" + gsp.getValue()
							+ " ";
				}
				String[] gsp_list = new String[gsps.size()];
				gsps_strs.toArray(gsp_list);
				goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
				saveGoodsLog(order, goods, goods_count, spectype);
				String inventory_type = goods.getInventory_type() == null ? "all"
						: goods.getInventory_type();
				boolean inventory_warn = false;
				if (inventory_type.equals("all")) {
					goods.setGoods_inventory(goods.getGoods_inventory()
							- goods_count);
					if (goods.getGoods_inventory() <= goods
							.getGoods_warn_inventory()) {
						inventory_warn = true;
					}
				} else {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							CommUtil.null2String(goods
									.getGoods_inventory_detail()));
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(temp_ids);
						Arrays.sort(gsp_list);
						if (Arrays.equals(temp_ids, gsp_list)) {
							temp.put("count",
									CommUtil.null2Int(temp.get("count"))
											- goods_count);
							if (CommUtil.null2Int(temp.get("count")) <= CommUtil
									.null2Int(temp.get("supp"))) {
								inventory_warn = true;
							}
						}
					}
					goods.setGoods_inventory_detail(Json.toJson(list,
							JsonFormat.compact()));
				}
				if (inventory_warn) {
					goods.setWarn_inventory_status(-1);// 该商品库存预警状态
				}
				this.goodsService.update(goods);
				// 更新lucene索引
				if (goods.getGroup_buy() == 2) {
					String goods_lucene_path = System
							.getProperty("iskyshopb2b2c.root")
							+ File.separator
							+ "luence" + File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.update(CommUtil.null2String(goods.getId()),
							luceneVoTools
									.updateGroupGoodsIndex(goods
											.getGroup_goods_list().get(
													goods.getGroup_goods_list()
															.size() - 1)));
				} else {
					String goods_lucene_path = System
							.getProperty("iskyshopb2b2c.root")
							+ File.separator
							+ "luence" + File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.update(CommUtil.null2String(goods.getId()),
							luceneVoTools.updateGoodsIndex(goods));
				}
			}
			// 更新子订单信息
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> order_maps = queryGoodsInfo(order
						.getChild_order_detail());
				for (Map temp_order : order_maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(temp_order
									.get("order_id")));
					updateGoodsInventory(child_order);
				}
			}
			// 判断是否有满就送如果有则进行库存操作
			if (order.getWhether_gift() == 1) {
				this.buyGiftViewTools.update_gift_invoke(order);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// 保存商品日志
	private void saveGoodsLog(OrderForm order, Goods goods, int goods_count,
			String spectype) {
		GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods
				.getId());
		todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum()
				+ goods_count);
		Map<String, Integer> logordermap = (Map<String, Integer>) Json
				.fromJson(todayGoodsLog.getGoods_order_type());
		String ordertype = order.getOrder_type();
		if (logordermap.containsKey(ordertype)) {
			logordermap
					.put(ordertype, logordermap.get(ordertype) + goods_count);
		} else {
			logordermap.put(ordertype, goods_count);
		}
		todayGoodsLog.setGoods_order_type(Json.toJson(logordermap,
				JsonFormat.compact()));
		Map<String, Integer> logspecmap = (Map<String, Integer>) Json
				.fromJson(todayGoodsLog.getGoods_sale_info());
		if (logspecmap.containsKey(spectype)) {
			logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
		} else {
			logspecmap.put(spectype, goods_count);
		}
		todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap,
				JsonFormat.compact()));
		this.goodsLogService.update(todayGoodsLog);

	}
}
