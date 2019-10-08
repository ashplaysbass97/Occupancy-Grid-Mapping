import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class SmartRobot {
    private Brick ev3;
    private GraphicsLCD lcd;
    private EV3TouchSensor leftBumper, rightBumper;
	private EV3UltrasonicSensor ultrasonicSensor;
    private SampleProvider leftBumperSampleProvider, rightBumperSampleProvider, ultrasonicSampleProvider;
    private float[] leftBumperSample, rightBumperSample, ultrasonicSample;
    private NXTRegulatedMotor ultrasonicSensorMotor;
    private MovePilot pilot;

    public static void main(String[] args) {
        SmartRobot myRobot = new SmartRobot();
        
        // test the pilot
        myRobot.pilot.travel(5);
        myRobot.pilot.rotate(45);
        
        // display whether the bumpers have been pressed and the distance from the ultrasonic sensor
        while (!Button.ESCAPE.isDown()) {
        	myRobot.lcd.clear();
            myRobot.lcd.setFont(Font.getSmallFont());
        	myRobot.lcd.drawString("LBump: " + myRobot.isLeftBumperPressed(), 0, 20, 0);
        	myRobot.lcd.drawString("RBump: " + myRobot.isRightBumperPressed(), 0, 30, 0);
        	myRobot.lcd.drawString("Dist: " + myRobot.getDistance(), 0, 40, 0); 
    		try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        myRobot.closeRobot();
    }

    // SmartRobot constructor
    public SmartRobot() {
        ev3 = BrickFinder.getDefault();
        lcd = LocalEV3.get().getGraphicsLCD();
        setupTouchSensor();
        setupUltrasonicSensor();
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
        ultrasonicSensorMotor = Motor.C;
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

    // close the bumpers
    private void closeRobot() {
        leftBumper.close();
        rightBumper.close();
    }
}