package data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import dataTypes.*;

public class MapLoader {
	public MapLoader() {
		MapLoader.loadFile();
	}
	
	public static void loadFile() {
	    Scanner file;
	    file = null;
	    
	    String fileName = "map.txt";
	    
	    // Load Map data
	    try {
	        file = new Scanner(Paths.get( Singletons.dataDirectory + fileName ));
	    } catch (FileNotFoundException ex) {
	    	System.out.print("Arquivo "+fileName+" Nao Encontrado!\n");
	        //Logger.getLogger(Skills.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException e) {
	    	System.out.println("Abertura de "+fileName+" falhou!");
			e.printStackTrace();
		}	
	    
	    // Prepare the list
	    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
	    
	    // Load Varibles
	    String line;
	    boolean breakFlag = false;
	    int waterQtd = 0;
	    
	    // Load Data
    	while( file.hasNextLine() ) {
    		// Ignore empty lines
    		line 		= file.nextLine().trim();
    		breakFlag 	= false;
	    	while( line.length() < 1 ) {
	    		if( file.hasNextLine() == false ) {
	    			breakFlag = true;
	    			break;
	    		} else {
	    			line = file.nextLine().trim();
	    		}
	    	}

	    	
	    	// End of file marker 
	    	if( breakFlag == true ) {
	    		break;
	    	}
	    	
	    	// Create new entry
	    	ArrayList<Cell> newRow = new ArrayList<Cell>();
	    	cells.add( 0 ,  newRow );
	    	
	    	for( int i = 0 ; i < line.length() ; i++ ) {
	    		Cell newCell = new Cell( 0 , 0 );
	    		newRow.add( newCell );
	    		
	    		switch( line.charAt(i) ) {
	    		case 'W':
	    			newCell.type = CellType.WALL;
	    			break;
	    			
	    		case 'd':
	    			newCell.type = CellType.ENEMY;
	    			break;
	    			
	    		case 'D':
	    			newCell.type = CellType.BOSS;
	    			break;
	    			
	    		case 'T':
	    			newCell.type = CellType.CYCLONE;
	    			break;
	    			
	    		case 'P':
	    			newCell.type = CellType.LANDMINE;
	    			break;
	    			
	    		case 'O':
	    			newCell.type = CellType.WATER;
	    			waterQtd++;
	    			break;
	    			
	    		case 'U':
	    			newCell.type = CellType.GAS;
	    			break;
	    			
	    		case 'H':
	    			newCell.type = CellType.HERO;
	    			newCell.discovered = true;
	    			break;
	    		}
	    	} // Great for end
    	} // Great While End
	    file.close();
	    
    	// Adjust Coords
		for( int y = 0 ; y < cells.size() ; y++ ) {
			ArrayList<Cell> newRow = new ArrayList<Cell>();
			
			for( int x = 0 ; x < cells.get(y).size() ; x++ ) {
				Cell current = cells.get(y).get(x);
				current.position.x = x;
				current.position.y = y;
				
				if( current.type == CellType.HERO ) {
					Singletons.heroPosition.x = x;
					Singletons.heroPosition.y = y;
				}
			}	
		}
	    
	    // Create the Grid Singleton
	    Singletons.gameGrid = new Grid( cells );
	    Singletons.gameGrid.waterQtd = waterQtd;
	}
}
