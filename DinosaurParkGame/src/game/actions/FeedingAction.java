package game.actions;

import edu.monash.fit2099.engine.*;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.actors.Allosaur;
import game.actors.Brachiosaur;
import game.actors.Stegosaur;
import game.items.Fruit;
import game.items.MeatMealKit;
import game.items.VegetarianMealKit;

import java.util.List;
import java.util.Scanner;

/**
 * Feeding action class that handles the action of player feeding other dinosaurs. Extends action
 * @author Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.engine.Action
 * @see edu.monash.fit2099.interfaces.ActionInterface
 */
public class FeedingAction extends Action {
    /**
     * target to store the target player wants to feed
     */
    private final Actor target;
    /**
     * String direction to store the direction message
     */
    private final String direction;

    /**
     * Feeding action that allows and array list of actors to be parsed in. This is to make sure all dinosaurs around them are being considered.
     * @param actor Actor target to feed
     */
    public FeedingAction(Actor actor, String myDirection){
        this.target = actor;
        this.direction = myDirection;
    }

    /**
     * Executes the Feeding action on the targets
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return String message to show the feeding message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Scans for user input
        Scanner scanner = new Scanner(System.in);
        Display display = new Display();
        display.println("1. Feed Fruit \n2. Feed MealKit");
        int option;
        option =scanner.nextInt();
        // If user decides to feed with fruit, we will loop through the play's inventory, find the fruit item and heal the dinosaurs by 20.
        List<Item> items;
        if (option == 1) {
            items = actor.getInventory();
            for (int j = 0; j < items.size(); j++) {
                if (items.get(j) instanceof Fruit) {
                    if (target.hasCapability(FoodCapability.STEG) || target.hasCapability(FoodCapability.BRAC)) {
                        target.heal(20);
                        actor.hasCapability(CanBreedCapability.CANADD);
                        actor.removeItemFromInventory(items.get(j));
                        return target +"'s health has been restored by 20";
                    }
                }
            }return ("Player does not have any fruit in the inventory");
        }else if (option == 2) {
            // If user decides to feed with fruit, we will loop through the play's inventory, find the MealKit item and heal the dinosaurs by 20.
            items = actor.getInventory();
            for (int j = 0; j < items.size(); j++) {
                // If the target is a herbivore, feed the vegetarian meal kit to them and heal them to their maximum food level
                if (items.get(j) instanceof VegetarianMealKit) {
                    if (target.hasCapability(FoodCapability.BRAC)) {
                        Brachiosaur brachiosaur = (Brachiosaur) target;
                        brachiosaur.setFoodLevel(160);
                        actor.removeItemFromInventory(items.get(j));
                        return target +"'s health has been restored to the maximum";
                    } else if (target.hasCapability(FoodCapability.STEG)) {
                        Stegosaur stegosaur = (Stegosaur) target;
                        stegosaur.setFoodLevel(100);
                        actor.removeItemFromInventory(items.get(j));
                        return target +"'s health has been restored to the maximum";
                    }
                    // If the target is a carnivore, feed the vegetarian meal kit to them and heal them to their maximum food level
                } else if (items.get(j) instanceof MeatMealKit) {
                    if (target.hasCapability(FoodCapability.ALLO)) {
                        Allosaur allosaur = (Allosaur) target;
                        allosaur.setFoodLevel(100);
                        actor.removeItemFromInventory(items.get(j));
                        return target +"'s health has been restored to the maximum";
                    }
                }
            }return ("Player does not have any meal kits in the inventory");

        }
        return "No dinosaur has been fed";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " feeds " + target + " at " + direction;
    }
}
