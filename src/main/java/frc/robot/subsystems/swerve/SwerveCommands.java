package frc.robot.subsystems.swerve;

import com.pathplanner.lib.commands.FollowPathCommand;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import frc.lib.util.commands.InitExecuteCommand;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import static frc.robot.RobotContainer.POSE_ESTIMATOR;
import static frc.robot.RobotContainer.SWERVE;
import static frc.robot.subsystems.swerve.SwerveConstants.*;
import static frc.robot.subsystems.swerve.SwerveConstants.ROTATION_CONTROLLER;
import static frc.robot.subsystems.swerve.SwerveModuleConstants.MODULES;

public class SwerveCommands {
    public static Command lockSwerve() {
        return Commands.run(
                () -> {
                    final SwerveModuleState
                            right = new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
                            left = new SwerveModuleState(0, Rotation2d.fromDegrees(45));

                    MODULES[0].setTargetState(left);
                    MODULES[1].setTargetState(right);
                    MODULES[2].setTargetState(right);
                    MODULES[3].setTargetState(left);
                },
                SWERVE
        );
    }

    public static Command goToPoseBezier(Pose2d targetPose) {
        return new FollowPathCommand(
                new PathPlannerPath(
                        PathPlannerPath.bezierFromPoses(POSE_ESTIMATOR.getCurrentPose(), targetPose),
                        new PathConstraints(MAX_SPEED_MPS, MAX_SPEED_MPS * 2,
                                MAX_ROTATION_RAD_PER_S, MAX_ROTATION_RAD_PER_S * 2),
                        new GoalEndState(0, targetPose.getRotation(), true)
                ),

                POSE_ESTIMATOR::getCurrentPose,
                SWERVE::getSelfRelativeVelocity,
                SWERVE::driveSelfRelative,

                new PPHolonomicDriveController(
                        HOLONOMIC_PATH_FOLLOWER_CONFIG.translationConstants,
                        HOLONOMIC_PATH_FOLLOWER_CONFIG.rotationConstants,
                        MAX_SPEED_MPS,
                        DRIVE_BASE_RADIUS),

                HOLONOMIC_PATH_FOLLOWER_CONFIG.replanningConfig,
                () -> true,
                SWERVE
        );
    }


    public static Command goToPoseWithPID(Pose2d targetPose) {
        final Pose2d fixedTargetPose = new Pose2d(targetPose.getTranslation(), Rotation2d.fromDegrees(MathUtil.inputModulus(targetPose.getRotation().getDegrees(), -180, 180)));

        return new FunctionalCommand(
                () -> SWERVE.initializeDrive(true),
                () -> SWERVE.driveToPose(fixedTargetPose),
                interrupt -> {
                },
                () -> false,
                SWERVE
        );
    }

    public static Command resetGyro() {
        return Commands.runOnce(() -> SWERVE.setGyroHeading(Rotation2d.fromDegrees(0)), SWERVE);
    }

    public static Command driveOpenLoop(DoubleSupplier x, DoubleSupplier y, DoubleSupplier rotation, BooleanSupplier robotCentric) {
        return new InitExecuteCommand(
                () -> SWERVE.initializeDrive(true),
                () -> SWERVE.driveOrientationBased(x.getAsDouble(), y.getAsDouble(), rotation.getAsDouble(), robotCentric.getAsBoolean()),
                SWERVE
        );
    }

    public static Command driveWhilstRotatingToTarget(DoubleSupplier x, DoubleSupplier y, Pose2d target, BooleanSupplier robotCentric) {
        return new FunctionalCommand(
                () -> SWERVE.initializeDrive(true),
                () -> SWERVE.driveWithTarget(x.getAsDouble(), y.getAsDouble(), target, robotCentric.getAsBoolean()),
                interrupt -> {
                },
                () -> false,
                SWERVE
        );
    }

    public static Command rotateToTarget(Pose2d target) {
        return new FunctionalCommand(
                () -> SWERVE.initializeDrive(true),
                () -> SWERVE.driveWithTarget(0, 0, target, false),
                interrupt -> {
                },
                ROTATION_CONTROLLER::atGoal,
                SWERVE
        ).withTimeout(5);
    }
}
