import java.awt.EventQueue;

import javax.swing.*;
import animation.*;
import data.Singletons;
import data.UpdateLinker;
import dataTypes.IVector2D;
import debug.DebugMap;
import prolog.AStar;
import prolog.AStarPath;
import user_interface.GUI_APPWindow;

public class APP extends JFrame {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Programa iniciado");
		
		// Inicia a interface grafica e aguarda seu carregamento
		GUI_APPWindow.startUI();
    	while( GUI_APPWindow.loaded == false ) {
    		Thread.sleep(100);
    	}
    		
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
    	
	}
	
	


}
