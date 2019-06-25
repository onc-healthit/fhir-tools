package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface RiskAssessmentDao {

	
	public DafRiskAssessment getRiskAssessmentById(int id);

	public DafRiskAssessment getRiskAssessmentByVersionId(int theId, String versionId);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(int id);
}
