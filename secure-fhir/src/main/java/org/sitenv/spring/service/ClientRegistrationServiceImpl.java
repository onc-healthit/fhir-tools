package org.sitenv.spring.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.dao.ClientRegistrationDao;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("clientRegistrationService")
@Transactional
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    @Autowired(required = true)
    private ClientRegistrationDao clientDao;

    @Override
    public DafClientRegister registerClient(DafClientRegister client) throws OAuthSystemException, FHIRHapiException {

        String registerToken = CommonUtil.generateRandomString(250);
        client.setRegister_token(registerToken);

        String client_id = CommonUtil.generateRandomString(30);
        client.setClient_id(client_id);

        String client_secret = CommonUtil.base64Encoder(CommonUtil.generateRandomString(50));
        client.setClient_secret(client_secret);

        return clientDao.registerClient(client);
    }

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException  {

        return clientDao.updateClient(client);
    }
    
    @Override
    @Transactional
    public DafClientRegister getClientByDetails(String clientId,
                                                String regtoken) {

        return clientDao.getClientByDetails(clientId, regtoken);
    }

    @Override
    @Transactional
    public DafClientRegister getClientByCredentials(String clientId,
                                                    String clientSecret) {
        return clientDao.getClientByCredentials(clientId, clientSecret);
    }

    @Override
    public DafClientRegister getClient(String clientId) {
        return clientDao.getClient(clientId);
    }

	@Override
	public List<DafClientRegister> getClientsByUserId(Integer userId) {
		return clientDao.getClientsByUserId(userId);
	}

	@Override
	public DafClientRegister registerBackendClient(HashMap<String,String> params, MultipartFile[] files,HttpServletRequest request) throws FHIRHapiException {
		
		DafClientRegister backendClient = new DafClientRegister();
		StringBuilder fileNames = new StringBuilder();
    	String contextPath = System.getProperty("catalina.base");
		String mainDirPath = "/BackendAPIData/certs/"+CommonUtil.generateRandomString(5)+"/";
    	for (int i = 0; i < files.length; i++) {
    		String fileName=null;
            MultipartFile file = files[i];
            try {
                if (!file.isEmpty()) {
                       	File dir = new File(contextPath+mainDirPath);
                       	if(!dir.exists()){
                       		dir.mkdirs();
                       	}
                    	fileName = file.getOriginalFilename();
                        byte[] bytes = file.getBytes();
                        BufferedOutputStream buffStream = 
                                new BufferedOutputStream(new FileOutputStream(new File(contextPath+mainDirPath+ fileName)));
                        buffStream.write(bytes);
                        buffStream.close();
                        
                }
                    
                }catch (Exception e) {
                System.out.println("You failed to upload " +fileName+  " => " + e.getMessage()); 
            }
            fileNames.append(fileName);
            if(i!=files.length-1) {
            	fileNames.append(",");
            }
        }
    	backendClient.setDirPath(mainDirPath);
    	backendClient.setFiles(fileNames.toString());
    	backendClient.setIsBackendClient(true);
    	backendClient.setName(params.get("name"));
    	backendClient.setOrg_name(params.get("org_name"));
    	if(params.get("userId") != null){
    		backendClient.setUserId(Integer.parseInt(params.get("userId")));	
    	}
    	backendClient.setIssuer(params.get("issuer"));
    	backendClient.setScope(params.get("scope"));
    	
    	
    	String uri = request.getScheme() + "://" +
                request.getServerName() + 
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                +request.getContextPath();
    	backendClient.setToken_url(uri+"/token");
		String client_id = backendClient.getName()+CommonUtil.generateRandomString(10);
        backendClient.setClient_id(client_id);
        String client_secret = "";
        backendClient.setClient_secret(client_secret);
        
        return clientDao.registerClient(backendClient);
	}

}
