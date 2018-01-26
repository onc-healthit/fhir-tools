package org.sitenv.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.sitenv.spring.dao.QuestionnaireResponseDao;
import org.sitenv.spring.model.DafQuestionnaireResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service("questionnaireResponseService")
public class QuestionnaireResponseServiceImpl implements QuestionnaireResponseService {
	
	@Autowired
	private QuestionnaireResponseDao dao;

	@Override
	public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse) {
		
		return this.dao.saveQuestionnaire(dafQuestionnaireResponse);
	}

	@Override
	public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId) {
		
		return this.dao.getQuestionnaireResponseById(questionnaireResponseId);
	}

	@Override
	public List<DafQuestionnaireResponse> getAllQuestionnaireResponses() {
		
		return this.dao.getAllQuestionnaireResponses();
	}

	@Override
	public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(
			Integer questionnaireId) {
		
		return this.dao.getQuestionnaireResponsesForQuestionnaire(questionnaireId);
	}

}
