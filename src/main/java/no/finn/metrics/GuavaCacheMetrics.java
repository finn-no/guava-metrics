package no.finn.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.cache.Cache;

/**
 * Created by Henning Spjelkavik on 23.10.2014.
 *
 * The idea and most of the code is copied from the blog post, with permission
 * http://antrix.net/posts/2014/codahale-metrics-guava-cache/
 *
 */

public class GuavaCacheMetrics extends HashMap< String, Metric> implements MetricSet {
    /** Metric names. */
    private static String hitRateMetric = "hitRate";
    private static String hitCountMetric = "hitCount";
    private static String missCountMetric = "missCount";
    private static String loadExceptionCountMetric = "loadExceptionCount";
    private static String evictionCountMetric = "evictionCount";

    /**
     * Rename metrics. Has global effect.
     */
    public static void setMetricNames(String hitRateMetric,
                                      String hitCountMetric,
                                      String missCountMetric,
                                      String loadExceptionCountMetric,
                                      String evictionCountMetric
    ) {
        GuavaCacheMetrics.hitRateMetric = hitRateMetric;
        GuavaCacheMetrics.hitCountMetric = hitCountMetric;
        GuavaCacheMetrics.missCountMetric = missCountMetric;
        GuavaCacheMetrics.loadExceptionCountMetric = loadExceptionCountMetric;
        GuavaCacheMetrics.evictionCountMetric = evictionCountMetric;
    }

    /**
     * Wraps the provided Guava cache's statistics into Gauges suitable for reporting via Codahale Metrics
     * The returned MetricSet is suitable for registration with a MetricRegistry like so:
     * <code>registry.registerAll( GuavaCacheMetrics.metricsFor( "MyCache", cache ) );</code>
     *
     * @param clzz Classname to prefix the cacheName
     * @param cacheName This will be prefixed to all the reported metrics
     * @param cache The cache from which to report the statistics
     * @return MetricSet suitable for registration with a MetricRegistry
     */
    public static MetricSet metricsFor( Class clzz, String cacheName, final Cache cache ) {

        GuavaCacheMetrics metrics = new GuavaCacheMetrics();

        metrics.put( name( clzz, cacheName, hitRateMetric), new Gauge< Double >() {
            @Override
            public Double getValue() {
                return cache.stats().hitRate();
            }
        } );

        metrics.put( name( clzz, cacheName, hitCountMetric), new Gauge< Long >() {
            @Override
            public Long getValue() {
                return cache.stats().hitCount();
            }
        } );

        metrics.put( name( clzz, cacheName, missCountMetric), new Gauge< Long >() {
            @Override
            public Long getValue() {
                return cache.stats().missCount();
            }
        } );

        metrics.put( name( clzz, cacheName, loadExceptionCountMetric), new Gauge< Long >() {
            @Override
            public Long getValue() {
                return cache.stats().loadExceptionCount();
            }
        } );

        metrics.put( name( clzz, cacheName, evictionCountMetric ), new Gauge< Long >() {
            @Override
            public Long getValue() {
                return cache.stats().evictionCount();
            }
        } );

        return metrics;
    }

    private GuavaCacheMetrics() {
    }

    @Override
    public Map< String, Metric > getMetrics() {
        return this;
    }

}

