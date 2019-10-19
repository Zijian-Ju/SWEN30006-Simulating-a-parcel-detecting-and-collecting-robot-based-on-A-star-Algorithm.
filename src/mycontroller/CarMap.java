package mycontroller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;
import world.WorldSpatial.Direction;


/**
 * CarMap stores the map (each tile type) that is detected by the GPS
 *@author Zijian Ju; Yuting Cai; Jacob Han
 */
public class CarMap {
	private HashMap<Coordinate, MapTile> exploredMap;
	private ArrayList<Coordinate> unexploredMap = new ArrayList<>();
	private MyAutoController controller;
	
	/**
	 * Constructor
	 * @param controller
	 */
	public CarMap(MyAutoController controller) {
		this.controller = controller;
		initalizeUnexploredMap();
		initializeExploredMap();
	}
	
	/**
	 * Get explored map
	 * @return the map that car has detected
	 */
	public HashMap<Coordinate, MapTile> getExploredMap(){
		return exploredMap;
	}
	
	/**
	 * Get unexplored map
	 * @return the map that car has not detected
	 */
	public ArrayList<Coordinate> getUnexploredMap(){
		return unexploredMap;
	}
	
	/**
	 * Initialize the explored view with provided map tiles
	 */
	private void initializeExploredMap() {
		exploredMap = new HashMap<Coordinate, MapTile>();
		exploredMap.putAll(controller.getMap());
	}
	
	/**
	 * Initialize the unexplored map base on the world map
	 */
	private void initalizeUnexploredMap() {
		for(int xPosition = 0 ; xPosition < World.MAP_WIDTH; xPosition++) {
			for(int yPosition = 0 ; yPosition < World.MAP_HEIGHT ; yPosition++) {
				unexploredMap.add(new Coordinate(xPosition, yPosition));
			}
		}
	}
	/**
	 * Update current view to map
	 * @param currentview
	 */
	public void update(HashMap<Coordinate, MapTile> currentview) {
		//Update explored map
		exploredMap.putAll(currentview);
		// Update unexplored map
		for(Coordinate coor: currentview.keySet()) {
			if(coor.x>=0 && coor.y>=0) {
				if (coor.x < World.MAP_WIDTH && coor.y < World.MAP_HEIGHT) {
					unexploredMap.remove(coor);
				}
			}	
		}
	}
	
	/**
	 * Get the exit tile from the explored view
	 * @return the coordinates of the exit
	 */
	public ArrayList<Coordinate> getExitLocation(){
		ArrayList<Coordinate> exit = new ArrayList<Coordinate>();
		for (Entry<Coordinate, MapTile> tile : exploredMap.entrySet()) {
			if(tile.getValue().isType(Type.FINISH)) {
				exit.add(tile.getKey());
			}
		}
		return exit;
	}
	
	/**
	 * Get a list of trap coordinates based on trap type
	 * @param trapType
	 * @return a list of traps' coordinates
	 */
	public ArrayList<Coordinate> getTrapLocations(String trapType){
		ArrayList<Coordinate> traps = new ArrayList<Coordinate>();
		for (Entry<Coordinate, MapTile> trap : exploredMap.entrySet()) {
			if(trap.getValue().isType(Type.TRAP)) {
				TrapTile temp = (TrapTile)trap.getValue();
				if(temp.getTrap().equals(trapType)) {
					traps.add(trap.getKey());
				}
			}
		}
		return traps;
	}
	
	/**
	 * Removes coordinate from the unexplored map
	 * @param coordinate
	 */
	public void removeUnexploredCoor(Coordinate coor) {
		unexploredMap.remove(coor);
	}
}
