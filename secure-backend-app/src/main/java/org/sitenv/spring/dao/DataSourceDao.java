package org.sitenv.spring.dao;

import org.sitenv.spring.model.DataSource;

public interface DataSourceDao {
	
	DataSource saveOrUpdate(DataSource ds);
	
	DataSource getDataSourceById(Integer id);

}
