package se.technipelago.alerts;

import io.micronaut.core.annotation.Introspected;

import java.util.Collection;
import java.util.Map;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Introspected
public class StatusCheck {

    private String status;
    private Collection<Map<String, Object>> alerts;

    public StatusCheck() {
    }

    public StatusCheck(String status, Collection<Map<String, Object>> alerts) {
        this.status = status;
        this.alerts = alerts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Collection<Map<String, Object>> getAlerts() {
        return alerts;
    }

    public void setAlerts(Collection<Map<String, Object>> alerts) {
        this.alerts = alerts;
    }
}
