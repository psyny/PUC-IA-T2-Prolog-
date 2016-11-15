package data;

import java.awt.Toolkit;
import java.util.ArrayList;

import dataTypes.*;
import user_interface.Actor;
import user_interface.ActorSensor;
import user_interface.ActorStormBorder;
import user_interface.GUI_APPWindow;
import user_interface.IsoGrid;

public class UpdateLinker implements Runnable {
	private final int 	DELAY = 100;
	private Thread 		thread;
	private long 		sweepMaxDelay = 1000;
	private long 		sweepDelay = 0;
	
	public static void start() {
		Singletons.updateLinker = new UpdateLinker();
		
		Singletons.updateLinker.thread = new Thread(Singletons.updateLinker);
		Singletons.updateLinker.thread.start();
	}
	
	
	 private void threadCycle( long passed ) {
		 Toolkit.getDefaultToolkit().sync();
		 // Look for discovered areas
		 if( Singletons.gameGrid != null ) {
			 for( ArrayList<Cell> row : Singletons.gameGrid.cells ) {
				 for( Cell cell : row ) {
					 cell.stormActor.updateStatus( cell );

					 if( cell.contentActor != null ) {
						 Actor actor = cell.contentActor;
						 if( cell.destroyed == true ) {
							 cell.contentActor = null;
						 }						 
						 actor.updateStatus( cell );
					 }
					 
					 if( cell.visibleGrid != null ) {
						 cell.visibleGrid.updateStatus( cell );
					 }
					 
					 for( ActorSensor sensor : cell.sensorList ) {
						 sensor.updateStatus(cell);
					 }
					 
					 for( ActorStormBorder border : cell.borderEffectList ) {
						 border.updateStatus( cell );
					 }
				 }
			 }
		 }
		 
		 // Update hero position and direction
		 if( Singletons.hero != null ) {
			 //Singletons.hero.setRealPosition( ( Singletons.heroPosition.x + 0.5 ) * IsoGrid.isoTileSize.x ,( Singletons.heroPosition.y + 0.5 ) * IsoGrid.isoTileSize.y  );
			 
			 //Singletons.hero.setOrtogonalDirection( Singletons.heroDirection );
		 }

		 // Update camera
		 if( Singletons.centerOnHero == true && Singletons.hero != null ) {
			 IVector2D target = Singletons.hero.getProjectionCenter();
			 Singletons.gameCamera.setTarget( target.x  , target.y );
			 Singletons.gameCamera.setIsFixedOnTarget( true );
		 } else {
			 Singletons.gameCamera.setIsFixedOnTarget( false ); 
		 }
	 }
	
	@Override
	public void run() {
		
        long beforeTime, timeDiff, sleep;
        timeDiff = 0;

        beforeTime = System.currentTimeMillis();

        while (true) {

        	threadCycle(DELAY);


            timeDiff = System.currentTimeMillis() - beforeTime;
            beforeTime = System.currentTimeMillis();
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }
           
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }   
        }	
	}	
	
}
