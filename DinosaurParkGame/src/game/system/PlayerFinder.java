package game.system;

import game.actors.Player;

/**
 * A class which helps to locate the Player
 */
public class PlayerFinder {
    /**
     * static player
     */
    private static Player player;

    /**
     * Constructor
     */
    public PlayerFinder() {
    }

    /**
     * constructor
     * @param player Player player
     */
    public PlayerFinder(Player player) {
        PlayerFinder.player = player;
    }

    /**
     * Getter of player
     * @return Player player
     */
    public Player getPlayer() {
        return player;
    }
}
