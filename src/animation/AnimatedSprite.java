package animation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

import dataTypes.*;


class animationData {
	public int 			id;
	public String 		name;
	
	public IVector2D		startingPosition = new IVector2D(0,0);
	public IVector2D		frameSize = new IVector2D(0,0);
	public IVector2D		frameStep = new IVector2D(0,0);
	public int			rows;
	public int			cols;
	public int			frames;
	public int			defaultSpeed;
	
	public String toString() {
		String str = "";
		
		str += "Name:\t\t" + this.name + "\n";
		str += "Id:\t\t" + this.id + "\n";
		str += "StartPos:\t" + this.startingPosition.x + " , " + this.startingPosition.y + "\n";
		str += "frameSize:\t" + this.frameSize.x + " , " + this.frameSize.y + "\n";		
		str += "frameStep:\t" + this.frameStep.x + " , " + this.frameStep.y + "\n";		
		str += "frames:\t\t" + this.frames + "\n";
		str += "defaultSpeed:\t" + this.defaultSpeed + "\n";
		
		return str;
	}
}


class animationState {
	public animationData currentAnimation;
	public int 		currentAnimationFrame = 1;
	public int 		currentFrameRow = 1;
	public int 		currentFrameCol = 1;
	public long 	currentFrameTime = 0;
	
	public int 		currentLoop = 0;
	public boolean 	paused = false;
	
	public void updateRowCol() {
		currentFrameRow = (int)( ( currentAnimationFrame - 1 ) / currentAnimation.cols ) + 1;
		currentFrameCol = (int)(currentAnimationFrame - ( (currentFrameRow-1)*currentAnimation.cols) );
	}
	
	public void setCurrentFrame( int frame ) {
		this.currentAnimationFrame = frame;
		this.updateRowCol();
	}
}


@SuppressWarnings("serial")
public class AnimatedSprite extends Sprite implements Animable {
	
	private ArrayList<animationData> animations = new ArrayList<animationData>();

	private animationState 	animState;
	private int	 			loopLimit;
	private LoopType 		loopType;
	
	private int 			defaultAnimationID;
	private double			speedScale;
	private int 			frameDirection;
	
	public AnimatedSprite	group = null;
	
	public AnimatedSprite( String spriteData ) {
		this.spriteData = spriteData;	
		super.init();
		this.speedScale = 1.0;
		this.frameDirection = 1;
		this.loadData(spriteData );	
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
	    
	    animations.clear();
	    animationData anim;
	    
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
    		anim = new animationData();
    		
		    // Animation Name
	    	lineSplit = line.split(";");
	    	anim.name = lineSplit[0];
	    	
	    	// Animation Data
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    	
	    	anim.id = Integer.parseInt( lineSplit[0].trim() );
	    	if( defaultIDflag == false ) {
	    		defaultIDflag = true;
	    		this.defaultAnimationID = anim.id;
	    	}
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	anim.startingPosition.x = Integer.parseInt( lineSplit[0].trim() );
	    	anim.startingPosition.y = Integer.parseInt( lineSplit[1].trim() );
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	anim.frameSize.x = Integer.parseInt( lineSplit[0].trim() );
	    	anim.frameSize.y = Integer.parseInt( lineSplit[1].trim() );  
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	anim.frameStep.x = Integer.parseInt( lineSplit[0].trim() );
	    	anim.frameStep.y = Integer.parseInt( lineSplit[1].trim() ); 
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	lineSplit = lineSplit[0].split(",");    
	    	anim.rows = Integer.parseInt( lineSplit[0].trim() );
	    	anim.cols = Integer.parseInt( lineSplit[1].trim() ); 
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	anim.frames = Integer.parseInt( lineSplit[0].trim() );
	    	if( anim.frames < 1 ) {
	    		anim.frames = anim.rows * anim.cols;
	    	}
	    	
	    	line = file.nextLine();
	    	lineSplit = line.split(";");    
	    	anim.defaultSpeed = Integer.parseInt( lineSplit[0].trim() );
	    	
	    	// Add this data to the list
	    	this.animations.add( anim );
    	}
    	
    	// Load image
        this.spriteImage = SpriteBank.registerImage( this.spriteFile );
        
        // Set animation stat
        this.animState = new animationState();
        this.playAnimation( this.defaultAnimationID );  
        
        // Set Last Frame
        this.lastFrame = new ImageCut( this.spriteImage , 0 , 0 , 1 , 1 );
        this.adjustLastFrame();
	}

	public void printAnimationData() {
		for( animationData anim : this.animations ) {
			System.out.println( anim );
		}
	}
	
	// Set passed time
	public void passTime( long time ) {
		if( this.animState.paused == true ) {
			return;
		}
		
		// If is in a group, use group stats
		if( this.group != null ) {
			this.animState.currentAnimationFrame = this.group.animState.currentAnimationFrame;
		} else {
		// Else... Calculate own animation
			this.animState.currentFrameTime += time;
			
			boolean finishedLoop = false;
			
			while( ( this.animState.currentFrameTime * this.speedScale ) > this.animState.currentAnimation.defaultSpeed ) {
				this.animState.currentFrameTime = this.animState.currentAnimation.defaultSpeed - this.animState.currentFrameTime;
				
				int lastFrame = this.animState.currentAnimationFrame;
				this.animState.currentAnimationFrame += frameDirection;			
				if( this.animState.currentAnimationFrame > this.animState.currentAnimation.frames ) {
					if( this.loopType == LoopType.ZIGZAG ) {
						this.animState.currentAnimationFrame = this.animState.currentAnimation.frames;
						this.frameDirection *= -1;
					} else {
						this.animState.currentAnimationFrame = 1;	
					}
					finishedLoop = true;
				} else if ( this.animState.currentAnimationFrame < 1 ) {
					if( this.loopType == LoopType.ZIGZAG ) {
						this.animState.currentAnimationFrame = 1;
						this.frameDirection *= -1;
					} else {
						this.animState.currentAnimationFrame = this.animState.currentAnimation.frames;	
					}
					finishedLoop = true;
				}
				
				// Loop Counting		
				if( ( this.loopType == LoopType.END_STOP ||  this.loopType == LoopType.END_DIE ) && finishedLoop == true ) {
					this.animState.currentLoop += 1;
					
					if( this.animState.currentLoop >= this.loopLimit ) {
						this.animState.paused = true;
						this.animState.currentAnimationFrame = lastFrame;
						
						if( this.loopType == LoopType.END_DIE ) {
							//System.out.println("Deletando: " + this.spriteFile );
							this.delete();
							return;
						}
					}
				}
			}
		}

		// State adjustment
		this.animState.updateRowCol();			
		this.adjustLastFrame();
	}
	
	private void adjustLastFrame() {
		this.lastFrame.origin.x 	= this.animState.currentAnimation.startingPosition.x;
		this.lastFrame.origin.x 	+= ( this.animState.currentAnimation.frameStep.x * ( this.animState.currentFrameCol - 1 ) );
		
		this.lastFrame.origin.y 	= this.animState.currentAnimation.startingPosition.y;
		this.lastFrame.origin.y 	+= ( this.animState.currentAnimation.frameStep.y * ( this.animState.currentFrameRow - 1 ) );
		
		this.lastFrame.size.x	 	= this.animState.currentAnimation.frameSize.x;
		this.lastFrame.size.y 		= this.animState.currentAnimation.frameSize.y;	
	} 
	
	private animationData findAnimation( int ID ) {
		for( animationData anim : this.animations ) {
			if( anim.id == ID ) {
		        return anim;
			}
		}		
		return null;
	}
	
	private animationData findAnimation( String name ) {
		for( animationData anim : this.animations ) {
			if( anim.name.equals( name ) ) {
		        return anim;
			}
		}		
		return null;
	}
	
	
	public void playAnimation( ) {
		this.animState.paused = false;
	}
	
	public void playAnimation( int loops , LoopType loopType  ) {
		this.playAnimation( this.animState.currentAnimation.id , loops , loopType );
	}
	
	public void playAnimation( int ID ) {
		this.playAnimation( ID , 1 );
	}
	
	public void playAnimation( String name ) {
		this.playAnimation( name , 1 , true , 1 , LoopType.REPEAT );
	}

	public void playAnimation( int ID , int frame ) {
		this.playAnimation( ID , frame , true , 1 , LoopType.REPEAT );
	}
	
	public void playAnimation( int ID , int loops , LoopType loopType  ) {
		this.playAnimation( ID , 1 , true , loops  , loopType );
	}
	
	public void playAnimation( int ID , int frame , boolean resume , int loops , LoopType loopType ) {
		this.playAnimation( this.findAnimation(ID) , frame , resume , loops , loopType );
	}
	
	public void playAnimation( String name , int frame , boolean resume , int loops , LoopType loopType ) {
		this.playAnimation( this.findAnimation(name) , frame , resume , loops , loopType );
	}

	private void playAnimation( animationData anim , int frame , boolean resume , int loops , LoopType loopType ) {
		if( anim == null ) {
			return;
		}
		
		if( loopType == LoopType.REPEAT_REVERSE ) {
			this.frameDirection = -1;
		}
		
		if( this.animState.currentAnimation == null || anim != this.animState.currentAnimation ) {
	        this.animState.currentAnimation = anim;
	        this.animState.currentFrameTime = 0;
	        this.animState.setCurrentFrame( frame );
		} else {
			if( resume == false ) {
				this.animState.setCurrentFrame( frame );
				this.animState.currentFrameTime = 0;
			}
		}
		
		this.loopLimit 				= loops;
		this.loopType 				= loopType;
		this.animState.currentLoop 	= 0;
		this.animState.paused 		= false;
		
		if( this.group != null ) {
			this.animState.currentAnimationFrame = this.group.animState.currentAnimationFrame;
		}
	}
		
	
	public void stopAnimation() {
		this.animState.paused = true;
	}
	
	public void setAnimationSpeedFactor( double factor ) {
		if( factor < 0 ){
			factor = 0.1;
		} 
		
		this.speedScale = factor;
	}
	
	public void playRandomAnimation() {
		int rand = (int)(Math.random() * this.animations.size() );
		this.playAnimation( rand );
	}
	
	public int getCurrentAnimationID() {
		return this.animState.currentAnimation.id;
	}
}
