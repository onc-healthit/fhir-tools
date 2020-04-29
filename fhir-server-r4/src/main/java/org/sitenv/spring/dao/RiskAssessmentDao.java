package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface RiskAssessmentDao {

	
	public DafRiskAssessment getRiskAssessmentById(String id);

	public DafRiskAssessment getRiskAssessmentByVersionId(String theId, String versionId);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(String id);
}
