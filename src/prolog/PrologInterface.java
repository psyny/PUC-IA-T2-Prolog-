package prolog;

import java.util.Map;

import org.jpl7.Query;
import org.jpl7.Term;

import data.Singletons;
import dataTypes.Cell;
import user_interface.EffectType;

abstract public class PrologInterface {

	public static void updateFromProlog( UpdateTypes updateType ) {
		Query query;
		Map<String, Term>[] solution;
		int x , y , w , z , o;
		
		switch( updateType ) {
			case ALL:
				PrologInterface.updateFromProlog( UpdateTypes.POSITION );
				PrologInterface.updateFromProlog( UpdateTypes.DIRECTION );
				PrologInterface.updateFromProlog( UpdateTypes.MAP );
				PrologInterface.updateFromProlog( UpdateTypes.LIFE );
				PrologInterface.updateFromProlog( UpdateTypes.SCORE );
				PrologInterface.updateFromProlog( UpdateTypes.AMMO );
				PrologInterface.updateFromProlog( UpdateTypes.WATER );
				PrologInterface.updateFromProlog( UpdateTypes.STATE );
				break;
		
			case POSITION:
				query = Prolog.doQuery("posicao(X,Y)" );	
				solution = query.allSolutions();
				x = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				y = java.lang.Integer.parseInt( String.valueOf(solution[0].get("Y")) );	
				
				Singletons.heroPosition.x = x;
				Singletons.heroPosition.y = y;	
				break;
				
			case DIRECTION:
				query = Prolog.doQuery("orientacao(X)" );	
				solution = query.allSolutions();
				
				Singletons.heroDirection = Translations.getJavaDirection( String.valueOf(solution[0].get("X")) );
				break;
				
			case MAP:
				query = Prolog.doQuery("certeza( ( X , Y ), O )" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					x = java.lang.Integer.parseInt( String.valueOf(solution[i].get("X")) );
					y = java.lang.Integer.parseInt( String.valueOf(solution[i].get("Y")) );
					
					Singletons.gameGrid.getCell(x, y).discovered = true;
					Singletons.gameGrid.getCell(x, y).frontier = false;
				}	
							
				query = Prolog.doQuery("fronteira(X,Y)" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					x = java.lang.Integer.parseInt( String.valueOf(solution[i].get("X")) );
					y = java.lang.Integer.parseInt( String.valueOf(solution[i].get("Y")) );
					
					Singletons.gameGrid.getCell(x, y).frontier = true;
				}	
				
				query = Prolog.doQuery("consumido(X,Y)" , true );	
				solution = query.allSolutions();
				Cell tCell;
				for( int i = 0 ; i < solution.length ; i++ ) {
					x = java.lang.Integer.parseInt( String.valueOf(solution[i].get("X")) );
					y = java.lang.Integer.parseInt( String.valueOf(solution[i].get("Y")) );
					
					tCell = Singletons.gameGrid.getCell(x, y);
					tCell.destroyed = true;
				}	
				
				break;
				
			case LIFE:
				query = Prolog.doQuery("energia(X)" );	
				solution = query.allSolutions();
				
				int newLife = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				if( newLife < Singletons.heroLife ) {
					Singletons.actorScene.createEffectInTile( EffectType.EXPLOSION_MINE , 10 , Singletons.heroPosition.x , Singletons.heroPosition.y );
				}
				Singletons.heroLife = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				break;
				
			case SCORE:
				query = Prolog.doQuery("score(X)" );	
				solution = query.allSolutions();
				
				Singletons.heroScore = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				break;
				
			case AMMO:
				query = Prolog.doQuery("municao(X)" );	
				solution = query.allSolutions();
				
				int newAmmo = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				Singletons.heroAmmo = newAmmo;
				
				break;
				
			case WATER:
				query = Prolog.doQuery("total_ouros(X)" );	
				solution = query.allSolutions();
				int catched = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				Singletons.waterCatched = Singletons.gameGrid.waterQtd - catched;
				break;
				
			case STATE:
				query = Prolog.doQuery("terminou(X)" , true );	
				solution = query.allSolutions();
				
				String state = String.valueOf(solution[0].get("X"));
				
				switch( state ) {
					case "nao":
						break;
						
					case "saiu_do_labirinto":
						Singletons.victory = true;
						break;
					
					case "morreu_para_buraco":
					case "morreu_para_inimigo":
						Singletons.death = true;
						break;
				}

				
				break;
				
		}
	}
	
	public static void printFromProlog( PrintTypes printType ) {
		Query query;
		Map<String, Term>[] solution;
		int x , y , w , z , o;
		
		switch( printType ) {
			case ALL:
				System.out.println("Estado do Prolog:");
				PrologInterface.printFromProlog( PrintTypes.POSITION );				
				PrologInterface.printFromProlog( PrintTypes.SENSORS );
				PrologInterface.printFromProlog( PrintTypes.DOUBTS );
				PrologInterface.printFromProlog( PrintTypes.KNOWN );
				PrologInterface.printFromProlog( PrintTypes.AMMO );			
				PrologInterface.printFromProlog( PrintTypes.ENERGY );
				PrologInterface.printFromProlog( PrintTypes.SCORE );	
				PrologInterface.printFromProlog( PrintTypes.WATER );	
				PrologInterface.printFromProlog( PrintTypes.DEBUG );					
				break;
		
			case SENSORS:
				query = Prolog.doQuery("sensor( (X,Y), (W,Z), O )" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					System.out.println("Sensor: " 
							+ String.valueOf(solution[i].get("X")) + " | " + String.valueOf(solution[i].get("Y")) + " | "
							+ String.valueOf(solution[i].get("W")) + " | " + String.valueOf(solution[i].get("Z")) + " | "
							+ String.valueOf(solution[i].get("O")) );

				}
				break;
				
			case POSITION:
				query = Prolog.doQuery("posicao(X,Y)" );	
				solution = query.allSolutions();
				x = java.lang.Integer.parseInt( String.valueOf(solution[0].get("X")) );
				y = java.lang.Integer.parseInt( String.valueOf(solution[0].get("Y")) );	
				System.out.println( "Posicao Atual: x: " + x + " | y: " + y );

				break;
				
			case DOUBTS:
				query = Prolog.doQuery("fronteira(X,Y)" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					System.out.println("Fronteira: " 
							+ String.valueOf(solution[i].get("X")) + " | " + String.valueOf(solution[i].get("Y")) );
				}
				break;
				
			case KNOWN:
				query = Prolog.doQuery("certeza( (X,Y), O )" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					System.out.println("Certeza: " 
							+ String.valueOf(solution[i].get("X")) + " | " + String.valueOf(solution[i].get("Y")) + " | " + String.valueOf(solution[i].get("O"))  );
				}
				break;
				
			case ENERGY:
				query = Prolog.doQuery("energia( V )" );	
				solution = query.allSolutions();
				System.out.println("Energia: " 	+ String.valueOf(solution[0].get("V")) );
				break;
				
			case SCORE:
				query = Prolog.doQuery("score( V )" );	
				solution = query.allSolutions();
				System.out.println("Score: " 	+ String.valueOf(solution[0].get("V")) );
				break;		
				
			case AMMO:
				query = Prolog.doQuery("municao( V )" );	
				solution = query.allSolutions();
				System.out.println("Municao: " 	+ String.valueOf(solution[0].get("V")) );
				break;	
				
			case WATER:
				query = Prolog.doQuery("total_ouros( V )" );	
				solution = query.allSolutions();
				System.out.println("Aguas no Mapa: " 	+ String.valueOf(solution[0].get("V")) );
				break;	
				
			case DEBUG:
				query = Prolog.doQuery("dano_dado( X  )" );	
				solution = query.allSolutions();
				for( int i = 0 ; i < solution.length ; i++ ) {
					System.out.println("Dano causado: " 
							+ String.valueOf(solution[i].get("X")) );
				}
				break;
		}
	}	
	
}
