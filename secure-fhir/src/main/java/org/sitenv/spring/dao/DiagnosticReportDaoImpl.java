package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.query.DiagnosticReportSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("diagnosticReportDao")
public class DiagnosticReportDaoImpl extends AbstractDao implements DiagnosticReportDao {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticReportDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafDiagnosticReport> getAllDiagnosticReports() {
        Criteria criteria = getSession().createCriteria(DafDiagnosticReport.class);
        return (List<DafDiagnosticReport>) criteria.list();
    }

    @Override
    public DafDiagnosticReport getDiagnosticResourceById(int id) {
        DafDiagnosticReport dafDiagnostic = (DafDiagnosticReport) getSession().get(DafDiagnosticReport.class, id);
        return dafDiagnostic;
    }

    @Override
    public List<DafDiagnosticReport> getDiagnosticByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafDiagnosticReport.class, "diagnostic")
                .createAlias("diagnostic.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafDiagnosticReport> dafDiagnostic = criteria.list();
        return dafDiagnostic;
    }

    @Override
    public List<DafDiagnosticReport> getDiagnosticReportBySearchCriteria(DiagnosticReportSearchCriteria diagnosticReportSearchCriteria) {
        List<DafDiagnosticReport> dafDiagnosticReports = getDiagnosticReports(diagnosticReportSearchCriteria);
        return dafDiagnosticReports;
    }

    public List<DafDiagnosticReport> getDiagnosticReports(DiagnosticReportSearchCriteria diagnosticReportSearchCriteria) {
        Criteria criteria = getSession().createCriteria(DafDiagnosticReport.class, "diagnostic").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (diagnosticReportSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("diagnostic.patient.id", diagnosticReportSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(diagnosticReportSearchCriteria.getCategory())) {
            criteria.add(Restrictions.like("cat_code", diagnosticReportSearchCriteria.getCategory()));
        }

        if (diagnosticReportSearchCriteria.getCode() != null && diagnosticReportSearchCriteria.getCode().size() > 0) {
            criteria.add(Restrictions.in("code", diagnosticReportSearchCriteria.getCode()));
        }

        if (diagnosticReportSearchCriteria.getDate() != null) {
            criteria.add(Restrictions.between("effectivedate", diagnosticReportSearchCriteria.getDate().getLowerBoundAsInstant(), diagnosticReportSearchCriteria.getDate().getUpperBoundAsInstant()));
        }
        return (List<DafDiagnosticReport>) criteria.list();
    }
    
    public List<DafDiagnosticReport> getDiagnosticReportForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafDiagnosticReport.class, "diagnostic")
                .createAlias("diagnostic.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }

}
