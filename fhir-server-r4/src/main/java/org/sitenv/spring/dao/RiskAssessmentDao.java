package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;

public interface RiskAssessmentDao {

	
	public DafRiskAssessment getRiskAssessmentById(int id);

	public DafRiskAssessment getRiskAssessmentByVersionId(int theId, String versionId);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(int id);
}
