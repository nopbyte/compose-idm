#!/bin/sh

gradle clean
gradle build -x test
gradle wrapper
./gradlew build -x test

