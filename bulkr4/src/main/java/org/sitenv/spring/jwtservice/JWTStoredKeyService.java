package org.sitenv.spring.jwtservice;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;

import java.util.Collection;
import java.util.Map;

public interface JWTStoredKeyService  {

	public Map<String, JWK> getAllPublicKeys();
	public boolean validateSignature(SignedJWT jwtString);
	public void signJwt(SignedJWT jwt);
	public JWSAlgorithm getDefaultSigningAlgorithm();
	public Collection<JWSAlgorithm> getAllSigningAlgsSupported();
	public void signJwt(SignedJWT jwt, JWSAlgorithm alg);
	public String getDefaultSignerKeyId();
	
	
}
