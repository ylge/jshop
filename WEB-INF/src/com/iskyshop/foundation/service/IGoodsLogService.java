package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoodsLog;

public interface IGoodsLogService {
	/**
	 * 保存一个GoodsLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsLog instance);
	
	/**
	 * 根据一个ID得到GoodsLog
	 * 
	 * @param id
	 * @return
	 */
	GoodsLog getObjById(Long id);
	
	/**
	 * 删除一个GoodsLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsLog
	 * 
	 * @param id
	 *            需要更新的GoodsLog的id
	 * @param dir
	 *            需要更新的GoodsLog
	 */
	boolean update(GoodsLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsLog> query(String query, Map params, int begin, int max);
}
