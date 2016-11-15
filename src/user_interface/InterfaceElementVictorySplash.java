package user_interface;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.IVector2D;



public class InterfaceElementVictorySplash extends InterfaceElement {
	private long timer = 0;
	
	public InterfaceElementVictorySplash() {
		super (467 , 687 );
		// TODO Auto-generated constructor stub
		
		this.setOpaque(false);

		this.addSprite( "victorySplash.txt" , new IVector2D( 0 , 0 ) , 0 , false );	

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
	
		
		if( Singletons.victory == true && this.disposed == false ) {
			if( timer < 3001 ) {
				timer += time;
			}
			
			if( timer > 1000 ) {
				Singletons.endProlog();
			}
			
			if( timer > 3000 ) {
				if( this.disposed == false ) {
					this.setVisible( Singletons.victory );
				}
			}
		} else {
			this.setVisible( false );	
		}
		
		
		
	} 
	
	
}
