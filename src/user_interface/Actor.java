package user_interface;

import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JLayeredPane;

import animation.*;
import dataTypes.*;
import data.*;

class innerSprite {
	public IVector2D 	projectionPosition;
	public Sprite		sprite;
	
	public innerSprite( IVector2D position , Sprite sprite ) {
		this.projectionPosition = new IVector2D( position.x , position.y );
		this.sprite = sprite;
	}
}

public class Actor extends JLayeredPane implements Animable {
	public DVector2D	realPosition		= new DVector2D( 0 , 0 );
	
	public DVector2D projectionPosition 	= new DVector2D( 0 , 0 );
	public IVector2D projectionSize 		= new IVector2D( 0 , 0 );
	
	public Container parent = null;
	
	public GameScene stage = null;
	
	private ArrayList<innerSprite> innerSprites;
	
	public boolean 		destroing = false;
	public EffectType 	destroyEffect = null;
	public long			destroyDelay = 0;
	private long		destroyCounter = 0;
	
	public double moveSpeed = 0;
	public double moveDirection = 0;
	

	public Actor( int sizeX , int sizeY ) {
		this.init();
		
		this.projectionSize.x = sizeX;
		this.projectionSize.y = sizeY;
	}
	
	private void init() {
		this.setVisible( true );
		this.setOpaque( false );
		this.setLayout( null );
		this.setBounds(0,0,1,1);
		
		this.setSize( 1 , 1 );
		
		this.innerSprites = new ArrayList<innerSprite>();
	}

	public void addSprite( String fileName , IVector2D relativePosition , int layer ) {
		AnimatedSprite aSpr;
		aSpr = new AnimatedSprite( fileName );
		aSpr.playAnimation();

		this.addSprite( aSpr , relativePosition, layer );
	}
	
	public void addSprite( String fileName , IVector2D relativePosition , int layer , boolean animated) {
		Sprite aSpr;
		
		if( animated == true ) {
			aSpr = new AnimatedSprite( fileName );
			((AnimatedSprite)aSpr).playAnimation();
		} else {
			aSpr = new TileSetSprite( fileName );
		}

		this.addSprite( aSpr , relativePosition, layer);
	}
	
	public void addSprite( Sprite sprite , IVector2D relativePosition , int layer ) {
		IVector2D spriteSize = sprite.getDimension();
		if( spriteSize.x > this.projectionSize.x ) {
			this.projectionSize.x = spriteSize.x;
			this.updateLocation();
		}
		if( spriteSize.y > this.projectionSize.y ) {
			this.projectionSize.y = spriteSize.y;
			this.updateLocation();
		}
		
		innerSprite newInnerSprite = new innerSprite( relativePosition , sprite );
		this.innerSprites.add( newInnerSprite );
		sprite.insertInto( this );
		this.setLayer( sprite , layer);
		
		IVector2D spritePosition =  new IVector2D( 0 , 0 );
		spritePosition.x = ( this.projectionSize.x / 2 ) + relativePosition.x - ( sprite.getDimension().x / 2 ) ;
		spritePosition.y = ( this.projectionSize.y / 2 ) + relativePosition.y - ( sprite.getDimension().y / 2 ) ;
		
		sprite.setBounds(spritePosition.x, spritePosition.y, spriteSize.x, spriteSize.y);
		
		//sprite.setLocation( spritePosition.x , spritePosition.y  );
	}
	
	@Override
	public void passTime(long time) {	
		// Position
		this.setRealPosition( this.realPosition.x + ( this.moveSpeed * Math.cos( this.moveDirection ) ) , this.realPosition.y + ( this.moveSpeed * Math.sin( this.moveDirection ) ) );
		
		// Destroy
		if( this.destroing == true ) {
			this.destroyCounter += time;
			if( this.destroyCounter >= this.destroyDelay  ) {
				this.setVisible(false);
				Toolkit.getDefaultToolkit().sync();
				Singletons.actorScene.remove(this);
			}
		}
		
		for( innerSprite iSpr : this.innerSprites ) {
    		if (iSpr.sprite instanceof Animable ) {
    			((Animable) iSpr.sprite).passTime( time );
    		}
		}
		
		// Set to remove
		if( this.parent != null ) {
			if( this.getComponents().length == 0 ) {
				this.setVisible(false);
				Toolkit.getDefaultToolkit().sync();
				this.parent.remove(this);
			}
		}
	}
	
	public void setRealPosition( double x , double y ) {
		this.realPosition.x = x;
		this.realPosition.y = y;
		
		this.updateLocation();
	}
	
	public void setLocation( int x , int y ) {
		this.setSize( this.projectionSize.x , this.projectionSize.y );		
		
		this.projectionPosition.x = x - ( this.projectionSize.x / 2 );
		this.projectionPosition.y = y - ( this.projectionSize.y / 2 );
		
		super.setLocation( (int)this.projectionPosition.x , (int)this.projectionPosition.y );
	}
	
	private void updateLocation() {
		this.setSize( this.projectionSize.x , this.projectionSize.y );
		
		IVector2D centerPosition = IsoGrid.getProjection( this.realPosition.x ,  this.realPosition.y , true , false );
		this.projectionPosition.x = centerPosition.x - ( this.projectionSize.x / 2 );
		this.projectionPosition.y = centerPosition.y - ( this.projectionSize.y / 2 );
		
		super.setLocation( (int)this.projectionPosition.x , (int)this.projectionPosition.y );
	}
	
	public AnimatedSprite getAnimatedSprite() {
		for( innerSprite iSpr : this.innerSprites ) {
    		if (iSpr.sprite instanceof Animable ) {
    			return (AnimatedSprite)iSpr.sprite;
    		}
		}
		
		return null;
	}
	
	public Sprite getSprite(int num) {
		if( num >= this.innerSprites.size() ) {
			return null;
		} else {
			return this.innerSprites.get(0).sprite;
		}	
	}
	
	public IVector2D getProjectionCenter() {
		IVector2D projectionCenter = new IVector2D( (int)this.projectionPosition.x , (int)this.projectionPosition.y );
		projectionCenter.x += ( this.projectionSize.x / 2 );
		projectionCenter.y += ( this.projectionSize.y / 2 );
		
		return projectionCenter;
	}
	
	public void updateStatus( Cell cell ) {
		if( ( cell.discovered == true || Singletons.fogUndiscovery == false ) && ( cell.destroyed == false ) ) {
			this.setVisible(true);
		} else {
			if( cell.destroyed == true ) {
				this.destroyFx();
			} else {
				this.setVisible(false);
			}
		}
	}
	
	public void destroyFx( ) {
		if( this.destroing == false ) {
			this.destroing = true;
			
			if( this.destroyEffect != null ) {
				Toolkit.getDefaultToolkit().sync();
				Singletons.actorScene.createEffectInRealPosition( this.destroyEffect , 20, this.realPosition.x , this.realPosition.y );
				this.destroyCounter = 0;
			}
		}
	}
}
