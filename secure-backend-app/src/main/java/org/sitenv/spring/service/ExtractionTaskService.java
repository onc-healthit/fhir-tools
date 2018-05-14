package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;

public interface ExtractionTaskService {
	
	public ExtractionTask saveOrUpdate(ExtractionTask et);
	
	public ExtractionTask getExtractionTaskById(Integer etId);
	
	public List<ExtractionTask> getAllExtractionTasks();
	
	public List<ExtractionTask> getExtractionTasksByProcessFlag(Boolean processFlag);
	
}
