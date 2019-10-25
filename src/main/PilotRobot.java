package main;
import lejos.hardware.Battery;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;

import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;

/**
 * Robot controller class
 * Initialises sensors and the pilot.
 */
public class PilotRobot {
	private Brick ev3;
	private EV3UltrasonicSensor ultrasonicSensor;
	private EV3GyroSensor gyroSensor;
	private EV3ColorSensor leftColorSensor, rightColorSensor;
	private SampleProvider ultrasonicSampleProvider, gyroSampleProvider, leftColorSampleProvider, rightColorSampleProvider;
	private float[] ultrasonicSample, gyroSample, leftColorSample, rightColorSample;
	private MovePilot pilot;
	private OdometryPoseProvider opp;
	private boolean scanRequired = true;
	private Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.05).offset(-4.9);
	private Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.05).offset(4.9);
	private float correctedGyro;
	
	// SmartRobot constructor
	public PilotRobot() {
		ev3 = BrickFinder.getDefault();
		setupColorSensors();
		setupGyroSensor();
		setupUltrasonicSensor();
		setupPilot();
		setupOdometryPoseProvider();
	}
	
	/**
	 * Set up the color sensors.
	 */
	private void setupColorSensors() {
		leftColorSensor = new EV3ColorSensor(ev3.getPort("S1"));
		leftColorSampleProvider = leftColorSensor.getColorIDMode();
		leftColorSample = new float[leftColorSampleProvider.sampleSize()];
		rightColorSensor = new EV3ColorSensor(ev3.getPort("S4"));
		rightColorSampleProvider = rightColorSensor.getColorIDMode();
		rightColorSample = new float[rightColorSampleProvider.sampleSize()];
	}
	
	/**
	 * Set up the gyro sensor.
	 */
	private void setupGyroSensor() {
		gyroSensor = new EV3GyroSensor(ev3.getPort("S3"));
		gyroSampleProvider = gyroSensor.getAngleMode();
		gyroSample = new float[gyroSampleProvider.sampleSize()];
	}

	/**
	 * Set up the ultrasonic sensor.
	 */
	private void setupUltrasonicSensor() {
		ultrasonicSensor = new EV3UltrasonicSensor(ev3.getPort("S2"));
		ultrasonicSampleProvider = ultrasonicSensor.getDistanceMode();
		ultrasonicSample = new float[ultrasonicSampleProvider.sampleSize()];
	}

	/**
	 * Set up the pilot.
	 */
	private void setupPilot() {
		Chassis myChassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(myChassis);
//		pilot.setLinearAcceleration(5);
		pilot.setLinearSpeed(3);
		// pilot.setAngularAcceleration(45);
		pilot.setAngularSpeed(45);
	}
	
	/**
	 * Set up the odometry pose provider.
	 */
	private void setupOdometryPoseProvider() {
		opp = new OdometryPoseProvider(pilot);
		opp.setPose(new Pose(0, 0, 0));
	}
	
	// whether a scan is required
	public final boolean isScanRequired() {
		return scanRequired;
	}
	
	// get the ultrasonic sensor
	public final EV3UltrasonicSensor getUltrasonicSensor() {
		return ultrasonicSensor;
	}

	// get the distance from the ultrasonic sensor
	public final float getDistance() {
		ultrasonicSampleProvider.fetchSample(ultrasonicSample, 0);
		return ultrasonicSample[0] * 100;
	}

	// get the robots current angle
	public final float getAngle() {
		gyroSampleProvider.fetchSample(gyroSample, 0);
		return gyroSample[0];
	}
	
	// get the left color sample
	public final float[] getLeftColor() {
		leftColorSampleProvider.fetchSample(leftColorSample, 0);
		return leftColorSample;
	}
	
	// get the left color sample
	public final float[] getRightColor() {
		rightColorSampleProvider.fetchSample(rightColorSample, 0);
		return rightColorSample;
	}

	// get the pilot object from the robot
	public final MovePilot getPilot() {
		return this.pilot;
	}
	
	// get the odometry pose provider
	public final OdometryPoseProvider getOdometryPoseProvider() {
		return opp;
	}

	// get the robots current battery voltage
	public final float getBatteryVoltage() {
		return Battery.getVoltage();
	}
	
	// set whether a scan is required
	public final void setScanRequired(boolean scanRequired) {
		this.scanRequired = scanRequired;
	}
	
	// reset the gyro sensor
	public final void resetGyro() {
		gyroSensor.reset();
	}

	// close the bumpers, ultrasonic sensor & gyro sensor
	public final void closeRobot() {
		ultrasonicSensor.close();
		gyroSensor.close();
	}
	
	public Brick getBrick() {
		return ev3;
	}
	
	public Wheel getRightWheel() {
		return rightWheel;
	}
	
	public Wheel getLeftWheel() {
		return leftWheel;
	}
	
	public float getCorrectedGyro() {
		return correctedGyro;
	}
	
	public void setCorrectedGyro(float cg) {
		this.correctedGyro = cg;
	}
	
}
