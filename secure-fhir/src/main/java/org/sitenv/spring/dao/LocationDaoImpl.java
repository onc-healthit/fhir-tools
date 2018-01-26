package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.query.LocationSearchCriteria;
import org.springframework.stereotype.Repository;

@Repository("locationDao")
public class LocationDaoImpl extends AbstractDao implements LocationDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<DafLocation> getAllLocations() {
		 Criteria criteria = getSession().createCriteria(DafLocation.class);
	        return (List<DafLocation>) criteria.list();
	}

	@Override
	public DafLocation getLocationById(int id) {
		DafLocation dafLocation = (DafLocation) getSession().get(DafLocation.class, id);
	        return dafLocation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafLocation> getLocationBySearchCriteria(
			LocationSearchCriteria locationSearchCriteria) {
		Criteria criteria = getSession().createCriteria(DafLocation.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //Identifier
        if (locationSearchCriteria.getIdentifier() != null) {
            if (locationSearchCriteria.getIdentifier().getSystem() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'system' = '" + locationSearchCriteria.getIdentifier().getSystem() + "'"));
            }
            if (locationSearchCriteria.getIdentifier().getValue() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'value' = '" + locationSearchCriteria.getIdentifier().getValue() + "'"));
            }
        }
        
        if(locationSearchCriteria.getName()!=null){
        	criteria.add(Restrictions.eq("name", locationSearchCriteria.getName()));
        }
        
        if(locationSearchCriteria.getAddress()!=null){
        	criteria.add(Restrictions.sqlRestriction("{alias}.address->>'line' = '" + locationSearchCriteria.getAddress() + "'"));
        }
        
        if(locationSearchCriteria.getPostal()!=null){
        	criteria.add(Restrictions.sqlRestriction("{alias}.address->>'postalCode' = '" + locationSearchCriteria.getPostal() + "'"));
        }
        
        if(locationSearchCriteria.getCity()!=null){
        	criteria.add(Restrictions.sqlRestriction("{alias}.address->>'city' = '" + locationSearchCriteria.getCity() + "'"));
        }
        
        if(locationSearchCriteria.getState()!=null){
        	criteria.add(Restrictions.sqlRestriction("{alias}.address->>'state' = '" + locationSearchCriteria.getState() + "'"));
        }
        
        return criteria.list();
	}
	
	public List<DafLocation> getLocationForBulkData(List<Integer> patients, Date start){
		
    	Criteria criteria = getSession().createCriteria(DafLocation.class, "location");
               // .createAlias("location.patient", "dp");
    			/*if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}*/
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    
	}

}
