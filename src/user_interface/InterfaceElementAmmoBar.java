package user_interface;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import animation.AnimatedSprite;
import animation.LoopType;
import animation.TileSetSprite;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementAmmoBar extends InterfaceElement {
	private int ammo = 0;
	private JLabel qtdLabel;	
	
	public InterfaceElementAmmoBar() {
		super( 330 , 30 );
		// TODO Auto-generated constructor stub
		
		this.setOpaque(false);

		IVector2D startingPos = new IVector2D( 0 , 0 );
		int offset = 25;
		AnimatedSprite ammoSpr = null;
		for( int i = 0 ; i < 8 ; i++ ) {
			this.addSprite( "ammoIcon.txt" , new IVector2D( startingPos.x + ( offset * i ) , startingPos.y ) , 0 , false );
		}
		
		this.qtdLabel = new JLabel("");
		qtdLabel.setFont(new Font("Arial Black", Font.BOLD, 14));
		qtdLabel.setHorizontalAlignment(SwingConstants.LEFT);
		qtdLabel.setForeground(Color.WHITE);
		this.add( this.qtdLabel );
		this.qtdLabel.setBounds( startingPos.x + (int)( offset * 1.5 ) , 0 , 70 , 30 );		
	}
	
	
	public void setAmmo( int a ) {
		this.ammo = a;
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
	
		this.setAmmo( Singletons.heroAmmo );
	
		if( this.ammo <= 8 ) { 
			for( int i = 0 ; i < this.spriteList.size() ; i++ ) {
				if( i < ammo ) {
					spriteList.get(i).setVisible(true);
				} else {
					spriteList.get(i).setVisible(false);
				}
			}
		} else {
			this.qtdLabel.setVisible(true);
			this.qtdLabel.setText( "x " + String.valueOf(this.ammo) );
			
			((TileSetSprite)spriteList.get(0)).setTile(1, 1);	
			spriteList.get(0).setVisible(true);
			
			for( int i = 1 ; i < this.spriteList.size() ; i++ ) {
				spriteList.get(i).setVisible(false);
			}
		}
	} 
	
	
}
