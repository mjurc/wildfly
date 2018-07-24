package org.wildfly.extension.microprofile.opentracing.smallrye;

import io.smallrye.opentracing.SmallRyeTracingDynamicFeature;
import org.jboss.as.ee.weld.WeldDeploymentMarker;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.as.weld.deployment.WeldPortableExtensions;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.wildfly.extension.microprofile.opentracing.smallrye.deployment.TracerInitializer;
import org.wildfly.extension.microprofile.opentracing.smallrye.deployment.TracingCDIExtension;

import java.util.ArrayList;
import java.util.List;

public class TracingDeploymentProcessor implements DeploymentUnitProcessor {
  public static final Phase PHASE = Phase.POST_MODULE;
  public static final int PRIORITY = 0x4000;

  @Override
  public void deploy(DeploymentPhaseContext deploymentPhaseContext) {
    TracingLogger.ROOT_LOGGER.processingDeployment();
    DeploymentUnit deploymentUnit = deploymentPhaseContext.getDeploymentUnit();

    if (!WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
      // SmallRye JAX-RS requires CDI. Without CDI, there's no integration needed
      TracingLogger.ROOT_LOGGER.noCdiDeployment();
      return;
    }

    addListeners(deploymentUnit);
    addJaxRsIntegration(deploymentUnit);
    addCDIExtension(deploymentUnit);
  }

  @Override
  public void undeploy(DeploymentUnit deploymentUnit) {
  }

  private void addListeners(DeploymentUnit deploymentUnit) {
    JBossWebMetaData jbossWebMetaData = getJBossWebMetaData(deploymentUnit);
    if (null == jbossWebMetaData) {
      // nothing to do here
      return;
    }

    TracingLogger.ROOT_LOGGER.registeringTracerInitializer();

    String serviceName = getServiceName(deploymentUnit);
    ParamValueMetaData serviceNameContextParameter = new ParamValueMetaData();
    serviceNameContextParameter.setParamName(TracerInitializer.SMALLRYE_OPENTRACING_SERVICE_NAME);
    serviceNameContextParameter.setParamValue(serviceName);
    addContextParameter(jbossWebMetaData, serviceNameContextParameter);

    ListenerMetaData listenerMetaData = new ListenerMetaData();
    listenerMetaData.setListenerClass(TracerInitializer.class.getName());

    List<ListenerMetaData> listeners = jbossWebMetaData.getListeners();
    if (null == listeners) {
      listeners = new ArrayList<>();
    }
    listeners.add(listenerMetaData);
    jbossWebMetaData.setListeners(listeners);
  }

  private void addJaxRsIntegration(DeploymentUnit deploymentUnit) {
    JBossWebMetaData jbossWebMetaData = getJBossWebMetaData(deploymentUnit);
    if (null == jbossWebMetaData) {
      // nothing to do here
      return;
    }

    TracingLogger.ROOT_LOGGER.registeringJaxRs();

    ParamValueMetaData restEasyProvider = new ParamValueMetaData();
    restEasyProvider.setParamName("resteasy.providers");
    restEasyProvider.setParamValue(SmallRyeTracingDynamicFeature.class.getName());
    addContextParameter(jbossWebMetaData, restEasyProvider);
  }

  private void addCDIExtension(DeploymentUnit deploymentUnit) {
    TracingLogger.ROOT_LOGGER.registeringCDIExtension();

    WeldPortableExtensions extensions = WeldPortableExtensions.getPortableExtensions(deploymentUnit);
    extensions.registerExtensionInstance(new TracingCDIExtension(), deploymentUnit);
  }

  private JBossWebMetaData getJBossWebMetaData(DeploymentUnit deploymentUnit) {
    WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
    if (null == warMetaData) {
      // not a web deployment, nothing to do here...
      return null;
    }

    return warMetaData.getMergedJBossWebMetaData();
  }

  private void addContextParameter(JBossWebMetaData jbossWebMetaData, ParamValueMetaData restEasyProvider) {
    List<ParamValueMetaData> contextParams = jbossWebMetaData.getContextParams();
    if (null == contextParams) {
      contextParams = new ArrayList<>();
    }
    contextParams.add(restEasyProvider);
    jbossWebMetaData.setContextParams(contextParams);
  }

  private String getServiceName(DeploymentUnit deploymentUnit) {
    String serviceName = System.getProperty("JAEGER_SERVICE_NAME");
    if (null == serviceName || serviceName.isEmpty()) {
      serviceName = System.getenv("JAEGER_SERVICE_NAME");
    }

    if (null == serviceName || serviceName.isEmpty()) {
      TracingLogger.ROOT_LOGGER.serviceNameDerivedFromDeploymentUnit(serviceName);
      serviceName = deploymentUnit.getServiceName().getSimpleName();
    }

    return serviceName;
  }
}
