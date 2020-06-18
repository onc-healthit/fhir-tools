package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.ProvenanceDao;
import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("provenanceService")
@Transactional
public class ProvenanceServiceImpl implements ProvenanceService {
	@Autowired
	ProvenanceDao provenanceDao;

	@Override
	@Transactional
	public DafProvenance getProvenanceById(String id) {
		return this.provenanceDao.getProvenanceById(id);
	}

	@Override
	@Transactional
	public DafProvenance getProvenanceByVersionId(String theId, String versionId) {
		return this.provenanceDao.getProvenanceByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafProvenance> search(SearchParameterMap paramMap) {

		return this.provenanceDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafProvenance> getProvenanceHistoryById(String theId) {
		return this.provenanceDao.getProvenanceHistoryById(theId);
	}

	@Override
	public List<DafProvenance> getProvenanceRequestDataRequest(StringJoiner patients, Date start) {
		return this.provenanceDao.getProvenanceRequestDataRequest(patients, start);

	}
	
	@Override
	@Transactional
	public List<DafProvenance> getProvenanceByResourceId(List<String> resourceID) {
		return this.provenanceDao.getProvenanceByResourceId(resourceID);
	}

}
