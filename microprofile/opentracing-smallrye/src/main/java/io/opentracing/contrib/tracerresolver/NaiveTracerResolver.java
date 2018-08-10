package io.opentracing.contrib.tracerresolver;

import io.opentracing.Tracer;
import org.wildfly.microprofile.opentracing.smallrye.TracingLogger;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * The TracerResolver from the contrib repository has problems when running in a complex classloader scenario, such
 * as WildFly's. As such, we are implementing a very basic resolver hoping that we can fix it upstream and remove this
 * eventually.
 *
 * Notably, this does *not* support priorities and converters or loading Tracers as services.
 *
 * See: https://github.com/opentracing-contrib/java-tracerresolver/issues/31
 */
public class NaiveTracerResolver {

    public static Tracer resolveTracer() {
        ServiceLoader<TracerResolver> tracerResolverServiceLoader = ServiceLoader.load(TracerResolver.class);
        Iterator<TracerResolver> tracerResolverIterator = tracerResolverServiceLoader.iterator();

        while (tracerResolverIterator.hasNext()) {
            TracerResolver tracerResolver = tracerResolverIterator.next();
            if (tracerResolver.getClass().equals(TracerResolver.class)) {
                continue;
            }

            if (tracerResolverIterator.hasNext()) {
                TracingLogger.ROOT_LOGGER.multipleTracerResolvers();
                return null;
            }

            return tracerResolver.resolve();
        }
        return null;
    }

}
