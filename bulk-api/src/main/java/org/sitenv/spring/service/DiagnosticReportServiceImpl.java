package org.sitenv.spring.service;

import org.sitenv.spring.dao.DiagnosticReportDao;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.query.DiagnosticReportSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("diagnosticResourceService")
@Transactional
public class DiagnosticReportServiceImpl implements DiagnosticReportService {

    @Autowired
    private DiagnosticReportDao diagnosticDao;

    @Override
    @Transactional
    public List<DafDiagnosticReport> getAllDiagnosticReports() {
        return this.diagnosticDao.getAllDiagnosticReports();
    }

    @Override
    @Transactional
    public DafDiagnosticReport getDiagnosticResourceById(int id) {
        return this.diagnosticDao.getDiagnosticResourceById(id);
    }

    @Override
    @Transactional
    public List<DafDiagnosticReport> getDiagnosticByPatient(String patient) {
        return this.diagnosticDao.getDiagnosticByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafDiagnosticReport> getDiagnosticReportBySearchCriteria(DiagnosticReportSearchCriteria diagnosticReportSearchCriteria) {
        return this.diagnosticDao.getDiagnosticReportBySearchCriteria(diagnosticReportSearchCriteria);
    }
    
    public List<DafDiagnosticReport> getDiagnosticReportForBulkData(List<Integer> patients, Date start){
    	return this.diagnosticDao.getDiagnosticReportForBulkData(patients, start);
    }
}
