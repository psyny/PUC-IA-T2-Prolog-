package user_interface;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.*;




@SuppressWarnings("serial")
public class ActorHero extends Actor {
	enum MoveState {
		SEEKING,
		DRIFTING,
		STOPED
	}
	private int direction = 8;
	private MoveState state = MoveState.STOPED;
	
	private AnimatedSprite sprite = null;
	
	public DVector2D targetPosition = null;
	public double targetDirection = 0;
	public double driftDirection = 0;
	
	
	private double defaultMoveSpeed = 8;
	private double defaultTurnSpeed = 0.07;
	private double turnSpeedFactor = 1;
	
	private boolean addDrift = false;
	public double acceleration = 0;
	
	private double startingDist = -1;
	private double minMoveSpeed = 0.1;
	private double maxMoveSpeed = this.defaultMoveSpeed;
	
	
	private long particleSpawnRate = 100;
	private long particleCounter_dirt = 0;
	
	public ActorHero(int sizeX, int sizeY) {
		super(sizeX, sizeY);
		
		this.addSprite( "hero.txt" , new IVector2D( 0 , 0 ) , 0 );
		
		this.sprite = this.getAnimatedSprite();
		
		this.setOrtogonalDirection( 1 );
		
		this.updateAnimation();
		
		this.destroyDelay = 0;
		this.destroyEffect = EffectType.EXPLOSION_LARGE;
	}
	
	public void instantTurn() {
		this.setOrtogonalDirection( Singletons.heroDirection , true );
		this.moveSpeed = 0;
		this.state = MoveState.STOPED;
	}
	
	public void instantMove() {
		DVector2D pos = Singletons.sceneGrid.getTileCenterRealPosition( Singletons.heroPosition.x , Singletons.heroPosition.y );
		this.setRealPosition( pos.x , pos.y );
		this.moveSpeed = 0;
		this.state = MoveState.STOPED;
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
		synchronized(this) {
			this.setTargetPosition(x, y,false,0);
		}
	}
	
	public void setTargetPosition( double x , double y , boolean addDrift , int turns ) {
		this.targetPosition = new DVector2D( x , y );
		this.state = MoveState.SEEKING;
		this.addDrift = addDrift;
		
		this.setMoveDirection( this.driftDirection );
		
		this.startingDist = -1;

		if( addDrift == true ) {
			double rad = this.getRadDirectionFromOrtogonal( Singletons.heroDirection );
			this.setTargetDirection( rad - ( Math.PI * ( 0.5 * turns ) ) , true );
			this.turnSpeedFactor = turns * 0.6;
			this.minMoveSpeed = 2.5 - ( turns * 0.5 );
		} else {
			this.turnSpeedFactor = 1;
		}
	}
	
	public void setTargetDirection ( double rad ) {
		this.setTargetDirection(rad , false);
	}
	
	public void setTargetDirection ( double rad , boolean moving  ) {
		this.targetDirection = this.normalizeRad( rad );
		
		if( moving == false ) {
			this.addDrift = false;
			this.state = MoveState.DRIFTING;
		} else {
			this.turnSpeedFactor = 1;
		}
		
	}	
	
	public void setTurn( int ortDir  ) {
		this.setTurn( ortDir , false );
	}
	
	public void setTurn( int ortDir , boolean moving ) {
		if( moving == false ) {
			this.setOrtogonalDirection( ortDir , false );
		} else {
			this.setOrtogonalDirection( ortDir , true );
		}
		
		this.setTargetDirection( this.getRadDirectionFromOrtogonal(ortDir) , moving );
	}
	
	public void shot() {
		DVector2D pos = new DVector2D( this.realPosition.x , this.realPosition.y );
		
		IVector2D targetPos = new IVector2D( Singletons.heroPosition.x , Singletons.heroPosition.y );
		
		
		switch( Singletons.heroDirection ) {
			case 1:
				targetPos.y++;
				break;
			case 2:
				targetPos.x++;
				break;
			case 3:
				targetPos.y--;
				break;
			case 4:
				targetPos.x--;
				break;
		}
		
		this.setOrtogonalDirection( Singletons.heroDirection , true );
		this.moveDirection = this.driftDirection;
		
		Singletons.hero.setMoveSpeed( 0.4 );
		
		DVector2D target = IsoGrid.getTileCenterRealPosition( targetPos.x , targetPos.y );
		this.shotsOnTheFly++;		
			
		new ActorMissile( pos , target , this );
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
	
	public boolean isStopped( ) {
		if( this.state == MoveState.STOPED && this.shotsOnTheFly == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updateLayer() {
		if( this.stage != null && ( Singletons.victory == false || Singletons.death == false ) ) {
			synchronized( Singletons.gameCamera ) {
				this.stage.setLayer( this , 5 );
			}
		}
	}
	
	@Override
	public void passTime(long time) {
		if( Singletons.paused == true ) {
			return;
		}
		
		
		if( this.state == MoveState.SEEKING ) {
			DVector2D dirVector = DVector2D.getDistanceVector( this.realPosition , targetPosition );
			double targetDirection = dirVector.getDirectionRAD();
			
			double dirMod = dirVector.getModulus();
			
			if( this.startingDist < 0 ) {
				this.startingDist = dirMod / 2;
			}
			
			if( dirMod < this.moveSpeed ) {
				this.setRealPosition( targetPosition.x , targetPosition.y );
				this.targetPosition = null;
				
				if( this.addDrift == true ) {
					this.setMoveDirection( this.driftDirection );
					
					this.addDrift = false;
				}
				
				this.state = MoveState.STOPED;
			} else {
				this.setMoveDirection( targetDirection  );
				
				// Sprite direction
				if( this.addDrift == false ) {
					this.driftDirection = this.moveDirection;
					
					this.moveSpeed = this.moveSpeed + 0.2;
					if( this.moveSpeed > this.maxMoveSpeed ) {
						this.moveSpeed = this.maxMoveSpeed;
					}
				}
				else {
				// Speed Update
					if( this.startingDist >= 0 ) {
						this.moveSpeed = this.defaultMoveSpeed * ( dirMod / this.startingDist );
						if( this.moveSpeed > this.defaultMoveSpeed ) {
							this.moveSpeed = this.defaultMoveSpeed;
						}
						else if( this.moveSpeed < this.minMoveSpeed ) {
							this.moveSpeed = this.minMoveSpeed;
						}
					}
				}
				
				this.particleCounter_dirt += ( time * ( ( this.moveSpeed + 1 ) / this.defaultMoveSpeed ) );
			}
			
		}
		if ( this.state == MoveState.DRIFTING || this.addDrift == true ) {
			double diff = this.targetDirection - this.driftDirection;
			double turn;
			
			if( this.addDrift == true ) {
				turn = ( this.defaultTurnSpeed * this.turnSpeedFactor );
			} else {
				turn = ( this.defaultTurnSpeed * this.turnSpeedFactor );
			}

			if( Math.abs( this.normalizeRad(diff)) < turn ) {
				this.driftDirection = this.normalizeRad( this.targetDirection );
				if( this.state == MoveState.DRIFTING ) {				
					this.state = MoveState.STOPED;
				} 
			} else {	
				if( this.addDrift == false ) {
					turn *= this.getTurnDirection(diff);
				} else {
					turn = -turn;
				}
				
				if( this.moveSpeed > this.defaultMoveSpeed ) {
					if( this.state == MoveState.DRIFTING ) {
						this.moveSpeed = this.defaultMoveSpeed;
					}
				} else {	
					if( this.state == MoveState.DRIFTING ) {
						this.moveSpeed = this.moveSpeed + this.acceleration;
						if( this.moveSpeed < 0.2 ) {
							this.moveSpeed = 0.2;
						}
					}
				}		
						
				this.driftDirection = this.normalizeRad( this.driftDirection + ( turn ) );
				
				// Spawn Dirt
				if( this.addDrift == false ) {
					this.particleCounter_dirt += time * 2;	
				} else {
					this.particleCounter_dirt += time * 1;	
				}
			}
		}
		
		// animation  
		this.setRadianDirection( this.driftDirection );
		this.updateAnimation();
		
		if( this.particleCounter_dirt > this.particleSpawnRate ) {
			this.particleCounter_dirt -= this.particleSpawnRate;
			if( this.stage != null  ) {
				DVector2D pos = new DVector2D( this.realPosition.x , this.realPosition.y );
				pos.x += - 35 + ( Math.random() * 70 );
				pos.y += - 35 + ( Math.random() * 70 );
				
				synchronized(this) {
					Singletons.actorScene.createEffectInRealPosition( EffectType.DIRT , 6 , pos.x , pos.y );
				}
			}
		}		
		
		super.passTime(time);
	}	
	
	public void updateAnimation() {
		if( this.state == MoveState.STOPED ) {
			this.sprite.playAnimation( this.direction );
		} else {
			this.sprite.playAnimation( 100 + this.direction );
		}
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
