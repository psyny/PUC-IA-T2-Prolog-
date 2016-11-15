package user_interface;

import javax.swing.JLayeredPane;

import animation.AnimatedSprite;
import animation.LoopType;
import animation.Sprite;
import data.Singletons;
import dataTypes.Cell;
import dataTypes.IVector2D;

public class ActorStorm extends Actor {
	private int 			defaultLayer = 0;
	private JLayeredPane 	parent = null;
	
	
	public ActorStorm(int sizeX, int sizeY ) {
		super(sizeX, sizeY);
		this.addSprite( "storm.txt" , new IVector2D( 5 , 5 ) , 0 );
		this.getAnimatedSprite().playAnimation(1);
	}
	
	public ActorStorm(int sizeX, int sizeY , int layer , JLayeredPane parent) {
		super(sizeX, sizeY);
	}
	
	public void updateStatus( Cell cell ) {
		if( cell.stormActor != this ) {
			return;
		}
		
		AnimatedSprite stormSpr = (AnimatedSprite)this.getAnimatedSprite();
		if( cell.discovered == true || Singletons.fogUndiscovery == false ) {
			if( stormSpr.getCurrentAnimationID() != 2 ) {
				stormSpr.playAnimation(2);			
				//parent.setLayer( this, this.defaultLayer + 1 );
				
				if( cell.contentActor != null ) {
					cell.contentActor.setVisible(true);
				}
			}
		} else if ( cell.frontier == true ){
			if( stormSpr.getCurrentAnimationID() != 15 ) {
				stormSpr.playAnimation(15);			
				//parent.setLayer( this, this.defaultLayer + 1 );
				
				if( cell.contentActor != null ) {
					cell.contentActor.setVisible(false);
				}
			}
		} else {
			if( stormSpr.getCurrentAnimationID() != 1 ) {
				stormSpr.playAnimation(1);	
				//parent.setLayer( this, this.defaultLayer );
				
				if( cell.contentActor != null ) {
					cell.contentActor.setVisible(false);
				}
			}
		}
	}

}