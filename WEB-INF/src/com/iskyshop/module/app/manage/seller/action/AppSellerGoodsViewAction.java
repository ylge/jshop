package com.iskyshop.module.app.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: AppSellerGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家app商品
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-4-2
 * 
 * @version 1.0
 */
@Controller
public class AppSellerGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IComplaintGoodsService complaintGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IZTCGoldLogService iztcGoldLogService;
	@Autowired
	private IGoodsCartService cartService;
	@Autowired
	private LuceneVoTools luceneVoTools;

	/**
	 * 商家商品列表
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param goods_status
	 * @param orderby
	 * @param ordertype
	 * @param begin_count
	 * @param select_count
	 */
	@RequestMapping("/app/seller/goods_list.htm")
	public void goods_list(HttpServletRequest request,
			HttpServletResponse response, String user_id, String goods_status,
			String orderby, String ordertype, String begin_count,
			String select_count, String user_goodsclass_id, String goods_name) {
		if (goods_status == null || goods_status.equals("")) {
			goods_status = "0";
		}
		if (orderby == null || orderby.equals("")) {
			orderby = "addTime";
		}
		if (ordertype == null || ordertype.equals("")) {
			ordertype = "desc";
		}
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId());

		String hql = "";
		if (user_goodsclass_id != null && !"".equals(user_goodsclass_id)) {
			UserGoodsClass u_gc = this.userGoodsClassService
					.getObjById(CommUtil.null2Long(user_goodsclass_id));
			params.put("u_gc", u_gc);
			hql += "and :u_gc  member of obj.goods_ugcs ";
		}
		if (goods_name != null && !goods_name.equals("")) {
			params.put("goods_name", "%" + goods_name + "%");
			hql += " and obj.goods_name like :goods_name ";
		}

		List<Goods> list;
		if (CommUtil.null2Int(goods_status) == 1) {
			params.put("goods_status1", 1);
			params.put("goods_status2", -5);
			list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and (obj.goods_status=:goods_status1 or obj.goods_status=:goods_status2) "
							+ hql + "order by " + orderby + " " + ordertype,
							params, CommUtil.null2Int(begin_count),
							CommUtil.null2Int(select_count));
		} else {

			params.put("goods_status", CommUtil.null2Int(goods_status));
			list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status "
							+ hql + "order by " + orderby + " " + ordertype,
							params, CommUtil.null2Int(begin_count),
							CommUtil.null2Int(select_count));
		}
		List goods_list = new ArrayList();
		for (Goods goods : list) {
			Map map = new HashMap();
			map.put("id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_current_price",
					CommUtil.null2String(goods.getGoods_current_price()));// 商品现价
			String goods_main_photo = url// 系统默认商品图片
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (goods.getGoods_main_photo() != null) {// 商品主图片
				goods_main_photo = url + "/"
						+ goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName() + "_small."
						+ goods.getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			map.put("goods_salenum", goods.getGoods_salenum());// 销量
			map.put("goods_inventory", goods.getGoods_inventory());
			map.put("addTime", CommUtil.formatShortDate(goods.getAddTime()));
			goods_list.add(map);
		}
		json_map.put("goods_list", goods_list);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}
	/**
	 * 商品详情编辑
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param goods_id
	 */
	@RequestMapping("/app/seller/goods_edit.htm")
	public void goods_edit(HttpServletRequest request,
			HttpServletResponse response, String user_id, String goods_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		if (goods.getGoods_store().getId().equals(user.getStore().getId())) {
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
			json_map.put("id", goods.getId());
			json_map.put("goods_name", goods.getGoods_name());
			json_map.put("goods_current_price",
					CommUtil.null2String(goods.getGoods_current_price()));// 商品现价
			List photo_list = new ArrayList();
			String goods_main_photo = url// 系统默认商品图片
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (goods.getGoods_main_photo() != null) {// 商品主图片
				goods_main_photo = url + "/"
						+ goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName() + "_small."
						+ goods.getGoods_main_photo().getExt();
			}
			photo_list.add(goods_main_photo);
			for (Accessory acc : goods.getGoods_photos()) {// 添加附图
				photo_list.add(CommUtil.getURL(request) + "/" + acc.getPath()
						+ "/" + acc.getName());
			}
			json_map.put("photo_list", photo_list);
			json_map.put("goods_inventory", goods.getGoods_inventory());// 库存
			json_map.put("goods_recommend", goods.isGoods_recommend());// 推荐
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 详情编辑保存
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param goods_id
	 * @param goods_name
	 * @param goods_current_price
	 * @param goods_inventory
	 * @param goods_recommend
	 */
	@RequestMapping("/app/seller/goods_edit_save.htm")
	public void goods_edit_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String goods_id,
			String goods_name, String goods_current_price,
			String goods_inventory, String goods_recommend) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		if (goods.getGoods_store().getId().equals(user.getStore().getId())) {
			goods.setGoods_name(goods_name);

			BigDecimal old_price = goods.getGoods_current_price();
			goods.setPrice_history(old_price);

			goods.setGoods_price(new BigDecimal(goods_current_price));
			goods.setGoods_current_price(goods.getGoods_price());
			goods.setGoods_inventory(CommUtil.null2Int(goods_inventory));
			goods.setGoods_recommend(CommUtil.null2Boolean(goods_recommend));
			this.goodsService.update(goods);
			json_map.put("ret", 100);
		} else {
			json_map.put("ret", -100);
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/seller/goods_class.htm")
	public void goods_class(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Map para = new HashMap();
		para.put("user_id", user.getId());
		List<UserGoodsClass> classlist = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.parent=null and obj.display=1 order by sequence",
						para, -1, -1);
		List list = new ArrayList();
		for (UserGoodsClass userGoodsClass : classlist) {
			Map map = new HashMap();
			map.put("id", userGoodsClass.getId());
			map.put("className", userGoodsClass.getClassName());
			if (userGoodsClass.getChilds() != null
					&& userGoodsClass.getChilds().size() > 0) {
				List child_list = new ArrayList();
				for (UserGoodsClass child : userGoodsClass.getChilds()) {
					Map child_map = new HashMap();
					child_map.put("id", child.getId());
					child_map.put("className", child.getClassName());
					child_list.add(child_map);
				}
				map.put("child_list", child_list);
			}
			list.add(map);
		}
		json_map.put("class_list", list);

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/seller/goods_del.htm")
	public void goods_del(HttpServletRequest request,
			HttpServletResponse response, String user_id, String mulitId) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		String[] ids = mulitId.split(",");
		String success = "";
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				if (goods.getGoods_store().getUser().getId()
						.equals(user.getId())) {
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					Map params = new HashMap();
					params.put("gid", CommUtil.null2Long(id));
					List<ComplaintGoods> complaintGoodses = this.complaintGoodsService
							.query("select obj from ComplaintGoods obj where obj.goods.id=:gid",
									params, -1, -1);
					for (ComplaintGoods cg : complaintGoodses) {
						this.complaintGoodsService.delete(cg.getId());
					}
					List<GroupGoods> groupGoodses = this.groupGoodsService
							.query("select obj from GroupGoods obj where obj.gg_goods.id=:gid",
									params, -1, -1);
					for (GroupGoods gg : groupGoodses) {
						this.groupGoodsService.delete(gg.getId());
					}
					params.clear();
					for (Accessory acc : goods.getGoods_photos()) {
						params.put("acid", acc.getId());
						List<Album> als = this.albumService
								.query("select obj from Album obj where obj.album_cover.id = :acid",
										params, -1, -1);
						for (Album al : als) {
							al.setAlbum_cover(null);
							this.albumService.update(al);
						}
						params.clear();
					}
					if (goods.getGoods_main_photo() != null) {
						params.put("acid", goods.getGoods_main_photo().getId());
						List<Album> als = this.albumService
								.query("select obj from Album obj where obj.album_cover.id = :acid",
										params, -1, -1);
						for (Album al : als) {
							al.setAlbum_cover(null);
							this.albumService.update(al);
						}
						CommUtil.del_acc(request, goods.getGoods_main_photo());
						goods.setGoods_main_photo(null);
						this.goodsService.update(goods);
					}
					List<ZTCGoldLog> ztcGoldLogs = this.iztcGoldLogService
							.query("select obj from ZTCGoldLog obj where obj.zgl_goods_id="
									+ CommUtil.null2Long(id), null, -1, -1);
					if (ztcGoldLogs.size() > 0) {
						for (ZTCGoldLog ztcGoldLog : ztcGoldLogs) {
							this.iztcGoldLogService.delete(ztcGoldLog.getId());
						}
					}
					for (GoodsCart cart : goods.getCarts()) {
						this.cartService.delete(cart.getId());
					}
					goods.getCarts().clear();
					goods.getGoods_ugcs().clear();
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getAg_goods_list().clear();
					goods.getEvaluates().clear();
					goods.getGoods_photos().clear();
					goods.getGroup_goods_list().clear();
					if (this.goodsService.delete(goods.getId())) {
						success += goods.getId() + ",";
					}
					// 删除索引
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
					lucene.delete_index(CommUtil.null2String(id));
				}
			}
		}
		json_map.put("success", success);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/seller/goods_sale.htm")
	public void goods_sale(HttpServletRequest request,
			HttpServletResponse response, String user_id, String mulitId) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		String[] ids = mulitId.split(",");
		String success = "";
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(Long.parseLong(id));
				if (goods.getGoods_status() != -5) {

					if (goods.getGoods_store().getUser().getId()
							.equals(user.getId())) {
						int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
						goods.setGoods_status(goods_status);
						if (this.goodsService.update(goods)) {
							success += goods.getId() + ",";
						}
						if (goods_status == 0) {

							// 添加lucene索引
							String goods_lucene_path = System
									.getProperty("iskyshopb2b2c.root")
									+ File.separator
									+ "luence" + File.separator + "goods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneVo vo = this.luceneVoTools
									.updateGoodsIndex(goods);
							LuceneUtil lucene = LuceneUtil.instance();
							lucene.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(goods.getId()),
									vo);
						} else {
							// 删除索引
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
							lucene.delete_index(CommUtil.null2String(goods
									.getId()));
						}
					}
				}
			}
		}

		json_map.put("success", success);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
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
