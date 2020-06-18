package org.sitenv.spring.service;


import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;


public interface RiskAssessmentService {
	
	public DafRiskAssessment getRiskAssessmentById(String id);

	public DafRiskAssessment getRiskAssessmentByVersionId(String id, String versionIdPart);

	public List<DafRiskAssessment> search(SearchParameterMap paramMap);

	public List<DafRiskAssessment> getRiskAssessmentHistoryById(String id);

	public List<DafRiskAssessment> getRiskAssessmentForBulkData(StringJoiner patients, Date start);

	

}
