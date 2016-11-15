package user_interface;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementHealthBar extends InterfaceElement {
	private int health = 0;
	
	public InterfaceElementHealthBar() {
		super( 330 , 30 );
		// TODO Auto-generated constructor stub
		
		this.setOpaque(true);
		this.setBackground( new Color(255,144,0, ((int)(255*0.7) ) ) );
		
		// Flames
		IVector2D startingPos = new IVector2D( 0 , 0 );
		int offset = 30;
		AnimatedSprite fire = null;
		for( int i = 0 ; i < 10 ; i++ ) {
			this.addSprite( "fire1_cut.txt" , new IVector2D( startingPos.x + ( offset * i ) , startingPos.y ) , 0 );
			fire = (AnimatedSprite)this.lastAddedSprite;
			fire.playAnimation( 1 , ((int)(Math.random()*12)) , false , 1 , LoopType.REPEAT );
		}
		
		// BG
		JPanel BG = new JPanel();
		BG.setOpaque(true);
		BG.setBackground( new Color(120,0,0, ((int)(255*0.8) ) ) );
		this.add(BG);
		BG.setBounds( 2 , 2 , this.getWidth() - 4 , this.getHeight() - 4 );
		
		
	}
	
	
	public void setHealth( int h ) {
		this.health = h;
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
		
		this.setHealth( Singletons.heroLife );
		
		int tempHealth = this.health / 10;
		
		for( int i = 0 ; i < this.aSpriteList.size() ; i++ ) {
			if( (i+1) <= tempHealth ) {
				aSpriteList.get(i).setVisible(true);
			} else {
				aSpriteList.get(i).setVisible(false);
			}
		}
	} 
	
	
}
