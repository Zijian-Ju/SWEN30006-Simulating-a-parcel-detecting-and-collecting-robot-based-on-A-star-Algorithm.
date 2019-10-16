package mycontroller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial.Direction;


/**
 * CarMap stores the map (each tile type) that is detected by the GPS
 *
 */
public class CarMap {
	private HashMap<Coordinate, MapTile> exploredMap;
	private ArrayList<Coordinate> unexploredMap;
	private MyAutoController controller;
	
	public CarMap(MyAutoController controller) {
		this.controller = controller;
		initializeExploredView();
		initalizeUnexploredView();
	}
	
	public HashMap<Coordinate, MapTile> getExploredMap(){
		return exploredMap;
	}
	
	public ArrayList<Coordinate> getUnexploredMap(){
		return unexploredMap;
	}
	
	/**
	 * initialize the explored view with provided map tiles
	 */
	private void initializeExploredView() {
		exploredMap = new HashMap<Coordinate, MapTile>();
		exploredMap.putAll(controller.getMap());
	}
	
	/**
	 * initialize the unexplored view contains all coordinates of the map
	 */
	private void initalizeUnexploredView() {
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
	 * @return the exit
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
	 * gets a list of trap locations based on trap type
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
	 * removes coordinate from the unexplored view
	 * @param coordinate
	 */
	public void removeUnexploredMap(Coordinate coor) {
		unexploredMap.remove(coor);
	}
	
	

}
