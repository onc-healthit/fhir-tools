package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.query.CareTeamSearchCriteria;

import java.util.List;

public interface CareTeamService {

    public List<DafCareTeam> getAllCareTeam();

    public DafCareTeam getCareTeamById(int id);

    public List<DafCareTeam> getCareTeamByPatient(String Patient);

    public List<DafCareTeam> getCareTeamBySearchCriteria(CareTeamSearchCriteria careTeamSearchCriteria);

}
