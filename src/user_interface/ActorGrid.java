package user_interface;

import javax.swing.JLabel;

import animation.*;
import data.Singletons;
import dataTypes.*;

@SuppressWarnings("serial")
public class ActorGrid extends Actor {
	private TileSetSprite 	sprite;
	private JLabel			gridLabel;
	
	private boolean			labelSet = false;

	public ActorGrid(int sizeX, int sizeY) {
		super(sizeX, sizeY);

		this.addSprite( "grid.txt" , new IVector2D(5,5) , 0 , false );	
		this.sprite = ((TileSetSprite)this.getSprite(0));
		this.sprite.setTile(1, 2);
		
		this.gridLabel 	= new JLabel("0,0");
		this.gridLabel.setVisible( true );
		this.gridLabel.setBounds( 40 , 80 , 100, 50);
		this.add(gridLabel);
		this.setLayer(gridLabel, 10000000 );
	}

	public void updateStatus( Cell cell ) {
		if( cell.visibleGrid != this ) {
			return;
		}
		
		if( this.labelSet == false ) { 
			this.labelSet = true;
			this.gridLabel.setText( cell.position.x + " , " + cell.position.y );
		}
		
		if( Singletons.showGrid == true ) {
			if( cell.discovered == true || Singletons.fogUndiscovery == false ) {
				if( this.isVisible() == false ) {
					this.setVisible( true );
					
					if( cell.discovered == true ) {
						this.sprite.setTile(1, 2);
					} else {
						this.sprite.setTile(1, 3);
					}
				}
			} else {
				if( this.isVisible() == true ) {
					this.setVisible( false );
				}
			}
		} else {
			if( this.isVisible() == true ) {
				this.setVisible( false );
			}
		}
	}	
	
}
