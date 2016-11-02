package user_interface;


import animation.*;
import dataTypes.*;

@SuppressWarnings("serial")
public class ActorWater extends Actor {
	private long maxDelay = 4000;
	private long delay = (int)( Math.random() * maxDelay ) + ( 2000 );
	private AnimatedSprite aSpr;
	
	public ActorWater(int sizeX, int sizeY) {
		super(sizeX, sizeY);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
		
		if( this.delay < 0 ) {
			delay = (int)( Math.random() * maxDelay ) + ( 2000 ); 
			this.aSpr.playAnimation( 1 , 1 , false , 1 , LoopType.END_STOP );
		} else {
			delay = delay - time;
		}
	}

	@Override
	public void addSprite( Sprite sprite , IVector2D relativePosition , int layer ) {
		super.addSprite(sprite, relativePosition, layer);
		
		this.aSpr = (AnimatedSprite)sprite;
		this.aSpr.playAnimation( 1 , LoopType.END_STOP );
	}

}
