package org.sitenv.spring.service;

import org.sitenv.spring.model.DafQuestionnaire;

import java.util.List;

public interface QuestionnaireService {

    public Integer saveQuestionnaire(DafQuestionnaire dafQuestionnaire);

    public DafQuestionnaire getQuestionnaireById(Integer questionnaireId);

    public List<DafQuestionnaire> getAllQuestionnaires();
}
