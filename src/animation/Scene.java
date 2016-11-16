package animation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import data.Singletons;
import user_interface.Actor;

public class Scene extends JLayeredPane implements Animable {
	private final int DELAY = 25;
	private Thread cameraThread;
	
	public double scale;
	
	protected ArrayList<Actor> toAdd = new ArrayList<Actor>();

	public Scene( int x , int y , int w , int h) {
		this.setLayout( null );
		this.setVisible( true );
		this.scale = 1.0;	
		
		if( w <= 0 ) {
			w = 1;
		}
		
		if( h <= 0 ) {
			h = 1;
		}
		
		this.setBounds(x , y , w , h);
		this.setPreferredSize( new Dimension(w , h) );
	}
	

	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }	
    
    @Override
    public void addNotify() {
        super.addNotify();
    }
    
	@Override
	public void passTime(long time) {
		
    	for( Component comp : this.getComponents() ) {
    		if (comp instanceof Animable ) {
    			((Animable) comp).passTime( time );
    		}
    	}
    
    	synchronized( Singletons.gameCamera ) {	
	    	for( Actor act : this.toAdd ) {
	    		
		    		this.add( act );
		    		this.setLayer( act , ((act.getProjectionCenter().y / 20 ) + act.desiredLayer ));
	    	}
	    	
    		this.toAdd.clear();
    	}
	}	
}
