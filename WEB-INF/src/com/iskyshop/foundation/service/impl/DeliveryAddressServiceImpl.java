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
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.service.IDeliveryAddressService;

@Service
@Transactional
public class DeliveryAddressServiceImpl implements IDeliveryAddressService {
	@Resource(name = "deliveryAddressDAO")
	private IGenericDAO<DeliveryAddress> deliveryAddressDao;

	public boolean save(DeliveryAddress deliveryAddress) {
		/**
		 * init other field here
		 */
		try {
			this.deliveryAddressDao.save(deliveryAddress);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public DeliveryAddress getObjById(Long id) {
		DeliveryAddress deliveryAddress = this.deliveryAddressDao.get(id);
		if (deliveryAddress != null) {
			return deliveryAddress;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.deliveryAddressDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> deliveryAddressIds) {
		// TODO Auto-generated method stub
		for (Serializable id : deliveryAddressIds) {
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
		GenericPageList pList = new GenericPageList(DeliveryAddress.class,
				construct, query, params, this.deliveryAddressDao);
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

	public boolean update(DeliveryAddress deliveryAddress) {
		try {
			this.deliveryAddressDao.update(deliveryAddress);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<DeliveryAddress> query(String query, Map params, int begin,
			int max) {
		return this.deliveryAddressDao.query(query, params, begin, max);

	}
}
