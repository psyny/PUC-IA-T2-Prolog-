package user_interface;

import animation.Animable;
import animation.AnimatedSprite;
import animation.LoopType;
import dataTypes.IVector2D;

@SuppressWarnings("serial")
public class ActorEffect extends Actor {
	private AnimatedSprite sprite = null;
	
	private long life = 0;
	
	public ActorEffect(int sizeX, int sizeY, EffectType type) {
		super(sizeX, sizeY);
		
		String 		spriteName 	= "lightning.txt"; 
		IVector2D 	offset 		= new IVector2D( 0 , 0 );
		
		switch( type ) {
			case LIGHTNING:
				spriteName = "lightning.txt";
				offset.x = -16;
				offset.y = -110;
				break;
				
			case WATERSPLASH:
				spriteName = "waterSplash.txt";
				offset.x = 0;
				offset.y = 0;
				break;

			case POWERUP:
				spriteName = "fadingFlame.txt";
				offset.x = 0;
				offset.y = 0;
				break;
				
			case DIRT:
				spriteName = "smokeDirt.txt";
				offset.x = 0;
				offset.y = 0;
				break;		
				
			case WHITESMOKE:
				spriteName = "smokeMissile.txt";
				offset.x = 0;
				offset.y = 0;
				break;		
				
			case EXPLOSION_1:
				spriteName = "testExplosion.txt";
				offset.x = 0;
				offset.y = 0;
				break;	
				
			case EXPLOSION_2:
				spriteName = "testExplosion.txt";
				offset.x = 0;
				offset.y = 0;
				break;		

			case EXPLOSION_LARGE:
				spriteName = "explosionLarge.txt";
				offset.x = 0;
				offset.y = -30;
				break;			
				
			case EXPLOSION_HUGE:
				spriteName = "explosionHuge.txt";
				offset.x = 0;
				offset.y = -30;
				break;		
				
			case EXPLOSION_MINE:
				spriteName = "mineExplosion.txt";
				offset.x = 0;
				offset.y = -20;
				break;		
				
		}
		
		
		this.addSprite( spriteName , offset , 0 );	
		this.sprite = this.getAnimatedSprite();
		
		this.sprite.playAnimation(1, 1, LoopType.END_DIE );
	}	
	
	@Override
	public void passTime(long time) {
		this.life += time;
		
		if( this.life > 10000 ) {
			//System.out.println("DEBUG: ANCIAO!");
		}
		
		super.passTime(time);
	}
	
}
