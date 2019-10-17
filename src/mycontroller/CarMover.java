package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * CarMover class to control the car's movement
 * @author Zijian Ju;Yuting Cai; Xu Han
 *
 */
public class CarMover {
	private MyAutoController controller;
	private IExploreStrategy strategy;
	
	private enum CAR_STATE{EXPLORE, PARCEL, EXIT};
	private CAR_STATE currentState;
	
	protected ArrayList<Coordinate> currentPath = null;
	private Coordinate nextCoor = null;
	private AStarNavigation navigation;
	private Coordinate position = null; 
	
	public CarMover(INavigation navigation, MyAutoController carController) {
		currentState = CAR_STATE.EXPLORE;
		this.navigation= (AStarNavigation) navigation;
		strategy = ExploreStrategyFactory.getInstance().getExploreStrategy();
		this.controller = carController;
	}
	
	/**
	 * Update the carMover
	 * @param myController
	 */
	public void update() {
		ArrayList<Coordinate> parcels = controller.getCarMap().getTrapLocations("parcel");
		ArrayList<Coordinate> exits = controller.getCarMap().getExitLocation();
		switch(currentState) {
		case EXPLORE:
			//go to exit if enough parcels are found
			if(controller.numParcels() == controller.numParcelsFound()) {
				if (getBestPath(exits) != null) {
				     currentState = CAR_STATE.EXIT;
				     currentPath = getBestPath(exits);
				}
				//Change state to collect parcel if possible
			}else if(getBestPath(parcels) != null){
					currentState = CAR_STATE.PARCEL;
				    currentPath = getBestPath(parcels);
			}else {
				currentPath = explore();
			}
			break;
		case PARCEL:
			if(getBestPath(parcels) == null) {
				currentState = CAR_STATE.EXPLORE;
				currentPath = explore();
			}
			break;
		default:
			break;
		}
		//moves the car following path
		followPath(currentPath);
	}
	
	/**
	 * Explore the map
	 * @return the path determined by AStarNavigation
	 */
	public ArrayList<Coordinate> explore(){
		Coordinate current = controller.getCurrentCoordinate();
		ArrayList<Coordinate> path = null;
		// check whether the position is explored
		if (!controller.getCarMap().getUnexploredMap().contains(position)) {
			position = strategy.getTargetPosition(controller.getCarMap().getUnexploredMap(), current);
		}
		if(position == null) {
			return null;
		}	
		path =navigation.getPath(controller.getCarMap().getExploredMap(), current, position);
	    if (path==null || path.isEmpty()) { 
			controller.getCarMap().removeUnexploredCoor(position);
			path = explore();
		}
		return path; 
	}
	
	
	/**
	 * Move the car following path
	 * @param path
	 */
	private void followPath(ArrayList<Coordinate> path) {	
		if(nextCoor!=null && !controller.getCurrentCoordinate().equals(nextCoor)) {
			move(controller, nextCoor);
		}else if (path.size()>1) {
			//The first element in array in current location, the second one is the target position.
			nextCoor = path.get(1);
			move(controller, nextCoor);
			path.remove(1);
		}
	}
	
	/**
	 * Move the car from 1 tile to one of its neighbor (one step move)
	 * @param controller
	 * @param target
	 */
	public void move(MyAutoController controller, Coordinate target) {
		Coordinate current = controller.getCurrentCoordinate();
		switch(controller.getOrientation()) {
		case NORTH:
			if(current.y - target.y < 0)
				controller.applyForwardAcceleration();
			else if(current.y - target.y > 0)
				controller.applyReverseAcceleration();
			else if(current.x - target.x < 0)
				controller.turnRight();
			else if (current.x - target.x > 0)
				controller.turnLeft();
			break;
		case SOUTH:
			if(current.y - target.y < 0)
				controller.applyReverseAcceleration();
			else if(current.y - target.y > 0)
				controller.applyForwardAcceleration();
			else if(current.x - target.x < 0)
				controller.turnLeft();
			else if(current.x - target.x > 0)
				controller.turnRight();
			break;
		case EAST:	
			if(current.y - target.y < 0)
				controller.turnLeft();
			else if(current.y - target.y > 0)
				controller.turnRight();
			else if(current.x - target.x < 0)
				controller.applyForwardAcceleration();
			else if(current.x - target.x > 0)
				controller.applyReverseAcceleration();
			break;
		case WEST:
			if(current.y - target.y < 0)
				controller.turnRight();
			else if(current.y - target.y > 0)
				controller.turnLeft();
			else if(current.x - target.x < 0)
				controller.applyReverseAcceleration();
			else if(current.x - target.x > 0)
				controller.applyForwardAcceleration();
			break;
		}
	}	
	
	/**
	 * Get the best path based on a list of target coordinates
	 * @param targets
	 * @return The best path presented by a list of coordinates
	 */
	private ArrayList<Coordinate> getBestPath(ArrayList<Coordinate> targets){
		double min = Double.MAX_VALUE;
		ArrayList<ArrayList<Coordinate>> paths = new ArrayList<>();
		ArrayList<Coordinate> currentPath = null;
		ArrayList<Coordinate> bestPath = null;
		
		// find all of the possible paths
		for(Coordinate target: targets) {
			currentPath = navigation.getPath(controller.getCarMap().getExploredMap(), controller.getCurrentCoordinate(), target);
			if(currentPath != null)
				paths.add(currentPath);
		}

        // Select the path consuming the least fuel, if the the paths contains a wall, the fuel cost will be the Double.MAX_VALUE, which will not be returned
		if(!paths.isEmpty())
			for(ArrayList<Coordinate> path: paths) {
				double fuelCost = path.size();
				if(fuelCost < min) {
					min = fuelCost;
					bestPath = path;
				}
			}
		return bestPath;
	}
}
