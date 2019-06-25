package org.sitenv.spring.service;

import org.sitenv.spring.dao.DiagnosticReportDao;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("diagnosticReportService")
@Transactional
public class DiagnosticReportServiceImpl implements DiagnosticReportService {

	@Autowired
	private DiagnosticReportDao diagnosticReportDao;

	@Override
	@Transactional
	public DafDiagnosticReport getDiagnosticReportById(int id) {
		return this.diagnosticReportDao.getDiagnosticReportById(id);
	}

	@Override
	@Transactional
	public DafDiagnosticReport getDiagnosticReportByVersionId(int theId, String versionId) {
		return this.diagnosticReportDao.getDiagnosticReportByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafDiagnosticReport> search(SearchParameterMap paramMap) {
		return this.diagnosticReportDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafDiagnosticReport> getDiagnosticReportHistoryById(int theId) {
		return this.diagnosticReportDao.getDiagnosticReportHistoryById(theId);
	}


}
