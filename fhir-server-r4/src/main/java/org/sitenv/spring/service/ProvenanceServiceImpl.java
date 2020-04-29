package org.sitenv.spring.service;

import org.sitenv.spring.dao.ProvenanceDao;
import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("provenanceService")
@Transactional
public class ProvenanceServiceImpl implements ProvenanceService{
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
	@Transactional
	public List<DafProvenance> getProvenanceByResourceId(List<String> resourceID) {
		return this.provenanceDao.getProvenanceByResourceId(resourceID);
	}

}
