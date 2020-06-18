package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

public interface ImagingStudyDao {

	DafImagingStudy getImagingStudyById(String id);
	
	DafImagingStudy getImagingStudyByVersionId(String theId, String versionId);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(String id);

	List<DafImagingStudy> getImagingStudyForBulkData(StringJoiner patients, Date start);

}
