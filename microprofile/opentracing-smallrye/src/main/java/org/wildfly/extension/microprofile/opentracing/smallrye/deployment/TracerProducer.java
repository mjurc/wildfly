package org.wildfly.extension.microprofile.opentracing.smallrye.deployment;

import io.opentracing.Tracer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TracerProducer {
  private Tracer tracer;

  @Produces
  public Tracer getTracer() {
    return this.tracer;
  }

  public void onTracerInitialized(@Observes Tracer tracer) {
    this.tracer = tracer;
  }
}
