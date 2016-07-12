package com.iskyshop.module.cms.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.cms.domain.Information;


public interface IInformationService {
	/**
	 * 保存一个Information，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Information instance);
	
	/**
	 * 根据一个ID得到Information
	 * 
	 * @param id
	 * @return
	 */
	Information getObjById(Long id);
	
	/**
	 * 删除一个Information
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Information
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Information
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Information
	 * 
	 * @param id
	 *            需要更新的Information的id
	 * @param dir
	 *            需要更新的Information
	 */
	boolean update(Information instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Information> query(String query, Map params, int begin, int max);
}
