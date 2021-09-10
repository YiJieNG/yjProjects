package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * This class shows the message of the Breeding Action when the action is executed
 * @author Ng Yi Jie, Darren Yee
 */
public class BreedingAction extends Action {
    /**
     * Empty constructor
     */
    public BreedingAction() {
    }

    /**
     * Execute method
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return String message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return menuDescription(actor);
    }

    /**
     * Print method
     * @param actor The actor performing the action.
     * @return String actor mate!
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " mate!";
    }
}
