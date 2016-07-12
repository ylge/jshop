package com.iskyshop.module.circle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.circle.domain.CircleClass;

public interface ICircleClassService {
	/**
	 * 保存一个CircleClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CircleClass instance);
	
	/**
	 * 根据一个ID得到CircleClass
	 * 
	 * @param id
	 * @return
	 */
	CircleClass getObjById(Long id);
	
	/**
	 * 删除一个CircleClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CircleClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CircleClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CircleClass
	 * 
	 * @param id
	 *            需要更新的CircleClass的id
	 * @param dir
	 *            需要更新的CircleClass
	 */
	boolean update(CircleClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CircleClass> query(String query, Map params, int begin, int max);
}
