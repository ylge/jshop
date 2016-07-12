package com.iskyshop.module.sns.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.sns.domain.UserShare;

public interface IUserShareService {
	/**
	 * 保存一个UserShare，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(UserShare instance);

	/**
	 * 根据一个ID得到UserShare
	 * 
	 * @param id
	 * @return
	 */
	UserShare getObjById(Long id);

	/**
	 * 删除一个UserShare
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除UserShare
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到UserShare
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个UserShare
	 * 
	 * @param id
	 *            需要更新的UserShare的id
	 * @param dir
	 *            需要更新的UserShare
	 */
	boolean update(UserShare instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<UserShare> query(String query, Map params, int begin, int max);
}
