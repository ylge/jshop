package com.iskyshop.module.sns.service.impl;

import java.io.Serializable;
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
import com.iskyshop.module.sns.domain.UserShare;
import com.iskyshop.module.sns.service.IUserShareService;

@Service
@Transactional
public class UserShareServiceImpl implements IUserShareService {
	@Resource(name = "userShareDAO")
	private IGenericDAO<UserShare> userShareDao;

	public boolean save(UserShare userShare) {
		/**
		 * init other field here
		 */
		try {
			this.userShareDao.save(userShare);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public UserShare getObjById(Long id) {
		UserShare userShare = this.userShareDao.get(id);
		if (userShare != null) {
			return userShare;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.userShareDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> userShareIds) {
		// TODO Auto-generated method stub
		for (Serializable id : userShareIds) {
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
		GenericPageList pList = new GenericPageList(UserShare.class, construct,
				query, params, this.userShareDao);
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

	public boolean update(UserShare userShare) {
		try {
			this.userShareDao.update(userShare);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<UserShare> query(String query, Map params, int begin, int max) {
		return this.userShareDao.query(query, params, begin, max);

	}
}
