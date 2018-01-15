package org.gravitechx.frc2018.robot.io.controlschemes;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.gravitechx.frc2018.robot.Constants;
import org.gravitechx.frc2018.utils.drivehelpers.RotationalDriveSignal;

public class DefaultControlScheme extends ControlScheme{
    private static Joystick throttleStick = new Joystick(Constants.THROTTLE_STICK_PORT);
    private static Joystick rotationStick = new Joystick(Constants.ROTATION_STICK_PORT);
    private static DefaultControlScheme mInstance = new DefaultControlScheme();

    //singleton pattern to prevent multiple instances of DefaultControlScheme

    protected DefaultControlScheme(){
        //prevents instantiation
    }

    //returns the instance of DefaultControlScheme
    public static DefaultControlScheme getInstance() {

        return mInstance;

    }
    //returns the throttle of the inputed Joystick (throttle is the Y axis)
    @Override
    public double getThrottle(Joystick throttleStick) {
        return throttleStick.getY();
    }
    //returns the rotation value of the inputed joystick(rotation is the X axis)
    @Override
    public double getWheel(Joystick rotationStick) {
        return rotationStick.getX();
    }
    //getter for throttleStick
    public static Joystick getThrottleStick(){
        return throttleStick;
    }
    //getter for rotationStick
    public static Joystick getRotationStick (){
        return rotationStick;
    }
    //returns the rotational drive signal for the inputs of the throttleStick Y value and rotationStick X value
    public static RotationalDriveSignal getRotationalDriveSignal(){
        return new RotationalDriveSignal(throttleStick.getY(), rotationStick.getX());
    }

}
