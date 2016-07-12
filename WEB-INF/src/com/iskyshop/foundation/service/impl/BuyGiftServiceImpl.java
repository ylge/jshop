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
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.service.IBuyGiftService;

@Service
@Transactional
public class BuyGiftServiceImpl implements IBuyGiftService {
	@Resource(name = "buyGiftDAO")
	private IGenericDAO<BuyGift> buyGiftDao;

	public boolean save(BuyGift buyGift) {
		/**
		 * init other field here
		 */
		try {
			this.buyGiftDao.save(buyGift);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public BuyGift getObjById(Long id) {
		BuyGift buyGift = this.buyGiftDao.get(id);
		if (buyGift != null) {
			return buyGift;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.buyGiftDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> buyGiftIds) {
		// TODO Auto-generated method stub
		for (Serializable id : buyGiftIds) {
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
		GenericPageList pList = new GenericPageList(BuyGift.class, construct,
				query, params, this.buyGiftDao);
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

	public boolean update(BuyGift buyGift) {
		try {
			this.buyGiftDao.update(buyGift);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<BuyGift> query(String query, Map params, int begin, int max) {
		return this.buyGiftDao.query(query, params, begin, max);

	}
}
