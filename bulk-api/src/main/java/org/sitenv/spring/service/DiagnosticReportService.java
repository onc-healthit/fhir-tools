package org.sitenv.spring.service;

import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.query.DiagnosticReportSearchCriteria;

import java.util.Date;
import java.util.List;

public interface DiagnosticReportService {

    public List<DafDiagnosticReport> getAllDiagnosticReports();

    public DafDiagnosticReport getDiagnosticResourceById(int id);

    public List<DafDiagnosticReport> getDiagnosticByPatient(String patient);

    public List<DafDiagnosticReport> getDiagnosticReportBySearchCriteria(DiagnosticReportSearchCriteria diagnosticReportSearchCriteria);

    public List<DafDiagnosticReport> getDiagnosticReportForBulkData(List<Integer> patients, Date start);
}
