package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafQuestionnaireResponse;

public interface QuestionnaireResponseService {
	
	public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse);
	
	public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId);
	
	public List<DafQuestionnaireResponse> getAllQuestionnaireResponses();
	
	public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(Integer questionnaireId);

}
