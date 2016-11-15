import java.awt.EventQueue;
import java.awt.event.WindowEvent;

import javax.swing.*;
import animation.*;
import data.Singletons;
import data.UpdateLinker;
import dataTypes.DVector2D;
import dataTypes.IVector2D;
import debug.DebugMap;
import prolog.AStar;
import prolog.AStarPath;
import prolog.Prolog;
import random_map_generator.Generator;
import user_interface.GUI_APPWindow;
import user_interface.IsoGrid;
import user_interface.Splash;

public class APP extends JFrame {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Programa iniciado");
		
		/*Generator.numero_buracos = 8;
		Generator.dimensao_x = 12;
		Generator.dimensao_y = 12;
		Generator.numero_inimigos = 4;
		Generator.numero_ouros = 3;
		Generator.numero_powerup = 3;
		Generator.numero_teleportes = 3;
		Generator.GerarMapa();*/
		
		// Splash Screen
		Splash.start();
    	while( Splash.loaded == false ) {
    		Thread.sleep(100);
    	}
		
		// Inicia a interface grafica e aguarda seu carregamento
		GUI_APPWindow.startUI();
    	while( GUI_APPWindow.loaded == false ) {
    		Thread.sleep(100);
    	}
    	while( Splash.finished == false ) {
    		Thread.sleep(100);
    	}
    	Splash.splash.dispatchEvent(new WindowEvent(Splash.splash, WindowEvent.WINDOW_CLOSING));
    	
    	System.out.println("GUI Loaded");
    	
    	// Exemplo de alterações nos dados e q interface atualizado de acordo
    	/*    	
    	IVector2D pPos = new IVector2D( Singletons.heroPosition.x , Singletons.heroPosition.y );

    	Singletons.gameGrid.getCell( pPos.x + 1 , pPos.y + 0 ).discovered = true;    	
    	Singletons.gameGrid.getCell( pPos.x + 0 , pPos.y + 1 ).discovered = true;
    	Singletons.gameGrid.getCell( pPos.x + 1 , pPos.y + 1 ).discovered = true;
		*/
    	
    	
    	/* Exemplo de Debug do A*
    	DebugMap.discoverEntireMap();
    	AStarPath asp = AStar.getPath( new IVector2D(10,0) );
    	DebugMap.printAStarPath(asp);
 		*/
    	
    	
    	//DebugMap.setFrontinerAround( Singletons.heroPosition );
    	
    
    	
    	
    	Prolog.start();
    	while( 1 == 1 ) {
    		Prolog.doStep();
    	}
    	
    	
    	/*
    	DebugMap.discoverEntireMap();

    	Singletons.hero.moveDirection = Math.PI;
    	Singletons.hero.setTargetPosition( Singletons.hero.realPosition.x + 2000 , Singletons.hero.realPosition.y + 2000 );
    	
    	Singletons.hero.setTargetDirection( 0 );
    	*/
    	
	}
	
	


}
