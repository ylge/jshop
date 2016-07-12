package com.iskyshop.module.cms.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.cms.domain.InformationClass;


public interface IInformationClassService {
	/**
	 * 保存一个InformationClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(InformationClass instance);
	
	/**
	 * 根据一个ID得到InformationClass
	 * 
	 * @param id
	 * @return
	 */
	InformationClass getObjById(Long id);
	
	/**
	 * 删除一个InformationClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除InformationClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到InformationClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个InformationClass
	 * 
	 * @param id
	 *            需要更新的InformationClass的id
	 * @param dir
	 *            需要更新的InformationClass
	 */
	boolean update(InformationClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<InformationClass> query(String query, Map params, int begin, int max);
}
