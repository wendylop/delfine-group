package de.sb.broker.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.sb.java.net.HttpAuthenticationCodec;


/**
 * Minimal HTTP authentication demo, authenticating user "sascha" with password "sascha":
 * <ul>
 * <li>/services/authentication/basic: HTTP "Basic authorization"</li>
 * <li>/services/authentication/digest: HTTP "Digest authorization"</li>
 * </ul>
 * Once a user is authenticated, changing the authentication method or the user-alias, or the
 * password, requires a browser cache clearance or restart.<br />
 * <br />
 * Some notes regarding Basic and Digest authentication: With realm set to "", the transferred
 * response for Digest authentication is MD5(MD5(alias::password)::MD5(httpMethod:uri)). The problem
 * with Digest as an authentication feature is that the last MD5 part changes with every request;
 * this is a necessary security feature when using digest authentication over plain HTTP, but
 * actually becomes a big weakness when using HTTPS due to side effects!<br />
 * <br />
 * This algorithm forces the server side to store MD5(alias::password) within the database, in order
 * to calculate the counterpart to the transferred secret. This basically implies that someone
 * gaining unauthorized access to the database can mass download alias->MD5(alias::password)
 * combinations, analyze the application code (or simply try&error) to find out how the overall MD5
 * is calculated, and thereby gain access to the whole application with any user alias. In contrast,
 * using HTTPS in combination with basic authentication allows the server side to store (for
 * example) a strong SHA-256 hash of the password within the database, hash the transferred secret,
 * and use this hash value to authenticate a given user; an attacker who downloaded these hash
 * values cannot use them for anything within the application, because she/he cannot recalculate the
 * required password from it.<br />
 * <br />
 * In the end, any authentication scheme is only as safe as it's weakest link. If we assign relative
 * "security worthiness" for the combinations of transport and storage technology, the difference
 * between Basic and Digest authentication quickly becomes obvious:
 * <ul>
 * <li>min(HTTP + Basic, SHA(secret) == storage) = min(zero + low, high) = low</li>
 * <li>min(HTTPS + Basic, SHA(secret) == storage) = min(high + low, high) = high</li>
 * <li>min(HTTP + Digest, secret == f(storage)) = min(zero + mid, mid) = mid</li>
 * <li>min(HTTPS + Digest, secret == f(storage)) = min(high + mid, mid) = mid</li>
 * </ul>
 * Result: While Digest authentication is much better than Basic when combined with HTTP transport,
 * it is much worse than Basic when combined with HTTPS. Unsurprisingly so, because non-encrypted
 * transport is exactly what Digest authentication was designed for. If strong stored hashes get
 * stolen from an application that uses HTTPS and Basic authentication, it may trigger bad press,
 * but no catastrophe. If the stored hashes get stolen from an application that uses HTTPS and
 * Digest authentication, you'd best reset all user accounts immediately, because your remaining
 * security margin is minimal, and no defense against determined attack.<br />
 * <br />
 * It is a perfect example of how blindly piling up security measures often does not result in
 * stronger security, but rather weakens a system decisively due to side-effects. That it's
 * designers believed Digest would completely supersede Basic authentication (see RFC 2617) is their
 * lasting shame, and no excuse for you to fall for the same illusion. After all, you have been
 * told.
 */
@Path("authentication")
public class AuthenticationDemoService {
	static private String AUTHENTICATED_AND_AUTORIZED = "thou mayest pass! (I authenticated you as %s, and as such you're also authorized to proceed)";
	static private String AUTHENTICATED_BUT_NOT_AUTORIZED = "thou shalt not pass! (I authenticated you as %s, but you are not authorized to proceed)";


	@GET
	@Path("basic")
	public Response basicAuthentication (@Context final HttpHeaders headers) {
		final Map<String,String> credentials;
		try {
			final List<String> headerValues = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			if (headerValues == null) return Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic").build();
			if (headerValues.size() != 1) throw new IllegalArgumentException();
			credentials = HttpAuthenticationCodec.decode(headerValues.get(0));
		} catch (final IllegalArgumentException exception) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		// Perform authentication
		final String userAlias = credentials.get("username");
		final String userPassword = credentials.get("password");
		if (!userAlias.equals(userPassword)) {
			// simulate failed user lookup
			return Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic").build();
		}

		// Perform authorization checks
		if ("sascha".equals(userAlias)) {
			return Response.status(Status.OK).entity(String.format(AUTHENTICATED_AND_AUTORIZED, userAlias)).build();
		}
		return Response.status(Status.FORBIDDEN).entity(String.format(AUTHENTICATED_BUT_NOT_AUTORIZED, userAlias)).build();
	}


	@GET
	@Path("digest")
	public Response digestAuthentication (@Context final HttpHeaders headers) {
		final Map<String,String> credentials;
		try {
			final List<String> headerValues = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			if (headerValues == null) {
				return Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Digest realm=\"\"").build();
			}
			if (headerValues.size() != 1) throw new IllegalArgumentException();
			credentials = HttpAuthenticationCodec.decode(headerValues.get(0));
		} catch (final IllegalArgumentException exception) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		final String userAlias = credentials.get("username");
		final String requestHash = credentials.get("response"); // note viewpoint confusion, which is part of RFC 2617

		// Digest authentication normally would involve retrieving the user's MD5(alias::password) from the database,
		// recalculating the overall MD5(MD5(alias::password)::MD5(httpMethod:uri)) using the additional information
		// present in the credentials (see RFC 2617), and comparing the latter to the reponseHash. Here we simply
		// check if the given Hash equals a precalculated value for simplicity.
		if ("sascha".equals(userAlias) && "57ace45f0128202c5fd7ee4ce2bf4ae6".equals(requestHash)) {
			return Response.status(Status.OK).entity(String.format(AUTHENTICATED_AND_AUTORIZED, "sascha")).build();
		}
		return Response.status(Status.FORBIDDEN).entity(String.format(AUTHENTICATED_BUT_NOT_AUTORIZED, userAlias)).build();
	}
}