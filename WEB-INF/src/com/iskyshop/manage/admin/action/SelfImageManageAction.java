package com.iskyshop.manage.admin.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.ImageTools;

/**
 * 自营图片管理控制器
 * 
 * @author 沈阳网之商科技有限公司 www.iskyshop.com jxz 20140327
 * @info 平台图片管理控制器，删除数据的同时也删除物理文件
 * @since V1.0
 * 
 */
/**
 * @info 自营商品相册管理控制器
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com jxz
 */
@Controller
public class SelfImageManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ImageTools ImageTools;

	@SecurityMapping(title = "自营相册列表", value = "/admin/imageself_list.htm*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping("/admin/imageself_list.htm")
	public ModelAndView imageself_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String user_name) {
		ModelAndView mv = new JModelAndView("admin/blue/imageself_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AlbumQueryObject qo = new AlbumQueryObject(currentPage, mv, orderBy,
				orderType);
		if (user_name != null && !user_name.trim().equals("")) {
			qo.addQuery("obj.user.userName", new SysMap("user_name", "%"
					+ user_name.trim() + "%"), "like");
			mv.addObject("user_name", user_name);
		}
		Map params = new HashMap();
		params.put("role1", "ADMIN");
		params.put("role2", "ADMIN_SELLER");
		qo.addQuery("(obj.user.userRole=:role1 or obj.user.userRole=:role2)",
				params);
		qo.setPageSize(15);
		IPageList pList = this.albumService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ImageTools", ImageTools);
		return mv;
	}

	@SecurityMapping(title = "会员相册删除", value = "/admin/imageself_del.htm*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping("/admin/imageself_del.htm")
	public String imageself_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
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
						this.accessoryService.delete(acc.getId());
					}
					this.albumService.delete(Long.parseLong(id));
				}
			}
		}
		String url = "redirect:/admin/imageself_list.htm?currentPage="
				+ currentPage;
		return url;
	}

	@SecurityMapping(title = "会员相册图片列表", value = "/admin/selfpic_list.htm*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping("/admin/selfpic_list.htm")
	public ModelAndView selfpic_list(HttpServletRequest request,
			HttpServletResponse response, String aid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/selfpic_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject qo = new AccessoryQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.album.id",
				new SysMap("obj_album_id", CommUtil.null2Long(aid)), "=");
		qo.setPageSize(50);
		IPageList pList = this.accessoryService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		Album album = this.albumService.getObjById(CommUtil.null2Long(aid));
		mv.addObject("album", album);
		return mv;
	}

	/**
	 * 会员相册图片删除，删除数据的同时删除服务器上的图片资源
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "会员相册图片删除", value = "/admin/selfpic_del.htm*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping("/admin/selfpic_del.htm")
	public String selfpic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId,
			String aid) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			boolean flag = false;
			Accessory obj = this.accessoryService.getObjById(CommUtil
					.null2Long(id));
			for (Goods goods : obj.getGoods_list()) {
				if (goods.getGoods_main_photo().getId().equals(obj.getId())) {
					goods.setGoods_main_photo(null);
					this.goodsService.update(goods);
				}
				goods.getGoods_photos().remove(obj);
			}
			flag = this.accessoryService.delete(CommUtil.null2Long(id));
			if (flag) {
				CommUtil.del_acc(request, obj);
			}
		}
		String url = "redirect:/admin/selfpic_list.htm?currentPage="
				+ currentPage + "&aid=" + aid;
		return url;
	}
}
