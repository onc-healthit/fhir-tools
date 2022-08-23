package org.sitenv.spring.service;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface DiagnosticReportService {

	public DafDiagnosticReport getDiagnosticReportById(String id);

	public DafDiagnosticReport getDiagnosticReportByVersionId(String theId, String versionId);

	public List<DafDiagnosticReport> getDiagnosticReportHistoryById(String theId);

	public List<DafDiagnosticReport> search(SearchParameterMap theMap);
	
	public DafDiagnosticReport createDiagnosticReport(DiagnosticReport theDiagnosticReport);

	public List<DafDiagnosticReport> getDiagnosticReportForBulkData(StringJoiner patients, Date start);
}
