package org.sitenv.spring.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.transaction.Transactional;

import org.sitenv.spring.dao.DataSourceDao;
import org.sitenv.spring.model.DataSource;
import org.sitenv.spring.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class DataSourceServiceImpl implements DataSourceService {

	@Autowired
	private DataSourceDao dsDao;
	
	public DataSource saveOrUpdate(HashMap<String, String> params, MultipartFile file) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		DataSource ds = mapper.readValue(params.get("ds"), DataSource.class);
		
		if(file!=null) {
			String contextPath = System.getProperty("catalina.base");
			String randomString = CommonUtil.generateRandomString(5);
			String mainDirPath = "/Private Certs/"+randomString+"/";
			
			File dir = new File(contextPath+mainDirPath);
        	if(!dir.exists()){
        		dir.mkdirs();
        	}
        	
        	String keyLocation = contextPath+mainDirPath+file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            BufferedOutputStream buffStream = 
                    new BufferedOutputStream(new FileOutputStream(new File(keyLocation)));
            buffStream.write(bytes);
            buffStream.close();
            
            ds.setKeyLocation(keyLocation);
           
		}
		
		 return dsDao.saveOrUpdate(ds);
	}

	public DataSource getDataSourceById(Integer id) {
		
		return dsDao.getDataSourceById(id);
	}

}
