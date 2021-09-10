package game.ground;

import edu.monash.fit2099.engine.*;

import java.util.Random;

/**
 * A class that represents bare dirt.
 * @author Darren Yee
 * @version 1.0
 */
public class Dirt extends Ground {
	/**
	 * left variable which store the left side of the ground
	 */
	private Ground left;
	/**
	 * right variable which store the right side of the ground
	 */
	private Ground right;

	/**
	 * Constructor
	 */
	public Dirt() {
		super('.');
	}

	/**
	 * Method which handles the current Dirt on the map for each and every round
	 * @param location The location of the Ground
	 */
	@Override
	public void tick(Location location) {
		GameMap gameMap = location.map();
		// Checks the type of ground the left and the right of the location is
		if (location.x() != 0 && location.x() != 79) {
			left = gameMap.at(location.x() - 1, location.y()).getGround();
			right = gameMap.at(location.x() + 1, location.y()).getGround();
			// If they are not around Trees
			if(!(left instanceof Tree) && !(right instanceof Tree)){
				// If they are surrounded by bushes
				if (left instanceof Bush && right instanceof Bush) {
					Bush bush = new Bush();
					super.tick(location);
					Random r = new Random();
					int randomInt = r.nextInt(100) + 1;
					if (randomInt > 90) {
						location.setGround(bush);
					}
				}
				// else they only have 1% chance of spawning bush
				else{
					Bush bush = new Bush();
					super.tick(location);
					Random d = new Random();
					int randomInt = d.nextInt(100) + 1;
					if (randomInt > 99) {
						location.setGround(bush);
					}
				}
			}
		}
		// If is at the corner, only check right
		else if (location.x() == 0) {
			if(!(right instanceof Tree)) {
				Bush bush = new Bush();
				super.tick(location);
				Random d = new Random();
				int randomInt = d.nextInt(100) + 1;
				if (randomInt > 99) {
					location.setGround(bush);
				}
			}
		}
		// If it is at the corner, only check left
		else if (location.x() == 79){
			if (!(left instanceof Tree)) {
				Bush bush = new Bush();
				super.tick(location);
				Random d = new Random();
				int randomInt = d.nextInt(100) + 1;
				if (randomInt > 99) {
					location.setGround(bush);
				}
			}
		}
	}
}
