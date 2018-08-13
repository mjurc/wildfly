/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
