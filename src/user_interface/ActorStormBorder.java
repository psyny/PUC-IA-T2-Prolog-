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
	private int direction;
		
	public ActorStormBorder(int sizeX, int sizeY , Cell borderOf , int direction ) {
		super(sizeX, sizeY);
		
		IVector2D offset = new IVector2D( 5 , 5 );
		
		if ( direction == 13 ) {
			offset.y += +1;
		}
		
		this.addSprite( "storm.txt" , offset , 0 );
		this.sprite = this.getAnimatedSprite();
		this.borderOf = borderOf;
		
		this.direction = direction;
		this.sprite.playAnimation(direction);
	}
	
	public void updateStatus( Cell cell ) {			
		if( Singletons.fogUndiscovery == true ) {	
			if( cell.frontier == true  ) {
				if( this.borderOf.frontier == true || this.borderOf.discovered == true ) {
					if( this.isVisible() == true ) {
						this.setVisible(false);
					}
				} else {
					if( this.isVisible() == false ) {
						this.setVisible(true);
					}
				}
			} else if( cell.discovered == true  ) {
					if( this.borderOf.frontier == true ) {
						if( this.isVisible() == false ) {
							this.setVisible(true);			
						}
						if( this.sprite.getCurrentAnimationID() != this.direction + 100 ) {
							this.sprite.playAnimation( this.direction + 100 );
						}
					} else if ( this.borderOf.discovered == false ) {
						if( this.isVisible() == false ) {
							this.setVisible(true);
						}
						if( this.sprite.getCurrentAnimationID() != this.direction ) {
							this.sprite.playAnimation( this.direction );
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
		
		} else {
			if( this.isVisible() == true ) {
				this.setVisible(false);
			}
		}
	}
}