package com.iskyshop.module.cms.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.cms.domain.CmsIndexTemplate;
import com.iskyshop.module.cms.service.ICmsIndexTemplateService;

@Service
@Transactional
public class CmsIndexTemplateServiceImpl implements ICmsIndexTemplateService{
	@Resource(name = "cmsIndexTemplateDAO")
	private IGenericDAO<CmsIndexTemplate> cmsIndexTemplateDao;
	
	public boolean save(CmsIndexTemplate cmsIndexTemplate) {
		/**
		 * init other field here
		 */
		try {
			this.cmsIndexTemplateDao.save(cmsIndexTemplate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public CmsIndexTemplate getObjById(Long id) {
		CmsIndexTemplate cmsIndexTemplate = this.cmsIndexTemplateDao.get(id);
		if (cmsIndexTemplate != null) {
			return cmsIndexTemplate;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.cmsIndexTemplateDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> cmsIndexTemplateIds) {
		// TODO Auto-generated method stub
		for (Serializable id : cmsIndexTemplateIds) {
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
		GenericPageList pList = new GenericPageList(CmsIndexTemplate.class, construct,query,
				params, this.cmsIndexTemplateDao);
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
	
	public boolean update(CmsIndexTemplate cmsIndexTemplate) {
		try {
			this.cmsIndexTemplateDao.update( cmsIndexTemplate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<CmsIndexTemplate> query(String query, Map params, int begin, int max){
		return this.cmsIndexTemplateDao.query(query, params, begin, max);
		
	}
}
