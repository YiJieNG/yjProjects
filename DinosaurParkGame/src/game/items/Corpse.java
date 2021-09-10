package game.items;

import edu.monash.fit2099.engine.Location;
import game.capabilities.FoodCapability;

/**
 * A class which creates the corpse for the dead Dinosaur and has its own age base on the dinosaur type.
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see game.items.PortableItem
 * @see game.actors.Dinosaur
 */
public class Corpse extends PortableItem {
    /**
     * int age of the corpse
     */
    private int age;
    private int foodPoint;

    /**
     * Empty constructor
     */
    public Corpse() {
        super("default",'%');
    }

    /**
     * Constructor which set the age of the corpse base on the dinosaur type
     * @param name String name of the dinosaur
     * @param displayChar char %
     */
    public Corpse(String name, char displayChar) {
        super(name, displayChar);
        if (name.equals("Brachiosaur")){
            this.age = 40;
            this.foodPoint = 160;
        }
        else if (name.equals("Pterodactyl")){
            this.age = 15;
            this.foodPoint = 30;
        }
        else{
            this.age = 15;
            this.foodPoint = 100;
        }
        this.addCapability(FoodCapability.CORPSE);
    }

    /**
     * Decrement the age of the corpse every round
     * @param currentLocation The location of the ground on which we lie.
     */
    @Override
    public void tick(Location currentLocation) {
        super.tick(currentLocation);
        this.age -= 1;
        if (this.age == 0){
            currentLocation.removeItem(this);
        }
    }
    public int getFoodPoint() {
        return foodPoint;
    }
    public void setFoodPoint(){
        foodPoint -= 10;
    }
}

