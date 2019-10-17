package mycontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

/**
 * A concrete Navigation applying AStar Algorighm.
 * @author Zijian Ju, Yuting Cai, Xu Han
 */
public class AStarNavigation extends INavigation {
	private static final double WALL = Double.MAX_VALUE;
	private static final int NETGHBOR_DIS = 1;
	
	/**
	 * Calculate the path between start point and target by A* Algorithm
	 *@return the path by A* Algorithm
	 */
	@Override
	public ArrayList<Coordinate> getPath(HashMap<Coordinate, MapTile> exploredMap, Coordinate start, Coordinate target) {
		ArrayList<Coordinate> open = new ArrayList<Coordinate>();
		ArrayList<Coordinate> close = new ArrayList<Coordinate>();
		//The previous path in a step by step HashMap,
		HashMap<Coordinate, Coordinate> pathMap = new HashMap<Coordinate, Coordinate>();
		HashMap<Coordinate, Double> gCostMap = new HashMap<>();
		HashMap<Coordinate, Double> hCostMap = new HashMap<>();
		
		gCostMap.put(start, 0.0);
		hCostMap.put(start, getDistance(start, target));
		open.add(start);
		
		while(!(open.size()==0)) {
			Coordinate currentCoor = getMinFTile(gCostMap, hCostMap, open);
			if (currentCoor == null) {
				return null;
			}
        	if (currentCoor.equals(target)) {
                return buildPath(pathMap, currentCoor);  
            }
        	HashMap<Coordinate, String> neighbours = getNeighbours(exploredMap, currentCoor);
        	close.add(currentCoor);
        	open.remove(currentCoor);
        	for (Entry<Coordinate, String> entry : neighbours.entrySet()) {
        		Coordinate neighbour = entry.getKey();
        		String tileType = entry.getValue();
                if (close.contains(neighbour)) {
                    continue;	
                }
                double neighbourGCost = gCostMap.get(currentCoor) + getDistance(currentCoor, neighbour);
				if (!open.contains(neighbour)) {
					open.add(neighbour);
				}
				else if (neighbourGCost > gCostMap.get(neighbour)) {
					continue;
				}
				pathMap.put(neighbour, currentCoor);
				markWall(tileType, neighbourGCost,  gCostMap, neighbour);
				hCostMap.put(neighbour, getDistance(neighbour, target)); 
        	}		
		}
        return new ArrayList<>(); 
	}
	
	/**
	 * If current location is the target position, build path from path map
	 * @param pathMap
	 * @param currentPos
	 * @return
	 */
	private ArrayList<Coordinate> buildPath(HashMap<Coordinate, Coordinate> pathMap, Coordinate currentPos) {
        ArrayList<Coordinate> path = new ArrayList<>();
        path.add(currentPos);
        while (pathMap.containsKey(currentPos)) {
            currentPos = pathMap.get(currentPos);
            path.add(currentPos);
        }
        Collections.reverse(path);
        return path;
    }
	
	/**
	 * Get the neighbor with the minimum F cost (Gcost+Hcost)
	 * @param map1
	 * @param map2
	 * @param list
	 * @return
	 */
	private Coordinate getMinFTile(HashMap<Coordinate, Double> map1, HashMap<Coordinate, Double> map2, ArrayList<Coordinate> nodes) {
		double min = Double.MAX_VALUE;
		Coordinate shortestCoor = null;
		for(int i=0; i<nodes.size(); i++) {
			double current = map1.get(nodes.get(i)) + map2.get(nodes.get(i));
		    if (min > current) {
		    	min = current;
		    	shortestCoor = nodes.get(i);
		    }
		}
		return shortestCoor;
	} 
	
	/**
	 * Mark walls on map
	 * @param tileType
	 * @param val
	 * @param exploredView
	 * @param coor
	 */
	public void markWall(String tileType, double val, HashMap<Coordinate, Double> exploredView, Coordinate coor) {
		if (tileType.equals("WALL")) {
			exploredView.put(coor, WALL);
		} else {
			exploredView.put(coor, val);
		}
	}
	
	/**
	 * Get  the type of  the trap tile with a given coordinate
	 * @param map
	 * @param coor
	 * @return The type of the trap 
	 */
	private String getTrapType(HashMap<Coordinate, MapTile> carMap, Coordinate coor) {
		MapTile tile = carMap.get(coor);
		//If the tile in not in the car's explored view, regard it is wall
		if (tile == null) {
			return "WALL";
		}else {
			if (tile.isType(Type.TRAP)) {
				TrapTile trapTile = (TrapTile) tile;
	        	return trapTile.getTrap().toString(); 
			}else {
				if(tile.isType(Type.EMPTY))
					return "WALL"; 
				return tile.getType().toString(); 
	        }
		}
	}
	
	/**
	 * Get the neighbors of the current coordinate
	 * @param map
	 * @param current coordinate
	 * @return neighbors of the current tile
	 */
	private HashMap<Coordinate, String> getNeighbours(HashMap<Coordinate, MapTile> map, Coordinate current){
		HashMap<Coordinate, String> neighbours = new HashMap<Coordinate, String>();
		Coordinate up= new Coordinate(current.x, current.y+NETGHBOR_DIS);
		neighbours.put(up,getTrapType(map,up));
		Coordinate down = new Coordinate(current.x, current.y-NETGHBOR_DIS);
		neighbours.put(down,getTrapType(map,down));
		Coordinate left = new Coordinate(current.x-NETGHBOR_DIS, current.y);
		neighbours.put(left,getTrapType(map,left));
		Coordinate right = new Coordinate(current.x+NETGHBOR_DIS, current.y);
		neighbours.put(right,getTrapType(map,right));
		return neighbours;
	}
	
	/**
	 * Get the direct cost between two  tiles without considering the wall
	 * @param current
	 * @param target
	 * @return the distance between two 
	 */
	private double getDistance(Coordinate start, Coordinate target) {
		double dx = target.x - start.x;
		double dy = target.y - start.y;
		double distance = Math.abs(dx) + Math.abs(dy);
		return distance;
	}

}
