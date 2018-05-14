package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;

public interface ExtractionTaskDao {
	
	public ExtractionTask saveOrUpdate(ExtractionTask et);
	
	public ExtractionTask getExtractionTaskById(Integer etId);
	
	public List<ExtractionTask> getAllExtractionTasks();
	
	public List<ExtractionTask> getExtractionTasksByProcessFlag(Boolean processFlag);
	
	//public List<ExtractionTask> getExtractionTasksByDataSourceIdAndGroupId(Integer dsId, Integer groupId);

}
