package game.items;

import edu.monash.fit2099.engine.WeaponItem;

/**
 * This class creates a Laser Gun object to make 80 damages on the target player shot
 */
public class LaserGun extends WeaponItem{
    /**
     * Constructor of the laser gun
     * @param name String name of the laser gun
     */
    public LaserGun(String name) {
        super(name, '4', 80, "biu");
    }

}
