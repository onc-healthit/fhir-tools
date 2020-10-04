package org.sitenv.spring;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.sitenv.spring.model.Jwks;
import org.sitenv.spring.service.JwksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.nimbusds.jose.jwk.KeyType;

/*
 * This class generate JWT id token which is signed by the issuer using
 * private key. RSA stand for Rivest, Shamir, Adleman.
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
	 * @throws URISyntaxException 
	 */
	public String generate(Map<String, Object> payloadData, HttpServletRequest request)
			throws JOSEException, ParseException, KeyStoreException, IOException, ParserConfigurationException,
			NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {

		String baseUrl = Common.getBaseUrl(request);
		//Private key generator
			for (int i = 1; i <= 1; i++) {
			jwks = jwksService.getById(i);
			Date lastUpdatedtime = jwks.getLastUpdatedDatetime();
			Integer idTokenExpiryTime = Common.convertTimestampToUnixTime(lastUpdatedtime.toString());
			idTokenExpiryTime += (60 * 60 * 24 * 70);
			//idTokenExpiryTime += (60);
			Integer currentTime = Common.convertTimestampToUnixTime(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));
			if (currentTime >= idTokenExpiryTime) {
				String kid = RandomStringUtils.randomAlphanumeric(10);
				Set<KeyOperation> ops= new LinkedHashSet<>();
				  //ops.add(KeyOperation.SIGN); 
				 // ops.add(KeyOperation.VERIFY);
				
				
				RSAKey rsaKey = new RSAKeyGenerator(2048)
						.keyOperations(ops)
						//.keyUse(KeyUse.SIGNATURE)
						.keyID(kid).algorithm(JWSAlgorithm.RS384)
						.generate();
				jwks=jwksService.getById(i);
				jwks.setJwk(rsaKey.toString());
//				System.out.println("rsaKey.toString()......"+rsaKey.toString());
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
		
		
		KeyOperation ops= KeyOperation.SIGN;
		jwk.getKeyOperations().add(ops);
//		System.out.println("private key...."+jwk);
		String id = (String) jsonObject.get("kid");
//		System.out.println("id..."+id);
		buildSignerAndVerifier(jwk, id);

		// Prepare JWT with claims set
		JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
				.subject((String) payloadData.get("sub")) //client_id
				.claim("iss", payloadData.get("iss"))     //client_id
				.audience((String) payloadData.get("aud"))
				//.issueTime((Date) payloadData.get("issueDate"))
				.expirationTime((Date) payloadData.get("expiryTime"))
				.jwtID(UUID.randomUUID().toString())  // unique identifier for JWT
				.build();	
		
		URI jku = new URI(baseUrl+"jwk");
		JOSEObjectType typ = new JOSEObjectType("JWT");
		//header being added 
		SignedJWT signedJWT = new SignedJWT(new JWSHeader
				.Builder(JWSAlgorithm.RS384)
				.keyID(jwk.getKeyID()).type(typ)
				//.type()
				//.jwkURL(jku)
				.build(),
				jwtClaims);

		// Compute the RSA signature
		
		
		signedJWT.sign((JWSSigner) signers.get(id));
		String jwtToken = signedJWT.serialize();
//		System.out.println("jwtToken.........."+jwtToken);
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
						//KeyOperation e = KeyOperation.DECRYPT;
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
				rsaJWK =  RSAKey.parse(jsonObject);
				KeyOperation ops = KeyOperation.VERIFY;
				rsaJWK.getKeyOperations().add(ops );
			//	rsaJWK.getKeyOperations().remove(KeyOperation.SIGN);
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