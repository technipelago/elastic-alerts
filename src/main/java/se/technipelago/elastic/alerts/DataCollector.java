package se.technipelago.elastic.alerts;

import java.util.Map;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@FunctionalInterface
public interface DataCollector {

    void collect(Map<String, Object> fields);
}
