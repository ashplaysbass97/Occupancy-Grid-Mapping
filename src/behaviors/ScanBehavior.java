package behaviors;

import java.io.File;
import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.Grid;
import main.PilotRobot;
import main.Sensor;
import monitors.PilotMonitor;

public class ScanBehavior implements Behavior {
	private boolean suppressed = false;
	private Grid grid;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private PilotMonitor myMonitor;
	private OdometryPoseProvider opp;
	private Sensor ultrasound;
	private boolean useSensorModel;
	
	public ScanBehavior(PilotRobot myRobot, PilotMonitor myMonitor, Grid grid, boolean useSensorModel) {
		this.myRobot = myRobot;
		this.myMonitor = myMonitor;
		this.grid = grid;
		this.ultrasound = new Sensor(myRobot, grid);
		
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
		
		this.useSensorModel = useSensorModel;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return myRobot.isScanRequired();
	}

	public final void action() {
		suppressed = false;
		if (!suppressed) {
			
			int chance = (int)(Math.random()*((10)+1));
			if (chance == 1) {
				File file=new File("soundEffects/turret_active.wav");
				Sound.playSample(file);
			} else 	if (chance == 2) {
				File file=new File("soundEffects/turret_search.wav");
				Sound.playSample(file);
			} else 	if (chance == 3) {
				File file=new File("soundEffects/turretstuckintube.wav");
				Sound.playSample(file);
			} else 	if (chance == 4) {
				File file=new File("soundEffects/finale_turret_defect_goodbye.wav");
				Sound.playSample(file);
			}
			
			
			OdometryPoseProvider opp = myRobot.getOdometryPoseProvider();
			double heading = opp.getPose().getHeading();
			Cell current = grid.getCurrentCell();
			Cell left, inFront, right;
			
			if (heading > -45 && heading <= 45) {
				//north
				left = grid.getCell(current.getX() - 1, current.getY());
				inFront = grid.getCell(current.getX(), current.getY() + 1);
				right = grid.getCell(current.getX() + 1, current.getY());
			} else if (heading > 45 && heading <= 135) {
				//east
				left = grid.getCell(current.getX(), current.getY() + 1);
				inFront = grid.getCell(current.getX() + 1, current.getY());
				right = grid.getCell(current.getX(), current.getY() - 1);
			} else if (heading > -135 && heading <= -45) {
				//west
				left = grid.getCell(current.getX(), current.getY() - 1);
				inFront = grid.getCell(current.getX() - 1, current.getY());
				right = grid.getCell(current.getX(), current.getY() + 1);
			} else {
				//south
				left = grid.getCell(current.getX() + 1, current.getY());
				inFront = grid.getCell(current.getX(), current.getY() - 1);
				right = grid.getCell(current.getX() - 1, current.getY());
			}
			
			if (left != null) {
				ultrasound.sensorRotateLeft();
				if (useSensorModel) {
					ultrasound.calculateCellsInSonarCone(heading - 90);
				} else {
					if (myRobot.getDistance() < 20) {
						left.setOccupancyProbability(1);
					} else {
						left.setOccupancyProbability(0);
					}
				}
			}
			
			if (right != null) {
				ultrasound.sensorRotateRight();
				if (useSensorModel) {
					ultrasound.calculateCellsInSonarCone(heading + 90);
				} else {
					if (myRobot.getDistance() < 25) {
						right.setOccupancyProbability(1);
					} else {
						right.setOccupancyProbability(0);
					}
				}
			}
			
			ultrasound.sensorRotateCentre(); // always return sensor to face in front
			if (inFront != null) {
				if (useSensorModel) {
					ultrasound.calculateCellsInSonarCone(heading);
				} else {
					if (myRobot.getDistance() < 25) {
						inFront.setOccupancyProbability(1);
					} else {
						inFront.setOccupancyProbability(0);
					}
				}
			}
			
			myRobot.setScanRequired(false);
		}
	}
}
