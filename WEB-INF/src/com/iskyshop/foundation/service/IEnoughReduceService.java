package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.EnoughReduce;

public interface IEnoughReduceService {
	/**
	 * 保存一个EnoughReduce，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(EnoughReduce instance);
	
	/**
	 * 根据一个ID得到EnoughReduce
	 * 
	 * @param id
	 * @return
	 */
	EnoughReduce getObjById(Long id);
	
	/**
	 * 删除一个EnoughReduce
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除EnoughReduce
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到EnoughReduce
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个EnoughReduce
	 * 
	 * @param id
	 *            需要更新的EnoughReduce的id
	 * @param dir
	 *            需要更新的EnoughReduce
	 */
	boolean update(EnoughReduce instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<EnoughReduce> query(String query, Map params, int begin, int max);
}
