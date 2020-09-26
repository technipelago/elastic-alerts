package se.technipelago.elastic.alerts;

import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@ConfigurationProperties("elastic-alerts")
public class AlertConfiguration {

    private Elasticsearch elasticsearch = new Elasticsearch();
    private Endpoints endpoints = new Endpoints();
    private Filter filter = new Filter();
    private Status status = new Status();

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ConfigurationProperties("elasticsearch")
    public static class Elasticsearch {

        public static final String DEFAULT_INDEX_NAME = "alerts";

        private String index = DEFAULT_INDEX_NAME;
        private String username;
        private String password;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @ConfigurationProperties("filter")
    public static class Filter {

        public static final String DEFAULT_RANGE = "now-30m";

        private String range = DEFAULT_RANGE;

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }
    }

    @ConfigurationProperties("status")
    public static class Status {

        public static final String DEFAULT_STATUS_OK = "OK";
        public static final String DEFAULT_STATUS_ALERT = "ALERT";

        private String ok = DEFAULT_STATUS_OK;
        private String alert = DEFAULT_STATUS_ALERT;

        public String getOk() {
            return ok;
        }

        public void setOk(String ok) {
            this.ok = ok;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }
    }

    @ConfigurationProperties("endpoints")
    public static class Endpoints {

        private static final String DEFAULT_USERNAME = "user";

        private String username = DEFAULT_USERNAME;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
