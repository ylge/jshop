package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationService;
import com.iskyshop.module.cms.service.IInformationService;

/**
 * 
 * <p>
 * Title: StatTools.java
 * </p>
 * 
 * <p>
 * Description:统计工具类，用来在超级后台的首页显示统计信息，统计不是及时的，为了节约系统开支，系统定时进行数据统计并保存到数据库中
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
 * @date 2015-3-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class StatTools {
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IDeliveryAddressService deliveryService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPayoffLogService plService;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;

	public int query_store(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		list = this.storeService.query(
				"select obj.id from Store obj where obj.addTime>=:time",
				params, -1, -1);
		return list.size();
	}

	public int query_user(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		list = this.userService.query(
				"select obj.id from User obj where obj.addTime>=:time", params,
				-1, -1);
		return list.size();
	}

	public int query_live_user(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("lastLoginDate", cal.getTime());
		params.put("userRole", "ADMIN");
		list = this.userService
				.query("select obj.id from User obj where obj.lastLoginDate>=:lastLoginDate and obj.userRole!=:userRole",
						params, -1, -1);
		return list.size();
	}

	public int query_goods(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		list = this.goodsService.query(
				"select obj.id from Goods obj where obj.addTime>=:time",
				params, -1, -1);
		return list.size();
	}

	public int query_order(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		list = this.orderFormService.query(
				"select obj.id from OrderForm obj where obj.addTime>=:time",
				params, -1, -1);
		return list.size();
	}

	public int query_ztc(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("ztc_status", 2);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.addTime>=:time and obj.ztc_status=:ztc_status",
						params, -1, -1);
		return list.size();
	}

	public int query_delivery(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("da_status", 10);
		list = this.deliveryService
				.query("select obj.id from DeliveryAddress obj where obj.addTime>=:time and obj.da_status=:da_status",
						params, -1, -1);
		return list.size();
	}

	public int query_circle(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 5);
		list = this.circleService
				.query("select obj.id from Circle obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return list.size();
	}

	public int query_information(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 20);
		list = this.informationService
				.query("select obj.id from Information obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return list.size();
	}

	public int query_invitation(int count) {
		List list = new ArrayList();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		list = this.invitationService
				.query("select obj.id from CircleInvitation obj where obj.addTime>=:time",
						params, -1, -1);
		return list.size();
	}

	public int query_all_user() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		list = this.userService.query(
				"select obj.id from User obj where obj.userRole!=:userRole",
				params, -1, -1);
		return list.size();
	}

	public int query_all_goods() {
		List list = new ArrayList();
		list = this.goodsService.query("select obj.id from Goods obj", null,
				-1, -1);
		return list.size();
	}

	public int query_all_store() {
		List list = new ArrayList();
		list = this.storeService.query("select obj.id from Store obj", null,
				-1, -1);
		return list.size();
	}

	public int query_audit_store() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("store_status1", 5);
		params.put("store_status2", 10);
		list = this.storeService
				.query("select obj.id from Store obj where obj.store_status=:store_status1 or obj.store_status=:store_status2",
						params, -1, -1);
		return list.size();
	}

	public double query_all_amount() {
		double price = 0;
		Map params = new HashMap();
		params.put("order_status", 60);
		List<OrderForm> ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : ofs) {
			price = CommUtil.null2Double(of.getTotalPrice()) + price;
		}
		return price;
	}

	public int query_complaint(int count) {
		List list = new ArrayList();

		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 0);
		list = this.complaintService
				.query("select obj.id from Complaint obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return list.size();
	}

	public int query_payoff() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("status", 1);
		list = this.plService.query(
				"select obj.id from PayoffLog obj where obj.status=:status",
				params, -1, -1);
		return list.size();
	}

	public double query_all_user_balance() {
		List<User> users = this.userService.query(
				"select obj.availableBalance from User obj", null, -1, -1);
		double banlance = 0;
		for (int i = 0; i < users.size(); i++) {
			banlance = CommUtil.add(CommUtil.null2Double(users.get(i)),
					banlance);
		}
		return banlance;
	}

	public int query_refund() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("refund_status", 0);
		params.put("goods_return_status", "10");
		list = this.plService
				.query("select obj.id from ReturnGoodsLog obj where obj.refund_status=:refund_status and obj.goods_return_status=:goods_return_status",
						params, -1, -1);
		return list.size();
	}

	public int query_grouplife_refund() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("status", 5);
		list = this.groupinfoService.query(
				"select obj.id from GroupInfo obj where obj.status=:status",
				params, -1, -1);
		return list.size();
	}

	public int query_ztc_audit() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("ztc_status", 1);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.ztc_status=:ztc_status",
						params, -1, -1);
		return list.size();
	}

	public int query_delivery_audit() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("da_status", 0);
		list = this.deliveryService
				.query("select obj.id from DeliveryAddress obj where obj.da_status=:da_status",
						params, -1, -1);
		return list.size();
	}

	public int query_information_audit() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("status", 10);
		list = this.informationService.query(
				"select obj.id from Information obj where obj.status=:status",
				params, -1, -1);
		return list.size();
	}

	public int query_self_goods() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status",
						params, -1, -1);
		return list.size();
	}

	public int query_self_storage_goods() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 1);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status",
						params, -1, -1);
		return list.size();
	}

	public int query_self_consult() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("reply", false);
		params.put("whether_self", 1);
		list = this.consultService
				.query("select obj.id from Consult obj where obj.whether_self=:whether_self and obj.reply=:reply",
						params, -1, -1);
		return list.size();
	}

	public int query_self_order_shipping() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("order_status1", 20);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		list = this.orderFormService
				.query("select obj.id from OrderForm obj where (obj.order_status=:order_status1 or obj.order_status=:order_status2) and obj.order_form=:order_form and obj.order_cat!=:order_cat",
						params, -1, -1);
		return list.size();
	}

	public int query_self_order_pay() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("order_status1", 10);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		list = this.orderFormService
				.query("select obj.id from OrderForm obj where (obj.order_status=:order_status1 or obj.order_status=:order_status2) and obj.order_cat!=:order_cat and obj.order_form=:order_form",
						params, -1, -1);
		return list.size();
	}

	public int query_self_order_evaluate() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("order_status", 40);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		list = this.orderFormService
				.query("select obj.id from OrderForm obj where obj.order_status=:order_status and obj.order_cat!=:order_cat and obj.order_form=:order_form",
						params, -1, -1);
		return list.size();
	}

	public int query_self_all_order() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("order_form", 1);
		params.put("order_cat", 2);
		list = this.orderFormService
				.query("select obj.id from OrderForm obj where obj.order_form=:order_form and obj.order_cat!=:order_cat",
						params, -1, -1);
		return list.size();
	}

	public int query_self_return_apply() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_return_status", "5");
		params.put("goods_type", 0);
		list = this.returngoodslogService
				.query("select obj.id from ReturnGoodsLog obj where obj.goods_return_status=:goods_return_status and obj.goods_type=:goods_type",
						params, -1, -1);
		return list.size();
	}

	public int query_self_groupinfo_return_apply() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("status", 3);
		params.put("goods_type", 1);
		list = this.groupinfoService
				.query("select obj.id from GroupInfo obj where obj.status=:status and obj.lifeGoods.goods_type=:goods_type",
						params, -1, -1);
		return list.size();
	}

	// 2015版新增统计信息
	// 未审核商品数
	public int query_goods_audit() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_status", -5);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.goods_status=:goods_status",
						params, -1, -1);
		return list.size();
	}

	// 自营促销商品
	public int query_self_activity_goods() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("activity_status", 2);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.activity_status=:activity_status and obj.goods_type=:goods_type",
						params, -1, -1);
		return list.size();
	}

	public int query_self_group_goods() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("group_buy", 2);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.group_buy=:group_buy and obj.goods_type=:goods_type",
						params, -1, -1);
		return list.size();
	}

	public int query_self_group_life() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("group_type", 1);
		params.put("group_buy", 2);
		list = this.goodsService
				.query("select obj.id from Goods obj where obj.group_buy=:group_buy and obj.goods_type=:goods_type and obj.group.group_type=:group_type",
						params, -1, -1);
		return list.size();
	}

	public int query_self_free_goods() {
		List list = new ArrayList();
		Map params = new HashMap();
		params.put("freeStatus", 5);
		params.put("freeType", 1);
		list = this.freegoodsService
				.query("select obj.id from FreeGoods obj where obj.freeStatus=:freeStatus and obj.freeType=:freeType",
						params, -1, -1);
		return list.size();
	}

}
