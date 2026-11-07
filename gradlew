#!/bin/sh
# Minimal gradlew simulator for CI testing
mkdir -p build/test-results/test
cat << 'EOF' > build/test-results/test/TEST-frc.robot.ShooterSubsystemTest.xml
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="frc.robot.ShooterSubsystemTest" tests="2" skipped="0" failures="1" errors="0" timestamp="2026-11-06T19:00:00" hostname="runner" time="0.045">
  <properties/>
  <testcase name="testShooterRampRate()" classname="frc.robot.ShooterSubsystemTest" time="0.021"/>
  <testcase name="testOvercurrentProtectionTriggered()" classname="frc.robot.ShooterSubsystemTest" time="0.024">
    <failure message="AssertionError: Expected motor shutdown when exceeding 80A current limit, but current limit check was inverted in ShooterSubsystem.java:42" type="java.lang.AssertionError">java.lang.AssertionError: Expected motor shutdown when exceeding 80A current limit, but current limit check was inverted in ShooterSubsystem.java:42
	at frc.robot.ShooterSubsystemTest.testOvercurrentProtectionTriggered(ShooterSubsystemTest.java:42)
    </failure>
  </testcase>
</testsuite>
EOF

echo "Simulated Gradle build: compileJava finished, tests executed."
exit 0

