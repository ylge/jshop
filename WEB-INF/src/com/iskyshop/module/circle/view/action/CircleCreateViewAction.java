package com.iskyshop.module.circle.view.action;

import java.io.File;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.domain.CircleClass;
import com.iskyshop.module.circle.service.ICircleClassService;
import com.iskyshop.module.circle.service.ICircleService;
import com.iskyshop.module.circle.service.IInvitationService;

/**
 * 
 * <p>
 * Title: CircleManageAction.java
 * </p>
 * 
 * <p>
 * Description: 圈主管理圈子控制器，包括创建圈子、审核圈子中帖子，管理帖子等操作
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
public class CircleCreateViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICircleService circleService;
	@Autowired
	private ICircleClassService circleclassService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 创建圈子
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "创建圈子", value = "/circle/create.htm*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/create.htm")
	public ModelAndView circle_create(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView("circle/circle_create.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		boolean ret = false;
		SysConfig sc = this.configService.getSysConfig();
		String op_title = "系统不限制用户等级";
		if (this.configService.getSysConfig().getCircle_limit() == 0) {
			ret = true;
		} else {
			int level = this.query_user_level(user);
			if (sc.getCircle_limit() == 1) {
				op_title = "系统规定铜牌用户可申请并创建圈子";
				if (level >= 0) {
					ret = true;
				}
			}
			if (sc.getCircle_limit() == 2) {
				op_title = "系统规定银牌用户可申请并创建圈子";
				if (level >= 1) {
					ret = true;
				}
			}
			if (sc.getCircle_limit() == 3) {
				op_title = "系统规定金牌用户可申请并创建圈子";
				if (level >= 2) {
					ret = true;
				}
			}
			if (sc.getCircle_limit() == 4) {
				op_title = "系统规定超级会员可申请并创建圈子";
				if (level >= 3) {
					ret = true;
				}
			}
		}
		if (ret) {
			boolean flag = false;
			String count_msg = "系统设置的用户可创建圈子数量为0";
			if (user.getCircle_create_info() != null) {
				List<Map> maps = Json.fromJson(List.class,
						user.getCircle_create_info());
				if (maps.size() < sc.getCircle_count()) {
					flag = true;
				} else {
					count_msg = "您已超出可申请数量，不可再申请";
				}
			} else {
				if (sc.getCircle_count() != 0) {
					flag = true;
				}
			}
			if (flag) {
				List<CircleClass> c_classes = this.circleclassService
						.query("select obj from CircleClass obj order by sequence asc",
								null, -1, -1);
				if (c_classes.size() > 0) {
					String session_circle_create = "session_circle_create_"
							+ UUID.randomUUID();
					request.getSession(false).setAttribute(
							"session_circle_create", session_circle_create);
					mv.addObject("session_circle_create", session_circle_create);
					mv.addObject("c_classes", c_classes);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "系统未添加类型信息，无法申请圈子");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", count_msg);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/circle/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", op_title);
			mv.addObject("url", CommUtil.getURL(request) + "/circle/index.htm");
		}
		return mv;
	}

	/**
	 * 圈子保存
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "创建圈子保存", value = "/circle/create_save.htm*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/create_save.htm")
	public ModelAndView circle_create_save(HttpServletRequest request,
			HttpServletResponse response, String class_id, String img_id,
			String session_circle_create) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op_title = "申请成功，请等待审核";
		String session_circle_create1 = CommUtil.null2String(request
				.getSession(false).getAttribute("session_circle_create"));
		if (session_circle_create1.equals(session_circle_create)) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			CircleClass cclass = this.circleclassService.getObjById(CommUtil
					.null2Long(class_id));
			WebForm wf = new WebForm();
			Circle circle = null;
			circle = wf.toPo(request, Circle.class);
			circle.setAddTime(new Date());
			circle.setClass_id(cclass.getId());
			circle.setClass_name(cclass.getClassName());
			circle.setUser_id(user.getId());
			circle.setUserName(user.getUserName());
			if (this.configService.getSysConfig().getCircle_audit() == 0) {
				circle.setStatus(5);
				op_title = "您的圈子已成功开通";
			} else {
				circle.setStatus(0);
			}
			Accessory image = this.accessoryService.getObjById(CommUtil
					.null2Long(img_id));
			Map img_map = new HashMap();
			img_map.put("id", image.getId());
			img_map.put("src", image.getPath() + "/" + image.getName());
			circle.setPhotoInfo(Json.toJson(img_map, JsonFormat.compact()));
			this.circleService.save(circle);
			List<Map> maps = new ArrayList<Map>();
			if (user.getCircle_create_info() != null) {
				maps = Json.fromJson(List.class, user.getCircle_create_info());

			}
			Map map = new HashMap();
			map.put("id", circle.getId());
			map.put("name", circle.getTitle());
			maps.add(map);
			user.setCircle_create_info(Json.toJson(maps, JsonFormat.compact()));
			this.userService.update(user);
			request.getSession(false).removeAttribute("session_circle_create");
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", op_title);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index.htm");
			mv.addObject("op_title", "禁止表单重复提交");
		}
		return mv;
	}

	@SecurityMapping(title = "圈子图片上传", value = "/circle/image_upload.htm*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping("/circle/image_upload.htm")
	public void circle_image_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id) {
		// 圈子图片上传
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "circle";
		Map map = new HashMap();
		Map json_map = new HashMap();
		Accessory img = null;
		String url = null;
		try {
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName, "", null);
			String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png|.tbi|.TBI)$";
			String imgp = (String) map.get("fileName");
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(imgp.toLowerCase());
			if (matcher.find()) {
				map = CommUtil.saveFileToServer(request, "image",
						saveFilePathName, "", null);
				if (img_id != null && !img_id.equals("")) {// 更新
					Accessory old_image = this.accessoryService
							.getObjById(CommUtil.null2Long(img_id));
					img = old_image;
					img.setName((String) map.get("fileName"));
					img.setExt("." + (String) map.get("mime"));
					img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					img.setPath(uploadFilePath + "/circle");
					img.setWidth((Integer) map.get("width"));
					img.setHeight((Integer) map.get("height"));
					img.setAddTime(new Date());
					this.accessoryService.update(img);
					url = CommUtil.getURL(request) + "/" + img.getPath() + "/"
							+ img.getName();
				} else {// 新建
					img = new Accessory();
					img.setName((String) map.get("fileName"));
					img.setExt("." + (String) map.get("mime"));
					img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					img.setPath(uploadFilePath + "/circle");
					img.setWidth((Integer) map.get("width"));
					img.setHeight((Integer) map.get("height"));
					img.setAddTime(new Date());
					this.accessoryService.save(img);
					url = CommUtil.getURL(request) + "/" + img.getPath() + "/"
							+ img.getName();
				}
				json_map.put("id", img.getId());
				json_map.put("src", url);
				json_map.put("ret", "true");
			} else {
				json_map.put("ret", "false");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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