package org.sitenv.spring.service;


import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;


public interface RiskAssessmentService {
	
	public DafRiskAssessment getRiskAssessmentById(int id);

	public DafRiskAssessment getRiskAssessmentByVersionId(int id, String versionIdPart);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(int id);

	

}
