package game.behaviours;

import edu.monash.fit2099.engine.*;
import edu.monash.fit2099.engine.Action;
import game.capabilities.AttackCapabilities;
import game.capabilities.FoodCapability;
import game.capabilities.GenderCapability;
import game.ground.Lake;
import game.items.Corpse;
import game.ground.Bush;
import game.ground.Tree;
import game.items.Egg;
import game.items.Fruit;

import javax.swing.*;
import java.util.List;

/**
 * A class that figures out a MoveAction that will move the actor one step
 * closer to a target Actor.
 * @author Ng Yi Jie
 * @version 1.0
 */
public class FindItemBehaviour implements Behaviour {
    /**
     * target to store the target type
     */
    private int dinoType;
    /**
     * gamemap of the actor
     */
    private GameMap gameMap;
    /**
     * temp variable to store the current nearest location to compare and choose the optimal one if multiple target found
     */
    private int nearest = 101;
    /**
     * location of target searched
     */
    private Location targetLocation;
    /**
     * list to act as a temp list
     */
    private List<Item> list;
    private Actor target;
    private Boolean found = false;
    private char foodWater;

    /**
     * An constructor which takes input of types to determine the location of the target and follow the target
     * @param types int type 1 = Brachiosaur, type 2 = Stegosaur, type 3 = Allosaur
     */
    public FindItemBehaviour(int types, char foodWater) {
        this.dinoType = types;
        this.foodWater = foodWater;
    }
    /**
     * If the condition is met, the method is called to search through the whole map and find the ground base on the
     * dinosaur type. If there is a fruit inside the valid location, choose the optimal location and step forward the
     * target.
     * @param actor the Actor acting
     * @param map the GameMap containing the Actor
     * @return Action move action if there is an target located else return null
     */
    @Override
    public Action getAction(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        // loop through the map
        for (int i : map.getXRange()) {
            for (int j : map.getYRange()) {
                gameMap = location.map();
                // if it is a stegosaur
                if (dinoType == 2 && foodWater == 'f') {
                    // if the ground is a bush and the bush has fruit, assign the location if it is current nearest one
                    if (gameMap.at(i, j).getGround() instanceof Bush) {
                        Bush bush = (Bush) gameMap.at(i, j).getGround();
                        if (bush.hasCapability(FoodCapability.HAS_FRUIT)) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                    // if the ground is a tree and the ground has fruit, assign the location if it is current nearest one
                    else if (gameMap.at(i, j).getGround() instanceof Tree) {
                        Location groundLocation = new Location(gameMap, i, j);
                        for (int z = 0; z < groundLocation.getItems().size(); z++) {
                            if (groundLocation.getItems().get(z) instanceof Fruit) {
                                int currentDistance = distance(location, groundLocation);
                                if (currentDistance < nearest) {
                                    nearest = currentDistance;
                                    targetLocation = groundLocation;
                                }
                            }
                        }
                    }
                }
                // if it is a brachiosaur
                else if (dinoType == 1 && foodWater == 'f') {
                    // if the ground is a tree and the tree has fruit, assign the location if it is current nearest one
                    if (gameMap.at(i, j).getGround() instanceof Tree) {
                        Tree tree = (Tree) gameMap.at(i, j).getGround();
                        if (tree.hasCapability(FoodCapability.HAS_FRUIT)) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                }
                // if it is a allosaur
                else if (dinoType == 3 && foodWater == 'f') {
                    // if the ground has a corpse, assign the location if it is a current nearest one
                    list = gameMap.at(i, j).getItems();
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k) instanceof Corpse) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                found = false;
                                targetLocation = groundLocation;
                            }
                        } else if (list.get(k) instanceof Egg) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                found = false;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                    if (gameMap.at(i, j).getActor() != null) {
                        if (gameMap.at(i, j).getActor().hasCapability(FoodCapability.STEG) ||gameMap.at(i, j).getActor().hasCapability(FoodCapability.PTERO) && !gameMap.at(i,j).getActor().hasCapability(AttackCapabilities.CANNOT_BE_ATTACKED)) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                found = true;
                                target = gameMap.at(i, j).getActor();
                            }
                        }
                    }
                }
                else if (dinoType == 4 && foodWater == 'f') {
                    list = gameMap.at(i, j).getItems();
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k) instanceof Corpse) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            if (currentDistance < nearest) {
                                nearest = currentDistance;
                                found = false;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                    if (gameMap.at(i, j).getGround() instanceof Lake) {
                        Lake lake = (Lake) gameMap.at(i, j).getGround();
                        if (lake.hasCapability(FoodCapability.HAS_FISH)) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            Location check = new Location(gameMap,i-1,j);
                            if (currentDistance < nearest && groundLocation.getActor() == null && check.getActor() == null) {
                                nearest = currentDistance;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                }
                else if (foodWater == 't') {
                    if (gameMap.at(i, j).getGround() instanceof Tree) {
                        Location groundLocation = new Location(gameMap, i, j);
                        int currentDistance = distance(location, groundLocation);
                        Location check = new Location(gameMap,i-1,j);
                        Location check1 = new Location(gameMap,i+1,j);
                        if (currentDistance < nearest && groundLocation.getActor() == null && check.getActor() == null && check1.getActor() == null) {
                            nearest = currentDistance;
                            targetLocation = groundLocation;
                        }
                    }
                }
                else if (foodWater == 'w'){
                    if (gameMap.at(i, j).getGround() instanceof Lake) {
                        Lake lake = (Lake)gameMap.at(i, j).getGround();
                        if (lake.getCapacity()>0 && gameMap.at(i, j)!=location) {
                            Location groundLocation = new Location(gameMap, i, j);
                            int currentDistance = distance(location, groundLocation);
                            Location check = new Location(gameMap,i-1,j);
                            Location check1 = new Location(gameMap,i+1,j);
                            if (currentDistance < nearest && groundLocation.getActor() == null && check.getActor() == null && check1.getActor() == null) {
                                nearest = currentDistance;
                                targetLocation = groundLocation;
                            }
                        }
                    }
                }
            }
        }

        if (found) {
            if (nearest >= 2) {
                FollowBehaviour followBehaviour = new FollowBehaviour(target);
                Action follow = followBehaviour.getAction(actor, map);
                if (follow != null)
                    return follow;
                return new DoNothingAction();
            }
        } else {
            // if the target or the actor is invalid, return null
            if (!map.contains(actor) || targetLocation == null) {
                return null;
            }
            // else return move actor action base on the targetLocation assigned
            Location here = map.locationOf(actor);
            Location there = targetLocation;

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

        }
        return null;
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
