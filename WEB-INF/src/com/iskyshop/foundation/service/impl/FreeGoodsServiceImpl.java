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
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.service.IFreeGoodsService;

@Service
@Transactional
public class FreeGoodsServiceImpl implements IFreeGoodsService {
	@Resource(name = "freeGoodsDAO")
	private IGenericDAO<FreeGoods> freeGoodsDao;

	public boolean save(FreeGoods freeGoods) {
		/**
		 * init other field here
		 */
		try {
			this.freeGoodsDao.save(freeGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public FreeGoods getObjById(Long id) {
		FreeGoods freeGoods = this.freeGoodsDao.get(id);
		if (freeGoods != null) {
			return freeGoods;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.freeGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> freeGoodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : freeGoodsIds) {
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
		GenericPageList pList = new GenericPageList(FreeGoods.class, construct,
				query, params, this.freeGoodsDao);
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

	public boolean update(FreeGoods freeGoods) {
		try {
			this.freeGoodsDao.update(freeGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<FreeGoods> query(String query, Map params, int begin, int max) {
		return this.freeGoodsDao.query(query, params, begin, max);

	}
}
