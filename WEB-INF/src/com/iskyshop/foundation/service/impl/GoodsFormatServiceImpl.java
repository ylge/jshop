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
import com.iskyshop.foundation.domain.GoodsFormat;
import com.iskyshop.foundation.service.IGoodsFormatService;

@Service
@Transactional
public class GoodsFormatServiceImpl implements IGoodsFormatService{
	@Resource(name = "goodsFormatDAO")
	private IGenericDAO<GoodsFormat> goodsFormatDao;
	
	public boolean save(GoodsFormat goodsFormat) {
		/**
		 * init other field here
		 */
		try {
			this.goodsFormatDao.save(goodsFormat);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsFormat getObjById(Long id) {
		GoodsFormat goodsFormat = this.goodsFormatDao.get(id);
		if (goodsFormat != null) {
			return goodsFormat;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsFormatDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsFormatIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsFormatIds) {
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
		GenericPageList pList = new GenericPageList(GoodsFormat.class,construct, query,
				params, this.goodsFormatDao);
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
	
	public boolean update(GoodsFormat goodsFormat) {
		try {
			this.goodsFormatDao.update( goodsFormat);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsFormat> query(String query, Map params, int begin, int max){
		return this.goodsFormatDao.query(query, params, begin, max);
		
	}
}
