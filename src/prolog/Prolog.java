package prolog;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import data.*;
import dataTypes.*;
import user_interface.IsoGrid;

import org.jpl7.*;

public class Prolog {
	public static Prolog 		prolog = null;
	public static PrologState	state;
	
	public static Commands lastCommand;

	
	private Prolog(){
		Query query;
		query = new Query("consult", new Term[] {new Atom("T2.pl")});
		System.out.println("consult " + (query.hasSolution() ? "succeeded" : "failed"));
		
	}
	
	
	public static void start() {
		if( Prolog.prolog == null ) {
			Prolog.prolog = new Prolog();
		}

		// Mapa Real
		Grid grid = Singletons.gameGrid;
		Cell cell;
		String prologcmd;
		
		Prolog.doQuery("extern_instanciar_tamanho_mapa( " + grid.cols + "," + grid.rows + ")");
		
		for( int y = 0 ; y < grid.rows ; y++ ) {
			for( int x = 0 ; x < grid.cols ; x++ ) {
				cell = grid.getCell(x, y);
				prologcmd = Translations.getPrologCellTypeString(cell.type);
				if( prologcmd != null ) { 
					Prolog.doQuery("extern_adicionar_ao_mapa( (" + x + "," + y + ") , " + prologcmd + " )");
				}
			}
		}
		Prolog.doQuery("extern_adicionar_ao_mapa( (" + Singletons.heroPosition.x + "," + Singletons.heroPosition.y + ") , saida )");
		
		// Estado inicial do jogador
		Prolog.doQuery("assert( posicao(" + Singletons.heroPosition.x + "," + Singletons.heroPosition.y + ") )");	
		Prolog.doQuery("assert( orientacao(" +Translations.getPrologDirectionString(Singletons.heroDirection) + ") )" );		
		Prolog.doQuery("observar(" + Singletons.heroPosition.x + "," + Singletons.heroPosition.y + ")");		
		Prolog.doQuery("assert( municao(" +Singletons.heroAmmo + ") )" );
		Prolog.doQuery("assert( energia(" +Singletons.heroLife + ") )" );	
		Prolog.doQuery("assert( score(" +Singletons.heroScore + ") )" );	
		Prolog.doQuery("assert( total_ouros(" + Singletons.gameGrid.waterQtd + ") )" );	
		
		
		PrologInterface.updateFromProlog( UpdateTypes.ALL );
		//PrologInterface.printFromProlog( PrintTypes.ALL );
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public static void doStep() {
		while( Singletons.paused == true ) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		
		
		Query query;
		Map<String, Term>[] solution;
		int x , y , o;
		
		ArrayList<PrologCellDecidions> cells = new ArrayList<PrologCellDecidions>();		

		// Get and store Prolog Solutions
		Prolog.doQuery("acao(decidir)" );
		query = Prolog.doQuery("proximo_passo((X,Y),O,T)" , true );	
		solution = query.allSolutions();
		for( int i = 0 ; i < solution.length ; i++ ) {
			x = java.lang.Integer.parseInt( String.valueOf(solution[i].get("X")) );
			y = java.lang.Integer.parseInt( String.valueOf(solution[i].get("Y")) );
			o = java.lang.Integer.parseInt( String.valueOf(solution[i].get("O")) );
			
			cells.add( new PrologCellDecidions(x,y,o,String.valueOf(solution[i].get("T"))) );
			
			//System.out.println( "Prolog CMD: x: " + x + " | y: " + y + " | T : " + String.valueOf(solution[i].get("T")) );
		}
		
		
		// Get paths to cells
		ArrayList<AStarPath> pathList = new ArrayList<AStarPath>();
		AStarPath 		newPath = null;
		AStarPath 		bestPathClustered = null;

		
		boolean repeatFlag = false;
		for( PrologCellDecidions cell : cells ) {
			switch( cell.cmd ) {				
				case MOVE:
				case FIRE:
				case EXIT:
					if( cell.x == Singletons.heroPosition.x && cell.y == Singletons.heroPosition.y ) {
						newPath = null;
						newPath = AStar.getPath( new IVector2D(cell.x,cell.y));
					} else {
						newPath = AStar.getPath( new IVector2D(cell.x,cell.y));
					}
					
					if( newPath != null ) {
						pathList.add(newPath);	
					}
					
					if( cell.cmd == Commands.FIRE ) {
						newPath.commandList.remove( newPath.commandList.size() - 1 );
						newPath.commandList.add( Commands.FIRE );
					} 
					else if( cell.cmd == Commands.EXIT ) {
						newPath.commandList.add( Commands.EXIT );
					}
					break;	
					
					
				case REPEATLAST:
					repeatFlag = true;
					break;
			}
		}
	
		
		// Adjust cost to considerate cell Clustering		
		bestPathClustered = null;	
		Singletons.gameGrid.calculateClustering();	
		for( AStarPath aPath : pathList ) {
			aPath.clusteredCost = aPath.cost - ( Singletons.gameGrid.getCell( aPath.destiny.x , aPath.destiny.y ).clusterWeight  );
			if( bestPathClustered == null ) {
				bestPathClustered = aPath;
			} else if( bestPathClustered.clusteredCost > aPath.clusteredCost ) {
				bestPathClustered = aPath;
			}
		}
		
		
		// Use all aPaths that arent the bestClustered path as middleway to the bestCluestered Path
		AStarPath 	bestPathRaw = bestPathClustered;
		pathList.remove( bestPathClustered );
		
		for( AStarPath aPath : pathList ) {
			if( aPath.cost >= bestPathClustered.cost ) continue;
			
			newPath = null;
			newPath = AStar.getPath( aPath.destiny , aPath.destinyDirection , bestPathClustered.destiny );
			
			if( newPath != null ) {
				aPath.addToPath( newPath );
			}
			
			if( bestPathRaw.cost >= aPath.cost - newPath.clusterCellsCleared ) {
				bestPathRaw = aPath;
			}
		}
	
		
		// Tell Prolog to do the path
		int turnStep = 0;
		int turnType = 0;
		int animationsToIgnore = 0;
		if( bestPathRaw != null ) {
			for( int i = 0 ; i < bestPathRaw.commandList.size() ; i++ ) {
				Commands cmd = bestPathRaw.commandList.get(i);
				Prolog.lastCommand = cmd;
				
				// DEBUG!
				/*
				if( Singletons.heroPosition.x == 0 && Singletons.heroPosition.y == 2 ) {
					cmd = Commands.FIRE;
					Singletons.hero.setMoveSpeed(0);
				}
				*/
				// DEBUG!
	
				Prolog.doQuery("acao(" + Translations.getPrologCommandString(cmd) + ")" );			
						
				PrologInterface.updateFromProlog( UpdateTypes.ALL );
				
				Toolkit.getDefaultToolkit().sync();
				Singletons.hero.updateLayer();
				
				if( animationsToIgnore > 0 ) {
					animationsToIgnore--;
					continue;
				}
				
				switch( cmd ) {
					case TURN:
						// Check turn Type
						if( turnType == 0 ) {
							if( i+1 < bestPathRaw.commandList.size() && bestPathRaw.commandList.get(i+1) == Commands.TURN ) {
								if( i+2 < bestPathRaw.commandList.size() && bestPathRaw.commandList.get(i+2) == Commands.TURN ) {
									// 3-4 Turn
									turnType = 3;
								} else {
									// U-Turn
									turnType = 2;
								}
							} else {
								// Simple Turn
								turnType = 1;
							}
							
							turnStep = 1;
						} 
						
						// Do Simple Turn
						if( turnType == 1 ) {
							Singletons.hero.updateMoveDirection();
							Singletons.hero.acceleration = 0;
							Singletons.hero.setMoveSpeed( 0.2 );
							turnStep++;
						} 
						
						// Do U-Turn
						else if( turnType == 2 ) {
							if( turnStep == 1 ) {
								Singletons.hero.acceleration = -0.4;
								Singletons.hero.setMoveSpeed( 1 );
							}
							else {
								//Singletons.hero.acceleration = -0.5;
							}	
								
							turnStep++;
						}
						
						// Do 3/4 Turn
						else if( turnType == 3 ) {
							if( turnStep == 1 ) {
								Singletons.hero.acceleration = -0.4;
								Singletons.hero.setMoveSpeed( 1 );
							}
							else if( turnStep == 2 ) {
								Singletons.hero.acceleration *= 2;
							} else {
								Singletons.hero.updateMoveDirection();
								Singletons.hero.acceleration = 0;
								Singletons.hero.setMoveSpeed( 0.6 );
							}
								
							turnStep++;
						}
						
						Singletons.hero.setTurn( Singletons.heroDirection );
						break;
						
					case MOVE:
						DVector2D target = IsoGrid.getTileCenterRealPosition( Singletons.heroPosition.x , Singletons.heroPosition.y );
						turnType = 0;
						
						// Preview next steps
						if( i+1 < bestPathRaw.commandList.size() ) {
							if( bestPathRaw.commandList.get(i+1) == Commands.TURN ) {
								// Turn Drift
								int turns = 1;
								animationsToIgnore++;
								
								/*
								if( i+2 < bestPathRaw.commandList.size() && bestPathRaw.commandList.get(i+2) == Commands.TURN  ) {
									
									//  U-Turn Drift
									turns++;
									animationsToIgnore++;
									
									if( i-1 >= 0 && bestPathRaw.commandList.get(i-1) == Commands.MOVE ) {
										if( i+3 < bestPathRaw.commandList.size() && bestPathRaw.commandList.get(i+3) == Commands.TURN  ) {
											//  3/4 Turn Drift
											turns++;
											animationsToIgnore++;
										}
									}
								}
								*/
								
								
								Singletons.hero.setMaxMoveSpeed( 1 );
								Singletons.hero.setTargetPosition( target.x , target.y , true , turns );
								
 							} else {
 								// Next command is another move, can speedup a bit
								Singletons.hero.setMaxMoveSpeed( 1.3 );
								Singletons.hero.setTargetPosition( target.x , target.y );
							}
						} else {
							Singletons.hero.setMaxMoveSpeed( 1 );
							Singletons.hero.setTargetPosition( target.x , target.y );
						}

						break; // Case Move
						
					case FIRE:
						Singletons.hero.shot();
						break;
						
					case EXIT:
						Singletons.victory = true;
						break;
						
				}
				
				while( Singletons.hero.isStopped() == false || Singletons.paused == true ) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}

			}
		}
		
		PrologInterface.updateFromProlog( UpdateTypes.ALL );
		//PrologInterface.printFromProlog( PrintTypes.ALL );
	}
	
	public static Query doQuery(String string ) {
		return Prolog.doQuery(string, false );
	}
	
	public static Query doQuery(String string , boolean failAllowed ) {
		Query query = new Query(string);
		if( query.hasSolution() == false && failAllowed == false ) {
			System.out.println( "DEBUG FALSE: " + string );
		}
		return query;
	}
}
