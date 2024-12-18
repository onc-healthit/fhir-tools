package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface FamilyMemberHistoryDao {
	
	public DafFamilyMemberHistory getFamilyMemberHistoryById(String id);
	
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(String theId, String versionId);

	public List<DafFamilyMemberHistory> search(SearchParameterMap paramMap);
	
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(String theId);

	public List<DafFamilyMemberHistory> getFamilyMemberHistoryForBulkData(StringJoiner patient, Date start);
}
