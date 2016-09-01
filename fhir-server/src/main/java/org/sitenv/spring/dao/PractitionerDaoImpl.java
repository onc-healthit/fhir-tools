package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPractitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("practitionerDao")
public class PractitionerDaoImpl extends AbstractDao implements PractitionerDao {

    private static final Logger logger = LoggerFactory.getLogger(PractitionerDaoImpl.class);

    @Override
    public DafPractitioner getPractitionerById(int id) {
        DafPractitioner dafPractitioner = (DafPractitioner) getSession().get(DafPractitioner.class, id);
        return dafPractitioner;
    }

}
