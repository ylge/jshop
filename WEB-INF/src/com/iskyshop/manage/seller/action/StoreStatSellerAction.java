package com.iskyshop.manage.seller.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.runtime.directive.Foreach;
import org.aspectj.lang.annotation.Before;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sun.awt.image.PixelConverter.Bgrx;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;


/**
 * 
* <p>Title: StoreStatSellerAction.java</p>

* <p>Description: 商家中心店铺统计控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2014-12-30

* @version iskyshop_b2b2c_2015
 */
@Controller
public class StoreStatSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;	
	@Autowired
	private IFavoriteService favoriteService;
	
	@SecurityMapping(title = "店铺统计", value = "/seller/stat_store.htm*", rtype = "seller", rname = "店铺统计", rcode = "seller_stat_store", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_store.htm")
	public ModelAndView stat_store(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/stat_store.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date end =CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		cal.add(Calendar.DAY_OF_MONTH, -30);
		Date begin = CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));

		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId());
		params.put("beginDate", begin);
		params.put("endDate",end);
		
		List<GoodsLog> logs = this.goodsLogService
				.query("select obj from GoodsLog obj where obj.store_id =:store_id and obj.addTime>=:beginDate and obj.addTime<:endDate",
						params, -1, -1);
		int goods_collectAll = 0;//期间商品收藏量
		int goods_salenumAll = 0;//商品期间总销量
		int orderFormAllCount = 0;//期间订单成交总数
		Iterator<String> it = null;
		//pie
		Map<String, Integer> orderTypeMap = new HashMap<String, Integer>();
		Map<String, Integer> goods_clickfrom = new HashMap<String, Integer>();
		for (GoodsLog gl : logs) {
			goods_collectAll+=gl.getGoods_collect();
			goods_salenumAll+=gl.getGoods_salenum();
			//订单的来源统计
			String jsonStr = gl.getGoods_order_type();
			if (jsonStr != null && !jsonStr.equals("")) {
				Map<String, Integer> fromMap = (Map<String, Integer>) Json
						.fromJson(jsonStr);
				it = fromMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					String from = "PC网页";
					if (key.equals("weixin")) {
						from = "手机网页";
					}
					if (key.equals("android")) {
						from = "Android客户端";
					}
					if (key.equals("ios")) {
						from = "iOS客户端";
					}
					if (orderTypeMap.containsKey(from)) {
						orderTypeMap.put(
								from,
								orderTypeMap.get(from)
										+ fromMap.get(key));
					} else {
						orderTypeMap.put(from, fromMap.get(key));
					}
				}
			}
			
			jsonStr = gl.getGoods_click_from();
			if (jsonStr != null && !jsonStr.equals("")) {
				Map<String, Integer> clickmap = (Map<String, Integer>) Json
						.fromJson(jsonStr);
				it = clickmap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					if (goods_clickfrom.containsKey(key)) {
						goods_clickfrom.put(
								key,
								goods_clickfrom.get(key)
										+ clickmap.get(key));
					} else {
						goods_clickfrom.put(key, clickmap.get(key));
					}
				}
			}
			
		}
		mv.addObject("goods_collectAll", goods_collectAll);
		mv.addObject("goods_salenumAll", goods_salenumAll);
		
		it = orderTypeMap.keySet().iterator();
		StringBuilder orderTypesb = new StringBuilder();
		while (it.hasNext()) {
			String str = (String) it.next();
			orderTypesb.append("['").append(str).append("',").append(orderTypeMap.get(str)).append("],");
		}
		if(orderTypesb.length()==0){
			orderTypesb = orderTypesb.append("['暂无数据',1]");
		}
		mv.addObject("orderType", orderTypesb);
		
		it = goods_clickfrom.keySet().iterator();
		StringBuilder clickFromsb = new StringBuilder();
		while (it.hasNext()) {
			String string = (String) it.next();
			clickFromsb.append("['").append(string).append("',")
					.append(goods_clickfrom.get(string)).append("],");
		}
		if(clickFromsb.length()==0){
			clickFromsb = clickFromsb.append("['暂无数据',1]");
		}
		mv.addObject("clickFrom", clickFromsb);
		
		//商品点击量排行
		Object goods_clickRanks = (Object)this.goodsLogService.query(
				"select obj.goods_name,sum(goods_click) from GoodsLog obj where obj.store_id =:store_id and obj.addTime>=:beginDate and obj.addTime<:endDate group by obj.goods_id order by sum(goods_click) desc",
						params, 0,10);
		List click_desc_List = new ArrayList();
		for (Object[] obj : (List<Object[]>)goods_clickRanks) {
			Map goods_clickMap = new HashMap();
			goods_clickMap.put("goods_name",obj[0]);
			goods_clickMap.put("click_count",obj[1]);
			click_desc_List.add(goods_clickMap);
		}
		mv.addObject("click_desc_List", click_desc_List);
		List click_asc_List = new ArrayList(click_desc_List);
		Collections.reverse(click_asc_List);
		mv.addObject("click_asc_List", click_asc_List);
		
		//商品收藏量排行
		Object goods_collectRanks = (Object)this.goodsLogService.query(
				"select obj.goods_name,sum(goods_collect) from GoodsLog obj where obj.store_id =:store_id and obj.addTime>=:beginDate and obj.addTime<:endDate group by obj.goods_id order by sum(goods_collect) desc",
				params, 0,10);
		List collect_desc_List = new ArrayList();
		for (Object[] obj : (List<Object[]>)goods_collectRanks) {
			Map goods_collectMap = new HashMap();
			goods_collectMap.put("goods_name",obj[0]);
			goods_collectMap.put("collect_count",obj[1]);
			collect_desc_List.add(goods_collectMap);
		}
		mv.addObject("collect_desc_List", collect_desc_List);
		List collect_asc_List = new ArrayList(collect_desc_List);
		Collections.reverse(collect_asc_List);
		mv.addObject("collect_asc_List", collect_asc_List);
		
		//商品销量排名
		Object goods_salenumRanks = (Object)this.goodsLogService.query(
				"select obj.goods_name,sum(goods_salenum) from GoodsLog obj where obj.store_id =:store_id and obj.addTime>=:beginDate and obj.addTime<:endDate group by obj.goods_id order by sum(goods_salenum) desc",
				params, 0,10);
		List salenum_desc_List = new ArrayList();
		for (Object[] obj : (List<Object[]>)goods_salenumRanks) {
			Map goods_salenumMap = new HashMap();
			goods_salenumMap.put("goods_name",obj[0]);
			goods_salenumMap.put("sale_num",obj[1]);
			salenum_desc_List.add(goods_salenumMap);
		}
		mv.addObject("salenum_desc_List", salenum_desc_List);
		List salenum_asc_List = new ArrayList(salenum_desc_List);
		Collections.reverse(salenum_asc_List);
		mv.addObject("salenum_asc_List", salenum_asc_List);
		
		//近期统计
		params.clear();
		params.put("order_form", 0);
		params.put("store_id", user.getStore().getId().toString());
		params.put("beginDate", begin);
		params.put("endDate",end);
		params.put("order_status",40);
		Object of_totalPriceTemp = (Object)this.orderFormService.query(
				"select sum(obj.totalPrice) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id " +
				"and obj.order_status>=:order_status and obj.finishTime>=:beginDate and obj.finishTime<:endDate ",
				params, 0, 3);
		List<Object> of_totalPrice = (List<Object>)of_totalPriceTemp;
		mv.addObject("of_totalPrice",of_totalPrice.get(0));
		
		params.clear();
		params.put("type",1);
		params.put("store_id",user.getStore().getId());
		params.put("beginDate", begin);
		params.put("endDate",end);
		int storeFavCount = this.favoriteService.query(
				"select obj.id from Favorite obj where obj.type =:type and obj.store_id =:store_id and obj.addTime>=:beginDate and obj.addTime<:endDate ",
				params, -1,-1).size();
		mv.addObject("storeFavCount",storeFavCount);
		//line
		params.clear();
		params.put("order_form", 0);
		params.put("store_id", user.getStore().getId().toString());
		StringBuilder formTimeData = new StringBuilder();//所统计的时间数据
		Object addFormCountTemp = null;
		List<Object> addFormCount = null;
		StringBuilder addFormCountData = new StringBuilder();
		Object payFormCountTemp = null;
		List<Object> payFormCount = null;
		StringBuilder payFormCountData = new StringBuilder();
		Object finishFormCountTemp = null;
		List<Object> finishFormCount = null;
		StringBuilder finishFormCountData = new StringBuilder();
		while (begin.before(end)) {
			formTimeData.append("'").append(CommUtil.formatTime("MM-dd",begin)).append("',");
			params.put("queryTime",CommUtil.formatTime("yyyy-MM-dd",begin));
			
			addFormCountTemp = (Object)this.orderFormService.query(
					"select count(*) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id and DATE_FORMAT(obj.addTime,'%Y-%m-%d') =:queryTime", 
					params, -1, -1);
			addFormCount = (List<Object>)addFormCountTemp;
			addFormCountData.append(addFormCount.get(0)).append(",");
			
			payFormCountTemp = (Object)this.orderFormService.query(
					"select count(*) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id and DATE_FORMAT(obj.payTime,'%Y-%m-%d') =:queryTime", 
					params, -1, -1);
			payFormCount = (List<Object>)payFormCountTemp;
			payFormCountData.append(payFormCount.get(0)).append(",");
			
			finishFormCountTemp = (Object)this.orderFormService.query(
					"select count(*) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id and DATE_FORMAT(obj.finishTime,'%Y-%m-%d') =:queryTime", 
					params, -1, -1);
			finishFormCount = (List<Object>)finishFormCountTemp;
			finishFormCountData.append(finishFormCount.get(0)).append(",");
			orderFormAllCount =+ orderFormAllCount+CommUtil.null2Int(finishFormCount.get(0));
			cal.setTime(begin);
			cal.add(Calendar.DAY_OF_MONTH,1);
			begin = cal.getTime();
		}
		mv.addObject("addFormCountData", addFormCountData);
		mv.addObject("payFormCountData", payFormCountData);
		mv.addObject("finishFormCountData", finishFormCountData);
		mv.addObject("formTimeData", formTimeData);
		mv.addObject("orderFormAllCount", orderFormAllCount);
		return mv;
	}
	
	@SecurityMapping(title = "地域分布", value = "/seller/stat_formArea.htm*", rtype = "seller", rname = "店铺统计", rcode = "seller_stat_store", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_formArea.htm")
	public ModelAndView stat_formArea(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/stat_formArea.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date end =CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		cal.add(Calendar.DAY_OF_MONTH, -30);
		Date begin = CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		
		StringBuilder lineDistrictData = new StringBuilder();
		StringBuilder lineCountData = new StringBuilder();
		StringBuilder mapCityData = new StringBuilder("{");
		
		Map params = new HashMap();
		params.put("order_form", 0);
		params.put("store_id", user.getStore().getId().toString());
		params.put("beginDate", begin);
		params.put("endDate",end);
		params.put("order_status",40);
		//订单地域分布
		Object formAreaDataTemp = this.orderFormService.query(
				"select substring(receiver_area,1,2),count(*) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id " +
				"and obj.order_status>=:order_status and obj.finishTime>=:beginDate and obj.finishTime<:endDate " +
				"group by substring(receiver_area,1,2) order by count(*) desc",
				params, -1, -1);
		List<Object> formAreaDatas = (List<Object>)formAreaDataTemp;
		
		List<Map> addAreaData = new ArrayList<Map>();
		//此处所截取的地名前两个字用以对应前端页面中，SVG地图js文件中的相应地域。
		for (int i = 0; i < formAreaDatas.size(); i++) {
			if(i<6){
				Object[] formAreaData = (Object[]) formAreaDatas.get(i);
				for (int j = 0; j < formAreaData.length; j++) {
					if(j==0){
						if(formAreaData[j].equals("黑龙")){
							lineDistrictData.append("'").append("黑龙江").append("',");					
						}else{
							lineDistrictData.append("'").append(formAreaData[j]).append("',");	
						}
						mapCityData.append("'").append(formAreaData[j]).append("':'").append(this.getDistrictColor(i)).append("',");
					}
					else{
						lineCountData.append(formAreaData[j]).append(",");
					}
				}				
			}
			Map<String,String> map = new HashMap();
			Object[] formAreaData = (Object[]) formAreaDatas.get(i);
			String str = CommUtil.null2String(formAreaData[0]);
			str = str.equals("黑龙")?"黑龙江":str;
			map.put("name", str);
			map.put("count",CommUtil.null2String(formAreaData[1]));
			addAreaData.add(map);
		}
		mv.addObject("addAreaData", addAreaData);
		mv.addObject("lineCityData", lineDistrictData);
		mv.addObject("lineCountData", lineCountData);
		mv.addObject("mapCityData", mapCityData.append("}"));
		return mv;
	}
	
	public String getDistrictColor(int i){
		switch (i) {
			case 0:
			case 1:
				return "#f00";
			case 2:
			case 3:
				return "#9aff04";
			case 4:
			case 5:	
				return "#fbb688";
		}
		return "#BBB";
	}
	
}
