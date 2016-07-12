package com.iskyshop.module.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.app.domain.AppPushLog;

public interface IAppPushLogService {
	/**
	 * 保存一个AppPushLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(AppPushLog instance);
	
	/**
	 * 根据一个ID得到AppPushLog
	 * 
	 * @param id
	 * @return
	 */
	AppPushLog getObjById(Long id);
	
	/**
	 * 删除一个AppPushLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除AppPushLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到AppPushLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个AppPushLog
	 * 
	 * @param id
	 *            需要更新的AppPushLog的id
	 * @param dir
	 *            需要更新的AppPushLog
	 */
	boolean update(AppPushLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<AppPushLog> query(String query, Map params, int begin, int max);
}
