package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.CustomerRelMana;

public interface ICustomerRelManaService {
	/**
	 * 保存一个CustomerRelMana，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CustomerRelMana instance);
	
	/**
	 * 根据一个ID得到CustomerRelMana
	 * 
	 * @param id
	 * @return
	 */
	CustomerRelMana getObjById(Long id);
	
	/**
	 * 删除一个CustomerRelMana
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CustomerRelMana
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CustomerRelMana
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CustomerRelMana
	 * 
	 * @param id
	 *            需要更新的CustomerRelMana的id
	 * @param dir
	 *            需要更新的CustomerRelMana
	 */
	boolean update(CustomerRelMana instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CustomerRelMana> query(String query, Map params, int begin, int max);
}
