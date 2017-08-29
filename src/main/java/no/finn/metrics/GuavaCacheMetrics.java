package no.finn.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.cache.Cache;

import java.util.HashMap;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by Henning Spjelkavik on 23.10.2014.
 *
 * The idea and most of the code is copied from the blog post, with permission
 * http://antrix.net/posts/2014/codahale-metrics-guava-cache/
 *
 */

public class GuavaCacheMetrics extends HashMap< String, Metric> implements MetricSet {

    public class Builder {
        private String hitRateMetric = "hitRate";
        private String hitCountMetric = "hitCount";
        private String missCountMetric = "missCount";
        private String loadExceptionCountMetric = "loadExceptionCount";
        private String evictionCountMetric = "evictionCount";
        private Class clzz = GuavaCacheMetrics.class;
        private String cacheName = "cache";
        final Cache cache;

        Builder(final Cache cache) {
            this.cache = cache;
        }

        public Builder withHitRateName(String name) {
            hitRateMetric = name;
            return this;
        }

        public Builder withHitCountName(String name) {
            hitCountMetric = name;
            return this;
        }

        public Builder withMissCountName(String name) {
            missCountMetric = name;
            return this;
        }

        public Builder withLoadExceptionCountName(String name) {
            loadExceptionCountMetric = name;
            return this;
        }

        public Builder withEvictionCountName(String name) {
            evictionCountMetric = name;
            return this;
        }

        public Builder forClass(Class clzz) {
            this.clzz = clzz;
            return this;
        }

        public Builder withCacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public GuavaCacheMetrics build() {
            GuavaCacheMetrics metrics = GuavaCacheMetrics.this;

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
    }

    /**
     * Rename metrics. Has global effect.
     */
    private GuavaCacheMetrics() {}

    /**
     * Create new Builder.
     */
    public static Builder newBuilder(final Cache cache) {
        GuavaCacheMetrics metrics = new GuavaCacheMetrics();
        return metrics.new Builder(cache);
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
        return newBuilder(cache).withCacheName(cacheName).forClass(clzz).build();
    }

    @Override
    public Map< String, Metric > getMetrics() {
        return this;
    }

}

