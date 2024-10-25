package frc.lib.generic;

import java.util.function.Function;

public class Feedforward {
    public record FeedForwardConstants(double kS, double kV, double kA, double kG) {
    }

    public record FeedForwardValues(double positionRotations, double velocityRPS, double accelerationRPSPS) {
    }

    public record ClosedLoopValues(FeedForwardConstants constants, FeedForwardValues values) {
    }

    public enum Type {
        SIMPLE(closedLoopValues -> calculateSimpleFeedforward(closedLoopValues.constants, closedLoopValues.values)),

        ARM(closedLoopValues ->
                calculateSimpleFeedforward(closedLoopValues.constants, closedLoopValues.values) +
                        closedLoopValues.constants.kG * Math.cos(closedLoopValues.values.positionRotations * 2 * Math.PI)
        ),

        ELEVATOR(closedLoopValues ->
                calculateSimpleFeedforward(closedLoopValues.constants, closedLoopValues.values) +
                        closedLoopValues.constants.kG
        );

        public final Function<ClosedLoopValues, Double> calculationFunction;
        public FeedForwardConstants konstants = new FeedForwardConstants(0, 0, 0, 0);

        Type(Function<ClosedLoopValues, Double> calculationFunction) {
            this.calculationFunction = calculationFunction;
        }

        public void setFeedforwardConstants(double kS, double kV, double kA, double kG) {
            this.konstants = new FeedForwardConstants(kS, kV, kA, kG);
        }

        public void setFeedforwardConstants(double kS, double kV, double kA) {
            this.konstants = new FeedForwardConstants(kS, kV, kA, 0);
        }

        /**
         * @param currentPositionRotations For arm ONLY. This should be the CURRENT ARM POSITION in rotations. For non-arms, this will be ignored.
         * @param velocityRPS       unit-less, preferably in rotations per second
         * @param accelerationRPSPS unit-less, preferably rotations per second squared
         * @return The input for the motor, in voltage
         */
        public double calculate(double currentPositionRotations, double velocityRPS, double accelerationRPSPS) {
            return calculationFunction.apply(new ClosedLoopValues(konstants,
                    new FeedForwardValues(currentPositionRotations, velocityRPS, accelerationRPSPS)));
        }

        /**
         * @param velocityRPS       unit-less, preferably in rotations per second
         * @param accelerationRPSPS unit-less, preferably rotations per second squared
         * @return The input for the motor, in voltage
         */
        public double calculate(double velocityRPS, double accelerationRPSPS) {
            return calculate(0, velocityRPS, accelerationRPSPS);
        }

        private static double calculateSimpleFeedforward(FeedForwardConstants constants, FeedForwardValues values) {
            return constants.kS * Math.signum(values.velocityRPS) + constants.kV * values.velocityRPS + constants.kA * values.accelerationRPSPS;
        }
    }

    // kS; Volts required to overcome the force of static friction
    // kV; Volts required to maintain a velocity of one unit
    // kA; Volts required to accelerate the mechanism by one unit
    // kG; Volts required to overcome the force of gravity
}
