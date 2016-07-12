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
import com.iskyshop.foundation.domain.RechargeCard;
import com.iskyshop.foundation.service.IRechargeCardService;

@Service
@Transactional
public class RechargeCardServiceImpl implements IRechargeCardService{
	@Resource(name = "rechargeCardDAO")
	private IGenericDAO<RechargeCard> rechargeCardDao;
	
	public boolean save(RechargeCard rechargeCard) {
		/**
		 * init other field here
		 */
		try {
			this.rechargeCardDao.save(rechargeCard);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public RechargeCard getObjById(Long id) {
		RechargeCard rechargeCard = this.rechargeCardDao.get(id);
		if (rechargeCard != null) {
			return rechargeCard;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.rechargeCardDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> rechargeCardIds) {
		// TODO Auto-generated method stub
		for (Serializable id : rechargeCardIds) {
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
		GenericPageList pList = new GenericPageList(RechargeCard.class,construct, query,
				params, this.rechargeCardDao);
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
	
	public boolean update(RechargeCard rechargeCard) {
		try {
			this.rechargeCardDao.update( rechargeCard);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<RechargeCard> query(String query, Map params, int begin, int max){
		return this.rechargeCardDao.query(query, params, begin, max);
		
	}
}
