package com.variant.demo.petclinic;

import com.variant.client.Connection;
import com.variant.client.ServerConnectException;
import com.variant.client.VariantClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  final private static VariantClient client = VariantClient.build(
    builder -> builder.withSessionIdTrackerClass(SessionIdTrackerServlet5.class)
  );

  private static Optional<Connection> _connection;
  // The connection to Variant server.
  private Optional<Connection> connection() {
    if (_connection.isEmpty()) {
      try {
        logger.info("(Re)connecting to Variant schema [" + url + "]");
        Connection result = client.connectTo(url);
        logger.info("Connected to Variant URI [" + url + "]");
        _connection = Optional.of(result);
      } catch (ServerConnectException sce) {
        logger.error(sce.getMessage());
        _connection = Optional.empty();
      } catch (Throwable t) {
        logger.error("Failed to connect to Variant URI [" + url + "]", t);
        _connection = Optional.empty();
      }
    }
    return _connection;
  }

}
