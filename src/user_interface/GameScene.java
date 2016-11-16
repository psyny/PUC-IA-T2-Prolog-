package user_interface;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JLabel;

import animation.*;
import data.*;
import dataTypes.*;

@SuppressWarnings("serial")
public class GameScene extends Scene {
	private long delayLightningMax = 1000;
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
			IVector2D randPos;
			
			randPos = new IVector2D( Singletons.heroPosition.x - 3 + ((int)(Math.random()*7)) , Singletons.heroPosition.y - 3 + ((int)(Math.random()*7))  );
			
			this.createEffectInTile( EffectType.LIGHTNING , 20 , randPos.x , randPos.y , Math.random() , Math.random() );
			
			randPos = new IVector2D( Singletons.heroPosition.x - 3 + ((int)(Math.random()*7)) , Singletons.heroPosition.y - 3 + ((int)(Math.random()*7))  );	
			this.createEffectInTile( EffectType.DIRT , 20 , randPos.x , randPos.y , Math.random() , Math.random() );
		} else {
			delayLightning -= time;
		}
		
	}
	
	@Override
	public void setLayer( Component comp , int layer ) {
		super.setLayer( comp , (((Actor)comp).getProjectionCenter().y / 20 ) + layer );
	}
	
	public void createEffectInTile( EffectType effect , int layer ,  int x , int y ) {
		this.createEffectInTile( effect , layer, x, y , 0.5 , 0.5 );
	}
	
	public void createEffectInTile( EffectType effect , int layer ,  int x , int y , double ox , double oy ) {
		ActorEffect actor = ActorFactory.fabricateEffect( effect );
		actor.setRealPosition( ( x + ox ) * IsoGrid.isoTileSize.x , ( y + oy ) * IsoGrid.isoTileSize.y );
		
		this.add( actor );
		Toolkit.getDefaultToolkit().sync();
		this.setLayer( actor , layer);
		
		actor.parent = this;
		actor.stage = this;		
	}
	
	public void createEffectInPosition( EffectType effect , int layer ,  int x , int y ) {
		ActorEffect actor = ActorFactory.fabricateEffect( effect );
		
		this.add( actor );
		Toolkit.getDefaultToolkit().sync();
		actor.setLocation(x, y);
		this.setLayer( actor , layer);
		
		actor.parent = this;
		actor.stage = this;
	}
	
	public void createEffectInRealPosition( EffectType effect , int layer ,  double x , double y ) {
		synchronized(this) {
			ActorEffect actor = ActorFactory.fabricateEffect( effect );
	
			this.add( actor );		
			Toolkit.getDefaultToolkit().sync();
			actor.setRealPosition(x, y);
			this.setLayer( actor , layer);
			
			actor.parent = this;
			actor.stage = this;		
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
						Singletons.hero.setOrtogonalDirection( Singletons.heroDirection , true );
						Singletons.hero.updateAnimation();
						break;
				}
				
				cell.contentActor = newActor;
				if( newActor != null ) {
					this.add( newActor );
					newActor.stage = this;
					newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
					this.setLayer( newActor , 5 );
					newActor.setVisible( false );
				}
							
				// Create Storm
				newActor = ActorFactory.fabricate( ActorTypes.STORM );
				newActor.stage = this;
				cell.stormActor = (ActorStorm)newActor;			
				if( Singletons.stormGroup == null ) {
					Singletons.stormGroup = cell.stormActor;
				} else {
					cell.stormActor.getAnimatedSprite().group = Singletons.stormGroup.getAnimatedSprite();
				}
				
				newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
				this.add( newActor );
				newActor.stage = this;
				this.setLayer( newActor , 1 );

				// Create grid
				newActor = ActorFactory.fabricate( ActorTypes.GRID );
				newActor.stage = this;
				cell.visibleGrid = (ActorGrid)newActor;			
				newActor.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
				this.add( newActor );
				this.setLayer( newActor , 1 );
				
				// Create Sensors and borders
				ArrayList<Cell> neighbors = Singletons.gameGrid.getNeighbors( cell.position.x , cell.position.y );
				for( Cell nCell : neighbors ) {
					if( sensor != null ) {
						// Create sensors
						ActorSensor newSensor = ActorFactory.fabricateSensor( sensor );
						newSensor.stage = this;
						nCell.addSensor( newSensor);
						newSensor.sensorOf = cell;
						newSensor.sensorPosition = nCell;
						newSensor.setRealPosition( ( nCell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( nCell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
						this.add( newSensor );
						this.setLayer( newSensor , 9 );
					}
					
					// Create borders
					ActorStormBorder newBorder = ActorFactory.fabricateBorder( nCell , cell );	
					newBorder.stage = this;
					newBorder.getAnimatedSprite().group = Singletons.stormGroup.getAnimatedSprite();
					newBorder.setRealPosition( ( cell.position.x + 0.5 ) * IsoGrid.isoTileSize.x , ( cell.position.y + 0.5 ) * IsoGrid.isoTileSize.y );
					cell.borderEffectList.add( newBorder );
					this.add( newBorder );	
					this.setLayer( newBorder , 3 );
				}
			}
		}
	}

}
