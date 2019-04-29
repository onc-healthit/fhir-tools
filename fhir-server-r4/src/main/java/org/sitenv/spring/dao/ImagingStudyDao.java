package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

public interface ImagingStudyDao {

	DafImagingStudy getImagingStudyById(int id);
	
	DafImagingStudy getImagingStudyByVersionId(int theId, String versionId);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(int id);


}
