package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import animation.*;
import data.Singletons;
import dataTypes.*;

@SuppressWarnings("serial")
public class SceneTile extends JLayeredPane {
	public static IVector2D paneSize = new IVector2D( 300 , 170 );
	
	private ArrayList<TileSetSprite> tiles;
	
	public Cell cell;
	
	public SceneTile( ) {
		this.setVisible( true );
		this.setOpaque( false );
		this.setLayout( null );
		this.setBounds(0,0,paneSize.x,paneSize.y);
		this.setPreferredSize( new Dimension( paneSize.x , paneSize.y ) );
		
		tiles = new ArrayList<TileSetSprite>();
		
		TileSetSprite spr;
		String spriteFileName;
		
		// Ground Tiles
		spriteFileName = "dirtTiles.txt";
		int subTiles = 4;
		IVector2D offset = IsoGrid.getOffset( 1 );
		
		for( int x = 0 ; x < subTiles ; x++ ) {
			for( int y = 0 ; y < subTiles ; y++ ) {
				IVector2D projection = IsoGrid.getProjection( IsoGrid.isoTileSize.x * x , IsoGrid.isoTileSize.y * y , false , false );

				spr = new TileSetSprite( spriteFileName );
				spr.setLocation( ( projection.x / subTiles ) + offset.x , ( projection.y / subTiles ) + offset.y );
				spr.insertInto( this );	
				
				if( Math.random()*100 < 95 ) {
					spr.setRandomTile(1);
				} else {
					spr.setRandomTile(2);
				}
				
				this.setLayer( spr, x - y );
				this.tiles.add( spr );
			}
		}
		
	}
	
}
