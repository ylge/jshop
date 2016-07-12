package com.iskyshop.module.weixin.view.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.view.web.tools.GoodsViewTools;
/**
 * 
 * 
* <p>Title:WapClassViewAction.java</p>

* <p>Description:移动端商城分类 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jinxinzhe

* @date 2014年8月20日

* @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinClassViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private GoodsViewTools goodsViewTools;

	/**
	 * 手机端商城分类请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/wap/goodsclass.htm")
	public ModelAndView goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		List<GoodsClass> gcs = null;
		if (id == null || id.equals("")) {
			mv = new JModelAndView("wap/goods_class.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			Map params = new HashMap();
			params.put("display", true);
			gcs = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("gcs",gcs);
		} else {
			mv = new JModelAndView("wap/goods_class1.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			GoodsClass parent = this.goodsClassService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("gcs",parent.getChilds());
		}
		return mv;
	}
	
	/**
	 * 根据商城分类查看商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/wap/class_goods.htm")
	public ModelAndView class_goods(HttpServletRequest request,
			HttpServletResponse response, String gc_id,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("wap/class_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_salenum";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		List<Goods> goods_list = null;
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("gc_id", CommUtil.null2Long(gc_id));
		if (orderBy.equals("goods_collect")) {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.id =:gc_id order by obj.goods_collect desc",
							params,0,12);
		}
		if (orderBy.equals("goods_salenum")) {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.id =:gc_id order by obj.goods_salenum desc",
							params,0, 12);
		}
		if (orderBy.equals("store_price")) {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.id =:gc_id order by obj.store_price " + orderType,
							params,0, 12);
		}
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id)).getParent();
		mv.addObject("gc",gc);
		mv.addObject("objs",goods_list);
		mv.addObject("gc_id", gc_id);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;  
	}	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param queryType
	 * @return
	 */
	@RequestMapping("/wap/class_goods_ajax.htm")
	public ModelAndView class_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count,
			String orderBy,String orderType,String gc_id,String store_id,String type) {
		ModelAndView mv = new JModelAndView("wap/class_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		String sql = "";
		if(!CommUtil.null2String(gc_id).equals("")){
			params.put("gc_id", CommUtil.null2Long(gc_id));
			mv.addObject("gc_id", gc_id);
			sql = " and obj.gc.id =:gc_id";
		}
		if(!CommUtil.null2String(store_id).equals("")){
			mv.addObject("store_id", store_id);
			params.put("store_id", CommUtil.null2Long(store_id));
			sql = sql + " and obj.goods_store.id =:store_id";
		}
		if(CommUtil.null2String(type).equals("mobile_hot")){
			mv.addObject("type", type);
			params.put("mobile_hot", 1);
			sql = sql + " and obj.mobile_hot=:mobile_hot";
		}
		if(CommUtil.null2String(type).equals("mobile_hot")){
			mv.addObject("type", type);
			params.put("mobile_recommend", 1);
			sql = sql + " and obj.mobile_recommend=:mobile_recommend";
		}
		List<Goods> goods_list = null;
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if (orderBy.equals("goods_collect")) {
			sql = sql + " order by obj.goods_collect desc";
		}
		if (orderBy.equals("goods_salenum")) {
			sql = sql + " order by obj.goods_salenum desc";
		}
		if (orderBy.equals("store_price")) {
			sql = sql + " order by obj.store_price "+orderType;
		}
		params.put("goods_status", 0);
		goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_status=:goods_status " + sql,
						params, CommUtil.null2Int(begin_count), 6);
		mv.addObject("objs", goods_list);
		return mv;
	}
}
