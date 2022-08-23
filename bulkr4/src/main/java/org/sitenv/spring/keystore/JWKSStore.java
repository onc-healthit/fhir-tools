package org.sitenv.spring.keystore;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

public class JWKSStore {

	private JWKSet jwkSet;

	private Resource location;

	public JWKSStore() {

	}
	//loading public key
	public JWKSStore(JWKSet jwkSet) {
		this.jwkSet = jwkSet;
		initializeJwkSet();
	}

	private void initializeJwkSet() {

		if (jwkSet == null) {
			if (location != null) {

				if (location.exists() && location.isReadable()) {

					try {
						// read in the file from disk
						String s = CharStreams.toString(new InputStreamReader(location.getInputStream(), Charsets.UTF_8));

						// parse it into a jwkSet object
						jwkSet = JWKSet.parse(s);
					} catch (IOException e) {
						throw new IllegalArgumentException("Key Set resource could not be read: " + location);
					} catch (ParseException e) {
						throw new IllegalArgumentException("Key Set resource could not be parsed: " + location);                    }

				} else {
					throw new IllegalArgumentException("Key Set resource could not be read: " + location);
				}

			} else {
				throw new IllegalArgumentException("Key store must be initialized with at least one of a jwkSet or a location.");
			}
		}
	}

	
	public JWKSet getJwkSet() {
		return jwkSet;
	}

	
	public void setJwkSet(JWKSet jwkSet) {
		this.jwkSet = jwkSet;
		initializeJwkSet();
	}

	public Resource getLocation() {
		return location;
	}

	
	public void setLocation(Resource location) {
		this.location = location;
		initializeJwkSet();
	}

	
	public List<JWK> getKeys() {
		if (jwkSet == null) {
			initializeJwkSet();
		}
		return jwkSet.getKeys();
	}



}
