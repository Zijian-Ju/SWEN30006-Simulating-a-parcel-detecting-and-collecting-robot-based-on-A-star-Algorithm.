package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public abstract class INavigation {
	public abstract ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> map, 
            Coordinate current, Coordinate target);
}
