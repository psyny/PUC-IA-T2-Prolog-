package animation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Scene extends JLayeredPane implements Animable {
	private final int DELAY = 25;
	private Thread cameraThread;
	
	public double scale;

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
	}	
}
