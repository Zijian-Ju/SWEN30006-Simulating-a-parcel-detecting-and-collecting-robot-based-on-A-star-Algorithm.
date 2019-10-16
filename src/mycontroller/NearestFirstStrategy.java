package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class NearestFirstStrategy implements IExploreStrategy{

	@Override
	public Coordinate getTargetPosition(ArrayList<Coordinate> unexploredMap, Coordinate current) {
		int nearestDist = Integer.MAX_VALUE;
		int distance;
		Coordinate target = null;
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
