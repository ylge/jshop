package com.iskyshop.manage.buyer.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.service.IExpressInfoService;

/**
 * 
 * 
 * <p>
 * Ship
 * </p>
 * 
 * <p>
 * Description:物流查询工具类
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
 * @date 2014年5月23日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class ShipTools {
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;

	/**
	 * 查询指定订单的物流信息
	 * 
	 * @param id
	 * @return
	 */
	public TransInfo query_Ordership_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && !CommUtil.null2String(obj.getShipCode()).equals("")) {
			if (this.configService.getSysConfig().getKuaidi_type() == 0) {// 免费物流接口
				try {
					ExpressCompany ec = this.queryExpressCompany(obj
							.getExpress_info());
					String query_url = "http://api.kuaidi100.com/api?id="
							+ this.configService.getSysConfig().getKuaidi_id()
							+ "&com="
							+ (ec != null ? ec.getCompany_mark() : "") + "&nu="
							+ obj.getShipCode() + "&show=0&muti=1&order=asc";
					URL url = new URL(query_url);
					URLConnection con = url.openConnection();
					con.setAllowUserInteraction(false);
					InputStream urlStream = url.openStream();
					String type = con.guessContentTypeFromStream(urlStream);
					String charSet = null;
					if (type == null)
						type = con.getContentType();
					if (type == null || type.trim().length() == 0
							|| type.trim().indexOf("text/html") < 0)
						return info;
					if (type.indexOf("charset=") > 0)
						charSet = type.substring(type.indexOf("charset=") + 8);
					byte b[] = new byte[10000];
					int numRead = urlStream.read(b);
					String content = new String(b, 0, numRead, charSet);
					while (numRead != -1) {
						numRead = urlStream.read(b);
						if (numRead != -1) {
							// String newContent = new String(b, 0, numRead);
							String newContent = new String(b, 0, numRead,
									charSet);
							content += newContent;
						}
					}
					info = Json.fromJson(TransInfo.class, content);
					urlStream.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {// 收费物流接口
				ExpressInfo ei = this.expressInfoService
						.getObjByPropertyWithType("order_id", obj.getId(), 0);
				if (ei != null) {
					List<TransContent> data = (List<TransContent>) Json
							.fromJson(CommUtil.null2String(ei
									.getOrder_express_info()));
					info.setData(data);
					info.setStatus("1");
				}
			}
		}
		return info;
	}

	/**
	 * 查询退款日志的物流信息，
	 * 
	 * @param id
	 * @return
	 */
	public TransInfo query_Returnship_getData(String id) {
		TransInfo info = new TransInfo();
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(id));
		if (this.configService.getSysConfig().getKuaidi_type() == 0) {// 免费物流接口
			try {
				ExpressCompany ec = this.queryExpressCompany(obj
						.getReturn_express_info());
				String query_url = "http://api.kuaidi100.com/api?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com=" + (ec != null ? ec.getCompany_mark() : "")
						+ "&nu=" + obj.getExpress_code()
						+ "&show=0&muti=1&order=asc";
				URL url = new URL(query_url);
				URLConnection con = url.openConnection();
				con.setAllowUserInteraction(false);
				InputStream urlStream = url.openStream();
				String type = con.guessContentTypeFromStream(urlStream);
				String charSet = null;
				if (type == null)
					type = con.getContentType();
				if (type == null || type.trim().length() == 0
						|| type.trim().indexOf("text/html") < 0)
					return info;
				if (type.indexOf("charset=") > 0)
					charSet = type.substring(type.indexOf("charset=") + 8);
				byte b[] = new byte[10000];
				int numRead = urlStream.read(b);
				String content = new String(b, 0, numRead, charSet);
				while (numRead != -1) {
					numRead = urlStream.read(b);
					if (numRead != -1) {
						// String newContent = new String(b, 0, numRead);
						String newContent = new String(b, 0, numRead, charSet);
						content += newContent;
					}
				}
				info = Json.fromJson(TransInfo.class, content);
				urlStream.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.configService.getSysConfig().getKuaidi_type() == 1) {// 收费物流接口
			ExpressInfo ei = this.expressInfoService.getObjByPropertyWithType(
					"order_id", obj.getId(), 1);
			if (ei != null) {
				List<TransContent> data = (List<TransContent>) Json.fromJson(ei
						.getOrder_express_info());
				info.setData(data);
				info.setStatus("1");
			}
		}
		return info;
	}

	private ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (json != null && !json.equals("")) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map
					.get("express_company_id")));
		}
		return ec;
	}
}
