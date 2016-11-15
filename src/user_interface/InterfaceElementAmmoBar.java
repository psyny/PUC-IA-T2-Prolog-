package user_interface;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementAmmoBar extends InterfaceElement {
	private int ammo = 0;
	
	public InterfaceElementAmmoBar() {
		super( 330 , 30 );
		// TODO Auto-generated constructor stub
		
		this.setOpaque(false);

		IVector2D startingPos = new IVector2D( 0 , 0 );
		int offset = 25;
		AnimatedSprite ammoSpr = null;
		for( int i = 0 ; i < 10 ; i++ ) {
			this.addSprite( "ammoIcon.txt" , new IVector2D( startingPos.x + ( offset * i ) , startingPos.y ) , 0 , false );
		}
		
		
	}
	
	
	public void setAmmo( int a ) {
		this.ammo = a;
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
	
		this.setAmmo( Singletons.heroAmmo );
	
		for( int i = 0 ; i < this.spriteList.size() ; i++ ) {
			if( i < ammo ) {
				spriteList.get(i).setVisible(true);
			} else {
				spriteList.get(i).setVisible(false);
			}
		}
	} 
	
	
}
