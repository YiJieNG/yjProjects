package game.system;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import edu.monash.fit2099.engine.*;
import game.actors.Brachiosaur;
import game.actors.Pterodactyl;
import game.actors.Stegosaur;
import game.ground.*;
import game.actors.Player;

/**
 * The main class for the Jurassic World game.
 *
 */
public class Application {
	/**
	 * Implementation of the application
	 * @param args String list arguments
	 */
	public static void main(String[] args) {
		boolean validChoice = false;
		boolean quitGame = false;
		while (!quitGame) {
			Display display = new Display();
			display.println("Welcome to Darren Yee Jie park. Select the gameMode you want\nGameMode 1: Challenge\nGameMode 2: SandBox\n3: Quit game :(\nChoice:");
			Scanner scanner = new Scanner(System.in);
			try {
				int option = scanner.nextInt();
				if (option == 1 || option == 2) {
					validChoice = true;
					World world = new World(new Display());

					FancyGroundFactory groundFactory = new FancyGroundFactory(new Dirt(), new Wall(), new Floor(), new Tree(), new VendingMachine(), new Lake());

					List<String> map = Arrays.asList(
							"................................................................................",
							"..................................................~.............................",
							".....#######....................................................................",
							".....#__H__#............~.......................................................",
							".....#_____#....................................................................",
							".....###.###....................................................................",
							"................................................................................",
							"......................................+++.......................................",
							".......................................++++.....................................",
							"...................................+++++.....................~..................",
							".....................................++++++.....................................",
							"......................................+++.......................................",
							".....................................+++........................................",
							"................................................................................",
							"............+++.................................................................",
							".............+++++.....................~........................................",
							"...............++....~.................~.................+++++..................",
							".............+++.....~~.............................++++++++....................",
							"............+++.......................................+++.~.....................",
							"................................................................................",
							".........................................................................++.....",
							"....~.................................................................~.++.++...",
							".........................................................................++++...",
							"..........................................................................++....",
							"................................................................................");
					GameMap gameMap = new GameMap(groundFactory, map);
					world.addGameMap(gameMap);

					Player player = new Player("Player", '@', 100);
					new PlayerFinder(player);
					if(option==2){
						player.setLimitRound(Double.POSITIVE_INFINITY);
					}
					else {
						boolean myValid = false;
						while (!myValid) {
							display.println("Please set your round limit:");
							try {
								Scanner scanner1 = new Scanner(System.in);
								Double round = scanner1.nextDouble();
								player.setLimitRound(round);
								myValid = true;
							} catch (InputMismatchException e){display.println("Please enter a valid round");}
						}
						myValid = false;
						while (!myValid) {
							display.println("Please set your EcoPoints target:");
							try {
								Scanner scanner2 = new Scanner(System.in);
								int targetPoints = scanner2.nextInt();
								player.setTargetPoints(targetPoints);
								myValid = true;
							} catch (InputMismatchException e){display.println("Please enter a valid target eco points");}
						}
					}
					player.addGameMaps(gameMap);
					world.addPlayer(player, gameMap.at(8, 4));
					//world.addPlayer(player, gameMap.at(0, 1));

					// Place a pair of stegosaurs in the middle of the map
					gameMap.at(21, 6).addActor(new Stegosaur("male",50));
					gameMap.at(42, 20).addActor(new Stegosaur("female",50));
					// Place two pairs of brachisaurs in the map
					gameMap.at(52, 13).addActor(new Brachiosaur("male",100));
					gameMap.at(40, 15).addActor(new Brachiosaur("female",100));
					gameMap.at(50, 13).addActor(new Brachiosaur("male",100));
					gameMap.at(35, 13).addActor(new Brachiosaur("female",100));

					gameMap.at(30,22).addActor(new Pterodactyl("male",100));
					gameMap.at(18,17).addActor(new Pterodactyl("female",100));


					List<String> myMap = Arrays.asList(
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"..............++...............................................++...............",
							"..............++...............................................++...............",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"....................................~~~.........................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"..........................++++++++++++++++++++++++..............................",
							"...........................+....................+...............................",
							"............................+..................+................................",
							".............................+~~~~~~~~~~~~~~~~+..................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................",
							"................................................................................");
					GameMap myWorld = new GameMap(groundFactory, myMap);
					world.addGameMap(myWorld);
					player.addGameMaps(myWorld);
					world.run();
				}else if (option == 3){
					validChoice = true;
					quitGame = true;
				}
				else {
					if(!validChoice) {
						display.println("Invalid choice, please make your choice either 1,2 or 3 : )");
					}
				}
			}catch (InputMismatchException e){
				display.println("Invalid choice, please make your choice either 1,2 or 3 : )");
			}
		}
	}
}
