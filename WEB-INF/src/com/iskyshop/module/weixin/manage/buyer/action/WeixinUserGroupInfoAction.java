package com.iskyshop.module.weixin.manage.buyer.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
/**
 * 
 * 
 * 
 * 
* <p>Title: WapUserGroupInfoAction.java</p>

* <p>Description: wap端用户中心团购</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author zw

* @date 2015年1月6日

* @version b2b2c_2015
 */
@Controller
public class WeixinUserGroupInfoAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupInfoService groupInfoService;
	/**
	 * 移动端户中心团购列表
	 * @param request
	 * @param response
	 * @param status
	 * @param begin_num
	 * @param count_num
	 * @return
	 */
	@SecurityMapping(title = "移动端户中心团购列表", value = "/wap/buyer/groupinfo.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心团购")
	@RequestMapping("/wap/buyer/groupinfo.htm")
	public ModelAndView groupinfo(HttpServletRequest request,
			HttpServletResponse response, String status,String begin_num,String count_num) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/groupinfo.html",
				configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		String sql = " ";
		Map map = new HashMap();
		if(!CommUtil.null2String(status).equals("")){
			if(CommUtil.null2String(status).equals("357")){
				Set set=new TreeSet();
				set.add(3);
				set.add(5);
				set.add(7);
				map.put("ids", set);
				
				sql = sql +" and obj.status in (:ids)";
			}else{
				sql = sql +" and  obj.status=:status";
				map.put("status", CommUtil.null2Int(status));
			}
		}
		mv.addObject("status", status);
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		int begin = 1;
		int count = 6;
		if(!CommUtil.null2String(begin_num).equals("")){
			begin = CommUtil.null2Int(begin_num);
		}
		if(begin!=1){
			mv = new JModelAndView("user/wap/usercenter/groupinfo_data.html",
					configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		}
		List<GroupInfo> groupInfos = this.groupInfoService.query("select obj from GroupInfo obj where obj.user_id=:user_id"+sql, map, begin, 12);
		mv.addObject("objs", groupInfos);
		return mv;			
	}
}
