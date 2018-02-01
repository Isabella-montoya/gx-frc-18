package org.gravitechx.frc2018.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.gravitechx.frc2018.utils.motorconfigs.TalonConfig;
import org.gravitechx.frc2018.utils.motorconfigs.TalonPIDConfig;

import java.util.function.UnaryOperator;

/**
 * Contains all constants used on the 2018 robot.
 */
public class Constants {
    /* TESTING CONSTANTS */
    public static int kPIDLoopIdx = 0;
    public static int kTimeoutMs = 1000;

    /* ========================== */
    /* Motor controller constants */
    /* ========================== */
    public static final int LEFT_TALON_CAN_CHANNEL = 0;
    public static final int RIGHT_TALON_CAN_CHANNEL = 1;
    public static final int LEFT_VICTOR_CAN_CHANNEL = 0;
    public static final int RIGHT_VICTOR_CAN_CHANNEL = 1;

    // Is reversed
    public static final boolean LEFT_DRIVE_MOTOR_REVERSED = false;
    public static final boolean RIGHT_DRIVE_MOTOR_REVERSED = true;

    /* PID */
    public static final TalonPIDConfig DRIVE_PID_CONFIG =
            new TalonPIDConfig(0.35, 5e-7, .33, 0.01, 1.3);
    public static final double DRIVE_ENCODER_MOTIFIER =  4096 * 500.0 / 600.0 * 2.0;

    /* NEGATIVE INERTIA CONSTANTS */
    public static final double NEG_INERTIA_TURN_SCALAR = 2.5;
    public static final double NEG_INERTIA_THREASHOLD = 0.65;
    public static final double NEG_INERTIA_CLOSE_SCALAR = 4.0;
    public static final double NEG_IRERTIA_FAR_SCALAR = 5.0;

    /* QUICK STOP */
    public static final double QUICK_STOP_DEADBAND =  0.16;
    public static final double QUICK_STOP_WEIGHT = 0.2;
    public static final double QUICK_STOP_SCALAR = 5.0;

    /* == */
    /* IO */
    /* == */

    /* WHEEL PORTS */
    public static final int ROTATION_STICK_PORT = 0;
    public static final int THROTTLE_STICK_PORT = 1;
    public static final int IO_QUICK_TURN_BUTTON = 2;

    /* Control System Deadband */
    public static final double THROTTLE_DEADBAND = 0.04;
    public static final double WHEEL_DEADBAND = 0.02;

    /* Throttle Reverse */
    public static final boolean REVERSE_THROTTLE_STICK = false;

    /* Control System Joystick Functions */
    public static final UnaryOperator<Double> THROTTLE_TRANSPOSITION_OPERATION = new UnaryOperator<Double>() {
        @Override
        public Double apply(Double signal) {
            return signal;
        }
    };

    /* Control System Transposition Function for Wheel */
    public static final double WHEEL_NONLINEARITY = 0.3;
    public static final UnaryOperator<Double> WHEEL_TRANSPOSITION_OPERATION = new UnaryOperator<Double>() {
        final double denominator = Math.sin(Math.PI / 2.0 *  WHEEL_NONLINEARITY);

        @Override
        public Double apply(Double signal) {
            signal = Math.sin(Math.PI / 2.0 * WHEEL_NONLINEARITY * signal) / denominator;
            signal = Math.sin(Math.PI / 2.0 * WHEEL_NONLINEARITY * signal) / denominator;
            return signal;
        }
    };

    /* ============= */
    /* MOTOR CONFIGS */
    /* ============= */

    /* Talon on the drive train */
    public static class DriveTalonConfig extends TalonConfig {
        public DriveTalonConfig(){
            super();
            this.BREAK_MODE = NeutralMode.Coast;
            this.CLOSED_LOOP_RAMP_RATE_SEC = 4.0;
            this.OPEN_LOOP_RAMP_RATE_SEC = 4.0;
        }
    }
}
