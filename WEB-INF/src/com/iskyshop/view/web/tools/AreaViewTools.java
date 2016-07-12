package com.iskyshop.view.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.service.IAreaService;

/**
 * 
 * <p>
 * Title: AreaViewTools.java
 * </p>
 * 
 * <p>
 * Description:区域工具类,根据id生成完整的区域信息
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
 * @date 2014-11-10
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class AreaViewTools {
	@Autowired
	private IAreaService areaService;

	/**
	 * 根据区域生成区域信息字符串
	 * 
	 * @param area
	 * @return
	 */
	public String generic_area_info(String area_id) {
		String area_info = "";
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		if (area != null) {
			String last = area.getAreaName();
			String second = this.getSecond_Area_info(area_id);
			String first = this.getFirst_Area_info(area_id);
			area_info = first + second + last;
		}
		return area_info;
	}

	private String getSecond_Area_info(String area_id) {
		String areaName = "";
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		if (area != null && area.getParent() != null) {
			areaName = area.getParent().getAreaName();
		}
		return areaName;
	}

	private String getFirst_Area_info(String area_id) {
		String areaName = "";
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		if (area != null && area.getParent() != null) {
			areaName = area.getParent().getParent().getAreaName();
		}
		return areaName;
	}

}
