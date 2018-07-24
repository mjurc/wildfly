package org.wildfly.extension.microprofile.opentracing.smallrye.deployment;

import io.opentracing.Tracer;
import io.smallrye.opentracing.SmallRyeTracingCDIInterceptor;
import org.wildfly.extension.microprofile.opentracing.smallrye.TracingLogger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

public class TracingCDIExtension implements Extension {

  public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager manager) {
    bbd.addAnnotatedType(manager.createAnnotatedType(TracerProducer.class));
    bbd.addAnnotatedType(manager.createAnnotatedType(SmallRyeTracingCDIInterceptor.class));
  }

  public void skipTracerBeans(@Observes ProcessAnnotatedType<? extends Tracer> processAnnotatedType) {
    TracingLogger.ROOT_LOGGER.extraTracerBean(processAnnotatedType.getAnnotatedType().getJavaClass().getName());
    processAnnotatedType.veto();
  }
}
