package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface FamilyMemberHistoryDao {
	
	public DafFamilyMemberHistory getFamilyMemberHistoryById(String id);
	
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(String theId, String versionId);

	public List<DafFamilyMemberHistory> search(SearchParameterMap paramMap);
	
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(String theId);
}
