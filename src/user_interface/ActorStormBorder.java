package user_interface;

import javax.swing.JLayeredPane;

import animation.AnimatedSprite;
import animation.LoopType;
import animation.Sprite;
import data.Singletons;
import dataTypes.Cell;
import dataTypes.IVector2D;

public class ActorStormBorder extends Actor {
	public Cell				borderOf = null;
	
	private AnimatedSprite 	sprite;
		
	public ActorStormBorder(int sizeX, int sizeY , Cell borderOf , int direction ) {
		super(sizeX, sizeY);
		
		IVector2D offset = new IVector2D( 5 , 5 );
		
		if ( direction == 13 ) {
			offset.y += +1;
		}
		
		this.addSprite( "storm.txt" , offset , 0 );
		this.sprite = this.getAnimatedSprite();
		this.borderOf = borderOf;
		
		this.sprite.playAnimation(direction);
	}
	
	public void updateStatus( Cell cell ) {			
		if( Singletons.fogUndiscovery == true ) {	
			if( cell.discovered == true  ) {
				if( this.borderOf.discovered == true ) {
					if( this.isVisible() == true ) {
						this.setVisible(false);
					}
				} else {
					if( this.isVisible() == false ) {
						this.setVisible(true);
					}
				}
			} else {
				if( this.isVisible() == true ) {
					this.setVisible(false);
				}
			}
		
		} else {
			if( this.isVisible() == true ) {
				this.setVisible(false);
			}
		}
	}
}