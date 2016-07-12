package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.domain.query.SubjectQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.SubjectTools;

/**
 * 
 * <p>
 * Title: SubjectViewAction.java
 * </p>
 * 
 * <p>
 * Description: 专题控制器
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
 * @date 2014-11-11
 * 
 * @version iskyshop_b2b2c 2.0
 */

@Controller
public class SubjectViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private SubjectTools SubjectTools;

	/**
	 * 专题首页,专题列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/subject/index.htm")
	public ModelAndView subject(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("subject.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SubjectQueryObject qo = new SubjectQueryObject(currentPage, mv,
				"sequence", "asc");
		qo.setPageSize(5);
		qo.addQuery("obj.display", new SysMap("display", 1), "=");
		IPageList pList = (IPageList) this.subjectService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * 专题详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/subject/view.htm")
	public ModelAndView subject_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("subject_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Subject obj = this.subjectService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getSubject_detail() != null) {
			List<Map> objs = (List<Map>) Json.fromJson(obj.getSubject_detail());
			mv.addObject("objs", objs);
		}
		mv.addObject("obj", obj);
		mv.addObject("SubjectTools", SubjectTools);
		return mv;
	}

	/**
	 * 解析前台热点坐标
	 * 
	 * @param areaInfo
	 * @param img_id
	 * @return
	 */
	@RequestMapping("/subject/getAreaInfo.htm")
	public void subject_getAreaInfo(HttpServletRequest request,
			HttpServletResponse response, String areaInfo, String img_id,
			String width) {
		Accessory img = this.accessoryService.getObjById(CommUtil
				.null2Long(img_id));
		double rate = 1;
		if (img != null && img.getWidth() > 1640) {// 判断图片宽度是否大于1640.1640为平台设置图片时显示的最大宽度，前台显示时根据实际图片宽度等比例缩放热点区域大小
			rate = CommUtil.div(img.getWidth(), 1640);
		}
		List<Map> maps = new ArrayList<Map>();
		if (areaInfo != null && !areaInfo.equals("")) {
			String infos[] = areaInfo.split("-");
			for (String obj : infos) {
				if (!obj.equals("")) {
					Map map = new HashMap();
					String detail_infos[] = obj.split("==");
					detail_infos[0] = detail_infos[0].replace("_", ",");
					String coords = detail_infos[0];
					String nums[] = detail_infos[0].split(",");
					String temp_coords = "";
					for (String num : nums) {
						String coor = CommUtil.null2String(Math.round(CommUtil
								.mul(rate, num)));
						if (temp_coords.equals("")) {
							temp_coords = coor;
						} else {
							temp_coords = temp_coords + "," + coor;
						}
					}
					if (!temp_coords.equals("")) {
						coords = temp_coords;
					}
					map.put("coords", coords);

					// 根据屏幕实际宽度缩放坐标
					int real_width = CommUtil.null2Int(width);
					if (img.getWidth() > real_width) {
						double rate2 = CommUtil.div(real_width, img.getWidth());
						String temp_real_coors[] = temp_coords.split(",");
						String real_coors = "";
						for (String real : temp_real_coors) {
							String coor = CommUtil.null2String(Math
									.round(CommUtil.mul(rate2, real)));
							if (real_coors.equals("")) {
								real_coors = coor;
							} else {
								real_coors = real_coors + "," + coor;
							}
						}
						if (!real_coors.equals("")) {
							coords = real_coors;
						}
						map.put("coords", real_coors);
						System.out.println("缩放之后坐标:" + real_coors);
					}
					map.put("url", detail_infos[1]);
					map.put("width",
							this.SubjectTools.getWidth(detail_infos[0]));
					map.put("height",
							this.SubjectTools.getHeight(detail_infos[0]));
					map.put("top", this.SubjectTools.getTop(detail_infos[0]));
					map.put("left", this.SubjectTools.getLeft(detail_infos[0]));
					maps.add(map);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(maps, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
