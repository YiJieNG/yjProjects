package game.actions;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;
import game.capabilities.AttackCapabilities;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.items.Corpse;

/**
 * Special Action for attacking other Actors. Extends Action.
 * @author Darren Yee
 * @version 1.0
 * @see edu.monash.fit2099.engine.Action
 * @see edu.monash.fit2099.interfaces.ActionInterface
 */
public class AttackAction extends Action {

	/**
	 * The Actor that is to be attacked
	 */
	protected Actor target;
	/**
	 * Random number generator
	 */
	protected Random rand = new Random();

	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public AttackAction(Actor target) {
		this.target = target;
	}

	/**
	 *
	 * @param actor The actor performing the action.
	 * @param map The map the actor is on.
	 * @return result if an attack has been executed
	 */

	@Override
	public String execute(Actor actor, GameMap map) {

		Weapon weapon = actor.getWeapon();
		// has a chance of missing
		//if (rand.nextBoolean()) {
			//return actor + " misses " + target + ".";
		//}
		// if the actor is an allosaur, set its damage to 20
		int damage = weapon.damage();
		if (actor.hasCapability(FoodCapability.ALLO)) {
			target.addCapability(AttackCapabilities.CANNOT_BE_ATTACKED);
			if (target.hasCapability(FoodCapability.STEG)) {
				damage = 20;
				if (actor.hasCapability(CanBreedCapability.BABY)) {
					actor.heal(10);
				} else {
					actor.heal(20);
				}
			} else if (target.hasCapability(FoodCapability.PTERO)){
				damage = 1000;
				actor.heal(100);
			}
		}
		// adds result if actor sucesfully attacked the target
		String result = actor + " " + weapon.verb() + " " + target + " for " + damage + " damage.";

		target.hurt(damage);
		if (!target.isConscious()) {
			// if the target is killed, create a corpse on the area and remove them while also dropping all of the items in their inventory
			Corpse corpse = new Corpse("dead " + target, '%');
			map.locationOf(target).addItem(corpse);

			
			Actions dropActions = new Actions();
			for (Item item : target.getInventory())
				dropActions.add(item.getDropAction());
			for (Action drop : dropActions)		
				drop.execute(target, map);
			map.removeActor(target);	
			
			result += System.lineSeparator() + target + " is killed.";
		}

		return result;
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " attacks " + target;
	}
}
