package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.model.DafCareTeamParticipant;
import org.sitenv.spring.query.CareTeamSearchCriteria;

import java.util.List;

public interface CareTeamDao {

    public List<DafCareTeam> getAllCareTeam();

    public DafCareTeam getCareTeamById(int id);

    public List<DafCareTeam> getCareTeamByPatient(String patient);

    public List<DafCareTeam> getCareTeamBySearchCriteria(CareTeamSearchCriteria careTeamSearchCriteria);

    public List<DafCareTeamParticipant> getCareteamparticipantByCareTeam(int id);

}
