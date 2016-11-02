package user_interface;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.*;
import data.*;
import dataTypes.*;

@SuppressWarnings("serial")
public class GameScene extends Scene {
	private long delayLightningMax = 250;
	private long delayLightning = delayLightningMax;

	public GameScene(int x, int y, int w, int h) {
		super(x, y, w, h);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
		
		
		// Create Lightning
		if( delayLightning <= 0 ) {
			delayLightning += delayLightningMax;
			
			IVector2D randPos = new IVector2D( (int)(Math.random() * Singletons.gameGrid.cols) , (int)(Math.random() * Singletons.gameGrid.rows) );	
			Actor lightning = ActorFactory.fabricateEffect( EffectType.LIGHTNING );
			lightning.setRealPosition( ( randPos.x + Math.random() ) * IsoGrid.isoTileSize.x , ( randPos.y + Math.random() ) * IsoGrid.isoTileSize.y );
			this.add( lightning );
			this.setLayer( lightning  , randPos.x - randPos.y + 1);
			lightning.parent = this;
		} else {
			delayLightning -= time;
		}
		
	}
	
	public void loadCells( Grid gameGrid ) {
		// Populate Area
		for( ArrayList<Cell> alCell : gameGrid.cells ) {
			for( Cell cell : alCell ) {
				
				//cell.tile = new SceneTile( );
				//cell.tile.cell = cell;
				//this.add( cell.tile );
				//this.setLayer( cell.tile , cell.position.x - cell.position.y );
				
				IVector2D projectedPosition = IsoGrid.getTileCenterProjection( cell.position.x , cell.position.y );

				// Cell Type
				Actor newActor = null;
				CellType sensor = null;
				switch( cell.type ) {
					case CYCLONE:
						newActor = ActorFactory.fabricate( ActorTypes.CYCLONE );
						sensor = CellType.CYCLONE;
						break;
					
					case LANDMINE:
						newActor = ActorFactory.fabricate( ActorTypes.LANDMINE );
						sensor = CellType.LANDMINE;
						break;
						
					case WATER:
						newActor = ActorFactory.fabricate( ActorTypes.WATER );
						break;
						
					case GAS:
						newActor = ActorFactory.fabricate( ActorTypes.GAS );
						break;
						
					case ENEMY:
						newActor = ActorFactory.fabricate( ActorTypes.ENEMY );
						sensor = CellType.ENEMY;
						break;
						
					case BOSS:
						newActor = ActorFactory.fabricate( ActorTypes.BOSS );
						sensor = CellType.BOSS;
						break;
						
					case HERO:
						newActor = ActorFactory.fabricate( ActorTypes.HERO );
						Singletons.hero = (ActorHero)newActor;
						break;
				}
				
				cell.contentActor = newActor;
				if( newActor != null ) {
					this.add( newActor );
					newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
					this.setLayer( newActor , cell.position.x - cell.position.y );
					newActor.setVisible( false );
				}
							
				// Create Storm
				newActor = ActorFactory.fabricate( ActorTypes.STORM );
				cell.stormActor = (ActorStorm)newActor;			
				newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
				this.add( newActor );
				cell.stormActor.setLayerConfigs( cell.position.x - cell.position.y , this );
				
				// Create grid
				newActor = ActorFactory.fabricate( ActorTypes.GRID );
				cell.visibleGrid = (ActorGrid)newActor;			
				newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
				this.add( newActor );
				this.setLayer( newActor , cell.position.x - cell.position.y - 100000 );
				
				// Create Sensors and borders
				ArrayList<Cell> neighbors = Singletons.gameGrid.getNeighbors( cell.position.x , cell.position.y );
				for( Cell nCell : neighbors ) {
					if( sensor != null ) {
						// Create sensors
						ActorSensor newSensor = ActorFactory.fabricateSensor( sensor );
						nCell.addSensor( newSensor);
						newSensor.sensorOf = cell;
						newSensor.sensorPosition = nCell;
						newSensor.setRealPosition( ( nCell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( nCell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
						this.add( newSensor );
						this.setLayer( newSensor , nCell.position.x - nCell.position.y + 1 );
					}
					
					// Create borders
					ActorStormBorder newBorder = ActorFactory.fabricateBorder( nCell , cell );	
					newBorder.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
					cell.borderEffectList.add( newBorder );
					this.add( newBorder );
					this.setLayer( newBorder , cell.position.x - cell.position.y - 0 );	
				}
			}
		}
	}

}
