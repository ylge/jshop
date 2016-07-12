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
import com.iskyshop.foundation.domain.CustomerRelMana;
import com.iskyshop.foundation.service.ICustomerRelManaService;

@Service
@Transactional
public class CustomerRelManaServiceImpl implements ICustomerRelManaService {
	@Resource(name = "customerRelManaDAO")
	private IGenericDAO<CustomerRelMana> customerRelManaDao;

	public boolean save(CustomerRelMana customerRelMana) {
		/**
		 * init other field here
		 */
		try {
			this.customerRelManaDao.save(customerRelMana);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public CustomerRelMana getObjById(Long id) {
		CustomerRelMana customerRelMana = this.customerRelManaDao.get(id);
		if (customerRelMana != null) {
			return customerRelMana;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.customerRelManaDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> customerRelManaIds) {
		// TODO Auto-generated method stub
		for (Serializable id : customerRelManaIds) {
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
		GenericPageList pList = new GenericPageList(CustomerRelMana.class,
				construct, query, params, this.customerRelManaDao);
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

	public boolean update(CustomerRelMana customerRelMana) {
		try {
			this.customerRelManaDao.update(customerRelMana);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<CustomerRelMana> query(String query, Map params, int begin,
			int max) {
		return this.customerRelManaDao.query(query, params, begin, max);

	}
}
