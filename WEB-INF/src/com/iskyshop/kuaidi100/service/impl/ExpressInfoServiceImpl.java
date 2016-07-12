package com.iskyshop.kuaidi100.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.service.IExpressInfoService;

@Service
@Transactional
public class ExpressInfoServiceImpl implements IExpressInfoService {
	@Resource(name = "expressInfoDAO")
	private IGenericDAO<ExpressInfo> expressInfoDao;

	public boolean save(ExpressInfo expressInfo) {
		/**
		 * init other field here
		 */
		try {
			this.expressInfoDao.save(expressInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ExpressInfo getObjById(Long id) {
		ExpressInfo expressInfo = this.expressInfoDao.get(id);
		if (expressInfo != null) {
			return expressInfo;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.expressInfoDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> expressInfoIds) {
		// TODO Auto-generated method stub
		for (Serializable id : expressInfoIds) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(ExpressInfo.class,construct, query,
				params, this.expressInfoDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(ExpressInfo expressInfo) {
		try {
			this.expressInfoDao.update(expressInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ExpressInfo> query(String query, Map params, int begin, int max) {
		return this.expressInfoDao.query(query, params, begin, max);

	}

	@Override
	public ExpressInfo getObjByPropertyWithType(String propertyName,
			Object value, int order_type) {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		params.put("order_type", order_type);
		params.put("value", value);
		List<ExpressInfo> eis = this.expressInfoDao.query(
				"select obj from ExpressInfo obj where obj.order_type=:order_type and obj."
						+ propertyName + "=:value", params, -1, -1);
		if (eis.size() > 0) {
			return eis.get(0);
		} else
			return null;
	}
}
