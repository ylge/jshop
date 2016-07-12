package com.iskyshop.module.weixin.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.weixin.domain.VMessage;


public interface IVMessageService {
	/**
	 * 保存一个VMessage，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(VMessage instance);
	
	/**
	 * 根据一个ID得到VMessage
	 * 
	 * @param id
	 * @return
	 */
	VMessage getObjById(Long id);
	
	/**
	 * 删除一个VMessage
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除VMessage
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到VMessage
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个VMessage
	 * 
	 * @param id
	 *            需要更新的VMessage的id
	 * @param dir
	 *            需要更新的VMessage
	 */
	boolean update(VMessage instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<VMessage> query(String query, Map params, int begin, int max);
}
