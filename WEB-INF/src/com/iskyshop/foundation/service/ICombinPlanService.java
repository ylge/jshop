package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.CombinPlan;

public interface ICombinPlanService {
	/**
	 * 保存一个CombinPlan，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CombinPlan instance);
	
	/**
	 * 根据一个ID得到CombinPlan
	 * 
	 * @param id
	 * @return
	 */
	CombinPlan getObjById(Long id);
	
	/**
	 * 删除一个CombinPlan
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CombinPlan
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CombinPlan
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CombinPlan
	 * 
	 * @param id
	 *            需要更新的CombinPlan的id
	 * @param dir
	 *            需要更新的CombinPlan
	 */
	boolean update(CombinPlan instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CombinPlan> query(String query, Map params, int begin, int max);
}
