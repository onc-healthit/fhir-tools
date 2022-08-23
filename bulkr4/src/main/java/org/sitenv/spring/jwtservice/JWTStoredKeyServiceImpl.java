package org.sitenv.spring.jwtservice;

import com.google.common.base.Strings;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import org.sitenv.spring.keystore.JWKSStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class JWTStoredKeyServiceImpl implements JWTStoredKeyService{
	// map of identifier to signer
	private Map<String, JWSSigner> signers = new HashMap<>();

	// map of identifier to verifier
	private Map<String, JWSVerifier> verifiers = new HashMap<>();

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);

	private String defaultSignerKeyId;

	private JWSAlgorithm defaultAlgorithm;

	// map of identifier to key
	private Map<String, JWK> keys = new HashMap<>();

	/**
	 * Build this service based on the keys given. All public keys will be used
	 * to make verifiers, all private keys will be used to make signers.
	 *
	 * @param keys
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public JWTStoredKeyServiceImpl(Map<String, JWK> keys) throws NoSuchAlgorithmException, InvalidKeySpecException {
		this.keys = keys;
		buildSignersAndVerifiers();
	}

	/**
	 * Build this service based on the given keystore. All keys must have a key
	 * 
	 * @param keyStore
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public JWTStoredKeyServiceImpl(JWKSStore keyStore) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// convert all keys in the keystore to a map based on key id
		if (keyStore!= null && keyStore.getJwkSet() != null) {
			for (JWK key : keyStore.getKeys()) {
				if (!Strings.isNullOrEmpty(key.getKeyID())) {
					// use the key ID that's built into the key itself
					this.keys.put(key.getKeyID(), key);
				} else {
					// create a random key id
					String fakeKid = UUID.randomUUID().toString();
					this.keys.put(fakeKid, key);
				}
			}
		}
		buildSignersAndVerifiers();
	}


	/**
	 * @return the defaultSignerKeyId
	 */
	@Override
	public String getDefaultSignerKeyId() {
		return defaultSignerKeyId;
	}

	/**
	 * @param defaultSignerKeyId the defaultSignerKeyId to set
	 */
	public void setDefaultSignerKeyId(String defaultSignerId) {
		this.defaultSignerKeyId = defaultSignerId;
	}

	/**
	 * @return
	 */
	@Override
	public JWSAlgorithm getDefaultSigningAlgorithm() {
		return defaultAlgorithm;
	}

	public void setDefaultSigningAlgorithmName(String algName) {
		defaultAlgorithm = JWSAlgorithm.parse(algName);
	}

	public String getDefaultSigningAlgorithmName() {
		if (defaultAlgorithm != null) {
			return defaultAlgorithm.getName();
		} else {
			return null;
		}
	}

	/**
	 * Build all of the signers and verifiers for this based on the key map.
	 * @throws 
	 * @throws 
	 */
	private void buildSignersAndVerifiers() throws NoSuchAlgorithmException, InvalidKeySpecException {
		for (Map.Entry<String, JWK> jwkEntry : keys.entrySet()) {
		

			String id = jwkEntry.getKey();
			JWK jwk = jwkEntry.getValue();

			try {
				if (jwk instanceof RSAKey) {
					// build RSA signers & verifiers

					if (jwk.isPrivate()) { // only add the signer if there's a private key
						RSASSASigner signer = new RSASSASigner((RSAKey) jwk);
						signers.put(id, signer);
					}

					RSASSAVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
					verifiers.put(id, verifier);

				} else if (jwk instanceof ECKey) {
					// build EC signers & verifiers

					if (jwk.isPrivate()) {
						ECDSASigner signer = new ECDSASigner((ECKey) jwk);
						signers.put(id, signer);
					}

					ECDSAVerifier verifier = new ECDSAVerifier((ECKey) jwk);
					verifiers.put(id, verifier);

				} else if (jwk instanceof OctetSequenceKey) {
					// build HMAC signers & verifiers

					if (jwk.isPrivate()) { // technically redundant check because all HMAC keys are private
						MACSigner signer = new MACSigner((OctetSequenceKey) jwk);
						signers.put(id, signer);
					}

					MACVerifier verifier = new MACVerifier((OctetSequenceKey) jwk);
					verifiers.put(id, verifier);

				} else {
					logger.warn("Unknown key type: " + jwk);
				}
			} catch (JOSEException e) {
				logger.warn("Exception loading signer/verifier", e);
			}
		}

		if (defaultSignerKeyId == null && keys.size() == 1) {
			setDefaultSignerKeyId(keys.keySet().iterator().next());
		}
	}

	
	@Override
	public void signJwt(SignedJWT jwt) {
		if (getDefaultSignerKeyId() == null) {
			throw new IllegalStateException("Tried to call default signing with no default signer ID set");
		}

		JWSSigner signer = signers.get(getDefaultSignerKeyId());

		try {
			jwt.sign(signer);
		} catch (JOSEException e) {

			logger.error("Failed to sign JWT, error was: ", e);
		}

	}

	@Override
	public void signJwt(SignedJWT jwt, JWSAlgorithm alg) {

		JWSSigner signer = null;

		for (JWSSigner s : signers.values()) {
			if (s.supportedJWSAlgorithms().contains(alg)) {
				signer = s;
				break;
			}
		}

		if (signer == null) {
			logger.error("No matching algirthm found for alg=" + alg);

		}

		try {
			jwt.sign(signer);
		} catch (JOSEException e) {

			logger.error("Failed to sign JWT, error was: ", e);
		}

	}

	@Override
	public boolean validateSignature(SignedJWT jwt) {

		for (JWSVerifier verifier : verifiers.values()) {
			try {
				if (jwt.verify(verifier)) {
					return true;
				}
			} catch (JOSEException e) {

				logger.error("Failed to validate signature with " + verifier + " error message: " + e.getMessage());
			}
		}
		return false;
	}

	@Override
	public Map<String, JWK> getAllPublicKeys() {
		Map<String, JWK> pubKeys = new HashMap<>();

		for (String keyId : keys.keySet()) {
			JWK key = keys.get(keyId);
			JWK pub = key.toPublicJWK();
			if (pub != null) {
				pubKeys.put(keyId, pub);
			}
		}

		return pubKeys;
	}

	
	@Override
	public Collection<JWSAlgorithm> getAllSigningAlgsSupported() {

		Set<JWSAlgorithm> algs = new HashSet<>();

		for (JWSSigner signer : signers.values()) {
			algs.addAll(signer.supportedJWSAlgorithms());
		}

		for (JWSVerifier verifier : verifiers.values()) {
			algs.addAll(verifier.supportedJWSAlgorithms());
		}

		return algs;

	}

}
