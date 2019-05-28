package org.sitenv.spring.service;


import java.util.List;

import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;


public interface RiskAssessmentService {
	
	public DafRiskAssessment getRiskAssessmentById(int id);

	public DafRiskAssessment getRiskAssessmentByVersionId(int id, String versionIdPart);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(int id);

	

}
