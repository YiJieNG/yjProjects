package game.ground;

import edu.monash.fit2099.engine.*;
import game.system.PlayerFinder;
import game.capabilities.FoodCapability;
import game.items.Fruit;
import game.actors.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creates tree object. Extends Ground.
 * @author Darren Yee
 * @version 1.0
 */
public class Tree extends Ground {
	/**
	 * age of the tree
	 */
	private int age = 0;
	/**
	 * arraylist of fruits to store the fruits
	 */
	private final ArrayList <Fruit> fruits = new ArrayList<Fruit>();

	/**
	 * Empty constructor for tree
	 */
	public Tree() {
		super('+');
	}


	/**
	 * Helps the tree to experience the turn of time. Tree has the ability to age and become a larger tree. It can also
	 * generate fruits that will be added into the tree's arraylist or onto the ground's list of items once it drops.
	 * @param location The location of the Ground
	 */
	@Override
	public void tick(Location location) {
		PlayerFinder playerFinder = new PlayerFinder();
		Player player = playerFinder.getPlayer();
		Fruit fruit = new Fruit("fruit", displayChar);
		super.tick(location);

		age++;
		if (age == 10)
			displayChar = 't';
		if (age == 20)
			displayChar = 'T';
		Random r = new Random();
		// Checks if it has fruits
		if (fruits.size() <= 0 && this.hasCapability(FoodCapability.HAS_FRUIT)){
			this.removeCapability(FoodCapability.HAS_FRUIT);
		}
		// Randomly generates food item and adds it to the array list of the tree
		int randomInt = r.nextInt(100) + 1;
		if (randomInt > 50) {
			this.addCapability(FoodCapability.HAS_FRUIT);
			fruits.add(fruit);
			player.addEcoPoint(1);
		}
		// If this tree has fruits, we make it remove it from the arraylist and add it onto the ground
		if (this.hasCapability(FoodCapability.HAS_FRUIT)){
			if (randomInt > 95) {
				this.removeCapability(FoodCapability.HAS_FRUIT);
				fruits.remove(fruit);
				Fruit dropped_fruit = new Fruit("fruit", displayChar, true,15);
				dropped_fruit.addCapability(FoodCapability.WILL_ROT);
				location.addItem(dropped_fruit);
			}
		}
	}

	/**
	 * Gets the arraylist of fruits in the specific tree
	 * @return
	 */
	public ArrayList<Fruit> getFruits() {
		return fruits;
	}

	/**
	 * Removes a fruit from the list
	 */

	public void removeFruit(){
		fruits.remove(0);
	}
}
