package user_interface;

import animation.AnimatedSprite;
import dataTypes.IVector2D;

@SuppressWarnings("serial")
public class ActorHero extends Actor {
	private int direction = 8;
	private int state = 0;
	
	private AnimatedSprite sprite = null;
	
	public ActorHero(int sizeX, int sizeY) {
		super(sizeX, sizeY);
		
		this.addSprite( "hero.txt" , new IVector2D( 0 , 0 ) , 0 );
		
		this.sprite = this.getAnimatedSprite();
		
		this.setOrtogonalDirection( 2 );
		// TODO Auto-generated constructor stub
	}
	
	public void setDirection( int dir ) {
		if( dir < 1 ) {
			dir = 1;
		} else if ( dir > 32 ) {
			dir = 32;
		}
		
		this.direction = dir;
		
		this.sprite.playAnimation( this.state + this.direction );
	}

	public void setOrtogonalDirection( int dir ) {
		if( dir < 1 ) {
			dir = 1;
		} else if ( dir > 4 ) {
			dir = 4;
		}
		
		switch(dir) {
			case 1:
				this.setDirection( 21 );
				return;
			case 2:
				this.setDirection( 29 );
				return;
			case 3:
				this.setDirection( 5 );
				return;
			case 4:
				this.setDirection( 13 );
				return;
		}
	}
	
}
