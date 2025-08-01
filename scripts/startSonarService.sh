#!/bin/bash

# Define SonarQube directory
SONARQUBE_DIR="../../../../../Desktop/istad/sonarqube-25.2.0.102705/bin/linux-x86-64"

# Navigate to SonarQube bin directory
cd "$SONARQUBE_DIR" || {
  echo "Failed to navigate to $SONARQUBE_DIR"
  exit 1
}

# Make sure sonar.sh is executable
chmod +x sonar.sh

# Start SonarQube
./sonar.sh start

# Check SonarQube status
./sonar.sh status
