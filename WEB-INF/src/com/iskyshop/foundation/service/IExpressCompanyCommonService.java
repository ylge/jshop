package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ExpressCompanyCommon;

public interface IExpressCompanyCommonService {
	/**
	 * 保存一个ExpressCompanyCommon，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ExpressCompanyCommon instance);
	
	/**
	 * 根据一个ID得到ExpressCompanyCommon
	 * 
	 * @param id
	 * @return
	 */
	ExpressCompanyCommon getObjById(Long id);
	
	/**
	 * 删除一个ExpressCompanyCommon
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ExpressCompanyCommon
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ExpressCompanyCommon
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ExpressCompanyCommon
	 * 
	 * @param id
	 *            需要更新的ExpressCompanyCommon的id
	 * @param dir
	 *            需要更新的ExpressCompanyCommon
	 */
	boolean update(ExpressCompanyCommon instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ExpressCompanyCommon> query(String query, Map params, int begin, int max);
}
