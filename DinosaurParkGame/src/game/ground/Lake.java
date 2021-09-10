package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;
import game.capabilities.FoodCapability;
import game.items.Fish;

import java.util.ArrayList;
import java.util.Random;

/**
 * A class which represents the lake
 */
public class Lake extends Ground {
    /**
     * int capacity of the lake
     */
    private int capacity;
    /**
     * Arraylist which represents the number of fish
     */
    private ArrayList<Fish> fish = new ArrayList<Fish>();

    /**
     * Constructor
     */
    public Lake() {
        super('~');
        capacity = 25;
        for(int x=0;x<5;x++){
            Fish myFish = new Fish();
            fish.add(myFish);
        }
    }

    /**
     * Getter of the lake capacity
     * @return int capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Setter of the lake capacity
     * @param capacity int capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * The method to update the lake on the map every round
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        spawnFish();
        if (fish.size() <= 0){
            this.removeCapability(FoodCapability.HAS_FISH);

        }
    }

    /**
     * Raining method which add the capacity of the lake for a random number
     */
    public void rain(){
        double randomDouble = (Math.random()*((0.6-0.1)+1))+0.1;
        int oldCapacity = getCapacity();
        setCapacity(getCapacity()+(int)Math.round(20 * randomDouble));
        System.out.println("The capacity of lake was added from "+oldCapacity+" to "+getCapacity());
    }

    /**
     * Spawn fish method which let the lake have 80% to spawn a fish inside a lake maximum 15 fish.
     */
    public void spawnFish(){
        Random r = new Random();
        int randomInt = r.nextInt(100) + 1;
        if (randomInt > 40){
            Fish myFish = new Fish();
            if (fish.size()<25){
                fish.add(myFish);
            }
            this.addCapability(FoodCapability.HAS_FISH);
        }
    }

    /**
     * Getter of fish
     * @return ArrayList<Fish> fish array list of fish
     */
    public ArrayList<Fish> getFish() {
        return fish;
    }

    /**
     * Method which helps to check whether the actor coming in can enter the lake or not
     * @param actor the Actor to check
     * @return boolean true if it is a pterodactyl else false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        if (actor.hasCapability(FoodCapability.STEG) || actor.hasCapability(FoodCapability.BRAC) || actor.hasCapability(FoodCapability.ALLO)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method which not allow any actor to throw object at the ground
     * @return boolean true
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }
}
