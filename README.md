
# Jakarta Compliance Checker Maven Plugin

## Overview
This Maven plugin scans project dependencies and runs `jdeps` to detect `javax.*` usage indicating non-Jakarta compliance.

## Usage
### Add the plugin to your project pom.xml
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

### Run the check
```bash
mvn jakarta-compliance-checker:check
```

#### Simplify the invocation
Add this in `~/.m2/settings.xml`:
```xml
<settings>
  <pluginGroups>
    <pluginGroup>org.ldejonghe.maven</pluginGroup>
  </pluginGroups>
</settings>
```
