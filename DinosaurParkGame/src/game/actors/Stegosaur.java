package game.actors;


import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;
import game.behaviours.FindItemBehaviour;
import game.capabilities.AttackCapabilities;
import game.capabilities.CanBreedCapability;
import game.capabilities.FoodCapability;
import game.ground.Bush;
import game.ground.Tree;
import game.items.Corpse;
import game.items.Fruit;

import java.util.Random;

/**
 * A herbivorous dinosaur. Extends the dinosaur abstract class
 *
 */
public class Stegosaur extends Dinosaur {
	/**
	 * Unconscious round for the stegosaur
	 */
	public int notConsciousRound = 0;
	/**
	 * Tracker to track rounds until it is allowed to be attacked by the Allosaur again
	 */
	private int tracker = 20;
	/**
	 * Fruit object
	 */
	private final Fruit fruit = new Fruit();

	/**
	 * Constructor.
	 * All Stegosaurs are represented by a 's' and have 100 hit points.
	 *
	 * @param name the name of this Stegosaur
	 */
	public Stegosaur(String name, int hitPoint) {
		super(name, 'S', hitPoint);
		if (this.name.equals("male") || this.name.equals("female")){
			setSpecificGender();
			this.name = "Stegosaur";
		}
		else{
			setGender();
		}
		if (this.hasCapability(CanBreedCapability.BABY)){
			this.maxHitPoints = 10;
		}
		else{
			this.maxHitPoints = 100;

		}
		this.addCapability(FoodCapability.STEG);
		setWaterLevel();
	}

	/**
	 * GetAllowableAction from Actor class
	 * @param otherActor the Actor that might be performing attack
	 * @param direction  String representing the direction of the other Actor
	 * @param map        current GameMap
	 * @return the list of actions that are valid
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
		if (map.locationOf(this).getGround() instanceof Bush) {
			Bush bush = (Bush) map.locationOf(this).getGround();
			if (bush.getFruits().size() > 0) {
				bush.getFruits().remove(0);
				this.heal(10);
			}
		} else if (map.locationOf(this).getGround() instanceof Tree) {
			if (this.hasCapability(FoodCapability.STEG)) {
				if (map.locationOf(this).getItems().contains(fruit)) {
					map.locationOf(this).getItems().remove(fruit);
					this.heal(10);
				}
			}
		}
	}

	/**
	 *Tells the Stegosaur what to execute every turn
	 * @param actions    collection of possible Actions for this Actor
	 * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
	 * @param map        the map containing the Actor
	 * @param display    the I/O object to which messages may be written
	 * @return Action action that actor executes at this round
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		if (this.hasCapability(AttackCapabilities.CANNOT_BE_ATTACKED)) {
			--tracker;
			if (tracker <= 0) {
				this.hasCapability(AttackCapabilities.CANNOT_BE_ATTACKED);
			}
		}
		checkBaby(this);
		checkEgg(this,map);
		if (isConscious()&&waterIsConscious()) {
			// If the dinosaur is conscious, we check its thirst and make sure it is pointed to the direction of the lake in order to find lake.
			notConsciousRound = 0;
			checkThirst();
			findWater(map);
			if (hasCapability(FoodCapability.THIRSTY)){
				display.println("Stegosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting thirsty!");
				FindItemBehaviour findItem = new FindItemBehaviour(2,'w');
				Action find = findItem.getAction(this, map);
				if (find != null)
					return find;
				return new DoNothingAction();
			}
			hitPoints--;
			if (hitPoints <= 30) {
				display.println("Stegosaur at (" + map.locationOf(this).x() + "," + map.locationOf(this).y() + ") is getting hungry!");
				// calls find food
				findFood(map);
				FindItemBehaviour findItem = new FindItemBehaviour(2,'f');
				Action find = findItem.getAction(this, map);
				if (find != null)
					return find;
				return new DoNothingAction();
				// if hp stuck in the middle, randomize their next action
			} else if (hitPoints < 70 || hasCapability(CanBreedCapability.FALSE)) {
				breedingCooldown(this);
				Random r = new Random();
				int random = r.nextInt();
				if (random > 50){
					FindItemBehaviour findItem = new FindItemBehaviour(2,'w');
					Action find = findItem.getAction(this, map);
					if (find != null)
						return find;
					return new DoNothingAction();
				}
				else{
					// else, find food to eat
					findFood(map);
					FindItemBehaviour findItem = new FindItemBehaviour(2,'f');
					Action find = findItem.getAction(this, map);
					if (find != null)
						return find;
					return new DoNothingAction();
				}
			} else {
				return findNearest(map, 1);
			}
		}

		if (notConsciousRound < 20) {
			notConsciousRound++;
			display.println(this.name + "is unconscious for "+ notConsciousRound);
			return new DoNothingAction();
		}
		// turn them into corpse once they are no conscious
		Item corpse = new Corpse("Stegosaur",'%');
		display.println(this.name + "is dead");
		corpse.addCapability(FoodCapability.STEG);
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



