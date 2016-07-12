package com.iskyshop.module.app.view.tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushBroadcastMessageRequest;
import com.baidu.yun.channel.model.PushBroadcastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.app.domain.AppPushLog;
import com.iskyshop.module.app.domain.AppPushUser;
import com.iskyshop.module.app.service.IAppPushLogService;
import com.iskyshop.module.app.service.IAppPushUserService;
import com.iskyshop.module.chatting.domain.ChattingLog;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

/**
 * 
 * <p>
 * Title: AppPushTools.java
 * </p>
 * 
 * <p>
 * Description: 推送工具类，封装推送方法
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
 * @author lixiaoyang
 * 
 * @date 2015-2-12
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class AppPushTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IAppPushUserService appPushuserService;
	@Autowired
	private IAppPushLogService appPushLogService;
	@Autowired
	private IUserService userService;

	/**
	 * 向所有安卓并且已登录用户推送消息
	 */
	@Async
	public void android_push(AppPushLog appPushLog) {
		SysConfig config = this.configService.getSysConfig();
		if (config.getApiKey() != null && config.getSecretKey() != null) {
			String apiKey = config.getApiKey();
			String secretKey = config.getSecretKey();
			ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);
			BaiduChannelClient channelClient = new BaiduChannelClient(pair);
			channelClient.setChannelLogHandler(new YunLogHandler() {
				@Override
				public void onHandle(YunLogEvent event) {
					System.out.println(event.getMessage());
				}
			});

			try {
				PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
				request.setDeviceType(3);
				// 若要通知，
				request.setMessageType(1);
				Map json_map = new HashMap();
				json_map.put("title", appPushLog.getTitle());
				json_map.put("description", appPushLog.getDescription());
				// Map custom_content = new HashMap();
				// custom_content.put("type", "order_goods");
				// custom_content.put("order_id", "316");
				// json_map.put("custom_content", custom_content);
				request.setMessage(Json.toJson(json_map, JsonFormat.compact()));

				// 5. 调用pushMessage接口
				PushBroadcastMessageResponse response = channelClient
						.pushBroadcastMessage(request);

				// 6. 认证推送成功
				System.out.println("push amount : "
						+ response.getSuccessAmount());
				appPushLog.setStatus(1);
				this.appPushLogService.update(appPushLog);

			} catch (ChannelClientException e) {
				// 处理客户端错误异常
				e.printStackTrace();
			} catch (ChannelServerException e) {
				// 处理服务端错误异常
				System.out.println(String.format(
						"request_id: %d, error_code: %d, error_message: %s",
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
			}
		}
	}

	/**
	 * 向所有ios并且已登录用户推送消息
	 * 
	 * @param request
	 */
	@Async
	public void ios_push(AppPushLog appPushLog) {
		ApnsService service = APNS
				.newService()
				.withCert(
						System.getProperty("iskyshopb2b2c.root")
								+ File.separator + "resources" + File.separator
								+ "data" + File.separator + "iOSPushDev.p12",
						"123456").withSandboxDestination() // or
				// .withProductionDestination()
				.build();

		String payload = APNS.newPayload()
				.alertBody(appPushLog.getDescription()).sound("default")
				.build();

		Map params = new HashMap();
		params.put("app_type", "iOS");
		List device_list = this.appPushuserService
				.query("select obj.app_id from AppPushUser obj where obj.app_type=:app_type",
						params, -1, -1);
		service.push(device_list, payload);

		appPushLog.setStatus(1);
		this.appPushLogService.update(appPushLog);

	}

	/**
	 * 商家聊天信息推送
	 * 
	 * @param log
	 */
	public void app_chat_push(ChattingLog log) {

		Long store_id = log.getChatting().getConfig().getStore_id();
		Map params = new HashMap();
		params.put("store_id", store_id);
		List<User> list = this.userService.query(
				"select obj from User obj where obj.store.id=:store_id",
				params, -1, -1);
		User user = list.get(0);

		params.clear();
		params.put("user", user.getId().toString());
		List<AppPushUser> appPushUser_list = this.appPushuserService.query(
				"select obj from AppPushUser obj where obj.user_id=:user",
				params, -1, -1);
		if (appPushUser_list.size() > 0) {
			AppPushUser app_user = appPushUser_list.get(0);

		}

	}
}
