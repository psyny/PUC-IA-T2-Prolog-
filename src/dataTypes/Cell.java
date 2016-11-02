package dataTypes;

import java.util.ArrayList;

import user_interface.*;

public class Cell {
	// Apenas para Consulta
	public IVector2D 			position = new IVector2D( 0 , 0 );
	public CellType 			type = CellType.CLEAN;
	
	// Atualizar com o ProLog
	public boolean		discovered = false;
	public boolean		destroyed = false;
	
	// Dados do RAFAEL
	public ArrayList<ActorSensor> 		sensorList = new ArrayList<ActorSensor>();
	public ArrayList<ActorStormBorder>	borderEffectList = new ArrayList<ActorStormBorder>();
	public SceneTile 	tile;
	public ActorStorm	stormActor;
	public Actor		contentActor;
	public ActorGrid	visibleGrid;

	
	// Dados do MAURICIO
	
	// Dados do LUCAS
	

	public Cell( int x , int y ) {
		this.position.x = x;
		this.position.y = y;
		this.type = CellType.CLEAN;
	}
	
	public boolean addSensor( ActorSensor sensor ) {
		boolean addFlag = true;
		for( ActorSensor act : this.sensorList ) {
			if( act.sensorType == sensor.sensorType ) {
				addFlag = false;
				break;
			}
		}
		
		if( addFlag == true ) {
			this.sensorList.add(sensor);
		}
		
		return addFlag;
	}
	
	public boolean haveSensor( CellType type ) {
		if( this.sensorList.size() == 0 ) {
			return false;
		}
		
		for( ActorSensor sensor : this.sensorList ) {
			if( sensor.sensorType == type ) {
				return true;
			}
		}
		return false;
	}
}
