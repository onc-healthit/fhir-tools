package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.SpecimenDao;
import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public List<DafSpecimen> getSpecimenRequestDataRequest(StringJoiner patients, Date start) {
		return this.specimenDao.getSpecimenRequestDataRequest(patients, start);

	}

}
