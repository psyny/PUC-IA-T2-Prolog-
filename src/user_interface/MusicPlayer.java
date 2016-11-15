package user_interface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import data.Singletons;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;


public class MusicPlayer implements Runnable {
	public static MusicPlayer defaultPlayer;

	private Thread musicThread;
    
	public AdvancedPlayer player;
	
	public MusicPlayer() {
	    this.musicThread = new Thread(this);
	    this.musicThread.start();	
	}

	public static void init() {
		MusicPlayer.defaultPlayer = new MusicPlayer();	
	}
	
	@Override
	public void run() {
	    File file = null;
	    String fileName = "bgm.mp3";
	    
	    file = new File( Singletons.soundsDirectory + fileName );
	    
	    double frameRate = 39;
	    int start = (int)( ( 60 * 2 + 1 ) * frameRate );
	    int end = (int)( ( 60 * 5 + 40 ) * frameRate );    
	    
	    
	    // Load Map data
	    while(true) {
		    try {
		        FileInputStream fis = new FileInputStream(file);
		        BufferedInputStream bis = new BufferedInputStream(fis);
	
		        System.out.println("P1");
		        
		        this.player = new AdvancedPlayer(bis);
		        this.player.play( start  , end );
		        start = 0;
	            
		    } catch (FileNotFoundException ex) {
		    	System.out.print("Arquivo "+fileName+" Nao Encontrado!\n");
		        //Logger.getLogger(Skills.class.getName()).log(Level.SEVERE, null, ex);
		    } catch (IOException e) {
		    	System.out.println("Abertura de "+fileName+" falhou!");
				e.printStackTrace();
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	    
	    }
	    

	}



}
