package com.iskyshop.foundation.service.impl;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.dao.FreeApplyLogDAO;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoodsCart;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.StatTools;
import com.iskyshop.module.app.domain.AppPushLog;
import com.iskyshop.module.app.service.IAppPushLogService;
import com.iskyshop.module.app.view.tools.AppPushTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.view.web.tools.GoodsViewTools;

@Service
@Transactional
public class SysConfigService implements ISysConfigService {
	@Resource(name = "sysConfigDAO")
	private IGenericDAO<SysConfig> sysConfigDAO;
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDAO;
	@Resource(name = "goodsLogDAO")
	private IGenericDAO<GoodsLog> goodsLogDAO;
	@Resource(name = "zTCGlodLogDAO")
	private IGenericDAO<ZTCGoldLog> zTCGlodLogDAO;
	@Resource(name = "storeDAO")
	private IGenericDAO<Store> storeDAO;
	@Resource(name = "templateDAO")
	private IGenericDAO<Template> templateDAO;
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;
	@Resource(name = "messageDAO")
	private IGenericDAO<Message> messageDAO;
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDAO;
	@Resource(name = "payoffLogDAO")
	private IGenericDAO<PayoffLog> payoffLogDAO;
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDAO;
	@Resource(name = "groupLifeGoodsDAO")
	private IGenericDAO<GroupLifeGoods> groupLifeGoodsDAO;
	@Resource(name = "groupGoodsDAO")
	private IGenericDAO<GroupGoods> groupGoodsDAO;
	@Resource(name = "couponInfoDAO")
	private IGenericDAO<CouponInfo> couponInfoDAO;
	@Resource(name = "groupInfoDAO")
	private IGenericDAO<GroupInfo> groupInfoDAO;
	@Resource(name = "freeGoodsDAO")
	private IGenericDAO<FreeGoods> freeGoodsDAO;
	@Resource(name = "combinPlanDAO")
	private IGenericDAO<CombinPlan> combinPlanDAO;
	@Resource(name = "buyGiftDAO")
	private IGenericDAO<BuyGift> buyGiftDAO;
	@Resource(name = "integralGoodsCartDAO")
	private IGenericDAO<IntegralGoodsCart> integralGoodsCartDAO;
	@Resource(name = "enoughReduceDAO")
	private IGenericDAO<EnoughReduce> enoughReduceDAO;
	@Resource(name = "storeStatDAO")
	private IGenericDAO<StoreStat> storeStatDAO;
	@Resource(name = "mobileVerifyCodeDAO")
	private IGenericDAO<VerifyCode> mobileVerifyCodeDAO;
	@Resource(name = "goodsClassDAO")
	private IGenericDAO<GoodsClass> goodsClassDAO;
	@Resource(name = "storePointDAO")
	private IGenericDAO<StorePoint> storePointDAO;
	@Resource(name = "groupDAO")
	private IGenericDAO<Group> groupDAO;
	@Resource(name = "activityDAO")
	private IGenericDAO<Activity> activityDAO;
	@Resource(name = "activityGoodsDAO")
	private IGenericDAO<ActivityGoods> activityGoodsDAO;
	@Resource(name = "orderFormLogDAO")
	private IGenericDAO<OrderFormLog> orderFormLogDAO;
	@Resource(name = "returnGoodsLogDAO")
	private IGenericDAO<ReturnGoodsLog> returnGoodsLogDAO;
	@Resource(name = "evaluateDAO")
	private IGenericDAO<Evaluate> evaluateDAO;
	@Resource(name = "accessoryDAO")
	private IGenericDAO<Accessory> accessoryDAO;
	@Resource(name = "favoriteDAO")
	private IGenericDAO<Favorite> favoriteDAO;
	@Resource(name = "integralLogDAO")
	private IGenericDAO<IntegralLog> integralLogDao;
	@Autowired
	private StatTools statTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IAppPushLogService appPushLogService;
	@Autowired
	private AppPushTools appPushTools;
	@Autowired
	private FreeApplyLogDAO freeApplyLogDAO;

	@Transactional(readOnly = false)
	public boolean delete(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional(readOnly = true)
	public SysConfig getSysConfig() {
		// TODO Auto-generated method stub
		List<SysConfig> configs = this.sysConfigDAO.query(
				"select obj from SysConfig obj", null, -1, -1);
		if (configs != null && configs.size() > 0) {
			SysConfig sc = configs.get(0);
			if (sc.getUploadFilePath() == null) {
				sc.setUploadFilePath(Globals.UPLOAD_FILE_PATH);
			}
			if (sc.getSysLanguage() == null) {
				sc.setSysLanguage(Globals.DEFAULT_SYSTEM_LANGUAGE);
			}
			if (sc.getWebsiteName() == null || sc.getWebsiteName().equals("")) {
				sc.setWebsiteName(Globals.DEFAULT_WBESITE_NAME);
			}
			if (sc.getCloseReason() == null || sc.getCloseReason().equals("")) {
				sc.setCloseReason(Globals.DEFAULT_CLOSE_REASON);
			}
			if (sc.getTitle() == null || sc.getTitle().equals("")) {
				sc.setTitle(Globals.DEFAULT_SYSTEM_TITLE);
			}
			if (sc.getImageSaveType() == null
					|| sc.getImageSaveType().equals("")) {
				sc.setImageSaveType(Globals.DEFAULT_IMAGESAVETYPE);
			}
			if (sc.getImageFilesize() == 0) {
				sc.setImageFilesize(Globals.DEFAULT_IMAGE_SIZE);
			}
			if (sc.getSmallWidth() == 0) {
				sc.setSmallWidth(Globals.DEFAULT_IMAGE_SMALL_WIDTH);
			}
			if (sc.getSmallHeight() == 0) {
				sc.setSmallHeight(Globals.DEFAULT_IMAGE_SMALL_HEIGH);
			}
			if (sc.getMiddleWidth() == 0) {
				sc.setMiddleWidth(Globals.DEFAULT_IMAGE_MIDDLE_WIDTH);
			}
			if (sc.getMiddleHeight() == 0) {
				sc.setMiddleHeight(Globals.DEFAULT_IMAGE_MIDDLE_HEIGH);
			}
			if (sc.getBigHeight() == 0) {
				sc.setBigHeight(Globals.DEFAULT_IMAGE_BIG_HEIGH);
			}
			if (sc.getBigWidth() == 0) {
				sc.setBigWidth(Globals.DEFAULT_IMAGE_BIG_WIDTH);
			}
			if (sc.getImageSuffix() == null || sc.getImageSuffix().equals("")) {
				sc.setImageSuffix(Globals.DEFAULT_IMAGE_SUFFIX);
			}
			if (sc.getStoreImage() == null) {
				Accessory storeImage = new Accessory();
				storeImage.setPath("resources/style/common/images");
				storeImage.setName("store.jpg");
				sc.setStoreImage(storeImage);
			}
			if (sc.getGoodsImage() == null) {
				Accessory goodsImage = new Accessory();
				goodsImage.setPath("resources/style/common/images");
				goodsImage.setName("good.jpg");
				sc.setGoodsImage(goodsImage);
			}
			if (sc.getMemberIcon() == null) {
				Accessory memberIcon = new Accessory();
				memberIcon.setPath("resources/style/common/images");
				memberIcon.setName("member.jpg");
				sc.setMemberIcon(memberIcon);
			}
			if (sc.getSecurityCodeType() == null
					|| sc.getSecurityCodeType().equals("")) {
				sc.setSecurityCodeType(Globals.SECURITY_CODE_TYPE);
			}
			if (sc.getWebsiteCss() == null || sc.getWebsiteCss().equals("")) {
				sc.setWebsiteCss(Globals.DEFAULT_THEME);
			}
			if (sc.getPayoff_date() == null) {
				Calendar cale = Calendar.getInstance();
				cale.set(Calendar.DAY_OF_MONTH,
						cale.getActualMaximum(Calendar.DAY_OF_MONTH));
				sc.setPayoff_date(cale.getTime());
			}
			if (sc.getSmsURL() == null || sc.getSmsURL().equals("")) {
				sc.setSmsURL(Globals.DEFAULT_SMS_URL);
			}
			if (sc.getAuto_order_notice() == 0) {
				sc.setAuto_order_notice(3);
			}
			if (sc.getAuto_order_evaluate() == 0) {
				sc.setAuto_order_evaluate(7);
			}
			if (sc.getAuto_order_return() == 0) {
				sc.setAuto_order_return(7);
			}
			if (sc.getAuto_order_confirm() == 0) {
				sc.setAuto_order_confirm(7);
			}
			if (sc.getGrouplife_order_return() == 0) {
				sc.setGrouplife_order_return(7);
			}
			return sc;
		} else {
			SysConfig sc = new SysConfig();
			sc.setUploadFilePath(Globals.UPLOAD_FILE_PATH);
			sc.setWebsiteName(Globals.DEFAULT_WBESITE_NAME);
			sc.setSysLanguage(Globals.DEFAULT_SYSTEM_LANGUAGE);
			sc.setTitle(Globals.DEFAULT_SYSTEM_TITLE);
			sc.setSecurityCodeType(Globals.SECURITY_CODE_TYPE);
			sc.setEmailEnable(Globals.EAMIL_ENABLE);
			sc.setCloseReason(Globals.DEFAULT_CLOSE_REASON);
			sc.setImageSaveType(Globals.DEFAULT_IMAGESAVETYPE);
			sc.setImageFilesize(Globals.DEFAULT_IMAGE_SIZE);
			sc.setSmallWidth(Globals.DEFAULT_IMAGE_SMALL_WIDTH);
			sc.setSmallHeight(Globals.DEFAULT_IMAGE_SMALL_HEIGH);
			sc.setMiddleHeight(Globals.DEFAULT_IMAGE_MIDDLE_HEIGH);
			sc.setMiddleWidth(Globals.DEFAULT_IMAGE_MIDDLE_WIDTH);
			sc.setBigHeight(Globals.DEFAULT_IMAGE_BIG_HEIGH);
			sc.setBigWidth(Globals.DEFAULT_IMAGE_BIG_WIDTH);
			sc.setImageSuffix(Globals.DEFAULT_IMAGE_SUFFIX);
			sc.setComplaint_time(Globals.DEFAULT_COMPLAINT_TIME);
			sc.setWebsiteCss(Globals.DEFAULT_THEME);
			sc.setSmsURL(Globals.DEFAULT_SMS_URL);
			Accessory goodsImage = new Accessory();
			goodsImage.setPath("resources/style/common/images");
			goodsImage.setName("good.jpg");
			sc.setGoodsImage(goodsImage);
			Accessory storeImage = new Accessory();
			storeImage.setPath("resources/style/common/images");
			storeImage.setName("store.jpg");
			sc.setStoreImage(storeImage);
			Accessory memberIcon = new Accessory();
			memberIcon.setPath("resources/style/common/images");
			memberIcon.setName("member.jpg");
			sc.setMemberIcon(memberIcon);
			Calendar cale = Calendar.getInstance();
			cale.set(Calendar.DAY_OF_MONTH,
					cale.getActualMaximum(Calendar.DAY_OF_MONTH));
			sc.setPayoff_date(cale.getTime());
			sc.setAuto_order_notice(3);
			sc.setAuto_order_evaluate(7);
			sc.setAuto_order_return(7);
			sc.setAuto_order_confirm(7);
			sc.setGrouplife_order_return(7);
			return sc;
		}
	}

	@Transactional(readOnly = false)
	public boolean save(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		try {
			this.sysConfigDAO.save(shopConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean update(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		try {
			this.sysConfigDAO.update(shopConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 系统零时定时任务控制器，每天00:00:01秒执行 此处已添加事务，不可使用try catch捕获异常，否则事务回滚将失效
	 * 
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runTimerByDay() {
		// TODO Auto-generated method stub
		SysConfig sysConfig = this.getSysConfig();
		// 处理直通车信息
		Map params = new HashMap();
		params.put("ztc_status", 2);
		List<Goods> goods_audit_list = this.goodsDAO.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);// 审核通过但未开通的直通车商品
		for (Goods goods : goods_audit_list) {
			if (goods.getZtc_begin_time().before(new Date())) {
				goods.setZtc_dredge_price(goods.getZtc_price());
				goods.setZtc_status(3);
				this.goodsDAO.update(goods);
			}
		}
		params.clear();
		params.put("ztc_status", 3);
		goods_audit_list = this.goodsDAO.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);
		for (Goods goods : goods_audit_list) {// 已经开通的商品扣除当日金币，金币不足时关闭直通车
			if (goods.getZtc_gold() >= goods.getZtc_price()) {
				goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
				goods.setZtc_dredge_price(goods.getZtc_price());
				this.goodsDAO.update(goods);
				ZTCGoldLog log = new ZTCGoldLog();
				log.setAddTime(new Date());
				log.setZgl_content("直通车消耗金币");
				log.setZgl_gold(goods.getZtc_price());
				log.setZgl_goods_id(goods.getId());
				log.setGoods_name(goods.getGoods_name());
				log.setStore_name(goods.getGoods_store().getStore_name());
				log.setUser_name(goods.getGoods_store().getUser().getUsername());
				log.setZgl_type(1);
				this.zTCGlodLogDAO.save(log);
			} else {
				goods.setZtc_status(0);
				goods.setZtc_dredge_price(0);
				goods.setZtc_pay_status(0);
				goods.setZtc_apply_time(null);
				this.goodsDAO.update(goods);
			}
		}
		// 处理店铺到期,2015版开始店铺到期对店铺商品不做处理，商家只可操作订单管理（管理已经下单并支付的订单），店铺过期后商品不可购买
		List<Store> stores = this.storeDAO.query(
				"select obj from Store obj where obj.validity is not null",
				null, -1, -1);
		for (Store store : stores) {
			if (store.getValidity().before(new Date())) {// 处理已经过期的店铺
				store.setStore_status(25);// 设定店铺状态为25，到期自动关闭
				this.storeDAO.update(store);
				Template template = this.templateDAO.getBy(null, "mark",
						"msg_toseller_store_auto_closed_notify");
				if (template != null && template.isOpen()) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(template.getContent());
					msg.setFromUser(this.userDAO.getBy(null, "userName",
							"admin"));
					msg.setStatus(0);
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageDAO.save(msg);
				}
			}
		}
		// 处理超过1天未登录用户并且未提交订单的购物车信息
		params.clear();
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		List<GoodsCart> cart_list = this.goodsCartDAO
				.query("select obj from GoodsCart obj where obj.user.id is null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.remove(gc.getId());
		}
		// 处理超过7天已经登录用户未提交订单的购物车信息
		params.clear();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		cart_list = this.goodsCartDAO
				.query("select obj from GoodsCart obj where obj.user.id is not null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.remove(gc.getId());
		}
		// 处理超过7天用户未提交订单的积分商品购物车
		params.clear();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		params.put("addTime", cal.getTime());
		List<IntegralGoodsCart> ig_cart_list = this.integralGoodsCartDAO
				.query("select obj from IntegralGoodsCart obj where obj.addTime<=:addTime",
						params, -1, -1);
		for (IntegralGoodsCart igc : ig_cart_list) {
			this.integralGoodsCartDAO.remove(igc.getId());
		}
		// 系统处理最近结算日期
		int payoff_count = sysConfig.getPayoff_count();
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int allDate = a.get(Calendar.DATE);// 当月总天数
		String selected = "";
		if (payoff_count == 1) {
			selected = CommUtil.null2String(allDate);
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		}
		Date payoff_data = new Date();
		int now_date = payoff_data.getDate();
		String str[] = selected.split(",");
		for (String payoff_date : str) {
			if (CommUtil.null2Int(payoff_date) >= now_date) {
				payoff_data.setDate(CommUtil.null2Int(payoff_date));
				payoff_data.setHours(0);
				payoff_data.setMinutes(00);
				payoff_data.setSeconds(01);
				break;
			}
		}
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		ms = "今天是"
				+ DateFormat.getDateInstance(DateFormat.FULL)
						.format(new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。";
		sysConfig.setPayoff_mag_default(ms);
		sysConfig.setPayoff_date(payoff_data);
		this.sysConfigDAO.update(sysConfig);
		params.clear();
		params.put("status", 1);
		List<PayoffLog> payofflogs_1 = this.payoffLogDAO
				.query("select obj from PayoffLog obj where obj.status=:status order by addTime desc",
						params, -1, -1);// 查询所有可结算账单，设置为未结算账单，可以防止上次结算日没有结算的账单在结算日之后的日期结算
		for (PayoffLog temp : payofflogs_1) {
			temp.setStatus(0);
			this.payoffLogDAO.update(temp);
		}
		params.clear();
		params.put("status", 0);
		params.put("PayoffTime", sysConfig.getPayoff_date());
		List<PayoffLog> payofflogs = this.payoffLogDAO
				.query("select obj from PayoffLog obj where obj.status=:status and obj.addTime<:PayoffTime order by addTime desc",
						params, -1, -1);// 结算日之前的所有未结算账单
		for (PayoffLog obj : payofflogs) {
			OrderForm of = this.orderFormDAO.get(CommUtil.null2Long(obj
					.getO_id()));
			Date Payoff_date = this.getSysConfig().getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			boolean payoff = false;// 当天是否为结算日期
			if (Payoff_date.after(now) && Payoff_date.before(next)) {
				payoff = true;
			}
			if (of.getOrder_cat() == 2) {
				if (of.getOrder_status() == 20 && payoff) {// 团购消费码订单
					obj.setStatus(1);// 设置当天可结算的账单
				}
			}
			if (of.getOrder_cat() == 0) {
				if (of.getOrder_status() >= 40 && payoff) {// 账单对应订单已经评价完成或者不可评价时
					obj.setStatus(1);// 设置当天可结算的账单
				}
				if (obj.getPayoff_type() == -1) {// 账单为退款账单，系统自动判定该退款账单为申请状态
					if (of.getOrder_status() >= 40 && payoff) {// 账单对应订单已经评价完成或者不可评价时
						obj.setStatus(3);
						obj.setApply_time(new Date());
					}
				}
			}
			this.payoffLogDAO.update(obj);
		}
		// 处理过期的生活团购
		params.clear();
		params.put("status", 1);
		params.put("end_time", new Date());
		List<GroupLifeGoods> groups = this.groupLifeGoodsDAO
				.query("select obj from GroupLifeGoods obj where obj.group_status=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupLifeGoods group : groups) {
			group.setGroup_status(-2);
			groupLifeGoodsDAO.update(group);
			// 删除索引
			String goodslife_lucene_path = System
					.getProperty("iskyshopb2b2c.root")
					+ File.separator
					+ "luence" + File.separator + "lifegoods";
			File filelife = new File(goodslife_lucene_path);
			if (!filelife.exists()) {
				CommUtil.createFolder(goodslife_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goodslife_lucene_path);
			lucene.delete_index(CommUtil.null2String(group.getId()));
		}
		// 处理过期的团购
		params.clear();
		params.put("status", -2);
		params.put("end_time", new Date());
		List<GroupGoods> groupgoodes = this.groupGoodsDAO
				.query("select obj from GroupGoods obj where obj.gg_status!=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupGoods group : groupgoodes) {
			group.setGg_status(-2);
			groupGoodsDAO.update(group);
			Goods goods = group.getGg_goods();
			// 团购过期，将商品还原
			goods.setGroup(null);
			goods.setGroup_buy(0);
			goods.setGoods_current_price(goods.getStore_price());
			this.goodsDAO.update(goods);

			// 删除索引
			String goodsgroup_lucene_path = System
					.getProperty("iskyshopb2b2c.root")
					+ File.separator
					+ "luence" + File.separator + "groupgoods";
			File filegroup = new File(goodsgroup_lucene_path);
			if (!filegroup.exists()) {
				CommUtil.createFolder(goodsgroup_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goodsgroup_lucene_path);
			lucene.delete_index(CommUtil.null2String(group.getId()));
		}
		// 处理即将开始的团购
		params.clear();
		params.put("status", 2);
		params.put("begin_time", new Date());
		List<GroupGoods> begin_groupgoodes = this.groupGoodsDAO
				.query("select obj from GroupGoods obj where obj.gg_status=:status and obj.beginTime<=:begin_time",
						params, -1, -1);
		String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		for (GroupGoods gg : begin_groupgoodes) {
			gg.setGg_status(1);
			groupGoodsDAO.update(gg);
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(2);
			goods.setGroup(gg.getGroup());
			goods.setGoods_current_price(gg.getGg_price());
			this.goodsDAO.update(goods);
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(gg.getId());
			vo.setVo_title(gg.getGg_name());
			vo.setVo_content(gg.getGg_content());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(gg.getGg_price()));
			vo.setVo_add_time(gg.getAddTime().getTime());
			vo.setVo_goods_salenum(gg.getGg_selled_count());
			if (gg.getGg_img() != null) {
				vo.setVo_main_photo_url(gg.getGg_img().getPath() + "/"
						+ gg.getGg_img().getName());
			}
			vo.setVo_cat(gg.getGg_gc().getId().toString());
			vo.setVo_rate(CommUtil.null2String(gg.getGg_rebate()));
			vo.setVo_goods_area(gg.getGg_ga().getId().toString());
			lucene.writeIndex(vo);
		}
		// 处理已经过期的团购券
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<GroupInfo> groupInfos = this.groupInfoDAO
				.query("select obj from GroupInfo obj where obj.status=:status and obj.lifeGoods.endTime<=:end_time",
						params, -1, -1);
		for (GroupInfo info : groupInfos) {
			info.setStatus(-1);
			groupInfoDAO.update(info);
		}
		// 处理已经过期的优惠券
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<CouponInfo> couponInfos = this.couponInfoDAO
				.query("select obj from CouponInfo obj where obj.status=:status and obj.coupon.coupon_end_time<=:end_time",
						params, -1, -1);
		for (CouponInfo couponInfo : couponInfos) {
			couponInfo.setStatus(-1);
			couponInfoDAO.update(couponInfo);
		}
		// 处理组合销售商品过期
		params.clear();
		params.put("combin_status", 1);
		params.put("combin_status0", 0);
		params.put("endTime", new Date());
		List<CombinPlan> combins = this.combinPlanDAO
				.query("select obj from CombinPlan obj where obj.combin_status=:combin_status and obj.endTime<=:endTime or obj.combin_status=:combin_status0 and obj.endTime<=:endTime",
						params, -1, -1);
		for (CombinPlan obj : combins) {
			obj.setCombin_status(-2);
			this.combinPlanDAO.update(obj);
			Goods goods = this.goodsDAO.get(obj.getMain_goods_id());
			if (goods.getCombin_status() == 1) {
				if (obj.getCombin_type() == 0) {
					if (goods.getCombin_suit_id().equals(obj.getId())) {
						goods.setCombin_suit_id(null);
					}
				} else {
					if (goods.getCombin_parts_id().equals(obj.getId())) {
						goods.setCombin_parts_id(null);
					}
				}
				goods.setCombin_status(0);
				this.goodsDAO.update(goods);
			}
		}
		// 处理已经过期的满就送
		params.clear();
		params.put("gift_status", 10);
		params.put("end_time", new Date());
		List<BuyGift> bgs = this.buyGiftDAO
				.query("select obj from BuyGift obj where obj.gift_status=:gift_status and obj.endTime<=:end_time",
						params, -1, -1);
		for (BuyGift bg : bgs) {
			bg.setGift_status(20);
			List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
			maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
			for (Map map : maps) {
				Goods goods = this.goodsDAO.get(CommUtil.null2Long(map
						.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					this.goodsDAO.update(goods);
				}
			}
			this.buyGiftDAO.update(bg);
		}
		// 处理过期的满就减
		params.clear();
		params.put("erstatus", 10);
		params.put("erend_time", new Date());
		List<EnoughReduce> er = this.enoughReduceDAO
				.query("select obj from EnoughReduce obj where obj.erstatus=:erstatus and obj.erend_time<=:erend_time",
						params, -1, -1);
		for (EnoughReduce enoughReduce : er) {
			enoughReduce.setErstatus(20);
			this.enoughReduceDAO.update(enoughReduce);
			String goods_json = enoughReduce.getErgoods_ids_json();
			List<String> goods_id_list = (List) Json.fromJson(goods_json);
			for (String goods_id : goods_id_list) {
				Goods ergood = this.goodsDAO.get(CommUtil.null2Long(goods_id));
				ergood.setEnough_reduce(0);
				ergood.setOrder_enough_reduce_id(null);
				this.goodsDAO.update(ergood);
			}
		}
		// 处理过期的0元试用
		params.clear();
		params.put("freeStatus", 5);
		params.put("endTime", new Date());
		List<FreeGoods> fgs = this.freeGoodsDAO
				.query("select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.endTime<=:endTime",
						params, -1, -1);
		for (FreeGoods fg : fgs) {
			fg.setFreeStatus(10);
			this.freeGoodsDAO.update(fg);
			params.clear();
			params.put("freeId", fg.getId());
			List<FreeApplyLog> fals = this.freeApplyLogDAO
					.query("select obj from FreeApplyLog obj where obj.freegoods_id=:freeId and obj.evaluate_status=0",
							params, -1, -1);
			for (FreeApplyLog fal : fals) {
				fal.setEvaluate_status(2);
				this.freeApplyLogDAO.update(fal);
			}
		}

	}

	/**
	 * 系统半小时定时任务控制器，每半小时运行一次 此处已添加事务，不可使用try catch捕获异常，否则事务回滚将失效
	 * 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runTimerByHalfhour() throws Exception {
		// TODO Auto-generated method stub
		// 统计信息
		SysConfig sc = this.getSysConfig();
		List<StoreStat> stats = this.storeStatDAO.query(
				"select obj from StoreStat obj", null, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		stat.setAddTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		stat.setNext_time(cal.getTime());
		stat.setWeek_complaint(this.statTools.query_complaint(-7));
		stat.setWeek_goods(this.statTools.query_goods(-7));
		stat.setWeek_order(this.statTools.query_order(-7));
		stat.setWeek_store(this.statTools.query_store(-7));
		stat.setWeek_user(this.statTools.query_user(-7));
		stat.setWeek_live_user(this.statTools.query_live_user(-7));
		stat.setWeek_ztc(this.statTools.query_ztc(-7));
		stat.setWeek_delivery(this.statTools.query_delivery(-7));
		stat.setWeek_information(this.statTools.query_information(-7));
		stat.setWeek_invitation(this.statTools.query_invitation(-7));
		stat.setWeek_circle(this.statTools.query_circle(-7));
		stat.setAll_goods(this.statTools.query_all_goods());
		stat.setAll_store(this.statTools.query_all_store());
		stat.setAll_user(this.statTools.query_all_user());
		stat.setStore_audit(this.statTools.query_audit_store());
		stat.setOrder_amount(BigDecimal.valueOf(this.statTools
				.query_all_amount()));
		stat.setNot_payoff_num(this.statTools.query_payoff());
		stat.setNot_refund(this.statTools.query_refund());
		stat.setNot_grouplife_refund(this.statTools.query_grouplife_refund());
		stat.setAll_sale_amount(CommUtil.null2Int(sc.getPayoff_all_sale()));
		stat.setAll_commission_amount(CommUtil.null2Int(sc
				.getPayoff_all_commission()));
		stat.setAll_payoff_amount(CommUtil.null2Int(sc.getPayoff_all_amount()));
		stat.setAll_payoff_amount_reality(CommUtil.null2Int(sc
				.getPayoff_all_amount_reality()));
		stat.setAll_user_balance(BigDecimal.valueOf(this.statTools
				.query_all_user_balance()));
		stat.setZtc_audit_count(this.statTools.query_ztc_audit());
		stat.setDelivery_audit_count(this.statTools.query_delivery_audit());
		stat.setInformation_audit_count(this.statTools
				.query_information_audit());
		stat.setSelf_goods(this.statTools.query_self_goods());
		stat.setSelf_storage_goods(this.statTools.query_self_storage_goods());
		stat.setSelf_order_shipping(this.statTools.query_self_order_shipping());
		stat.setSelf_order_pay(this.statTools.query_self_order_pay());
		stat.setSelf_order_evaluate(this.statTools.query_self_order_evaluate());
		stat.setSelf_all_order(this.statTools.query_self_all_order());
		stat.setSelf_return_apply(this.statTools.query_self_return_apply());
		stat.setSelf_grouplife_refund(this.statTools
				.query_self_groupinfo_return_apply());
		stat.setGoods_audit(this.statTools.query_goods_audit());
		stat.setSelf_goods_consult(this.statTools.query_self_consult());
		stat.setSelf_activity_goods(this.statTools.query_self_activity_goods());
		stat.setSelf_group_goods(this.statTools.query_self_group_goods());
		stat.setSelf_group_life(this.statTools.query_self_group_life());
		stat.setSelf_free_goods(this.statTools.query_self_free_goods());
		if (stats.size() > 0) {
			this.storeStatDAO.update(stat);
		} else
			this.storeStatDAO.save(stat);
		// 删除验证码信息
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -30);
		Map params = new HashMap();
		params.put("time", cal.getTime());
		List<Serializable> mvcs = this.mobileVerifyCodeDAO.query(
				"select obj.id from VerifyCode obj where obj.addTime<=:time",
				params, -1, -1);
		for (Serializable id : mvcs) {
			this.mobileVerifyCodeDAO.remove((Long) id);
		}

		// 统计店铺的评分信息
		List<GoodsClass> gcs = this.goodsClassDAO.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		for (GoodsClass gc : gcs) {
			double description_evaluate = 0;
			double service_evaluate = 0;
			double ship_evaluate = 0;
			params.clear();
			params.put("gc_id", gc.getId());
			params.put("store_status", 15);// 只查询正常营业且主营类目一致的店铺评分
			List<StorePoint> sp_list = this.storePointDAO
					.query("select obj from StorePoint obj where obj.store.gc_main_id=:gc_id and obj.store.store_status=:store_status",
							params, -1, -1);
			for (StorePoint sp : sp_list) {
				description_evaluate = CommUtil.add(description_evaluate,
						sp.getDescription_evaluate());
				service_evaluate = CommUtil.add(service_evaluate,
						sp.getService_evaluate());
				ship_evaluate = CommUtil.add(ship_evaluate,
						sp.getShip_evaluate());
			}
			gc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
					description_evaluate, sp_list.size())));
			gc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
					service_evaluate, sp_list.size())));
			gc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(ship_evaluate,
					sp_list.size())));
			this.goodsClassDAO.update(gc);
		}
		// 团购监控，团购添加时候可以控制到小时，每个半小时统计一次团购是否过期，是否开启
		List<Group> groups = this.groupDAO.query(
				"select obj from Group obj order by obj.addTime", null, -1, -1);
		for (Group group : groups) {
			if (group.getBeginTime().before(new Date())
					&& group.getEndTime().after(new Date())) {
				group.setStatus(0);
				this.groupDAO.update(group);
			}
			if (group.getEndTime().before(new Date())) {
				group.setStatus(-2);
				this.groupDAO.update(group);
				for (GroupGoods gg : group.getGg_list()) {
					gg.setGg_status(-2);
					this.groupGoodsDAO.update(gg);
					Goods goods = gg.getGg_goods();
					goods.setGroup_buy(0);
					goods.setGroup(null);
					goods.setGoods_current_price(goods.getStore_price());
					this.goodsDAO.update(goods);
				}
			}
		}
		// 商城活动监控，自动关闭过期的商城活动,同时恢复对应的商品状态、价格
		params.clear();
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> acts = this.activityDAO
				.query("select obj from Activity obj where obj.ac_end_time<=:ac_end_time and obj.ac_status=:ac_status",
						params, -1, -1);
		for (Activity act : acts) {
			act.setAc_status(0);
			this.activityDAO.update(act);
			for (ActivityGoods ac : act.getAgs()) {
				ac.setAg_status(-2);// 到期关闭
				this.activityGoodsDAO.update(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);// 商品无商城活动状态
				goods.setActivity_goods_id(null);
				this.goodsDAO.update(goods);
			}
		}
		// 检测给予短信、邮件提醒即将确认自动收货的订单信息
		int auto_order_notice = sc.getAuto_order_notice();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_notice);
		params.put("shipTime", cal.getTime());
		params.put("auto_confirm_email", true);
		params.put("auto_confirm_sms", true);
		List<OrderForm> notice_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and (obj.auto_confirm_email=:auto_confirm_email or obj.auto_confirm_sms=:auto_confirm_sms)",
						params, -1, -1);
		for (OrderForm of : notice_ofs) {
			if (!of.isAuto_confirm_email()) {// 订单为商家订单
				try {
					boolean email = this.send_email(of,
							"email_tobuyer_order_will_confirm_notify");
					if (email) {
						of.setAuto_confirm_email(true);
						this.orderFormDAO.update(of);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
			if (!of.isAuto_confirm_sms()) {
				User buyer = this.userDAO.get(CommUtil.null2Long(of
						.getUser_id()));
				boolean sms =false;
				try {
					 sms = this.send_sms(of, buyer.getMobile(),
							"sms_tobuyer_order_will_confirm_notify");
				} catch (Exception e) {
					// TODO: handle exception
				}				
				if (sms) {
					of.setAuto_confirm_sms(true);
					this.orderFormDAO.update(of);
				}
			}
		}
		// 检测默认自动收货的订单信息
		int auto_order_confirm = sc.getAuto_order_confirm();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_confirm);
		params.put("shipTime", cal.getTime());
		params.put("order_status", 30);
		List<OrderForm> confirm_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : confirm_ofs) {
			cal.setTime(of.getShipTime());
			cal.add(Calendar.DAY_OF_YEAR,
					auto_order_confirm + of.getOrder_confirm_delay());// 计算最初发货日期、系统默认收货时长、延长收货时间综合计算后，是否已经在自动收货时间段
			if (cal.getTime().before(new Date())) {
				of.setOrder_status(40);// 自动确认收货
				of.setConfirmTime(new Date());//20150908自动确认收货时间未添加
				// 增加购物积分
				int user_integral = (int) CommUtil.div(of.getTotalPrice(),
						sc.getConsumptionRatio());
				if (user_integral > sc.getEveryIndentLimit()) {
					user_integral = sc.getEveryIndentLimit();
				}
				User orderUser = this.userDAO.get(CommUtil.null2Long(of
						.getUser_id()));
				if (orderUser != null) {
					orderUser.setIntegral(CommUtil.null2Int(orderUser
							.getIntegral()) + user_integral);
					this.userDAO.update(orderUser);
					// 记录积分明细
					if (sc.isIntegral()) {
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("购物增加" + user_integral + "分");
						log.setIntegral(user_integral);
						log.setIntegral_user(orderUser);
						log.setType("order");
						this.integralLogDao.save(log);
					}
				}
				
				this.orderFormDAO.update(of);
				Store store = this.storeDAO.get(CommUtil.null2Long(of
						.getStore_id()));
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(orderUser);
				ofl.setOf(of);
				this.orderFormLogDAO.save(ofl);
				if (sc.isEmailEnable() && of.getOrder_form() == 0) {
					try {
						this.send_email(of,
								"email_toseller_order_receive_ok_notify");
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
				if (sc.isSmsEnbale() && of.getOrder_form() == 0) {
					try {
						this.send_sms(of, store.getUser().getMobile(),
								"sms_toseller_order_receive_ok_notify");
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if ("payafter".equals(of.getPayType())) {// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
					this.update_goods_inventory(of);// 更新商品库存
				}
				// 自动生成结算日志
				if (of.getOrder_form() == 0) {// 商家订单生成结算日志,这里查询的是所有订单信息，不需要区分主订单及从订单信息
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("订单到期自动收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(of.getId()));
					plog.setOrder_id(of.getOrder_id().toString());
					plog.setCommission_amount(of.getCommission_amount());// 该订单总佣金费用
					plog.setGoods_info(of.getGoods_info());
					plog.setOrder_total_price(of.getGoods_amount());// 该订单总商品金额
					plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(
							of.getGoods_amount(), of.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffLogDAO.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), store.getStore_sale_amount())));// 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(of.getCommission_amount(),
									store.getStore_commission_amount())));// 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));// 店铺本次结算总佣金
					this.storeDAO.update(store);
					// 增加系统总销售金额、总佣金
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil
							.add(of.getCommission_amount(),
									sc.getPayoff_all_commission())));
					this.sysConfigDAO.update(sc);
				}
			}
		}
		// 到达设定时间，系统自动关闭订单相互评价功能
		int auto_order_evaluate = sc.getAuto_order_evaluate();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_evaluate);
		params.put("auto_order_evaluate", cal.getTime());
		params.put("order_status_40", 40);
		List<OrderForm> confirm_evaluate_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.confirmTime<=:auto_order_evaluate and obj.order_status=:order_status_40 order by addTime asc",
						params, -1, -1);
		for (OrderForm order : confirm_evaluate_ofs) {
			order.setOrder_status(65);
			this.orderFormDAO.update(order);

			User user = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			if (user != null) {
				// 增加消费金额
				BigDecimal ugf = user.getUser_goods_fee() == null ? BigDecimal
						.valueOf(0) : user.getUser_goods_fee();
				user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(ugf,
						order.getTotalPrice())));
				this.userDAO.update(user);
			}
		}
		// 申请退货后到达设定时间，未能输入退货物流单号和物流公司
		int auto_order_return = sc.getAuto_order_return();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		params.put("return_shipTime", cal.getTime());
		params.put("order_status", 40);
		params.put("goods_info", "%" + "\"goods_return_status\":\"\"" + "%");
		List<OrderForm> confirm_return_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status and obj.goods_info like:goods_info",
						params, -1, -1);
		for (OrderForm order : confirm_return_ofs) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				m.put("goods_return_status", -1);
				gls.putAll(m);
				new_maps.add(m);
			}
			order.setGoods_info(Json.toJson(new_maps));
			this.orderFormDAO.update(order);
			Map rgl_params = new HashMap();
			rgl_params.put("goods_return_status", "-2");
			rgl_params.put("return_order_id", order.getId());
			List<ReturnGoodsLog> rgl = this.returnGoodsLogDAO
					.query("select obj from ReturnGoodsLog obj where obj.goods_return_status is not :goods_return_status and obj.return_order_id=:return_order_id",
							rgl_params, -1, -1);
			for (ReturnGoodsLog r : rgl) {
				r.setGoods_return_status("-2");
				this.returnGoodsLogDAO.update(r);
			}

		}
		// 统计所有商品的评分信息
		List<Goods> goods_list = this.goodsDAO.query(
				"select distinct obj.evaluate_goods from Evaluate obj ", null,
				-1, -1);
		for (Goods goods : goods_list) {
			// 统计所有商品的描述相符评分
			double description_evaluate = 0;
			params.clear();
			params.put("evaluate_goods_id", goods.getId());
			List<Evaluate> eva_list = this.evaluateDAO
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
							params, -1, -1);
			for (Evaluate eva : eva_list) {
				description_evaluate = CommUtil.add(
						eva.getDescription_evaluate(), description_evaluate);
			}
			description_evaluate = CommUtil.div(description_evaluate,
					eva_list.size());
			goods.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			if (eva_list.size() > 0) {// 商品有评价情况下
				// 统计所有商品的好评率
				double well_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", 1);
				List<Evaluate> well_list = this.evaluateDAO
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				well_evaluate = CommUtil.div(well_list.size(), eva_list.size());
				goods.setWell_evaluate(BigDecimal.valueOf(well_evaluate));
				// 统计所有商品的中评率
				double middle_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", 0);
				List<Evaluate> middle_list = this.evaluateDAO
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				middle_evaluate = CommUtil.div(middle_list.size(),
						eva_list.size());
				goods.setMiddle_evaluate(BigDecimal.valueOf(middle_evaluate));
				// 统计所有商品的差评率
				double bad_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", -1);
				List<Evaluate> bad_list = this.evaluateDAO
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				bad_evaluate = CommUtil.div(bad_list.size(), eva_list.size());
				goods.setBad_evaluate(BigDecimal.valueOf(bad_evaluate));
			}
			this.goodsDAO.update(goods);
		}
		// 处理定时发布商品
		params.clear();
		params.put("goods_status", 2);
		List<Goods> goods_list2 = this.goodsDAO
				.query("select obj from Goods obj where obj.goods_status=:goods_status ",
						params, -1, -1);
		for (Goods goods : goods_list2) {
			if(goods.getGoods_seller_time()!=null){
			if (goods.getGoods_seller_time().after(new Date())) {
				goods.setGoods_status(0);
				this.goodsDAO.update(goods);
				// 添加lucene索引
				String goods_lucene_path = System
						.getProperty("iskyshopb2b2c.root")
						+ File.separator
						+ "luence" + File.separator + "goods";
				LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.writeIndex(vo);
			}
			}
		}
		// 删除缓存的晒单图片
		params.clear();
		params.put("info", "eva_temp");
		List<Accessory> acc = this.accessoryDAO
				.query("select new Accessory(id) from Accessory obj where obj.info=:info",
						params, -1, -1);
		for (Accessory accessory : acc) {
			boolean ret = CommUtil.deleteFile(System
					.getProperty("iskyshop.root")
					+ File.separator
					+ accessory.getPath()
					+ File.separator
					+ accessory.getName());
			if (ret) {
				this.accessoryDAO.remove(accessory.getId());
			}
		}
		// 为每个商品收藏者发送降价通知，此过程请写于定时器最后。
		List<Favorite> favs = this.favoriteDAO.query(
				"select obj from Favorite obj where obj.type=0", null, -1, -1);
		BigDecimal bd = new BigDecimal(0.00);
		User fromUser = this.userDAO.getBy(null, "userName", "admin");
		for (Favorite fav : favs) {
			Goods goods = this.goodsDAO.get(fav.getGoods_id());
			if (goods != null && goods.getPrice_history() != null
					&& fav.getGoods_current_price() != null) {
				if (goods != null
						&& goods.getGoods_current_price().compareTo(
								fav.getGoods_current_price()) < 0) {
					String msg_content = "您收藏的商品" + goods.getGoods_name()
							+ "已降价，请注意查看";
					// 发送系统站内信
					User user = this.userDAO.get(fav.getUser_id());
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(fromUser);
					msg.setToUser(user);
					this.messageDAO.save(msg);
					fav.setGoods_current_price(goods.getGoods_current_price());
					this.favoriteDAO.update(fav);
				}
			}
		}
		params.clear();
		params.put("send_type", 1);
		params.put("status", 0);
		params.put("sendtime", new Date());
		List<AppPushLog> appPushLoglist = this.appPushLogService
				.query("select obj from AppPushLog obj where obj.send_type=:send_type and obj.status=:status and obj.sendtime<=:sendtime",
						params, -1, -1);
		for (AppPushLog appPushLog : appPushLoglist) {
			if (appPushLog.getDevice() == 0) {
				this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
				this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
			} else if (appPushLog.getDevice() == 1) {
				this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
			} else if (appPushLog.getDevice() == 2) {
				this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
			}
		}
	}

	private boolean send_email(OrderForm order, String mark) throws Exception {
		SysConfig sc = this.getSysConfig();
		Template template = this.templateDAO.getBy(null, "mark", mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			Store store = this.storeDAO.get(CommUtil.null2Long(order
					.getStore_id()));
			String email = store.getUser().getEmail();
			String subject = template.getTitle();
			User buyer = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			// System.out.println(writer.toString());
			boolean ret = false;
			try {
				ret = this.msgTools.sendEmail(email, subject, content);
			} catch (Exception e) {
				// TODO: handle exception
			} 
			return ret;
		} else
			return false;
	}

	private boolean send_sms(OrderForm order, String mobile, String mark)
			throws Exception {
		SysConfig sc = this.getSysConfig();
		Store store = this.storeDAO
				.get(CommUtil.null2Long(order.getStore_id()));
		Template template = this.templateDAO.getBy(null, "mark", mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			User buyer = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			boolean ret = false;
			try {
				 ret = this.msgTools.sendSMS(mobile, content);
			} catch (Exception e) {
				// TODO: handle exception
			}		
			return ret;
		} else
			return false;
	}

	/**
	 * 更新商品库存
	 * 
	 * @param order
	 */
	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil
				.null2String(order.getId()));
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(
					CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_count(gg.getGg_count() - goods_count);
						this.groupGoodsDAO.update(gg);
						// 更新lucene索引
						String goods_lucene_path = System
								.getProperty("iskyshopb2b2c.root")
								+ File.separator
								+ "luence"
								+ File.separator
								+ "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()),
								luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
			List<String> gsps = new ArrayList<String>();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
					.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
			String spectype = "";
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				if(gsp!=null){
				gsps.add(gsp.getId().toString());
				spectype += gsp.getSpec().getName() + ":" + gsp.getValue()
						+ " ";
				}
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods
					.getId());
			todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum()
					+ goods_count);
			Map<String, Integer> logordermap = (Map<String, Integer>) Json
					.fromJson(todayGoodsLog.getGoods_order_type());
			String ordertype = order.getOrder_type();
			if (logordermap.containsKey(ordertype)) {
				logordermap.put(ordertype, logordermap.get(ordertype)
						+ goods_count);
			} else {
				logordermap.put(ordertype, goods_count);
			}
			todayGoodsLog.setGoods_order_type(Json.toJson(logordermap,
					JsonFormat.compact()));

			Map<String, Integer> logspecmap = (Map<String, Integer>) Json
					.fromJson(todayGoodsLog.getGoods_sale_info());

			if (logspecmap.containsKey(spectype)) {
				logspecmap
						.put(spectype, logspecmap.get(spectype) + goods_count);
			} else {
				logspecmap.put(spectype, goods_count);
			}
			todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap,
					JsonFormat.compact()));

			this.goodsLogDAO.update(todayGoodsLog);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- goods_count);
			} else {
				List<HashMap> list = Json
						.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put("count", CommUtil.null2Int(temp.get("count"))
								- goods_count);
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(list,
						JsonFormat.compact()));
			}
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
				}
			}
			this.goodsDAO.update(goods);
			// 更新lucene索引
			String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()),
					luceneVoTools.updateGoodsIndex(goods));
		}

	}
}
