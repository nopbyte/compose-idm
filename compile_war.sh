#!/bin/sh


sed '/JAR_OR_WAR/q' template_build.gradle>final.gradle.build
cat war_part_build.gradle>>final.gradle.build
awk 'f;/JAR_OR_WAR/{f=1}' template_build.gradle>>final.gradle.build
gradle -x test build -b final.gradle.build
gradle clean -b final.gradle.build
gradle build -x test -b final.gradle.build
gradle wrapper -b final.gradle.build
./gradlew build -x test
rm final.gradle.build

