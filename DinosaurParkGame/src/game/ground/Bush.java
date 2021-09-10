package game.ground;

import edu.monash.fit2099.engine.*;
import game.capabilities.FoodCapability;
import game.items.Fruit;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class to create bush object. Extends ground.
 * @author Darren Yee, Ng Yi Jie
 * @see edu.monash.fit2099.engine.Ground
 * @see game.ground.Dirt
 */

public class Bush extends Ground {
    /**
     * An arraylist helps to store all the fruits which means that the fruits are currently on the tree
     */
    private final ArrayList<Fruit> fruits = new ArrayList<Fruit>();

    /**
     * Constructor for bush object, with the display char of w
     */
    public Bush() {
        super('w');
    }

    /**
     * Tick method for bush to experience the passage of time. Has a chance (10%) to spawn fruit object and add it to
     * bush's fruit arraylist.
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        Fruit fruit = new Fruit("Fruit", 'f');
        super.tick(location);
        // checks if fruits is empty
        if (fruits.size() <= 0 && this.hasCapability(FoodCapability.HAS_FRUIT)){
            this.removeCapability(FoodCapability.HAS_FRUIT);
        }
        // creates fruit in bush
        Random r = new Random();
        int randomInt = r.nextInt(100) + 1;
        if (randomInt > 90) {
            this.addCapability(FoodCapability.HAS_FRUIT);
            fruits.add(fruit);
            //Display display = new Display();
            //display.println("Food spawned in bush");
        }
    }

    /**
     * Gets the arraylist of fruits
     * @return the array list of fruits
     */

    public ArrayList<Fruit> getFruits() {
        return fruits;
    }

    /***
     * removes fruit from the arraylist of fruit in the bush
     */
    public void removeFruit(){
        fruits.remove(0);
    }
}

