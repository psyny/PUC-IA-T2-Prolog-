package animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.LineBorder;

import data.Singletons;
import dataTypes.*;

public class Camera extends JScrollPane implements Runnable , MouseMotionListener {
	private final int 				DELAY = 25;
	private Thread 					cameraThread;
	private ArrayList<Component>  	scenes;
	
	private DVector2D		cameraTarget;
	private DVector2D		cameraPosition;
	private DVector2D		cameraValidPosition;
	private double			maxMoveSpeed = 50;
	private double			minMoveSpeed = 0.5;
	
	private long 			sweepDelay = 10;
	public boolean			isSweeping = false;
	private IVector2D		sweepStep = new IVector2D( 300 , 300 );
	private IVector2D		sweepPosition = new IVector2D( 0 , 0 );
	private IVector2D		sweepLimit = new IVector2D( 100 , 100 );
	
	private boolean			fixedTarget = true;
	
	private ArrayList<Animable>	animableList = new ArrayList<Animable>();
	
	
	
	public Camera( Component viewPort , int x , int y ) {
		super();
		this.scenes = new ArrayList<Component>();
		this.scenes.add(viewPort);
		this.cameraTarget = new DVector2D( x , y );
		this.cameraPosition = new DVector2D( x , y );
		this.cameraValidPosition = new DVector2D( 0 , 0 );
		this.setViewportView( viewPort );
		this.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // ???
		//this.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
	}

    @Override
    public void addNotify() {
        super.addNotify();
        
        this.setAutoscrolls( true );
        this.addMouseMotionListener(this);

        this.cameraThread = new Thread(this);
        this.cameraThread.start();
    }	
	
    private void threadCycle( long passed ) {
    	synchronized( Singletons.gameCamera ) {	
	    	for( Component comp : this.scenes ) {
	    		if (comp instanceof Animable ) {
	    			((Animable) comp).passTime( passed );
	    		}
	    	}
    	}
    	
    	for( Animable anim : this.animableList ) {
    		anim.passTime( passed );
    	}
    	
    	if( this.isSweeping == true ) {
    		if( this.sweepStep() == true ) {
	    		this.cameraPosition.x = this.sweepPosition.x;
	    		this.cameraPosition.y = this.sweepPosition.y;	    		
    		} else {
    			IVector2D rPos =  Singletons.hero.getProjectionCenter();
    			
	    		this.cameraPosition.x = rPos.x;
	    		this.cameraPosition.y = rPos.y;
    		}
    		this.getViewport().setViewPosition( new Point( (int)this.cameraPosition.x , (int)this.cameraPosition.y ));
    	} else if( this.fixedTarget == true ) {
	    	// Set Position
	    	DVector2D offset = new DVector2D( this.getSize().getWidth() / 2 , this.getSize().getHeight() / 2 );
	    	DVector2D effectiveTarget = new DVector2D( this.cameraTarget.x - offset.x , this.cameraTarget.y - offset.y );
	    	
			// Boundaries
			if( effectiveTarget.x < 0 ) {
				effectiveTarget.x = 0;
			} else if ( effectiveTarget.x > 100000 ) {
				effectiveTarget.x = 100000;
			}
			
			if( effectiveTarget.y < 0 ) {
				effectiveTarget.y = 0;
			} else if ( effectiveTarget.y > 100000 ) {
				effectiveTarget.y = 100000;
			} 
	    	
	    	// Calculate Move Speed
	    	DVector2D distance = DVector2D.getDistanceVector( this.cameraPosition , effectiveTarget );
	    	if( distance.getModulus() > 1 ) {
	    		DVector2D moveSpeed = new DVector2D( distance.x / 50 , distance.y / 50 );
	    		distance.normalize();
	    		int xDir = 1;
	    		int yDir = 1;
	    		
	    		
	    		if( Math.abs( this.cameraPosition.x - effectiveTarget.x ) >= 1 ) {
	    			if( moveSpeed.x < 0 ) {
	    				xDir = -1;
	    			}
	    			
		    		if( Math.abs(moveSpeed.x) < this.minMoveSpeed ) {
		    			moveSpeed.x = this.minMoveSpeed * xDir;
		    		} else if ( Math.abs(moveSpeed.x) > this.maxMoveSpeed ) {
		    			moveSpeed.x = this.maxMoveSpeed * xDir;
		    		}
	    		} else {
	    			moveSpeed.x = 0;
	    		}	
	    		
	    		if( Math.abs( this.cameraPosition.y - effectiveTarget.y ) >= 1 ) {
	    			if( moveSpeed.y < 0 ) {
	    				yDir = -1;
	    			}
	    			
		    		if( Math.abs(moveSpeed.y) < this.minMoveSpeed ) {
		    			moveSpeed.y = this.minMoveSpeed * yDir;
		    		} else if ( Math.abs(moveSpeed.y) > this.maxMoveSpeed ) {
		    			moveSpeed.y = this.maxMoveSpeed * yDir;
		    		}
	    		} else {
	    			moveSpeed.y = 0;
	    		}
	
	    		this.cameraPosition.x += moveSpeed.x ;
	    		this.cameraPosition.y +=  moveSpeed.y ;
	    	} else {
	    		this.cameraPosition.x = effectiveTarget.x;
	    		this.cameraPosition.y = effectiveTarget.y;
	    	}
    		
    		// Finally Set   
    		this.getViewport().setViewPosition( new Point( (int)this.cameraPosition.x , (int)this.cameraPosition.y ));
    	}
    	
    	Toolkit.getDefaultToolkit().sync();
    }	
	
	@Override
	public void run() {
		
        long beforeTime, timeDiff, sleep;
        timeDiff = 0;

        beforeTime = System.currentTimeMillis();

        while (true) {

        	threadCycle(DELAY);
            repaint();

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
	
	public void setTarget( int x , int y ) {
		this.cameraTarget.x = x;
		this.cameraTarget.y = y;
	}
	
	public void setIsFixedOnTarget( boolean mode ) {
		this.fixedTarget = mode;
	}
	
	public void setPositionCenteredOn( int x , int y ) {
    	// Set Position
    	DVector2D offset = new DVector2D( this.getSize().getWidth() / 2 , this.getSize().getHeight() / 2 );
    	DVector2D effectivePosition = new DVector2D( x - offset.x , y - offset.y );
    	
		// Boundaries
		if( effectivePosition.x < 0 ) {
			effectivePosition.x = 0;
		} else if ( effectivePosition.x > 100000 ) {
			effectivePosition.x = 100000;
		}
		
		if( effectivePosition.y < 0 ) {
			effectivePosition.y = 0;
		} else if ( effectivePosition.y > 100000 ) {
			effectivePosition.y = 100000;
		} 
		
		this.cameraPosition.x = effectivePosition.x;
		this.cameraPosition.y = effectivePosition.y;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        
        //this.setTarget(e.getX(), e.getY());
        
        //((JPanel)e.getSource()).scrollRectToVisible(r);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void doSweep( IVector2D limits , IVector2D cameraSize ) {
		this.sweepLimit = limits;
		this.sweepLimit.x -= cameraSize.x;
		this.sweepLimit.y  = ( this.sweepLimit.y / 2 ) - cameraSize.y;
		
		this.isSweeping = true;
		this.sweepPosition = new IVector2D(0,0);
		
		this.sweepStep.x = cameraSize.x / 4;
		this.sweepStep.y = cameraSize.y / 4;
	}
	
	private boolean sweepStep() {
		this.sweepPosition.x += this.sweepStep.x;
		
		if( this.sweepPosition.x > this.sweepLimit.x ) {
			this.sweepPosition.x = 0;
			this.sweepPosition.y += this.sweepStep.y;
			
			if( this.sweepPosition.y > this.sweepLimit.y ) {
				this.isSweeping = false;
				return false;
			}
		}
		
		return true;
	}
	
	public void addToAnimableList( Animable anim ) {
		this.animableList.add( anim );
	}
 
}
