package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.FamilyMemberHistoryDao;
import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("familyMemberHistoryService")
@Transactional
public class FamilyMemberHistoryServiceImpl implements FamilyMemberHistoryService {
	
	@Autowired
    private FamilyMemberHistoryDao familyMemberHistoryDao;

	@Override
	@Transactional
	public DafFamilyMemberHistory getFamilyMemberHistoryById(int id) {
		return this.familyMemberHistoryDao.getFamilyMemberHistoryById(id);
	}

	@Override
	@Transactional
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId) {
		return this.familyMemberHistoryDao.getFamilyMemberHistoryByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafFamilyMemberHistory> search(SearchParameterMap paramMap) {
		return this.familyMemberHistoryDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(int theId) {
		return this.familyMemberHistoryDao.getFamilyMemberHistoryHistoryById(theId);
	}

}
