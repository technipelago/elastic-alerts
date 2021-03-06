package se.technipelago.elastic.alerts;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class AlertController {

    private final AlertConfiguration configuration;
    private final AlertService alertService;

    public AlertController(AlertConfiguration configuration, AlertService alertService) {
        this.configuration = configuration;
        this.alertService = alertService;
    }

    @Get
    @ExecuteOn(TaskExecutors.IO)
    public HttpResponse<StatusCheck> check(@Nullable Authentication authentication,
                                           @Nullable @QueryValue List<String> group,
                                           @Nullable @QueryValue List<String> tag
    ) {
        if (isAuthenticated(authentication)) {
            Collection<Map<String, Object>> alerts = alertService.getActiveAlerts(group, tag);
            return HttpResponse.ok(new StatusCheck(getStatus(alerts), alerts));
        } else {
            return HttpResponse.unauthorized();
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        if (configuration.getEndpoints().getPassword() == null) {
            return true;
        }
        return authentication != null;
    }

    private String getStatus(Collection<Map<String, Object>> alerts) {
        return alerts.isEmpty() ? configuration.getStatus().getOk() : configuration.getStatus().getAlert();
    }
}
