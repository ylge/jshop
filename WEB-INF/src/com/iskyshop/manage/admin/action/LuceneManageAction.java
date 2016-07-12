package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.lucene.LuceneThread;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.view.web.tools.GroupViewTools;

/**
 * 
 * <p>
 * Title: LuceneManageAction.java
 * </p>
 * 
 * <p>
 * Description: 全文检索处理器
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
 * @author erikzhang
 * 
 * @date 2014年5月27日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class LuceneManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;

	@SecurityMapping(title = "全文检索设置", value = "/admin/lucene.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene.htm")
	public ModelAndView lucene(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/lucene.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String path = System.getProperty("iskyshopb2b2c.root") + File.separator
				+ "luence";
		File file = new File(path);
		if (!file.exists()) {
			CommUtil.createFolder(path);
		}
		mv.addObject("lucene_disk_size", CommUtil.fileSize(file));
		mv.addObject("lucene_disk_path", path);
		return mv;
	}

	@SecurityMapping(title = "全文检索关键字保存", value = "/admin/lucene_hot_save.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene_hot_save.htm")
	public void lucene_hot_save(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch,String lucenen_queue) {
		SysConfig obj = this.configService.getSysConfig();
		boolean ret = true;
		if (id.equals("")) {
			obj.setHotSearch(hotSearch);
			obj.setLucenen_queue(CommUtil.null2Int(lucenen_queue));
			obj.setAddTime(new Date());
			ret = this.configService.save(obj);
		} else {
			obj.setHotSearch(hotSearch);
			obj.setLucenen_queue(CommUtil.null2Int(lucenen_queue));
			ret = this.configService.update(obj);
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

	@SecurityMapping(title = "全文检索更新", value = "/admin/lucene_update.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene_update.htm")
	public void lucene_update(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch) {
		Map params = new HashMap();
		params.put("goods_status", 0);
		List<Goods> goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_status=:goods_status",
						params, -1, -1);
		params.clear();
		params.put("group_status", 1);
		List<GroupLifeGoods> lifeGoods_list = this.groupLifeGoodsService
				.query("select obj from GroupLifeGoods obj where obj.group_status=:group_status",
						params, -1, -1);
		List<GroupGoods> groupGoods_list = this.groupGoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:group_status",
						params, -1, -1);
		String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
				+ File.separator + "luence" + File.separator + "goods";
		String grouplifegoods_lucene_path = System.getProperty("iskyshopb2b2c.root")
				+ File.separator + "luence" + File.separator + "grouplifegoods";
		String groupgoods_lucene_path = System.getProperty("iskyshopb2b2c.root")
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		file = new File(grouplifegoods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(grouplifegoods_lucene_path);
		}
		file = new File(groupgoods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(groupgoods_lucene_path);
		}
		List<LuceneVo> goods_vo_list = new ArrayList<LuceneVo>();
		for (Goods goods : goods_list) {
			if(goods!=null){
			LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
			goods_vo_list.add(vo);
			}
		}
		List<LuceneVo> lifegoods_vo_list = new ArrayList<LuceneVo>();
		for (GroupLifeGoods goods : lifeGoods_list) {
			if(goods!=null){
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGg_name());
			vo.setVo_content(goods.getGroup_details());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getGroup_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGroupInfos().size());
			vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
			if (goods.getGroup_acc() != null) {
				vo.setVo_main_photo_url(goods.getGroup_acc().getPath() + "/"
						+ goods.getGroup_acc().getName());
			}
			if(goods.getGg_gc()!=null){
			vo.setVo_cat(goods.getGg_gc().getId().toString());
			}
			String rate = this.groupViewTools.getRate(
					CommUtil.null2Double(goods.getGroup_price()),
					CommUtil.null2Double(goods.getCost_price())).toString();
			vo.setVo_rate(rate);
			if (goods.getGg_ga() != null) {
				vo.setVo_goods_area(goods.getGg_ga().getId().toString());
			}
			lifegoods_vo_list.add(vo);
			}
		}

		List<LuceneVo> groupgoods_vo_list = new ArrayList<LuceneVo>();
		for (GroupGoods goods : groupGoods_list) {
			if(goods!=null){
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGg_name());
			vo.setVo_content(goods.getGg_content());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getGg_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGg_selled_count());
			vo.setVo_cost_price(CommUtil.null2Double(goods.getGg_goods()
					.getGoods_price()));
			if (goods.getGg_img() != null) {
				vo.setVo_main_photo_url(goods.getGg_img().getPath() + "/"
						+ goods.getGg_img().getName());
			}
			if(goods.getGg_gc()!=null){
			vo.setVo_cat(goods.getGg_gc().getId().toString());
			}
			vo.setVo_rate(CommUtil.null2String(goods.getGg_rebate()));
			if (goods.getGg_ga() != null) {
				vo.setVo_goods_area(goods.getGg_ga().getId().toString());
			}
			groupgoods_vo_list.add(vo);
			}
		}

		LuceneThread goods_thread = new LuceneThread(goods_lucene_path,
				goods_vo_list);
		LuceneThread lifegoods_thread = new LuceneThread(
				grouplifegoods_lucene_path, lifegoods_vo_list);
		LuceneThread groupgoods_thread = new LuceneThread(
				groupgoods_lucene_path, groupgoods_vo_list);
		Date d1 = new Date();
		if(goods_thread!=null){
		goods_thread.run();
		}
		lifegoods_thread.run();
		groupgoods_thread.run();
		Date d2 = new Date();
		String path = System.getProperty("iskyshopb2b2c.root") + File.separator
				+ "luence";
		Map map = new HashMap();
		map.put("run_time", d2.getTime() - d1.getTime());
		map.put("file_size", CommUtil.fileSize(new File(path)));
		map.put("update_time", CommUtil.formatLongDate(new Date()));
		SysConfig config = this.configService.getSysConfig();
		config.setLucene_update(new Date());
		this.configService.update(config);
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
