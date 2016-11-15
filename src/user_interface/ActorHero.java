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
	private boolean addDrift = false;
	public double acceleration = 0;
	
	
	private long particleSpawnRate = 100;
	private long particleCounter_dirt = 0;
	
	public ActorHero(int sizeX, int sizeY) {
		super(sizeX, sizeY);
		
		this.addSprite( "hero.txt" , new IVector2D( 0 , 0 ) , 0 );
		
		this.sprite = this.getAnimatedSprite();
		
		this.setOrtogonalDirection( 1 );
		
		this.updateAnimation();
		// TODO Auto-generated constructor stub
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
		this.setTargetPosition(x, y,false);
		
	}
	
	public void setTargetPosition( double x , double y , boolean addDrift ) {
		this.targetPosition = new DVector2D( x , y );
		this.state = MoveState.SEEKING;
		this.addDrift = addDrift;
		
		this.setMoveDirection( this.driftDirection );

		if( addDrift == true ) {
			double rad = this.getRadDirectionFromOrtogonal( Singletons.heroDirection );
			this.setTargetDirection( rad - ( Math.PI * 0.5 ) , true );
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
	}	
	
	public void setMinMoveSpeed( double factor ) {
		if( this.moveSpeed < this.defaultMoveSpeed * factor ) {
			this.moveSpeed = this.defaultMoveSpeed * factor;
		}
	}	
	
	public void setMaxMoveSpeed( double factor ) {
		if( this.moveSpeed > this.defaultMoveSpeed * factor ) {
			this.moveSpeed = this.defaultMoveSpeed * factor;
		}
	}	
	
	public void updateMoveDirection() {
		this.setMoveDirection( this.driftDirection );
	}
	
	public boolean isStopped( ) {
		if( this.state == MoveState.STOPED ) {
			return true;
		} else {
			return false;
		}
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
		
		
		if( this.state == MoveState.SEEKING ) {
			DVector2D dirVector = DVector2D.getDistanceVector( this.realPosition , targetPosition );
			double targetDirection = dirVector.getDirectionRAD();
			
			double dirMod = dirVector.getModulus();
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
				} 
				
				this.particleCounter_dirt += ( time * ( ( this.moveSpeed + 1 ) / this.defaultMoveSpeed ) );
			}
			
		}
		if ( this.state == MoveState.DRIFTING || this.addDrift == true ) {
			double diff = this.targetDirection - this.driftDirection;
			double turn;
			
			if( this.addDrift == true ) {
				turn = ( this.defaultTurnSpeed * 0.7 );
			} else {
				turn = ( this.defaultTurnSpeed * 1.0 );
			}

			if( Math.abs( this.normalizeRad(diff)) < turn ) {
				this.driftDirection = this.normalizeRad( this.targetDirection );
				if( this.state == MoveState.DRIFTING ) {				
					this.state = MoveState.STOPED;
				} 
			} else {	
				turn *= this.getTurnDirection(diff);
				
				if( this.moveSpeed > this.defaultMoveSpeed ) {
					if( this.state == MoveState.DRIFTING ) {
						this.moveSpeed = this.defaultMoveSpeed;
					}
				} else {	
					this.moveSpeed = this.moveSpeed + this.acceleration;
					
					if( this.state == MoveState.DRIFTING ) {
						if( this.moveSpeed < 0 ) {
							this.moveSpeed = 0;
						}
					} else {
						if( this.moveSpeed < 2 ) {
							this.moveSpeed = 2;
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
				
				Singletons.actorScene.createEffectInRealPosition( EffectType.DIRT , 6 , pos.x , pos.y );
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
