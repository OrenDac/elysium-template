package frc.lib.generic.motor;

import com.ctre.phoenix6.signals.GravityTypeValue;

public class MotorProperties {
    public enum IdleMode {
        COAST, BRAKE
    }

    public enum ControlMode {
        CURRENT, POSITION, VOLTAGE,
        PERCENTAGE_OUTPUT,

        /** In rotations per second*/
        VELOCITY
    }

    public enum SparkType {
        MAX, FLEX
    }

    public static final class Slot {
        private final double kP, kD, kI, kV, kA, kS, kG;
        private final GravityTypeValue gravityType;

        public Slot(double kP, double kD, double kI, double kV, double kA, double kS, double kG, GravityTypeValue gravityType) {
            this.kP = kP;
            this.kD = kD;
            this.kI = kI;
            this.kV = kV;
            this.kA = kA;
            this.kS = kS;
            this.kG = kG;
            this.gravityType = gravityType;
        }

        public Slot(double kP, double kD, double kI, double kV, double kA, double kS) {
            this(kP, kD, kI, kV, kA, kS, 0, null);
        }

        public double kP() {
            return kP;
        }

        public double kD() {
            return kD;
        }

        public double kI() {
            return kI;
        }

        public double kV() {
            return kV;
        }

        public double kA() {
            return kA;
        }

        public double kS() {
            return kS;
        }

        public double kG() {
            return kG;
        }

        public GravityTypeValue gravityType() {
            return gravityType;
        }
    }
}
