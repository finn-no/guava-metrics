package no.finn.metrics;

import com.codahale.metrics.MetricRegistry
import com.google.common.cache.CacheBuilder
import spock.lang.Specification

class GuavaCacheMetricsTest extends Specification {


    def "ensure guava cache metrics are reported to the registry"() {

        given: "a guava cache registered with a Metrics registry"

        def cache = CacheBuilder.newBuilder().recordStats().build()
        def registry = new MetricRegistry()
        registry.registerAll(GuavaCacheMetrics.metricsFor(GuavaCacheMetricsTest.class, "MyCache", cache))

        when: "various read/write operations are performed on the cache"

        cache.put("k1", "v1")
        cache.put("k2", "v2")

        cache.getIfPresent("k1")
        cache.getIfPresent("k2")
        cache.getIfPresent("k3")

        cache.get("k4", { "v4" })

        try {
            cache.get "k5", {
                throw new Exception()
            }
        } catch (Exception expected) {
        }

        then: "the metrics registry records them correctly"

        def gauges = registry.gauges

        2   == gauges["no.finn.metrics.GuavaCacheMetricsTest.MyCache.hitCount"].value
        3   == gauges["no.finn.metrics.GuavaCacheMetricsTest.MyCache.missCount"].value
        0.4 == gauges["no.finn.metrics.GuavaCacheMetricsTest.MyCache.hitRate"].value
        1   == gauges["no.finn.metrics.GuavaCacheMetricsTest.MyCache.loadExceptionCount"].value
        0   == gauges["no.finn.metrics.GuavaCacheMetricsTest.MyCache.evictionCount"].value
    }
}
