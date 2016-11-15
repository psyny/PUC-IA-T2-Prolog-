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
import dataTypes.DVector2D;
import dataTypes.IVector2D;



@SuppressWarnings("serial")
public class GUI_APPWindow extends JFrame {
	static GUI_APPWindow APPWin;
	
	public static boolean loaded = false;
	
	public GUI_APPWindow() {
    	setSize(1024, 768);

    	setTitle("Log Max - Fury Load");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	
    	// Menu
    	JMenuBar menuBar = new JMenuBar();
    	JMenu menuExibir = new MenuView();
    	menuBar.add( menuExibir );
    	
    	menuExibir = new MenuControl();
    	menuBar.add( menuExibir );

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
                
                // Layered Structure
                JLayeredPane mainStructure = new JLayeredPane();
                mainStructure.setLayout(null);
                mainStructure.setBounds(0, 0, Singletons.gameCameraArea.x , Singletons.gameCameraArea.y );
                mainContentPane.add( mainStructure );
                
                // Actors Layer               
                Scene mainScene = new Scene( 0 , 0 , sceneSize.x , sceneSize.y );
                
                // OverAll Layer
                GameScene actorsLayer = new GameScene( 0 , 0 , sceneSize.x , sceneSize.y ); 
                Singletons.actorScene = actorsLayer;
                mainScene.add(actorsLayer);
                mainScene.setLayer(actorsLayer,1);
 
                // Camera Controls
                Singletons.gameCamera = new Camera( mainScene , 0 , 0 ); 
                Camera gameCamera = Singletons.gameCamera;
                gameCamera.setBounds(0, 0, Singletons.gameCameraArea.x , Singletons.gameCameraArea.y );
                gameCamera.setTarget( 0 , 0 );
                
                mainStructure.add( gameCamera );
                mainStructure.setLayer( gameCamera , 10 );

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
        		
                // Game Stat Menu
                JPanel gameStatBar = new JPanel();
                gameStatBar.setLayout( null );
                gameStatBar.setBounds( 0 , 0 , Singletons.gameCameraArea.x - 16  , 40 );
                gameStatBar.setBackground( new Color(0,0,0,(int)(255*0.8) ) );
                mainStructure.add( gameStatBar );
                mainStructure.setLayer( gameStatBar , 20 );
                
                InterfaceElementHealthBar healthBar = new InterfaceElementHealthBar();
                Singletons.gameCamera.addToAnimableList( healthBar );
                gameStatBar.add( healthBar );
                healthBar.setPosition(5, 5);

                InterfaceElementAmmoBar ammoBar = new InterfaceElementAmmoBar();
                Singletons.gameCamera.addToAnimableList( ammoBar );
                gameStatBar.add( ammoBar );
                ammoBar.setPosition(340, 5);  
                
                InterfaceElementWaterBar waterBar = new InterfaceElementWaterBar();
                Singletons.gameCamera.addToAnimableList( waterBar );
                gameStatBar.add( waterBar );
                waterBar.setPosition(650, 5); 
                
                InterfaceElementScoreBar scoreBar = new InterfaceElementScoreBar();
                Singletons.gameCamera.addToAnimableList( scoreBar );
                gameStatBar.add( scoreBar );
                scoreBar.setPosition(800, 5);               
                
                // Splashes
                JPanel splashArea = new JPanel();
                splashArea.setLayout( null );
                splashArea.setBounds( 0 , 0 , Singletons.gameCameraArea.x  , Singletons.gameCameraArea.y );
                splashArea.setOpaque( false );
                mainStructure.add( splashArea );
                mainStructure.setLayer( splashArea , 25 );
                
                InterfaceElement newIE;
                
               
                newIE = new InterfaceElementVictorySplash( );
                Singletons.gameCamera.addToAnimableList( newIE );
                splashArea.add( newIE );
				newIE.setPosition( 270, 20);
				
                newIE = new InterfaceElementDeathSplash( );
                Singletons.gameCamera.addToAnimableList( newIE );
                splashArea.add( newIE );
				newIE.setPosition( 270, 20);
        		
                
        		// Set Window to Visible
            	GUI_APPWindow.APPWin.setVisible(true);
            	
            	// Start the sync
            	IVector2D heroPos = Singletons.hero.getProjectionCenter();
            	Singletons.gameCamera.setPositionCenteredOn( (int)heroPos.x , (int)heroPos.y );
            	UpdateLinker.start();
            	
            	// Call loaded flag
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
			
			this.setAccelerator(KeyStroke.getKeyStroke('f'));	
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
			
			this.setAccelerator(KeyStroke.getKeyStroke('c'));	
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
			
			this.setAccelerator(KeyStroke.getKeyStroke('s'));	
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
			
			this.setAccelerator(KeyStroke.getKeyStroke('w'));	
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
			
			this.setAccelerator(KeyStroke.getKeyStroke('g'));	
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




class MenuControl extends JMenu {
	class CB_Pause extends JCheckBoxMenuItem implements ItemListener  {
		public CB_Pause( ) {
			super( "Pausar");
			this.addItemListener(this);
			this.setState( true );
			
			Singletons.paused = this.getState();	
			
			//this.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));	
			this.setAccelerator(KeyStroke.getKeyStroke(' '));	
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Singletons.paused = this.getState();
		}
	}
	
	// Constructor
	public MenuControl( ) {
		super( "Controle" );
		this.add( new CB_Pause() );
	}
}

