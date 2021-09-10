package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import game.capabilities.FoodCapability;
import game.ground.Bush;
import game.ground.Tree;
import game.items.Fruit;
import game.actors.Player;
import java.util.Random;

/**
 * This class provides an action for player to search or find fruit when they stepped on the bush or tree.
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.engine.Action
 * @see edu.monash.fit2099.interfaces.ActionInterface
 */
public class FindFruitAction extends Action {
    /**
     * Empty constructor to let player call the action
     */
    public FindFruitAction() {
    }

    /**
     * This method is called when the action is executed and it will have 60% chance to search a fruit and add it to
     * player's inventory.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return String message to show the result.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Random r = new Random();
        Player player = (Player) actor;
        int randomInt = r.nextInt(100) + 1;
        // When the current ground player stepped on is a bush
        if (map.locationOf(player).getGround() instanceof Bush){
            Bush bush = (Bush) map.locationOf(player).getGround();
            // if current bush has no fruit, return fail message
            if (!bush.hasCapability(FoodCapability.HAS_FRUIT)){
                return "There is currently no fruit inside the Bush";
            }
            // else provide 60% chance to add the fruit in to the player's inventory
            else{
                if (randomInt > 60){
                    Fruit fruit = new Fruit();
                    player.addItemToInventory(fruit);
                    bush.removeFruit();
                    player.addEcoPoint(10);
                    return "A fruit has been added to your inventory! Your current inventory: " + player.getInventory().toString();
                }
                else{
                    return "You search the tree or bush for fruit, but you can’t find any ripe ones.";
                }
            }
        }
        // When the current ground player stepped on is a tree
        else if (map.locationOf(player).getGround() instanceof Tree){
            Tree tree = (Tree) map.locationOf(player).getGround();
            // if current tree has no fruit, return fail message
            if (!tree.hasCapability(FoodCapability.HAS_FRUIT)){
                return "There is currently no fruit on the Tree";
            }
            // else provide 60% chance to add the fruit in to the player's inventory
            else{
                if (randomInt > 60){
                    Fruit fruit = new Fruit();
                    player.addItemToInventory(fruit);
                    tree.removeFruit();
                    player.addEcoPoint(10);
                    return "A fruit has been added to your inventory! Your current inventory: " + player.getInventory().toString();
                }
                else{
                    return "You search the tree or bush for fruit, but you can’t find any ripe ones.";
                }
            }
        }
        // if the ground is not tree or bush, return nothing
        else{
            return null;
        }
    }

    /**
     * This method provides a menu description for FindFruit action
     * @param actor The actor performing the action.
     * @return String player search fruit
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " search fruit";
    }
}
