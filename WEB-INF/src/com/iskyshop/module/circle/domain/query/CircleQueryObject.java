package com.iskyshop.module.circle.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class CircleQueryObject extends QueryObject {
	public CircleQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public CircleQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
