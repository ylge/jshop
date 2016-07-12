package com.iskyshop.module.sns.service.impl;
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
import com.iskyshop.module.sns.domain.UserDynamic;
import com.iskyshop.module.sns.service.IUserDynamicService;

@Service
@Transactional
public class UserDynamicServiceImpl implements IUserDynamicService{
	@Resource(name = "userDynamicDAO")
	private IGenericDAO<UserDynamic> userDynamicDao;
	
	public boolean save(UserDynamic userDynamic) {
		/**
		 * init other field here
		 */
		try {
			this.userDynamicDao.save(userDynamic);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public UserDynamic getObjById(Long id) {
		UserDynamic userDynamic = this.userDynamicDao.get(id);
		if (userDynamic != null) {
			return userDynamic;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.userDynamicDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> userDynamicIds) {
		// TODO Auto-generated method stub
		for (Serializable id : userDynamicIds) {
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
		GenericPageList pList = new GenericPageList(UserDynamic.class, construct,query,
				params, this.userDynamicDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(UserDynamic userDynamic) {
		try {
			this.userDynamicDao.update( userDynamic);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<UserDynamic> query(String query, Map params, int begin, int max){
		return this.userDynamicDao.query(query, params, begin, max);
		
	}
}
