package com.iskyshop.manage.admin.action;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.DeliveryAddressQueryObject;
import com.iskyshop.foundation.domain.query.GoodsBrandQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.manage.admin.tools.AreaManageTools;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;

/**
 * 
* <p>Title: DeliveryAddressSelfManageAction.java</p>

* <p>Description: 超级后台自提点管理器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2014-11-14

* @version iskyshop_b2b2c_2015
 */
@Controller
public class DeliveryAddressManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IDeliveryAddressService deliveryAddressService;	
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaManageTools areaManageTools;
	@Autowired
	private DeliveryAddressTools deliveryAddressTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	
	/**
	 * 正在服务及暂停服务的自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点列表", value = "/admin/delivery_address_list.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_list.htm")
	public ModelAndView delivery_address_list(HttpServletRequest request,
			HttpServletResponse response,String currentPage, String orderBy,
			String orderType,String da_name,String da_contact_user,String da_type){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_address_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DeliveryAddressQueryObject qo = new DeliveryAddressQueryObject(currentPage, mv, orderBy,
				orderType);
		if (da_name != null && !da_name.equals("")) {
			qo.addQuery("obj.da_name", new SysMap("da_name", "%"
					+ da_name + "%"), "like");
			mv.addObject("da_name", da_name);
		}
		if (da_contact_user != null && !da_contact_user.equals("")) {
			qo.addQuery("obj.da_contact_user", new SysMap("da_contact_user", "%"
					+ da_contact_user + "%"), "like");
			mv.addObject("da_contact_user", da_contact_user);
		}
		if (da_type != null && !da_type.equals("")) {
			qo.addQuery("obj.da_type", new SysMap("da_type",
					CommUtil.null2Int(da_type)), "=");
			mv.addObject("da_type", da_type);
		}
		qo.addQuery("obj.da_status > 4", null);
		IPageList pList = this.deliveryAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("","","", pList, mv);
		mv.addObject("areaManageTools", areaManageTools);
		return mv;
	}
	
	/**
	 * 自提点申请列表
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请列表", value = "/admin/delivery_apply_list.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_apply_list.htm")
	public ModelAndView delivery_apply_list(HttpServletRequest request,
			HttpServletResponse response,String currentPage, String orderBy,
			String orderType){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_apply_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DeliveryAddressQueryObject qo = new DeliveryAddressQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.da_status < 5", null);
		IPageList pList = this.deliveryAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("","","", pList, mv);
		mv.addObject("areaManageTools", areaManageTools);
		return mv;
	}
	
	/**
	 * 超级后台审核自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请审核", value = "/admin/delivery_apply_audit.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_apply_audit.htm")
	public ModelAndView delivery_apply_audit(HttpServletRequest request,
			HttpServletResponse response,String id,String currentPage){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_address_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj",deliveryAddress);
		mv.addObject("service_day",this.deliveryAddressTools.query_service_day(deliveryAddress.getDa_service_day()));
		mv.addObject("areaManageTools", areaManageTools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("url","/admin/delivery_apply_list.htm?currentPage="+currentPage);
		mv.addObject("audit", true);
		return mv;
	}

	/**
	 * 超级后台自提点申请拒绝
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请拒绝", value = "/admin/delivery_apply_refuse.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_apply_refuse.htm")
	public String delivery_apply_refuse(HttpServletRequest request,
			HttpServletResponse response, String id,String currentPage) {
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		if(deliveryAddress!=null){
			deliveryAddress.setDa_status(4);
			this.deliveryAddressService.update(deliveryAddress);
		}
		return "redirect:" + "/admin/delivery_apply_list.htm?currentPage="+currentPage;
	}
	
	/**
	 * 超级后台自提点申请通过
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请通过", value = "/admin/delivery_apply_pass.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_apply_pass.htm")
	public String delivery_apply_pass(HttpServletRequest request,
			HttpServletResponse response, String id,String currentPage) {
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		if(deliveryAddress!=null){
			deliveryAddress.setDa_status(10);
			this.deliveryAddressService.update(deliveryAddress);
			//赋予权限
			User user = this.userService.getObjById(deliveryAddress.getDa_user_id());
			Map params = new HashMap();
			params.put("type", "DELIVERY");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type",
					params, -1, -1);
			for (Role role : roles) {
				user.getRoles().add(role);
			}
			user.getRoles().addAll(roles);
			this.userService.update(user);
		}
		return "redirect:" + "/admin/delivery_apply_list.htm?currentPage="+currentPage;
	}
	
	
	/**
	 * 超级后台查看自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点查看", value = "/admin/delivery_address_view.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_view.htm")
	public ModelAndView delivery_address_view(HttpServletRequest request,
			HttpServletResponse response,String id,String currentPage){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_address_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj",deliveryAddress);
		mv.addObject("service_day",this.deliveryAddressTools.query_service_day(deliveryAddress.getDa_service_day()));
		mv.addObject("areaManageTools", areaManageTools);
		mv.addObject("url","/admin/delivery_address_list.htm?currentPage="+currentPage);
		return mv;
	}
	
	/**
	 * 超级后台新增自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点新增", value = "/admin/delivery_address_add.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_add.htm")
	public ModelAndView delivery_address_add(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 超级后台编辑自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点编辑", value = "/admin/delivery_address_edit.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_edit.htm")
	public ModelAndView delivery_address_edit(HttpServletRequest request,
			HttpServletResponse response,String id){
		ModelAndView mv = new JModelAndView("admin/blue/delivery_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj",deliveryAddress);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("edit", true);
		return mv;
	}
	
	/**
	 * 超级后台保存自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点保存", value = "/admin/delivery_address_save.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_save.htm")
	public ModelAndView delivery_address_save(HttpServletRequest request,
			HttpServletResponse response,String id,String add_url,String list_url,
			String area3,String da_begin_time,String da_end_time,String da_service_day){
		WebForm wf = new WebForm();
		DeliveryAddress deliveryaddress =null;
		if (id.equals("")) {
			deliveryaddress = wf.toPo(request, DeliveryAddress.class);
			deliveryaddress.setAddTime(new Date());
		}else{
			DeliveryAddress obj=this.deliveryAddressService.getObjById(Long.parseLong(id));
			deliveryaddress = (DeliveryAddress)wf.toPo(request,obj);
		}
		deliveryaddress.setDa_area(this.areaService.getObjById(CommUtil.null2Long(area3)));
		deliveryaddress.setDa_service_day(da_service_day.toString());
		if (id.equals("")) {
			this.deliveryAddressService.save(deliveryaddress);
		} else{
			this.deliveryAddressService.update(deliveryaddress);
		}
	   ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request,response);
	   mv.addObject("list_url", list_url);
	   mv.addObject("op_title", "保存自提点成功");
	   if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
	   return mv;
	}
	
	/**
	 * 超级后台删除自提点
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点删除", value = "/admin/delivery_address_del.htm*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping("/admin/delivery_address_del.htm")
	public String delivery_address_del(HttpServletRequest request,
			HttpServletResponse response,String id,String currentPage){
		DeliveryAddress deliveryAddress = this.deliveryAddressService.getObjById(CommUtil.null2Long(id));
		if(deliveryAddress!=null){
			if(deliveryAddress.getDa_type()==0){
				this.deliveryAddressService.delete(CommUtil.null2Long(id));
			}else{
				this.remove_deliveryAddress_userRole(deliveryAddress.getDa_user_id());
				this.deliveryAddressService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:delivery_address_list.htm?currentPage="+currentPage;
	}
	
	private void remove_deliveryAddress_userRole(Long user_id){
		User user = this.userService.getObjById(user_id);
		if (user != null) {
			Set<Role> newRoles = new HashSet<Role>();
			for (Role role : user.getRoles()) {
				if (!role.getType().equals("DELIVERY")) {
					newRoles.add(role);
				}
			}
			user.getRoles().clear();
			for (Role role : newRoles) {
				user.getRoles().add(role);
			}
			user.getRoles().addAll(newRoles);
			user.setDelivery_id(null);
			this.userService.update(user);
		}
	}
	
}
