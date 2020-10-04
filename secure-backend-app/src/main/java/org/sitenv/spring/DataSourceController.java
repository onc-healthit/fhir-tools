package org.sitenv.spring;

import java.io.IOException;
import java.util.HashMap;

import org.sitenv.spring.model.DataSource;
import org.sitenv.spring.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/datasource")
public class DataSourceController {
	
	@Autowired
	private DataSourceService dsService;
	
	@RequestMapping(value="/secure/", method=RequestMethod.POST)
	@ResponseBody
	public DataSource saveOrUpdateForSecure(@RequestParam HashMap<String, String> params) throws IOException {
		return dsService.saveOrUpdate(params, null);
	}
	
	@RequestMapping(value="/open/", method=RequestMethod.POST)
	@ResponseBody
	public DataSource saveOrUpdate(@RequestParam HashMap<String, String> params) throws IOException {
		
		return dsService.saveOrUpdate(params,null);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody
	public DataSource getDataSourceById(@PathVariable Integer id) {
		
		return dsService.getDataSourceById(id);
	}
	
	@RequestMapping(value="/model", method=RequestMethod.GET)
	@ResponseBody
	public DataSource getData() {
		
		return new DataSource();
	}

}
