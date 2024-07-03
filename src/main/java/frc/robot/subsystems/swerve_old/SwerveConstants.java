package frc.robot.subsystems.swerve_old;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import frc.lib.util.LoggedTunableNumber;
import frc.robot.GlobalConstants;
import frc.robot.subsystems.swerve_old.real.RealSwerveConstants;
import frc.robot.subsystems.swerve_old.simulation.SimulatedSwerveConstants;

import java.util.Optional;
import java.util.function.Supplier;

import static edu.wpi.first.units.Units.Inch;
import static edu.wpi.first.units.Units.Meters;
import static frc.robot.subsystems.swerve.SwerveConstants.ROTATION_CONTROLLER;

public abstract class SwerveConstants {
    public static final double WHEEL_DIAMETER = Meters.convertFrom(4, Inch);

    static final double WHEEL_BASE = 0.565;
    static final double TRACK_WIDTH = 0.615;

    public static final SwerveDriveKinematics SWERVE_KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            new Translation2d(WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0),

            new Translation2d(-WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            new Translation2d(-WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0));

    public static final double DRIVE_BASE_RADIUS = new Translation2d(TRACK_WIDTH / 2, WHEEL_BASE / 2).getNorm();

    public static final double MAX_SPEED_MPS = 5.1;
    public static final double MAX_ROTATION_RAD_PER_S = 3 * Math.PI;

    protected static <T> Optional<T> ofReplayable(Supplier<T> value) {
        if (GlobalConstants.CURRENT_MODE == GlobalConstants.Mode.REPLAY)
            return Optional.empty();
        return Optional.of(value.get());
    }

    static SwerveConstants generateConstants() {
        ROTATION_CONTROLLER.enableContinuousInput(-Math.PI, Math.PI);
        ROTATION_CONTROLLER.setTolerance(Units.degreesToRadians(0.5));

        if (GlobalConstants.CURRENT_MODE == GlobalConstants.Mode.REAL) {
            return new RealSwerveConstants();
        }

        return new SimulatedSwerveConstants();
    }


    protected abstract Optional<SwerveModuleIO[]> getSwerveModules();
}