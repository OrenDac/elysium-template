package frc.robot.subsystems.flywheel;

import edu.wpi.first.math.util.Units;

public class FlywheelConstants {
    public static final double LEFT_FLYWHEEL_DIAMETER =  Units.inchesToMeters(3);
    public static final double RIGHT_FLYWHEEL_DIAMETER =  Units.inchesToMeters(4);

    public static final double TOLERANCE_ROTATIONS_PER_SECONDS = 0.9;

    public static final double MAXIMUM_VELOCITY_RPM = 5400;

    public static final boolean LEFT_MOTOR_INVERT = false;
    public static final boolean RIGHT_MOTOR_INVERT = true;

    public static FlywheelIO[] getFlywheels() {
        return new FlywheelIO[]{
                new FlywheelIO("Left"),
                new FlywheelIO("Right")
        };
    }
}
