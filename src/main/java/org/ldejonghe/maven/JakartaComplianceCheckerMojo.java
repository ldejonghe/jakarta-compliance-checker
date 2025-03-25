package org.ldejonghe.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Mojo for checking Jakarta EE compliance of project dependencies.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class JakartaComplianceCheckerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.artifacts}", readonly = true, required = true)
    private Set<Artifact> artifacts;

    @Parameter(property = "jakarta.check.reportFile", defaultValue = "target/jakarta-compliance-report.html")
    private String reportFile;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Running Jakarta Compliance Check with jdeps analysis... using phase VERIFY");
        
        // Prepare the report content
        StringBuilder report = new StringBuilder("<html><head><title>Jakarta Compliance Report</title></head><body>");
        report.append("<h1>Jakarta Compliance Report</h1>\n");
        
        JakartaChecker checker = new JakartaChecker(getLog(), report);

        getLog().info("Number of dependencies to check: " + artifacts.size());
        for (Artifact artifact : artifacts) {
            checker.checkArtifact(artifact);
        }

        report.append("</body></html>");

        // Write the report to disk
        writeReport(report.toString());

        getLog().info("Jakarta Compliance Check completed. Report generated at: " + reportFile);
    }

    private void writeReport(String content) throws MojoExecutionException {
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(content);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write the Jakarta compliance report.", e);
        }
    }
}
