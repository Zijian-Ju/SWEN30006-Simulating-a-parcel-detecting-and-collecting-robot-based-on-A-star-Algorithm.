package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

/**
 * The interface for the explore strategy
 * @author Zijian Ju; Yuting Cai; Xu Han
 */
public interface IExploreStrategy {
	
	/**
	 * Get target position among unexplored map
	 */
	public Coordinate getTargetPosition(ArrayList<Coordinate> unexplored, Coordinate current);

}
