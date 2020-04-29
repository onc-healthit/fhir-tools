package org.sitenv.spring.service;


import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;


public interface RiskAssessmentService {
	
	public DafRiskAssessment getRiskAssessmentById(String id);

	public DafRiskAssessment getRiskAssessmentByVersionId(String id, String versionIdPart);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(String id);

	

}
