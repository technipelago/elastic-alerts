package se.technipelago.elastic.alerts;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Maybe;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Collections;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Singleton
public class AlertAuthenticationProvider implements AuthenticationProvider {

    private final AlertConfiguration configuration;

    public AlertAuthenticationProvider(AlertConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        final String username = configuration.getEndpoints().getUsername();
        final String password = configuration.getEndpoints().getPassword();
        return Maybe.<AuthenticationResponse>create(emitter -> {
            if (password == null || (authenticationRequest.getIdentity().equals(username) && authenticationRequest.getSecret().equals(password))) {
                emitter.onSuccess(new UserDetails(username, Collections.emptyList()));
            } else {
                emitter.onError(new AuthenticationException(new AuthenticationFailed()));
            }
        }).toFlowable();
    }
}
