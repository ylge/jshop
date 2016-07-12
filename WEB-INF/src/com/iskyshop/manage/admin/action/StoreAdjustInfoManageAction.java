package com.iskyshop.manage.admin.action;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.foundation.service.IStoreAdjustInfoService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreAdjustInfo;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.query.StoreAdjustInfoQueryObject;

/**
 * 
* <p>Title: StoreAdjustInfoManageAction.java</p>

* <p>Description: 平台处理商家店铺调整申请的控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2015-1-6

* @version iskyshop_b2b2c_2015
 */
@Controller
public class StoreAdjustInfoManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private IStoreAdjustInfoService storeadjustinfoService;	
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreGradeService storeGradeService;
	
	/**
	 * 申请列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "申请列表", value = "/admin/adjust_info.htm*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping("/admin/adjust_info.htm")
	public ModelAndView adjust_info(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/adjust_info.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		StoreAdjustInfoQueryObject qo = new StoreAdjustInfoQueryObject(currentPage, mv, orderBy,
				orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,StoreAdjustInfo.class,mv);
		IPageList pList = this.storeadjustinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/adjust_info.htm","",
				params, pList, mv);
		return mv;
	}
	
	@SecurityMapping(title = "申请详情", value = "/admin/adjust_info.htm*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping("/admin/adjust_info_view.htm")
	public ModelAndView adjust_info_view(HttpServletRequest request,HttpServletResponse response,String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/adjust_info_view.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		StoreAdjustInfo storeAdjustInfo = this.storeadjustinfoService.getObjById(CommUtil.null2Long(id));
		mv.addObject("adjustInfo", storeAdjustInfo);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	@SecurityMapping(title = "申请审核", value = "/admin/adjust_info.htm*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping("/admin/adjust_info_audit.htm")
	public ModelAndView adjust_info_audit(HttpServletRequest request,HttpServletResponse response,String id,
			String currentPage,String type) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreAdjustInfo storeAdjustInfo = this.storeadjustinfoService.getObjById(CommUtil.null2Long(id));
		if("succeed".equals(type)){
			Store store = this.storeService.getObjById(storeAdjustInfo.getStore_id());
			StoreGrade grade = this.storeGradeService.getObjById(storeAdjustInfo.getAdjust_storeGrade_id());
			store.setGrade(grade);
			store.setGc_main_id(storeAdjustInfo.getAdjust_gc_main_id());
			store.setGc_detail_info(this.getStoreGC_detail_info(storeAdjustInfo.getAdjust_gc_info()));
			this.storeService.update(store);
			this.storeadjustinfoService.delete(storeAdjustInfo.getId());
			
			mv.addObject("op_title", "操作成功");
		}
		if("defeat".equals(type)){
			storeAdjustInfo.setApply_status(5);
			this.storeadjustinfoService.update(storeAdjustInfo);
			mv.addObject("op_title", "操作成功");
  		}
		mv.addObject("list_url", "/admin/adjust_info.htm?currentPage="+currentPage);
		return mv;
	}
	
	/**
	 * 由调整申请中的类目信息，得到店铺详细经营类目信息
	 * @param adjust_gc_info
	 * @return
	 */
	public String getStoreGC_detail_info(String adjust_gc_info){
		Map fromMap = Json.fromJson(HashMap.class, adjust_gc_info);
		if(fromMap==null){
			return null;
		}
		List<Map> toList = new ArrayList();
		Iterator<Map> it = fromMap.values().iterator();
		while (it.hasNext()) {
			Map map = it.next();
			int j = 0;
			for (;j < toList.size(); j++) {
				if(map.get("parent_id").equals(toList.get(j).get("m_id"))){
					List gc_list = (List) toList.get(j).get("gc_list");
					gc_list.add(CommUtil.null2Int(map.get("gc_id")));
					Map toMap = new HashMap();
					toMap.put("m_id", map.get("parent_id"));
					toMap.put("gc_list",gc_list);
					toList.set(j, toMap);
					break;
				}
			}
			if(j==toList.size()){
				List gc_list = new ArrayList();
				gc_list.add(CommUtil.null2Int(map.get("gc_id")));
				Map toMap = new HashMap();
				toMap.put("m_id", map.get("parent_id"));
				toMap.put("gc_list",gc_list);
				toList.add(toMap);						
			}
		}
		return Json.toJson(toList, JsonFormat.compact());
		
	}
	
}