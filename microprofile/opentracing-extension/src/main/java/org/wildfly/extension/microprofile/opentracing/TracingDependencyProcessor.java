package org.wildfly.extension.microprofile.opentracing;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

public class TracingDependencyProcessor implements DeploymentUnitProcessor {
  public static final ModuleIdentifier MP_OT_MODULE = ModuleIdentifier.create("org.eclipse.microprofile.opentracing");
  public static final ModuleIdentifier WF_OT_MODULE = ModuleIdentifier.create("org.wildfly.microprofile.opentracing-smallrye");

  public static final Phase PHASE = Phase.DEPENDENCIES;
  public static final int PRIORITY = 0x4000;

  @Override
  public void deploy(DeploymentPhaseContext phaseContext) {
    DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
    addDependencies(deploymentUnit);
  }

  private void addDependencies(DeploymentUnit deploymentUnit) {
    ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
    ModuleLoader moduleLoader = Module.getBootModuleLoader();

    moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, MP_OT_MODULE, false, true, true, false));
    moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, WF_OT_MODULE, false, true, true, false));
  }

  @Override
  public void undeploy(DeploymentUnit context) {
  }
}
