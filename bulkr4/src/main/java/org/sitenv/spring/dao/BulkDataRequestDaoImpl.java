package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("bulkDataRequestDao")
public class BulkDataRequestDaoImpl extends AbstractDao implements BulkDataRequestDao {

	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr) {
		 getSession().saveOrUpdate(bdr);
		return bdr;
	}

	public DafBulkDataRequest getBulkDataRequestById(Integer id) {
		DafBulkDataRequest bdr = (DafBulkDataRequest) getSession().get(DafBulkDataRequest.class,id);
		return bdr;
	}

	@Override
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag) {
		Criteria crit = getSession().createCriteria(DafBulkDataRequest.class);
		crit.add(Restrictions.eq("processedFlag", flag));
		return crit.list();
	}

	@Override
	public Integer deleteRequestById(Integer id) {
		Query qry = getSession().createQuery("delete from DafBulkDataRequest d where d.requestId=:id");
				         qry.setParameter("id",id);
		Integer res = qry.executeUpdate();
				         
		return res;
		
	}

}
