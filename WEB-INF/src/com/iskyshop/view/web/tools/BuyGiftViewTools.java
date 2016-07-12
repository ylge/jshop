package com.iskyshop.view.web.tools;

import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: BuyGiftViewTools.java
 * </p>
 * 
 * <p>
 * Description: 满就送信息管理工具
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
 * @date 2014-10-24
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class BuyGiftViewTools {
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;

	public void update_gift_invoke(OrderForm order) {
		if (order != null) {
			if (order.getGift_infos() != null
					&& !order.getGift_infos().equals("")) {
				List<Map> maps = Json.fromJson(List.class,
						order.getGift_infos());
				for (Map map : maps) {
					BuyGift bg = this.buyGiftService.getObjById(CommUtil
							.null2Long(map.get("buyGify_id")));
					if (bg != null) {
						List<Map> gifts = Json.fromJson(List.class,
								bg.getGift_info());
						for (Map gift : gifts) {
							String goods_id = gift.get("goods_id").toString();
							if (goods_id.equals(map.get("goods_id").toString())) {
								if (gift.get("storegoods_count").toString()
										.equals("1")) {
									Goods goods = this.goodsService
											.getObjById(CommUtil
													.null2Long(goods_id));
									goods.setGoods_inventory(goods
											.getGoods_inventory() - 1);
									this.goodsService.update(goods);
									if(goods.getGoods_inventory()==0){
										bg.setGift_status(20);
										List<Map> g_maps = Json.fromJson(List.class, bg.getGift_info());
										maps.addAll(Json.fromJson(List.class, bg.getGift_info()));
										for (Map m : g_maps) {
											Goods g_goods = this.goodsService.getObjById(CommUtil.null2Long(m
													.get("goods_id")));
											if (g_goods != null) {
												g_goods.setOrder_enough_give_status(0);
												g_goods.setOrder_enough_if_give(0);
												g_goods.setBuyGift_id(null);
												this.goodsService.update(g_goods);
											}
										}
									}
								} else {
									if (gift.get("goods_count") != null) {
										gift.put(
												"goods_count",
												CommUtil.null2Int(gift
														.get("goods_count")) - 1);
										if(CommUtil.null2Int(gift.get("goods_count"))==0){
											bg.setGift_status(20);
											List<Map> g_maps = Json.fromJson(List.class, bg.getGift_info());
											maps.addAll(Json.fromJson(List.class, bg.getGift_info()));
											for (Map m : g_maps) {
												Goods g_goods = this.goodsService.getObjById(CommUtil.null2Long(m
														.get("goods_id")));
												if (g_goods != null) {
													g_goods.setOrder_enough_give_status(0);
													g_goods.setOrder_enough_if_give(0);
													g_goods.setBuyGift_id(null);
													this.goodsService.update(g_goods);
												}
											}
										}
									}
								}
							}
						}
						bg.setGift_info(Json.toJson(gifts, JsonFormat.compact()));
						this.buyGiftService.update(bg);
					}
				}
			}
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> cmaps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : cmaps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					this.update_gift_invoke(child_order);
				}
			}
		}
	}
}
