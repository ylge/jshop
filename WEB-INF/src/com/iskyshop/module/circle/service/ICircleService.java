package com.iskyshop.module.circle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.circle.domain.Circle;

public interface ICircleService {
	/**
	 * 保存一个Circle，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Circle instance);
	
	/**
	 * 根据一个ID得到Circle
	 * 
	 * @param id
	 * @return
	 */
	Circle getObjById(Long id);
	
	/**
	 * 删除一个Circle
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Circle
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Circle
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Circle
	 * 
	 * @param id
	 *            需要更新的Circle的id
	 * @param dir
	 *            需要更新的Circle
	 */
	boolean update(Circle instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Circle> query(String query, Map params, int begin, int max);
}
