package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ImagingStudyDao {

	DafImagingStudy getImagingStudyById(String id);
	
	DafImagingStudy getImagingStudyByVersionId(String theId, String versionId);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(String id);


}
