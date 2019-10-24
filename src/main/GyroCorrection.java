import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.LocalEV3;
import lejos.robotics.navigation.MovePilot;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

import lejos.robotics.SampleProvider;

public class GyroCorrection {

 	Brick myEV3 = BrickFinder.getDefault();
	Port gyroPort = LocalEV3.get().getPort("S3");
	EV3GyroSensor gyro = new EV3GyroSensor(gyroPort);
	Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.05).offset(-4.9);
	Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.05).offset(4.9);
	
	Chassis myChassis = new WheeledChassis( new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
	MovePilot pilot = new MovePilot(myChassis);
	
  	static int direction[] = {0,90,180,270};
  	int angle;
  	SampleProvider gyroSP;
  	private float[] angleSample;
  	
  	float getGyroAngle() {
  		gyroSP = gyro.getAngleMode();
  		angleSample = new float[gyroSP.sampleSize()];
  		gyroSP.fetchSample(angleSample, 0);
  		return angleSample[0];
  		}
 
  	//returns the angle of the gyro in whole numbers.
  	public int getGyroInt() {
  		int angle = Math.round(getGyroAngle());
  		return angle;
  	}
  	
  	int findClosest(int arr[], int target) {
  		int n = arr.length; 
  		int i = 0, j = n, mid = 0; 
        while (i < j) { 
            mid = (i + j) / 2; 
  
            if (arr[mid] == target) 
                return arr[mid]; 
  
            /* If target is less than array element, 
               then search in left */
            if (target < arr[mid]) { 
         
                // If target is greater than previous 
                // to mid, return closest of two 
                if (mid > 0 && target > arr[mid - 1])  
                    return getClosest(arr[mid - 1], arr[mid], target); 
                  
                /* Repeat for left half */
                j = mid;               
            } 
  
            // If target is greater than mid 
            else { 
                if (mid < n-1 && target < arr[mid + 1])  
                    return getClosest(arr[mid],  
                          arr[mid + 1], target);                 
                i = mid + 1; // update i 
            } 
        } 
  
        // The only element left after the search 
        return arr[mid]; 
    }
  	
  	int getClosest(int val1, int val2, int target) { 
		if (target - val1 >= val2 - target)  
			return val2;         
		
		else 
			return val1;         
		}
  	
  	//checks if the robot is pointing towards a valid direction
  	boolean arrayCompare(int arr[], int target){
  		for (int i = 0 ; i < arr.length; i++) {

        if (arr[i] ==  target){
				return true;
			}

      }
      return false;
  	}
  	
  	//THE MAIN METHOD TO USE
  	//rotates the robot to the nearest proper direction
  	//the robot won't be able to move once it's corrected itself, so make sure to break the function when it's completed
  	void gyroCorrection() {
  		int difference = findClosest(direction,getGyroInt()) - getGyroInt();
  		if (arrayCompare(direction, getGyroInt()) == false) {
  			pilot.setAngularAcceleration(25);
  			pilot.setAngularSpeed(50);
        	pilot.rotate(difference);
        	}
  		
  		}
  	
  }