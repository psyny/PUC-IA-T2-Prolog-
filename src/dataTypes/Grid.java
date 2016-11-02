package dataTypes;

import java.util.ArrayList;

public class Grid {
	public ArrayList<ArrayList<Cell>> cells;
	
	public int rows;
	public int cols;
	
	public IVector2D cellDimensions = new IVector2D( 20 , 20 );
	
	public Grid( int rows , int cols ) {
		this.rows = rows;
		this.cols = cols;
		
		for( int y = 0 ; y < rows ; y++ ) {
			ArrayList<Cell> newRow = new ArrayList<Cell>();
			cells.add( newRow );
			for( int x = 0 ; x < cols ; x++ ) {
				newRow.add( new Cell( x , y ) );
			}	
		}
	}
	
	public Grid( ArrayList<ArrayList<Cell>> cells ) {
		this.cells = cells;
		this.rows = cells.size();
		this.cols = cells.get(0).size();
	}
	
	public boolean isInGrid( int x , int y ) {
		if( x < 0 ) {
			return false;
		}
		if( x >= this.cols ) {
			return false;
		}
		if( y < 0 ) {
			return false;
		}
		if( y >= this.rows ) {
			return false;
		}
		return true;
	}
	
	public Cell getCell( int x , int y ) {
		if( this.isInGrid(x, y) == false ) {
			return null;
		}
		
		return this.cells.get(y).get(x);
	}
	
	public ArrayList<Cell> getNeighbors( int x , int y ) {
		ArrayList<Cell> neighbors = new ArrayList<Cell>();

		Cell center = this.getCell( x , y );
		if( center == null ) {
			return neighbors;
		}
		
		Cell nei;
		
		nei = this.getCell(x+1, y);
		if( nei != null ) {
			neighbors.add( nei );
		}
		
		nei = this.getCell(x-1, y);
		if( nei != null ) {
			neighbors.add( nei );
		}
		
		nei = this.getCell(x, y+1);
		if( nei != null ) {
			neighbors.add( nei );
		}

		nei = this.getCell(x, y-1);
		if( nei != null ) {
			neighbors.add( nei );
		}		
		
		return neighbors;
	}
}
