package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafQuestionnaireResponse;

import java.util.List;



public interface QuestionnaireResponseDao {

    public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse);

    public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId);

    public List<DafQuestionnaireResponse> getAllQuestionnaireResponses();

    public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(Integer questionnaireId);

}
