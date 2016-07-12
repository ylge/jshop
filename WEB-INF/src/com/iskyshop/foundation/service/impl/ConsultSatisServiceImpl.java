package com.iskyshop.foundation.service.impl;

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
import com.iskyshop.foundation.domain.ConsultSatis;
import com.iskyshop.foundation.service.IConsultSatisService;

@Service
@Transactional
public class ConsultSatisServiceImpl implements IConsultSatisService {
	@Resource(name = "consultSatisDAO")
	private IGenericDAO<ConsultSatis> consultSatisDao;

	public boolean save(ConsultSatis consultSatis) {
		/**
		 * init other field here
		 */
		try {
			this.consultSatisDao.save(consultSatis);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ConsultSatis getObjById(Long id) {
		ConsultSatis consultSatis = this.consultSatisDao.get(id);
		if (consultSatis != null) {
			return consultSatis;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.consultSatisDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> consultSatisIds) {
		// TODO Auto-generated method stub
		for (Serializable id : consultSatisIds) {
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
		GenericPageList pList = new GenericPageList(ConsultSatis.class,
				construct, query, params, this.consultSatisDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(ConsultSatis consultSatis) {
		try {
			this.consultSatisDao.update(consultSatis);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ConsultSatis> query(String query, Map params, int begin, int max) {
		return this.consultSatisDao.query(query, params, begin, max);

	}
}
