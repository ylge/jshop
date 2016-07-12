package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.RechargeCard;

public interface IRechargeCardService {
	/**
	 * 保存一个RechargeCard，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(RechargeCard instance);
	
	/**
	 * 根据一个ID得到RechargeCard
	 * 
	 * @param id
	 * @return
	 */
	RechargeCard getObjById(Long id);
	
	/**
	 * 删除一个RechargeCard
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除RechargeCard
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到RechargeCard
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个RechargeCard
	 * 
	 * @param id
	 *            需要更新的RechargeCard的id
	 * @param dir
	 *            需要更新的RechargeCard
	 */
	boolean update(RechargeCard instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<RechargeCard> query(String query, Map params, int begin, int max);
}
