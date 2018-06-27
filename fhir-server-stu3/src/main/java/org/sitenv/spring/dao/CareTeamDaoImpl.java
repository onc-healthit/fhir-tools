package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.query.CareTeamSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("carTeamDao")
public class CareTeamDaoImpl extends AbstractDao implements CareTeamDao {

    private static final Logger logger = LoggerFactory.getLogger(CareTeamDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafCareTeam> getAllCareTeam() {
        Criteria criteria = getSession().createCriteria(DafCareTeam.class);
        return (List<DafCareTeam>) criteria.list();
    }

    @Override
    public DafCareTeam getCareTeamById(int id) {
        DafCareTeam dafCareTeam = (DafCareTeam) getSession().get(DafCareTeam.class, id);
        return dafCareTeam;
    }

    @Override
    public List<DafCareTeam> getCareTeamByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafCareTeam.class, "careteam")
                .createAlias("careteam.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafCareTeam> dafCareTeam = criteria.list();
        return dafCareTeam;
    }

    @Override
    public List<DafCareTeam> getCareTeamBySearchCriteria(CareTeamSearchCriteria careTeamSearchCriteria) {
        List<DafCareTeam> dafCareTeam = getCareTeam(careTeamSearchCriteria);
        return dafCareTeam;
    }

    public List<DafCareTeam> getCareTeam(CareTeamSearchCriteria careTeamSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafCareTeam.class, "careteam").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (careTeamSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("careteam.subject.id", careTeamSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(careTeamSearchCriteria.getStatus())) {
            criteria.add(Restrictions.eq("status", careTeamSearchCriteria.getStatus()).ignoreCase());
        }

        return (List<DafCareTeam>) criteria.list();
    }

}
