package org.sitenv.spring.service;

import java.io.IOException;
import java.util.HashMap;

import org.sitenv.spring.model.DataSource;
import org.springframework.web.multipart.MultipartFile;

public interface DataSourceService {
	
	DataSource saveOrUpdate(HashMap<String, String> params, MultipartFile file) throws IOException;
	
	DataSource getDataSourceById(Integer id);

}
