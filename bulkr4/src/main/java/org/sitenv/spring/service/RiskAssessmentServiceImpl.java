package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.RiskAssessmentDao;
import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("riskAssessmentService")
@Transactional
public class RiskAssessmentServiceImpl implements RiskAssessmentService {
	 

	@Autowired
    private RiskAssessmentDao riskAssessmentDao;

	@Override
	@Transactional
	public DafRiskAssessment getRiskAssessmentById(String id) {
        return this.riskAssessmentDao.getRiskAssessmentById(id);
	}

	@Override
	@Transactional
	public DafRiskAssessment getRiskAssessmentByVersionId(String theId, String versionId) {
		return this.riskAssessmentDao.getRiskAssessmentByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafRiskAssessment> search(SearchParameterMap paramMap) {
		return this.riskAssessmentDao.search(paramMap);
	}

	@Override
	public List<DafRiskAssessment> getRiskAssessmentHistoryById(String id) {
		return this.riskAssessmentDao.getRiskAssessmentHistoryById(id);
	}

	@Override
	public List<DafRiskAssessment> getRiskAssessmentForBulkData(StringJoiner patients, Date start) {
		return this.riskAssessmentDao.getRiskAssessmentForBulkData(patients, start);
	}
}
