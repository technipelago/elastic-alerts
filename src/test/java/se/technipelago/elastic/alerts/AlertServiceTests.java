package se.technipelago.elastic.alerts;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@MicronautTest
public class AlertServiceTests {

    private static final boolean ALERT_EXISTS = false;

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    AlertConfiguration configuration;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testService() {
        final AlertService service = application.getApplicationContext().getBean(AlertService.class);
        List<Map<String, Object>> alerts = service.getActiveAlerts();
        if (ALERT_EXISTS) {
            assertFalse(alerts.isEmpty());
        } else {
            assertTrue(alerts.isEmpty());
        }
    }

    @Test
    void testController() {
        final String username = configuration.getEndpoints().getUsername();
        final String password = configuration.getEndpoints().getPassword();
        final StatusCheck status = client.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(username, password), StatusCheck.class);
        if (ALERT_EXISTS) {
            assertEquals("DOWN", status.getStatus());
            assertFalse(status.getAlerts().isEmpty());
        } else {
            assertEquals("UP", status.getStatus());
            assertNull(status.getAlerts());
        }
    }
}
