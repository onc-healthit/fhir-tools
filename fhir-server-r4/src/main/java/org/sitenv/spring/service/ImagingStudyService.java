package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;

public interface ImagingStudyService {

	DafImagingStudy getImagingStudyByVersionId(int id, String versionIdPart);

	DafImagingStudy getImagingStudyById(int id);

	List<DafImagingStudy> search(SearchParameterMap paramMap);

	List<DafImagingStudy> getImagingStudyHistoryById(int id);

}
