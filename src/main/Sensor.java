package main;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Sensor {
	
	Port sensorPort = LocalEV3.get().getPort("S2");
	EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(sensorPort);
	EV3MediumRegulatedMotor sensorMotor = new EV3MediumRegulatedMotor(sensorPort);
	int distance;
	
	
	public void enableSensor() {
		sensor.enable();
	}
	
	public void disableSensor() {
		sensor.disable();
	}
	
	
	//measures distance to an object (in metres) in front of the sensor.
	public int sensorDistance() {
		distance = sensor.getDistanceMode().sampleSize();
		return distance;
	}
	
	public void sensorRotateRight() {
		sensorMotor.rotateTo(90);
	}
	public void sensorRotateLeft() {
		sensorMotor.rotateTo(-90);
	}
	public void SensorRotateCentre() {
		sensorMotor.rotateTo(0);
	}
	
}
