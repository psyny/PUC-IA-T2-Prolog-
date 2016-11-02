package user_interface;

import animation.*;
import data.Singletons;
import dataTypes.*;

@SuppressWarnings("serial")
public class ActorSensor extends Actor {
	private AnimatedSprite 	sprite;

	public CellType		sensorType;
	public Cell 		sensorPosition;	
	public Cell 		sensorOf;	
	
	private boolean		isVisible = true;
	
	public ActorSensor(int sizeX, int sizeY, CellType type) {
		super(sizeX, sizeY);

		this.sensorType = type;
		
		int animType = 1;
		IVector2D offset = new IVector2D(0,0);
		
		switch(type) {
			case BOSS:
			case ENEMY:
				animType = 3;
				offset.x = 20;
				offset.y = -20;
				break;
			
			case CYCLONE:
				animType = 1;
				offset.x = 0;
				offset.y = 20;
				break;
				
			case LANDMINE:
				animType = 2;
				offset.x = -20;
				offset.y = -20;
				break;
		}
		
		this.addSprite( "sensors.txt" , offset , 0 );	
		this.sprite = this.getAnimatedSprite();
		sprite.playAnimation( animType , 1 , LoopType.ZIGZAG);
		
	}
	
	public ActorSensor(int sizeX, int sizeY) {
		this( sizeX , sizeY , CellType.CYCLONE );
		
	}

	
	public void updateStatus( Cell cell ) {
		if( cell.haveSensor( this.sensorType ) == false ) {
			return;
		}
		
		
		if( Singletons.showSensors == true ) {
			if( cell.discovered == true || Singletons.fogUndiscovery == false ) {
				if ( sensorOf.discovered == true && Singletons.hideDiscoveredSensors == true ){
					if (  this.isVisible() == true ) {
						this.setVisible( false );
					}
				} else {
					if ( this.isVisible() == false ) {
						this.setVisible( true );
					}
				}
			} else {
				if ( this.isVisible() == true ) {
					this.setVisible( false );
				}
			}
		} else {
			if ( this.isVisible() == true ) {
				this.setVisible( false );
			}
		}
		
		
	}
	

	
}
