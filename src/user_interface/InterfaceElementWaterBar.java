package user_interface;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.*;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementWaterBar extends InterfaceElement {
	private int catched = 0;
	private int max;
	
	public InterfaceElementWaterBar() {
		super( 150 , 30 );
		
		this.max = Singletons.gameGrid.waterQtd;
		
		this.setOpaque(false);

		IVector2D startingPos = new IVector2D( 0 , 0 );
		int offset = 28;
		for( int i = 0 ; i < 5 ; i++ ) {
			this.addSprite( "waterIcon.txt" , new IVector2D( startingPos.x + ( offset * i ) , startingPos.y ) , 0 , false );
		}
		
		
	}
	
	
	public void setWater( int w ) {
		this.catched = w;
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
	
		this.setWater( Singletons.waterCatched );
	
		for( int i = 0 ; i < this.spriteList.size() ; i++ ) {
			if( i < this.catched ) {
				((TileSetSprite)spriteList.get(i)).setTile(1, 1);		
			} else if ( i >= this.max ) {
				spriteList.get(i).setVisible(false);
			} else {
				((TileSetSprite)spriteList.get(i)).setTile(1, 2);
			}
		}
	} 
	
	
}
