package com.iskyshop.manage.admin.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.view.web.tools.AlbumViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: AlbumSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营相册管理类
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
 * @date 2014年5月27日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfAlbumManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsSerivce;
	@Autowired
	private AlbumViewTools albumViewTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IWaterMarkService watermarkService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ImageTools ImageTools;
	@Autowired
	private StoreTools storeTools;

	@SecurityMapping(title = "相册列表", value = "/admin/album.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album.htm")
	public ModelAndView album(HttpServletRequest request,
			HttpServletResponse response, String currentPage,String album_name) {
		ModelAndView mv = new JModelAndView("admin/blue/album.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AlbumQueryObject aqo = new AlbumQueryObject();
		if(album_name!=null&&!"".equals(album_name)){			
			aqo.addQuery("obj.album_name", new SysMap("album_name","%"+album_name+"%"), "like");
			mv.addObject("album_name", album_name);
		}
		aqo.setPageSize(32);
		Map params = new HashMap();
		params.put("role1", "ADMIN");
		params.put("role2", "ADMIN_SELLER");
		aqo.addQuery("(obj.user.userRole=:role1 or obj.user.userRole=:role2)",
				params);
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setOrderBy("album_sequence");
		aqo.setOrderType("asc");
		IPageList pList = this.albumService.list(aqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
	
		CommUtil.saveIPageList2ModelAndView(url + "/admin/album.htm", "", "",
				pList, mv);
		mv.addObject("albumViewTools", albumViewTools);
		params.clear();
		params.put("userRole", "ADMIN");
		params.put("userRole2", "ADMIN_SELLER");
		List<Album> albums = this.albumService
				.query("select obj from Album obj where (obj.user.userRole=:userRole or obj.user.userRole=:userRole2) order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("albums", albums);
		// 处理上传格式
		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		mv.addObject("currentPage",currentPage);
		this.isAdminAlbumExist();
		return mv;
	}

	@SecurityMapping(title = "修改相册", value = "/admin/album_add.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_add.htm")
	public ModelAndView album_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/album_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "新增相册", value = "/admin/album_edit.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_edit.htm")
	public ModelAndView album_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,String album_name) {
		ModelAndView mv = new JModelAndView("admin/blue/album_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album obj = this.albumService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_name1", album_name);
		return mv;
	}

	@SecurityMapping(title = "相册保存", value = "/admin/album_save.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_save.htm")
	public ModelAndView album_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,String album_name1) {
		WebForm wf = new WebForm();
		Album album = null;
		if (id.equals("")) {
			album = wf.toPo(request, Album.class);
			album.setAddTime(new Date());
		} else {
			Album obj = this.albumService.getObjById(Long.parseLong(id));
			album = (Album) wf.toPo(request, obj);
		}
		album.setUser(this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		boolean ret = true;
		if (id.equals("")) {
			ret = this.albumService.save(album);
		} else
			ret = this.albumService.update(album);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/album.htm"
				+ "?currentPage=" + currentPage+"&album_name="+album_name1);
		mv.addObject("op_title", "保存相册成功");
		return mv;
	}

	@SecurityMapping(title = "图片上传", value = "/admin/album_upload.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_upload.htm")
	public ModelAndView album_upload(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id) {
		ModelAndView mv = new JModelAndView("admin/blue/album_upload.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("role1", "ADMIN");
		params.put("role2", "ADMIN_SELLER");
		List<Album> objs = this.albumService
				.query("select obj from Album obj where (obj.user.userRole=:role1 or obj.user.userRole=:role2) order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_id", album_id);
		mv.addObject("jsessionid", request.getSession().getId());
		mv.addObject("imageSuffix", this.storeViewTools
				.genericImageSuffix(this.configService.getSysConfig()
						.getImageSuffix()));
		// 生成user_id字符串，防止在特定环境下swf上传无法获取session
		String temp_begin = request.getSession().getId().toString()
				.substring(0, 5);
		String temp_end = CommUtil.randomInt(5);
		String user_id = CommUtil.null2String(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("session_u_id", temp_begin + user_id + temp_end);
		return mv;
	}

	@SecurityMapping(title = "相册删除", value = "/admin/album_del.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_del.htm")
	public String album_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Album album = this.albumService.getObjById(CommUtil
						.null2Long(id));
				if (album != null) {
					Map params = new HashMap();
					params.put("album_id", album.getId());
					List<Accessory> accs = this.accessoryService
							.query("select obj from Accessory obj where obj.album.id=:album_id",
									params, -1, -1);
					for (Accessory acc : accs) {
						CommUtil.del_acc(request, acc);
						for (Goods goods : acc.getGoods_main_list()) {
							goods.setGoods_main_photo(null);
							this.goodsService.update(goods);
						}
						for (Goods goods1 : acc.getGoods_list()) {
							goods1.getGoods_photos().remove(acc);
							this.goodsService.update(goods1);
						}
						// 如果该图片为相册封面
						if (acc.getAlbum().getAlbum_cover() != null) {
							if (acc.getAlbum().getAlbum_cover().getId()
									.equals(acc.getId())) {
								album.setAlbum_cover(null);
								this.albumService.update(album);
							}
						}
						this.accessoryService.delete(acc.getId());
					}
					this.albumService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:album.htm";
	}

	@SecurityMapping(title = "相册封面设置", value = "/admin/album_cover.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_cover.htm")
	public String album_cover(HttpServletRequest request, String album_id,
			String id, String currentPage) {
		Accessory album_cover = this.accessoryService.getObjById(Long
				.parseLong(id));
		Album album = this.albumService.getObjById(Long.parseLong(album_id));
		album.setAlbum_cover(album_cover);
		this.albumService.update(album);
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "相册转移", value = "/admin/album_transfer.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_transfer.htm")
	public ModelAndView album_transfer(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id,
			String id) {
		ModelAndView mv = new JModelAndView("admin/blue/album_transfer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("role1", "ADMIN");
		params.put("role2", "ADMIN_SELLER");
		List<Album> objs = this.albumService
				.query("select obj from Album obj where (obj.user.userRole=:role1 or obj.user.userRole=:role2) order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_id", album_id);
		mv.addObject("mulitId", id);
		return mv;
	}

	@SecurityMapping(title = "图片转移相册", value = "/admin/album_transfer_save.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_transfer_save.htm")
	public String album_transfer_save(HttpServletRequest request,
			String mulitId, String album_id, String to_album_id,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Accessory acc = this.accessoryService.getObjById(Long
						.parseLong(id));
				Album to_album = this.albumService.getObjById(Long
						.parseLong(to_album_id));
				acc.setAlbum(to_album);
				this.accessoryService.update(acc);
			}
		}
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "图片列表", value = "/admin/album_image.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_image.htm")
	public ModelAndView album_image(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/album_image.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album album = this.albumService.getObjById(Long.parseLong(id));
		AccessoryQueryObject aqo = new AccessoryQueryObject();
		if (id != null && !id.equals("")) {
			aqo.addQuery("obj.album.id",
					new SysMap("album_id", CommUtil.null2Long(id)), "=");
		} else {
			aqo.addQuery("obj.album.id is null", null);
		}
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setPageSize(15);
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/album_image.htm", "",
				"&id=" + id, pList, mv);
		Map params = new HashMap();
		params.put("userRole1", "ADMIN");
		params.put("userRole2", "ADMIN_SELLER");
		List<Album> albums = this.albumService
				.query("select obj from Album obj where (obj.user.userRole=:userRole1 or obj.user.userRole=:userRole2) order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("albums", albums);
		mv.addObject("album", album);
		return mv;
	}

	@SecurityMapping(title = "图片幻灯查看", value = "/admin/image_slide.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/image_slide.htm")
	public ModelAndView image_slide(HttpServletRequest request,
			HttpServletResponse response, String album_id, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/image_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album album = this.albumService
				.getObjById(CommUtil.null2Long(album_id));
		mv.addObject("album", album);
		Accessory current_img = this.accessoryService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("current_img", current_img);
		mv.addObject("ImageTools", ImageTools);
		return mv;
	}

	@SecurityMapping(title = "相册内图片删除", value = "/admin/album_img_del.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_img_del.htm")
	public String album_img_del(HttpServletRequest request, String mulitId,
			String album_id, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Accessory acc = this.accessoryService.getObjById(Long
						.parseLong(id));
				String middle_path = request.getSession().getServletContext()
						.getRealPath("/")
						+ acc.getPath()
						+ File.separator
						+ acc.getName()
						+ "_middle." + acc.getExt();
				CommUtil.deleteFile(middle_path);
				CommUtil.del_acc(request, acc);
				for (Goods goods : acc.getGoods_main_list()) {
					goods.setGoods_main_photo(null);
					this.goodsSerivce.update(goods);
				}
				for (Goods goods : acc.getGoods_list()) {
					goods.getGoods_photos().remove(acc);
					this.goodsSerivce.update(goods);
				}
				// 这个acc附件是否是这个相册的封面，如果是请删除
				Map params = new HashMap();
				params.put("ac_id", acc.getId());
				List<Album> albums = this.albumService
						.query("select obj from Album obj where obj.album_cover.id=:ac_id",
								params, 0, 1);
				if (albums.size() > 0) {
					Album album = albums.get(0);
					album.setAlbum_cover(null);
					this.albumService.update(album);
				}
				this.accessoryService.delete(acc.getId());
			}
		}
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "图片添加水印", value = "/admin/album_watermark.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_watermark.htm")
	public String album_watermark(HttpServletRequest request, String mulitId,
			String album_id, String to_album_id, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		WaterMark waterMark = this.waterMarkService.getObjByProperty(null,
				"user.id", user.getId());
		if (waterMark != null) {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					Accessory acc = this.accessoryService.getObjById(Long
							.parseLong(id));
					String path = request.getSession().getServletContext()
							.getRealPath("/")
							+ acc.getPath() + File.separator + acc.getName();
					String path_middle = request.getSession()
							.getServletContext().getRealPath("/")
							+ acc.getPath()
							+ File.separator
							+ acc.getName()
							+ "_middle." + acc.getExt();
					String path_small = request.getSession()
							.getServletContext().getRealPath("/")
							+ acc.getPath()
							+ File.separator
							+ acc.getName()
							+ "_small." + acc.getExt();
					path = path.replace("|\\", File.separator).replace("/",
							File.separator);
					path_middle = path_middle.replace("|\\", File.separator)
							.replace("/", File.separator);
					path_small = path_small.replace("|\\", File.separator)
							.replace("/", File.separator);
					if (waterMark.isWm_image_open()
							&& waterMark.getWm_image() != null) {
						String wm_path = request.getSession()
								.getServletContext().getRealPath("/")
								+ waterMark.getWm_image().getPath()
								+ File.separator
								+ waterMark.getWm_image().getName();
						CommUtil.waterMarkWithImage(wm_path, path,
								waterMark.getWm_image_pos(),
								waterMark.getWm_image_alpha());
					}
					if (waterMark.isWm_text_open()) {
						Font font = new Font(waterMark.getWm_text_font(),
								Font.BOLD, waterMark.getWm_text_font_size());
						CommUtil.waterMarkWithText(path, path,
								waterMark.getWm_text(),
								waterMark.getWm_text_color(), font,
								waterMark.getWm_text_pos(), 100f);
					}
					// 同步生成小图片
					CommUtil.createSmall(path, path_small, this.configService
							.getSysConfig().getSmallWidth(), this.configService
							.getSysConfig().getSmallHeight());
					// 同步生成中等图片
					CommUtil.createSmall(path, path_middle, this.configService
							.getSysConfig().getMiddleWidth(),
							this.configService.getSysConfig().getMiddleHeight());
				}
			}
		}
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "图片水印", value = "/admin/watermark.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/watermark.htm")
	public ModelAndView watermark(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/watermark.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<WaterMark> wms = this.watermarkService.query(
				"select obj from WaterMark obj where obj.user.id=:user_id",
				params, -1, -1);
		if (wms.size() > 0) {
			mv.addObject("obj", wms.get(0));
		}
		return mv;
	}

	/**
	 * watermark保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "图片水印保存", value = "/admin/watermark_save.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/watermark_save.htm")
	public ModelAndView watermark_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (SecurityUserHolder.getCurrentUser() != null) {
			WebForm wf = new WebForm();
			WaterMark watermark = null;
			if (id.equals("")) {
				watermark = wf.toPo(request, WaterMark.class);
				watermark.setAddTime(new Date());
			} else {
				WaterMark obj = this.watermarkService.getObjById(Long
						.parseLong(id));
				watermark = (WaterMark) wf.toPo(request, obj);
			}
			watermark.setUser(user);
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "upload/wm";
			try {
				Map map = CommUtil.saveFileToServer(request, "wm_img", path,
						null, null);
				if (!map.get("fileName").equals("")) {
					Accessory wm_image = new Accessory();
					wm_image.setAddTime(new Date());
					wm_image.setHeight(CommUtil.null2Int(map.get("height")));
					wm_image.setName(CommUtil.null2String(map.get("fileName")));
					wm_image.setPath("upload/wm");
					wm_image.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					wm_image.setUser(SecurityUserHolder.getCurrentUser());
					wm_image.setWidth(CommUtil.null2Int("width"));
					this.accessoryService.save(wm_image);
					watermark.setWm_image(wm_image);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (id.equals("")) {
				this.watermarkService.save(watermark);
			} else
				this.watermarkService.update(watermark);
			mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "水印设置成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未登陆");
		}
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/album.htm");
		return mv;
	}

	/**
	 * 自营相册图片上传
	 * 
	 * @param request
	 * @param response
	 * @param album_id
	 *            相册id
	 * @param ajaxUploadMark
	 *            上传类型标识
	 */
	@SecurityMapping(title = "自营相册图片上传", value = "/admin/album_image_upload.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_image_upload.htm")
	public void album_image_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String ajaxUploadMark) {
		Boolean html5Uploadret = false;
		Map ajaxUploadInfo = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();
		try {
			Map map = CommUtil.saveFileToServer(request, "fileImage", path,
					null, null);
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<WaterMark> wms = this.waterMarkService.query(
					"select obj from WaterMark obj where obj.user.id=:user_id",
					params, -1, -1);
			if (wms.size() > 0) {
				WaterMark mark = wms.get(0);
				if (mark.isWm_image_open() && mark.getWm_image() != null) {
					String pressImg = request.getSession().getServletContext()
							.getRealPath("")
							+ File.separator
							+ mark.getWm_image().getPath()
							+ File.separator + mark.getWm_image().getName();
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_image_pos();
					float alpha = mark.getWm_image_alpha();
					CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
				}
				if (mark.isWm_text_open()) {
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_text_pos();
					String text = mark.getWm_text();
					String markContentColor = mark.getWm_text_color();
					CommUtil.waterMarkWithText(targetImg, targetImg, text,
							markContentColor, new Font(mark.getWm_text_font(),
									Font.BOLD, mark.getWm_text_font_size()),
							pos, 100f);
				}
			}
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(url);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setUser(user);
			Album album = null;
			if (album_id != null && !album_id.equals("")) {
				album = this.albumService.getObjById(CommUtil
						.null2Long(album_id));
			} else {
				album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册【" + user.getUserName() + "】");
					album.setAlbum_sequence(-10000);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.save(album);
				}
			}
			image.setAlbum(album);
			html5Uploadret = this.accessoryService.save(image);
			if (html5Uploadret && ajaxUploadMark != null) {
				ajaxUploadInfo = new HashMap<String, String>();
				ajaxUploadInfo.put("url",
						image.getPath() + "/" + image.getName());
			}
			// 同步生成小图片
			String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt()
					: image.getExt();
			String source = request.getSession().getServletContext()
					.getRealPath("/")
					+ image.getPath() + File.separator + image.getName();
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.configService
					.getSysConfig().getSmallWidth(), this.configService
					.getSysConfig().getSmallHeight());
			// 同步生成中等图片
			String midext = image.getExt().indexOf(".") < 0 ? "."
					+ image.getExt() : image.getExt();
			String midtarget = source + "_middle" + ext;
			CommUtil.createSmall(source, midtarget, this.configService
					.getSysConfig().getMiddleWidth(), this.configService
					.getSysConfig().getMiddleHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			if (ajaxUploadMark != null) {
				writer.print(Json.toJson(ajaxUploadInfo, JsonFormat.compact()));
			} else {
				writer.print(html5Uploadret);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 按照名称查找相册
	 * 
	 * @param request
	 * @param response
	 * @param album_name
	 */
	@SecurityMapping(title = "查找自营相册", value = "/admin/album_name.htm*", rtype = "admin", rname = "自营相册", rcode = "album_admin", rgroup = "自营")
	@RequestMapping("/admin/album_name.htm")
	public void album_name(HttpServletRequest request,
			HttpServletResponse response, String album_name) {
		String album_json = "";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		params.put("userRole2", "ADMIN_SELLER");
		params.put("album_name", "%" + album_name + "%");
		List<Album> albums = this.albumService
				.query("select obj from Album obj where (obj.user.userRole=:userRole or obj.user.userRole=:userRole2)  and obj.album_name like:album_name order by obj.album_sequence asc",
						params, -1, -1);
		List<Map> new_album = new ArrayList<Map>();
		if (albums.size() > 0) {
			for (Album album : albums) {
				Map map = new HashMap();
				map.put("id", album.getId());
				map.put("album_name", album.getAlbum_name());
				if (album.getAlbum_cover() != null) {
					map.put("img_url", CommUtil.getURL(request) + "/"
							+ album.getAlbum_cover().getPath() + "/"
							+ album.getAlbum_cover().getName() + "_small."
							+ album.getAlbum_cover().getExt());
					System.out.println(CommUtil.getURL(request) + "/"
							+ album.getAlbum_cover().getPath() + "/"
							+ album.getAlbum_cover().getName() + "_small."
							+ album.getAlbum_cover().getExt());
				} else {
					map.put("img_url",
							CommUtil.getURL(request)
									+ "/resources/style/system/front/default/images/user_photo/phone.jpg");
				}
				new_album.add(map);
			}
		}
		album_json = Json.toJson(new_album, JsonFormat.compact());
		try {
			response.getWriter().print(album_json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// 判断管理员相册是否存在，不存在测创建默认相册
		private void isAdminAlbumExist() {
			Map params = new HashMap();
			params.put("role1", "ADMIN");
			params.put("role2", "ADMIN_SELLER");
			List<Album> albums = this.albumService
					.query("select obj.id from Album obj where obj.user.userRole=:role1 or obj.user.userRole=:role2",
							params, -1, -1);
			if (albums.size() == 0) {
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(SecurityUserHolder.getCurrentUser());
				this.albumService.save(album);
			}
		}
}
