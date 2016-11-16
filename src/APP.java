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
		
		int reply = JOptionPane.showConfirmDialog(null, "Random Map?", "Log Max" , JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
        	Singletons.randomMap = true;
        }
        else {
        	Singletons.randomMap = false;
        }
		

        if( Singletons.randomMap == true  ) {
			Generator g = new Generator(8, 4, 4, 3, 3, 12, 12);
			try
			{
				g.GerarMapa();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
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
    	
    	

    	Prolog.start();
    	while( 1 == 1 ) {
    		Prolog.doStep();
    	}
    	
    	
	}
	
	


}
