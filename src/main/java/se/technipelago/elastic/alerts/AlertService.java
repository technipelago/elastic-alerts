package se.technipelago.elastic.alerts;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Singleton
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private static final String TIMESTAMP_FIELD = "@timestamp";

    private final AlertConfiguration configuration;
    private final RestHighLevelClient elasticsearch;

    public AlertService(AlertConfiguration configuration, RestHighLevelClient elasticsearch) {
        this.configuration = configuration;
        this.elasticsearch = elasticsearch;
    }


    public List<Map<String, Object>> getActiveAlerts() {
        SearchRequest request = new SearchRequest(configuration.getElasticsearch().getIndex());
        String range = configuration.getFilter().getRange();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery(TIMESTAMP_FIELD).gte(range)));
        sourceBuilder.sort(TIMESTAMP_FIELD, SortOrder.DESC);
        sourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));

        log.trace("{}", sourceBuilder);

        request.source(sourceBuilder);

        request.scroll(TimeValue.timeValueSeconds(10));

        final List<Map<String, Object>> alerts = new ArrayList<>();
        final List<String> scrollIds = new ArrayList<>();
        final DataCollector collector = (fields) -> alerts.add(fields);

        try {
            SearchResponse response = elasticsearch.search(request, RequestOptions.DEFAULT);
            String scrollId = response.getScrollId();
            while (collectHits(response.getHits(), collector)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollIds.add(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(10));
                response = elasticsearch.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error searching index", e);
        } finally {
            if (!scrollIds.isEmpty()) {
                clearScrollContext(scrollIds);
            }
        }

        return alerts;
    }

    private boolean collectHits(SearchHits hits, DataCollector collector) {
        if (hits.getHits().length > 0) {
            for (SearchHit hit : hits) {
                Map<String, Object> fields = hit.getSourceAsMap();
                collector.collect(fields);
            }
            return true;
        }
        return false;
    }

    private void clearScrollContext(List<String> scrollIds) {
        ClearScrollRequest request = new ClearScrollRequest();
        request.scrollIds(scrollIds);
        try {
            elasticsearch.clearScroll(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Failed to clear scroll context", e);
        }
    }

}
