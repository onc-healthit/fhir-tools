package org.sitenv.spring.service;

import org.sitenv.spring.model.DafQuestionnaireResponse;

import java.util.List;

public interface QuestionnaireResponseService {

    public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse);

    public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId);

    public List<DafQuestionnaireResponse> getAllQuestionnaireResponses();

    public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(Integer questionnaireId);

}
