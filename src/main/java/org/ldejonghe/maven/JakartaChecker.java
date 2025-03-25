package org.ldejonghe.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.doxia.sink.Sink;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Jakarta Compliance Checker utility class.
 * Performs javax.* migration check and jdeps analysis.
 */
public class JakartaChecker {

    private final Log log;
    
  
    private final StringBuilder report;

    public JakartaChecker(Log log, StringBuilder report) {
        this.log = log;
        this.report = report;
    }
    

    // List of problematic javax packages that require migration to jakarta.*
    private static final List<String> MIGRATION_PACKAGES = Arrays.asList(
            "javax.servlet",
            "javax.ws.rs",
            "javax.ejb",
            "javax.persistence",
            "javax.faces",
            "javax.jms",
            "javax.enterprise",
            "javax.transaction",
            "javax.xml.ws",
            "javax.xml.bind",
            "javax.annotation",
            "javax.mail"
    );

  
    /**
     * Analyze a Maven artifact for Jakarta compliance and collect issues.
     * @param artifact the Maven artifact
     * @return List of issue descriptions
     */
    public void checkArtifact(Artifact artifact) {
        String artifactIdentifier = generateIdentifierString(artifact);
    	
    	
        String dependencyLine = "Dependency: " + artifactIdentifier ;
        log.info(dependencyLine);
        appendToReport("<p>" + dependencyLine + "</p>");

        // Check for javax.* groupId that requires migration
        if (isMigrationRequiredGroup(artifact.getGroupId())) {
            String warn = "Potential non-Jakarta compliant dependency: " + artifactIdentifier;
            log.warn(warn);
            appendToReport("<p style='color:red;'>" + warn + "</p>");
        }

        // jdeps analysis
        if (artifact.getFile() != null && artifact.getFile().exists()) {
            analyzeWithJdeps(artifact);
        }
    }

    private String generateIdentifierString(Artifact artifact) {
        StringBuilder sb = new StringBuilder();
        sb.append("GroupId: ").append(artifact.getGroupId())
          .append(", ArtifactId: ").append(artifact.getArtifactId())
          .append(", Version: ").append(artifact.getVersion());
        
        if (artifact.getScope() != null && !artifact.getScope().isEmpty()) {
            sb.append(", Scope: ").append(artifact.getScope());
        }
        return sb.toString();
    }

	/**
     * Runs jdeps on the artifact's jar and logs any problematic javax usage.
     * @param artifact the Maven artifact
     * @return List of jdeps warnings
     */
    
  
    
    private List<String> analyzeWithJdeps(Artifact artifact) {
        List<String> jdepsIssues = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "jdeps", "-verbose:class", artifact.getFile().getAbsolutePath()
            );
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (String problematic : MIGRATION_PACKAGES) {
                        if (line.contains(problematic)) {
                            String warn = "[jdeps] Usage of problematic package " + problematic +
                                    " found in " + artifact.getFile().getName() + ": " + line.trim();
                            log.warn(warn);
                            appendToReport("<p style='color:red;'>" + warn + "</p>");  // <-- ADD THIS LINE
                            jdepsIssues.add(warn);
                        }
                    }
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            String error = "Error running jdeps on artifact: " + artifact;
            log.error(error, e);
            appendToReport("<p style='color:red;'>" + error + "</p>");  // Optional: report errors
            jdepsIssues.add(error);
        }
        return jdepsIssues;
    }
    
    
    
    
    
    public boolean analyzeWithJdepsForSink(File jarFile, Sink sink) {
        boolean issuesFound = false;
        try {
            ProcessBuilder pb = new ProcessBuilder("jdeps", "-verbose:class", jarFile.getAbsolutePath());
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (String problematic : MIGRATION_PACKAGES) {
                        if (line.contains(problematic)) {
                            String warn = "[jdeps] Usage of problematic package " + problematic +
                                    " found in " + jarFile.getName() + ": " + line.trim();
                            log.warn(warn);
                            sink.paragraph();
                            sink.text(warn);
                            sink.paragraph_();
                            issuesFound = true;
                        }
                    }
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            String error = "Error running jdeps on file: " + jarFile;
            log.error(error, e);
            sink.paragraph();
            sink.text(error);
            sink.paragraph_();
            issuesFound = true;
        }
        return issuesFound;
    }
    
    
    
    
    
    
    
    private void appendToReport(String html) {
        if (report != null) {
            report.append(html).append("\n");
        }
    }

    /**
     * Determines if the artifact's groupId indicates a need for migration.
     * @param groupId the Maven groupId
     * @return true if migration is needed
     */
    public boolean isMigrationRequiredGroup(String groupId) {
        return groupId.startsWith("javax.") && MIGRATION_PACKAGES.stream().anyMatch(groupId::contains);
    }
}
