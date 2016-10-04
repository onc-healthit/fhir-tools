package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafQuestionnaireResponse;
import org.springframework.stereotype.Repository;

@Repository("questionnaireResponseDao")
public class QuestionnaireResponseDaoImpl extends AbstractDao implements QuestionnaireResponseDao {

	Session session = null;
	
	@Override
	public Integer saveQuestionnaire(DafQuestionnaireResponse dafQuestionnaireResponse) {
		session = getSession();
        session.saveOrUpdate(dafQuestionnaireResponse);
        session.flush();
		return dafQuestionnaireResponse.getQuestionnaireResponseId();
	}

	@Override
	public DafQuestionnaireResponse getQuestionnaireResponseById(String questionnaireResponseId) {
		//DafQuestionnaireResponse dafQuestionnaireResponse = (DafQuestionnaireResponse) getSession().get(DafQuestionnaireResponse.class, questionnaireResponseId);
		Criteria criteria = getSession().createCriteria(DafQuestionnaireResponse.class);
		criteria.add(Restrictions.eq("response_id", questionnaireResponseId));
		return (DafQuestionnaireResponse) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafQuestionnaireResponse> getAllQuestionnaireResponses() {
		Criteria criteria = getSession().createCriteria(DafQuestionnaireResponse.class);
        return (List<DafQuestionnaireResponse>) criteria.list();
	}

	@Override
	public List<DafQuestionnaireResponse> getQuestionnaireResponsesForQuestionnaire(
			Integer questionnaireId) {
		Criteria criteria = getSession().createCriteria(DafQuestionnaireResponse.class);
		criteria.add(Restrictions.eq("questionnaire_id", questionnaireId));
		criteria.addOrder(Order.asc("questionnaireResponseId"));
        return (List<DafQuestionnaireResponse>) criteria.list();
	}

}
