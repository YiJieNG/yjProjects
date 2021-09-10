package game.behaviours;

import edu.monash.fit2099.engine.*;

/**
 * A class that figures out a MoveAction that will move the actor one step 
 * closer to a target Actor.
 * @author Ng Yi Jie, Darren Yee
 * @version 1.0
 * @see game.behaviours.Behaviour
 * @see game.actors.Allosaur
 */
public class FollowBehaviour implements Behaviour {

	private Actor target;

	/**
	 * Constructor.
	 * 
	 * @param subject the Actor to follow
	 */
	public FollowBehaviour(Actor subject) {
		this.target = subject;
	}

	/**
	 * If the condition is met, the action is called to move the actor towards to the target one step if the target and
	 * actor both are valid.
	 * @param actor the Actor acting
	 * @param map the GameMap containing the Actor
	 * @return Action MoveActorAction if valid target found else return nothing.
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		// if no valid target or no actor found, return nothing
		if(!map.contains(target) || !map.contains(actor)){
			return null;
		}
		else{
		// else return action base on the location of the target
		Location here = map.locationOf(actor);
		Location there = map.locationOf(target);


		int currentDistance = distance(here, there);
		for (Exit exit : here.getExits()) {
			Location destination = exit.getDestination();
			if (destination.canActorEnter(actor)) {
				int newDistance = distance(destination, there);
				if (newDistance < currentDistance) {
					return new MoveActorAction(destination, exit.getName());
				}
			}
		}

		return null;}

	}

	/**
	 * Compute the Manhattan distance between two locations.
	 * 
	 * @param a the first location
	 * @param b the first location
	 * @return the number of steps between a and b if you only move in the four cardinal directions.
	 */
	private int distance(Location a, Location b) {
		return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
	}
}