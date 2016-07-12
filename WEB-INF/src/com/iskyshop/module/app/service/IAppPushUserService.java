package com.iskyshop.module.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.app.domain.AppPushUser;

public interface IAppPushUserService {
	/**
	 * 保存一个AppPushUser，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(AppPushUser instance);
	
	/**
	 * 根据一个ID得到AppPushUser
	 * 
	 * @param id
	 * @return
	 */
	AppPushUser getObjById(Long id);
	
	/**
	 * 删除一个AppPushUser
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除AppPushUser
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到AppPushUser
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个AppPushUser
	 * 
	 * @param id
	 *            需要更新的AppPushUser的id
	 * @param dir
	 *            需要更新的AppPushUser
	 */
	boolean update(AppPushUser instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<AppPushUser> query(String query, Map params, int begin, int max);
}
