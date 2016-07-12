package com.iskyshop.module.circle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.module.circle.domain.CircleInvitation;

public interface IInvitationService {
	/**
	 * 保存一个Invitation，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CircleInvitation instance);
	
	/**
	 * 根据一个ID得到Invitation
	 * 
	 * @param id
	 * @return
	 */
	CircleInvitation getObjById(Long id);
	
	/**
	 * 删除一个Invitation
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Invitation
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Invitation
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Invitation
	 * 
	 * @param id
	 *            需要更新的Invitation的id
	 * @param dir
	 *            需要更新的Invitation
	 */
	boolean update(CircleInvitation instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CircleInvitation> query(String query, Map params, int begin, int max);
}
