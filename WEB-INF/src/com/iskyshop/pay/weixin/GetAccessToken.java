package com.iskyshop.pay.weixin;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.service.IPaymentService;

/**
 * 
 * <p>
 * Title: GetAccessToken.java
 * </p>
 * 
 * <p>
 * Description:微信支付处理接口，用来处理微信token
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
 * @author jxz
 * 
 * @date 2014-12-29
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class GetAccessToken {
	@Autowired
	private IPaymentService paymentService;
	private static GetAccessToken getAccessToken = new GetAccessToken();//创建单例
	private static String accessToken="";//存放token
	private static Date date =  null;//超时时间
	
	
	//获取单例
	public static GetAccessToken instance() {
		return getAccessToken;
	}

	// 获取accessToken
	public String getToken() {
		Map params = new HashMap();
		params.put("mark", "wx_app");
		List<Payment> objs = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		if (objs.size() > 0) {
			if (this.date != null && date.after(new Date())) {
				return this.accessToken;
			} else {
				String appid = objs.get(0).getWx_appid();
				String appSecret = objs.get(0).getWx_appSecret();
				String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
						+ appid + "&secret=" + appSecret;
				try {
					URL urlGet = new URL(url);
					HttpURLConnection http = (HttpURLConnection) urlGet
							.openConnection();
					http.setRequestMethod("GET"); // 必须是get方式请求
					http.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					http.setDoOutput(true);
					http.setDoInput(true);
					System.setProperty("sun.net.client.defaultConnectTimeout",
							"30000");// 连接超时30秒
					System.setProperty("sun.net.client.defaultReadTimeout",
							"30000"); // 读取超时30秒
					System.setProperty("jsse.enableSNIExtension", "false");
					http.connect();
					InputStream is = http.getInputStream();
					int size = is.available();
					byte[] jsonBytes = new byte[size];
					is.read(jsonBytes);
					String message = new String(jsonBytes, "UTF-8");
					Map map = Json.fromJson(HashMap.class, message);
					String token = CommUtil
							.null2String(map.get("access_token"));
					is.close();
					if (token != null && !token.equals("")) {
						this.accessToken = token;
						// 设置20分钟后重新获取token
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 20);
						this.date = cal.getTime();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return this.accessToken;
			}
		} else {
			return "error";
		}
	}
}
