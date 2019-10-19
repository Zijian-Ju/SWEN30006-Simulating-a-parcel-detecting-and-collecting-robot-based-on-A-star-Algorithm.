package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

/**
 * The concrete class of explore strategy based on nearest first principle;
 * @author Zijian Ju; Yuting Cai; Jacob Han
 *
 */
public class NearestFirstStrategy implements IExploreStrategy{


	/**
	 * Get the target coordinate from unexplored map based on current locaiton
	 * @param unexploredMap
	 * @param current The current locaiton of the car
	 * @return the target coordinate from unexplored map
	 */
	public Coordinate getTargetPosition(ArrayList<Coordinate> unexploredMap, Coordinate current) {
		int nearestDist = Integer.MAX_VALUE;
		int distance;
		Coordinate target = null;
		//Retrieve all the coordinate of the unexplored map and get the nearest position as the target position
		for(Coordinate pos: unexploredMap) {
			distance = Math.abs(pos.x - current.x) + Math.abs(pos.y - current.y);
			if(distance < nearestDist) {
				target = pos;
				nearestDist = distance;
			}
		}
		return target;
	}
}
