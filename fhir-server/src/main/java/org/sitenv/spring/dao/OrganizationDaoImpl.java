package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.query.OrganizationSearchCriteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("organizationDao")
public class OrganizationDaoImpl extends AbstractDao implements OrganizationDao {

    public DafOrganization getOrganizationResourceById(int id) {
        DafOrganization dafOrganization = (DafOrganization) getSession().get(DafOrganization.class, id);
        return dafOrganization;

    }

    @Override
    public List<DafOrganization> getAllOrganizations() {

        Criteria criteria = getSession().createCriteria(DafOrganization.class);
        return (List<DafOrganization>) criteria.list();
    }

    public List<DafOrganization> getOrganizationBySearchCriteria(OrganizationSearchCriteria criteria) {
        List<DafOrganization> result = getOrganization(criteria);
        return result;
    }

    public List<DafOrganization> getOrganization(OrganizationSearchCriteria searchOptions) {

        Criteria criteria = getSession().createCriteria(DafOrganization.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        // Identifier
        if (searchOptions.getIdentifier() != null) {
            if (searchOptions.getIdentifier().getSystem() != null) {
                criteria.add(Restrictions.sqlRestriction(
                        "{alias}.identifier->>'system' = '" + searchOptions.getIdentifier().getSystem() + "'"));
            }
            if (searchOptions.getIdentifier().getValue() != null) {
                criteria.add(Restrictions.sqlRestriction(
                        "{alias}.identifier->>'value' = '" + searchOptions.getIdentifier().getValue() + "'"));
            }
        }

        // name
        if (StringUtils.isNotBlank(searchOptions.getName())) {
            criteria.add(Restrictions.eq("name", searchOptions.getName()).ignoreCase());
        }

        // address
        if (StringUtils.isNotBlank(searchOptions.getAddress())) {
            criteria.add(
                    Restrictions.sqlRestriction("{alias}.address->>'line' = '" + searchOptions.getAddress() + "'"));
        }

        // address_city
        if (StringUtils.isNotBlank(searchOptions.getAddress_city())) {
            criteria.add(
                    Restrictions.sqlRestriction("{alias}.address->>'city' = '" + searchOptions.getAddress_city() + "'"));
        }

        // address_state
        if (StringUtils.isNotBlank(searchOptions.getAddress_state())) {
            criteria.add(
                    Restrictions.sqlRestriction("{alias}.address->>'state' = '" + searchOptions.getAddress_state() + "'"));
        }

        // address_country
        if (StringUtils.isNotBlank(searchOptions.getAddress_country())) {
            criteria.add(
                    Restrictions.sqlRestriction("{alias}.address->>'country' = '" + searchOptions.getAddress_country() + "'"));
        }

        // address_postalCode
        if (StringUtils.isNotBlank(searchOptions.getAddress_postalCode())) {
            criteria.add(
                    Restrictions.sqlRestriction("{alias}.address->>'postalCode' = '" + searchOptions.getAddress_postalCode() + "'"));
        }

        return criteria.list();

    }

}
