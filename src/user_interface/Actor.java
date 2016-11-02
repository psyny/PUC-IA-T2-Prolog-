package user_interface;

import java.awt.Color;
import java.awt.Container;
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
	
	private ArrayList<innerSprite> innerSprites;

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
		IVector2D newSize = sprite.getDimension();
		if( newSize.x > this.projectionSize.x ) {
			this.projectionSize.x = newSize.x;
			this.updateLocation();
		}
		if( newSize.y > this.projectionSize.y ) {
			this.projectionSize.y = newSize.y;
			this.updateLocation();
		}
		
		innerSprite newInnerSprite = new innerSprite( relativePosition , sprite );
		this.innerSprites.add( newInnerSprite );
		sprite.insertInto( this );
		sprite.setLocation( ( this.projectionSize.x / 2 ) + relativePosition.x - ( sprite.getDimension().x / 2 ) , ( this.projectionSize.y / 2 ) + relativePosition.y - ( sprite.getDimension().y / 2 ) );
		this.setLayer( sprite , layer);
	}
	
	@Override
	public void passTime(long time) {
		for( innerSprite iSpr : this.innerSprites ) {
    		if (iSpr.sprite instanceof Animable ) {
    			((Animable) iSpr.sprite).passTime( time );
    		}
		}
		
		// Set to remove
		if( this.parent != null ) {
			if( this.getComponents().length == 0 ) {
				this.setVisible(false);
				this.parent.remove(this);
			}
		}
	}
	
	public void setRealPosition( double x , double y ) {
		this.realPosition.x = x;
		this.realPosition.y = y;
		
		this.updateLocation();
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
}
