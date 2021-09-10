package game.actors;

import edu.monash.fit2099.engine.*;
import game.behaviours.FindItemBehaviour;
import game.capabilities.AttackCapabilities;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.capabilities.GenderCapability;
import game.ground.Lake;
import game.ground.Tree;
import game.items.Corpse;

import java.util.List;
import java.util.Random;

public class Pterodactyl extends Dinosaur {

    /**
     * Display to print out messages
     */
    private final Display display = new Display();
    /**
     * Amount of rounds that the pterodactyl has flown
     */
    private int flyingRounds = 0;
    /**
     * Amount of rounds that the pterodactyl has been unconscious for
     */
    private int notConsciousRound;

    /**
     * Constructor.
     *
     * @param name      the name of the Actor
     * @param hitPoints the Actor's starting hit points
     */
    public Pterodactyl(String name, int hitPoints) {
        super(name, 'P', hitPoints);
        if (this.name.equals("male") || this.name.equals("female")){
            setSpecificGender();
            this.name = "Pterodactyl";
        }
        else{
            setGender();
        }
        if (this.hasCapability(CanBreedCapability.BABY)) {
            this.maxHitPoints = 10;
        } else {
            this.maxHitPoints = 100;

        }
        this.addCapability(FoodCapability.PTERO);
        if (!this.hasCapability(CanBreedCapability.BABY)) {
            this.addCapability(CanBreedCapability.TRUE);
        }
        this.addCapability(AttackCapabilities.CANNOT_BE_ATTACKED);
        setWaterLevel();
    }

    @Override
    public void findFood(GameMap map) {
        super.findFood(map);
        int amountOfFish = (int) (Math.random() * 3);
        int tracker = 0;
        List<Item> items = map.locationOf(this).getItems();
        if (map.locationOf(this).getGround() instanceof Lake) {
            Lake lake = (Lake) map.locationOf(this).getGround();
            this.addWaterLevel(30);
            while (tracker <= amountOfFish) {
                if (lake.getFish().size() > 0) {
                    display.println("There are " + lake.getFish().size() + " fish in the lake.");
                    lake.getFish().remove(0);
                    this.heal(30);
                    lake.setCapacity(lake.getCapacity()-1);
                    amountOfFish--;
                    display.println("There are " + lake.getFish().size() + " fish in the lake now.");
                    tracker++;
                }
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).hasCapability(FoodCapability.CORPSE)) {
                    Corpse corpse = (Corpse) items.get(i);
                    this.heal(10);
                    corpse.setFoodPoint();
                    if (corpse.getFoodPoint() == 0) {
                        map.locationOf(this).removeItem(items.get(i));
                    }
                }

            }
        }
    }

    /**
     *Tells the Pterodactyl what to execute every turn
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return Action action of player can execute at this turn
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        // checks whether the pterodactyl
        flyingRounds++;
        if (flyingRounds >= 1) {
            if (map.locationOf(this).getGround() instanceof Tree) {
                flyingRounds = 0;
                this.addCapability(AttackCapabilities.CANNOT_BE_ATTACKED);
            } else{
                this.removeCapability(AttackCapabilities.CANNOT_BE_ATTACKED);
            }
        }
        checkBaby(this);
        checkEgg(this, map);
        if (isConscious() && waterIsConscious()) {
            // If the dinosaur is conscious, we check its thirst and make sure it is pointed to the direction of the lake in order to find lake.
            notConsciousRound = 0;
            checkThirst();
            findWater(map);
            if (hasCapability(FoodCapability.THIRSTY)) {
                display.println("Pterodactyl at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting thirsty!");
                FindItemBehaviour findItem = new FindItemBehaviour(4, 'w');
                Action find = findItem.getAction(this, map);
                if (find != null)
                    return find;
                return new DoNothingAction();
            }

            if (isConscious()) {
                hitPoints--;
                if (hitPoints <= 40) {
                    display.println("Pterodactyl flying at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting hungry");
                    findFood(map);
                    FindItemBehaviour findItem = new FindItemBehaviour(4, 'f');
                    Action find = findItem.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }else if (this.hasCapability(CanBreedCapability.HASEGG)) {
                    FindItemBehaviour findItem = new FindItemBehaviour(4, 't');
                    Action find = findItem.getAction(this, map);
                    if (find != null)
                        return find;
                    return new DoNothingAction();
                }
                else if (hitPoints < 60 || hasCapability(CanBreedCapability.FALSE)) {
                    breedingCooldown(this);
                    Random r = new Random();
                    int random = r.nextInt(100) + 1;
                    if (random > 80) {
                        if (!(map.locationOf(this).getGround() instanceof Tree)) {
                            FindItemBehaviour findItem = new FindItemBehaviour(4, 't');
                            Action find = findItem.getAction(this, map);
                            if (find != null)
                                return find;
                        }
                    } else {
                        FindItemBehaviour findItem = new FindItemBehaviour(4, 'f');
                        Action find = findItem.getAction(this, map);
                        if (find != null)
                            return find;
                    }
                    return new DoNothingAction();
                }
                else {
                    if (this.hasCapability(GenderCapability.MALE)) {
                        return findNearest(map, 4);
                    }else{
                        if (!(map.locationOf(this).getGround() instanceof Tree)) {
                            FindItemBehaviour findItem = new FindItemBehaviour(4, 't');
                            Action find = findItem.getAction(this, map);
                            if (find != null)
                                return find;
                        }
                        return new DoNothingAction();

                    }
                }
            }
        }
        if (notConsciousRound < 15) {
            notConsciousRound++;
            display.println(this.name + " is unconscious for "+ notConsciousRound + " rounds");
            return new DoNothingAction();
        }
        // Create a corpse object and make it stay on the map
        Item corpse = new Corpse("Pterodactyl",'%');
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
