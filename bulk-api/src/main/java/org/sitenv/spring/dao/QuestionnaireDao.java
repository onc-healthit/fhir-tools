package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafQuestionnaire;

public interface QuestionnaireDao {
	
	public Integer saveQuestionnaire(DafQuestionnaire dafQuestionnaire);
	
	public DafQuestionnaire getQuestionnaireById(Integer questionnaireId);
	
	public List<DafQuestionnaire> getAllQuestionnaires();

}
