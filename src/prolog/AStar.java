package prolog;

import java.util.ArrayList;

import data.Singletons;
import dataTypes.*;

public class AStar {
	public static AStarPath getPath( IVector2D Destination ) {
		AStarPath aspath = new AStarPath();
		Grid grid = Singletons.gameGrid;
		Cell tCell = grid.getCell( Singletons.heroPosition.x , Singletons.heroPosition.y );
		Cell dCell = grid.getCell( Destination.x , Destination.y );
		Cell cCell = null;
		
		// Starting Cell Options
		tCell.ASData.predecessor 		= null;
		tCell.ASData.direction 			= Singletons.heroDirection;
		tCell.ASData.costToEnter 		= 0;
		tCell.ASData.accumulatedCost 	= 0;
		tCell.ASData.type				= 1;
		
		// Build Path
		ArrayList<Cell> frontier = new ArrayList<Cell>();
		while( tCell != dCell ) {
			AStar.addFrontier( frontier , tCell , dCell );
			if( frontier.size() < 1 ) break;
			tCell = AStar.getBestCandidate( frontier );
		}
		
		// Check if pathExists
		if( tCell != dCell ) {
			return null;
		}
		
		// Build Path List
		while( tCell != null ) {
			aspath.cellList.add( 0 , tCell );
			tCell = tCell.ASData.predecessor;
		}
		
		// Build Command List
		for( int i = 0 ; i < (aspath.cellList.size()-1) ; i++ ) {
			// Find the needed commands
			cCell = aspath.cellList.get(i+1);
			tCell = cCell.ASData.predecessor;
			
			for( int j = 0 ; j < AStar.getTurnCost( tCell.ASData.direction , cCell.ASData.direction ) ; j++ ) {
				aspath.commandList.add( Commands.TURN );
			}
			aspath.commandList.add( Commands.MOVE );
		}
		
		
		return aspath;
	}
	
	
	
	private static void addFrontier( ArrayList<Cell> frontier , Cell center , Cell destiny ) {
		Grid grid = Singletons.gameGrid;
		
		if( center == null ) {
			return;
		}
		int x = center.position.x;
		int y = center.position.y;
		
		Cell nei;

		nei = grid.getCell(x+1, y);
		AStar.addCellToList( frontier , nei , center , destiny , 2 );
		
		nei = grid.getCell(x-1, y);
		AStar.addCellToList( frontier , nei , center , destiny , 4 );
		
		nei = grid.getCell(x, y+1);
		AStar.addCellToList( frontier , nei , center , destiny , 1 );

		nei = grid.getCell(x, y-1);
		AStar.addCellToList( frontier , nei , center , destiny , 3 );
	}
	
	private static int getTurnCost( int heroDirection , int destinationDirection ) {
		if( destinationDirection < heroDirection  ) {
			destinationDirection += 4;
		}
		return ( destinationDirection - heroDirection );
	}
	
	private static int getManhattan( IVector2D origin , IVector2D destiny ) {
		return ( Math.abs( origin.x - destiny.x ) + Math.abs( origin.y - destiny.y ) );
	}
	
	private static void addCellToList( ArrayList<Cell> list , Cell cell , Cell predecessor , Cell finalDestination , int cellRelativeDirection ) {
		// Cells to avoid:
		if( cell == null || cell.ASData.type != 0 ) return;
		if( cell.discovered == false && cell != finalDestination ) return;
		if( cell.destroyed == false ) {
			switch( cell.type ) {
				case BOSS:
				case ENEMY:
				case CYCLONE:
				case LANDMINE:
					return;
			}
		}
		

		// Calculate new cell data
		int tCost = AStar.getTurnCost( Singletons.heroDirection , cellRelativeDirection );
		int tHeu = AStar.getManhattan( cell.position , finalDestination.position );
		
		// Penalize turns
		tCost = tCost * 4;

		// Cell new Specs
		cell.ASData.predecessor = predecessor;
		cell.ASData.costToEnter = tCost;
		cell.ASData.accumulatedCost = predecessor.ASData.accumulatedCost + cell.ASData.costToEnter;
		cell.ASData.heuristicFinalCost = tHeu + cell.ASData.accumulatedCost;
		cell.ASData.type = 2;	
		cell.ASData.direction = cellRelativeDirection;

		list.add( cell );
	}
	
	private static Cell getBestCandidate( ArrayList<Cell> list ) {
		Cell best = list.get(0);
		
		for( Cell cell : list ) {
			if( cell.ASData.heuristicFinalCost < best.ASData.heuristicFinalCost ) {
				best = cell;
			}
		}
		
		list.remove( best );
		
		return best;
	}
 } 
