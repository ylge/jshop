package com.iskyshop.module.circle.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.domain.CircleInvitation;
import com.iskyshop.module.circle.domain.CircleInvitationReply;
import com.iskyshop.module.circle.domain.query.InvitationReplyQueryObject;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationReplyService;
import com.iskyshop.module.circle.service.IInvitationService;
import com.iskyshop.module.circle.view.tools.CircleViewTools;

/**
 * 
 * <p>
 * Title: CircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子控制器，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class InvitationViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IInvitationReplyService invitationReplyService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private CircleViewTools circleViewTools;
	@Autowired
	private IFavoriteService favoriteService;

	/**
	 * 帖子发布
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "帖子发布", value = "/circle/invitation_publish.htm*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_publish.htm")
	public ModelAndView invitation_publish(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		ModelAndView mv = new JModelAndView("circle/invitation_publish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (cid != null && !cid.equals("")) {
			User user = SecurityUserHolder.getCurrentUser();
			SysConfig sc = this.configService.getSysConfig();
			int temp = sc.getPublish_post_limit();
			int seller_limit = sc.getPublish_seller_limit();
			int level = this.query_user_level(SecurityUserHolder
					.getCurrentUser());
			boolean limit = false;
			if (seller_limit == 0) {
				limit = true;
			} else {
				if (user.getUserRole().equalsIgnoreCase("seller")) {
					limit = true;
				}
			}
			String op_title = "铜牌用户";
			if (temp == 1) {
				op_title = "银牌用户";
			} else if (temp == 2) {
				op_title = "金牌用户";
			}
			if (temp == 3) {
				op_title = "超级会员";
			}
			if (level >= temp) {
				if (limit) {
					Circle cir = this.circleService.getObjById(CommUtil
							.null2Long(cid));
					String invitation_publish_session = "invitation_publish_"
							+ UUID.randomUUID();
					request.getSession(false).setAttribute(
							"invitation_publish_session",
							invitation_publish_session);
					mv.addObject("cir", cir);
					mv.addObject("invitation_publish_session",
							invitation_publish_session);
					// 查询收藏商品和店铺信息
					FavoriteQueryObject fqo = new FavoriteQueryObject();
					fqo.addQuery("obj.type", new SysMap("type", 0), "=");
					fqo.addQuery("obj.user_id",
							new SysMap("user_id", user.getId()), "=");
					fqo.setConstruct("new Favorite(id,type,goods_name,goods_id,goods_photo,goods_type,goods_store_id,goods_current_price, goods_photo_ext,goods_store_second_domain, user_name, user_id)");
					fqo.setCurrentPage(1);
					fqo.setPageSize(5);
					IPageList pList = this.favoriteService.list(fqo);
					CommUtil.saveIPageList2ModelAndView2(
							CommUtil.getURL(request)
									+ "/circle/invitation_goods.htm", "", "",
							"goods", pList, mv);
					fqo.clearQuery();
					fqo.addQuery("obj.type", new SysMap("type", 1), "=");
					fqo.addQuery("obj.user_id",
							new SysMap("user_id", user.getId()), "=");
					fqo.setCurrentPage(1);
					fqo.setPageSize(5);
					fqo.setConstruct("new Favorite(id,type,store_name,store_id,store_photo,store_second_domain,store_addr,store_ower,user_name,user_id)");
					pList = this.favoriteService.list(fqo);
					CommUtil.saveIPageList2ModelAndView2(
							CommUtil.getURL(request)
									+ "/circle/invitation_store.htm", "", "",
							"store", pList, mv);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("url", "/circle/index.htm");
					mv.addObject("op_title", "只有商家用户可以发布帖子");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index.htm");
				mv.addObject("op_title", "只有" + op_title + "可以发布帖子");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", "请求参数错误");
		}
		return mv;
	}

	/**
	 * 帖子中收藏店铺ajax加载,前端使用Ajax加载分页数据
	 */
	@SecurityMapping(title = "帖子中收藏店铺ajax加载", value = "/circle/invitation_store.htm*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_store.htm")
	public ModelAndView invitation_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("/circle/invitation_store.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FavoriteQueryObject fqo = new FavoriteQueryObject();
		fqo.addQuery("obj.type", new SysMap("type", 1), "=");
		fqo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
		fqo.setCurrentPage(CommUtil.null2Int(currentPage));
		fqo.setPageSize(5);
		fqo.setConstruct("new Favorite(id,type,store_name,store_id,store_photo,store_second_domain,store_addr,store_ower,user_name,user_id)");
		IPageList pList = this.favoriteService.list(fqo);
		CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request)
				+ "/circle/invitation_store.htm", "", "", "store", pList, mv);
		return mv;
	}

	/**
	 * 帖子中收藏商品ajax加载,前端使用Ajax加载分页数据
	 */
	@SecurityMapping(title = "帖子中商品ajax加载", value = "/circle/invitation_goods.htm*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_goods.htm")
	public ModelAndView invitation_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("/circle/invitation_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FavoriteQueryObject fqo = new FavoriteQueryObject();
		fqo.addQuery("obj.type", new SysMap("type", 0), "=");
		fqo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
		fqo.setConstruct("new Favorite(id,type,goods_name,goods_id,goods_photo,goods_type,goods_store_id,goods_current_price, goods_photo_ext,goods_store_second_domain, user_name, user_id)");
		fqo.setCurrentPage(CommUtil.null2Int(currentPage));
		fqo.setPageSize(5);
		IPageList pList = this.favoriteService.list(fqo);
		CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request)
				+ "/circle/invitation_goods.htm", "", "", "goods", pList, mv);
		return mv;
	}

	/**
	 * 帖子发布
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "帖子发布", value = "/circle/invitation_publish_save.htm*", rtype = "buyer", rname = "发布帖子", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_publish_save.htm")
	public ModelAndView invitation_publish_save(HttpServletRequest request,
			HttpServletResponse response, String cid,
			String invitation_publish_session, String item_ids) {
		ModelAndView mv = new JModelAndView("circle/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("url", "/circle/index.htm");
		mv.addObject("op_title", "帖子发布成功，请等待圈主审核");
		if (cid != null && !cid.equals("")) {
			String invitation_publish_session1 = CommUtil.null2String(request
					.getSession(false).getAttribute(
							"invitation_publish_session"));
			if (invitation_publish_session1.equals(invitation_publish_session)) {
				request.getSession(false).removeAttribute(
						"invitation_publish_session");
				Circle cir = this.circleService.getObjById(CommUtil
						.null2Long(cid));
				WebForm wf = new WebForm();
				CircleInvitation invitation = wf.toPo(request,
						CircleInvitation.class);
				invitation.setAddTime(new Date());
				// 更新圈子中帖子数量
				cir.setInvitation_count(cir.getInvitation_count() + 1);
				this.circleService.update(cir);
				invitation.setCircle_id(cir.getId());
				invitation.setCircle_name(cir.getTitle());
				invitation.setUser_id(SecurityUserHolder.getCurrentUser()
						.getId());
				invitation.setUserName(SecurityUserHolder.getCurrentUser()
						.getUserName());
				// 处理附加信息，包含商品附件、店铺附件
				String[] item_list = CommUtil.null2String(item_ids).split(",");
				List<Map> item_maps = new ArrayList<Map>();
				for (String item : item_list) {
					String[] items = item.split("-");
					if (items.length == 2) {
						Map map = new HashMap();
						String item_url = "";
						String item_img = "";
						if (items[0].indexOf("_fav") >= 0) {// 收藏添加商品或者店铺
							Favorite fav = this.favoriteService
									.getObjById(CommUtil.null2Long(items[1]));
							if (items[0].indexOf("goods") >= 0) {
								if (CommUtil.null2String(fav.getGoods_photo())
										.equals("")) {
									item_img = CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getName();
								} else {
									item_img = CommUtil.getURL(request) + "/"
											+ fav.getGoods_photo();
								}
								if (this.configService.getSysConfig()
										.isSecond_domain_open()
										&& fav.getGoods_type() == 1
										&& !CommUtil
												.null2String(
														fav.getGoods_store_second_domain())
												.equals("")) {
									item_url = "http://"
											+ fav.getGoods_store_second_domain()
											+ "."
											+ CommUtil.generic_domain(request)
											+ "/goods_" + fav.getGoods_id()
											+ ".htm";
								} else {
									item_url = CommUtil.getURL(request)
											+ "/goods_" + fav.getGoods_id()
											+ ".htm";
								}

							}
							if (items[0].indexOf("store") >= 0) {
								if (!CommUtil.null2String(fav.getStore_photo())
										.equals("")) {
									item_img = CommUtil.getURL(request) + "/"
											+ fav.getStore_photo();
								} else {
									item_img = CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getName();
								}
								if (this.configService.getSysConfig()
										.isSecond_domain_open()
										&& !CommUtil
												.null2String(
														fav.getGoods_store_second_domain())
												.equals("")) {
									item_url = "http://"
											+ fav.getGoods_store_second_domain()
											+ "."
											+ CommUtil.generic_domain(request);
								} else {
									item_url = CommUtil.getURL(request)
											+ "/store_" + fav.getStore_id()
											+ ".htm";
								}
							}
						}
						if (items[0].indexOf("_url") >= 0) {// 路径添加商品或者店铺
							if (items[0].indexOf("goods") >= 0) {
								Goods goods = this.goodsService
										.getObjById(CommUtil
												.null2Long(items[1]));
								if (goods.getGoods_main_photo() == null) {
									item_img = CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getGoodsImage().getName();
								} else {
									item_img = CommUtil.getURL(request)
											+ "/"
											+ goods.getGoods_main_photo()
													.getPath()
											+ "/"
											+ goods.getGoods_main_photo()
													.getName();
								}
								if (this.configService.getSysConfig()
										.isSecond_domain_open()
										&& goods.getGoods_type() == 1
										&& !CommUtil
												.null2String(
														goods.getGoods_store()
																.getStore_second_domain())
												.equals("")) {
									item_url = "http://"
											+ goods.getGoods_store()
													.getStore_second_domain()
											+ "."
											+ CommUtil.generic_domain(request)
											+ "/goods_" + goods.getId()
											+ ".htm";
								} else {
									item_url = CommUtil.getURL(request)
											+ "/goods_" + goods.getId()
											+ ".htm";
								}
							}
							if (items[0].indexOf("store") >= 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(items[1]));
								if (!CommUtil
										.null2String(store.getStore_logo())
										.equals("")) {
									item_img = CommUtil.getURL(request) + "/"
											+ store.getStore_logo().getPath()
											+ "/"
											+ store.getStore_logo().getName();
								} else {
									item_img = CommUtil.getURL(request)
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getPath()
											+ "/"
											+ this.configService.getSysConfig()
													.getStoreImage().getName();
								}
								if (this.configService.getSysConfig()
										.isSecond_domain_open()
										&& !CommUtil.null2String(
												store.getStore_second_domain())
												.equals("")) {
									item_url = "http://"
											+ store.getStore_second_domain()
											+ "."
											+ CommUtil.generic_domain(request);
								} else {
									item_url = CommUtil.getURL(request)
											+ "/store_" + store.getId()
											+ ".htm";
								}
							}
						}
						String item_id = items[1];
						map.put("item_type", items[0]);
						map.put("item_url", item_url);
						map.put("item_img", item_img);
						map.put("item_id", item_id);
						item_maps.add(map);
					}
				}
				invitation.setItem_info(Json.toJson(item_maps,
						JsonFormat.compact()));
				this.invitationService.save(invitation);
				mv.addObject("url",
						"/circle/invitation_detail_" + invitation.getId()
								+ ".htm");
				mv.addObject("op_title", "帖子发布成功");
			} else {
				mv = new JModelAndView("circle/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index.htm");
				mv.addObject("op_title", "禁止表单重复提交");
			}
		} else {
			mv = new JModelAndView("circle/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", "请求参数错误");
		}
		return mv;
	}

	/**
	 * 帖子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子点赞", value = "/circle/invitation_parise.htm*", rtype = "buyer", rname = "帖子点赞", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_parise.htm")
	public void invitation_parise(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = new HashMap();
		int code = 100;// 点赞成功，-100，点赞失败
		CircleInvitation obj = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		String uid = CommUtil.null2String(SecurityUserHolder.getCurrentUser()
				.getId());
		String ret = circleViewTools.generInvitationParise(id, uid);
		if (ret == "false") {// 没有点赞
			if (obj.getPraiseInfo() != null && !obj.getPraiseInfo().equals("")) {
				obj.setPraiseInfo(obj.getPraiseInfo() + uid + ",");
			} else {
				obj.setPraiseInfo("," + uid + ",");
			}
			obj.setPraise_count(obj.getPraise_count() + 1);
			this.invitationService.update(obj);
			json_map.put("count", obj.getPraise_count());
		} else {
			code = -100;
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 帖子详情
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/invitation_detail.htm")
	public ModelAndView invitation_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("circle/invitation_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CircleInvitation obj = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		if (obj != null) {
			mv.addObject("obj", obj);
			mv.addObject("circleViewTools", circleViewTools);
			// 推荐圈子
			Map params = new HashMap();
			params.put("status", 5);
			List<Circle> recommends = this.circleService
					.query("select obj from Circle obj where obj.status=:status order by obj.attention_count desc",
							params, 0, 10);
			mv.addObject("recommends", recommends);
			// 热门讨论帖子
			params.clear();
			List<CircleInvitation> hots = this.invitationService
					.query("select obj from CircleInvitation obj order by obj.reply_count desc",
							params, 0, 10);
			mv.addObject("hots", hots);
			// 判断是否已经关注该圈子
			int code = 100;
			if (SecurityUserHolder.getCurrentUser() != null) {
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				Map map = new HashMap();
				List<Map> map_list = new ArrayList<Map>();
				if (user.getCircle_attention_info() != null
						&& !user.getCircle_attention_info().equals("")) {
					map_list = Json.fromJson(List.class,
							user.getCircle_attention_info());
					for (Map temp : map_list) {
						if (CommUtil.null2Long(temp.get("id")).equals(
								obj.getCircle_id())) {
							code = -100;// 已经关注该圈子
							break;
						}
					}
				}
			}
			if (obj.getItem_info() != null) {
				mv.addObject("items",
						Json.fromJson(List.class, obj.getItem_info()));
			}
			mv.addObject("code", code);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", "您所访问的帖子不存在！");
		}
		return mv;
	}

	/**
	 * 帖子回复
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "帖子回复", value = "/circle/invitation_reply_save.htm*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_reply_save.htm")
	public void invitation_reply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String reply_content) {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100.失败，参数错误
		if (id != null && !id.equals("")) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			CircleInvitation obj = this.invitationService.getObjById(CommUtil
					.null2Long(id));
			obj.setReply_count(obj.getReply_count() + 1);
			invitationService.update(obj);
			CircleInvitationReply reply = new CircleInvitationReply();
			reply.setAddTime(new Date());
			reply.setContent(reply_content);
			reply.setInvitation_id(obj.getId());
			reply.setUser_id(user.getId());
			reply.setUser_name(user.getUserName());
			reply.setLevel_count(obj.getReply_count());
			String photo = "";
			if (user.getPhoto() != null) {
				photo = user.getPhoto().getPath() + "/"
						+ user.getPhoto().getName();
			} else {
				photo = this.configService.getSysConfig().getMemberIcon()
						.getPath()
						+ "/"
						+ this.configService.getSysConfig().getMemberIcon()
								.getName();
			}
			reply.setUser_photo(photo);
			this.invitationReplyService.save(reply);
		} else {
			code = -100;
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 帖子回复列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/invitation_reply.htm")
	public ModelAndView invitation_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String owner_id) {
		ModelAndView mv = new JModelAndView("circle/invitation_reply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CircleInvitation inv = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		InvitationReplyQueryObject qo = new InvitationReplyQueryObject(
				currentPage, mv, "addTime", "asc");
		qo.addQuery("obj.invitation_id",
				new SysMap("invitation_id", CommUtil.null2Long(id)), "=");
		if (owner_id != null && !owner_id.equals("")) {
			qo.addQuery("obj.user_id",
					new SysMap("user_id", CommUtil.null2Long(owner_id)), "=");
		}
		qo.addQuery("and obj.parent_id is null", null);
		qo.setPageSize(15);
		IPageList pList = this.invitationReplyService.list(qo);
		String url = CommUtil.getURL(request) + "/circle/invitation_reply.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("inv", inv);
		return mv;
	}

	/**
	 * 帖子回复他人信息请求列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/circle/invitation_reply_others.htm")
	public ModelAndView invitation_reply_others(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"circle/invitation_reply_others.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		InvitationReplyQueryObject qo = new InvitationReplyQueryObject(
				currentPage, mv, "addTime", "asc");
		qo.addQuery("obj.parent_id",
				new SysMap("parent_id", CommUtil.null2Long(pid)), "=");
		qo.setPageSize(10);// 回复他人的信息无分页
		IPageList pList = this.invitationReplyService.list(qo);
		String url = CommUtil.getURL(request)
				+ "/circle/invitation_reply_others.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("pid", pid);
		return mv;
	}

	/**
	 * 帖子回复他人信息方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "回复他人信息", value = "/circle/invitation_reply_others_save.htm*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_reply_others_save.htm")
	public void invitation_reply_others_save(HttpServletRequest request,
			HttpServletResponse response, String pid, String reply_content) {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100.失败，参数错误
		if (pid != null && !pid.equals("")) {
			User Fromuser = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			CircleInvitationReply parent = this.invitationReplyService
					.getObjById(CommUtil.null2Long(pid));
			String userName = parent.getUser_name();
			String temp_str = "回复" + userName + ":";
			if (reply_content.indexOf(temp_str) >= 0) {
				CircleInvitationReply reply = new CircleInvitationReply();
				reply.setAddTime(new Date());
				reply.setContent(Fromuser.getUserName() + reply_content);
				reply.setUser_id(Fromuser.getId());
				reply.setUser_name(Fromuser.getUserName());
				String photo = "";
				if (Fromuser.getPhoto() != null) {
					photo = Fromuser.getPhoto().getPath() + "/"
							+ Fromuser.getPhoto().getName();
				} else {
					photo = this.configService.getSysConfig().getMemberIcon()
							.getPath()
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getName();
				}
				reply.setParent_id(parent.getId());
				reply.setInvitation_id(parent.getInvitation_id());
				reply.setUser_photo(photo);
				this.invitationReplyService.save(reply);
				parent.setReply_count(parent.getReply_count() + 1);
				this.invitationReplyService.save(parent);
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除帖子，只有楼主可以删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "删除帖子", value = "/circle/invitation_reply_others_save.htm*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_delete.htm")
	public void invitation_delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		CircleInvitation obj = this.invitationService.getObjById(CommUtil
				.null2Long(id));
		Long cid = obj.getCircle_id();
		boolean ret = true;
		if (obj != null) {
			if (CommUtil.null2String(obj.getUser_id()).equals(
					CommUtil.null2String(SecurityUserHolder.getCurrentUser()
							.getId()))) {
				ret = this.invitationService.delete(CommUtil.null2Long(id));
				if (ret) {
					Circle cir = this.circleService.getObjById(cid);
					cir.setInvitation_count(cir.getInvitation_count() - 1);
					this.circleService.update(cir);
					// 删除帖子所有回复
					Map params = new HashMap();
					params.put("id", CommUtil.null2Long(id));
					List reply_ids = this.invitationReplyService
							.query("select obj.id from CircleInvitationReply obj where obj.invitation_id=:id",
									params, -1, -1);
					List dele_ids = new ArrayList();
					for (Object temp_id : reply_ids) {
						dele_ids.add(CommUtil.null2Long(temp_id));
					}
					this.invitationReplyService.batchDelete(dele_ids);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 商品URL解析，根据商品URL解析出商品信息，并以json返回到前台，适用于iskyshop_b2b2c v2015版
	 * 
	 * @param request
	 * @param response
	 * @param goods_url
	 */
	@SecurityMapping(title = "商品URL解析", value = "/circle/invitation_goods_parse.htm*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_goods_parse.htm")
	public void invitation_goods_parse(HttpServletRequest request,
			HttpServletResponse response, String goods_url) {
		Map map = new HashMap();
		int error = 1;// 1表示商品URL不正确
		String img_path = "";// 商品图片路径
		String img_url = "";
		Long id = -1l;// 商品id
		Goods obj = null;
		String site_url = CommUtil.getURL(request);
		String regr1 = site_url + "/goods_[0-9]+_*[a-z]*.htm$";
		String regr2 = site_url + "/goods.htm\\?id=[0-9]+&*.*$";
		Pattern pattern1 = Pattern.compile(regr1);
		Pattern pattern2 = Pattern.compile(regr2);
		Matcher matcher1 = pattern1.matcher(goods_url);
		Matcher matcher2 = pattern2.matcher(goods_url);
		if (matcher1.matches()) {
			error = 0;
			if (goods_url.indexOf("_") < goods_url.lastIndexOf("_")) {
				id = CommUtil
						.null2Long(goods_url.substring(
								goods_url.indexOf("_") + 1,
								goods_url.lastIndexOf("_")));
			} else {
				id = CommUtil
						.null2Long(goods_url.substring(
								goods_url.indexOf("_") + 1,
								goods_url.lastIndexOf(".")));
			}
		}
		if (matcher2.matches()) {
			error = 0;
			if (goods_url.indexOf("&") > 0) {
				id = CommUtil.null2Long(goods_url.substring(
						goods_url.indexOf("?id=") + 4, goods_url.indexOf("&")));
			} else {
				id = CommUtil.null2Long(goods_url.substring(goods_url
						.indexOf("?id=") + 4));
			}

		}
		obj = this.goodsService.getObjById(id);
		if (obj != null) {
			if (obj.getGoods_main_photo() != null) {
				img_path = CommUtil.getURL(request) + "/"
						+ obj.getGoods_main_photo().getPath() + "/"
						+ obj.getGoods_main_photo().getName() + "_small" + "."
						+ obj.getGoods_main_photo().getExt();
			} else {
				img_path = CommUtil.getURL(request)
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();

			}
			img_url = site_url + "/goods_" + id + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& !CommUtil.null2String(
							obj.getGoods_store().getStore_second_domain())
							.equals("")) {
				img_url = "http://"
						+ obj.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request) + "/goods_" + id
						+ ".htm";
			}
		}
		map.put("error", error);
		map.put("img_path", img_path);
		map.put("id", id);
		map.put("img_url", img_url);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 店铺url继续，根据店铺地址，解析出店铺信息，并以json返回到前端处理
	 * 
	 * @param request
	 * @param response
	 * @param goods_url
	 */
	@SecurityMapping(title = "店铺URL解析", value = "/circle/invitation_store_parse.htm*", rtype = "buyer", rname = "帖子回复", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/invitation_store_parse.htm")
	public void invitation_store_parse(HttpServletRequest request,
			HttpServletResponse response, String store_url) {
		Map map = new HashMap();
		int error = 1;// 1表示商品URL不正确
		String img_path = "";// 商品图片路径
		String img_url = "";
		Long id = -1l;// 商品id
		Store obj = null;
		String site_url = CommUtil.getURL(request);
		String regr1 = site_url + "/store_[0-9]+.htm$";
		String regr2 = site_url + "/store.htm\\?id=[0-9]+&*.*$";
		String regr3 = "http://.*" + CommUtil.generic_domain(request);
		Pattern pattern1 = Pattern.compile(regr1);
		Pattern pattern2 = Pattern.compile(regr2);
		Pattern pattern3 = Pattern.compile(regr3);
		Matcher matcher1 = pattern1.matcher(store_url);
		Matcher matcher2 = pattern2.matcher(store_url);
		Matcher matcher3 = pattern3.matcher(store_url);
		// System.out.println(matcher1.matches());
		if (matcher1.matches()) {
			error = 0;
			id = CommUtil.null2Long(store_url.substring(
					store_url.indexOf("_") + 1, store_url.indexOf(".")));

			obj = this.storeService.getObjById(id);
		}
		if (matcher2.matches()) {
			error = 0;
			if (store_url.indexOf("&") > 0) {
				id = CommUtil.null2Long(store_url.substring(
						store_url.indexOf("?id=") + 4, store_url.indexOf("&")));
			} else {
				id = CommUtil.null2Long(store_url.substring(
						store_url.indexOf("?id=") + 4, store_url.indexOf(".")));
			}
			obj = this.storeService.getObjById(id);
		}
		if (matcher3.matches()) {
			error = 0;
			String store_name = store_url.substring(7,
					store_url.indexOf(CommUtil.generic_domain(request)));
			obj = this.storeService.getObjByProperty("", "store_name",
					store_name);

		}
		if (obj != null) {
			if (obj.getStore_logo() != null) {
				img_path = CommUtil.getURL(request) + "/"
						+ obj.getStore_logo().getPath() + "/"
						+ obj.getStore_logo().getName();
			} else {
				img_path = CommUtil.getURL(request)
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getName();
			}
			img_url = site_url + "/store_" + id + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& !CommUtil.null2String(obj.getStore_second_domain())
							.equals("")) {
				img_url = "http://" + obj.getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
			}
		}
		map.put("error", error);
		map.put("img_path", img_path);
		map.put("id", id);
		map.put("img_url", img_url);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据会员确定其会员等级
	 * 
	 * @param user_goods_fee
	 * @return 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
	 */
	private int query_user_level(User user) {
		if (this.configService.getSysConfig().getUser_level() != null
				&& !this.configService.getSysConfig().getUser_level()
						.equals("")) {
			if (user.getUser_goods_fee() == null) {
				user.setUser_goods_fee(new BigDecimal(0));
				this.userService.update(user);
			}
			int user_goods_fee = CommUtil.null2Int(user.getUser_goods_fee());
			Map map = Json.fromJson(HashMap.class, this.configService
					.getSysConfig().getUser_level());
			int goods_fee = CommUtil.null2Int(user_goods_fee);
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule6"))) {
				return 3;
			}
			;
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule4"))) {
				return 2;
			}
			;
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule2"))) {
				return 1;
			}
			return 0;
		} else {
			return 0;
		}
	}

}