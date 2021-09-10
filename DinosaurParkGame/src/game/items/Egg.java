package game.items;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.system.PlayerFinder;
import game.actors.*;
import game.capabilities.CanBreedCapability;

/**
 * Class to handle eggs of dinosaurs. Extends Item class in engine
 * @author Darren Yee, Ng Yi Jie
 */

public class Egg extends PortableItem {
    private final Actor type;
    private int round;
    private final Display display = new Display();

    /***
     * Constructor for egg
     *  @param parent the parent of this egg
     */
    public Egg(Actor parent) {
        super("Egg", 'O');
        type = parent;
        round = 0;
    }
    /**
     * Tick method for egg to experience time passing in the game, for every round, the egg will check whether it is time to hatch and become a baby version of the specific dinosaur
     * @param currentLocation represents the current location this egg is at
     */
    @Override
    public void tick(Location currentLocation) {
        PlayerFinder playerFinder = new PlayerFinder();
        Player player = playerFinder.getPlayer();
        super.tick(currentLocation);
        GameMap gameMap = currentLocation.map();
        //finds the location of the player in order to add their EcoPoint once the egg has been hatched
        for (int i : gameMap.getXRange()) {
            for (int j : gameMap.getYRange()) {
                gameMap = currentLocation.map();
                if (gameMap.at(i, j).getActor() instanceof Player) {
                    player = (Player) gameMap.at(i, j).getActor();
                }
            }
        }
        // checks whether each egg is available for hatching
        if (round <= 5 && (type instanceof Stegosaur|| type instanceof Pterodactyl)) {
            round++;
        } else if (round <= 10 && type instanceof Brachiosaur) {
            round++;
        } else if (round <= 15 && type instanceof Allosaur) {
            round++;
            // once round has been reached, remove the egg from the location in the map and spawn a baby dinosaur at the currentLocation
        } else {
            if (type instanceof Stegosaur) {
                Stegosaur stegosaur = new Stegosaur("Stegosaur", 10);
                stegosaur.addCapability(CanBreedCapability.BABY);
                stegosaur.setAge(15);
                if (currentLocation.getActor() == null) {
                    display.println("Stegosaur egg hatched!");
                    currentLocation.addActor(stegosaur);
                    player.addEcoPoint(100);
                    currentLocation.removeItem(this);
                }
            } else if (type instanceof Brachiosaur) {
                Brachiosaur brachiosaur = new Brachiosaur("Brachiosaur", 10);
                brachiosaur.addCapability(CanBreedCapability.BABY);
                brachiosaur.setAge(15);
                if (currentLocation.getActor() == null) {
                    display.println("Brachiosaur egg hatched!");
                    currentLocation.addActor(brachiosaur);
                    player.addEcoPoint(1000);
                    currentLocation.removeItem(this);
                }
            } else if (type instanceof Allosaur) {
                Allosaur allosaur = new Allosaur("Allosaur", 20);
                allosaur.addCapability(CanBreedCapability.BABY);
                allosaur.setAge(15);
                if (currentLocation.getActor() == null) {
                    display.println("Allosaur egg hatched!");
                    currentLocation.addActor(allosaur);
                    player.addEcoPoint(1000);
                    currentLocation.removeItem(this);
                }
            } else if (type instanceof Pterodactyl) {
                Pterodactyl pterodactyl = new Pterodactyl("Pterodactyl", 20);
                pterodactyl.addCapability(CanBreedCapability.BABY);
                pterodactyl.setAge(15);
                if (currentLocation.getActor() == null) {
                    display.println("Pterodactyl egg hatched!");
                    System.out.println("yessssss");
                    currentLocation.addActor(pterodactyl);
                    player.addEcoPoint(100);
                    currentLocation.removeItem(this);
                }
            }

        }
    }
}
