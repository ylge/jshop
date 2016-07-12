package com.iskyshop.module.weixin.manage.buyer.action;

import java.util.Date;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupClassQueryObject;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
/**
 * 
 * 
 * 
 * 
* <p>Title: WapUserGroupInfoAction.java</p>

* <p>Description: wap端用户中心0元试用</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author zw

* @date 2015年1月6日

* @version b2b2c_2015
 */
@Controller
public class WeixinUserFreeAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeApplyLogService applyLogService;
	@Autowired
	private IFreeGoodsService freeGoodsService;

	/**
	 * 移动端户中心团购列表
	 * @param request
	 * @param response
	 * @param status
	 * @param begin_num
	 * @param count_num
	 * @return
	 */
	@SecurityMapping(title = "移动端户中心0元试用列表", value = "/wap/buyer/freeapply_list.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping("/wap/buyer/freeapply_list.htm")
	public ModelAndView freeapply_list(HttpServletRequest request,
			HttpServletResponse response, String status,String begin_num,String count_num,String apply_status,String evaluate_status) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/freeapply_list.html",
				configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map map = new HashMap();
		map.put("user_id", user.getId());
		String sql ="";
		if(!CommUtil.null2String(apply_status).equals("0")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", 0);
		}
		if(!CommUtil.null2String(apply_status).equals("5")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", 5);
		}
		if(!CommUtil.null2String(apply_status).equals("-5")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", -5);
		}
		if(!CommUtil.null2String(evaluate_status).equals("0")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 0);
		}
		if(!CommUtil.null2String(evaluate_status).equals("1")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 1);
		}
		if(!CommUtil.null2String(evaluate_status).equals("2")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 2);
		}
		sql = sql + " order by obj.addTime desc";
		List<FreeApplyLog> applyLogs = this.applyLogService.query("select obj from FreeApplyLog obj where obj.user_id=:user_id"+sql, map, -1, 12);
		mv.addObject("applyLogs", applyLogs);
		mv.addObject("this", this);
		mv.addObject("apply_status", apply_status);
		mv.addObject("evaluate_status", evaluate_status);
		return mv;			
	}
	@RequestMapping("/wap/buyer/freeapply_data.htm")
	public ModelAndView freeapply_data(HttpServletRequest request,
			HttpServletResponse response, String status,String begin_num,String count_num,String apply_status,String evaluate_status,String begin_count) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/freeapply_data.html",
				configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map map = new HashMap();
		map.put("user_id", user.getId());
		String sql ="";
		if(!CommUtil.null2String(apply_status).equals("0")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", 0);
		}
		if(!CommUtil.null2String(apply_status).equals("5")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", 5);
		}
		if(!CommUtil.null2String(apply_status).equals("-5")){
			sql = sql + " obj.apply_status=:apply_status";
			map.put("apply_status", -5);
		}
		if(!CommUtil.null2String(evaluate_status).equals("0")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 0);
		}
		if(!CommUtil.null2String(evaluate_status).equals("1")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 1);
		}
		if(!CommUtil.null2String(evaluate_status).equals("2")){
			sql = sql + " obj.evaluate_status=:evaluate_status";
			map.put("evaluate_status", 2);
		}
		sql = sql + " order by obj.addTime desc";
		List<FreeApplyLog> applyLogs = this.applyLogService.query("select obj from FreeApplyLog obj where obj.user_id=:user_id"+sql, map, CommUtil.null2Int(begin_count), 12);
		mv.addObject("applyLogs", applyLogs);
		mv.addObject("this", this);
		mv.addObject("apply_status", apply_status);
		mv.addObject("evaluate_status", evaluate_status);
		return mv;			
	}
	@SecurityMapping(title = "移动端户中心0元试用详细", value = "/wap/buyer/freeapply_detail.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping("/wap/buyer/freeapply_detail.htm")
	public ModelAndView freeapply_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/freeapply_detail.html",
				configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		if(!CommUtil.null2String(id).equals("")){
			FreeApplyLog applyLog = this.applyLogService.getObjById(CommUtil.null2Long(id));
			mv.addObject("applyLog", applyLog);
			if(applyLog!=null){
				FreeGoods freeGoods = this.freeGoodsService.getObjById(applyLog.getFreegoods_id());
				mv.addObject("freeGoods", freeGoods);
			}
		}
		return mv;
	}
	@SecurityMapping(title = "移动端户中心0元试用评论保存", value = "/wap/buyer/freeapply_detail.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping("/wap/buyer/freeapply_save.htm")
	public ModelAndView freeapply_detail(HttpServletRequest request,
			HttpServletResponse response, String id,String use_experience) {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),this.userConfigService.getUserConfig(), 1, request, response);
		if(!CommUtil.null2String(id).equals("")){
			FreeApplyLog applyLog = this.applyLogService.getObjById(CommUtil.null2Long(id));
			if(applyLog.getEvaluate_status()==1){
				applyLog.setUse_experience(use_experience);
				applyLog.setEvaluate_time(new Date());
				applyLog.setEvaluate_status(2);
				this.applyLogService.update(applyLog);
				mv.addObject("op_title", "0元试用评论成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/wap/buyer/freeapply_list.htm");
			}else{
				mv = new JModelAndView("wap/error.html",
						configService.getSysConfig(),this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "参数错误！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/wap/buyer/freeapply_list.htm");
			}
		}else{
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/user/wap/buyer/freeapply_list.htm");
		}
		return mv;
	}
	public FreeGoods queryFreeGoods(String id){
		FreeGoods freeGoods = this.freeGoodsService.getObjById(CommUtil.null2Long(id));
		return freeGoods;
	}
}
