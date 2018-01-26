package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafMedicationOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationOrderDao")
public class MedicationOrderDaoImpl extends AbstractDao implements MedicationOrderDao {

    private static final Logger logger = LoggerFactory.getLogger(MedicationOrderDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationOrder> getAllMedicationOrder() {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class);
        return (List<DafMedicationOrder>) criteria.list();
    }

    @Override
    public DafMedicationOrder getMedicationOrderResourceById(int id) {
        DafMedicationOrder dafMedicationOrder = (DafMedicationOrder) getSession().get(DafMedicationOrder.class, id);
        return dafMedicationOrder;
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .createAlias("medOrder.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafMedicationOrder> dafMedicationOrder = criteria.list();
        return dafMedicationOrder;
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByCode(String code) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .createAlias("medOrder.medicationreference", "dp")
                .add(Restrictions.eq("dp.med_code", code));
        List<DafMedicationOrder> dafMedicationOrder = criteria.list();
        return dafMedicationOrder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationOrder> getMedicationOrderByIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .add(Restrictions.like("identifierSystem", identifierSystem).ignoreCase())
                .add(Restrictions.like("identifierValue", identifierValue).ignoreCase());
        List<DafMedicationOrder> dafMedicationOrders = criteria.list();

        return dafMedicationOrders;
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByMedication(String medication) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .createAlias("medOrder.medicationreference", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(medication)));
        List<DafMedicationOrder> dafMedicationOrder = criteria.list();
        return dafMedicationOrder;
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByPrescriber(String prescriber) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .createAlias("medOrder.prescriber", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(prescriber)));
        List<DafMedicationOrder> dafMedicationOrder = criteria.list();
        return dafMedicationOrder;
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByStatus(String status) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .add(Restrictions.like("status", status).ignoreCase());

        List<DafMedicationOrder> dafMedicationOrder = criteria.list();

        return dafMedicationOrder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationOrder> getMedicationOrderByDateWritten(String comparatorStr, Date dateWritten) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder");
        if (comparatorStr == "" || comparatorStr == "eq") {
            criteria.add(Restrictions.eq("dateWritten", dateWritten));
        } else if (comparatorStr == "ge") {
            criteria.add(Restrictions.ge("dateWritten", dateWritten));
        } else if (comparatorStr == "le") {
            criteria.add(Restrictions.le("dateWritten", dateWritten));
        } else if (comparatorStr == "gt") {
            criteria.add(Restrictions.gt("dateWritten", dateWritten));
        } else if (comparatorStr == "lt") {
            criteria.add(Restrictions.lt("dateWritten", dateWritten));
        }

        List<DafMedicationOrder> dafMedicationOrder = criteria.list();
        return dafMedicationOrder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationOrder> getMedicationOrderByIdentifierValue(String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .add(Restrictions.like("identifierValue", identifierValue).ignoreCase());
        List<DafMedicationOrder> dafMedicationOrders = criteria.list();

        return dafMedicationOrders;
    }
    
    @Override
    public List<DafMedicationOrder> getMedicationOrderForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafMedicationOrder.class, "medOrder")
                .createAlias("medOrder.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    
	}
}
