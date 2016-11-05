package debug;

import data.*;
import dataTypes.*;
import prolog.*;

public class DebugMap {
	static boolean debugMode = true;
	
	
	
	public static void discoverEntireMap() {
		if( DebugMap.debugMode == false ) return;
		
		Grid grid = Singletons.gameGrid;
		
		for( int x = 0 ; x < grid.cols ; x++ ) {
			for( int y = 0 ; y < grid.rows ; y++ ) {
				grid.getCell(x, y).discovered = true;
			}
		}
	}
	
	
	public static void printAStarPath( AStarPath asp ) {
		System.out.println( "Cells: ");
		for( Cell cell : asp.cellList ) {
			System.out.println( cell.position.x + " , " + cell.position.y );
		}
			
		System.out.println("Commands: ");
		
		int i = 1;
		for( Commands cmd : asp.commandList ) {		
			System.out.println( i + ":\t" + Translations.getCommandString( cmd ) );
			i++;
		}
		
		

	}
}
