package user_interface;

import dataTypes.Cell;
import dataTypes.CellType;
import dataTypes.IVector2D;

public class ActorFactory {
	public static Actor fabricate( ActorTypes type ) {
		Actor newActor = null;
			
		switch( type ) {
			case CYCLONE:
				newActor = new Actor( 500 , 500 );
				newActor.addSprite( "hurricane.txt" , new IVector2D( 0 , -100 ) , 0 );
				break;
				
			case LANDMINE:
				newActor = new Actor( 100 , 100 );
				newActor.addSprite( "landmine.txt" , new IVector2D( 0 , 0 ) , 0 );
				break;
				
			case GAS:
				newActor = new Actor( 150 , 150 );
				newActor.addSprite( "upgrade.txt" , new IVector2D( 0 , 0 ) , 0 );
				newActor.destroyDelay = 100;
				newActor.destroyEffect = EffectType.POWERUP;
				break;
			
			case WATER:
				newActor = new ActorWater( 200 , 200 );
				newActor.addSprite( "water.txt" , new IVector2D( 0 , -30 ) , 0 );
				break;
				
			case ENEMY:
				newActor = new Actor( 240 , 240 );
				newActor.addSprite( "enemy.txt" , new IVector2D( 0 , 0 ) , 0 );
				newActor.getAnimatedSprite().playRandomAnimation();
				break;
				
			case BOSS:
				newActor = new Actor( 240 , 240 );
				newActor.addSprite( "boss.txt" , new IVector2D( 0 , 0 ) , 0 );
				newActor.getAnimatedSprite().playRandomAnimation();
				break;
				
			case HERO:
				newActor = new ActorHero( 150 , 150 );
				break;
				
			case STORM:
				newActor = new ActorStorm( 400 , 400 );
				break;
				
			case GRID:
				newActor = new ActorGrid( 200 , 200 );
				break;
		}
		
		
		return newActor;
	}
	
	public static ActorSensor fabricateSensor( CellType type ) {
		ActorSensor newActor = null;	
		
		newActor = new ActorSensor( 100 , 100  , type );
		
		return newActor;
	}
	
	public static ActorStormBorder fabricateBorder( Cell borderOf , Cell origin ) {
		ActorStormBorder newActor = null;	
		int direction = 11;
		
		if( borderOf.position.x > origin.position.x ) {
			direction = 12;
		} else if( borderOf.position.x < origin.position.x ) {
			direction = 14;
		} else if( borderOf.position.y > origin.position.y ) {
			direction = 11;
		} else if( borderOf.position.y < origin.position.y ) {
			direction = 13;
		}
		
		newActor = new ActorStormBorder( 400 , 400  , borderOf , direction );
		
		return newActor;
	}
	
	public static ActorEffect fabricateEffect( EffectType type ) {
		ActorEffect newActor = null;
		
		switch( type ) {
			case LIGHTNING:
				newActor = new ActorEffect( 100 , 512 , type );
				break;
				
			case WATERSPLASH:
				newActor = new ActorEffect( 150 , 150 , type );
				break;	
				
			case POWERUP:
				newActor = new ActorEffect( 150 , 150 , type );
				break;		
				
			case DIRT:
				newActor = new ActorEffect( 130 , 130 , type );
				newActor.moveDirection = Math.PI * ( 1.4 + ( Math.random() * 0.2 ) );
				newActor.moveSpeed = 1 + ( Math.random() * 2 );
				break;				
		}
		
		return newActor;
	}
	
}
