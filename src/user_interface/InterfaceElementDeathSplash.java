package user_interface;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementDeathSplash extends InterfaceElement {
	private long timer = 0;
	
	public InterfaceElementDeathSplash() {
		super (467 , 687 );
		// TODO Auto-generated constructor stub
		
		this.setOpaque(false);

		this.addSprite( "deathSplash.txt" , new IVector2D( 0 , 0 ) , 0 , false );	
		
		this.setVisible(false);	
	}

	@Override 
	public void clicked() {
		this.setVisible( false );
		this.disposed = true;
	}
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
	
		
		if( Singletons.death == true ) {
			if( timer < 3001 ) {
				timer += time;
			}
			
			if( timer > 1000 ) {
				Singletons.endProlog();
			}
			
			if( timer > 3000 ) {
				if( this.disposed == false ) {
					this.setVisible( Singletons.death );
				}			
			}
		} else {
			this.setVisible( false );	
		}
		
	} 
	
	
}
