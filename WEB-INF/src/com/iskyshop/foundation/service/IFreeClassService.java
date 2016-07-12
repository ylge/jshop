package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.FreeClass;

public interface IFreeClassService {
	/**
	 * 保存一个FreeClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(FreeClass instance);
	
	/**
	 * 根据一个ID得到FreeClass
	 * 
	 * @param id
	 * @return
	 */
	FreeClass getObjById(Long id);
	
	/**
	 * 删除一个FreeClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除FreeClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到FreeClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个FreeClass
	 * 
	 * @param id
	 *            需要更新的FreeClass的id
	 * @param dir
	 *            需要更新的FreeClass
	 */
	boolean update(FreeClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<FreeClass> query(String query, Map params, int begin, int max);
}
