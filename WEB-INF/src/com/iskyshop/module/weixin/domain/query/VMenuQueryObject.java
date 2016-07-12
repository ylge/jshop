package com.iskyshop.module.weixin.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class VMenuQueryObject extends QueryObject {
	public VMenuQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public VMenuQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
