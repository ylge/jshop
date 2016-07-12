package com.iskyshop.module.app.service.impl;
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
import com.iskyshop.module.app.domain.AppPushLog;
import com.iskyshop.module.app.service.IAppPushLogService;

@Service
@Transactional
public class AppPushLogServiceImpl implements IAppPushLogService{
	@Resource(name = "appPushLogDAO")
	private IGenericDAO<AppPushLog> appPushLogDao;
	
	public boolean save(AppPushLog appPushLog) {
		/**
		 * init other field here
		 */
		try {
			this.appPushLogDao.save(appPushLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public AppPushLog getObjById(Long id) {
		AppPushLog appPushLog = this.appPushLogDao.get(id);
		if (appPushLog != null) {
			return appPushLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.appPushLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> appPushLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : appPushLogIds) {
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
		GenericPageList pList = new GenericPageList(AppPushLog.class, construct,query,
				params, this.appPushLogDao);
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
	
	public boolean update(AppPushLog appPushLog) {
		try {
			this.appPushLogDao.update( appPushLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<AppPushLog> query(String query, Map params, int begin, int max){
		return this.appPushLogDao.query(query, params, begin, max);
		
	}
}
