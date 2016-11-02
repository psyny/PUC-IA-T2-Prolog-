package animation;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import dataTypes.IVector2D;
import dataTypes.ImageCut;

@SuppressWarnings("serial")
public abstract class Sprite extends JPanel {
	protected String 	spriteData;
	protected String 	spriteFile;
	protected Image 	spriteImage;
	
	protected ImageCut 	lastFrame;
		
	protected Container parent;
	
	protected Camera 	camera;
	
	public double scale;
	
	protected void init() {
		this.setVisible( true );
		this.setOpaque( false );
		this.setLayout( null );
		this.scale = 1.0;	
		this.setBounds(0,0,1,1);
	}
	
	// --------------------------------------------
	
	public ImageCut getCut() {
		return lastFrame;
	}
	
	public void insertInto( Container parent ) {
		this.parent = parent;
		parent.add( this );
	}
	
	public void delete() {
		this.setVisible( false );
		parent.remove( this );
	}
	
	public IVector2D getDimension() {
		return this.lastFrame.size;
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	int sizeX = (int)(this.lastFrame.size.x * this.scale);
    	int sizeY = (int)(this.lastFrame.size.y * this.scale);
    	
    	this.setSize( sizeX , sizeY );
    	this.setPreferredSize( new Dimension( sizeX , sizeY ));

        g.drawImage( this.lastFrame.image , 0 , 0 , sizeX , sizeY , 
        		this.lastFrame.origin.x, 
        		this.lastFrame.origin.y,
        		this.lastFrame.size.x + this.lastFrame.origin.x , 
        		this.lastFrame.size.y + this.lastFrame.origin.y,
        		null);
    }
    
	@Override
	// Runs when the component is added to a pane
	public void addNotify() {
	    super.addNotify();

	}
}
