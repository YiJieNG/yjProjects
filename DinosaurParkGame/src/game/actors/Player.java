package game.actors;

import edu.monash.fit2099.engine.*;
import game.system.Rain;
import game.actions.FindFruitAction;
import game.actions.QuitGameAction;
import game.actions.VisitAction;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.actions.FeedingAction;
import game.ground.Bush;
import game.ground.Lake;
import game.ground.Tree;
import game.ground.VendingMachine;

import java.util.ArrayList;

/**
 * Class representing the Player.
 */
public class Player extends Actor {
	/**
	 * eco point of player
	 */
	private int EcoPoint=0;

	/**
	 * menu of the game
	 */
	private final Menu menu = new Menu();
	/**
	 * ArrayList to store valid dinosaur
	 */
	private Actor actor;
	/**
	 * Rain object
	 */

	private final Rain rain;
	/**
	 * Display object to display messages
	 */

	private final Display display = new Display();
	/**
	 * ArrayList of GameMaps that contains the player.
	 */

	protected ArrayList<GameMap> gameMaps = new ArrayList<GameMap>();
	/**
	 * Amount of rounds for challenge mode
	 */

	private int round;
	/**
	 * Round limit for the challenge mode
	 */

	private Double limitRound;
	/**
	 * TargetPoint for challenge mode
	 */

	private int targetPoints;

	/**
	 * Constructor.
	 *
	 * @param name        Name to call the player in the UI
	 * @param displayChar Character to represent the player in the UI
	 * @param hitPoints   Player's starting number of hitpoints
	 */
	public Player(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);
		rain = new Rain();
		round = 0;
	}


	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		display.println("Player current ecoPoints:" + EcoPoint);
		if(round >= limitRound){
			boolean win = false;
			if (getEcoPoint()>=targetPoints){
				win = true;
			}
			int intRound = (int) Math.round(limitRound);
			String message;
			if (win){
				message = "Congratulation! You have earned "+getEcoPoint()+" points which is higher than your target "+targetPoints+" points within "+intRound+" rounds!";
			}else{
				message = "You have lost the game since you only earned "+getEcoPoint()+" points which is lower than your target "+targetPoints+" points within "+intRound+ " rounds.";
			}
			return new QuitGameAction(message);
		}else {
			round++;
			raining();
			actions.add(quitGame());
			if (checkPosition(map) != null) {
				actions.add(checkPosition(map));
			}
			// Handle multi-turn Actions
			// Locate the four directions(North, East, South, West) to see whether there is a dinosaur or not
			int x = map.locationOf(this).x();
			int y = map.locationOf(this).y();
			// If the target is valid, store the dinosaur into an array for further implementation
			if (x + 1 < 80) {
				addActors(x + 1, y, map);
				if (actor != null) {
					FeedingAction feedingAction = new FeedingAction(actor, "East");
					actions.add(feedingAction);
				}
			}
			if (x - 1 > 0) {
				addActors(x - 1, y, map);
				if (actor != null) {
					FeedingAction feedingAction = new FeedingAction(actor, "West");
					actions.add(feedingAction);
				}
			}
			if (y - 1 > 0) {
				addActors(x, y - 1, map);
				if (actor != null) {
					FeedingAction feedingAction = new FeedingAction(actor, "North");
					actions.add(feedingAction);
				}
			}
			if (y + 1 < 24) {
				addActors(x, y + 1, map);
				if (actor != null) {
					FeedingAction feedingAction = new FeedingAction(actor, "South");
					actions.add(feedingAction);
				}
			}
			// add eco points of 10 to player since player fed a dinosaur
			if (this.hasCapability(CanBreedCapability.CANADD)) {
				addEcoPoint(10);
			}
			// if Player stepped on a vending machine, it can be visited by Player
			if (map.locationOf(this).getGround() instanceof VendingMachine) {
				VisitAction visitAction = new VisitAction();
				actions.add(visitAction);
			}
			// if Player stepped on a tree or a bush, player can search fruit from the bush or tree
			if (map.locationOf(this).getGround() instanceof Tree || map.locationOf(this).getGround() instanceof Bush) {
				FindFruitAction findFruitAction = new FindFruitAction();
				actions.add(findFruitAction);
			}

			// do the actions called before
			if (lastAction.getNextAction() != null)
				return lastAction.getNextAction();
			return menu.showMenu(this, actions, display);
		}
	}

	/**
	 * Method to store the valid dinosaurs to the array list
	 * @param x int location x
	 * @param y int location y
	 * @param map gameMap map
	 */
	public void addActors(int x, int y, GameMap map){
		// if there is an dinosaur at the input location
		if (map.getActorAt(map.at(x, y)) != null ) {
			if ((map.getActorAt(map.at(x, y))).hasCapability(FoodCapability.CAN_FEED)) {
				actor = (map.getActorAt(map.at(x, y)));
			}
			else{
				actor = null;
			}
		}
		else{
			actor = null;
		}
	}
	/**
	 * Getter of ecoPoints
	 * @return int ecoPoints
	 */
	public int getEcoPoint() {
		return this.EcoPoint;
	}

	/**
	 * Method to add the ecoPoints
	 * @param ecoPoint int points to add
	 */
	public void addEcoPoint(int ecoPoint) {
		this.EcoPoint += ecoPoint;
	}

	/**
	 * Method to minus the ecoPoints
	 * @param ecoPoint int points to minus
	 */
	public void minusEcoPoint (int ecoPoint){
		this.EcoPoint -= ecoPoint;
	}

	public void raining(){
		if (rain.isRaining()) {
			display.println("Day " + round + " rainy day!");
			for (GameMap map : getGameMaps()) {
				for (int i : map.getXRange()) {
					for (int j : map.getYRange()) {
						if (map.at(i, j).getGround() instanceof Lake) {
							Lake lake = (Lake) map.at(i, j).getGround();
							lake.rain();
						}
						if (map.at(i, j).getActor() != null && map.at(i, j).getActor().hasCapability(FoodCapability.THIRSTY)) {
							if (map.at(i, j).getActor().hasCapability(FoodCapability.STEG)) {
								Stegosaur stegosaur = (Stegosaur) map.at(i, j).getActor();
								if (!(stegosaur.waterIsConscious())){
									stegosaur.addWaterLevel(10);
									display.println("Water level of Stegosaur at (" + i + ", " + j + ") was added by 10 due to the rain");
								}
							} else if (map.at(i, j).getActor().hasCapability(FoodCapability.BRAC)) {
								Brachiosaur brachiosaur = (Brachiosaur) map.at(i, j).getActor();
								if (!(brachiosaur.waterIsConscious())) {
									brachiosaur.addWaterLevel(10);
									display.println("Water level of Brachiosaur at (" + i + ", " + j + ") was added by 10 due to the rain");
								}
							} else if (map.at(i, j).getActor().hasCapability(FoodCapability.ALLO)) {
								Allosaur allosaur = (Allosaur) map.at(i, j).getActor();
								if (!allosaur.waterIsConscious()) {
									allosaur.addWaterLevel(10);
									display.println("Water level of Allosaur at (" + i + ", " + j + ") was added by 10 due to the rain");
								}
							} else if (map.at(i, j).getActor().hasCapability(FoodCapability.PTERO)) {
								Pterodactyl pterodactyl = (Pterodactyl) map.at(i, j).getActor();
								if (!(pterodactyl.waterIsConscious())) {
									pterodactyl.addWaterLevel(10);
									display.println("Water level of Pterodactyl at (" + i + ", " + j + ") was added by 10 due to the rain");
								}
							}
						}
					}
				}
			}
		}
		else{
			display.println("Day "+round+" sunny day!");
		}
	}

	/**
	 * Returns the list of maps for the player
	 * @return ArrayList<GameMap>
	 */
	public ArrayList<GameMap> getGameMaps() {
		return gameMaps;
	}

	/**
	 * Adds maps into the player's map array list in order to be used when iterating
	 * @param gameMap GameMap gameMap
	 */

	public void addGameMaps(GameMap gameMap) {
		gameMaps.add(gameMap);
	}

	/**
	 * Checks the current position of the player to see whether it is allowable for the actor
	 * @param map GameMap
	 * @return Action action
	 */

	public Action checkPosition(GameMap map){
		int y = map.locationOf(this).y();
		int x = map.locationOf(this).x();
		if (y==0 && map == this.getGameMaps().get(0)){
			return (new MoveActorAction(this.getGameMaps().get(1).at(x, 24), "to new map!"));
		}
		else if(y==24 && map == this.getGameMaps().get(1)){
			return (new MoveActorAction(this.getGameMaps().get(0).at(x, 0), "to old map!"));
		}
		return null;
	}

	/**
	 * Calls quit game action when the player meets the condition of quiting the game
	 * @return Action quit game action
	 */

	public Action quitGame(){
		return new QuitGameAction("You have quit the game TT");
	}

	/**
	 * Sets the amount of rounds that the player gets before the game ends. Used for challenge mode
	 * @param limitRound Double limit round that player wish to have
	 */

	public void setLimitRound(Double limitRound) {
		this.limitRound = limitRound;
	}

	/**
	 * Sets the amount of EcoPoints that the player is supposed to get before the game ends. Used for challenge mode
	 * @param targetPoints int targetPoints that player wish to set
	 */

	public void setTargetPoints(int targetPoints) {
		this.targetPoints = targetPoints;
	}
}