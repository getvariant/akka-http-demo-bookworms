package com.variant.demo;

import com.variant.client.*;
import com.variant.client.stdlib.SessionIdTrackerServlet5;
import com.variant.share.schema.State;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

public class Variant {

	final private static Logger logger = LoggerFactory.getLogger(Variant.class);

	final private static String url;
	static {
		Properties props = new Properties();
		try {
			props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
			url = props.getProperty("variant.server.url");
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to load application properties: " + e.getMessage(), e);
		}
	}

	final private static VariantClient client = VariantClient
		.build(builder -> builder.withSessionIdTrackerClass(SessionIdTrackerServlet5.class));

	private static Optional<Connection> _connection = Optional.empty();

	// The connection to Variant server.
	private static Optional<Connection> connection() {
		if (_connection.isEmpty()) {
			try {
				logger.info("(Re)connecting to Variant schema [" + url + "]");
				Connection result = client.connectTo(url);
				logger.info("Connected to Variant URI [" + url + "]");
				_connection = Optional.of(result);
			}
			catch (ServerConnectException sce) {
				logger.error(sce.getMessage());
				_connection = Optional.empty();
			}
			catch (Throwable t) {
				logger.error("Failed to connect to Variant URI [" + url + "]", t);
				_connection = Optional.empty();
			}
		}
		return _connection;
	}

	/** Infer the Variant state from the referring page's URL. */
	public static Optional<State> inferState(Session ssn) {

		HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder
			.currentRequestAttributes()).getRequest();

		return Optional.ofNullable(httpServletRequest.getHeader("referer")).flatMap(refererString -> {
			try {
				String path = new URL(refererString).getPath();
				return ssn.getSchema()
					.getStates()
					.stream()
					.filter(state -> path.matches(state.getParameters().get("path")))
					.findAny();
			}
			catch (MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	/** Get Variant session. */
	public static Optional<Session> getVariantSession() {
		try {
			HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes()).getRequest();
			return connection().map(conn -> conn.getOrCreateSession(httpServletRequest,
					Optional.of(VariantController.loggedInOwner.owner.getFullName())));
		}
		catch (ServerConnectException scx) {
			// The server is down. We'll attempt to reconnect.
			return Optional.empty();
		}
	}

	/** Is a given experience live in a given state request */
	public static Boolean isExperienceLive(StateRequest stateRequest, String experimentName, String experienceName) {
		return stateRequest.getLiveExperience(experimentName)
			.map(exp -> exp.getName().equals(experienceName))
			.orElse(false);
	}

}
