package org.ldejonghe.maven;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate-report", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class JakartaComplianceReportMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.artifacts}", readonly = true, required = true)
    private Set<Artifact> artifacts;

    @Parameter(property = "jakarta.check.reportFile", defaultValue = "target/jakarta-compliance-report.html")
    private String reportFile;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating Jakarta Compliance Report...");
        StringBuilder report = new StringBuilder("<html><head><title>Jakarta Compliance Report</title></head><body>");
        report.append("<h1>Jakarta Compliance Report</h1>\n");

        JakartaChecker checker = new JakartaChecker(getLog(), report);

        for (Artifact artifact : artifacts) {
            checker.checkArtifact(artifact);
        }

        report.append("</body></html>");
        writeReport(report.toString());
        getLog().info("Jakarta Compliance Report generated at: " + reportFile);
    }

    private void writeReport(String content) throws MojoExecutionException {
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(content);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write the Jakarta compliance report.", e);
        }
    }
}
