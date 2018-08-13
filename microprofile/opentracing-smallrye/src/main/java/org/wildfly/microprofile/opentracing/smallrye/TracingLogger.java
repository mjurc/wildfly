package org.wildfly.microprofile.opentracing.smallrye;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import static org.jboss.logging.Logger.Level.DEBUG;
import static org.jboss.logging.Logger.Level.WARN;

@MessageLogger(projectCode = "WFLYTRAC", length = 4)
public interface TracingLogger extends BasicLogger {
  TracingLogger ROOT_LOGGER = Logger.getMessageLogger(TracingLogger.class, TracingLogger.class.getPackage().getName());

  @LogMessage(level = DEBUG)
  @Message(id = 1, value = "Tracer initialized: %s")
  void initializing(String message);

  @LogMessage(level = DEBUG)
  @Message(id = 2, value = "A Tracer is already registered at the GlobalTracer. Skipping resolution.")
  void alreadyRegistered();

  @LogMessage(level = WARN)
  @Message(id = 3, value = "Could not determine the service name and can't therefore use Jaeger Tracer. Using NoopTracer.")
  void noServiceName();

  @LogMessage(level = DEBUG)
  @Message(id = 4, value = "Registering %s as the OpenTracing Tracer")
  void registeringTracer(String message);

  @LogMessage(level = DEBUG)
  @Message(id = 5, value = "CDI events are not supported for this deployment. Possible configuration issues? Skipping MicroProfile OpenTracing integration.")
  void noCdiEventSupport();

  @LogMessage(level = DEBUG)
  @Message(id = 6, value = "Extra Tracer bean found: %s. Vetoing it, please use TracerResolver to specify a custom tracer to use.")
  void extraTracerBean(String clazz);

  @LogMessage(level = WARN)
  @Message(id = 7, value = "Multiple tracer resolvers found. Cannot properly determine which one to use.")
  void multipleTracerResolvers();
}
