package game.items;

import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Location;
import game.capabilities.FoodCapability;

/**
 * This class creates Fruit object
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.engine.Item
 * @see game.ground.Tree
 * @see game.ground.Bush
 */
public class Fruit extends PortableItem {
    /**
     * round of the fruit dropped to the ground from tree
     */
    private int dropped;
    private Display display = new Display();

    /**
     * Constructor for fruit
     * @param name the name of this Item
     * @param displayChar the character to use to represent this item if it is on the ground
     */
    public Fruit(String name, char displayChar) {
        super("fruit", displayChar);
        this.addCapability(FoodCapability.CAN_FEED);
    }

    /**
     * Empty Constructor of Fruit
     */
    public Fruit(){
        super("fruit", 'F');
    }

    /**
     * Constructor for fruit
     * @param name the name of this Item
     * @param displayChar the character to use to represent this item if it is on the ground
     * @param portable true if and only if the Item can be picked up
     * @param dropped round of fruit that dropped on the ground
     */
    public Fruit(String name, char displayChar, boolean portable, int dropped) {
        super(name, displayChar);
        this.dropped = dropped;
        this.addCapability(FoodCapability.CAN_FEED);
    }

    /**
     * This method records the round of the fruit dropped. If it was dropped 15 rounds before, it will disappear.
     * @param currentLocation The location of the ground on which we lie.
     */
    @Override
    public void tick(Location currentLocation) {
        super.tick(currentLocation);
        if (this.hasCapability(FoodCapability.WILL_ROT)){
            this.dropped -= 1;
            if (this.dropped <= 0){
                currentLocation.removeItem(this);
                //display.println("Fruit Rotten");
            }
        }
    }
}



