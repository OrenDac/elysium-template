package frc.lib.generic.sensors;

import frc.lib.generic.advantagekit.HardwareManager;
import frc.lib.generic.advantagekit.LoggableHardware;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.Logger;

public class Sensor implements LoggableHardware {
    private final SensorInputsAutoLogged inputs = new SensorInputsAutoLogged();
    private final String name;

    public Sensor(String name) {
        this.name = name;

        periodic();
        HardwareManager.addHardware(this);
    }

    public int get() { return inputs.currentValue; }

    @Override
    public void periodic() {
        refreshInputs(inputs);
        Logger.processInputs(name, inputs);
    }

    public SensorInputsAutoLogged getInputs() { return inputs; }
    public void refreshInputs(SensorInputsAutoLogged inputs) { }

    @AutoLog
    public static class SensorInputs {
        public int currentValue = 0;

        public int[] threadCurrentValue = new int[0];
        public double[] timestamp = new double[0];
    }
}
