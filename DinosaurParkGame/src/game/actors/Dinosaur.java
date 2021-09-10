package game.actors;

import edu.monash.fit2099.engine.*;
import game.actions.BreedingAction;
import game.behaviours.FindItemBehaviour;
import game.behaviours.FollowBehaviour;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.capabilities.GenderCapability;
import game.ground.Lake;
import game.ground.Tree;
import game.items.Egg;
import java.util.Random;

public abstract class Dinosaur extends Actor {
    /**
     * Age of the baby dinosaur
     */
    private int age;
    /**
     * Target that the dinosaur is being led towards
     */
    private Actor target;
    /**
     * Water level of the dinosaurs
     */
    private int waterLevel = 0;
    /**
     * Display to print out the messages in the menu
     */
    private Display display = new Display();
    /**
     * Checking the nearest dinosaur within the range
     */
    private int nearest = 100;
    /**
     * Rounds until laying eggs are allowed
     */
    private int layEgg;
    /**
     * Valid location to enter
     */
    private Location validLocation;
    /**
     * Breeding cooldown
     */
    private int coolDown = 0;
    /**
     * Maximum water level for the dinosaurs
     */
    private int maxWaterLevel;

    /**
     * Constructor.
     *
     * @param name        the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints   the Actor's starting hit points
     */
    public Dinosaur(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }


    /**
     * Sets the starting water level and the maximum water level of each dinosaurs
     */
    public void setWaterLevel(){
        if (this.hasCapability(FoodCapability.BRAC)){
            this.maxWaterLevel = 200;
        }
        else{
            this.maxWaterLevel = 100;
        }
        this.waterLevel = 1;
    }

    /**
     * Increments the water level of each dinosaur by the specified values
     * @param water
     */
    public void addWaterLevel(int water) {
        if (this.waterLevel + water > this.maxWaterLevel) {
            this.waterLevel = this.maxWaterLevel;
        } else {
            this.waterLevel += water;
        }
    }

    /**
     * Checks whether the dinosaur is thirsty (water level <40). Will add capability of thirsty which will help us check
     * whether the dinosaur needs to look for a lake to drink.
     */
    public void checkThirst(){
        this.waterLevel -= 1;
        if (this.waterLevel < 40){
            this.addCapability(FoodCapability.THIRSTY);
        }
        else{
            this.removeCapability(FoodCapability.THIRSTY);
        }
    }

    /**
     * Checks whether the dinosaur is conscious/unconscious due to the lack of water.
     * @return
     */

    public Boolean waterIsConscious(){
        return this.waterLevel > 0;
    }

    /**
     * sets hitpoint
     * @param hp
     */

    public void setFoodLevel(int hp) {
        this.hitPoints = hp;
    }

    /**
     * decrements the age
     * @param age age binded to baby dinosaurs for them to turn into adults
     */

    public void minusAge(int age) {
        this.age -= age;
    }

    /**
     * returns age
     * @return
     */

    public int returnAge() {
        return this.age;
    }

    /**
     * sets age of dinosaur (for baby)
     * @param age
     */

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Specific gender setter
     */
    public void setSpecificGender(){
        // if its name is male, set the gender to male. Else set to female
        if (this.name == "male"){
            this.addCapability(GenderCapability.MALE);
            this.addCapability(FoodCapability.CAN_FEED);
        }
        else {
            this.addCapability(GenderCapability.FEMALE);
            this.addCapability(FoodCapability.CAN_FEED);
        }
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.addCapability(CanBreedCapability.TRUE);
        }
    }

    /**
     * Random generator to set the dinosaur's gender
     */

    public void setGender() {
        // Randomly generate its gender
        Random r = new Random();
        int randomInt = r.nextInt(100) + 1;
        if (randomInt > 50) {
            this.addCapability(GenderCapability.MALE);
            this.addCapability(FoodCapability.CAN_FEED);
        } else {
            this.addCapability(GenderCapability.FEMALE);
            this.addCapability(FoodCapability.CAN_FEED);
            layEgg = 0;
        }
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.addCapability(CanBreedCapability.TRUE);
        }
    }

    /**
     * checks if the dinosaur is a baby
     * @param actor
     */

    public void checkBaby(Actor actor) {
        // Checks if the actor has the capability of baby
        Dinosaur dinosaur = (Dinosaur) actor;
        if (dinosaur.hasCapability(CanBreedCapability.BABY)) {
            char display = actor.getDisplayChar();
            dinosaur.displayChar = Character.toLowerCase(display);
            dinosaur.minusAge(1);
            if (dinosaur.returnAge() <= 0) {
                dinosaur.removeCapability(CanBreedCapability.BABY);
                dinosaur.displayChar = Character.toUpperCase((display));
            }
        }
    }

    /**
     * checks if the egg is ready to be hatched
     * @param actor
     * @param map
     */

    public void checkEgg(Actor actor, GameMap map) {
        // checks if the egg is ready to be htached
        Dinosaur dinosaur = (Dinosaur) actor;
        if (isConscious()) {
            if (hasCapability(CanBreedCapability.HASEGG) && hasCapability(FoodCapability.BRAC)) {
                if (layEgg <= 40) {
                    layEgg++;
                } else {
                    Egg egg = new Egg(dinosaur);
                    map.locationOf(dinosaur).addItem(egg);
                    removeCapability(CanBreedCapability.HASEGG);
                }
            } else if (hasCapability(CanBreedCapability.HASEGG)) {
                if (layEgg <= 15) {
                    layEgg++;
                } else {
                    if (this.hasCapability(FoodCapability.PTERO))
                        if (map.locationOf(this).getGround() instanceof Tree) {
                            Egg egg = new Egg(dinosaur);
                            map.locationOf(dinosaur).addItem(egg);
                            removeCapability(CanBreedCapability.HASEGG);
                        } else {
                            Egg egg = new Egg(dinosaur);
                            map.locationOf(dinosaur).addItem(egg);
                            removeCapability(CanBreedCapability.HASEGG);
                        }
                }
            }
            }
        }

    /**
     * empty find food method to be overwritten
     * @param map
     */

    public void findFood(GameMap map) {

    }

    public void findWater(GameMap map){
        Location location = map.locationOf(this);
        int addWater;
        if (this.hasCapability(FoodCapability.BRAC)){
            addWater = 80;
        }
        else{
            addWater = 30;
        }
        if (location.y() + 1 < 25 && map.at(location.x(), location.y()+1).getGround() instanceof Lake){
            Lake lake = (Lake)map.at(location.x(), location.y()+1).getGround();
            this.addWaterLevel(addWater);
            lake.setCapacity(lake.getCapacity()-1);
            display.println(this + " drinks water");
            //display.println("lake: "+location.x()+" "+(location.y()+1));
        }
        else if (location.y() - 1 >= 0 && map.at(location.x(), location.y()-1).getGround() instanceof Lake){
            Lake lake = (Lake)map.at(location.x(), location.y()-1).getGround();
            this.addWaterLevel(addWater);
            lake.setCapacity(lake.getCapacity()-1);
            display.println(this + " drinks water");
            //display.println("lake: "+location.x()+" " +(location.y()-1));
        }
        else if (location.x()+1 < 80 && map.at(location.x()+1, location.y()).getGround() instanceof Lake){
            Lake lake = (Lake)map.at(location.x()+1, location.y()).getGround();
            this.addWaterLevel(addWater);
            lake.setCapacity(lake.getCapacity()-1);
            display.println(this + " drinks water");
            //display.println("lake: "+(location.x()+1)+" " +(location.y()));
        }
        else if (location.x()-1>=0 && map.at(location.x()-1, location.y()).getGround() instanceof Lake){
            Lake lake = (Lake)map.at(location.x()-1, location.y()).getGround();
            this.addWaterLevel(addWater);
            lake.setCapacity(lake.getCapacity()-1);
            display.println(this + " drinks water");
            //display.println("lake: "+(location.x()-1)+" " +(location.y()));
        }
    }

    /**
     * Checks if the cooldown for breeding is over (default 15 rounds)
     * @param actor
     */

    public void breedingCooldown(Actor actor) {
        if (hasCapability(CanBreedCapability.FALSE) && !hasCapability(CanBreedCapability.HASEGG)) {
            if (coolDown <= 15)
                coolDown++;
            else {
                removeCapability(CanBreedCapability.FALSE);
                addCapability(CanBreedCapability.TRUE);
                coolDown = 0;
            }
        }
    }

    /**
     * Loops through the entire map and find the nearest partner for the dinosaurs to breed
     * @param map
     * @param type
     * @return
     */
    public Action findNearest(GameMap map, int type) {
        Location actorLocation = map.locationOf(this);
        for (int i : map.getXRange()) {
            for (int j : map.getYRange()) {
                boolean found = false;
                // checks which type it is
                GameMap gameMap = actorLocation.map();
                if (type == 1) {
                    if (gameMap.at(i, j).getActor() instanceof Stegosaur) {
                        if (gameMap.at(i, j).getActor().hasCapability(CanBreedCapability.TRUE))
                            found = true;
                    }
                } else if (type == 2) {
                    if (gameMap.at(i, j).getActor() instanceof Brachiosaur) {
                        if (gameMap.at(i, j).getActor().hasCapability(CanBreedCapability.TRUE))
                            found = true;
                    }
                } else if (type == 3) {
                    if (gameMap.at(i, j).getActor() instanceof Allosaur) {
                        if (gameMap.at(i, j).getActor().hasCapability(CanBreedCapability.TRUE))
                            found = true;
                    }
                } else if (type == 4) {
                    if (gameMap.at(i, j).getActor() instanceof Pterodactyl) {
                        if (gameMap.at(i, j).getActor().hasCapability(CanBreedCapability.TRUE))
                            found = true;
                    }
                }
                // checks whether it is a valid partner
                if (found) {
                    if (hasCapability(GenderCapability.MALE) && gameMap.at(i, j).getActor().hasCapability(GenderCapability.FEMALE)) {
                        validLocation = new Location(gameMap, i, j);
                        int currentDistance = distance(actorLocation, validLocation);
                        if (currentDistance < nearest) {
                            nearest = currentDistance;
                            target = gameMap.at(i, j).getActor();
                        }
                    } else if (this.hasCapability(GenderCapability.FEMALE) && gameMap.at(i, j).getActor().hasCapability(GenderCapability.MALE)) {
                        validLocation = new Location(gameMap, i, j);
                        int currentDistance = distance(actorLocation, validLocation);
                        if (currentDistance < nearest) {
                            nearest = currentDistance;
                            target = gameMap.at(i, j).getActor();
                        }
                    }
                }
            }
        }
        // choose the nearest and use follow behaviour to seek the target
        if (nearest >= 2) {
            FollowBehaviour followBehaviour = new FollowBehaviour(target);
            Action follow = followBehaviour.getAction(this, map);
            if (follow != null)
                return follow;
            return new DoNothingAction();
        } else {
            if (this.hasCapability(FoodCapability.PTERO)  && !(map.locationOf(target).getGround() instanceof Tree) || !(map.locationOf(this).getGround() instanceof Tree)) {
                FindItemBehaviour findItem = new FindItemBehaviour(4, 't');
                Action find = findItem.getAction(this, map);
                if (find != null)
                    return find;
                return new DoNothingAction();
            } else {
                FinishedBreeding();
                return new BreedingAction();
            }
        }
    }

    /**
     * Once the finish breeding, we set them to their cooldowns and make sure they are able to hatch the eggs
     */
    public void FinishedBreeding() {
        removeCapability(CanBreedCapability.TRUE);
        addCapability(CanBreedCapability.FALSE);
        target.removeCapability(CanBreedCapability.TRUE);
        target.addCapability(CanBreedCapability.FALSE);
        if (hasCapability(GenderCapability.FEMALE)) {
            addCapability(CanBreedCapability.HASEGG);
        } else if (target.hasCapability(GenderCapability.FEMALE)) {
            target.addCapability(CanBreedCapability.HASEGG);
        }
    }

    /**
     * General method used to find the distance between the two objects
     * @param a
     * @param b
     * @return
     */
    public int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}


