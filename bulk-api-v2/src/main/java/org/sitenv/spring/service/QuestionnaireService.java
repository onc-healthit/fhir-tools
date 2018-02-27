package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafQuestionnaire;

public interface QuestionnaireService {

	public Integer saveQuestionnaire(DafQuestionnaire dafQuestionnaire);
	
	public DafQuestionnaire getQuestionnaireById(Integer questionnaireId);
	
	public List<DafQuestionnaire> getAllQuestionnaires();
}
