package game.ground;

import edu.monash.fit2099.engine.*;
import game.actors.*;
import game.items.Egg;
import game.items.Fruit;
import game.items.LaserGun;
import game.items.MeatMealKit;
import game.items.VegetarianMealKit;

/**
 * Creates the vending machine location in the map. Represented by the display char H. It handles the creation of
 * objects and adding them to the player's inventory once it is purchased.
 * @author Darren Yee, Ng Yi Jie
 * @see edu.monash.fit2099.engine.Ground
 * @see game.actors.Player
 */
public class VendingMachine extends Ground {
    /**
     * Empty constructor
     */
    public VendingMachine() {
        super('H');
    }

    /**
     * This method checks whether the input option(item wish to buy) is valid and the ecoPoints is enough for the item
     * or not
     * @param player Player the player actor
     * @param option int item option wish to buy
     * @return Boolean true if valid purchase action, else false
     */
    public Boolean canPurchase(Player player, int option){
        int points = player.getEcoPoint();
        // 1. fruit
        if (option == 1 && points >= 30){
            Fruit fruit = new Fruit();
            player.minusEcoPoint(30);
            player.addItemToInventory(fruit);
            return true;
        }
        // 2. vegetarian meal kit
        else if(option == 2 && points >= 100){
            VegetarianMealKit vegetarianMealKit = new VegetarianMealKit();
            player.minusEcoPoint(100);
            player.addItemToInventory(vegetarianMealKit);
            return true;
        }
        // 3. meat meal kit
        else if(option == 3 && points >= 500){
            MeatMealKit meatMealKit = new MeatMealKit();
            player.minusEcoPoint(500);
            player.addItemToInventory(meatMealKit);
            return true;
        }
        // 4. stegosaur egg
        else if(option == 4 && points >= 200){
            Stegosaur stegosaur = new Stegosaur("Stegosaur",10);
            Egg egg = new Egg(stegosaur);
            player.minusEcoPoint(200);
            player.addItemToInventory(egg);
            return true;
        }
        // 5. brachiosaur egg
        else if(option == 5 && points >= 500){
            Brachiosaur brachiosaur = new Brachiosaur("Brachiosaur",10);
            Egg egg = new Egg(brachiosaur);
            player.minusEcoPoint(500);
            player.addItemToInventory(egg);
            return true;
        }
        // 6. allosaur egg
        else if(option == 6 && points >= 1000){
            Allosaur allosaur = new Allosaur("Allosaur",10);
            Egg egg = new Egg(allosaur);
            player.minusEcoPoint(1000);
            player.addItemToInventory(egg);
            return true;
        }
        else if(option == 7 && points >= 300){
            Pterodactyl pterodactyl = new Pterodactyl("Pterodactyl",10);
            Egg egg = new Egg(pterodactyl);
            player.minusEcoPoint(300);
            player.addItemToInventory(egg);
            return true;
        }
        // 7. laser gun
        else if(option == 8 && points >= 500){
            LaserGun laserGun = new LaserGun("Laser Gun");
            player.minusEcoPoint(500);
            player.addItemToInventory(laserGun);
            return true;
        }
        else{
            return false;
        }
    }

}
