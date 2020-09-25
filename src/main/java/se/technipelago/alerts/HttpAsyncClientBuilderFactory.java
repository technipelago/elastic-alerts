package se.technipelago.alerts;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import javax.inject.Singleton;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Factory
public class HttpAsyncClientBuilderFactory {

    private final AlertConfiguration configuration;

    public HttpAsyncClientBuilderFactory(AlertConfiguration configuration) {
        this.configuration = configuration;
    }

    @Singleton
    @Replaces(HttpAsyncClientBuilder.class)
    public HttpAsyncClientBuilder builder() {
        final String password = configuration.getElasticsearch().getPassword();
        if (password != null) {
            final String username = configuration.getElasticsearch().getUsername();
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            return HttpAsyncClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider);
        } else {
            return HttpAsyncClientBuilder.create();
        }
    }
}
