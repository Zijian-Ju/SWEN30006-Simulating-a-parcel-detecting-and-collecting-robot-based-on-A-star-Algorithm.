package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAutoController extends CarController{
	private boolean initiate = true;
	//sensor of the controller
	private CarMap carMap;
	//path finder find the path
	private INavigation navigation;
	//path follower follows the path
	private CarMover carMover;
	
	public MyAutoController(Car car) {
		super(car);
		this.navigation = new AStarNavigation();
		this.carMover = new CarMover(navigation, this);
		this.carMap = new CarMap(this);
	}
	
	/**
	 * @Override
	 * updates the controller to move the car in certain ways
	 */
	public void update() {
		//initiate the car to move
		if(initiate) {
			applyForwardAcceleration(); 
			initiate = false;
		}
		
		//update currentView to car map
		carMap.update(getView());
		
		//update the path follower
		carMover.update();
	}
	

	
	/**
	 * Get the current location of the car
	 * @return current coordinate
	 */
	public Coordinate getCurrentCoordinate() {
		Coordinate currentCoor = new Coordinate(getPosition());
		return currentCoor;
	}
	

	/**
	 * get the carMap
	 * @return carMap
	 */
	public CarMap getCarMap() {
		return carMap;
	}
}
