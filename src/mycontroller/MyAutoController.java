package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

/**
 * The controller of the car including a car map to store the views detected by the car, a carMover to move the car
 * @author Zijian Ju; Yuting Cai ; Xu Han
 *
 */
public class MyAutoController extends CarController{
	private final int CAR_MAX_SPEED = 1;
	//A car map to store the explored view
	private CarMap carMap;
	//A navigation to find the path
	private INavigation navigation;
	//The class to control the car's movement
	private CarMover carMover;
	
	/**
	 * Constructor of controller
	 * @param car
	 */
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
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		// checkStateChange();
		if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		//update currentView to car map
		carMap.update(currentView);
		//update the carMover to move the car
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
	 * Get the carMap
	 * @return carMap
	 */
	public CarMap getCarMap() {
		return carMap;
	}
	
}
