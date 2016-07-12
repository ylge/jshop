package com.iskyshop.module.sns.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class SnsAttentionQueryObject extends QueryObject {
	public SnsAttentionQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public SnsAttentionQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
