package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IGoodsService;

@Component
public class ActivityViewTools {
	@Autowired
	private IGoodsService goodspService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools IntegralViewTools;

	/**
	 * 活动专题页中，每个商品显示其四个会员等级的价格，
	 * 
	 * @param goods_id
	 * @return
	 */
	public Map getActivityPrices(String goods_id) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		if (obj != null && obj.getActivity_status() == 2) {
			ActivityGoods actGoods = this.actgoodsService.getObjById(obj
					.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				map.put("price1", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate(), obj.getGoods_current_price())));
				map.put("price2", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate1(), obj.getGoods_current_price())));
				map.put("price3", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate2(), obj.getGoods_current_price())));
				map.put("price4", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate3(), obj.getGoods_current_price())));
			}
		}
		return map;
	}

	/**
	 * 商品详情页，显示商品的所有活动信息，包括活动商品价格、活动折扣，当前登录用户的用户等级
	 * 
	 * @param goods_id
	 * @param user_id
	 * @return
	 */
	public Map getActivityGoodsInfo(String goods_id, String user_id) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		Double price = CommUtil.null2Double(obj.getGoods_current_price());
		if (obj != null && obj.getActivity_status() == 2) {
			ActivityGoods actGoods = this.actgoodsService.getObjById(obj
					.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				String rate = "0.00";
				String level_name = "铜牌会员";
				int level = this.IntegralViewTools.query_user_level(user_id);
				if (level == 0) {
					rate = CommUtil.formatMoney(act.getAc_rebate());
				} else if (level == 1) {
					level_name = "银牌会员";
					rate = CommUtil.formatMoney(act.getAc_rebate1());
				} else if (level == 2) {
					level_name = "金牌会员";
					rate = CommUtil.formatMoney(act.getAc_rebate2());
				} else if (level == 3) {
					level_name = "超级会员";
					rate = CommUtil.formatMoney(act.getAc_rebate3());
				}
				String fina_gsp = "";
				if (obj != null) {
					List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
					if ("spec".equals(obj.getInventory_type())) {
						for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
							GoodsSpecification spec = gsp.getSpec();
							if (!specs.contains(spec)) {
								specs.add(spec);
							}
						}
						java.util.Collections.sort(specs,
								new Comparator<GoodsSpecification>() {

									@Override
									public int compare(GoodsSpecification gs1,
											GoodsSpecification gs2) {
										// TODO Auto-generated method stub
										return gs1.getSequence()
												- gs2.getSequence();
									}
								});
					}
					for (GoodsSpecification spec : specs) {
						// System.out.println(spec.getName());
						for (GoodsSpecProperty prop : obj.getGoods_specs()) {
							if (prop.getSpec().getId().equals(spec.getId())) {
								fina_gsp = prop.getId() + "," + fina_gsp;
								break;
							}
						}
					}
				}
				price = CommUtil.null2Double(this.generic_default_info(obj,
						fina_gsp).get("price"));// 计算价格
				map.put("rate",
						CommUtil.formatMoney(CommUtil.null2Double(rate) * 10));
				map.put("level_name", level_name);
				map.put("rate_price", price);
			}
		}
		return map;
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
				if (gsp != null) {
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
			int level = this.IntegralViewTools.query_user_level(CommUtil
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
}
