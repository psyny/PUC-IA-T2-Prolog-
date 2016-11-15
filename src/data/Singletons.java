package data;

import animation.*;
import dataTypes.*;
import user_interface.*;

public class Singletons {
	public static int		debugLevel 			= 0;
	public static String 	dataDirectory 		= "map\\";
	public static String 	soundsDirectory		= "sfx\\";

	// OTHER Elements
	public static Camera		gameCamera;
	public static IVector2D		gameCameraArea = new IVector2D( 1010 , 710 );
	public static GameScene		actorScene = null;
	public static IsoGrid 		sceneGrid;
	public static UpdateLinker 	updateLinker = null;	
	public static ActorHero		hero = null;	
	public static ActorStorm 	stormGroup = null;
	
	// State of the Game
	public static boolean 	fogUndiscovery 	= true;
	public static boolean 	centerOnHero 	= true;
	public static boolean	showSensors 	= true;
	public static boolean	hideDiscoveredSensors = true;
	public static boolean	showGrid		= false;
	public static boolean	paused			= true;
	
	public static boolean	victory			= false;
	public static boolean	death			= false;
	
	
	
	// Sync with PROLOG
	public static Grid 		gameGrid;
	public static int 		heroLife		= 100;
	public static int		heroScore		= 0;
	public static int		heroAmmo		= 5;	
	public static IVector2D	heroPosition	= new IVector2D( 0, 0 );
	public static int		heroDirection	= 1;
	public static int		waterCatched	= 0;
	
	
	static public void endProlog() {
		Singletons.paused = true;
		Singletons.centerOnHero = false;
	}
}
