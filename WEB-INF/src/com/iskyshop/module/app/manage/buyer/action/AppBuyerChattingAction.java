package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;
import com.iskyshop.module.chatting.domain.Chatting;
import com.iskyshop.module.chatting.domain.ChattingConfig;
import com.iskyshop.module.chatting.domain.ChattingLog;
import com.iskyshop.module.chatting.service.IChattingConfigService;
import com.iskyshop.module.chatting.service.IChattingLogService;
import com.iskyshop.module.chatting.service.IChattingService;

/**
 * 
 * <p>
 * Title: MobileClassViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端买家聊天控制器
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
 * @author hezeng
 * 
 * @date 2014-7-25
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppBuyerChattingAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IChattingService chattingService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private HtmlFilterTools htmlFileTools;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private IChattingConfigService chattingconfigService;

	/**
	 * 手机客户端买家聊天发送请求
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 *            ：发送消息的人
	 * @param token
	 *            ：用户验证信息
	 * @param to_user_id
	 *            ,接收消息的人
	 * @param chatting_type
	 *            ：聊天类型，0为买家和商家聊天，1为买家和平台自营聊天
	 */
	@RequestMapping("/app/buyer/chatting_logs_send.htm")
	public void chatting_logs_send(HttpServletRequest request,
			HttpServletResponse response, String user_id,
			String chatting_content, String goods_id) {
		Map json_map = new HashMap();
		List<Chatting> chattings = null;
		Chatting chatting = new Chatting();
		ChattingConfig config = new ChattingConfig();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		// 生成聊天组件配置信息
		if (goods.getGoods_type() == 1) {// 和商家客服对话
			map.clear();
			map.put("store_id", goods.getGoods_store().getId());
			List<ChattingConfig> config_list = this.chattingconfigService
					.query("select obj from ChattingConfig obj where obj.store_id=:store_id ",
							map, 0, 1);
			if (config_list.size() == 0) {
				config.setAddTime(new Date());
				config.setConfig_type(0);
				config.setStore_id(goods.getGoods_store().getId());
				config.setKf_name(goods.getGoods_store().getStore_name()
						+ "在线客服");
				this.chattingconfigService.save(config);
			} else {
				config = config_list.get(0);
			}
			map.clear();
			map.put("uid", user.getId());
			map.put("store_id", goods.getGoods_store().getId());
			chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.store_id=:store_id order by addTime asc",
							map, 0, 1);
			if (chattings.size() == 0) {
				chatting.setAddTime(new Date());
				chatting.setUser_id(user.getId());
				chatting.setConfig(config);
				chatting.setUser_name(user.getUserName());
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.save(chatting);
			} else {
				chatting = chattings.get(0);
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.update(chatting);
			}
		} else {// 和平台客服对话
			map.clear();
			map.put("config_type", 1);// 平台
			List<ChattingConfig> config_list = this.chattingconfigService
					.query("select obj from ChattingConfig obj where obj.config_type=:config_type ",
							map, 0, 1);
			if (config_list.size() == 0) {
				config.setAddTime(new Date());
				config.setConfig_type(1);// 平台聊天
				config.setKf_name("平台在线客服");
				this.chattingconfigService.save(config);
			} else {
				config = config_list.get(0);
			}
			map.clear();
			map.put("uid", user.getId());
			map.put("config_type", 1);
			chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.config_type=:config_type order by addTime asc",
							map, 0, 1);
			if (chattings.size() == 0) {
				chatting.setAddTime(new Date());
				chatting.setUser_id(user.getId());
				chatting.setConfig(config);
				chatting.setUser_name(user.getUserName());
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.save(chatting);
			} else {
				chatting = chattings.get(0);
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.update(chatting);
			}
		}
		chatting.setChatting_display(0);// 显示
		this.chattingService.update(chatting);
		ChattingLog log = new ChattingLog();
		log.setAddTime(new Date());
		log.setContent(chatting_content);
		log.setChatting(chatting);
		log.setUser_id(user.getId());
		log.setUser_read(1);// 自己发布的消息设置为自己已读
		this.chattinglogService.save(log);
		List<ChattingLog> cls = new ArrayList<ChattingLog>();
		cls.add(log);
		// 客服自动回复
		if (chatting.getConfig().getQuick_reply_open() == 1) {
			ChattingLog log2 = new ChattingLog();
			log2.setAddTime(new Date());
			log2.setChatting(chatting);
			log2.setContent(chatting.getConfig().getQuick_reply_content()
					+ "[自动回复]");
			log2.setFont(chatting.getConfig().getFont());
			log2.setFont_size(chatting.getConfig().getFont_size());
			log2.setFont_colour(chatting.getConfig().getFont_colour());
			log2.setStore_id(chatting.getConfig().getStore_id());// 当与平台对话时，该值为null
			this.chattinglogService.save(log2);
		}
		List<Map> datas = new ArrayList<Map>();
		for (ChattingLog obj : cls) {
			Map data = new HashMap();
			data.put("content", obj.getContent());
			data.put("time", CommUtil.formatLongDate(obj.getAddTime()));
			datas.add(data);
		}
		json_map.put("datas", datas);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/app/buyer/chatting_logs_refresh.htm")
	public void chatting_logs_refresh(HttpServletRequest request,
			HttpServletResponse response, String user_id, String goods_id) {
		Map json_map = new HashMap();
		ChattingConfig config = new ChattingConfig();
		Chatting chatting = new Chatting();
		List<Chatting> chattings = null;
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map map = new HashMap();
		// 生成聊天组件配置信息
		if (goods.getGoods_type() == 1) {// 和商家客服对话
			map.clear();
			map.put("store_id", goods.getGoods_store().getId());
			List<ChattingConfig> config_list = this.chattingconfigService
					.query("select obj from ChattingConfig obj where obj.store_id=:store_id ",
							map, 0, 1);
			if (config_list.size() == 0) {
				config.setAddTime(new Date());
				config.setConfig_type(0);
				config.setStore_id(goods.getGoods_store().getId());
				config.setKf_name(goods.getGoods_store().getStore_name()
						+ "在线客服");
				this.chattingconfigService.save(config);
			} else {
				config = config_list.get(0);
			}
			map.clear();
			map.put("uid", user.getId());
			map.put("store_id", goods.getGoods_store().getId());
			chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.store_id=:store_id order by addTime asc",
							map, 0, 1);
			if (chattings.size() == 0) {
				chatting.setAddTime(new Date());
				chatting.setUser_id(user.getId());
				chatting.setConfig(config);
				chatting.setUser_name(user.getUserName());
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.save(chatting);
			} else {
				chatting = chattings.get(0);
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.update(chatting);
			}
		} else {// 和平台客服对话
			map.clear();
			map.put("config_type", 1);// 平台
			List<ChattingConfig> config_list = this.chattingconfigService
					.query("select obj from ChattingConfig obj where obj.config_type=:config_type ",
							map, 0, 1);
			if (config_list.size() == 0) {
				config.setAddTime(new Date());
				config.setConfig_type(1);// 平台聊天
				config.setKf_name("平台在线客服");
				this.chattingconfigService.save(config);
			} else {
				config = config_list.get(0);
			}
			map.clear();
			map.put("uid", user.getId());
			map.put("config_type", 1);
			chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.config_type=:config_type order by addTime asc",
							map, 0, 1);
			if (chattings.size() == 0) {
				chatting.setAddTime(new Date());
				chatting.setUser_id(user.getId());
				chatting.setConfig(config);
				chatting.setUser_name(user.getUserName());
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.save(chatting);
			} else {
				chatting = chattings.get(0);
				chatting.setGoods_id(CommUtil.null2Long(goods_id));
				this.chattingService.update(chatting);
			}
		}
		map.clear();
		map.put("chatting_id", chatting.getId());
		map.put("user_read", 0);// user_read:用户没有读过的信息
		List<ChattingLog> logs = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.user_read=:user_read order by addTime asc",
						map, -1, -1);
		for (ChattingLog log : logs) {
			log.setUser_read(1);// 标记为用户已读
			this.chattinglogService.update(log);
		}
		List<Map> datas = new ArrayList<Map>();
		for (ChattingLog obj : logs) {
			Map data = new HashMap();
			data.put(
					"content",
					this.pattern_emotion(CommUtil.getURL(request),
							obj.getContent()));
			data.put("time", CommUtil.formatLongDate(obj.getAddTime()));
			datas.add(data);
		}
		json_map.put("datas", datas);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 图片标签正则
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	// 图片src路径的正则
	private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";
	// 表情数组
	private static final String EMOTIONS[] = { "", "[微笑]", "[色]", "[发呆]",
			"[得意]", "[流泪]", "[害羞]", "[闭嘴]", "[睡觉]", "[大哭]", "[尴尬]", "[发怒]",
			"[调皮]", "[呲牙]", "[惊讶]", "[难过]", "[酷]", "[冷汗]", "[抓狂]", "[呕吐]",
			"[偷笑]", "[可爱]", "[白眼]", "[撇嘴]", "[饿了]", "[困了]", "[惊恐]", "[流汗]",
			"[憨笑]", "[大兵]", "[奋斗]", "[咒骂]", "[疑问]", "[嘘]", "[晕]", "[折磨]",
			"[衰]", "[骷髅]", "[敲打]", "[再见]", "[擦汗]", "[抠鼻]", "[鼓掌]", "[糗大了]",
			"[坏笑]", "[左哼哼]", "[右哼哼]", "[哈欠]", "[鄙视]", "[委屈]", "[快哭了]", "[阴险]",
			"[亲亲]", "[吓]", "[可怜]", "[菜刀]", "[西瓜]", "[啤酒]", "[篮球]", "[乒乓球]",
			"[咖啡]", "[米饭]", "[猪头]", "[玫瑰]", "[凋谢]", "[示爱]", "[爱心]", "[心碎]",
			"[蛋糕]", "[闪电]", "[炸弹]", "[刀]", "[足球]", "[瓢虫]", "[便便]", "[月亮]",
			"[太阳]", "[礼物]", "[拥抱]", "[强]", "[弱]", "[握手]", "[胜利]", "[抱拳]",
			"[勾引]", "[拳头]", "[差劲]", "[爱你]", "[NO]", "[OK]", "[爱情]", "[飞吻]",
			"[跳跳]", "[发抖]", "[窝火]", "[转圈]", "[磕头]", "[回头]", "[跳绳]" };

	/**
	 * 将电脑端网页图片路径替换为表情汉字，并将网页代码过滤
	 * 
	 * @param web_url
	 * @param HTML
	 * @return
	 */
	private String pattern_emotion(String web_url, String HTML) {
		List<String> img_urls = this.getImageUrl(HTML);
		for (String image : img_urls) {
			Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
			while (matcher.find()) {
				String temp_src = matcher.group().substring(0,
						matcher.group().length() - 1);
				String img_srcs1[] = temp_src
						.split("/resources/style/im/images/emo/");
				if (img_srcs1.length == 2) {
					String img_srcs2[] = img_srcs1[1].split(".gif");
					String emo_str = EMOTIONS[CommUtil.null2Int(img_srcs2[0])];
					HTML = HTML.replaceAll(image, emo_str);
				} else {
					String temp_img = "<img src='" + this.getImageSrc(image)
							+ "'></img>";
					HTML = HTML.replace(image, temp_img);
				}
			}
		}
		return HTML;
	}

	/***
	 * 获取ImageUrl地址
	 * 
	 * @param HTML
	 * @return
	 */
	private List<String> getImageUrl(String HTML) {
		Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
		List<String> listImgUrl = new ArrayList<String>();
		while (matcher.find()) {
			listImgUrl.add(matcher.group());
		}
		return listImgUrl;
	}

	/***
	 * 获取ImageSrc地址
	 * 
	 * @param listImageUrl
	 * @return
	 */
	private String getImageSrc(String ImageUrl) {
		String src = "";
		Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(ImageUrl);
		while (matcher.find()) {
			src = matcher.group().substring(0, matcher.group().length() - 1);
		}
		src = src.replace("'", "");
		return src;
	}
}
