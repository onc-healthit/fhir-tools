package org.sitenv.spring.service;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ImagingStudyService {

	DafImagingStudy getImagingStudyByVersionId(int id, String versionIdPart);

	DafImagingStudy getImagingStudyById(int id);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(int id);

}
