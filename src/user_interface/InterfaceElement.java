package user_interface;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JLayeredPane;

import animation.*;
import dataTypes.IVector2D;


class MA extends MouseAdapter {
	InterfaceElement ie;
	
	public MA( InterfaceElement ie ) {
		this.ie = ie;
	}
	
    public void mouseClicked (MouseEvent event)
    {
    	ie.clicked();
    }
}

public class InterfaceElement extends JLayeredPane implements Animable {
	protected ArrayList<Sprite>			spriteList 		= new ArrayList<Sprite>();
	protected ArrayList<AnimatedSprite>	aSpriteList		= new ArrayList<AnimatedSprite>();
	
	public 	IVector2D size;
	public 	IVector2D position = new IVector2D(0 , 0);
	
	public Sprite lastAddedSprite = null;
	
	protected boolean disposed = false;
	
	public InterfaceElement( int sizeX , int sizeY ) {
		this.size = new IVector2D( sizeX , sizeY );
		addMouseListener (new MA(this));	
		this.updateBounds();
	}
	
	public void clicked() {
		return;
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
			//((AnimatedSprite)aSpr).playAnimation();
		} else {
			aSpr = new TileSetSprite( fileName );
		}

		this.addSprite( aSpr , relativePosition, layer);
	}
	
	public void addSprite( Sprite sprite , IVector2D relativePosition , int layer ) {
		IVector2D spriteSize = sprite.getDimension();
		if( spriteSize.x > this.size.x ) {
			this.size.x = spriteSize.x;
			this.updateBounds();
		}
		if( spriteSize.y > this.size.y ) {
			this.size.y = spriteSize.y;
			this.updateBounds();
		}
		
		this.spriteList.add( sprite );
		if ( sprite instanceof Animable ) {
			this.aSpriteList.add( (AnimatedSprite)sprite );
		}
		
		sprite.insertInto( this );
		this.setLayer( sprite , layer);
		
		IVector2D spritePosition =  relativePosition;
		
		lastAddedSprite = sprite;
		
		sprite.setBounds(spritePosition.x, spritePosition.y, spriteSize.x, spriteSize.y);
		
		
	}
	
	private void updateBounds() {
		this.setBounds( position.x , position.y , size.x , size.y );
	}
	
	@Override
	public void passTime(long time) {
		if( this.aSpriteList.size() > 0 ) {
			for( AnimatedSprite aSpr : this.aSpriteList ) {
				aSpr.passTime( time );
			}
		}
	}
	
	public void setPosition( int x , int y ) {
		this.position.x = x;
		this.position.y = y;
		this.updateBounds();
	}
	
}
