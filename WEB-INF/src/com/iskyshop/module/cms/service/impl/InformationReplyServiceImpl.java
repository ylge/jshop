package com.iskyshop.module.cms.service.impl;
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
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.service.IInformationReplyService;

@Service
@Transactional
public class InformationReplyServiceImpl implements IInformationReplyService{
	@Resource(name = "informationReplyDAO")
	private IGenericDAO<InformationReply> informationReplyDao;
	
	public boolean save(InformationReply informationReply) {
		/**
		 * init other field here
		 */
		try {
			this.informationReplyDao.save(informationReply);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public InformationReply getObjById(Long id) {
		InformationReply informationReply = this.informationReplyDao.get(id);
		if (informationReply != null) {
			return informationReply;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.informationReplyDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> informationReplyIds) {
		// TODO Auto-generated method stub
		for (Serializable id : informationReplyIds) {
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
		GenericPageList pList = new GenericPageList(InformationReply.class, construct,query,
				params, this.informationReplyDao);
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
	
	public boolean update(InformationReply informationReply) {
		try {
			this.informationReplyDao.update( informationReply);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<InformationReply> query(String query, Map params, int begin, int max){
		return this.informationReplyDao.query(query, params, begin, max);
		
	}
}
