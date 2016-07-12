package com.iskyshop.module.circle.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.module.circle.domain.CircleInvitationReply;
import com.iskyshop.module.circle.service.IInvitationReplyService;

@Service
@Transactional
public class InvitationReplyServiceImpl implements IInvitationReplyService{
	@Resource(name = "invitationReplyDAO")
	private IGenericDAO<CircleInvitationReply> invitationReplyDao;
	
	public boolean save(CircleInvitationReply invitationReply) {
		/**
		 * init other field here
		 */
		try {
			this.invitationReplyDao.save(invitationReply);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public CircleInvitationReply getObjById(Long id) {
		CircleInvitationReply invitationReply = this.invitationReplyDao.get(id);
		if (invitationReply != null) {
			return invitationReply;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.invitationReplyDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> invitationReplyIds) {
		// TODO Auto-generated method stub
		for (Serializable id : invitationReplyIds) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(CircleInvitationReply.class, construct,query,
				params, this.invitationReplyDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(CircleInvitationReply invitationReply) {
		try {
			this.invitationReplyDao.update( invitationReply);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<CircleInvitationReply> query(String query, Map params, int begin, int max){
		return this.invitationReplyDao.query(query, params, begin, max);
		
	}
}
