package org.wildfly.extension.microprofile.opentracing;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import static org.jboss.logging.Logger.Level.DEBUG;
import static org.jboss.logging.Logger.Level.INFO;

@MessageLogger(projectCode = "WFLYTRACEXT", length = 4)
public interface TracingExtensionLogger extends BasicLogger {
  TracingExtensionLogger ROOT_LOGGER = Logger.getMessageLogger(TracingExtensionLogger.class, TracingExtensionLogger.class.getPackage().getName());

  @LogMessage(level = INFO)
  @Message(id = 1, value = "Activating MicroProfile OpenTracing Subsystem")
  void activatingSubsystem();

  @LogMessage(level = DEBUG)
  @Message(id = 2, value = "MicroProfile OpenTracing Subsystem is processing deployment")
  void processingDeployment();

  @LogMessage(level = DEBUG)
  @Message(id = 3, value = "Registering SmallRye CDI Extension")
  void registeringCDIExtension();

  @LogMessage(level = DEBUG)
  @Message(id = 4, value = "The deployment does not have CDI enabled. Skipping MicroProfile OpenTracing integration.")
  void noCdiDeployment();

  @LogMessage(level = DEBUG)
  @Message(id = 5, value = "Registering MicroProfile OpenTracing JAX-RS integration")
  void registeringJaxRs();

  @LogMessage(level = DEBUG)
  @Message(id = 6, value = "Deriving service name based on the deployment unit's name: %s")
  void serviceNameDerivedFromDeploymentUnit(String serviceName);

  @LogMessage(level = DEBUG)
  @Message(id = 7, value = "Registering the TracerInitializer filter")
  void registeringTracerInitializer();
}
