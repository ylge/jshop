package com.iskyshop.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
 * @info 卖家相册中心管理控制器
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class AlbumSellerAction {
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
	private StoreTools storeTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ImageTools ImageTools;

	@SecurityMapping(title = "相册列表", value = "/seller/album.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album.htm")
	public ModelAndView album(HttpServletRequest request,
			HttpServletResponse response, String currentPage,String album_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		AlbumQueryObject aqo = new AlbumQueryObject();
		if(!"".equals(album_name)&&album_name!=null){
			aqo.addQuery("obj.album_name", new SysMap("album_name","%"+album_name+"%"), "like");
			mv.addObject("album_name", album_name);
		}
		aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setOrderBy("album_sequence");
		aqo.setOrderType("asc");
		IPageList pList = this.albumService.list(aqo);
		String url = this.configService.getSysConfig().getAddress();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0;
		if (user.getStore().getGrade().getSpaceSize() > 0) {
			remainSpace = CommUtil.div(user.getStore().getGrade()
					.getSpaceSize()
					* 1024 - csize, 1024);
			mv.addObject("remainSpace", remainSpace);
		}
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/seller/album.htm", "", "",
				pList, mv);
		mv.addObject("albumViewTools", albumViewTools);
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Album> albums = this.albumService
				.query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("albums", albums);
		//处理上传格式
		String[] strs =  this.configService.getSysConfig().getImageSuffix().split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("."+str+",");
		}
		mv.addObject("imageSuffix1", sb);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "新增相册", value = "/seller/album_add.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_add.htm")
	public ModelAndView album_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "新增相册", value = "/seller/album_edit.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_edit.htm")
	public ModelAndView album_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,String album_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album obj = this.albumService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_name1", album_name);
		return mv;
	}

	@SecurityMapping(title = "相册保存", value = "/seller/album_save.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_save.htm")
	public void album_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		WebForm wf = new WebForm();
		Album album = null;
		if (id.equals("")) {
			album = wf.toPo(request, Album.class);
			album.setAddTime(new Date());
		} else {
			Album obj = this.albumService.getObjById(Long.parseLong(id));
			album = (Album) wf.toPo(request, obj);
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		album.setUser(user);
		boolean ret = true;
		if (id.equals("")) {
			ret = this.albumService.save(album);
		} else{
			ret = this.albumService.update(album);
		}
		try {
			response.getWriter().print("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "图片上传", value = "/seller/album_upload.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_upload.htm")
	public ModelAndView album_upload(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album_upload.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Album> objs = this.albumService
				.query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
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

	@SecurityMapping(title = "相册删除", value = "/seller/album_del.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_del.htm")
	public String album_del(HttpServletRequest request, String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
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
						//如果该图片为相册封面
						if (acc.getAlbum().getAlbum_cover()!=null) {
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
		return "redirect:album.htm?currentPage="+currentPage;
	}

	@SecurityMapping(title = "相册封面设置", value = "/seller/album_cover.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_cover.htm")
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

	@SecurityMapping(title = "相册转移", value = "/seller/album_transfer.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_transfer.htm")
	public ModelAndView album_transfer(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id,
			String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album_transfer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Album> objs = this.albumService
				.query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_id", album_id);
		mv.addObject("mulitId", id);
		return mv;
	}

	@SecurityMapping(title = "图片转移相册", value = "/seller/album_transfer_save.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_transfer_save.htm")
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

	@SecurityMapping(title = "图片列表", value = "/seller/album_image.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_image.htm")
	public ModelAndView album_image(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/album_image.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album album = this.albumService.getObjById(Long.parseLong(id));
		AccessoryQueryObject aqo = new AccessoryQueryObject();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id != null && !id.equals("")) {
			aqo.addQuery("obj.album.id",
					new SysMap("album_id", CommUtil.null2Long(id)), "=");
		} else {
			aqo.addQuery("obj.album.id is null", null);
		}
		aqo.addQuery("obj.album.user.id", new SysMap("user_id", user.getId()),
				"=");
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setPageSize(16);
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0;
		if (user.getStore().getGrade().getSpaceSize() > 0) {
			remainSpace = CommUtil.div(user.getStore().getGrade()
					.getSpaceSize()
					* 1024 - csize, 1024);
			mv.addObject("remainSpace", remainSpace);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/seller/album_image.htm",
				"", "&id=" + id, pList, mv);
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<Album> albums = this.albumService
				.query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc",
						params, -1, -1);
		mv.addObject("albums", albums);
		mv.addObject("album", album);
		return mv;
	}

	@SecurityMapping(title = "图片幻灯查看", value = "/seller/image_slide.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/image_slide.htm")
	public ModelAndView image_slide(HttpServletRequest request,
			HttpServletResponse response, String album_id, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/image_slide.html",
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

	@SecurityMapping(title = "相册内图片删除", value = "/seller/album_img_del.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_img_del.htm")
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
				this.accessoryService.delete(acc.getId());
			}
		}
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "图片添加水印", value = "/seller/album_watermark.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_watermark.htm")
	public String album_watermark(HttpServletRequest request, String mulitId,
			String album_id, String to_album_id, String currentPage) {
		Long store_id = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore() != null) {
			store_id = user.getStore().getId();
		}
		if (store_id != null) {
			WaterMark waterMark = this.waterMarkService.getObjByProperty(null,
					"store.id", store_id);
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
		}
		return "redirect:album_image.htm?id=" + album_id + "&currentPage="
				+ currentPage;
	}
	
	/**
	 * 商家相册图片上传
	 * @param request
	 * @param response
	 * @param album_id 相册id
	 * @param ajaxUploadMark 上传类型标识
	 */
	@SecurityMapping(title = "相册图片上传", value = "/seller/album_image_upload.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_image_upload.htm")
	public void album_image_upload(HttpServletRequest request,
			HttpServletResponse response,String album_id,String ajaxUploadMark) {
		Boolean html5Uploadret = false;
		Map ajaxUploadInfo = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());;
		user = user.getParent() == null ? user : user.getParent();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		String url = this.storeTools.createUserFolderURL(user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double img_remain_size = 0;
		if (user.getStore().getGrade().getSpaceSize() > 0) {
			img_remain_size = CommUtil.div(user.getStore().getGrade()
					.getSpaceSize()
					* 1024 - csize, 1024);
		}
		if (img_remain_size >= 0) {
			try {
				Map map = CommUtil.saveFileToServer(request, "fileImage", path,
						null, null);
				Map params = new HashMap();
				params.put("store_id", user.getStore().getId());
				List<WaterMark> wms = this.waterMarkService
						.query("select obj from WaterMark obj where obj.store.id=:store_id",
								params, -1, -1);
				if (wms.size() > 0) {
					WaterMark mark = wms.get(0);
					if (mark.isWm_image_open()) {
						String pressImg = request.getSession()
								.getServletContext().getRealPath("")
								+ File.separator
								+ mark.getWm_image().getPath()
								+ File.separator + mark.getWm_image().getName();
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos,
								alpha);
					}
					if (mark.isWm_text_open()) {
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_text_pos();
						String text = mark.getWm_text();
						String markContentColor = mark.getWm_text_color();
						CommUtil.waterMarkWithText(targetImg, targetImg, text,
								markContentColor,
								new Font(mark.getWm_text_font(), Font.BOLD,
										mark.getWm_text_font_size()), pos, 100f);
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
						album.setAlbum_name("默认相册");
						album.setAlbum_sequence(-10000);
						album.setAlbum_default(true);
						this.albumService.save(album);
					}
				}
				image.setAlbum(album);
				html5Uploadret = this.accessoryService.save(image);
				if(html5Uploadret&&ajaxUploadMark!=null){
					ajaxUploadInfo = new HashMap<String,String>();
					ajaxUploadInfo.put("url", image.getPath()+"/"+image.getName());
				}
				// 同步生成小图片
				String ext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
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
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			if(ajaxUploadMark!=null){
				writer.print(Json.toJson(ajaxUploadInfo, JsonFormat.compact()));
			}else{
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
	@SecurityMapping(title = "搜索图片相册", value = "/seller/album_name.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/album_name.htm")
	public void album_name(HttpServletRequest request,
			HttpServletResponse response, String album_name) {
		String album_json = "";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		Map params = new HashMap();
		params.put("user_id",SecurityUserHolder.getCurrentUser().getId());
		params.put("album_name", "%" + album_name + "%");
		List<Album> albums = this.albumService
				.query("select obj from Album obj where obj.user.id=:user_id  and obj.album_name like:album_name order by obj.album_sequence asc",
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
}
