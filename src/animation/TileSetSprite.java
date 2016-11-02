package animation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

import dataTypes.ImageCut;
import dataTypes.IVector2D;

class setData {
	public int 			id;
	public String 		name;
	
	public IVector2D		startingPosition = new IVector2D(0,0);
	public IVector2D		frameSize = new IVector2D(0,0);
	public IVector2D		frameStep = new IVector2D(0,0);
	public int			rows;
	public int			cols;
	public int			frames;
	
	public String toString() {
		String str = "";
		
		str += "Name:\t\t" + this.name + "\n";
		str += "Id:\t\t" + this.id + "\n";
		str += "StartPos:\t" + this.startingPosition.x + " , " + this.startingPosition.y + "\n";
		str += "frameSize:\t" + this.frameSize.x + " , " + this.frameSize.y + "\n";		
		str += "frameStep:\t" + this.frameStep.x + " , " + this.frameStep.y + "\n";		
		str += "frames:\t\t" + this.frames + "\n";
		
		return str;
	}
}


class tileState {
	public setData 	currentSet;
	public int 		currentFrame = 1;
	public int 		currentFrameRow = 1;
	public int 		currentFrameCol = 1;
	public long 	currentFrameTime = 0;
	
	public int 		currentLoop = 0;
	public boolean 	paused = false;
	
	public void updateRowCol() {
		currentFrameRow = (int)( ( currentFrame - 1 ) / currentSet.cols ) + 1;
		currentFrameCol = (int)( currentFrame - ( (currentFrameRow-1)*currentSet.cols) );
	}
	
	public void setCurrentFrame( int frame ) {
		this.currentFrame = frame;
		this.updateRowCol();
	}
}




@SuppressWarnings("serial")
public class TileSetSprite extends Sprite {
	private tileState 	state;
	private int 		defaultSetID;
	
	private ArrayList<setData> tileSets = new ArrayList<setData>();
	
	public TileSetSprite( String spriteData ) {
		this.spriteData = spriteData;	
		super.init();
		this.loadData( spriteData );	
	}
	
	private void loadData( String spriteData ) {
	    Scanner file;
	    file = null;
	    
	    String fileName = this.spriteData;
	    
	    // Load Sprite data
	    try {
	        file = new Scanner(Paths.get( SpriteBank.imageLoadDirectory + fileName ));
	    } catch (FileNotFoundException ex) {
	    	System.out.print("Arquivo "+fileName+" Nao Encontrado!\n");
	        //Logger.getLogger(Skills.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (IOException e) {
	    	System.out.println("Abertura de "+fileName+" falhou!");
			e.printStackTrace();
		}	
	    
	    tileSets.clear();
	    setData set;
	    
	    String line;
	    String[] lineSplit;

	    // Sprite File Name
	    if( file.hasNextLine() ) {
	    	line = file.nextLine();
	    	lineSplit = line.split(";");
	    	this.spriteFile = lineSplit[0];
	    }
    	
	    boolean defaultIDflag = false;
	    boolean breakFlag = false;
	    // Animation
    	while( file.hasNextLine() ) {
    		// Ignore empty lines
    		line 		= file.nextLine();
    		breakFlag 	= false;
	    	while( line.trim().length() < 1 ) {
	    		if( file.hasNextLine() == false ) {
	    			breakFlag = true;
	    			break;
	    		} else {
	    			line = file.nextLine();
	    		}
	    	}

	    	
	    	// End of file marker 
	    	if( breakFlag == true || line.charAt(0) == '#' ) {
	    		break;
	    	}
	    	
	    	// Create new entry
    		set = new setData();
    		
		    // Animation Name
	    	lineSplit = line.split(";");
	    	set.name = lineSplit[0];
	    	
	    	// Animation Data
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    	
	    	set.id = Integer.parseInt( lineSplit[0].trim() );
	    	if( defaultIDflag == false ) {
	    		defaultIDflag = true;
	    		this.defaultSetID = set.id;
	    	}
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	set.startingPosition.x = Integer.parseInt( lineSplit[0].trim() );
	    	set.startingPosition.y = Integer.parseInt( lineSplit[1].trim() );
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	set.frameSize.x = Integer.parseInt( lineSplit[0].trim() );
	    	set.frameSize.y = Integer.parseInt( lineSplit[1].trim() );  
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	set.frameStep.x = Integer.parseInt( lineSplit[0].trim() );
	    	set.frameStep.y = Integer.parseInt( lineSplit[1].trim() ); 
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	set.rows = Integer.parseInt( lineSplit[0].trim() );
	    	set.cols = Integer.parseInt( lineSplit[1].trim() ); 
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	set.frames = Integer.parseInt( lineSplit[0].trim() );
	    	if( set.frames < 1 ) {
	    		set.frames = set.rows * set.cols;
	    	}
	    	
	    	// Add this data to the list
	    	this.tileSets.add( set );
    	}
    	
    	// Load image
        this.spriteImage = SpriteBank.registerImage( this.spriteFile );
        
        // Set Last Frame
        this.lastFrame = new ImageCut( this.spriteImage , 0 , 0 , 1 , 1 );
        this.state = new tileState();
        this.setTile( this.defaultSetID , 1 );
        this.adjustLastFrame();
	}

	public void printSetData() {
		for( setData set : this.tileSets ) {
			System.out.println( set );
		}
	}
		
	private void adjustLastFrame() {
		if( this.lastFrame == null ) {
			return;
		}
		this.lastFrame.origin.x 	= this.state.currentSet.startingPosition.x;
		this.lastFrame.origin.x 	+= ( this.state.currentSet.frameStep.x * ( this.state.currentFrameCol - 1 ) );
		
		this.lastFrame.origin.y 	= this.state.currentSet.startingPosition.y;
		this.lastFrame.origin.y 	+= ( this.state.currentSet.frameStep.y * ( this.state.currentFrameRow - 1 ) );
		
		this.lastFrame.size.x	 	= this.state.currentSet.frameSize.x;
		this.lastFrame.size.y 		= this.state.currentSet.frameSize.y;	
	} 

	private setData findSet( int ID ) {
		for( setData set : this.tileSets ) {
			if( set.id == ID ) {
		        return set;
			}
		}		
		return null;
	}	
	
	private setData findSet( String setName ) {
		for( setData set : this.tileSets ) {
			if( set.name.equals( setName ) ) {
		        return set;
			}
		}		
		return null;
	}	

	public void setTile( String setName , int tile  ) {
		this.setTile( this.findSet(setName) , tile);
	}	
	
	public void setTile( int setID , int tile  ) {
		this.setTile( this.findSet(setID) , tile);
	}
	
	private void setTile( setData set , int tile ) {
		if( set != null ) {
			this.state.currentSet 		= set;
			this.state.setCurrentFrame(tile);
			this.adjustLastFrame();
		}
	}
	
	public void setRandomTile( ) {
		int qtySets = this.tileSets.size();
		
		int targetSet = (int)(Math.random() * qtySets);
		int counter = 0;
		for( setData set : this.tileSets ) {
			if( counter >= targetSet ) {
				this.setRandomTile( set );
				return;
			} else {
				counter++;
			}
		}	
	}
	
	public void setRandomTile( int ID ) {
		setData set = this.findSet( ID );
		if( set == null ) {
			return;
		}
		this.setRandomTile( set );
	}
	
	public void setRandomTile( setData set ) {
		int qtySets = this.tileSets.size();
		
		int targetTile = (int)(Math.random() * set.frames ) + 1;
		this.setTile( set , targetTile);
	}

}
