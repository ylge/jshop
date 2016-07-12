package com.iskyshop.module.app.service.impl;
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
import com.iskyshop.module.app.domain.QRLogin;
import com.iskyshop.module.app.service.IQRLoginService;

@Service
@Transactional
public class QRLoginServiceImpl implements IQRLoginService{
	@Resource(name = "qRLoginDAO")
	private IGenericDAO<QRLogin> qRLoginDao;
	
	public boolean save(QRLogin qRLogin) {
		/**
		 * init other field here
		 */
		try {
			this.qRLoginDao.save(qRLogin);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public QRLogin getObjById(Long id) {
		QRLogin qRLogin = this.qRLoginDao.get(id);
		if (qRLogin != null) {
			return qRLogin;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.qRLoginDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> qRLoginIds) {
		// TODO Auto-generated method stub
		for (Serializable id : qRLoginIds) {
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
		GenericPageList pList = new GenericPageList(QRLogin.class, construct,query,
				params, this.qRLoginDao);
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
	
	public boolean update(QRLogin qRLogin) {
		try {
			this.qRLoginDao.update( qRLogin);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<QRLogin> query(String query, Map params, int begin, int max){
		return this.qRLoginDao.query(query, params, begin, max);
		
	}
}
