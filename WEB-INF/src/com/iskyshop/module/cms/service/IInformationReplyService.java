package com.iskyshop.module.cms.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.cms.domain.InformationReply;


public interface IInformationReplyService {
	/**
	 * 保存一个InformationReply，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(InformationReply instance);
	
	/**
	 * 根据一个ID得到InformationReply
	 * 
	 * @param id
	 * @return
	 */
	InformationReply getObjById(Long id);
	
	/**
	 * 删除一个InformationReply
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除InformationReply
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到InformationReply
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个InformationReply
	 * 
	 * @param id
	 *            需要更新的InformationReply的id
	 * @param dir
	 *            需要更新的InformationReply
	 */
	boolean update(InformationReply instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<InformationReply> query(String query, Map params, int begin, int max);
}
