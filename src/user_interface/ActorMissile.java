package user_interface;

import java.awt.Toolkit;

import animation.AnimatedSprite;
import animation.LoopType;
import animation.TileSetSprite;
import data.Singletons;
import dataTypes.*;




@SuppressWarnings("serial")
public class ActorMissile extends Actor {
	private int direction = 8;
	
	private Actor feedBack;
	
	private TileSetSprite sprite = null;
	
	public DVector2D targetPosition = null;
	public double targetDirection = 0;
	public double driftDirection = 0;
	
	
	private double defaultMoveSpeed = 15;
	private double defaultTurnSpeed = 0.2;
	private double turnSpeedFactor = 1;
	
	public double acceleration = 0;
	
	private double startingDist = -1;
	private double minMoveSpeed = 0.1;
	private double maxMoveSpeed = this.defaultMoveSpeed;
	
	
	private long particleSpawnRate = 50;
	private long particleCounter_dirt = 0;
	
	public ActorMissile(DVector2D origin , DVector2D target , Actor feedBack ) {
		super(40, 40);
		this.feedBack = feedBack;
		
		this.addSprite( "missile.txt" , new IVector2D( 0 , 0 ) , 0 , false );
		
		this.sprite = (TileSetSprite)this.getSprite(0);
		
		this.setOrtogonalDirection( 1 );
		
		this.updateAnimation();
		
		this.destroyDelay = 0;
		this.destroyEffect = EffectType.EXPLOSION_1;
		
		this.setRealPosition( origin.x , origin.y );	
		this.setTargetPosition( target.x , target.y );
		
		// Add Chaos
		/*
		DVector2D dirVector = DVector2D.getDistanceVector( origin , target );
		double targetDirection = dirVector.getDirectionRAD();
		targetDirection = this.normalizeRad( targetDirection - (Math.PI * 0.3 ) + ( Math.random() * Math.PI * 0.6 ) ); 
		this.driftDirection = targetDirection;
		this.setMoveDirection( targetDirection  );
		*/
		
		
		this.moveSpeed = this.defaultMoveSpeed;
		this.maxMoveSpeed = this.defaultMoveSpeed;
		
		Singletons.actorScene.addActor( this , 20 );
	}
	

	
	public void setDirection( int dir ) {
		if( dir < 1 ) {
			dir = 1;
		} else if ( dir > 32 ) {
			dir = 32;
		}
		
		this.direction = dir;
	}
	
	public void setOrtogonalDirection( int dir  ) {
		this.setOrtogonalDirection( dir , false );
	}

	public void setOrtogonalDirection( int dir , boolean forceMoveDirection ) {
		if( dir < 1 ) {
			dir = 1;
		} else if ( dir > 4 ) {
			dir = 4;
		}
		
		if( forceMoveDirection == true ) {
			this.driftDirection = this.getRadDirectionFromOrtogonal( dir );
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
	
	private double getRadDirectionFromOrtogonal( int ortDir ) {
		ortDir = ortDir % 4;
		
		switch( ortDir ) {
			case 1:
				return Math.PI * 0.5;
			case 2:
				return 0;
			case 3:
				return Math.PI * 1.5;
			default:
				return Math.PI * 1;
		}
	}
	
	public void setRadianDirection( double rad ) {
		// Coord system Conversion
		rad = -rad;
		rad += ( Math.PI * 9.75 );
		rad = rad % ( Math.PI * 2 );
		
		// Discretize
		double dir = ( ( ( 32.0 * rad ) / ( 2.0 * Math.PI  ) ) + 0.5 );
		int iDir = (int)dir + 1;
		
		this.setDirection( iDir );
	}
	
	
	public void setTargetPosition( double x , double y ) {
		this.targetPosition = new DVector2D( x , y );
		
		this.setMoveDirection( this.driftDirection );
		
		this.startingDist = -1;
		
		this.turnSpeedFactor = 1;
		this.minMoveSpeed = 1;
	}
	
	public double normalizeRad( double rad ) {
		if( rad < 0 ) {
			rad = rad % ( Math.PI * 2 );
			rad += ( Math.PI * 2);
		} else {
			rad = rad % ( Math.PI * 2 );
		}
		
		return rad;
	}
	
	private void setMoveDirection( double dir ) {
		this.moveDirection = this.normalizeRad( dir );
	}
	
	public void resetMoveSpeed( ) {
		this.moveSpeed = this.defaultMoveSpeed;
	}	
	
	public void setMoveSpeed( double factor ) {
		this.moveSpeed = this.defaultMoveSpeed * factor;
		this.maxMoveSpeed = this.defaultMoveSpeed;
	}	
	
	public void setMinMoveSpeed( double factor ) {
		this.minMoveSpeed = this.defaultMoveSpeed * factor;
	}	
	
	public void setMaxMoveSpeed( double factor ) {
		this.maxMoveSpeed = this.defaultMoveSpeed * factor;
	}	
	
	public void updateMoveDirection() {
		this.setMoveDirection( this.driftDirection );
	}
	
	public void updateLayer() {
		if( this.stage != null ) {
			this.stage.setLayer( this , 5 );
		}
	}
	
	@Override
	public void passTime(long time) {
		if( Singletons.paused == true ) {
			return;
		}
		
		DVector2D dirVector = DVector2D.getDistanceVector( this.realPosition , targetPosition );
		double nowTargetdirection = dirVector.getDirectionRAD();
		double dirMod = dirVector.getModulus();
		
		if( dirMod < this.moveSpeed ) {
			this.setRealPosition( targetPosition.x , targetPosition.y );
			this.targetPosition = null;
			this.feedBack.shotsOnTheFly--;
			this.destroyFx();
		} else {
			/*
			double diff = nowTargetdirection - this.driftDirection;
			double turn = ( this.defaultTurnSpeed * this.turnSpeedFactor );

			if( Math.abs( this.normalizeRad(diff)) < turn ) {
				this.driftDirection = this.normalizeRad( nowTargetdirection );
			} else {
				turn *= this.getTurnDirection(diff);
				this.driftDirection = this.normalizeRad( this.driftDirection + ( turn ) );
			}
			
			this.setMoveDirection( this.driftDirection  );
			*/
			
			this.setMoveDirection( nowTargetdirection );
			
			// Speed;
			if( this.moveSpeed > this.maxMoveSpeed ) {
				this.moveSpeed = this.maxMoveSpeed;
			}
			
			// Effect
			this.particleCounter_dirt += time * 1;	
		}	
	
		
		// animation  
		this.setRadianDirection( this.driftDirection );
		this.updateAnimation();
		
		if( this.particleCounter_dirt > this.particleSpawnRate ) {
			this.particleCounter_dirt -= this.particleSpawnRate;
			if( this.stage != null  ) {
				DVector2D pos = new DVector2D( this.realPosition.x , this.realPosition.y );
				
				synchronized(this) {
					Singletons.actorScene.createEffectInRealPosition( EffectType.WHITESMOKE , 6 , pos.x , pos.y );
				}
			}
		}		
		
		super.passTime(time);
	}	
	
	public void updateAnimation() {
		this.sprite.setTile(1, this.direction);
	}
	
	private int getTurnDirection( double radTarget , double radNow ) {
		return this.getTurnDirection( this.normalizeRad(radTarget) - this.normalizeRad(radNow) );
	}
	
	private int getTurnDirection( double diff ) {
		double tol = Math.PI * 0.01;
		
		if( diff < -Math.PI ) {
			return 1;
		}
		else if( diff < 0 + tol ) {
			return -1;
		}
		else if( diff < Math.PI ) {
			return 1;
		} else {
			return -1;
		}
	}
	
}
