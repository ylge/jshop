package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.FreeApplyLog;

public interface IFreeApplyLogService {
	/**
	 * 保存一个FreeApplyLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(FreeApplyLog instance);
	
	/**
	 * 根据一个ID得到FreeApplyLog
	 * 
	 * @param id
	 * @return
	 */
	FreeApplyLog getObjById(Long id);
	
	/**
	 * 删除一个FreeApplyLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除FreeApplyLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到FreeApplyLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个FreeApplyLog
	 * 
	 * @param id
	 *            需要更新的FreeApplyLog的id
	 * @param dir
	 *            需要更新的FreeApplyLog
	 */
	boolean update(FreeApplyLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<FreeApplyLog> query(String query, Map params, int begin, int max);
}
