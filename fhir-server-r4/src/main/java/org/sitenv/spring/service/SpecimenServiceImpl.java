package org.sitenv.spring.service;

import org.sitenv.spring.dao.SpecimenDao;
import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("specimenService")
@Transactional
public class SpecimenServiceImpl implements SpecimenService {
	
	@Autowired
    private SpecimenDao specimenDao;
	
	@Override
	@Transactional
	public DafSpecimen getSpecimenById(String id) {
		return this.specimenDao.getSpecimenById(id);
	}

	@Override
	@Transactional
	public DafSpecimen getSpecimenByVersionId(String theId, String versionId) {
		return this.specimenDao.getSpecimenByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafSpecimen> getSpecimenHistoryById(String theId) {
		return this.specimenDao.getSpecimenHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafSpecimen> search(SearchParameterMap paramMap) {
		return this.specimenDao.search(paramMap);
	}

}
