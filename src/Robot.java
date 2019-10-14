import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class Robot {
    private Brick ev3;
    private EV3TouchSensor leftBumper, rightBumper;
	private EV3UltrasonicSensor ultrasonicSensor;
    private SampleProvider leftBumperSampleProvider, rightBumperSampleProvider, ultrasonicSampleProvider;
    private float[] leftBumperSample, rightBumperSample, ultrasonicSample;
    private MovePilot pilot;

    public static void main(String[] args) {
        Robot myRobot = new Robot();
        Monitor myMonitor = new Monitor(myRobot);	
        
        // start the monitor
     	myMonitor.start();
        
        // test the pilot
        myRobot.pilot.travel(5);
        myRobot.pilot.rotate(45);
        
        // stop the monitor thread and close the robot
        Button.ESCAPE.waitForPress();
        myMonitor.terminate();
        myRobot.closeRobot();
    }

    // SmartRobot constructor
    public Robot() {
        ev3 = BrickFinder.getDefault();
        setupTouchSensor();
//        setupUltrasonicSensor();
        setupPilot();
    }

    // set up the bumpers
    private void setupTouchSensor() {
        leftBumper = new EV3TouchSensor(ev3.getPort("S1"));
        rightBumper = new EV3TouchSensor(ev3.getPort("S4"));
        leftBumperSampleProvider = leftBumper.getTouchMode();
        rightBumperSampleProvider = rightBumper.getTouchMode();
        leftBumperSample = new float[leftBumperSampleProvider.sampleSize()];
        rightBumperSample = new float[rightBumperSampleProvider.sampleSize()];
    }
    
	// set up the ultrasonic sensor
    private void setupUltrasonicSensor() {
        ultrasonicSensor = new EV3UltrasonicSensor(ev3.getPort("S3"));
        ultrasonicSampleProvider = ultrasonicSensor.getDistanceMode();
        ultrasonicSample = new float[ultrasonicSampleProvider.sampleSize()];
    }
    
    // set up the pilot
    private void setupPilot() {
        Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.05).offset(-4.9);
        Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.05).offset(4.9);
        Chassis myChassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);
        pilot = new MovePilot(myChassis);
        pilot.setLinearSpeed(10);
    }

    // whether the left bumper is pressed
    public boolean isLeftBumperPressed() {
        leftBumperSampleProvider.fetchSample(leftBumperSample, 0);
        return (leftBumperSample[0] == 1.0);
    }

    // whether the right bumper is pressed
    public boolean isRightBumperPressed() {
        rightBumperSampleProvider.fetchSample(rightBumperSample, 0);
        return (rightBumperSample[0] == 1.0);
    }
    
    // get the distance from the ultrasonic sensor
    public float getDistance() {
    	ultrasonicSampleProvider.fetchSample(ultrasonicSample, 0);
    	return ultrasonicSample[0];
	}
    
    // get the pilot object from the robot
    public MovePilot getPilot() {
      return this.pilot;
    }
    
    //get the robots current angle
//    public float getAngle() {
//        gyroSP.fetchSample(angleSample, 0);
//        return angleSample[0];
//    }

    // close the bumpers and ultrasonic sensor
    private void closeRobot() {
        leftBumper.close();
        rightBumper.close();
        ultrasonicSensor.close();
    }
}