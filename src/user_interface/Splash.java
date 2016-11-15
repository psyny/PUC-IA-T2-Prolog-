package user_interface;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

import animation.AnimatedSprite;
import animation.Camera;
import animation.Scene;
import data.Singletons;
import dataTypes.IVector2D;

public class Splash extends JFrame {
	public static Splash splash = null;
	public static IVector2D size = new IVector2D( 683,670 );
	
	public static boolean loaded = false;
	public static boolean finished = false;
	
	public Splash() {
    	setSize( size.x , size.y );

    	setTitle("Log Max - Splash");
    	//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	setUndecorated(true);
	}	
	
	static public void start() { 
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 
            	Splash.splash = new Splash();
            	Splash splash = Splash.splash;
                Container mainContentPane = splash.getContentPane();
                mainContentPane.setLayout( null );
                
                AnimatedSprite splashImg = new AnimatedSprite( "splash.txt" );
                splashImg.insertInto( mainContentPane );
                splashImg.setLocation(0, 0);
                
                splash.setVisible(true);
                
                loaded = true;
                Toolkit.getDefaultToolkit().sync();
            }
        } );
        
    	
    	// Music Player
    	MusicPlayer.init();
        
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        finished = true;
	}
	
}
