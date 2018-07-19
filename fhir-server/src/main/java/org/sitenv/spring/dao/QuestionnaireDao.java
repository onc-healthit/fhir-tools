package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafQuestionnaire;

import java.util.List;

public interface QuestionnaireDao {

    public Integer saveQuestionnaire(DafQuestionnaire dafQuestionnaire);

    public DafQuestionnaire getQuestionnaireById(Integer questionnaireId);

    public List<DafQuestionnaire> getAllQuestionnaires();

}
