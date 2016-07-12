package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.SalesLog;

public interface ISalesLogService {
	/**
	 * 保存一个SalesLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SalesLog instance);
	
	/**
	 * 根据一个ID得到SalesLog
	 * 
	 * @param id
	 * @return
	 */
	SalesLog getObjById(Long id);
	
	/**
	 * 删除一个SalesLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SalesLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SalesLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SalesLog
	 * 
	 * @param id
	 *            需要更新的SalesLog的id
	 * @param dir
	 *            需要更新的SalesLog
	 */
	boolean update(SalesLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SalesLog> query(String query, Map params, int begin, int max);
}
