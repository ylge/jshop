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
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.service.ISnsAttentionService;


@Service
@Transactional
public class SnsAttentionServiceImpl implements ISnsAttentionService{
	@Resource(name = "snsAttentionDAO")
	private IGenericDAO<SnsAttention> snsAttentionDao;
	
	public boolean save(SnsAttention snsAttention) {
		/**
		 * init other field here
		 */
		try {
			this.snsAttentionDao.save(snsAttention);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public SnsAttention getObjById(Long id) {
		SnsAttention snsAttention = this.snsAttentionDao.get(id);
		if (snsAttention != null) {
			return snsAttention;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.snsAttentionDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> snsAttentionIds) {
		// TODO Auto-generated method stub
		for (Serializable id : snsAttentionIds) {
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
		GenericPageList pList = new GenericPageList(SnsAttention.class,construct, query,
				params, this.snsAttentionDao);
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
	
	public boolean update(SnsAttention snsAttention) {
		try {
			this.snsAttentionDao.update( snsAttention);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<SnsAttention> query(String query, Map params, int begin, int max){
		return this.snsAttentionDao.query(query, params, begin, max);
		
	}
}
