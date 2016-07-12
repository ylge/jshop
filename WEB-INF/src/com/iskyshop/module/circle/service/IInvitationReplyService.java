package com.iskyshop.module.circle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.circle.domain.CircleInvitationReply;

public interface IInvitationReplyService {
	/**
	 * 保存一个InvitationReply，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CircleInvitationReply instance);
	
	/**
	 * 根据一个ID得到InvitationReply
	 * 
	 * @param id
	 * @return
	 */
	CircleInvitationReply getObjById(Long id);
	
	/**
	 * 删除一个InvitationReply
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除InvitationReply
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到InvitationReply
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个InvitationReply
	 * 
	 * @param id
	 *            需要更新的InvitationReply的id
	 * @param dir
	 *            需要更新的InvitationReply
	 */
	boolean update(CircleInvitationReply instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CircleInvitationReply> query(String query, Map params, int begin, int max);
}
