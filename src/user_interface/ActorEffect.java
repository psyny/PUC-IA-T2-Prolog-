package user_interface;

import animation.Animable;
import animation.AnimatedSprite;
import animation.LoopType;
import dataTypes.IVector2D;

@SuppressWarnings("serial")
public class ActorEffect extends Actor {
	private AnimatedSprite sprite = null;
	
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
		}
		
		
		this.addSprite( spriteName , offset , 0 );	
		this.sprite = this.getAnimatedSprite();
		
		this.sprite.playAnimation(1, 1, LoopType.END_DIE );
	}	
}
