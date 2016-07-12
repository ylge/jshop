package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.StoreAdjustInfo;

public interface IStoreAdjustInfoService {
	/**
	 * 保存一个StoreAdjustInfo，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreAdjustInfo instance);
	
	/**
	 * 根据一个ID得到StoreAdjustInfo
	 * 
	 * @param id
	 * @return
	 */
	StoreAdjustInfo getObjById(Long id);
	
	/**
	 * 删除一个StoreAdjustInfo
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreAdjustInfo
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreAdjustInfo
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreAdjustInfo
	 * 
	 * @param id
	 *            需要更新的StoreAdjustInfo的id
	 * @param dir
	 *            需要更新的StoreAdjustInfo
	 */
	boolean update(StoreAdjustInfo instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreAdjustInfo> query(String query, Map params, int begin, int max);
}
