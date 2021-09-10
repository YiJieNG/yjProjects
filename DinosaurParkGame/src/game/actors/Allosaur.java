package game.actors;

import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;
import game.behaviours.*;
import game.capabilities.AttackCapabilities;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.items.Corpse;
import game.items.Egg;

import java.util.List;
import java.util.Random;

/**
 * A herbivorous dinosaur.
 *
 */
public class Allosaur extends Dinosaur {
    private int notConsciousRound;

    /**
     * Constructor.
     * All Allosaur are represented by a 'A' and have 100 hit points.
     * @param name the name of this Allosaur
     */
    public Allosaur(String name, int hitpoint) {
        super(name, 'A', hitpoint);
        setSpecificGender();
        this.addCapability(FoodCapability.ALLO);
        if (this.hasCapability(CanBreedCapability.BABY)) {
            this.maxHitPoints = 20;
        } else {
            this.maxHitPoints = 100;
        }
        this.name = "Allosaur";
        this.addCapability(FoodCapability.ALLO);
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.addCapability(CanBreedCapability.TRUE);
        }
        setWaterLevel();
    }

    /**
     * GetAllowableAction from Actor class
     * @param otherActor the Actor that might be performing attack
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return
     */
    @Override
    public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
        return new Actions(new AttackAction(this));
    }

    /**
     * Find food method that overwrites the dinosaur class due to the each dinosaur's food being different
     * @param map map of the actor
     */

    @Override
    public void findFood(GameMap map) {
        super.findFood(map);
        if (this.hasCapability(FoodCapability.ALLO)) {
            List<Item> items = map.locationOf(this).getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) instanceof Corpse) {
                    Corpse corpse = (Corpse) items.get(i);
                    this.heal(corpse.getFoodPoint());
                    System.out.println("Ate corpse");
                    map.locationOf(this).removeItem(items.get(i));
                } else if (items.get(i) instanceof Egg) {
                    map.locationOf(this).removeItem(items.get(i));
                    System.out.println("Ate egg");
                    this.heal(10);
                }
            }
        }
    }
    /**
     *Tells the Allosaur what to execute every turn
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        // check baby method from dinosaur
        checkBaby(this);
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.maxHitPoints = 100;
        }
        checkEgg(this,map);
        if (isConscious() && waterIsConscious()) {
            // If the dinosaur is conscious, we check its thirst and make sure it is pointed to the direction of the lake in order to find lake.
            notConsciousRound = 0;
            checkThirst();
            findWater(map);
            if (hasCapability(FoodCapability.THIRSTY)){
                display.println("Allosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting thirsty!");
                FindItemBehaviour findItem = new FindItemBehaviour(3,'w');
                Action find = findItem.getAction(this, map);
                if (find != null)
                    return find;
                return new DoNothingAction();
            }
            hitPoints--;
            if (hitPoints <= 40) {
                int x = map.locationOf(this).x();
                int y = map.locationOf(this).y();
                display.println("Allosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting hungry!");
                // check whether there are stegosaurs at the right or left of them.
                if (x + 1 < 80) {
                    if (map.getActorAt(map.at(x + 1, y)) != null) {
                        if (!(map.getActorAt(map.at(x + 1, y))).hasCapability(FoodCapability.BRAC) && !(map.getActorAt(map.at(x + 1, y))).hasCapability(AttackCapabilities.CANNOT_BE_ATTACKED)) {
                            return new AttackAction(map.getActorAt(map.at(x + 1, y)));
                        }
                    }
                }
                if (x - 1 > 0) {
                    if (map.getActorAt(map.at(x - 1, y)) != null) { if (!(map.getActorAt(map.at(x - 1, y))).hasCapability(FoodCapability.BRAC) && !(map.getActorAt(map.at(x - 1, y))).hasCapability(AttackCapabilities.CANNOT_BE_ATTACKED)) {
                            return new AttackAction(map.getActorAt(map.at(x - 1, y)));
                        }
                    }
                }
                findFood(map);
                if (hitPoints <= 50) {
                    FindItemBehaviour findItemBehaviour = new FindItemBehaviour(3,'f');
                    Action find = findItemBehaviour.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
                // if in middle ground, randomize next option
            } else if (hitPoints <= 70 || hasCapability(CanBreedCapability.FALSE)) {
                breedingCooldown(this);
                Random r = new Random();
                int random = r.nextInt(100)+1;
                if (random > 50){
                    FindItemBehaviour findItem = new FindItemBehaviour(3,'w');
                    Action find = findItem.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
                else{
                    FindItemBehaviour findItemBehaviour = new FindItemBehaviour(3,'f');
                    Action find = findItemBehaviour.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
            } else {
                return findNearest(map,3);
            }
        }
        // make their corpse stay on the map for 20 rounds
        if (notConsciousRound < 20) {
            notConsciousRound++;
            display.println(this.name + "is unconscious for "+ notConsciousRound);
            return new DoNothingAction();
        }
        // make the location to its corpse to ensure that allosaurs can find it and eat them
        Item corpse = new Corpse("Allosaur",'%');
        display.println(this.name + "is dead");
        corpse.addCapability(FoodCapability.ALLO);
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
