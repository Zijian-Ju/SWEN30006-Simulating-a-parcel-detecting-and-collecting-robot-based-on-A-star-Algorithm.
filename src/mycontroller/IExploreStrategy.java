package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public interface IExploreStrategy {
	
	/**
	 * Get target position among unexplored map
	 */
	public Coordinate getTargetPosition(ArrayList<Coordinate> unexplored, Coordinate current);

}
