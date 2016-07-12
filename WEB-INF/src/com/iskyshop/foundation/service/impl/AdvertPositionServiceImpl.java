package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;

@Service
@Transactional
public class AdvertPositionServiceImpl implements IAdvertPositionService {
	@Resource(name = "advertPositionDAO")
	private IGenericDAO<AdvertPosition> advertPositionDao;

	public boolean save(AdvertPosition advertPosition) {
		/**
		 * init other field here
		 */
		try {
			this.advertPositionDao.save(advertPosition);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public AdvertPosition getObjById(Long id) {
		AdvertPosition advertPosition = this.advertPositionDao.get(id);
		if (advertPosition != null) {
			return advertPosition;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.advertPositionDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> advertPositionIds) {
		// TODO Auto-generated method stub
		for (Serializable id : advertPositionIds) {
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
		GenericPageList pList = new GenericPageList(AdvertPosition.class,
				construct, query, params, this.advertPositionDao);
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

	public boolean update(AdvertPosition advertPosition) {
		try {
			this.advertPositionDao.update(advertPosition);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<AdvertPosition> query(String query, Map params, int begin,
			int max) {
		return this.advertPositionDao.query(query, params, begin, max);

	}
}
