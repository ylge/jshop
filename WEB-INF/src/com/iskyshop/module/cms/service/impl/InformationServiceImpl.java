package com.iskyshop.module.cms.service.impl;
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
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.service.IInformationService;

@Service
@Transactional
public class InformationServiceImpl implements IInformationService{
	@Resource(name = "informationDAO")
	private IGenericDAO<Information> informationDao;
	
	public boolean save(Information information) {
		/**
		 * init other field here
		 */
		try {
			this.informationDao.save(information);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Information getObjById(Long id) {
		Information information = this.informationDao.get(id);
		if (information != null) {
			return information;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.informationDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> informationIds) {
		// TODO Auto-generated method stub
		for (Serializable id : informationIds) {
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
		GenericPageList pList = new GenericPageList(Information.class,construct, query,
				params, this.informationDao);
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
	
	public boolean update(Information information) {
		try {
			this.informationDao.update( information);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Information> query(String query, Map params, int begin, int max){
		return this.informationDao.query(query, params, begin, max);
		
	}
}
