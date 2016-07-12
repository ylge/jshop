package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.nutz.json.Json;
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
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: OrderManageAction.java
 * </p>
 * 
 * <p>
 * Description:商城后台订单管理器
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
 * @date 2014-5-21
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class OrderManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IExpressCompanyService ecService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;

	private static final BigDecimal WHETHER_ENOUGH = new BigDecimal(0.00);
	private static final Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>() {
		{
			put(0, "已取消");
			put(10, "待付款");
			put(15, "线下支付待审核");
			put(16, "货到付款待发货");
			put(20, "已付款");
			put(30, "已发货");
			put(40, "已收货");
			put(50, "已完成");
			put(60, "已结束");
		}
	};

	private static final Map<String, String> PAYMENT_MAP = new HashMap<String, String>() {
		{
			put(null, "未支付");
			put("", "未支付");
			put("alipay", "支付宝");
			put("alipay_wap", "手机网页支付宝");
			put("alipay_app", "手机支付宝APP");
			put("tenpay", "财付通");
			put("bill", "快钱");
			put("chinabank", "网银在线");
			put("outline", "线下支付");
			put("balance", "预存款支付");
			put("payafter", "货到付款");
			put("paypal", "paypal");
		}
	};

	private static final Map<String, String> TYPE_MAP = new HashMap<String, String>() {
		{
			put(null, "PC订单");
			put("web", "PC订单");
			put("weixin", "微信订单");
			put("android", "Android订单");
			put("ios", "IOS订单");
		}
	};

	@SecurityMapping(title = "订单设置", value = "/admin/set_order_confirm.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping("/admin/set_order_confirm.htm")
	public ModelAndView set_order_confirm(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_order_confirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "订单设置保存", value = "/admin/set_order_confirm_save.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping("/admin/set_order_confirm_save.htm")
	public ModelAndView set_order_confirm_save(HttpServletRequest request,
			HttpServletResponse response, String id, String auto_order_confirm,
			String auto_order_notice, String auto_order_return,
			String auto_order_evaluate, String grouplife_order_return,
			String evaluate_edit_deadline, String evaluate_add_deadline) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig config = null;
		if (id.equals("")) {
			config = wf.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) wf.toPo(request, obj);
		}
		config.setAuto_order_confirm(CommUtil.null2Int(auto_order_confirm));
		config.setAuto_order_notice(CommUtil.null2Int(auto_order_notice));
		config.setAuto_order_return(CommUtil.null2Int(auto_order_return));
		config.setAuto_order_evaluate(CommUtil.null2Int(auto_order_evaluate));
		config.setGrouplife_order_return(CommUtil
				.null2Int(grouplife_order_return));
		config.setEvaluate_edit_deadline(CommUtil
				.null2Int(evaluate_edit_deadline));
		config.setEvaluate_add_deadline(CommUtil
				.null2Int(evaluate_add_deadline));
		if (id.equals("")) {
			this.configService.save(config);
		} else {
			this.configService.update(config);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "订单设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_order_confirm.htm");
		return mv;
	}

	@SecurityMapping(title = "订单列表", value = "/admin/order_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_list.htm")
	public ModelAndView order_list(HttpServletRequest request,
			HttpServletResponse response, String order_status, String type,
			String type_data, String payment, String beginTime, String endTime,
			String begin_price, String end_price, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/order_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 0), "=");// 这里只查询商品订单，手机充值订单独立出来
		if (!CommUtil.null2String(order_status).equals("")) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status",
					CommUtil.null2Int(order_status)), "=");
		}
		if (!CommUtil.null2String(type_data).equals("")) {
			if (type.equals("store")) {
				ofqo.addQuery("obj.store_name", new SysMap("store_name",
						type_data), "=");
			}
			if (type.equals("buyer")) {
				ofqo.addQuery("obj.user_name",
						new SysMap("userName", type_data), "=");
			}
			if (type.equals("order")) {
				ofqo.addQuery("obj.order_id",
						new SysMap("order_id", type_data), "=");
			}
		}
		if (CommUtil.null2String(payment).equals("alipay")) {
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", "alipay_app"),
					"=", "or");
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", "alipay_wap"),
					"=", "or");
		} else if (CommUtil.null2String(payment).equals("apyafter")) {
			ofqo.addQuery("obj.payType", new SysMap("mark", "payafter"), "=");
		} else if (CommUtil.null2String(payment).equals("wx_app")) {
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", "wx_app"), "=");
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", "wx_pay"),
					"=", "or");
		} else if (!CommUtil.null2String(payment).equals("")) {
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!CommUtil.null2String(begin_price).equals("")) {
			ofqo.addQuery("obj.totalPrice", new SysMap("begin_price",
					BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!CommUtil.null2String(end_price).equals("")) {
			ofqo.addQuery(
					"obj.totalPrice",
					new SysMap("end_price", BigDecimal.valueOf(CommUtil
							.null2Double(end_price))), "<=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		mv.addObject("type_data", type_data);
		mv.addObject("payment", payment);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		return mv;
	}

	@SecurityMapping(title = "手机充值订单列表", value = "/admin/order_recharge.htm*", rtype = "admin", rname = "充值列表", rcode = "ofcard_list", rgroup = "交易")
	@RequestMapping("/admin/order_recharge.htm")
	public ModelAndView order_recharge(HttpServletRequest request,
			HttpServletResponse response, String order_status,
			String beginTime, String endTime, String begin_price,
			String end_price, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/order_recharge.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 1), "=");// 这里只查手机充值订单
		if (!CommUtil.null2String(order_status).equals("")) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status",
					CommUtil.null2Int(order_status)), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!CommUtil.null2String(begin_price).equals("")) {
			ofqo.addQuery("obj.totalPrice", new SysMap("begin_price",
					BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!CommUtil.null2String(end_price).equals("")) {
			ofqo.addQuery(
					"obj.totalPrice",
					new SysMap("end_price", BigDecimal.valueOf(CommUtil
							.null2Double(end_price))), "<=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		return mv;
	}

	@SecurityMapping(title = "订单详情", value = "/admin/order_view.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id, String view_type) {
		ModelAndView mv = new JModelAndView("admin/blue/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_cat() == 1) {
			mv = new JModelAndView("admin/blue/order_recharge_view.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		} else {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getStore_id()));
			mv.addObject("store", store);
			mv.addObject("obj", obj);
		}
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(
				obj.getExpress_info(), "express_company_name"));
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		return mv;
	}

	@SecurityMapping(title = "订单导出excel", value = "/admin/order_manage_excel.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_manage_excel.htm")
	public void order_manage_excel(HttpServletRequest request,
			HttpServletResponse response, String order_status, String order_id,
			String beginTime, String endTime, String type, String type_date) {
		String buyer_userName = "";
		String store_name = "";
		if (type.equals("buyer")) {
			buyer_userName = type_date;
		}
		if (type.equals("store")) {
			store_name = type_date;
		}
		if (type.equals("order")) {
			order_id = type_date;
		}
		OrderFormQueryObject qo = new OrderFormQueryObject();
		qo.setPageSize(1000000000);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				qo.addQuery(
						"(obj.order_status=:order_status1 or obj.order_status=:order_status2)",
						map);
			}
			if (order_status.equals("order_pay")) {// 已经付款
				qo.addQuery("obj.order_status",
						new SysMap("order_status1", 16), ">=");
				qo.addQuery("obj.order_status",
						new SysMap("order_status2", 20), "<=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				qo.addQuery("obj.order_status", new SysMap("order_status", 30),
						"=");
			}
			if (order_status.equals("order_evaluate")) {// 等待评价
				qo.addQuery("obj.order_status", new SysMap("order_status", 40),
						"=");
			}
			if (order_status.equals("order_finish")) {// 已经完成
				qo.addQuery("obj.order_status", new SysMap("order_status", 50),
						"=");
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				qo.addQuery("obj.order_status", new SysMap("order_status", 0),
						"=");
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			qo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			qo.addQuery("obj.user_name",
					new SysMap("user_name", buyer_userName), "=");
		}
		if (!CommUtil.null2String(store_name).equals("")) {
			qo.addQuery("obj.store_name", new SysMap("store_name", store_name),
					"=");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		qo.setOrderType("desc");
		IPageList pList = this.orderFormService.list(qo);
		if (pList.getResult() != null) {
			List<OrderForm> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet("订单列表");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 12000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 8000);
			// 创建字体样式
			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor(HSSFColor.BLUE.index);
			// 创建单元格样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置边框
			style.setBottomBorderColor(HSSFColor.RED.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setFont(font);// 设置字体
			// 创建Excel的sheet的一行
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500);// 设定行的高度
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "订单列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1)
					+ " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+ title + "（" + time + "）");
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true);// 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("订单号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("订单类型");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("商品");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("物流单号");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品总价");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("发货时间");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("活动信息");
			double all_order_price = 0.00;// 订单总金额
			double all_total_amount = 0.00;// 商品总金额
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);
				// 设置单元格的样式格式
				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getOrder_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2)
						.getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				if (datas.get(j - 2).getPayment() != null) {
					cell.setCellValue(PAYMENT_MAP.get(datas.get(j - 2)
							.getPayment().getMark()));
				} else {
					cell.setCellValue("未支付");
				}

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(TYPE_MAP
						.get(datas.get(j - 2).getOrder_type()));
				List<Map> goods_json = new ArrayList<Map>();
				if (datas.size() >= j - 2 && datas.get(j - 2) != null) {
					goods_json = Json.fromJson(List.class, CommUtil
							.null2String(datas.get(j - 2).getGoods_info()));
				}
				StringBuilder sb = new StringBuilder();
				boolean whether_combin = false;
				if (goods_json != null) {
					for (Map map : goods_json) {
						sb.append(map.get("goods_name") + "*"
								+ map.get("goods_count") + ",");
						if (map.get("goods_type") != null
								&& !"".equals(map.get("goods_type"))) {
							if (map.get("goods_type").toString()
									.equals("combin")) {
								whether_combin = true;
							}
						}
					}
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(sb.toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getShipCode());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getShip_price().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getGoods_amount().toString());
				all_total_amount = CommUtil.add(all_total_amount,
						datas.get(j - 2).getGoods_amount());// 计算商品总价

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2)
						.getTotalPrice()));
				all_order_price = CommUtil.add(all_order_price, datas
						.get(j - 2).getTotalPrice());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(STATUS_MAP.get(datas.get(j - 2)
						.getOrder_status()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2)
						.getShipTime()));

				if (datas.get(j - 2).getWhether_gift() == 1) {
					List<Map> gifts_json = Json.fromJson(List.class,
							datas.get(j - 2).getGift_infos());
					StringBuilder gsb = new StringBuilder();
					for (Map map : gifts_json) {
						gsb.append(map.get("goods_name") + ",");
					}
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue(gsb.toString());
				}
				if (datas.get(j - 2).getEnough_reduce_amount() != null
						&& datas.get(j - 2).getEnough_reduce_amount()
								.compareTo(WHETHER_ENOUGH) == 1) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("满减");
				}
				if (whether_combin) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("组合销售");
				}

			}
			// 设置底部统计信息
			int m = datas.size() + 2;
			row = sheet.createRow(m);
			// 设置单元格的样式格式
			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次订单金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次商品总金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_total_amount);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
