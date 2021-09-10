package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * This class provides an action for player to quit the game.
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.engine.Action
 * @see edu.monash.fit2099.interfaces.ActionInterface
 */
public class QuitGameAction extends Action {
    /**
     * message that wants to print at console
     */
    private final String myMessage;

    /**
     * constructor
     * @param message String message that wants to print at console
     */
    public QuitGameAction(String message){
        this.myMessage = message;
    }

    /**
     * This method remove the actor from the map
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return String myMessage
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        map.removeActor(actor);
        return myMessage;
    }

    /**
     * This method provides a menu description for QuitGame action
     * @param actor The actor performing the action.
     * @return String Quit game
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Quit game";
    }
}
