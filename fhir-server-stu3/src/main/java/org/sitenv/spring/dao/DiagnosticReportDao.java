package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.query.DiagnosticReportSearchCriteria;

import java.util.List;

public interface DiagnosticReportDao {

    public List<DafDiagnosticReport> getAllDiagnosticReports();

    public DafDiagnosticReport getDiagnosticResourceById(int id);

    public List<DafDiagnosticReport> getDiagnosticByPatient(String patient);

    public List<DafDiagnosticReport> getDiagnosticReportBySearchCriteria(DiagnosticReportSearchCriteria diagnosticReportSearchCriteria);

}
