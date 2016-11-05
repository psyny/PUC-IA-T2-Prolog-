package user_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import animation.*;
import data.MapLoader;
import data.Singletons;
import data.UpdateLinker;
import dataTypes.Cell;
import dataTypes.IVector2D;

@SuppressWarnings("serial")
public class GUI_APPWindow extends JFrame {
	static GUI_APPWindow APPWin;
	
	public static boolean loaded = false;
	
	public GUI_APPWindow() {
    	setSize(1024, 768);

    	setTitle("Mad Prolog");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	
    	// Menu
    	JMenuBar menuBar = new JMenuBar();
    	JMenu menuExibir = new MenuView();
    	menuBar.add( menuExibir );
    	
    	
    	//JMenuItem menuItem = new JMenuItem("Nevoa");
    	//menuExibir.add( menuItem );
    	
    	//JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Nevoa");
    	//menuExibir.add( cbMenuItem );
    	
    	this.setJMenuBar( menuBar );
	}
	
	
	public static void startUI() {
    	// Load Data
    	MapLoader.loadFile();
    	IVector2D sceneSize = IsoGrid.getOrtogonalSize();	
		
		
		
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 
            	// Main Windows
            	GUI_APPWindow.APPWin = new GUI_APPWindow();
                Container mainContentPane = GUI_APPWindow.APPWin.getContentPane();
                mainContentPane.setLayout( null );

                // Actors Layer               
                Scene mainScene = new Scene( 0 , 0 , sceneSize.x , sceneSize.y );
                
                // OverAll Layer
                GameScene actorsLayer = new GameScene( 0 , 0 , sceneSize.x , sceneSize.y ); 
                mainScene.add(actorsLayer);
                mainScene.setLayer(actorsLayer,1);
 
                // Camera Controls
                Singletons.gameCamera = new Camera( mainScene , 0 , 0 ); 
                Camera gameCamera = Singletons.gameCamera;
                gameCamera.setBounds(0, 0, 1010 , 730);
                gameCamera.setTarget( 0 , 0 );
                
                mainContentPane.add( gameCamera );

                // Test Animation
        		AnimatedSprite anim = new AnimatedSprite( "testExplosion.txt");
        		anim.setLocation( 0, 100 );   
        		anim.insertInto( mainScene );
        		anim.playAnimation( 5 , LoopType.REPEAT );
        		
        		// Game Grid Layer - ( Terrain )
        		IsoGrid groundLayer = new IsoGrid( Singletons.gameGrid );
        		mainScene.add( groundLayer );
        		mainScene.setLayer(groundLayer, 0);
        		
        		// Load Cell Types
        		actorsLayer.loadCells( Singletons.gameGrid );
        		
        		// Set Window to Visible
            	GUI_APPWindow.APPWin.setVisible(true);
            	
            	// Start the sync
            	UpdateLinker.start();
            	
            	// Call loaded
            	GUI_APPWindow.loaded = true;
            	Toolkit.getDefaultToolkit().sync();
            }
        });
	}

}




class MenuView extends JMenu {
	class CB_Fog extends JCheckBoxMenuItem implements ItemListener  {
		public CB_Fog( ) {
			super( "Ocultar Regiões Desconhecidas");
			this.addItemListener(this);
			this.setState( true );
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.fogUndiscovery = this.getState();
		}
	}
	

	
	class CB_Camera extends JCheckBoxMenuItem implements ItemListener  {
		public CB_Camera( ) {
			super( "Fixar Camera No Heroi");
			this.addItemListener(this);
			this.setState( true );
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.centerOnHero = this.getState();
		}
	}
	
	
	class CB_Sensors extends JCheckBoxMenuItem implements ItemListener  {
		public CB_Sensors( ) {
			super( "Exibir Sensores");
			this.addItemListener(this);
			this.setState( true );
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.showSensors = this.getState();
		}
	}
	
	class CB_SmartSensors extends JCheckBoxMenuItem implements ItemListener  {
		public CB_SmartSensors( ) {
			super( "Ocultar Sensores de Celulas Conhecidas");
			this.addItemListener(this);
			this.setState( true );
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.hideDiscoveredSensors = this.getState();
		}
	}
	
	class CB_Grid extends JCheckBoxMenuItem implements ItemListener  {
		public CB_Grid( ) {
			super( "Exibir Grade");
			this.addItemListener(this);
			this.setState( false );
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.showGrid = this.getState();
		}
	}
	
	// Constructor
	public MenuView( ) {
		super( "Opções" );
		this.add( new CB_Fog() );
		this.add( new CB_Camera() );
		this.add( new CB_Sensors() );
		this.add( new CB_SmartSensors() );
		this.add( new CB_Grid() );
	}
}
