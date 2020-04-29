package org.sitenv.spring.auth;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.RandomStringUtils;
import org.sitenv.spring.model.Jwks;
import org.sitenv.spring.service.JwksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/*
 * This class generate JWT id token which is signed by the issuer using
 * private key. 
 *
 */

@Component
public class JwtGenerator {
	RSAKey rsaJWK = null;
	JWK jwk = null;
	Jwks jwks = null;
		
    Logger log = (Logger) LoggerFactory.getLogger(JwtGenerator.class);
    
	@Autowired
	private JwksService jwksService;

	// map of identifier to signer
	private Map<String, JWSSigner> signers = new HashMap<>();

	// map of identifier to verifier
	private Map<String, JWSVerifier> verifiers = new HashMap<>();
	JSONObject jsonObject = null;
	String timeStamp = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").format(Calendar.getInstance().getTime());

	/** Method to generate the IdToken
	 * 
	 * @param payloadData
	 * @param request
	 * @return
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public String generate(Map<String, Object> payloadData, HttpServletRequest request)
			throws JOSEException, ParseException, KeyStoreException, IOException, ParserConfigurationException,
			NoSuchAlgorithmException, InvalidKeySpecException {

		String baseUrl = Common.getBaseUrl(request);
		//Private key generator
			for (int i = 1; i <= 1; i++) {
			jwks = jwksService.getById(i);
			Date lastUpdatedtime = jwks.getLastUpdatedDatetime();
			Integer idTokenExpiryTime = Common.convertTimestampToUnixTime(lastUpdatedtime.toString());
			idTokenExpiryTime += (60 * 60 * 24 * 7);
			Integer currentTime = Common.convertTimestampToUnixTime(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));
			if (currentTime >= idTokenExpiryTime) {
				String kid = RandomStringUtils.randomAlphanumeric(10);
				RSAKey rsaKey = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID(kid).algorithm(JWSAlgorithm.RS256).generate();
				jwks=jwksService.getById(i);
				jwks.setJwk(rsaKey.toString());
				jwks.setLastUpdatedDatetime(Common.convertToDateFormat(timeStamp));
				jwksService.saveOrUpdate(jwks);
			}
		}

		//Random rand = new Random();

		// Obtain a number between [1 - 3].
		//int n = rand.nextInt(2);
		//n += 1;
		//System.out.println("random" + n);
		//select key to sing the jwt 	
		jwks = jwksService.getById(1);
		String j = jwks.getJwk();
		jsonObject = JSONObjectUtils.parse(j);
		jwk = JWK.parse(jsonObject);// return the instance of JWK
		String id = (String) jsonObject.get("kid");
		buildSignerAndVerifier(jwk, id);

		// Prepare JWT with claims set
		JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder().subject((String) payloadData.get("sub"))
				.claim("email", payloadData.get("email"))
				.claim("userName", payloadData.get("userName"))
				.claim("fhirUser", baseUrl+"fhir/Patient/"+payloadData.get("fhirUser"))
				.issuer(baseUrl)
				.audience((String) payloadData.get("aud"))
				.issueTime((Date) payloadData.get("issueDate"))
				.expirationTime((Date) payloadData.get("expiryTime"))
				.jwtID(UUID.randomUUID().toString()) // unique identifier for JWT
				.build();

		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build(),
				jwtClaims);

		// Compute the RSA signature
		signedJWT.sign((JWSSigner) signers.get(id));
		String jwtToken = signedJWT.serialize();
		return jwtToken;

	}
	
	
	/**This function build and verify the key
	 * 
	 * @param jwk
	 * @param id
	 */
	public void buildSignerAndVerifier(JWK jwk, String id) {
		Map<String, JWK> keys = ImmutableMap.of(id, jwk);
		for (Map.Entry<String, JWK> jwkEntry : keys.entrySet()) {

			String idk = jwkEntry.getKey();
			jwk = jwkEntry.getValue();

			try {
				if (jwk instanceof RSAKey) {
					// build RSA signers & verifiers

					if (jwk.isPrivate()) { // only add the signer if there's a private key
						RSASSASigner signer = new RSASSASigner((RSAKey) jwk);
						signers.put(idk, signer);
					}

					RSASSAVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
					verifiers.put(idk, verifier);

				} else if (jwk instanceof ECKey) {
					// build EC signers & verifiers

					if (jwk.isPrivate()) {
						ECDSASigner signer = new ECDSASigner((ECKey) jwk);
						signers.put(idk, signer);
					}

					ECDSAVerifier verifier = new ECDSAVerifier((ECKey) jwk);
					verifiers.put(idk, verifier);

				} else if (jwk instanceof OctetSequenceKey) {
					// build HMAC signers & verifiers

					if (jwk.isPrivate()) { // technically redundant check because all HMAC keys are private
						MACSigner signer = new MACSigner((OctetSequenceKey) jwk);
						signers.put(idk, signer);
					}

					MACVerifier verifier = new MACVerifier((OctetSequenceKey) jwk);
					verifiers.put(idk, verifier);

				} else {
					 log.warn("Unknown key type: " + jwk);
				}
			} catch (JOSEException e) {
				 log.warn("Exception loading signer/verifier", e);
			}
		}
	}
	
	/**This method will fetch the public key and publish it to jwks_uri
	 * 
	 * @return
	 */
	public Map<String, List<JSONObject>> getAllPublicKeys() {
		JSONParser parser = new JSONParser();
		List<JWK> list = new ArrayList<>();
		List<JSONObject> publicKeyList = new ArrayList<>();
		try {

			for (int i = 1; i <= 1; i++) {
				Jwks jwks = jwksService.getById(i);
				String s = jwks.getJwk();
				Object object = parser.parse(s);

				// convert Object to JSONObject
				JSONObject jsonObject = (JSONObject) object;
				rsaJWK = RSAKey.parse(jsonObject);

				JWK publicKey = rsaJWK.toPublicJWK();
				list.add(publicKey);
				publicKeyList.add(publicKey.toJSONObject());
			}
		} catch (Exception e) {
			log.warn("Exception loading key", e);
		}
		Map<String, List<JSONObject>> keys = new HashMap<>();
		keys.put("keys", publicKeyList);
		return keys;
	}
}
