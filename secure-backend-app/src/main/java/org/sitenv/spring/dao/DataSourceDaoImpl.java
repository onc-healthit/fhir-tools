package org.sitenv.spring.dao;

import org.sitenv.spring.model.DataSource;
import org.springframework.stereotype.Repository;

@Repository("DataSourceDao")
public class DataSourceDaoImpl extends AbstractDao implements DataSourceDao {

	public DataSource saveOrUpdate(DataSource ds) {
		
		getSession().saveOrUpdate(ds);
		return ds;
	}

	public DataSource getDataSourceById(Integer id) {
		DataSource dataSource = (DataSource) getSession().get(DataSource.class, id);
		return dataSource;
	}

}
