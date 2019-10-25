package main;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;

public class Sensor {
	
	PilotRobot robot; 
	int distance;
	private final int coneAngle = 40;
	private int currentPosition;
	private Grid grid;
	private final  double cellSize = 25;
	private final double cellCenterDistance = cellSize/2;
	
	public Sensor(PilotRobot r, Grid g) {
	  this.robot = r;
	  this.grid = g;
	}
	
	public void enableSensor() {
	  robot.getUltrasonicSensor().enable();
	}
	
	public void disableSensor() {
	  robot.getUltrasonicSensor().disable();
	}
	
	
	//measures distance to an object (in metres) in front of the sensor.
	public float sensorDistance() {
		return robot.getDistance();
	}
	
	public void sensorRotateLeft() {
		Motor.C.rotateTo(95);
		currentPosition = (int) (robot.getAngle() + 90);
	}
	public void sensorRotateRight() {
		Motor.C.rotateTo(-95);
		currentPosition = (int) (robot.getAngle() - 90);
	}
	public void sensorRotateCentre() {
		Motor.C.rotateTo(0);
		currentPosition = (int) (robot.getAngle());
	}
	
	public double tan(int opposite, int adjacent) {
	  return Math.tan(((opposite * cellSize) - cellCenterDistance) / ((adjacent * cellSize) - cellCenterDistance)) * (180 / Math.PI);
	}
	
	public void calculateRegion1Probability(Cell cell, Double r, double a) {
	  //If this is the cells first sensor reading assume occupancy probability of 0.5.
	  if (cell.getOccupancyProbability() == -1) {
	    cell.setOccupancyProbability(0.5);
	  }
	  double probabilityRGivenOccupied = ((((250 - r)/250) + ((20 - a) / 20))/2) * 0.98;
	  double probabilityRGivenEmpty = 1 - probabilityRGivenOccupied;
	  
	  
	  //update with bayes rule
	  double probabilityOccupied = (probabilityRGivenOccupied * cell.getOccupancyProbability()) / 
	      ((probabilityRGivenOccupied * cell.getOccupancyProbability()) + (probabilityRGivenEmpty * (1 - cell.getOccupancyProbability())));
	  
	  cell.setOccupancyProbability(probabilityOccupied);
	}
	
	public void calculateRegion2Probability(Cell cell, Double r, double a) {
	   //If this is the cells first sensor reading assume occupancy probability of 0.5.
      if (cell.getOccupancyProbability() == -1) {
        cell.setOccupancyProbability(0.5);
      }
      double probabilityRGivenEmpty= ((((250 - r)/250) + ((20 - a) / 20))/2) * 0.98;
      double probabilityRGivenOccupied = 1 - probabilityRGivenEmpty;
      
      
      //update with Bayes rule
      double probabilityEmpty = (probabilityRGivenEmpty * (1 - cell.getOccupancyProbability())) / 
          ((probabilityRGivenEmpty * (1 - cell.getOccupancyProbability())) + (probabilityRGivenOccupied * (cell.getOccupancyProbability())));
      
      cell.setOccupancyProbability(1 - probabilityEmpty);
	}
	
	  public void calculateCellsInSonarCone(double sensorHeading) {
	    //Make sure sensor reading is above the min range.
	    double sensorReading = sensorDistance();
	    int cellsInReading = (int)((sensorReading/*-cellCenterDistance*/)/cellSize) +1;
	    if (!(sensorReading == 0)) {
	      //If sensor reading is infinity assume it is at max range the areana is at most 1.5m * 1.75m so this should never be the case.
	      if (sensorReading == Double.POSITIVE_INFINITY) {
	        sensorReading = 250;
	      }

//	      String heading = getSensorHeading(); //Direction sensor is pointing
	      
	      //a cell that is within the sonar cone.
	      //WARNING GROSS CODE.
	      /*For each heading type figure out what cells have a chance of being within the utrasound sensor cone a angle of 40 degrees is assumed.*/
	      Cell withinCone;
	      if (sensorHeading > -45 && sensorHeading <= 45 ) {
	        
	        //take the sensor reading and convert it to number of grid cells
	        /*Since we are face north we want to decrease the Y coordinate to go through every cell that lies within the sensor reading. */
	        for (int i = 0; i <= (cellsInReading); i++) {
	          
	          //check if cell is in +- 1cm boundray at sonar detection arc. if so calculate proability assuming its in region 1
	          if (i == cellsInReading)/*((int)((sensorReading-cellCenterDistance)/cellSize)-0.01) && i <= ((int)((sensorReading+cellCenterDistance)/cellSize)+0.01))*/ {
	            
	            //Check if cell is exists on the grid this prevents nullException errors when the wall is detected.
	            withinCone = grid.getCell(grid.getCurrentCell().getX(), grid.getCurrentCell().getY()+i);
	            if ( withinCone != null ) {
	              calculateRegion1Probability(withinCone,sensorReading,0);
	            }
	            
	            // for every cell within the sensor reading check if neighbour cells (left or right) are within the fieldOfView.
	            // Converts i to a distance value since cellSizem is lenght of a cell and cellCenterDistancem is distance to cell centroid.
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              //Check if right cell exists on the grid
	              withinCone = grid.getCell(grid.getCurrentCell().getX()+j, grid.getCurrentCell().getY()+i);
	              if (withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	              //Check if left cell exists on the grid
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-j, grid.getCurrentCell().getY()+i);
	              if (withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	            }
	            
	            //Otherwise calcuate occupation probability assuming its in region 2.
	          } else {
	            
	            //check if cell is within grid.
	            withinCone = grid.getCell(grid.getCurrentCell().getX(), grid.getCurrentCell().getY()+i);
	            if (withinCone != null) {
	              calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,0);
	            }
	            
	            //same as before check if neighbour cells are within the field of view.
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              
	              //check if right cell is within grid
	              withinCone = grid.getCell(grid.getCurrentCell().getX()+j, grid.getCurrentCell().getY()+i);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	              
	              //check if left cell is within grid
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-j, grid.getCurrentCell().getY()+i);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	            }
	          }
	        }
	        //Now repeat the above code with slight changes for the headings.
	       } else if (sensorHeading > 45 && sensorHeading <= 135) {
	       //take the sensor reading and convert to number of grid cells
	        for (int i = 0; i <= cellsInReading; i++) {
	          
	          //check if in +- 1cm boundray at sonar detection arc. if so calculate proability assuming its in region 1
	          if (i == cellsInReading)/*((int)((sensorReading+cellCenterDistance)/cellSize)-0.01) && i <= ((int)((sensorReading+cellCenterDistance)/cellSize)+0.01))*/ {
	            withinCone = grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY());
	            if (withinCone != null) {
	              calculateRegion1Probability(withinCone,sensorReading,0);
	            }
	            
	            //check if neighbour cells are within the fieldOfView.
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY()+j);
	              if (withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	              
	              withinCone =  grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY()-j);
	              if (withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	            }
	            //Otherwise calcuate occupation probability assuming its in region 2.
	          } else {
	            
	            withinCone = grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY());
	            if (withinCone != null) {
	              calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,0);
	            }
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY()+j);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()+i, grid.getCurrentCell().getY()-j);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	            }
	          }
	        }
	      } else if (sensorHeading > -135 && sensorHeading <= -45){
	        
	        for (int i = 0; i < cellsInReading; i++) {
	          //check if in +- 1cm boundray at sonar detection arc. if so calculate proability assuming its in region 1
	          if (i == cellsInReading)/*(i >= ((int)((sensorReading+cellCenterDistance)/cellSize)-0.01) && i <= ((int)((sensorReading+cellCenterDistance)/cellSize)+0.01))*/ {
	            
	            withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY());
	            if (withinCone != null) {
	              calculateRegion1Probability(withinCone,sensorReading,0);
	            }
	            
	            //check if neighbour cells are within the fieldOfView.
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY()-j);
	              if (withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY()+j);
	              if ( withinCone != null) {
	                calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
	              }
	            }
	            //Otherwise calcuate occupation probability assuming its in region 2.
	          } else {
	            
	            withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY());
	            if (withinCone != null) {
	              calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,0);
	            }
	            
	            double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
	            for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY()-j);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	              
	              withinCone = grid.getCell(grid.getCurrentCell().getX()-i, grid.getCurrentCell().getY()+j);
	              if (withinCone != null) {
	                calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
	              }
	            }
	          }
	        }
	      } else {
	          for (int i = 0; i <= cellsInReading; i++) {
              
              //check if in +- 1cm boundray at sonar detection arc. if so calculate proability assuming its in region 1
              if (i == cellsInReading)/*(i >= ((int)((sensorReading+cellCenterDistance)/cellSize)-0.01) && i <= ((int)((sensorReading+cellCenterDistance)/cellSize)+0.01))*/ {
                
                withinCone = grid.getCell(grid.getCurrentCell().getX(), grid.getCurrentCell().getY()-i);
                if (withinCone != null ) {
                  calculateRegion1Probability(withinCone,sensorReading,0);
                }
                
                //check if neighbour cells are within the fieldOfView.
                double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
                for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
                  
                  withinCone = grid.getCell(grid.getCurrentCell().getX()-j, grid.getCurrentCell().getY()-i);
                  if (withinCone != null) {
                    calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
                  }
                  
                  withinCone = grid.getCell(grid.getCurrentCell().getX()+j, grid.getCurrentCell().getY()-i);
                  if (withinCone != null) {
                    calculateRegion1Probability(withinCone,sensorReading,tan(j,i));
                  }
                }
                //Otherwise calcuate occupation probability assuming its in region 2.
              } else {
                withinCone = grid.getCell(grid.getCurrentCell().getX(), grid.getCurrentCell().getY()-i);
                if (withinCone != null) {
                  calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,0);
                }
               
                double fieldOfView = Math.atan(20*Math.PI/180)*((i*cellSize)/*-cellCenterDistance*/);
                for (int j = 1; j <= (int)((fieldOfView/*+cellCenterDistance*/)/cellSize); j ++) {
                  
                  withinCone = grid.getCell(grid.getCurrentCell().getX()-j, grid.getCurrentCell().getY()-i);
                  if (withinCone != null) {
                    calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
                  }
                  
                  withinCone = grid.getCell(grid.getCurrentCell().getX()+j, grid.getCurrentCell().getY()-i);
                  if ( withinCone != null) {
                    calculateRegion2Probability(withinCone,(i*cellSize)/*-cellCenterDistance*/,tan(j,i));
                  }
                }
              }
            }
	      }
	  } 
	 }
	  
}
