package org.gravitechx.frc2018.utils.motorconfigs;

/**
 * A talon version of the PID config that holds a PID_ID needed by the talon.
 */
public class TalonPIDConfig extends PIDConfig {
    public int PID_ID = 0;
    public int TIME_TILL_ERROR_MS = 0;

    public TalonPIDConfig(double kP, double kI, double kD, double kF, double scalingFactor){
        super(kP, kI, kD, kF, scalingFactor);
    }

    public TalonPIDConfig(double kP, double kI, double kD, double kF){
        super(kP, kI, kD, kF);
    }
}