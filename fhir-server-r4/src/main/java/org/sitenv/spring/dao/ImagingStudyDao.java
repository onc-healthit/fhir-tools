package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ImagingStudyDao {

	DafImagingStudy getImagingStudyById(int id);
	
	DafImagingStudy getImagingStudyByVersionId(int theId, String versionId);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(int id);


}
