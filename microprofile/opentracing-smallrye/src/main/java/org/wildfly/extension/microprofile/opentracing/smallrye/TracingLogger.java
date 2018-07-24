package org.wildfly.extension.microprofile.opentracing.smallrye;

import static org.jboss.logging.Logger.Level.DEBUG;
import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "WFLYTRAC", length = 4)
public interface TracingLogger extends BasicLogger {
  TracingLogger ROOT_LOGGER = Logger.getMessageLogger(TracingLogger.class, TracingLogger.class.getPackage().getName());

  @LogMessage(level = INFO)
  @Message(id = 1, value = "Activating MicroProfile OpenTracing Subsystem")
  void activatingSubsystem();

  @LogMessage(level = DEBUG)
  @Message(id = 2, value = "MicroProfile OpenTracing Subsystem is processing deployment")
  void processingDeployment();

  @LogMessage(level = DEBUG)
  @Message(id = 3, value = "Tracer initialized: %s")
  void initializing(String message);

  @LogMessage(level = DEBUG)
  @Message(id = 4, value = "A Tracer is already registered at the GlobalTracer. Skipping resolution.")
  void alreadyRegistered();

  @LogMessage(level = WARN)
  @Message(id = 5, value = "Could not determine the service name and can't therefore use Jaeger Tracer. Using NoopTracer.")
  void noServiceName();

  @LogMessage(level = DEBUG)
  @Message(id = 6, value = "Registering %s as the OpenTracing Tracer")
  void registeringTracer(String message);

  @LogMessage(level = DEBUG)
  @Message(id = 7, value = "Registering SmallRye CDI Extension")
  void registeringCDIExtension();

  @LogMessage(level = DEBUG)
  @Message(id = 8, value = "The deployment does not have CDI enabled. Skipping MicroProfile OpenTracing integration.")
  void noCdiDeployment();

  @LogMessage(level = DEBUG)
  @Message(id = 9, value = "Registering MicroProfile OpenTracing JAX-RS integration")
  void registeringJaxRs();

  @LogMessage(level = DEBUG)
  @Message(id = 10, value = "Deriving service name based on the deployment unit's name: %s")
  void serviceNameDerivedFromDeploymentUnit(String serviceName);

  @LogMessage(level = DEBUG)
  @Message(id = 11, value = "Registering the TracerInitializer filter")
  void registeringTracerInitializer();

  @LogMessage(level = WARN)
  @Message(id = 12, value = "Multiple tracer resolvers found. Cannot properly determine which one to use.")
  void multipleTracerResolvers();

  @LogMessage(level = DEBUG)
  @Message(id = 13, value = "CDI events are not supported for this deployment. Possible configuration issues? Skipping MicroProfile OpenTracing integration.")
  void noCdiEventSupport();

  @LogMessage(level = DEBUG)
  @Message(id = 14, value = "Extra Tracer bean found: %s. Vetoing it, please use TracerResolver to specify a custom tracer to use.")
  void extraTracerBean(String clazz);

}
