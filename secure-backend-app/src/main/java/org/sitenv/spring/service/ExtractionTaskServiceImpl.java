package org.sitenv.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.sitenv.spring.dao.ExtractionTaskDao;
import org.sitenv.spring.model.ExtractionTask;

@Service
@Transactional
public class ExtractionTaskServiceImpl implements ExtractionTaskService {
	
	@Autowired
	private ExtractionTaskDao etDao;

	public ExtractionTask saveOrUpdate(ExtractionTask et) {
		
		return etDao.saveOrUpdate(et);
	}

	public ExtractionTask getExtractionTaskById(Integer etId) {
		
		return etDao.getExtractionTaskById(etId);
	}

	public List<ExtractionTask> getAllExtractionTasks() {
		
		return etDao.getAllExtractionTasks();
	}

	public List<ExtractionTask> getExtractionTasksByProcessFlag(Boolean processFlag) {
		
		return etDao.getExtractionTasksByProcessFlag(processFlag);
	}

}
