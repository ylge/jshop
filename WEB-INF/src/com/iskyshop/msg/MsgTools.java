package com.iskyshop.msg;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.PopupAuthenticator;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MsgTools.java<／p>
 * 
 * <p>
 * Description: 系统手机短信、邮件发送工具类，手机短信发送需要运营商购买短信平台提供的相关接口信息，邮件发送需要正确配置邮件服务器，
 * 运营商管理后台均有相关配置及发送测试（erikzhang） <／p>
 * <p>
 * 发送短信邮件工具类 参数json数据 buyer_id:如果有买家，则买家user.id seller_id:如果有卖家,卖家的user.id
 * sender_id:发送者的user.id receiver_id:接收者的user.id order_id:如果有订单 订单order.id
 * childorder_id：如果有子订单id goods_id:商品的id self_goods: 如果是自营商品 则在邮件或者短信显示 平台名称
 * SysConfig.title,（jinxinzhe）
 * 
 * 其中收费工具类只作为商家和用户在交易中的发送工具类，发送的短信邮件均收费，需要商家在商家中心购买相应数量的短信和邮件，
 * 在短信和邮件数量允许的情况下才能发送（hezeng）
 * 
 * 2015年7月16日修改，将异步去掉，有时异步发送不能获取对应中的对应关系，导致发送短信失败
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015<／p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com<／p>
 * 
 * @author erikzhang，jinxinzhe，hezeng
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class MsgTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 收费短信发送方法，商家购买短信或者邮件后，当商家有交易订单需要发送短信提醒商家或者订单用户时使用该收费工具
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 *            :参数json，发送非订单信息的参数
	 * @param order_id
	 *            ：订单id，
	 * @throws Exception
	 */

	public void sendSmsCharge(String web, String mark, String mobile,
			String json, String order_id, String store_id) throws Exception {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null,
					"mark", mark);
			Store store = null;
			boolean flag = false;
			Map function_map = new HashMap();
			List<Map> function_maps = new ArrayList<Map>();
			if (store_id != null && !store_id.equals("")) {
				store = this.storeService.getObjById(CommUtil
						.null2Long(store_id));
				if (store.getStore_sms_count() > 0) {
					function_maps = (List<Map>) Json.fromJson(store
							.getSms_email_info());
					for (Map temp_map2 : function_maps) {

						if (template != null
								&& CommUtil.null2String(temp_map2.get("type"))
										.equals(CommUtil.null2String(template
												.getType()))
								&& CommUtil.null2String(temp_map2.get("mark"))
										.equals(template.getMark())) {
							function_map = temp_map2;
							if (CommUtil.null2Int(function_map.get("sms_open")) == 1) {// 验证功能是否开启
								flag = true;
								break;
							} else {
								System.out.println("商家已关闭该短信发送功能");
							}
						}
					}
				}else{
					System.out.println("该商家可用短信数量为0，发送失败");
				}
			}
			if (flag && template != null && template.isOpen()) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				Map map = this.queryJson(json);
				if (mobile != null && !mobile.equals("")) {
					if (order_id != null) {
						OrderForm order = this.orderFormService
								.getObjById(CommUtil.null2Long(order_id));
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(order.getUser_id()));
						context.setVariable("buyer", buyer);
						if (order.getStore_id()!=null) {
							context.setVariable("store", store);
							User seller = this.userService.getObjById(store.getUser().getId());
							context.setVariable("seller", seller);
						}
						context.setVariable("config",
								this.configService.getSysConfig());
						context.setVariable("send_time",
								CommUtil.formatLongDate(new Date()));
						context.setVariable("webPath", web);
						context.setVariable("order", order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map
								.get("receiver_id"));
						User receiver = this.userService
								.getObjById(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map
								.get("sender_id"));
						User sender = this.userService.getObjById(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService.getObjById(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map
								.get("seller_id"));
						User seller = this.userService.getObjById(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long temp_order_id = CommUtil.null2Long(map
								.get("order_id"));
						OrderForm orderForm = this.orderFormService
								.getObjById(temp_order_id);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map
								.get("childorder_id"));
						OrderForm orderForm = this.orderFormService
								.getObjById(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService.getObjById(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods")
								.toString());
					}
					context.setVariable("config",
							this.configService.getSysConfig());
					context.setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					Expression ex = exp.parseExpression(template.getContent(),
							new SpelTemplate());
					String content = ex.getValue(context, String.class);
					boolean result = this.sendSMS(mobile, content);
					if (result) {// 更新商家店铺发送短信邮件信息
						System.out.println("发送短信成功");
						if (store != null) {
							store.setStore_sms_count(store.getStore_sms_count() - 1);// 商家短信数量减1
							function_map.put("sms_count",
									CommUtil.null2Int(function_map
											.get("sms_count")) + 1);// 商家功能发送短信数量加1
							String sms_email_json = Json.toJson(function_maps,
									JsonFormat.compact());
							store.setSend_sms_count(store.getSend_sms_count() + 1);
							store.setSms_email_info(sms_email_json);
							this.storeService.update(store);
						}
					}
				}
			}
		} else {
			System.out.println("系统关闭了短信发送功能！");
		}
	}

	/**
	 * 收费邮件发送方法，商家购买短信或者邮件后，当商家有交易订单需要发送短信提醒商家或者订单用户时使用该收费工具
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @param order_id
	 *            :订单操作时发送邮件
	 * @param store_id
	 *            :发送邮件的店铺id
	 * @throws Exception
	 */

	public void sendEmailCharge(String weburl, String mark, String email,
			String json, String order_id, String store_id) throws Exception {
		System.out.println("email:" + email);
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null,
					"mark", mark);
			Store store = null;
			boolean flag = false;
			Map function_map = new HashMap();
			List<Map> function_maps = new ArrayList<Map>();
			if (store_id != null && !store_id.equals("")) {
				store = this.storeService.getObjById(CommUtil
						.null2Long(store_id));
				if (store != null && store.getStore_email_count() > 0) {
					function_maps = (List<Map>) Json.fromJson(store
							.getSms_email_info());
					for (Map temp_map2 : function_maps) {
						if (template != null
								&& CommUtil.null2String(temp_map2.get("type"))
										.equals(CommUtil.null2String(template
												.getType()))
								&& CommUtil.null2String(temp_map2.get("mark"))
										.equals(template.getMark())) {
							function_map = temp_map2;
							if (CommUtil.null2Int(function_map
									.get("email_open")) == 1) {// 验证功能是否开启
								flag = true;
								break;
							} else {
								flag = false;
								System.out.println("商家已关闭该邮件发送功能");
							}
						}
					}
				} else {
					System.out.println("商家没有购买邮件流量");
				}
			}
			if (flag && template != null && template.isOpen()) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				Map map = this.queryJson(json);
				String subject = template.getTitle();
				if (order_id != null) {
					OrderForm order = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					User buyer = this.userService.getObjById(CommUtil
							.null2Long(order.getUser_id()));
					context.setVariable("buyer", buyer);
					if (order.getStore_id()!=null) {
						context.setVariable("store", store);
						User seller = this.userService.getObjById(store.getUser().getId());
						context.setVariable("seller", seller);
					}
					context.setVariable("config",
							this.configService.getSysConfig());
					context.setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", weburl);
					context.setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map
							.get("receiver_id"));
					User receiver = this.userService.getObjById(receiver_id);
					context.setVariable("receiver", receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService.getObjById(sender_id);
					context.setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.getObjById(buyer_id);
					context.setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService.getObjById(seller_id);
					context.setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long temp_order_id = CommUtil
							.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(temp_order_id);
					context.setVariable("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map
							.get("childorder_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(childorder_id);
					context.setVariable("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService.getObjById(goods_id);
					context.setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.setVariable("seller", map.get("self_goods")
							.toString());
				}
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				context.setVariable("webPath", weburl);
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = ex.getValue(context, String.class);
				boolean result = this.sendEmail(email, subject, content);
				if (result) {// 更新商家店铺发送短信邮件信息
					System.out.println("发送邮件成功");
					if (store != null) {
						store.setStore_email_count(store.getStore_email_count() - 1);// 商家邮件数量减1
						function_map.put("email_count", CommUtil
								.null2Int(function_map.get("email_count")) + 1);// 商家功能发送邮件数量加1
						String sms_email_json = Json.toJson(function_maps,
								JsonFormat.compact());
						store.setSms_email_info(sms_email_json);
						store.setSend_email_count(store.getSend_email_count() + 1);
						this.storeService.update(store);
					}
				}
			}
		} else {
			System.out.println("系统关闭了邮件发送功能！");
		}
	}

	/**
	 * 免费短信发送方法，系统给用户发送的短信工具，
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */

	public void sendSmsFree(String web, String mark, String mobile,
			String json, String order_id) throws Exception {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null,
					"mark", mark);
			if (template != null && template.isOpen()) {
				Map map = this.queryJson(json);
				if (mobile != null && !mobile.equals("")) {
					ExpressionParser exp = new SpelExpressionParser();
					EvaluationContext context = new StandardEvaluationContext();
					if (order_id != null) {
						OrderForm order = this.orderFormService
								.getObjById(CommUtil.null2Long(order_id));
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(order.getUser_id()));
						if (order.getStore_id()!=null) {
							Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
							context.setVariable("store", store);
							User seller = this.userService.getObjById(store.getUser().getId());
							context.setVariable("seller", seller);
						}
						context.setVariable("buyer", buyer);
						context.setVariable("config",
								this.configService.getSysConfig());
						context.setVariable("send_time",
								CommUtil.formatLongDate(new Date()));
						context.setVariable("webPath", web);
						context.setVariable("order", order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map
								.get("receiver_id"));
						User receiver = this.userService
								.getObjById(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map
								.get("sender_id"));
						User sender = this.userService.getObjById(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService.getObjById(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map
								.get("seller_id"));
						User seller = this.userService.getObjById(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long order_id_temp = CommUtil.null2Long(map
								.get("order_id"));
						OrderForm orderForm = this.orderFormService
								.getObjById(order_id_temp);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map
								.get("childorder_id"));
						OrderForm orderForm = this.orderFormService
								.getObjById(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService.getObjById(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods")
								.toString());
					}
					context.setVariable("config",
							this.configService.getSysConfig());
					context.setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					System.out.println("短信模板内容："+template.getContent());
					Expression ex = exp.parseExpression(template.getContent(),
							new SpelTemplate());
					try {
						String content = ex.getValue(context, String.class);
						boolean ret = this.sendSMS(mobile, content);
						if (ret) {
							System.out.println("发送短信成功");
						} else {
							System.out.println("发送短信失败");
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				
				
				}
			}
		} else {
			System.out.println("系统关闭了短信发送功能！");
		}
	}

	/**
	 * 免费邮件发送方法， 系统给用户发送的邮件工具，
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */

	public void sendEmailFree(String web, String mark, String email,
			String json, String order_id) throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null,
					"mark", mark);
			if (template != null && template.isOpen()) {
				Map map = this.queryJson(json);
				String subject = template.getTitle();
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				if (order_id != null) {
					OrderForm order = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					User buyer = this.userService.getObjById(CommUtil
							.null2Long(order.getUser_id()));
					if (order.getStore_id()!=null) {
						Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
						context.setVariable("store", store);
						User seller = this.userService.getObjById(store.getUser().getId());
						context.setVariable("seller", seller);
					}
					context.setVariable("buyer", buyer);
					context.setVariable("config",
							this.configService.getSysConfig());
					context.setVariable("send_time",
							CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					context.setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map
							.get("receiver_id"));
					User receiver = this.userService.getObjById(receiver_id);
					context.setVariable("receiver", receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService.getObjById(sender_id);
					context.setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.getObjById(buyer_id);
					context.setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService.getObjById(seller_id);
					context.setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long order_id_temp = CommUtil
							.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(order_id_temp);
					context.setVariable("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map
							.get("childorder_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(childorder_id);
					context.setVariable("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService.getObjById(goods_id);
					context.setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.setVariable("seller", map.get("self_goods")
							.toString());
				}
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				context.setVariable("webPath", web);
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = ex.getValue(context, String.class);
				this.sendEmail(email, subject, content);
				System.out.println("发送邮件成功");
			} else {
				System.out.println("系统关闭了邮件发送功能");
			}
		}
	}

	/**
	 * 发送短信底层工具
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendSMS(String mobile, String content)
			throws UnsupportedEncodingException {
		boolean result = true;
		if (this.configService.getSysConfig().isSmsEnbale()) {
			String url = this.configService.getSysConfig().getSmsURL();
			String userName = this.configService.getSysConfig()
					.getSmsUserName();
			String password = this.configService.getSysConfig()
					.getSmsPassword();
			SmsBase sb = new SmsBase(Globals.DEFAULT_SMS_URL, userName,
					password);// 固定硬编码短信发送接口
			String ret = sb.SendSms(mobile, content);
			if (!ret.substring(0, 3).equals("000")) {
				result = false;
			}
		} else {
			result = false;
			System.out.println("系统关闭了短信发送功能");
		}
		return result;
	}

	/**
	 * 发送邮件底层工具
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = true;
		if (this.configService.getSysConfig().isEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig()
					.getEmailUser();
			String to_mail_address = email;
			if (username != null && password != null && !username.equals("")
					&& !password.equals("") && smtp_server != null
					&& !smtp_server.equals("") && to_mail_address != null
					&& !to_mail_address.trim().equals("")) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties mailProps = new Properties();
				mailProps.put("mail.smtp.auth", "true");
				mailProps.put("username", username);
				mailProps.put("password", password);
				mailProps.put("mail.smtp.host", smtp_server);
				Session mailSession = Session.getInstance(mailProps, auth);
				MimeMessage message = new MimeMessage(mailSession);
				try {
					message.setFrom(new InternetAddress(from_mail_address));
					message.setRecipient(Message.RecipientType.TO,
							new InternetAddress(to_mail_address));
					message.setSubject(subject);
					MimeMultipart multi = new MimeMultipart("related");
					BodyPart bodyPart = new MimeBodyPart();
					bodyPart.setDataHandler(new DataHandler(content,
							"text/html;charset=UTF-8"));// 网页格式
					// bodyPart.setText(content);
					multi.addBodyPart(bodyPart);
					message.setContent(multi);
					message.saveChanges();
					Transport.send(message);
					ret = true;
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				}
			} else {
				ret = false;
			}
		} else {
			ret = false;
			System.out.println("系统关闭了邮件发送功能");
		}
		System.out.println("ret:" + ret);
		return ret;
	}

	/**
	 * 解析json工具
	 * 
	 * @param json
	 * @return
	 */
	private Map queryJson(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}
}
