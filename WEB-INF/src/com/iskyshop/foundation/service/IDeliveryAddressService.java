package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.DeliveryAddress;

public interface IDeliveryAddressService {
	/**
	 * 保存一个DeliveryAddress，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(DeliveryAddress instance);
	
	/**
	 * 根据一个ID得到DeliveryAddress
	 * 
	 * @param id
	 * @return
	 */
	DeliveryAddress getObjById(Long id);
	
	/**
	 * 删除一个DeliveryAddress
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除DeliveryAddress
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到DeliveryAddress
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个DeliveryAddress
	 * 
	 * @param id
	 *            需要更新的DeliveryAddress的id
	 * @param dir
	 *            需要更新的DeliveryAddress
	 */
	boolean update(DeliveryAddress instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<DeliveryAddress> query(String query, Map params, int begin, int max);
}
