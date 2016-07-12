package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoodsCase;

public interface IGoodsCaseService {
	/**
	 * 保存一个GoodsCase，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsCase instance);
	
	/**
	 * 根据一个ID得到GoodsCase
	 * 
	 * @param id
	 * @return
	 */
	GoodsCase getObjById(Long id);
	
	/**
	 * 删除一个GoodsCase
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsCase
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsCase
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsCase
	 * 
	 * @param id
	 *            需要更新的GoodsCase的id
	 * @param dir
	 *            需要更新的GoodsCase
	 */
	boolean update(GoodsCase instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsCase> query(String query, Map params, int begin, int max);
}
