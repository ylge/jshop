package com.iskyshop.foundation.service.impl;

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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IGoodsService;

@Service
@Transactional
public class GoodsServiceImpl implements IGoodsService {
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDao;

	public boolean save(Goods goods) {
		/**
		 * init other field here
		 */
		try {
			this.goodsDao.save(goods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Goods getObjById(Long id) {
		Goods goods = this.goodsDao.get(id);
		if (goods != null) {
			return goods;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.goodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> goodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsIds) {
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
		GenericPageList pList = new GenericPageList(Goods.class, construct,
				query, params, this.goodsDao);
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

	public boolean update(Goods goods) {
		try {
			
			this.goodsDao.update(goods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Goods> query(String query, Map params, int begin, int max) {
		return this.goodsDao.query(query, params, begin, max);

	}

	@Override
	public Goods getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		return this.goodsDao.getBy(construct, propertyName, value);
	}
}
