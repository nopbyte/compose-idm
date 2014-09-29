sed '/JAR_OR_WAR/q' template_build.gradle>final.gradle.build
cat jar_part_build.gradle>>final.gradle.build
awk 'f;/JAR_OR_WAR/{f=1}' template_build.gradle>>final.gradle.build
gradle -x test build -b final.gradle.build
rm final.gradle.build

