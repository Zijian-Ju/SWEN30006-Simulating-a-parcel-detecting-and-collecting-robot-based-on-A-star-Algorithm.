package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;

public class CarMover {
	protected MyAutoController controller;
	private IExploreStrategy strategy;
	//two states: explore and goto
	public enum state{EXPLORE, GOTO};
	//two type of tiles the car wants to go directly
	public enum goal{EXIT, PARCEL};
	protected state currentState;
	protected goal currentGoal = null;
	protected ArrayList<Coordinate> currentPath = null;
	private Coordinate last = null;
	protected AStarNavigation navigation;
	private Coordinate position = null; 

	
	public CarMover(INavigation navigation, MyAutoController carController) {
		currentState = state.EXPLORE;
		this.navigation= (AStarNavigation) navigation;
		strategy = new NearestFirstStrategy ();
		this.controller = carController;
	}
	
	/**
	 * updates the path follower
	 * @param myController
	 */
	public void update() {
		switch(currentState) {
		//explores the map 
		case EXPLORE:
			//boolean: whether to change the state
			boolean state = false;	
			//go to exit if enough parcels are found
			if(controller.numParcels() == controller.numParcelsFound()) {
				state = isExit();
			}
			//go to parcel if a parcel is found
			else if(!controller.getCarMap().getTrapLocations("parcel").isEmpty()){
				state = isParcel();
			}
			if(!state) {
				currentPath = explore();
			}
			break;
		//after a GOTO state, change the state if necessary.
		case GOTO:
			changeArrivedState();
			break;
		default:
			break;
		}
		//moves the car according to a path
		follow(currentPath);
	}
	

	/**
	 * explores the map
	 * @return
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
		if (controller.getCarMap().getExploredMap().get(position).isType(Type.WALL)==false) {
			path =navigation.getPath(controller.getCarMap().getExploredMap(), current, position);
			if (path==null || path.isEmpty()) { 
				controller.getCarMap().removeUnexploredMap(position);
				path = explore();
			}
		}
		else {
			controller.getCarMap().removeUnexploredMap(position);
			path = explore();
		}
		return path; 
	}
	
	
	/**
	 * whether to get the parcel
	 * @return boolean
	 */
	protected boolean isParcel() {
		ArrayList<Coordinate> parcels = controller.getCarMap().getTrapLocations("parcel");
		ArrayList<Coordinate> path = getBestPath(parcels);
		if(path != null) {
			currentState = state.GOTO;
			currentGoal = goal.PARCEL;
			currentPath = path;
			return true;
		}
		
		return false;
	}
	
	/**
	 * whether to get the exit
	 * @return boolean
	 */
	protected boolean isExit() {
		ArrayList<Coordinate> exits = controller.getCarMap().getExitLocation();
		ArrayList<Coordinate> path = getBestPath(exits);
		if(path != null) {
			currentState = state.GOTO;
			currentGoal = goal.EXIT;
			currentPath = path;
			return true;
		}
		return false;
	}
		

	/**
	 * change the state back to explore
	 */
	protected void changeArrivedState() {
		if(currentPath.size() == 2) {
			if (currentGoal == goal.PARCEL) {
				currentState = state.EXPLORE;
				currentGoal = null;
			}
		}
	}
	

	/**
	 * moves the car according to a path
	 * @param path
	 */
	protected void follow(ArrayList<Coordinate> path) {	

		int next = 1;
		if(last!=null && !controller.getCurrentCoordinate().equals(last)) {
			move(controller, last);

		}else if (path!=null && path.size()>1) {	
			last = path.get(next);
			move(controller, last);
			path.remove(next);
		}
	}
	
	/**
	 * moves the car from 1 tile to another tile (one step move)
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
	 * gets the best path based on a list of target coordinates
	 * @param targets
	 * @return
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
		// select the least health consuming path
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
