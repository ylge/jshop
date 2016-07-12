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
import com.iskyshop.foundation.domain.StoreAdjustInfo;
import com.iskyshop.foundation.service.IStoreAdjustInfoService;

@Service
@Transactional
public class StoreAdjustInfoServiceImpl implements IStoreAdjustInfoService{
	@Resource(name = "storeAdjustInfoDAO")
	private IGenericDAO<StoreAdjustInfo> storeAdjustInfoDao;
	
	public boolean save(StoreAdjustInfo storeAdjustInfo) {
		/**
		 * init other field here
		 */
		try {
			this.storeAdjustInfoDao.save(storeAdjustInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public StoreAdjustInfo getObjById(Long id) {
		StoreAdjustInfo storeAdjustInfo = this.storeAdjustInfoDao.get(id);
		if (storeAdjustInfo != null) {
			return storeAdjustInfo;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeAdjustInfoDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeAdjustInfoIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeAdjustInfoIds) {
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
		GenericPageList pList = new GenericPageList(StoreAdjustInfo.class, construct,query,
				params, this.storeAdjustInfoDao);
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
	
	public boolean update(StoreAdjustInfo storeAdjustInfo) {
		try {
			this.storeAdjustInfoDao.update( storeAdjustInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<StoreAdjustInfo> query(String query, Map params, int begin, int max){
		return this.storeAdjustInfoDao.query(query, params, begin, max);
		
	}
}
