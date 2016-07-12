package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreAdjustInfo;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IStoreAdjustInfoService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;

/**
 * 
* <p>Title: StoreAdjustApplyAction.java</p>

* <p>Description: 商家店铺相关调整申请控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2015-1-5

* @version iskyshop_b2b2c_2015
 */
@Controller
public class StoreAdjustApplyAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private IUserService userService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IStoreAdjustInfoService adjustInfoService;
	

	@SecurityMapping(title = "调整类目申请", value = "/seller/adjust_goodsclass.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/adjust_goodsclass.htm")
	public ModelAndView adjust_goodsclass(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mv = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map map = new HashMap();
		map.put("store_id",store.getId());
		List<StoreAdjustInfo> infos =	this.adjustInfoService.query("select obj from StoreAdjustInfo obj where obj.store_id=:store_id",map, -1,-1);
		if(infos.size()==0){
			mv = new JModelAndView(
					"user/default/sellercenter/adjust_goodsclass.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			List<StoreGrade> grades = this.storeGradeService
					.query("select obj from StoreGrade obj order by obj.sequence asc",
							null, -1, -1);
			mv.addObject("grades", grades);
			List<GoodsClass> gcs = this.goodsclassService
					.query("select obj from GoodsClass obj where obj.parent.id is null ",
							null, -1, -1);
			mv.addObject("goodsClass", gcs);
			mv.addObject("store", store);
			if (user.getStore().getGc_detail_info() != null) {// 店铺详细类目
				Set<GoodsClass> detail_gcs = this.storeTools
						.query_store_DetailGc(user.getStore().getGc_detail_info());
				mv.addObject("detail_gcs", detail_gcs);
			}
			GoodsClass main_gc = this.goodsClassService.getObjById(user.getStore()
					.getGc_main_id());// 店铺主营类目
			mv.addObject("main_gc", main_gc);
			return mv;
		}else{
			StoreAdjustInfo adjustInfo = infos.get(0);
			mv = new JModelAndView(
					"user/default/sellercenter/adjust_info.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			if(adjustInfo.getApply_status()==0){
				mv.addObject("title","我们正在处理您提交的申请...");				
			}
			if(adjustInfo.getApply_status()==5){
				mv.addObject("title","您提交的调整申请已经被拒绝！");	
				mv.addObject("again",true);	
			}
		}
		return mv;
	}
	
	@SecurityMapping(title = "重新提交申请", value = "/seller/adjust_again.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/adjust_again.htm")
	public String adjust_again(HttpServletRequest request,
			HttpServletResponse response){
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map map = new HashMap();
		map.put("store_id",store.getId());
		List<StoreAdjustInfo> infos =	this.adjustInfoService.query("select obj from StoreAdjustInfo obj where obj.store_id=:store_id",map, -1,-1);
		for (StoreAdjustInfo info : infos) {
			this.adjustInfoService.delete(info.getId());
		}
		return "redirect:adjust_goodsclass.htm";
	}
	
	
	@SecurityMapping(title = "新增经营类目", value = "/seller/add_gc_detail.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/add_gc_detail.htm")
	public void add_gc_detail(HttpServletRequest request,
			HttpServletResponse response,String grade_id,String gc_main_id){
		String jsonList = "";
		StoreGrade storeGrade = this.storeGradeService.getObjById(CommUtil.null2Long(grade_id));
		if(storeGrade!=null&&storeGrade.getMain_limit()==1){
			GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(gc_main_id));
			List<Map> list = new ArrayList<Map>();
			for (GoodsClass child : gc.getChilds()) {
				Map map = new HashMap();
				map.put("id",child.getId());
				map.put("name",child.getClassName());
				list.add(map);
			}
			jsonList = Json.toJson(list, JsonFormat.compact());
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "保存类目", value = "/seller/adjust_gc_save.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/adjust_gc_save.htm")
	public void adjust_gc_save(HttpServletRequest request,
			HttpServletResponse response,String ids,String gc_detail_info){
		Map mapJson = null;//用于转换成店铺中的详细经营类目json
		String[] idsStr = ids.split(",");
		String listJson = "";
		for (String id : idsStr) {
			GoodsClass gc = this.goodsclassService.getObjById(CommUtil
					.null2Long(id));
			if(gc!=null){
				GoodsClass parent = gc.getParent();
				if(gc_detail_info!=null&&!gc_detail_info.equals("")){
					mapJson = Json.fromJson(HashMap.class, gc_detail_info);
					Map map = new HashMap();
					map.put("gc_id",id);
					map.put("gc_name",gc.getClassName());
					map.put("parent_name", parent.getClassName());
					map.put("parent_id", parent.getId());
					mapJson.put(id, map);
				}else{
					mapJson = new HashMap();
					Map map = new HashMap();
					map.put("gc_id",id);
					map.put("gc_name",gc.getClassName());
					map.put("parent_name", parent.getClassName());
					map.put("parent_id", parent.getId());
					mapJson.put(id, map);
				}
				gc_detail_info = Json.toJson(mapJson, JsonFormat.compact());
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(gc_detail_info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "删除类目", value = "/seller/adjust_gc_del.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/adjust_gc_del.htm")
	public void adjust_gc_del(HttpServletRequest request,
				HttpServletResponse response,String id,String gc_detail_info) {
		GoodsClass gc = this.goodsclassService.getObjById(CommUtil
				.null2Long(id));
		if(gc_detail_info!=null&&!gc_detail_info.equals("")&&gc!=null){
			Map mapJson = Json.fromJson(HashMap.class, gc_detail_info);
			mapJson.remove(id);
			gc_detail_info = Json.toJson(mapJson, JsonFormat.compact());
		}	
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(gc_detail_info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "调整类目申请保存", value = "/seller/adjust_apply_save.htm*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/adjust_apply_save.htm")
	public ModelAndView adjust_apply_save(HttpServletRequest request,
			HttpServletResponse response,String gc_main,String gc_detail_info,String store_grade){
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ModelAndView mv = null;
		Map map = new HashMap();
		map.put("store_id",store.getId());
		List<StoreAdjustInfo> infos =	this.adjustInfoService.query("select obj from StoreAdjustInfo obj where obj.store_id=:store_id",map, -1,-1);
		if(infos.size()==0){
			mv = new JModelAndView(
					"user/default/sellercenter/adjust_info.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			StoreAdjustInfo storeAdjustInfo = new StoreAdjustInfo();
			storeAdjustInfo.setAddTime(new Date());
			storeAdjustInfo.setStore_id(store.getId());
			storeAdjustInfo.setStore_name(store.getStore_name());
			storeAdjustInfo.setAdjust_type("gc");
			storeAdjustInfo.setApply_status(0);
			storeAdjustInfo.setAdjust_gc_info(Json.toJson(gc_detail_info, JsonFormat.compact()));
			GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(gc_main));
			storeAdjustInfo.setAdjust_gc_main(gc.getClassName());
			storeAdjustInfo.setAdjust_gc_main_id(CommUtil.null2Long(gc_main));
			StoreGrade sg = this.storeGradeService.getObjById(CommUtil.null2Long(store_grade));
			storeAdjustInfo.setAdjust_store_grade(sg.getGradeName());
			storeAdjustInfo.setAdjust_storeGrade_id(CommUtil.null2Long(store_grade));
			this.adjustInfoService.save(storeAdjustInfo);
			mv.addObject("title","申请成功，我们会尽快为您处理...");
			return mv;			
		}else{
			mv = new JModelAndView(
					"user/default/sellercenter/adjust_info.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("title","我们正在处理您提交的申请...");				
		}
		return mv;	
	}
}
