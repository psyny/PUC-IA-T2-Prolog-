package user_interface;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import animation.*;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementWaterBar extends InterfaceElement {
	private int catched = 0;
	private int max;
	private JLabel qtdLabel;	
	
	public InterfaceElementWaterBar() {
		super( 150 , 30 );
		
		this.max = Singletons.gameGrid.waterQtd;
		
		this.setOpaque(false);

		IVector2D startingPos = new IVector2D( 0 , 0 );
		int offset = 28;
		int i = 0;
		for( i = 0 ; i < 5 ; i++ ) {
			this.addSprite( "waterIcon.txt" , new IVector2D( startingPos.x + ( offset * i ) , startingPos.y ) , 0 , false );
		}
		
		this.qtdLabel = new JLabel("");
		qtdLabel.setFont(new Font("Arial Black", Font.BOLD, 14));
		qtdLabel.setHorizontalAlignment(SwingConstants.LEFT);
		qtdLabel.setForeground(Color.WHITE);
		this.add( this.qtdLabel );
		this.qtdLabel.setBounds( startingPos.x + (int)( offset * 1.5 ) , 0 , 70 , 30 );		
	}
	
	
	public void setWater( int w ) {
		this.catched = w;
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
	
		this.setWater( Singletons.waterCatched );
	
		if( this.catched <= 5 ) { 
			this.qtdLabel.setVisible(false);
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
		else {
			this.qtdLabel.setVisible(true);
			this.qtdLabel.setText( "x " + String.valueOf(this.catched) );
			
			((TileSetSprite)spriteList.get(0)).setTile(1, 1);	
			spriteList.get(0).setVisible(true);
			
			for( int i = 1 ; i < this.spriteList.size() ; i++ ) {
				spriteList.get(i).setVisible(false);
			}
		}
	} 
	
	
}
