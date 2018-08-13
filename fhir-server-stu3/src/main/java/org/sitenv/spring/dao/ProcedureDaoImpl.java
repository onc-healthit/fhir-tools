package org.sitenv.spring.dao;

import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("procedureDao")
public class ProcedureDaoImpl extends AbstractDao implements ProcedureDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<DafProcedure> getAllProcedures() {
        Criteria criteria = getSession().createCriteria(DafProcedure.class);
        return (List<DafProcedure>) criteria.list();
    }

    @Override
    public DafProcedure getProcedureById(int id) {
        DafProcedure dafProcedure = (DafProcedure) getSession().get(DafProcedure.class, id);
        return dafProcedure;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafProcedure> getProcedureBySearchCriteria(
            ProcedureSearchCriteria procedureSearchCriteria) {
        Criteria criteria = getSession().createCriteria(DafProcedure.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (procedureSearchCriteria.getSubject() != null) {
            criteria.add(Restrictions.eq("subject.id", procedureSearchCriteria.getSubject()));
        }

        if (procedureSearchCriteria.getStatus() != null) {
            criteria.add(Restrictions.eq("status", procedureSearchCriteria.getStatus()));
        }

        if (procedureSearchCriteria.getRangedates() != null) {
            Date from = null;
            Date to = null;
            ParamPrefixEnum fromParamPrefixEnum = null;
            ParamPrefixEnum toParamPrefixEnum = null;
            if (procedureSearchCriteria.getRangedates().getLowerBoundAsInstant() != null) {
                fromParamPrefixEnum = procedureSearchCriteria.getRangedates().getLowerBound().getPrefix();
                from = procedureSearchCriteria.getRangedates().getLowerBoundAsInstant();
            }
            if (procedureSearchCriteria.getRangedates().getUpperBoundAsInstant() != null) {
                toParamPrefixEnum = procedureSearchCriteria.getRangedates().getUpperBound().getPrefix();
                to = procedureSearchCriteria.getRangedates().getUpperBoundAsInstant();
            }

            if (from == null) {
                if (toParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("performed", to));
                } else if (toParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("performed", to));
                } else if (toParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("performed", to));
                } else if (toParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("performed", to));
                }
            }
            if (to == null) {
                if (fromParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("performed", from));
                } else if (fromParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("performed", from));
                } else if (fromParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("performed", from));
                } else if (fromParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("performed", from));
                }
            }
            if (from != null && to != null) {
                criteria.add(Restrictions.between("performed", from, to));
            }
        }


        return criteria.list();
    }

	@Override
	public List<DafProcedure> getProcedureForBulkData(List<Integer> patients, Date start) {
		Criteria criteria = getSession().createCriteria(DafProcedure.class, "procedure")
                .createAlias("procedure.subject", "dp");
        if(patients!=null) {
            criteria.add(Restrictions.in("dp.id", patients));
        }
        if(start != null) {
            criteria.add(Restrictions.ge("updated", start));
        }
        
        return criteria.list();
	}

}
