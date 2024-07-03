package frc.robot.subsystems.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.math.Conversions;
import frc.lib.math.Optimizations;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.Logger;

public class SwerveModuleIO {
    private final SwerveModuleInputsAutoLogged swerveModuleInputs = new SwerveModuleInputsAutoLogged();
    private final String name;

    private SwerveModuleState targetState = new SwerveModuleState();
    private boolean driveMotorClosedLoop = false;

    public SwerveModuleIO(String name) {
        this.name = name;
    }

    /**
     * This method should be called periodically to update the inputs and network tables of the module.
     */
    public void periodic() {
        refreshInputs(swerveModuleInputs);
        Logger.processInputs(getLoggingPath(), swerveModuleInputs);
    }

    public void setDriveMotorClosedLoop(boolean closedLoop) {
        driveMotorClosedLoop = closedLoop;
    }

    public void setTargetState(SwerveModuleState targetState) {
        this.targetState = Optimizations.optimize(targetState, getCurrentAngle());

        setTargetAngle(this.targetState.angle);
        setTargetVelocity(this.targetState.speedMetersPerSecond, this.targetState.angle);
    }

    protected String getLoggingPath() {
        return "Swerve/" + name + "/";
    }

    protected double velocityToOpenLoopVoltage(double velocityMetersPerSecond, double wheelDiameterMeters, double steerVelocityRevolutionsPerSecond, double couplingRatio, double maxSpeedRevolutionsPerSecond, double voltageCompensationSaturation) {
        final double velocityRevolutionsPerSecond = Conversions.metresToRotations(velocityMetersPerSecond, wheelDiameterMeters);
        final double optimizedVelocityRevolutionsPerSecond = Optimizations.removeCouplingFromRevolutions(velocityRevolutionsPerSecond, Rotation2d.fromDegrees(steerVelocityRevolutionsPerSecond), couplingRatio);
        final double power = optimizedVelocityRevolutionsPerSecond / maxSpeedRevolutionsPerSecond;

        return Conversions.compensatedPowerToVoltage(power, voltageCompensationSaturation);
    }

    /**
     * The odometry thread can update itself faster than the main code loop (which is 50 hertz).
     * Instead of using the latest odometry update, the accumulated odometry positions since the last loop to get a more accurate position.
     *
     * @param odometryUpdateIndex the index of the odometry update
     * @return the position of the module at the given odometry update index
     */
    protected SwerveModulePosition getOdometryPosition(int odometryUpdateIndex) {
        return new SwerveModulePosition(
                swerveModuleInputs.odometryUpdatesDriveDistanceMeters[odometryUpdateIndex],
                Rotation2d.fromDegrees(swerveModuleInputs.odometryUpdatesSteerAngleDegrees[odometryUpdateIndex])
        );
    }

    protected int getLastOdometryUpdateIndex() {
        return swerveModuleInputs.odometryUpdatesSteerAngleDegrees.length - 1;
    }

    SwerveModuleState getCurrentState() {
        return new SwerveModuleState(swerveModuleInputs.driveVelocityMetersPerSecond, getCurrentAngle());
    }

    SwerveModuleState getTargetState() {
        return targetState;
    }

    /**
     * Sets the target velocity for the module.
     *
     * @param targetVelocityMetersPerSecond the target velocity, in meters per second
     * @param targetSteerAngle              the target steer angle, to calculate for skew reduction
     */
    private void setTargetVelocity(double targetVelocityMetersPerSecond, Rotation2d targetSteerAngle) {
        targetVelocityMetersPerSecond = Optimizations.reduceSkew(targetVelocityMetersPerSecond, getCurrentAngle(), targetSteerAngle);

        if (driveMotorClosedLoop)
            setTargetClosedLoopVelocity(targetVelocityMetersPerSecond);
        else
            setTargetOpenLoopVelocity(targetVelocityMetersPerSecond);
    }

    private Rotation2d getCurrentAngle() {
        return Rotation2d.fromDegrees(swerveModuleInputs.steerAngleDegrees);
    }

    protected void refreshInputs(SwerveModuleInputsAutoLogged inputs) {
    }

    protected void setTargetOpenLoopVelocity(double targetVelocityMetersPerSecond) {
    }

    protected void setTargetClosedLoopVelocity(double targetVelocityMetersPerSecond) {
    }

    protected void setTargetAngle(Rotation2d angle) {
    }

    protected void stop() {
    }

    @AutoLog
    public static class SwerveModuleInputs {
        public double steerAngleDegrees = 0;
        public double steerVoltage = 0;

        public double driveVelocityMetersPerSecond = 0;
        public double driveDistanceMeters = 0;
        public double driveVoltage = 0;

        public double[] odometryUpdatesSteerAngleDegrees = new double[0];
        public double[] odometryUpdatesDriveDistanceMeters = new double[0];
    }
}
