# Define variables
SCRIPTS_DIR := "scripts"

# Recipe to clean and build Spring Boot App
clean-build:
    ./gradlew clean build

# Recipe to run autopush.sh with a commit message
autopush COMMIT_MSG:
    bash {{SCRIPTS_DIR}}/autopush.sh "{{COMMIT_MSG}}"

# Recipe to run startScan.sh
start-scan:
    bash {{SCRIPTS_DIR}}/startScan.sh

# Recipe to run startSonarService.sh
start-sonar:
    bash {{SCRIPTS_DIR}}/startSonarService.sh

# Run all scripts (autopush requires manual commit message)
run-all COMMIT_MSG:
    just autopush "{{COMMIT_MSG}}"
    just start-scan
    just start-sonar
