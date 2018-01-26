package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafQuestionnaireResponse;;

public interface QuestionnaireResponseDao {
	
	public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse);
	
	public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId);
	
	public List<DafQuestionnaireResponse> getAllQuestionnaireResponses();
	
	public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(Integer questionnaireId);

}
