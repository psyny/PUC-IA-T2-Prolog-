package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import animation.AnimatedSprite;
import data.Singletons;
import dataTypes.*;

public class IsoGrid extends JLayeredPane {
	public static DVector2D isoTileSize = new DVector2D( 162 , 162 ); // 82 82 		
	public static DVector2D baseVectorX = new DVector2D( 14 , -7 , true );
	public static DVector2D baseVectorY  = new DVector2D( 14 , +7 , true );

	public static DVector2D isoGridSize = new DVector2D( 0 , 0 );
	
	public static IVector2D origin = new IVector2D( 0 , 0 );
	public static IVector2D border = new IVector2D( 100 , 100 );
	
	private Grid baseGrid;
	
	
	public IsoGrid( Grid gameGrid ) { 
		this.baseGrid = gameGrid;
		
		this.setVisible( true );
		this.setOpaque( false );
		this.setLayout( null );
		
		this.setOpaque( true );
		this.setBackground( new Color(0,0,0) );
		
		IVector2D canonSize = IsoGrid.getOrtogonalSize();
		this.setBounds(0,0, canonSize.x ,  canonSize.y );
		
		IVector2D offSet = IsoGrid.getOffset(this.baseGrid.rows -1);
		
		boolean originSetFlag = false;
		
		// Populate Area
		for( ArrayList<Cell> alCell : gameGrid.cells ) {
			for( Cell cell : alCell ) {
				cell.tile = new SceneTile( );
				cell.tile.cell = cell;
				this.add( cell.tile );
				this.setLayer( cell.tile , cell.position.x - cell.position.y );
				
				IVector2D position = IsoGrid.getProjection( ( cell.position.x * IsoGrid.isoTileSize.x ) , ( cell.position.y * IsoGrid.isoTileSize.y ) , false );
				position.y += offSet.y;
				cell.tile.setLocation( (int)position.x , (int)position.y );
				
				if( originSetFlag == false ) {
					IsoGrid.origin.x = (int)position.x;
					IsoGrid.origin.y = (int)position.y + (cell.tile.paneSize.y / 2 );
					originSetFlag = true;
				}
				
				if( Singletons.debugLevel > 0 ) {
					JLabel label = new JLabel( cell.position.x + " | " + cell.position.y );
					label.setBounds(0 , 0 , 100, 15 );
					label.setLocation( (int)position.x + 60 , (int)position.y + 30 );
					label.setVisible(true);
					this.add( label );
					this.setLayer( label , 1000 );
				}
				
				
			}
		}
	}
	
	public static IVector2D getProjection( double x , double y ) {		
		return IsoGrid.getProjection( x , y , true );
	}
	
	public static IVector2D getProjection( double x , double y , boolean originEffected ) {
		return IsoGrid.getProjection(x, y, originEffected , true );
	}
	
	public static IVector2D getProjection( double x , double y , boolean originEffected , boolean borderEffected) {
		IVector2D projection;
		if( originEffected == true ) {
			projection = new IVector2D( IsoGrid.origin.x , IsoGrid.origin.y );
		} else {
			projection = new IVector2D( 0 , 0 );
		}
		
		if( borderEffected == true ) {
			projection.x += IsoGrid.border.x;
			projection.y += IsoGrid.border.y;
		}		
		
		projection.x += ( ( x * IsoGrid.baseVectorY.x ) + ( y * IsoGrid.baseVectorX.x ) );
		projection.y += ( ( x * IsoGrid.baseVectorY.y ) + ( y * IsoGrid.baseVectorX.y ) );
		
		return projection;
	}
	
	public static IVector2D getTileCenterProjection( int x , int y ) {
		return IsoGrid.getTileCenterProjection(x, y , false);
	}
	
	public static IVector2D getTileCenterProjection( int x , int y , boolean borderEffected ) {
		IVector2D projection = IsoGrid.getProjection( ( (x+0.5) * IsoGrid.isoTileSize.x ) , ( (y+0.5) * IsoGrid.isoTileSize.y ) , true , borderEffected  );
		
		return projection;
	}
	
	public static DVector2D getTileCenterRealPosition( int x , int y ) {
		DVector2D rPos = new DVector2D( 0 , 0 );
		
		// IVector2D centerPosition = IsoGrid.getProjection( ( x + 0.5 ) * IsoGrid.isoTileSize.x ,  ( y + 0.5 ) * IsoGrid.isoTileSize.y , true , false );
		// rPos.x = centerPosition.x ;
		// rPos.y = centerPosition.y ;
		rPos = new DVector2D( ( x + 0.5 ) * IsoGrid.isoTileSize.x ,  ( y + 0.5 ) * IsoGrid.isoTileSize.y );
		
		return rPos;
	}
	
	public static IVector2D getOrtogonalSize( ) {
		IVector2D canonSize1 = IsoGrid.getProjection( ( 1 * IsoGrid.isoTileSize.x ) , ( 0 * IsoGrid.isoTileSize.y ) , false );
		
		IVector2D conSize = new IVector2D( 0 , 0 );
		conSize.x = (int)( canonSize1.x * ( Singletons.gameGrid.cols + Singletons.gameGrid.rows ) ) + ( IsoGrid.border.x * 2 );
		conSize.y = (int)( canonSize1.y * ( Singletons.gameGrid.cols + Singletons.gameGrid.rows ) ) + ( IsoGrid.border.x * 2 );
		
		return conSize;
	}
	
	public static IVector2D getOffset( int rows ) {
		IVector2D canonSize1 = IsoGrid.getProjection( ( 1 * IsoGrid.isoTileSize.x ) , ( 0 * IsoGrid.isoTileSize.y ) , false , false );
		
		IVector2D conSize = new IVector2D( 0 , 0 );
		
		conSize.x = 0;
		conSize.y = (int)( canonSize1.y * rows ) + 0;
		
		return conSize;
	}
}
