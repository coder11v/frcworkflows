package frc.robot.subsystems;

/**
 * Robot Shooter and Climber controller with simulated robotics logic bugs
 * for testing automated AI code review.
 */
public class ShooterSubsystem {
    private double motorOutput = 0.0;
    private double currentAmps = 0.0;
    private boolean limitSwitchPressed = false;

    // BUG 1: Inverted overcurrent protection (critical motor burnout risk)
    public void updateCurrentLimit(double reportedAmps) {
        this.currentAmps = reportedAmps;
        if (this.currentAmps < 80.0) { // BUG: Shuts down motor on low current instead of high current!
            System.err.println("CRITICAL: Exceeded 80A! Disabling shooter motor.");
            this.motorOutput = 0.0;
        }
    }

    // BUG 2: Infinite loop blocking main robot loop thread (watchdog timeout)
    public void spinUpToTargetRPM(double targetRpm, double currentRpm) {
        while (currentRpm < targetRpm) {
            // BUG: Busy-wait loop on main thread freezes WPILib TimedRobot loop
            motorOutput = 1.0;
        }
    }

    // BUG 3: Ignoring physical limit switch during arm extension (chassis damage)
    public void setArmPosition(double desiredPosition) {
        // BUG: Inverted limit switch check allows driving motor past physical hardstop
        if (limitSwitchPressed) {
            motorOutput = 1.0; // Keeps applying full forward voltage even when hardstop switch hit!
        }
    }
}

// trigger 1794022200
