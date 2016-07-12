package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
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
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.FreeTools;

/**
 * 
 * <p>
 * Title: FreeGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营0元试用商品
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
 * @date 2014年11月12日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfFreeGoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IFreeClassService freeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private IFreeApplyLogService freeapplylogService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;

	/**
	 * FreeGoods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营0元试用商品列表", value = "/admin/self_freegoods_list.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freegoods_list.htm")
	public ModelAndView self_freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name, String beginTime,
			String endTime, String cls, String status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_freegoods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.freeType", new SysMap("freeType", 1), "=");
		if (free_name != null && !free_name.equals("")) {
			qo.addQuery("obj.free_name", new SysMap("free_name", "%"
					+ free_name + "%"), "like");
			mv.addObject("free_name", free_name);
		}
		if (cls != null && !cls.equals("")) {
			qo.addQuery("obj.class_id",
					new SysMap("class_id", CommUtil.null2Long(cls)), "=");
			mv.addObject("cls_id", cls);
		}
		if (status != null && status.equals("going")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("finish")) {
			qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 10), "=");
			mv.addObject("status", status);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeGoods.class, mv);
		IPageList pList = this.freegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/self_freegoods_list.htm", "", params, pList, mv);
		List<FreeClass> fcls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", freeTools);
		return mv;
	}

	/**
	 * freegoods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/admin/self_freegoods_add.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freegoods_add.htm")
	public ModelAndView self_freegoods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_freegoods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		List<FreeClass> cls = this.freeClassService.query(
				"select obj from FreeClass obj", null, -1, -1);
		mv.addObject("cls", cls);
		return mv;
	}

	/**
	 * freegoods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用商品编辑", value = "/admin/self_freegoods_edit.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freegoods_edit.htm")
	public ModelAndView self_freegoods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_freegoods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeGoods freegoods = this.freegoodsService.getObjById(Long
					.parseLong(id));
			List<FreeClass> cls = this.freeClassService.query(
					"select obj from FreeClass obj", null, -1, -1);
			mv.addObject("cls", cls);
			if (freegoods != null) {
				Goods goods = this.goodsService.getObjById(freegoods
						.getClass_id());
				mv.addObject("goods", goods);
			}
			mv.addObject("obj", freegoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * freegoods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品保存", value = "/admin/self_freegoods_save.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freegoods_save.htm")
	public ModelAndView self_freegoods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String class_id,
			String goods_id,String default_count) {
		WebForm wf = new WebForm();
		FreeGoods freegoods = null;
		if (id.equals("")) {
			freegoods = wf.toPo(request, FreeGoods.class);
			freegoods.setAddTime(new Date());
		} else {
			FreeGoods obj = this.freegoodsService
					.getObjById(Long.parseLong(id));
			freegoods = (FreeGoods) wf.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "free";
		Map map = new HashMap();
		try {
			String fileName = freegoods.getFree_acc() == null ? "" : freegoods
					.getFree_acc().getName();
			map = CommUtil.saveFileToServer(request, "free_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory free_acc = new Accessory();
					free_acc.setName(CommUtil.null2String(map.get("fileName")));
					free_acc.setExt(CommUtil.null2String(map.get("mime")));
					free_acc.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					free_acc.setPath(uploadFilePath + "/free");
					free_acc.setWidth(CommUtil.null2Int(map.get("width")));
					free_acc.setHeight(CommUtil.null2Int(map.get("height")));
					free_acc.setAddTime(new Date());
					this.accessoryService.save(free_acc);
					freegoods.setFree_acc(free_acc);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory free_acc = freegoods.getFree_acc();
					free_acc.setName(CommUtil.null2String(map.get("fileName")));
					free_acc.setExt(CommUtil.null2String(map.get("mime")));
					free_acc.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					free_acc.setPath(uploadFilePath + "/free");
					free_acc.setWidth(CommUtil.null2Int(map.get("width")));
					free_acc.setHeight(CommUtil.null2Int(map.get("height")));
					free_acc.setAddTime(new Date());
					this.accessoryService.update(free_acc);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		freegoods.setClass_id(CommUtil.null2Long(class_id));
		freegoods.setCurrent_count(CommUtil.null2Int(default_count));
		freegoods.setDefault_count(CommUtil.null2Int(default_count));
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		freegoods.setGoods_id(CommUtil.null2Long(goods_id));
		if (goods != null) {
			freegoods.setGoods_name(goods.getGoods_name());
			goods.setWhether_free(1);
			this.goodsService.update(goods);
		}
		freegoods.setFreeType(1);
		freegoods.setFreeStatus(5);
		if (id.equals("")) {
			this.freegoodsService.save(freegoods);
		} else
			this.freegoodsService.update(freegoods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/self_freegoods_list.htm");
		mv.addObject("op_title", "保存0元试用成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/self_freegoods_add.htm" + "?currentPage="
				+ currentPage);
		return mv;
	}

	@SecurityMapping(title = "0元试用商品删除", value = "/admin/self_freegoods_del.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freegoods_del.htm")
	public String self_freegoods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				FreeGoods freegoods = this.freegoodsService.getObjById(Long
						.parseLong(id));
				if (freegoods != null && freegoods.getFreeStatus() != 5
						&& freegoods.getFreeType() == 1) {
					Goods goods = this.goodsService.getObjById(freegoods
							.getGoods_id());
					goods.setWhether_free(0);
					this.goodsService.update(goods);
					this.freegoodsService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:self_freegoods_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "0元试用申请记录", value = "/admin/self_freeapply_logs.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_freeapply_logs.htm")
	public ModelAndView self_freeapply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String userName, String status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_freeapplylog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.freegoods_id",
				new SysMap("freegoods_id", CommUtil.null2Long(id)), "=");
		if (userName != null && !userName.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("userName", "%" + userName
					+ "%"), "like");
			mv.addObject("userName", userName);
		}
		if (status != null && status.equals("yes")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", 5), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("waiting")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", 0), "=");
			mv.addObject("status", status);
		}
		if (status != null && status.equals("no")) {
			qo.addQuery("obj.apply_status", new SysMap("apply_status", -5), "=");
			mv.addObject("status", status);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeApplyLog.class, mv);
		IPageList pList = this.freeapplylogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/self_freeapply_logs.htm", "",
				params, pList, mv);
		mv.addObject("id", id);
		return mv;
	}

	@SecurityMapping(title = "申请记录详情", value = "/admin/self_apply_log_info.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_apply_log_info.htm")
	public ModelAndView self_apply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_freeapplylog_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeApplyLog freeapplylog = this.freeapplylogService
					.getObjById(Long.parseLong(id));
			Map params = new HashMap();
			params.put("status", 0);
			List<ExpressCompany> expressCompanys = this.expressCompayService
					.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
							params, -1, -1);
			params.clear();
			params.put("sa_type", 1);
			List<ShipAddress> shipAddrs = this.shipAddressService
					.query("select obj from ShipAddress obj where obj.sa_type=:sa_type order by obj.sa_default desc,obj.sa_sequence asc",
							params, -1, -1);// 按照默认地址倒叙、其他地址按照索引升序排序，保证默认地址在第一位
			mv.addObject("shipAddrs", shipAddrs);
			mv.addObject("expressCompanys", expressCompanys);
			mv.addObject("obj", freeapplylog);
		}
		return mv;
	}

	@SecurityMapping(title = "申请记录详情保存", value = "/admin/self_apply_log_save.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/self_apply_log_save.htm")
	public ModelAndView self_apply_log_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String shipCode, String ec_id,String sa_id) {
		WebForm wf = new WebForm();
		FreeApplyLog freeapplylog = null;
		if (id.equals("")) {
			freeapplylog = wf.toPo(request, FreeApplyLog.class);
			freeapplylog.setAddTime(new Date());
		} else {
			FreeApplyLog obj = this.freeapplylogService.getObjById(Long
					.parseLong(id));
			freeapplylog = (FreeApplyLog) wf.toPo(request, obj);
		}
		if (status.equals("yes")) {
			ExpressCompany ec = this.expressCompayService.getObjById(CommUtil
					.null2Long(ec_id));
			ShipAddress sa = this.shipAddressService.getObjById(CommUtil
					.null2Long(sa_id));
			Area area = this.areaService.getObjById(sa.getSa_area_id());
			freeapplylog.setShip_addr(area.getParent().getParent().getAreaName()
					+ area.getParent().getAreaName() + area.getAreaName()
					+ sa.getSa_addr());
			freeapplylog.setShip_addr_id(sa.getId());
			freeapplylog.setShipCode(shipCode);
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			freeapplylog.setExpress_info(Json.toJson(json_map));
			freeapplylog.setApply_status(5);
			FreeGoods fg = this.freegoodsService.getObjById(freeapplylog.getFreegoods_id());
			int count = fg.getCurrent_count()-1;
			fg.setCurrent_count(count);
			if(count<=0){
				fg.setFreeStatus(10);
				this.freegoodsService.update(fg);
			}
			String msg_content = "您申请的0元试用："+freeapplylog.getFreegoods_name()+"已通过审核。";
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			User buyer = this.userService.getObjById(freeapplylog.getUser_id());
			msg.setToUser(buyer);
			this.messageService.save(msg);
		} else {
			freeapplylog.setApply_status(-5);
			freeapplylog.setEvaluate_status(2);
			String msg_content = "您申请的0元试用："+freeapplylog.getFreegoods_name()+"未过审核。";
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			User buyer = this.userService.getObjById(freeapplylog.getUser_id());
			msg.setToUser(buyer);
			this.messageService.save(msg);
		}
		if (id.equals("")) {
			this.freeapplylogService.save(freeapplylog);
		}
		this.freeapplylogService.update(freeapplylog);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/self_freeapply_logs.htm?id="+freeapplylog.getFreegoods_id());
		mv.addObject("op_title", "审核申请成功");
		return mv;
	}

	@RequestMapping("/admin/self_freegoods_ajax.htm")
	public void freegoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		FreeGoods obj = this.freegoodsService.getObjById(Long.parseLong(id));
		Field[] fields = FreeGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.freegoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "自营0元试用商品", value = "/admin/free_goods_self.htm*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping("/admin/free_goods_self.htm")
	public ModelAndView free_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/free_goods_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/admin/free_goods_self_load.htm")
	public void free_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("whether_free", 0);
		String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.goods_type=:goods_type and obj.goods_status=:goods_status and obj.whether_free=:whether_free";
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			Set<Long> ids = this.genericGcIds(gc);
			params.put("ids", ids);
			query = query + " and obj.gc.id in (:ids)";
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getStore_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory", obj.getGoods_inventory());
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}
}
