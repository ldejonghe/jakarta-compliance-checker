
# Jakarta Compliance Checker Maven Plugin

## Overview
The **Jakarta Compliance Checker** is a Maven plugin that scans your project dependencies to detect potential non-Jakarta EE compliant libraries. It verifies both `javax.*` package usage and analyzes transitive dependencies using `jdeps`.

It helps projects migrating to Jakarta EE 10 by identifying libraries that might need attention.

---

## Features
✅ Scans all runtime dependencies  
✅ Detects problematic `javax.*` packages known for requiring migration  
✅ Integrates `jdeps` analysis for deep inspection  
✅ Generates a detailed **HTML report** listing the findings  

---

## Installation (if built locally)
Install the plugin in your local Maven repository:

```bash
mvn clean install
```

---

## Plugin Configuration in Your Project (`pom.xml`)
Add the plugin to your `build.plugins` section:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.ldejonghe.maven</groupId>
      <artifactId>jakarta-compliance-checker</artifactId>
      <version>1.0-SNAPSHOT</version>
    </plugin>
  </plugins>
</build>
```

---

## Usage: Run the Check
### Default full command:

```bash
mvn org.ldejonghe.maven:jakarta-compliance-checker:1.0-SNAPSHOT:check
```

### Optional: Simplify Command Execution
Add the plugin group to your `~/.m2/settings.xml`:

```xml
<settings>
  <pluginGroups>
    <pluginGroup>org.ldejonghe.maven</pluginGroup>
  </pluginGroups>
</settings>
```

After this, you can run:

```bash
mvn jakarta-compliance-checker:check
```

---

## Customizing Report Location
By default, the report is generated at:

```
target/jakarta-compliance-report.html
```

You can override the report output location:

```bash
mvn jakarta-compliance-checker:check -Djakarta.check.reportFile=target/custom-report.html
```

---

## Generated Report
- The HTML report includes:
  - All scanned dependencies
  - Highlighted **warnings** for any detected usage of problematic `javax.*` packages
  - `jdeps` output showing transitive issues

Example snippet of the report:

```html
<h1>Jakarta Compliance Report</h1>
<p>Dependency: org.example:example-library</p>
<p style='color:red;'>Potential non-Jakarta compliant dependency: javax.xml.bind:jaxb-api</p>
```

---

## Requirements
- Java 11+ installed (required for `jdeps`)
- Maven 3.6+

---

## Notes
- The plugin uses `jdeps` internally; make sure `jdeps` is accessible in your system's PATH.
- Only libraries with known problematic packages (`javax.servlet`, `javax.persistence`, etc.) are flagged.

---

## License
MIT License
