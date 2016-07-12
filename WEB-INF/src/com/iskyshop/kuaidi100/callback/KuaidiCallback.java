package com.iskyshop.kuaidi100.callback;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.NoticeRequest;
import com.iskyshop.kuaidi100.pojo.NoticeResponse;
import com.iskyshop.kuaidi100.pojo.Result;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;

/**
 * 
 * <p>
 * Title: KuaidiCallback.java
 * </p>
 * 
 * <p>
 * Description: 快递100收费接口主动推送快递信息到系统，系统接收保存
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
 * @date 2014-11-4
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class KuaidiCallback {
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IExpressInfoService expressInfoService;

	@RequestMapping("/kuaidi100_callback.htm")
	public void kuaidi100_callback(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		NoticeResponse resp = new NoticeResponse();
		resp.setResult(false);
		resp.setReturnCode("500");
		resp.setMessage("成功");
		try {
			String param = request.getParameter("param");
			NoticeRequest nReq = JacksonHelper.fromJSON(param,
					NoticeRequest.class);
			Result result = nReq.getLastResult();
			// 处理快递结果
			Long order_id = CommUtil
					.null2Long(request.getParameter("order_id"));
			int order_type = CommUtil.null2Int(request
					.getParameter("order_type"));
			ExpressInfo obj = this.expressInfoService.getObjByPropertyWithType(
					"order_id", order_id, order_type);
			// System.out.println(param);
			String sign = CommUtil.null2String(request.getParameter("sign"));// 返回的签名值
			String salt = Md5Encrypt.md5(CommUtil.null2String(order_id))
					.toLowerCase();
			String sign1 = Md5Encrypt.md5(param + salt).toUpperCase();// 根据快递100的规则计算的签名值，2者一致才认为是快递100返回数据，避免恶意数据攻击
			// System.out.println("返回签名为：" + sign + ",计算的签名为：" + sign1);
			if (sign.equals(sign1)) {
				if (obj == null) {
					obj = new ExpressInfo();
					obj.setAddTime(new Date());
					obj.setOrder_id(order_id);
					obj.setOrder_express_id(result.getNu());
					obj.setOrder_type(order_type);
					obj.setOrder_status(1);
					// System.out.println(Json.toJson(result.getData(),JsonFormat.compact()));
					obj.setOrder_express_info(Json.toJson(result.getData(),
							JsonFormat.compact()));
					this.expressInfoService.save(obj);
				} else {
					obj.setOrder_id(order_id);
					obj.setOrder_express_id(result.getNu());
					obj.setOrder_status(1);
					// System.out.println(Json.toJson(result.getData(),JsonFormat.compact()));
					obj.setOrder_express_info(Json.toJson(result.getData(),
							JsonFormat.compact()));
					obj.setOrder_type(order_type);
					this.expressInfoService.update(obj);
				}
				resp.setResult(true);
				resp.setReturnCode("200");
				response.getWriter().print(JacksonHelper.toJSON(resp)); // 这里必须返回，否则认为失败，过30分钟又会重复推送。
			}
		} catch (Exception e) {
			resp.setMessage("保存失败" + e.getMessage());// 保存失败，服务端等30分钟会重复推送。
			response.getWriter().print(JacksonHelper.toJSON(resp));// 保存失败，服务端等30分钟会重复推送。
		}

	}

}
