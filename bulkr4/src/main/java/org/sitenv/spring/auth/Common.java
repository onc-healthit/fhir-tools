package org.sitenv.spring.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.service.ClientRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class Common {
	static Logger logger = (Logger) LoggerFactory.getLogger(Common.class);

	@Autowired
	private ClientRegistrationService clientRegistrationService;

	public static Integer convertTimestampToUnixTime(String timestamp) throws ParseException {
		if (timestamp != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

			int epoch = (int) (sdf.parse(timestamp).getTime() / 1000);

			return epoch;
		} else {
			return null;
		}
	}

	public boolean isValid(JWT jsonWebToken) throws ParseException {

		if (!(jsonWebToken instanceof SignedJWT)) {
			// unsigned assertion
			return false;
		}
		// else {

		JWTClaimsSet claims;
		try {
			claims = jsonWebToken.getJWTClaimsSet();
		} catch (ParseException e) {
			logger.debug("Invalid assertion claims");
			return false;
		}

		if (claims == null) {
			logger.debug("No claims found ");
			return false;
		}

		// make sure the issuer exists
		if (Strings.isNullOrEmpty(claims.getIssuer())) {
			logger.debug("No issuer for assertion, rejecting");
			return false;
		}
		DafClientRegister client = null;
		try {
			client = clientRegistrationService.getClient(claims.getIssuer());
		} catch (Exception e) {
			return false;
		}
		
		if (client == null) {
			logger.debug("issuer is not valid");
			return false;
		}

		if (Strings.isNullOrEmpty(claims.getSubject())) {
			logger.debug("subject not found, rejecting");
			return false;
		}

		try {
			client = clientRegistrationService.getClient(claims.getSubject());
		} catch (Exception e) {
			return false;
		}
		if (client == null) {
			logger.debug("subject is not valid");
			return false;
		}

		/*
		 * // validate the signature based on our public key if
		 * (jwtValidator.validateSignature((SignedJWT) jsonWebToken)) { return true; }
		 */

		return true;
	}

	//verify the requested scopes 
	public boolean isValidScopes(String scopesRequested, List<String> registeredScope, String clientIdFromJWTToken) {
		String scope = scopesRequested.replaceAll("\\s+", ",");
        List<String> reqScopes = Arrays.asList(scope.split(","));
			if (registeredScope.containsAll(reqScopes)) {
				return true;
			}
		return false;
	}

}
