package org.ldejonghe.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.Locale;
import java.util.Set;

public class JakartaComplianceReport implements MavenReport {

    private final MavenProject project;
    private final Log log;
    private File reportOutputDirectory;

    public JakartaComplianceReport(MavenProject project, Log log) {
        this.project = project;
        this.log = log;
    }

    @Override
    public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale) throws MavenReportException {
        sink.head();
        sink.title();
        sink.text(getName(locale));
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();
        sink.sectionTitle1();
        sink.text("Jakarta Compliance Dependency Analysis");
        sink.sectionTitle1_();

        Set<Artifact> artifacts = project.getArtifacts();
        log.info("Generating Jakarta Compliance Report for " + artifacts.size() + " dependencies.");

        JakartaChecker checker = new JakartaChecker(log, new StringBuilder());

        for (Artifact artifact : artifacts) {
            sink.paragraph();
            sink.text("Checking: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
            sink.paragraph_();

            boolean issueFound = false;

            // Check javax group
            if (checker.isMigrationRequiredGroup(artifact.getGroupId())) {
                sink.paragraph();
                sink.text("⚠️ Potential non-Jakarta compliant groupId: " + artifact.getGroupId());
                sink.paragraph_();
                issueFound = true;
            }

            File artifactFile = artifact.getFile();
            if (artifactFile != null && artifactFile.exists()) {
                boolean jdepsIssue = checker.analyzeWithJdepsForSink(artifactFile, sink);
                issueFound = issueFound || jdepsIssue;
            } else {
                sink.paragraph();
                sink.text("Artifact file not found or unavailable for jdeps analysis.");
                sink.paragraph_();
            }

            if (!issueFound) {
                sink.paragraph();
                sink.text("No Jakarta migration issues detected.");
                sink.paragraph_();
            }
        }

        sink.section1_();
        sink.body_();
    }

    @Override
    public String getOutputName() {
        return "jakarta-compliance-report";
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    @Override
    public String getName(Locale locale) {
        return "Jakarta Compliance Report";
    }

    @Override
    public String getDescription(Locale locale) {
        return "This report analyzes project dependencies for non-Jakarta compliant packages.";
    }

    @Override
    public void setReportOutputDirectory(File outputDirectory) {
        this.reportOutputDirectory = outputDirectory;
    }

    @Override
    public File getReportOutputDirectory() {
        return reportOutputDirectory;
    }

    @Override
    public boolean isExternalReport() {
        return false;
    }

    @Override
    public boolean canGenerateReport() {
        return project != null && !project.getArtifacts().isEmpty();
    }
}
