package org.gravitechx.frc2018.robot.subsystems;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.gravitechx.frc2018.robot.Constants;
import org.gravitechx.frc2018.utils.PIDFController;
import org.gravitechx.frc2018.utils.TalonSRXFactory;
import org.gravitechx.frc2018.utils.drivehelpers.DriveSignal;
import org.gravitechx.frc2018.utils.lifthelpers.ElevatorHallEffect;

public class Lift extends Subsystem {
    /* MOTOR CONTROLLERS */
    private LiftGearBox leftGearBox;
    private LiftGearBox rightGearBox;

    private double outputScalar = 1.6;

    private ElevatorHallEffect mTopHall;
    private ElevatorHallEffect mMidHall;
    private ElevatorHallEffect mBottomHall;

    private SensorCollection encoder;

    private double M_MAX_VOLTAGE = Constants.MAX_LIFT_VOLTAGE;

    private double mPosition = 0.0;

    /* SINGLETON */
    private static Lift mLift = new Lift();

    private PIDFController liftController;

    enum ControlState {NEUTRAL, HOLDING, LIFTING};

    private ControlState mControlMode;

    private int mLastQuadature = 0;

    public static Lift getInstance(){
        return mLift;
    }

    public void zeroPosition(){
        mPosition = 0.0;
        encoder.setQuadraturePosition(0, 0);
        mLastQuadature = encoder.getQuadraturePosition();
    }

    public void setPosition(double position){
        mPosition = position;
        mLastQuadature = encoder.getQuadraturePosition();
    }

    public void getPosition(){
        mPosition += getDQ();
    }

    public double getDQ(){
        double dq = (encoder.getQuadraturePosition() - mLastQuadature);
        mLastQuadature = encoder.getQuadraturePosition();
        return dq * Constants.QUADRATURE_TO_METER_LIFT;
    }

    public class LiftGearBox {
        private Spark mSlaveFront;
        private Spark mMaster;
        private Spark mSlaveBack;


        public LiftGearBox(Spark mSlaveFront, Spark mMaster, Spark mSlaveBack){
            this.mSlaveFront = mSlaveFront;
            this.mMaster = mMaster;
            this.mSlaveBack = mSlaveBack;
        }

        public void set(double speed){
            mMaster.set(speed);
            mSlaveFront.set(speed);
            mSlaveBack.set(speed);
        }

        /* GETTERS AND SETTERS */
        public Spark getmSlaveFront() {
            return mSlaveFront;
        }

        public void setmSlaveFront(Spark mSlaveFront) {
            this.mSlaveFront = mSlaveFront;
        }

        public Spark getmMaster() {
            return mMaster;
        }

        public void setmMaster(Spark mMaster) {
            this.mMaster = mMaster;
        }

        public Spark getmSlaveBack() {
            return mSlaveBack;
        }

        public void setmSlaveBack(Spark mSlaveBack) {
            this.mSlaveBack = mSlaveBack;
        }
    }

    private Lift(){
        WPI_TalonSRX jankySensorTalon = TalonSRXFactory.createTalon(Constants.JANKEY_SENSOR_TALON, new Constants.ElevatorTalonConfig());
        encoder = jankySensorTalon.getSensorCollection();

        leftGearBox = new LiftGearBox(new Spark(Constants.LEFT_LIFT_FRONT_SPARK_PWM_CHANNEL),
                new Spark(Constants.MID_LIFT_LEFT_PWM_CHANNEL),
                new Spark(Constants.LEFT_LIFT_BACK_SPARK_PWM_CHANNEL));

        rightGearBox = new LiftGearBox(
                new Spark(Constants.RIGHT_LIFT_FRONT_SPARK_PWM_CHANNEL),
                new Spark(Constants.MID_LIFT_RIGHT_PWM_CHANNEL),
                /* TalonSRXFactory.createTalon(Constants.RIGHT_LIFT_TALON_CAN_CHANNEL, new Constants.ElevatorTalonConfig()), */
                new Spark(Constants.RIGHT_LIFT_BACK_SPARK_PWM_CHANNEL));

        liftController = new PIDFController(Constants.LIFT_PIDF_CONFIG);

        mTopHall = new ElevatorHallEffect(Constants.HALL_TOP_DIO_CHANNEL, Constants.HALL_TOP_DISTANCE);
        mMidHall = new ElevatorHallEffect(Constants.HALL_MID_DIO_CHANNEL, Constants.HALL_MID_DISTANCE);
        mBottomHall = new ElevatorHallEffect(Constants.HALL_BOTTOM_DIO_CHANNEL, Constants.HALL_BOTTOM_DISTANCE);

        mControlMode = ControlState.LIFTING;
    }

    public double getHallPosition(){
        if(mTopHall.isPressed()) return mTopHall.getOffset();
        if(mBottomHall.isPressed()) return mBottomHall.getOffset();
        if(mMidHall.isPressed()) return mMidHall.getOffset();

        return - Integer.MAX_VALUE;
    }

    public void set(double speed){
        if(mControlMode != ControlState.NEUTRAL) {
            speed = DriveSignal.limit(speed, 1.0);
            speed += Constants.NOMINAL_UP_VOLTAGE / 12.0;
            setDirect(speed);
        }
    }

    public void setDirect(double speed){
        speed = DriveSignal.limit(speed, 1.0);
        leftGearBox.set(M_MAX_VOLTAGE * speed / 12.0);
        rightGearBox.set(-M_MAX_VOLTAGE * speed / 12.0);
    }

    public void setSetPoint(double position, double velocity, double acceleration){
        System.out.println("SIGNAL: " + position + " REAL: " + mPosition);
        liftController.setSetpoints(position, velocity, acceleration);
        liftController.run(mPosition, Timer.getFPGATimestamp());
        set(liftController.get() * outputScalar);
        System.out.print("SET POINT: " + position + "Lift Controller: " + (M_MAX_VOLTAGE * liftController.get() * outputScalar / 12.0) + " DISTANCE: " + mPosition + "\n");
    }

    public void setRelitivePosition(double position, double velocity, double acceleration){
        setSetPoint(position * Constants.LIFT_MAX_TRAVEL_M, velocity, acceleration);
    }

    @Override
    protected void initDefaultCommand() {

    }

    public double getOutputScalar() {
        return outputScalar;
    }

    public void setOutputScalar(double outputScalar) {
        this.outputScalar = outputScalar;
    }

    public void loop(){
        getPosition();
        /*
        double hallPosition = getHallPosition();
        if(hallPosition >= 0.0) {
            mPosition = hallPosition;
        }
        */
        SmartDashboard.putNumber("Position: ", mPosition);
    }

    public void graphPIDOuts(){
        SmartDashboard.putNumber("KP: ", liftController.getKP());
        SmartDashboard.putNumber("KI: ", liftController.getKI());
        SmartDashboard.putNumber("KD: ", liftController.getKD());
        SmartDashboard.putNumber("KA: ", liftController.getKA());
        SmartDashboard.putNumber("KV: ", liftController.getKV());
        SmartDashboard.putNumber("Error: ", liftController.getError());
    }
}
