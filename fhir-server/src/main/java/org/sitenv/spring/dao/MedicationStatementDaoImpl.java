package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafMedicationStatement;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationStatementDao")
public class MedicationStatementDaoImpl extends AbstractDao implements MedicationStatementDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationStatement> getAllMedicationStatement(Integer count) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class);
        if(count!=null){
        criteria.setMaxResults(count);
        }
        return (List<DafMedicationStatement>) criteria.list();
    }

    @Override
    public DafMedicationStatement getMedicationStatementResourceById(Integer id) {
        DafMedicationStatement dafMedicationStatement = (DafMedicationStatement) getSession().get(DafMedicationStatement.class, id);
        return dafMedicationStatement;
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .createAlias("medStatement.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafMedicationStatement> dafMedicationStatement = criteria.list();
        return dafMedicationStatement;
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByCode(String code) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .createAlias("medStatement.medicationreference", "dp")
                .add(Restrictions.eq("dp.med_code", code));
        List<DafMedicationStatement> dafMedicationStatement = criteria.list();
        return dafMedicationStatement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationStatement> getMedicationStatementByIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .add(Restrictions.like("identifier_system", identifierSystem).ignoreCase())
                .add(Restrictions.like("identifier_value", identifierValue).ignoreCase());
        List<DafMedicationStatement> dafMedicationStatement = criteria.list();

        return dafMedicationStatement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationStatement> getMedicationStatementByEffectiveDate(String comparatorStr, Date effectiveDate) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement");
        if (comparatorStr == "" || comparatorStr == "eq") {
            criteria.add(Restrictions.eq("effectivePeriod", effectiveDate));
        } else if (comparatorStr == "ge") {
            criteria.add(Restrictions.ge("effectivePeriod", effectiveDate));
        } else if (comparatorStr == "le") {
            criteria.add(Restrictions.le("effectivePeriod", effectiveDate));
        } else if (comparatorStr == "gt") {
            criteria.add(Restrictions.gt("effectivePeriod", effectiveDate));
        } else if (comparatorStr == "lt") {
            criteria.add(Restrictions.lt("effectivePeriod", effectiveDate));
        }

        List<DafMedicationStatement> dafMedicationStatement = criteria.list();
        return dafMedicationStatement;
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByMedication(String medication) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .createAlias("medStatement.medicationreference", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(medication)));
        List<DafMedicationStatement> dafMedicationStatement = criteria.list();
        return dafMedicationStatement;
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByStatus(String status) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .add(Restrictions.like("status", status).ignoreCase());

        List<DafMedicationStatement> dafMedicationStatement = criteria.list();

        return dafMedicationStatement;
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByIdentifierValue(String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationStatement.class, "medStatement")
                .add(Restrictions.like("identifier_value", identifierValue).ignoreCase());
        List<DafMedicationStatement> dafMedicationStatement = criteria.list();

        return dafMedicationStatement;
    }

}
