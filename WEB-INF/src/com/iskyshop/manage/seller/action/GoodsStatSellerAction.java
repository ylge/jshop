package com.iskyshop.manage.seller.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.OrderStat;
import com.iskyshop.foundation.domain.virtual.UserStat;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.StatTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: StatSelfAction.java
 * </p>
 * 
 * <p>
 * Description: 店铺统计统计。只统计店铺统计商品
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class GoodsStatSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private StatTools statTools;

	
	
	
	@SecurityMapping(title = "商品统计", value = "/seller/stat_goods.htm*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_goods.htm")
	public ModelAndView stat_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/stat_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("self", 1);
		return mv;
	}

	@SecurityMapping(title = "有信息的商品", value = "/seller/stat_goods_list.htm*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_goods_list.htm")
	public ModelAndView stat_goods_list(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String currentPage, String goods_name, String class_id,
			String brand_id) {
		ModelAndView mv = null;
		if (endTime != null && beginTime != null && !endTime.equals("")
				&& !beginTime.equals("")) {
			mv = new JModelAndView(
					"user/default/sellercenter/stat_goods_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);

			Date begin = CommUtil.formatDate(beginTime);
			Date end = CommUtil.formatDate(endTime);
			Map para = new HashMap();
			para.put("begin", begin);
			para.put("end", end);
			para.put("store_id", SecurityUserHolder.getCurrentUser().getStore()
					.getId());

			String hql = "select obj from GoodsLog obj where obj.addTime>=:begin and obj.addTime<=:end  and obj.log_form=1 and obj.store_id=:store_id ";

			String hql2 = "select  COUNT(*) from GoodsLog obj where obj.addTime>=:begin and obj.addTime<=:end  and obj.log_form=1 and obj.store_id=:store_id ";

			if (goods_name != null && !goods_name.equals("")) {
				hql += " and obj.goods_name LIKE :goods_name ";
				hql2 += " and obj.goods_name LIKE :goods_name ";
				para.put("goods_name", "%" + goods_name + "%");
			}

			if (class_id != null && !class_id.equals("")) {
				Set<Long> ids = this.genericIds(this.goodsClassService
						.getObjById(CommUtil.null2Long(class_id)));
				para.put("ids", ids);
				hql += " and obj.gc_id in (:ids)";
				hql2 += " and obj.gc_id in (:ids)";
			}

			if (brand_id != null && !brand_id.equals("")) {
				hql += " and obj.goods_brand_id =:goods_brand_id ";
				hql2 += " and obj.goods_brand_id =:goods_brand_id ";
				para.put("goods_brand_id", CommUtil.null2Long(brand_id));
			}

			hql += " group by obj.goods_id";
			hql2 += " group by obj.goods_id";

			List list = this.goodsLogService.query(hql2, para, -1, -1);
			int length = list.size();
			int pagesize = 18;

			List newlist = null;

			int nowpage = CommUtil.null2Int(currentPage);
			int cp = nowpage;
			int pages = (int) Math.ceil((double) length / (double) pagesize);
			if (nowpage == 0) {
				cp = 1;
				newlist = this.goodsLogService.query(hql, para, 0, pagesize);
			} else {
				if (nowpage > pages) {
					cp = pages;
					newlist = this.goodsLogService.query(hql, para, (pages - 1)
							* pagesize, pagesize);
				} else {
					newlist = this.goodsLogService.query(hql, para,
							(nowpage - 1) * pagesize, pagesize);
				}
			}

			mv.addObject("imageTools", imageTools);
			String url = CommUtil.getURL(request)
					+ "/seller/stat_goods_list.htm";
			mv.addObject("goodslog_list", newlist);
			mv.addObject("goodslog_list_gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(url, "", cp, pages));

		}
		return mv;
	}

	@SecurityMapping(title = "商品统计结果", value = "/seller/stat_goods_done.htm*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_goods_done.htm")
	public ModelAndView stat_goods_done(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String ids, String statType) {
		ModelAndView mv = null;
		if (ids != null && !ids.equals("")) {
			String[] arr = ids.split(",");
			Date begin = CommUtil.formatDate(beginTime);
			Date end = CommUtil.formatDate(endTime);
			Map map = CommUtil.cal_time_space(begin, end);
			int day = CommUtil.null2Int(map.get("day"));
			if (day > 90) {// 超过90天按月统计
				statType = "bymonth";
			}
			int increaseType = Calendar.DAY_OF_YEAR;// 统计间隔的增长方式
			String timeFormater = "MM月dd日";
			if (statType.equals("bymonth")) {
				increaseType = Calendar.MONTH;
				timeFormater = "yy年MM月";
			}
			int allclick = 0;// 总点击量
			int allsale = 0;// 总销售量

			if (arr.length == 1) {// 一件商品的统计
				mv = new JModelAndView(
						"user/default/sellercenter/stat_single_goods_result.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 0, request, response);
				if (begin != null && end != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(begin);

					Map goodinfo = new HashMap();// 统计的商品的信息
					List<Map> stat = new ArrayList<Map>();// 统计的表格
					// 线性统计图
					List times = new ArrayList();
					List goods_click = new ArrayList();
					List goods_collect = new ArrayList();
					List goods_salenum = new ArrayList();
					// 饼图
					Map<String, Integer> goods_clickfrom = new HashMap<String, Integer>();
					Map<String, Integer> goods_ordertype = new HashMap<String, Integer>();
					Map<String, Integer> goods_spectype = new HashMap<String, Integer>();

					Map params = new HashMap();
					params.put("goods_id", CommUtil.null2Long(ids));
					List<GoodsLog> log = null;

					while (!cal.getTime().after(end)) {
						Map statmap = new HashMap();
						params.put("beginDate", cal.getTime());
						times.add(CommUtil.formatTime(timeFormater,
								cal.getTime()));
						statmap.put("time", CommUtil.formatTime(timeFormater,
								cal.getTime()));
						if (statType.equals("bymonth")) {
							cal.set(Calendar.DAY_OF_MONTH, 1);
						}
						cal.add(increaseType, 1);

						if (statType.equals("bymonth")) {// 按月
							if (cal.getTime().before(end)) {// 中间月份
								params.put("endDate", cal.getTime());
							} else {// 最后一个月，统计到end那一天
								cal.setTime(end);
								cal.add(Calendar.DAY_OF_YEAR, 1);// end后一天，"<"号不包括
								params.put("endDate", cal.getTime());
							}
						} else {
							params.put("endDate", cal.getTime());
						}
						log = this.goodsLogService
								.query("select obj from GoodsLog obj where obj.goods_id=:goods_id and obj.addTime>=:beginDate and obj.addTime<:endDate",
										params, -1, -1);
						int clicknum = 0;
						int collectnum = 0;
						int salenum = 0;
						double price = 0;
						List<Map> preferential = new ArrayList<Map>();
						for (GoodsLog gl : log) {
							if (!goodinfo.containsKey("info")) {
								goodinfo.put("info", gl);
							}
							clicknum += gl.getGoods_click();
							allclick += gl.getGoods_click();
							collectnum += gl.getGoods_collect();
							salenum += gl.getGoods_salenum();
							allsale += gl.getGoods_salenum();
							price = price == 0 ? CommUtil.null2Double(gl
									.getPrice()) : (price + CommUtil
									.null2Double(gl.getPrice())) / 2;

							if (!gl.getPreferential().equals("")) {

								if (preferential.size() == 0) {
									Map todaytpre = new HashMap();
									todaytpre.put(gl.getPreferential(),
											gl.getPreferential_info());
									preferential.add(todaytpre);
								} else {

									Map temppre = preferential.get(preferential
											.size() - 1);
									if (!(temppre.containsKey(gl
											.getPreferential()) && temppre.get(
											gl.getPreferential()).equals(
											gl.getPreferential_info()))) {
										Map todaytpre = new HashMap();
										todaytpre.put(gl.getPreferential(),
												gl.getPreferential_info());
										preferential.add(todaytpre);
									}
								}
							}

							Iterator<String> it = null;
							String jsonstr = gl.getGoods_click_from();
							if (jsonstr != null && !jsonstr.equals("")) {
								Map<String, Integer> clickmap = (Map<String, Integer>) Json
										.fromJson(jsonstr);
								it = clickmap.keySet().iterator();
								while (it.hasNext()) {
									String key = it.next().toString();
									if (goods_clickfrom.containsKey(key)) {
										goods_clickfrom.put(key,
												goods_clickfrom.get(key)
														+ clickmap.get(key));
									} else {
										goods_clickfrom.put(key,
												clickmap.get(key));
									}
								}
							}

							jsonstr = gl.getGoods_order_type();
							if (jsonstr != null && !jsonstr.equals("")) {
								Map<String, Integer> ordermap = (Map<String, Integer>) Json
										.fromJson(jsonstr);
								it = ordermap.keySet().iterator();
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
									if (goods_ordertype.containsKey(from)) {
										goods_ordertype.put(from,
												goods_ordertype.get(from)
														+ ordermap.get(key));
									} else {
										goods_ordertype.put(from,
												ordermap.get(key));
									}
								}
							}

							jsonstr = gl.getGoods_sale_info();
							if (jsonstr != null && !jsonstr.equals("")) {
								Map<String, Integer> specmap = (Map<String, Integer>) Json
										.fromJson(jsonstr);
								it = specmap.keySet().iterator();
								while (it.hasNext()) {
									String key = it.next().toString();
									if (goods_spectype.containsKey(key)) {
										goods_spectype.put(key,
												goods_spectype.get(key)
														+ specmap.get(key));
									} else {
										goods_spectype.put(key,
												specmap.get(key));
									}
								}
							}
						}
						statmap.put("clicknum", clicknum);
						statmap.put("collectnum", collectnum);
						statmap.put("salenum", salenum);
						if (price > 0) {
							statmap.put("price", CommUtil.formatMoney(price));
						} else {
							statmap.put("price", "无记录");
						}
						statmap.put("preferential", preferential);
						stat.add(statmap);
						goods_click.add(clicknum);
						goods_collect.add(collectnum);
						goods_salenum.add(salenum);
					}

					Iterator<String> it = goods_clickfrom.keySet().iterator();
					StringBuilder clicksb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						clicksb.append("['").append(string).append("',")
								.append(goods_clickfrom.get(string))
								.append("],");
					}

					it = goods_ordertype.keySet().iterator();
					StringBuilder ordersb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						ordersb.append("['").append(string).append("',")
								.append(goods_ordertype.get(string))
								.append("],");
					}

					it = goods_spectype.keySet().iterator();
					StringBuilder specsb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						if (string.equals("")) {
							specsb.append("['").append("默认规格").append("',")
									.append(goods_spectype.get(string))
									.append("],");
						} else {

							specsb.append("['").append(string).append("',")
									.append(goods_spectype.get(string))
									.append("],");
						}
					}

					mv.addObject("allclick", allclick);
					mv.addObject("allsale", allsale);

					mv.addObject("begin", begin);
					mv.addObject("end", end);

					mv.addObject("goodinfo", goodinfo);
					mv.addObject("objs", stat);
					mv.addObject("imageTools", imageTools);

					mv.addObject("stat_title", "商城商品统计图");
					mv.addObject("times",
							Json.toJson(times, JsonFormat.compact()));
					mv.addObject("timeslength",
							CommUtil.null2Int(times.size() / 8));
					mv.addObject("goods_click",
							Json.toJson(goods_click, JsonFormat.compact()));
					mv.addObject("goods_collect",
							Json.toJson(goods_collect, JsonFormat.compact()));
					mv.addObject("goods_salenum",
							Json.toJson(goods_salenum, JsonFormat.compact()));
					mv.addObject("goods_clickfrom", clicksb);
					mv.addObject("goods_ordertype", ordersb);
					mv.addObject("goods_spectype", specsb);
				}
			} else {// 2个及以上商品
				mv = new JModelAndView(
						"user/default/sellercenter/stat_multi_goods_result.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 0, request, response);
				if (begin != null && end != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(begin);

					Map<Long, String> goods = new HashMap<Long, String>();// key:
																			// id
																			// value:
																			// 商品信息
					Map<Date, Map<String, Map>> stat = new TreeMap<Date, Map<String, Map>>();
					// line统计图
					List times = new ArrayList();
					Map<Long, List> goods_click = new HashMap<Long, List>();
					Map<Long, List> goods_collect = new HashMap<Long, List>();
					Map<Long, List> goods_salenum = new HashMap<Long, List>();

					// 饼图
					Map<String, Integer> goods_clickfrom = new HashMap<String, Integer>();
					Map<String, Integer> goods_ordertype = new HashMap<String, Integer>();

					// 排名
					Map<Long, Integer> clickcount = new HashMap<Long, Integer>();
					Map<Long, Integer> salecount = new HashMap<Long, Integer>();

					Map params = new HashMap();
					List<GoodsLog> log = null;

					while (!cal.getTime().after(end)) {

						Date calday = cal.getTime();
						times.add(CommUtil.formatTime(timeFormater, calday));

						params.put("beginDate", calday);

						if (statType.equals("bymonth")) {
							cal.set(Calendar.DAY_OF_MONTH, 1);
						}
						cal.add(increaseType, 1);

						if (statType.equals("bymonth")) {// 按月
							if (cal.getTime().before(end)) {// 中间月份
								params.put("endDate", cal.getTime());
							} else {// 最后一个月，统计到end那一天
								cal.setTime(end);
								cal.add(Calendar.DAY_OF_YEAR, 1);// end后一天，"<"号不包括
								params.put("endDate", cal.getTime());
							}
						} else {
							params.put("endDate", cal.getTime());
						}

						Map idgoodmap = new HashMap();
						for (String id : arr) {
							params.put("goods_id", CommUtil.null2Long(id));
							log = this.goodsLogService
									.query("select obj from GoodsLog obj where obj.goods_id=:goods_id and obj.addTime>=:beginDate and obj.addTime<:endDate",
											params, -1, -1);
							long longid = CommUtil.null2Long(id);
							int clicknum = 0;
							int collectnum = 0;
							int salenum = 0;
							double price = 0;
							List<Map> preferential = new ArrayList<Map>();

							for (GoodsLog gl : log) {

								long tempid = CommUtil.null2Long(id);
								if (!goods.containsKey(id)) {
									goods.put(tempid, gl.getGoods_name());
								}

								if (clickcount.containsKey(tempid)) {
									clickcount.put(
											tempid,
											clickcount.get(tempid)
													+ gl.getGoods_click());
								} else {
									clickcount.put(tempid, gl.getGoods_click());
								}

								if (salecount.containsKey(tempid)) {
									salecount.put(tempid, salecount.get(tempid)
											+ gl.getGoods_salenum());
								} else {
									salecount
											.put(tempid, gl.getGoods_salenum());
								}

								clicknum += gl.getGoods_click();
								allclick += gl.getGoods_click();
								collectnum += gl.getGoods_collect();
								salenum += gl.getGoods_salenum();
								allsale += gl.getGoods_salenum();
								price = price == 0 ? CommUtil.null2Double(gl
										.getPrice()) : (price + CommUtil
										.null2Double(gl.getPrice())) / 2;

								if (!gl.getPreferential().equals("")) {

									if (preferential.size() == 0) {
										Map todaytpre = new HashMap();
										todaytpre.put(gl.getPreferential(),
												gl.getPreferential_info());
										preferential.add(todaytpre);
									} else {

										Map temppre = preferential
												.get(preferential.size() - 1);
										if (!(temppre.containsKey(gl
												.getPreferential()) && temppre
												.get(gl.getPreferential())
												.equals(gl
														.getPreferential_info()))) {
											Map todaytpre = new HashMap();
											todaytpre.put(gl.getPreferential(),
													gl.getPreferential_info());
											preferential.add(todaytpre);
										}
									}
								}

								Iterator<String> it = null;
								String jsonstr = gl.getGoods_click_from();
								if (jsonstr != null && !jsonstr.equals("")) {
									Map<String, Integer> clickmap = (Map<String, Integer>) Json
											.fromJson(jsonstr);
									it = clickmap.keySet().iterator();
									while (it.hasNext()) {
										String key = it.next().toString();
										if (goods_clickfrom.containsKey(key)) {
											goods_clickfrom
													.put(key,
															goods_clickfrom
																	.get(key)
																	+ clickmap
																			.get(key));
										} else {
											goods_clickfrom.put(key,
													clickmap.get(key));
										}
									}
								}

								jsonstr = gl.getGoods_order_type();
								if (jsonstr != null && !jsonstr.equals("")) {
									Map<String, Integer> ordermap = (Map<String, Integer>) Json
											.fromJson(jsonstr);
									it = ordermap.keySet().iterator();
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
										if (goods_ordertype.containsKey(from)) {
											goods_ordertype
													.put(from,
															goods_ordertype
																	.get(from)
																	+ ordermap
																			.get(key));
										} else {
											goods_ordertype.put(from,
													ordermap.get(key));
										}
									}
								}

							}
							Map statmap = new HashMap();
							statmap.put("clicknum", clicknum);
							statmap.put("collectnum", collectnum);
							statmap.put("salenum", salenum);
							if (price > 0) {
								statmap.put("price",
										CommUtil.formatMoney(price));
							} else {
								statmap.put("price", "无记录");
							}
							statmap.put("preferential", preferential);

							idgoodmap.put(id, statmap);

							if (goods_click.containsKey(longid)) {
								goods_click.get(longid).add(clicknum);
							} else {
								List list = new ArrayList();
								list.add(clicknum);
								goods_click.put(longid, list);
							}

							if (goods_collect.containsKey(longid)) {
								goods_collect.get(longid).add(collectnum);
							} else {
								List list = new ArrayList();
								list.add(collectnum);
								goods_collect.put(longid, list);
							}

							if (goods_salenum.containsKey(longid)) {
								goods_salenum.get(longid).add(salenum);
							} else {
								List list = new ArrayList();
								list.add(salenum);
								goods_salenum.put(longid, list);
							}

						}
						stat.put(calday, idgoodmap);
					}

					Iterator<String> it = goods_clickfrom.keySet().iterator();
					StringBuilder clicksb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						clicksb.append("['").append(string).append("',")
								.append(goods_clickfrom.get(string))
								.append("],");
					}

					it = goods_ordertype.keySet().iterator();
					StringBuilder ordersb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						ordersb.append("['").append(string).append("',")
								.append(goods_ordertype.get(string))
								.append("],");
					}

					mv.addObject("allclick", allclick);
					mv.addObject("allsale", allsale);

					mv.addObject("clicksort", sort(clickcount, goods));
					mv.addObject("salesort", sort(salecount, goods));

					mv.addObject("begin", begin);
					mv.addObject("end", end);

					mv.addObject("goods", goods);
					mv.addObject("objs", stat);

					mv.addObject("times",
							Json.toJson(times, JsonFormat.compact()));
					mv.addObject("timeslength",
							CommUtil.null2Int(times.size() / 8));
					mv.addObject("stat_title", "商城商品统计图");
					mv.addObject("goods_click", goods_click);
					mv.addObject("goods_collect", goods_collect);
					mv.addObject("goods_salenum", goods_salenum);
					mv.addObject("goods_clickfrom", clicksb);
					mv.addObject("goods_ordertype", ordersb);

				}
			}
		}
		return mv;

	}

	@SecurityMapping(title = "全商城统计结果", value = "/seller/stat_all.htm*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping("/seller/stat_all.htm")
	public ModelAndView stat_all(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String goods_name, String class_id, String brand_id, String statType) {
		ModelAndView mv = null;
		Date begin = CommUtil.formatDate(beginTime);
		Date end = CommUtil.formatDate(endTime);
		Map map = CommUtil.cal_time_space(begin, end);
		int day = CommUtil.null2Int(map.get("day"));
		if (day > 90) {// 超过90天按月统计
			statType = "bymonth";
		}
		int increaseType = Calendar.DAY_OF_YEAR;// 统计间隔的增长方式
		String timeFormater = "MM月dd日";
		if (statType.equals("bymonth")) {
			increaseType = Calendar.MONTH;
			timeFormater = "yy年MM月";
		}
		mv = new JModelAndView(
				"user/default/sellercenter/stat_all_goods_result.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (begin != null && end != null) {

			// sheet
			List<Map> stat = new ArrayList<Map>();
			// line
			List times = new ArrayList();
			List<Integer> goods_click = new ArrayList<Integer>();
			List<Integer> goods_collect = new ArrayList<Integer>();
			List<Integer> goods_salenum = new ArrayList<Integer>();
			// pie
			Map<String, Integer> goods_clickfrom = new HashMap<String, Integer>();
			Map<String, Integer> goods_ordertype = new HashMap<String, Integer>();

			// 排名
			Map<Long, String> goodsnames = new HashMap<Long, String>();
			Map<Long, Integer> clickcount = new HashMap<Long, Integer>();
			Map<Long, Integer> salecount = new HashMap<Long, Integer>();

			int allclick = 0;
			int allsale = 0;

			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);

			Map params = new HashMap();
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			params.put("store_id", user.getStore().getId());
			List<GoodsLog> log = null;

			while (!cal.getTime().after(end)) {
				Date today = cal.getTime();
				params.put("beginDate", today);
				times.add(CommUtil.formatTime(timeFormater, today));
				Map todaymap = new HashMap();
				todaymap.put("date", CommUtil.formatTime(timeFormater, today));
				if (statType.equals("bymonth")) {
					cal.set(Calendar.DAY_OF_MONTH, 1);
				}
				cal.add(increaseType, 1);

				if (statType.equals("bymonth")) {// 按月
					if (cal.getTime().before(end)) {// 中间月份
						params.put("endDate", cal.getTime());
					} else {// 最后一个月，统计到end那一天
						cal.setTime(end);
						cal.add(Calendar.DAY_OF_YEAR, 1);// end后一天，"<"号不包括
						params.put("endDate", cal.getTime());
					}
				} else {
					params.put("endDate", cal.getTime());
				}

				String hql = "select obj from GoodsLog obj where obj.addTime>=:beginDate and obj.addTime<:endDate  and obj.log_form=1 and obj.store_id=:store_id";

				if (goods_name != null && !goods_name.equals("")) {
					hql += " and obj.goods_name LIKE :goods_name ";
					params.put("goods_name", "%" + goods_name + "%");
				}

				if (class_id != null && !class_id.equals("")) {
					Set<Long> ids = this.genericIds(this.goodsClassService
							.getObjById(CommUtil.null2Long(class_id)));
					params.put("ids", ids);
					hql += " and obj.gc_id in (:ids)";
				}

				if (brand_id != null && !brand_id.equals("")) {
					hql += " and obj.goods_brand_id =:goods_brand_id ";
					params.put("goods_brand_id", CommUtil.null2Long(brand_id));
				}

				log = this.goodsLogService.query(hql, params, -1, -1);
				int click = 0;
				int collect = 0;
				int sale = 0;

				for (GoodsLog gl : log) {
					click += gl.getGoods_click();
					allclick += gl.getGoods_click();
					collect += gl.getGoods_collect();
					sale += gl.getGoods_salenum();
					allsale += gl.getGoods_salenum();

					long id = gl.getGoods_id();
					if (clickcount.containsKey(id)) {
						clickcount.put(id,
								clickcount.get(id) + gl.getGoods_click());
					} else {
						clickcount.put(id, gl.getGoods_click());
					}

					if (salecount.containsKey(id)) {
						salecount.put(id,
								salecount.get(id) + gl.getGoods_salenum());
					} else {
						salecount.put(id, gl.getGoods_salenum());
					}

					if (!goodsnames.containsKey(id)) {
						goodsnames.put(id, gl.getGoods_name());
					}

					Iterator<String> it = null;
					String jsonstr = gl.getGoods_click_from();
					if (jsonstr != null && !jsonstr.equals("")) {
						Map<String, Integer> clickmap = (Map<String, Integer>) Json
								.fromJson(jsonstr);
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

					jsonstr = gl.getGoods_order_type();
					if (jsonstr != null && !jsonstr.equals("")) {
						Map<String, Integer> ordermap = (Map<String, Integer>) Json
								.fromJson(jsonstr);
						it = ordermap.keySet().iterator();
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
							if (goods_ordertype.containsKey(from)) {
								goods_ordertype.put(
										from,
										goods_ordertype.get(from)
												+ ordermap.get(key));
							} else {
								goods_ordertype.put(from, ordermap.get(key));
							}
						}
					}

				}
				goods_click.add(click);
				goods_collect.add(collect);
				goods_salenum.add(sale);

				todaymap.put("click", click);
				todaymap.put("collect", collect);
				todaymap.put("sale", sale);
				stat.add(todaymap);
			}
			Iterator<String> it = goods_clickfrom.keySet().iterator();
			StringBuilder clicksb = new StringBuilder();
			while (it.hasNext()) {
				String string = (String) it.next();
				clicksb.append("['").append(string).append("',")
						.append(goods_clickfrom.get(string)).append("],");
			}

			it = goods_ordertype.keySet().iterator();
			StringBuilder ordersb = new StringBuilder();
			while (it.hasNext()) {
				String string = (String) it.next();
				ordersb.append("['").append(string).append("',")
						.append(goods_ordertype.get(string)).append("],");
			}

			mv.addObject("allclick", allclick);
			mv.addObject("allsale", allsale);

			mv.addObject("clicksort", sort(clickcount, goodsnames));
			mv.addObject("salesort", sort(salecount, goodsnames));

			mv.addObject("stat", stat);
			mv.addObject("stat_title", "商城总体统计图");
			mv.addObject("begin", begin);
			mv.addObject("end", end);
			mv.addObject("times", Json.toJson(times, JsonFormat.compact()));
			mv.addObject("timeslength", CommUtil.null2Int(times.size() / 8));
			mv.addObject("goods_click",
					Json.toJson(goods_click, JsonFormat.compact()));
			mv.addObject("goods_collect",
					Json.toJson(goods_collect, JsonFormat.compact()));
			mv.addObject("goods_salenum",
					Json.toJson(goods_salenum, JsonFormat.compact()));
			mv.addObject("goods_clickfrom", clicksb);
			mv.addObject("goods_ordertype", ordersb);
		}

		return mv;
	}
	/**
	 * 排序，将点击量，销量等按名次返回
	 * 
	 * @return
	 */
	private List sort(Map<Long, Integer> map, Map<Long, String> goodsnames) {

		List<Long> keylist = new LinkedList<Long>();
		List<Integer> valuelist = new LinkedList<Integer>();

		Iterator<Long> it = map.keySet().iterator();
		while (it.hasNext()) {
			Long key = (Long) it.next();
			int count = map.get(key);
			boolean add = true;

			int length = valuelist.size();
			for (int i = 0; i < length; i++) {
				int value = valuelist.get(i);
				if (count > value) {
					valuelist.add(i, count);
					keylist.add(i, key);
					add = false;
					break;
				}
			}
			if (add) {
				valuelist.add(count);
				keylist.add(key);
			}

		}
		List result = new ArrayList();
		int length = keylist.size();
		for (int i = 0; i < length; i++) {
			if (i == 150) {
				break;
			}
			long key = keylist.get(i);
			Map map2 = new HashMap();
			map2.put("name", goodsnames.get(key));
			map2.put("data", map.get(key));
			result.add(map2);
		}

		return result;

	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
