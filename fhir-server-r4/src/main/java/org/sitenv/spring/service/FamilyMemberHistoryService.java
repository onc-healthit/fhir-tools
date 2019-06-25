package org.sitenv.spring.service;

import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface FamilyMemberHistoryService {
	
	public DafFamilyMemberHistory getFamilyMemberHistoryById(int id);
	
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId);

	public List<DafFamilyMemberHistory> search(SearchParameterMap paramMap);
	
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(int theId);

}
