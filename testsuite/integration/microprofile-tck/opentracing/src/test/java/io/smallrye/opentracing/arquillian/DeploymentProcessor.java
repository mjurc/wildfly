package io.smallrye.opentracing.arquillian;

import java.io.File;

import io.opentracing.contrib.tracerresolver.TracerResolver;
import io.opentracing.mock.MockTracer;
import io.smallrye.opentracing.ExceptionMapper;
import io.smallrye.opentracing.MockTracerResolver;
import io.smallrye.opentracing.ResteasyClientTracingRegistrarProvider;
import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.eclipse.microprofile.opentracing.ClientTracingRegistrarProvider;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

import javax.ws.rs.ext.Providers;

/**
 * @author Pavol Loffay
 */
public class DeploymentProcessor implements ApplicationArchiveProcessor {

  @Override
  public void process(Archive<?> archive, TestClass testClass) {
    if (archive instanceof WebArchive) {
      JavaArchive extensionsJar = ShrinkWrap.create(JavaArchive.class,"extension.jar");

      extensionsJar.addClass(ExceptionMapper.class);
      extensionsJar.addAsServiceProvider(Providers.class, ExceptionMapper.class);

      extensionsJar.addClass(ResteasyClientTracingRegistrarProvider.class);
      extensionsJar.addClass(ClientTracingRegistrarProvider.class);
      extensionsJar.addClass(ClientTracingRegistrar.class);
      extensionsJar.addAsServiceProvider(ClientTracingRegistrarProvider.class, ResteasyClientTracingRegistrarProvider.class);

      extensionsJar.addClasses(MockTracerResolver.class);
      extensionsJar.addPackage(MockTracer.class.getPackage());
      extensionsJar.addAsServiceProvider(TracerResolver.class, MockTracerResolver.class);

      WebArchive war = WebArchive.class.cast(archive);
      war.addAsWebInfResource(DeploymentProcessor.class.getPackage(), "jboss-deployment-structure.xml",
              "jboss-deployment-structure.xml");
      war.addAsLibraries(extensionsJar);
      war.delete(new BasicPath("/WEB-INF/lib/jackson-jaxrs-json-provider-2.9.0.jar"));
      war.delete(new BasicPath("/WEB-INF/lib/jackson-jaxrs-base-2.9.0.jar"));
      war.delete(new BasicPath("/WEB-INF/lib/jackson-core-2.9.0.jar"));
      war.delete(new BasicPath("/WEB-INF/lib/jackson-databind-2.9.0.jar"));
      war.delete(new BasicPath("/WEB-INF/lib/jackson-module-jaxb-annotations-2.9.0.jar"));
      war.delete(new BasicPath("/WEB-INF/lib/jackson-annotations-2.9.0.jar"));
      final File archiveDir = new File("target/archives");
      archiveDir.mkdirs();
      File moduleFile = new File(archiveDir, "testapp.war");
      war.as(ZipExporter.class).exportTo(moduleFile);
    }
  }
}
