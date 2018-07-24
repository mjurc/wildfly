package org.wildfly.extension.microprofile.opentracing.smallrye.deployment;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;
import io.opentracing.contrib.tracerresolver.NaiveTracerResolver;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import org.wildfly.extension.microprofile.opentracing.smallrye.TracingLogger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.EnumSet;

@WebListener
public class TracerInitializer implements ServletContextListener {
  public static final String SMALLRYE_OPENTRACING_SERVICE_NAME = "smallrye.opentracing.serviceName";

  @Inject
  Event<Tracer> tracerInitialized;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    if (null == tracerInitialized) {
      // Weld integration problems? This happens with the test org.jboss.as.test.integration.ws.injection.ejb.basic.InjectionTestCase
      // TODO: is this a valid scenario? Sounds like a bug to me...
      TracingLogger.ROOT_LOGGER.noCdiEventSupport();
      return;
    }

    if (GlobalTracer.isRegistered()) {
      TracingLogger.ROOT_LOGGER.alreadyRegistered();
      return;
    }

    // an application has the option to provide a tracer resolver
    // if we can find a tracer with that, that's what we use
    // TODO: use TracerResolver.resolveTracer() once the following PR is merged and released:
    // https://github.com/opentracing-contrib/java-tracerresolver/issues/31
    Tracer tracer = NaiveTracerResolver.resolveTracer();
    if (null == tracer) {
      String serviceName = sce.getServletContext().getInitParameter(SMALLRYE_OPENTRACING_SERVICE_NAME);
      if (null == serviceName || serviceName.isEmpty()) {
        // this should really not happen, as this is set by the deployment processor
        TracingLogger.ROOT_LOGGER.noServiceName();
        tracer = NoopTracerFactory.create();
      } else {
        // TODO: once there's a version of the client incorporating the PR #510, we can simplify this with
        // Configuration.fromEnv(serviceName).getTracer();
        // See: https://github.com/jaegertracing/jaeger-client-java/pull/510
        // Note that this is missing a call to `withTracerTags`, but that logic is within a private method, so, we
        // just skip it for this first version
        tracer = new Configuration(serviceName)
                .withReporter(Configuration.ReporterConfiguration.fromEnv())
                .withSampler(Configuration.SamplerConfiguration.fromEnv())
                .withCodec(Configuration.CodecConfiguration.fromEnv())
                .getTracer();
      }
    }

    TracingLogger.ROOT_LOGGER.registeringTracer(tracer.getClass().getName());
    tracerInitialized.fire(tracer);

    // TODO: determine whether it's appropriate to register the global tracer here, as it seems it's shared across deployments
    // GlobalTracer.register(tracer);

    TracingLogger.ROOT_LOGGER.initializing(tracer.toString());

    FilterRegistration.Dynamic filterRegistration = sce.getServletContext()
            .addFilter(SpanFinishingFilter.class.getName(), new SpanFinishingFilter(tracer));
    filterRegistration.setAsyncSupported(true);
    filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "*");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
