package data;

import animation.*;
import dataTypes.*;
import user_interface.*;

public class Singletons {
	public static int		debugLevel 			= 0;
	public static String 	dataDirectory 		= "map\\";

	// OTHER Elements
	public static Camera	gameCamera;
	public static IsoGrid 	sceneGrid;
	public static UpdateLinker updateLinker = null;	
	public static ActorHero		hero = null;	
	
	// State of the Game
	public static boolean 	fogUndiscovery 	= true;
	public static boolean 	centerOnHero 	= true;
	public static boolean	showSensors 	= true;
	public static boolean	hideDiscoveredSensors = true;
	public static boolean	showGrid		= false;
	
	
	
	// Sync with PROLOG
	public static Grid 		gameGrid;
	public static int 		heroLife		= 100;
	public static int		heroScore		= 0;
	public static IVector2D	heroPosition	= new IVector2D( 0, 0 );
	public static int		heroDirection	= 1;
}
