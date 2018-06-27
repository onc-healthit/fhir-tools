package org.sitenv.spring.service;

import org.sitenv.spring.dao.CareTeamDao;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.query.CareTeamSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("careTeamResourceService")
@Transactional
public class CareTeamServiceImpl implements CareTeamService {

    @Autowired
    private CareTeamDao careTeamDao;

    @Override
    @Transactional
    public List<DafCareTeam> getAllCareTeam() {
        return this.careTeamDao.getAllCareTeam();
    }

    @Override
    @Transactional
    public DafCareTeam getCareTeamById(int id) {
        return this.careTeamDao.getCareTeamById(id);
    }

    @Override
    @Transactional
    public List<DafCareTeam> getCareTeamByPatient(String patient) {
        return this.careTeamDao.getCareTeamByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafCareTeam> getCareTeamBySearchCriteria(CareTeamSearchCriteria careTeamSearchCriteria) {
        return this.careTeamDao.getCareTeamBySearchCriteria(careTeamSearchCriteria);
    }

}
