import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;

public class Monitor extends Thread {
	private volatile boolean running = true;
	private GraphicsLCD lcd;
	private Robot myRobot;
	
	public Monitor(Robot myRobot) {
		this.setDaemon(true);
		this.myRobot = myRobot;
		lcd = LocalEV3.get().getGraphicsLCD();
	}
	
	public void run() {
        while (running) {
        	lcd.clear();
        	
        	// display whether the bumpers have been pressed and the distance from the ultrasonic sensor
            lcd.setFont(Font.getSmallFont());
        	lcd.drawString("LBump: " + myRobot.isLeftBumperPressed(), 0, 20, 0);
        	lcd.drawString("RBump: " + myRobot.isRightBumperPressed(), 0, 30, 0);
        	lcd.drawString("Dist: " + myRobot.getDistance(), 0, 40, 0); 
        	
    		try {
				sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
        }
	}
	
	public void terminate() {
		running = false;
	}
}
