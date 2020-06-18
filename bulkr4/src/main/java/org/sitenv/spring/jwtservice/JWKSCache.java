package org.sitenv.spring.jwtservice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.sitenv.spring.keystore.JWKSStore;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.service.ClientRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.jwk.JWKSet;

@Service
public class JWKSCache {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JWKSCache.class);

	@Autowired
	private ClientRegistrationService clientRegistrationService;

	// map of jwk set uri -> signing/validation service built on the keys found in
	// that jwk set
	private LoadingCache<String, JWTService> validators;

	public JWKSCache() {
		String clientId;
		this.validators = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS) // expires 1 hour after fetch
				.maximumSize(100)
				.build(new JWKSetVerifierFetcher(HttpClientBuilder.create().useSystemProperties().build()));

	}

	/**
	 * @param jwksUri
	 * @return
	 * @throws ExecutionException
	 * @see com.google.common.cache.Cache#get(java.lang.Object)
	 */
	public JWTService getValidator(String jwksUri) {
		try {
			return validators.get(jwksUri);
		} catch (UncheckedExecutionException | ExecutionException e) {
			logger.warn("Couldn't load JWK Set from " + jwksUri + ": " + e.getMessage());
			return null;
		}
	}
	
	private class JWKSetVerifierFetcher extends CacheLoader<String, JWTService> {
		private HttpComponentsClientHttpRequestFactory httpFactory;
		private RestTemplate restTemplate;

		JWKSetVerifierFetcher(HttpClient httpClient) {
			this.httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			this.restTemplate = new RestTemplate(httpFactory);
		}

		@Override
		public JWTService load(String key) throws Exception {
			String jsonString = restTemplate.getForObject(key, String.class);
			JWKSet jwkSet = JWKSet.parse(jsonString);

			JWKSStore keyStore = new JWKSStore(jwkSet);

			JWTService service = new JWTServiceImpl(keyStore);

			return service;
		}
	}

	public JWTStoredKeyService loadStoredPublicKey(String clientId) throws Exception {
		DafClientRegister client = clientRegistrationService.getClient(clientId);
		if (client != null) {
			JWKSet jwkSet = JWKSet.parse(client.getDirPath());
			JWKSStore keyStore = new JWKSStore(jwkSet);
			JWTStoredKeyService service = new JWTStoredKeyServiceImpl(keyStore);
			return service;
		} else {
			return null;
		}
	}

}
