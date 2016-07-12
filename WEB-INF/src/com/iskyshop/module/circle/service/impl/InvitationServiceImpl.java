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
import com.iskyshop.module.circle.domain.CircleInvitation;
import com.iskyshop.module.circle.service.IInvitationService;

@Service
@Transactional
public class InvitationServiceImpl implements IInvitationService{
	@Resource(name = "invitationDAO")
	private IGenericDAO<CircleInvitation> invitationDao;
	
	public boolean save(CircleInvitation invitation) {
		/**
		 * init other field here
		 */
		try {
			this.invitationDao.save(invitation);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public CircleInvitation getObjById(Long id) {
		CircleInvitation invitation = this.invitationDao.get(id);
		if (invitation != null) {
			return invitation;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.invitationDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> invitationIds) {
		// TODO Auto-generated method stub
		for (Serializable id : invitationIds) {
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
		GenericPageList pList = new GenericPageList(CircleInvitation.class, construct,query,
				params, this.invitationDao);
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
	
	public boolean update(CircleInvitation invitation) {
		try {
			this.invitationDao.update( invitation);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<CircleInvitation> query(String query, Map params, int begin, int max){
		return this.invitationDao.query(query, params, begin, max);
		
	}
}
