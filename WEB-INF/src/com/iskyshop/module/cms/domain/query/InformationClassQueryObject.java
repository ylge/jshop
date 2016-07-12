package com.iskyshop.module.cms.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class InformationClassQueryObject extends QueryObject {
	public InformationClassQueryObject(String construct, String currentPage,
			ModelAndView mv, String orderBy, String orderType) {
		super(construct,currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	public InformationClassQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InformationClassQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	
}
