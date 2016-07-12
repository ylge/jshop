package com.iskyshop.module.weixin.view.tools;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.service.IStoreService;


/**
 * 
 * <p>
 * Title: WeixinTools.java
 * </p>
 * 
 * <p>
 * Description:解析微信xml工具类
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
 * @date 2014-12-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class WeixinTools {
	@Autowired
	private IStoreService storeService;

	public Map<String, String> parse_xml(String xml) {
		Map map = new HashMap();
		if (!CommUtil.null2String(xml).equals("")) {
			Document doc;
			try {
				doc = DocumentHelper.parseText(xml);
				String ToUserName = doc.selectSingleNode("xml/ToUserName") != null ? doc
						.selectSingleNode("xml/ToUserName").getText() : "";
				String FromUserName = doc.selectSingleNode("xml/FromUserName") != null ? doc
						.selectSingleNode("xml/FromUserName").getText() : "";
				String CreateTime = doc.selectSingleNode("xml/CreateTime") != null ? doc
						.selectSingleNode("xml/CreateTime").getText() : "";
				String MsgType = doc.selectSingleNode("xml/MsgType") != null ? doc
						.selectSingleNode("xml/MsgType").getText() : "";
				String Content = doc.selectSingleNode("xml/Content") != null ? doc
						.selectSingleNode("xml/Content").getText() : "";
				String MsgId = doc.selectSingleNode("xml/MsgId") != null ? doc
						.selectSingleNode("xml/MsgId").getText() : "";
				String Event = doc.selectSingleNode("xml/Event") != null ? doc
						.selectSingleNode("xml/Event").getText() : "";
				String EventKey = doc.selectSingleNode("xml/EventKey") != null ? doc
						.selectSingleNode("xml/EventKey").getText() : "";
				String Latitude = doc.selectSingleNode("xml/Latitude") != null ? doc
						.selectSingleNode("xml/Latitude").getText() : "";
				String Longitude = doc.selectSingleNode("xml/Longitude") != null ? doc
						.selectSingleNode("xml/Longitude").getText() : "";
				String Location_X = doc.selectSingleNode("xml/Location_X") != null ? doc
						.selectSingleNode("xml/Location_X").getText() : "";
				String Location_Y = doc.selectSingleNode("xml/Location_Y") != null ? doc
						.selectSingleNode("xml/Location_Y").getText() : "";
			    String scene_id = doc.selectSingleNode("xml/scene_id")!=null ? doc
			    		.selectSingleNode("xml/scene_id").getText():"";
			    		String user_id = doc.selectSingleNode("xml/user_id")!=null ? doc
					    		.selectSingleNode("xml/user_id").getText():"";
						
				map.put("ToUserName", ToUserName);
				map.put("FromUserName", FromUserName);
				map.put("CreateTime", CreateTime);
				map.put("MsgType", MsgType);
				map.put("Content", Content);
				map.put("MsgId", MsgId);
				map.put("Event", Event);
				map.put("Latitude", Latitude);
				map.put("Longitude", Longitude);
				map.put("Location_X", Location_X);
				map.put("Location_Y", Location_Y);
				map.put("EventKey", EventKey);
				map.put("scene_id", scene_id);
				map.put("user_id", user_id);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}

	public String reply_xml(String reply_type, Map<String, String> map,
			String content, HttpServletRequest request) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");// 创建根节点
		if (reply_type == null) {
			reply_type = "";
		}
		if (reply_type.equals("text") || reply_type.equals("event")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			if ("findany".equals(map.get("EventKey"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
			if ("@乐呐喊".equals(map.get("Content"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
			if ("打印".equals(map.get("Content"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
		}
		return doc
				.asXML()
				.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
						"").trim();
	}

}
