package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import game.ground.VendingMachine;
import game.actors.Player;

import java.util.Scanner;

/**
 * This class provides an action for player to visit the vending machine when player stepped on the vending machine.
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.interfaces.ActionInterface
 * @see edu.monash.fit2099.engine.Action
 * @see game.ground.VendingMachine
 */
public class VisitAction extends Action {
    /**
     * the vending machine current stepped
     */
    private VendingMachine shop = new VendingMachine();
    /**
     * player
     */
    private Player player;
    /**
     * display method
     */
    private Display display = new Display();
    /**
     * message return
     */
    private String ans;

    /**
     * An empty constructor to allow player access the visitAction
     */
    public VisitAction() {
    }

    /**
     * This method is called when the VisitAction is executed and it will access the vending machine to provide
     * further action and return the status message.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return String message to show the result.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        player = (Player) actor;
        int option;
        Scanner scanner = new Scanner(System.in);
        // Show shop description and let player enter the option
        display.println("<-------------------------------------SHOP-------------------------------------------->");
        display.println("Current Inventory List: " + player.getInventory().toString());
        display.println("Current available eco points: "+ player.getEcoPoint());
        display.println("Select an option to buy the item (Item option: Item(Eco Points required per item))");
        display.println("FOOD--> 1: Fruit(30)  2: Vegetarian Meal Kit(100)  3. Meat Meal Kit(500)");
        display.println("EGG--> 4. Stegosaur Egg(200)  5. Brachiosaur Egg(500)  6. Allosaur Egg(1000) 7. Pterodactyl Egg(300)");
        display.println("OTHER--> 8. Laser Gun(500)  0. Exit");
        display.println("<------------------------------------------------------------------------------------->");
        display.println("Option: ");
        option = scanner.nextInt();
        // if the player successfully purchase an item, return the message
        if(shop.canPurchase(player, option)){
            ans = "Player bought item " + option;
        }
        // else if player has no enough money or player enter wrong item option, return the message
        else{
            ans = "Player bought nothing";
        }
        // show player current inventory
        display.println(player.getInventory().toString());
        return ans;
    }

    /**
     * This method provides a menu description for VisitAction action
     * @param actor The actor performing the action.
     * @return String player visits store.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " visits store";
    }
}
