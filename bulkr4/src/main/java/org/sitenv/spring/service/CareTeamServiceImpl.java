package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.CareTeamDao;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("careTeamService")
@Transactional
public class CareTeamServiceImpl implements CareTeamService {
	
	@Autowired
    private CareTeamDao careTeamDao;

	@Override
	@Transactional
	public DafCareTeam getCareTeamById(String id) {
        return this.careTeamDao.getCareTeamById(id);
	}

	@Override
	@Transactional
	public DafCareTeam getCareTeamByVersionId(String theId, String versionId) {
		return this.careTeamDao.getCareTeamByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafCareTeam> search(SearchParameterMap paramMap){
        return this.careTeamDao.search(paramMap);
    }

	@Override
	public List<DafCareTeam> getCareTeamHistoryById(String theId) {
		return this.careTeamDao.getCareTeamHistoryById(theId);
	}

	@Override
	public List<DafCareTeam> getCareTeamForBulkData(StringJoiner patients, Date start) {
		return this.careTeamDao.getCareTeamForBulkData(patients, start);

	}
}
