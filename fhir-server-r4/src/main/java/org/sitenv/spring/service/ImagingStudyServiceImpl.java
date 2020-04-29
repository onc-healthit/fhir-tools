package org.sitenv.spring.service;

import org.sitenv.spring.dao.ImagingStudyDao;
import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("imagingStudyService")
@Transactional
public class ImagingStudyServiceImpl implements ImagingStudyService{

	@Autowired
    private ImagingStudyDao imagingStudyDao;
	
	@Transactional
	@Override
	public DafImagingStudy getImagingStudyById(String id) {
		 return this.imagingStudyDao.getImagingStudyById(id);
	}
	
	@Override
	@Transactional
	public DafImagingStudy getImagingStudyByVersionId(String theId, String versionId) {
		return this.imagingStudyDao.getImagingStudyByVersionId(theId, versionId);
	}

	@Override
	public List<DafImagingStudy> search(SearchParameterMap paramMap) {
		return this.imagingStudyDao.search(paramMap);
	}

	@Override
	public List<DafImagingStudy> getImagingStudyHistoryById(String id) {
		return this.imagingStudyDao.getImagingStudyHistoryById(id);
	}
	
}
