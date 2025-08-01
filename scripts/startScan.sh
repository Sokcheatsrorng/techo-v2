#!/bin/bash

./gradlew clean build

echo "Start scan..."

./gradlew sonarqube -Dorg.gradle.warning.mode=none