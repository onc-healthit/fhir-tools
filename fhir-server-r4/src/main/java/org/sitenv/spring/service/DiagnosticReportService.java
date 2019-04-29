package org.sitenv.spring.service;

import java.util.List;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.util.SearchParameterMap;

public interface DiagnosticReportService {

	public DafDiagnosticReport getDiagnosticReportById(int id);

	public DafDiagnosticReport getDiagnosticReportByVersionId(int theId, String versionId);

	public List<DafDiagnosticReport> getDiagnosticReportHistoryById(int theId);

	public List<DafDiagnosticReport> search(SearchParameterMap theMap);
}
