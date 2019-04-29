package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;

public interface FamilyMemberHistoryService {
	
	public DafFamilyMemberHistory getFamilyMemberHistoryById(int id);
	
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId);

	public List<DafFamilyMemberHistory> search(SearchParameterMap paramMap);
	
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(int theId);

}
