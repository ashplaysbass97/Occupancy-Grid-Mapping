import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class SmartRobot {
    private Brick ev3;
    private EV3TouchSensor leftBumper, rightBumper;
    private SampleProvider leftBumperSampleProvider, rightBumperSampleProvider;
    private float[] leftBumperSample, rightBumperSample;

    public static void main(String[] args) {
        SmartRobot myRobot = new SmartRobot();
        
        // test the bumpers
        while (!Button.ESCAPE.isDown()) {
        	if (myRobot.isLeftBumperPressed()) {
            	System.out.println("Left bumper");
            }
            if (myRobot.isRightBumperPressed()) {
            	System.out.println("Right bumper");
            }        	
        }
        
        myRobot.closeRobot();
    }

    // SmartRobot constructor
    public SmartRobot() {
        ev3 = BrickFinder.getDefault();
        setupTouchSensor();
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

    // close the bumpers
    private void closeRobot() {
        leftBumper.close();
        rightBumper.close();
    }
}