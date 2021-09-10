package game.actors;

import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;
import game.behaviours.FindItemBehaviour;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.ground.Bush;
import game.ground.Dirt;
import game.ground.Tree;
import game.items.Corpse;

import java.util.Random;

/**
 * A herbivorous dinosaur.
 *
 */
public class Brachiosaur extends Dinosaur {
    /**
     * Amount of rounds that the Brachiosaur is unconscious for
     */
    private int notConsciousRound;
    /**
     * Constructor.
     * All Brachiosaur are represented by a 'b' and have 160 hp max.
     *
     * @param name the name of this Brachiosaur
     */
    public Brachiosaur(String name,int hitpoint) {
        super(name, 'B', hitpoint);
        if (this.name == "male" || this.name == "female"){
            setSpecificGender();
            this.name = "Brachiosaur";
        }
        else{
            setGender();
        }
        if (this.hasCapability(CanBreedCapability.BABY)){
            this.maxHitPoints = 10;
        }
        else{
            this.maxHitPoints = 160;

        }
        this.addCapability(FoodCapability.BRAC);
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.addCapability(CanBreedCapability.TRUE);
        }
        setWaterLevel();
    }
    /**
     * Find food method that overwrites the dinosaur class due to the each dinosaur's food being different
     * @param map map of the actor
     */
    @Override
    public void findFood(GameMap map) {
        super.findFood(map);
        if (map.locationOf(this).getGround() instanceof Tree) {
            Tree tree = (Tree) map.locationOf(this).getGround();
            while (tree.getFruits().size() > 0) {
                tree.getFruits().remove(0);
                this.heal(5);
                }
            }
        }

    @Override
    public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
        return new Actions(new AttackAction(this));
    }
    /**
     *Tells the Brachiosaur what to execute every turn
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        checkBaby(this);
        checkEgg(this,map);
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.maxHitPoints = 160;
        }
        if(isConscious()&&waterIsConscious()) {
            // If the dinosaur is conscious, we check its thirst and make sure it is pointed to the direction of the lake in order to find lake.
            notConsciousRound = 0;
            checkThirst();
            findWater(map);
            if (hasCapability(FoodCapability.THIRSTY)){
                display.println("Brachiosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting thirsty!");
                FindItemBehaviour findItem = new FindItemBehaviour(1,'w');
                Action find = findItem.getAction(this, map);
                if (find != null)
                    return find;
                return new DoNothingAction();
            }
            // has a chance of destroying the bush when stepped on
            if (map.locationOf(this).getGround() instanceof Bush) {
                Random r = new Random();
                int random = r.nextInt(100) + 1;
                if (random > 25) {
                    map.locationOf(this).setGround(new Dirt());
                    display.println("The ground at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") has been stepped on and is now a dirt: " + map.locationOf(this).getGround().getDisplayChar());
                }
            }
            hitPoints--;
            // if hungry, find food to eat
            if (hitPoints <= 80) {
                display.println("Brachiosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting hungry!");
                findFood(map);
                FindItemBehaviour findItem = new FindItemBehaviour(1,'f');
                Action find = findItem.getAction(this, map);
                if (find != null)
                    return find;
                return new DoNothingAction();

            } // if stuck in the middle ground, we randomize their next action
            else if (hitPoints < 120 || hasCapability(CanBreedCapability.FALSE)) {
                breedingCooldown(this);
                Random r = new Random();
                int random = r.nextInt(100)+1;
                if (random > 50) {
                    FindItemBehaviour findItem = new FindItemBehaviour(1,'w');
                    Action find = findItem.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
                else{
                    FindItemBehaviour findItem = new FindItemBehaviour(1,'f');
                    Action find = findItem.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
            } else {
                return findNearest(map, 2);
            }
        }
        // if they are not conscious for a certain round , return do nothing action
        if (notConsciousRound < 40) {
            notConsciousRound++;
            display.println(this.name + " is unconscious for "+ notConsciousRound + " rounds");
            return new DoNothingAction();
        }
        // Create a corpse object and make it stay on the map
        Item corpse = new Corpse("Brachiosaur",'%');
        display.println(this.name + " is dead");
        map.locationOf(this).addItem(corpse);

        Actions dropActions = new Actions();
        for (Item item : this.getInventory())
            dropActions.add(item.getDropAction());
        for (Action drop : dropActions)
            drop.execute(this, map);
        map.removeActor(this);
        return new DoNothingAction();
    }
}
