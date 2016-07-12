package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.FreeGoods;

public interface IFreeGoodsService {
	/**
	 * 保存一个FreeGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(FreeGoods instance);
	
	/**
	 * 根据一个ID得到FreeGoods
	 * 
	 * @param id
	 * @return
	 */
	FreeGoods getObjById(Long id);
	
	/**
	 * 删除一个FreeGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除FreeGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到FreeGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个FreeGoods
	 * 
	 * @param id
	 *            需要更新的FreeGoods的id
	 * @param dir
	 *            需要更新的FreeGoods
	 */
	boolean update(FreeGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<FreeGoods> query(String query, Map params, int begin, int max);
}
