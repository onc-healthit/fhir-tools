package org.sitenv.spring.service;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.sitenv.spring.dao.DiagnosticReportDao;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Service("diagnosticReportService")
@Transactional
public class DiagnosticReportServiceImpl implements DiagnosticReportService {

	@Autowired
	private DiagnosticReportDao diagnosticReportDao;

	@Override
	@Transactional
	public DafDiagnosticReport getDiagnosticReportById(String id) {
		return this.diagnosticReportDao.getDiagnosticReportById(id);
	}

	@Override
	@Transactional
	public DafDiagnosticReport getDiagnosticReportByVersionId(String theId, String versionId) {
		return this.diagnosticReportDao.getDiagnosticReportByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafDiagnosticReport> search(SearchParameterMap paramMap) {
		return this.diagnosticReportDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafDiagnosticReport> getDiagnosticReportHistoryById(String theId) {
		return this.diagnosticReportDao.getDiagnosticReportHistoryById(theId);
	}

	@Override
	public DafDiagnosticReport createDiagnosticReport(DiagnosticReport theDiagnosticReport) {
		return this.diagnosticReportDao.createDiagnosticReport(theDiagnosticReport);
	}

	@Override
	public List<DafDiagnosticReport> getDiagnosticReportForBulkData(StringJoiner patients, Date start) {
		return this.diagnosticReportDao.getDiagnosticReportForBulkData(patients, start);
	}

}
