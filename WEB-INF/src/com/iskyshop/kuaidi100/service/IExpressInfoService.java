package com.iskyshop.kuaidi100.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.kuaidi100.domain.ExpressInfo;

public interface IExpressInfoService {
	/**
	 * 保存一个ExpressInfo，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ExpressInfo instance);

	/**
	 * 根据一个ID得到ExpressInfo
	 * 
	 * @param id
	 * @return
	 */
	ExpressInfo getObjById(Long id);

	/**
	 * 删除一个ExpressInfo
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除ExpressInfo
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到ExpressInfo
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个ExpressInfo
	 * 
	 * @param id
	 *            需要更新的ExpressInfo的id
	 * @param dir
	 *            需要更新的ExpressInfo
	 */
	boolean update(ExpressInfo instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ExpressInfo> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	ExpressInfo getObjByPropertyWithType(String propertyName, Object value,int type);
}
