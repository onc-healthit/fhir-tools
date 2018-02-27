package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.sitenv.spring.model.DafQuestionnaire;
import org.springframework.stereotype.Repository;

@Repository("questionnaireDao")
public class QuestionnaireDaoImpl extends AbstractDao implements QuestionnaireDao {
	
	Session session = null;

	@Override
	public Integer saveQuestionnaire(DafQuestionnaire dafQuestionnaire) {
		 session = getSession();
         session.saveOrUpdate(dafQuestionnaire);
         session.flush();
		return dafQuestionnaire.getId();
	}

	@Override
	public DafQuestionnaire getQuestionnaireById(Integer questionnaireId) {
		DafQuestionnaire dafQuestionnaire = (DafQuestionnaire) getSession().get(DafQuestionnaire.class, questionnaireId);
		return dafQuestionnaire;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafQuestionnaire> getAllQuestionnaires() {
		 Criteria criteria = getSession().createCriteria(DafQuestionnaire.class);
	        return (List<DafQuestionnaire>) criteria.list();
	}

}
