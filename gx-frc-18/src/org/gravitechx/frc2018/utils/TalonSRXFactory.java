package org.gravitechx.frc2018.utils;

import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import org.gravitechx.frc2018.utils.motorconfigs.PIDConfig;
import org.gravitechx.frc2018.utils.motorconfigs.TalonConfig;
import org.gravitechx.frc2018.utils.motorconfigs.TalonPIDConfig;
import org.gravitechx.frc2018.utils.wrappers.EfficientTalonSRX;
import org.gravitechx.frc2018.utils.wrappers.MasterTalonSRX;

/**
 * Constructs Talon objects using various configurations.
 */
public class TalonSRXFactory {

    /**
     * Creates a Talon using only the default settings and the PWM port.
     * @param canPort
     * @return
     */
    public static WPI_TalonSRX createDefaultTalon(int canPort){
        return createTalon(canPort, new TalonConfig());
    }

    /**
     * Creates a Slave, Talon pair using default values.
     * @param canPort
     * @param speedController
     * @return
     */
    public static MasterTalonSRX createDefaultSlaveTalon(int canPort, SpeedController speedController){
        return createSlaveTalon(canPort, new TalonConfig(), speedController);
    }

    /**
     * Create a slave talon and returns the master.
     */
    public static MasterTalonSRX createSlaveTalon(int port, TalonConfig config, SpeedController s){
        return (MasterTalonSRX) configureTalon(new MasterTalonSRX(port, s), config);
    }

    /**
     * Creates a WPI_TalonSRX using a TalonConfig
     * @param port
     * @param config
     * @return
     */
    public static WPI_TalonSRX createTalon(int port, TalonConfig config){
        WPI_TalonSRX talon = new EfficientTalonSRX(port);
        return configureTalon(talon, config);
    }

    private static WPI_TalonSRX configureTalon(WPI_TalonSRX talon, TalonConfig config){

        // CAN settings
        talon.changeMotionControlFramePeriod(config.MOTION_CONTROL_FRAME_PERIOD_MS);

        // Motion profile
        talon.clearMotionProfileHasUnderrun(config.TIME_TILL_ERROR_MS);
        talon.clearMotionProfileTrajectories();

        // Clear faults
        talon.clearStickyFaults(config.TIME_TILL_ERROR_MS);

        // PID
        talon.setIntegralAccumulator(0.0, config.PID_ID, config.TIME_TILL_ERROR_MS);

        // Configure ouputs
        talon.configPeakOutputForward(config.PEAK_OUTPUT_FORWARD, config.TIME_TILL_ERROR_MS);
        talon.configPeakOutputReverse(config.PEAK_OUTPUT_REVERSE, config.TIME_TILL_ERROR_MS);
        talon.configNominalOutputForward(config.NOMINAL_OUTPUT_FORWARD, config.TIME_TILL_ERROR_MS);
        talon.configNominalOutputForward(config.NOMINAL_OUTPUT_REVERSE,  config.TIME_TILL_ERROR_MS);

        // Sources for limit Swtiches
        talon.configForwardLimitSwitchSource(config.LIMIT_SWITCH_FORWARD_SOURCE, config.LIMIT_SWITCH_FORWARD_NORMAL_POSITION, config.TIME_TILL_ERROR_MS);
        talon.configReverseLimitSwitchSource(config.LIMIT_SWITCH_REVERSE_SOURCE, config.LIMIT_SWITCH_REVERSED_NORMAL_POSITION, config.TIME_TILL_ERROR_MS);

        // Break mode
        talon.setNeutralMode(config.BREAK_MODE);

        // Soft limits
        talon.configForwardSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, config.TIME_TILL_ERROR_MS);
        talon.configReverseSoftLimitThreshold(config.REVERSE_SOFT_LIMIT, config.TIME_TILL_ERROR_MS);
        talon.configForwardSoftLimitEnable(config.ENABLE_SOFT_LIMIT, config.TIME_TILL_ERROR_MS);
        talon.configReverseSoftLimitEnable(config.ENABLE_SOFT_LIMIT, config.TIME_TILL_ERROR_MS);


        talon.setExpiration(config.EXPIRATION_TIMEOUT_SECONDS);
        talon.setInverted(config.INVERTED);
        talon.setSafetyEnabled(config.SAFETY_ENABLED);

        talon.overrideLimitSwitchesEnable(config.ENABLE_LIMIT_SWITCH);
        talon.getSensorCollection().setAnalogPosition(0, config.TIME_TILL_ERROR_MS);
        talon.selectProfileSlot(config.SLOT_ID, config.PID_ID);
        talon.setSelectedSensorPosition(0, config.PID_ID, config.TIME_TILL_ERROR_MS);

        talon.configContinuousCurrentLimit(0, config.CURRENT_LIMIT);
        talon.enableCurrentLimit(config.ENABLE_CURRENT_LIMIT);
        talon.getSensorCollection().setPulseWidthPosition(0, config.TIME_TILL_ERROR_MS);

        talon.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, config.TIME_TILL_ERROR_MS);
        talon.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW, config.TIME_TILL_ERROR_MS);
        talon.configOpenloopRamp(config.OPEN_LOOP_RAMP_RATE_SEC, config.TIME_TILL_ERROR_MS);
        talon.configClosedloopRamp(config.CLOSED_LOOP_RAMP_RATE_SEC, config.TIME_TILL_ERROR_MS);

        /*
        Depricated functions for which I (Alex) couldn't find a replacement.

        talon.enableZeroSensorPositionOnForwardLimit(false);
        talon.enableZeroSensorPositionOnIndex(false, false);
        talon.enableZeroSensorPositionOnReverseLimit(false);
        talon.reverseOutput(false);
        talon.reverseSensor(false);
        talon.setStatusFramePeriod(, config.QUAD_ENCODER_STATUS_FRAME_RATE_MS);
        talon.setStatusFrameRateMs(StatusFrame., config.PULSE_WIDTH_STATUS_FRAME_RATE_MS);
        */

        talon.setStatusFramePeriod(StatusFrame.Status_1_General, config.GENERAL_STATUS_FRAME_RATE_MS, config.TIME_TILL_ERROR_MS);
        talon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, config.FEEDBACK_STATUS_FRAME_RATE_MS, config.TIME_TILL_ERROR_MS);
        talon.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, config.TIME_TILL_ERROR_MS);


        return talon;
    }

    public static WPI_TalonSRX configurePID(WPI_TalonSRX talon, TalonPIDConfig config){
        talon.config_kP(config.PID_ID, config.kP, 0);
        talon.config_kI(config.PID_ID, config.kI, 0);
        talon.config_kD(config.PID_ID, config.kD, 0);
        talon.config_kF(config.PID_ID, config.kD, 0);
        return talon;
    }

    /* A start of a to string function. We can add things as we go. */

    public static String getProperties(WPI_TalonSRX talon){
        return "Inverted: " + talon.getInverted() + "\n";
    }
}
