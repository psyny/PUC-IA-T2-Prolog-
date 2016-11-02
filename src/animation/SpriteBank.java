package animation;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

class StoredImage {
	Image 	image;
	String 	fileName;
}

public class SpriteBank {
	public static String imageLoadDirectory = "sprites\\";
	
	private static ArrayList<StoredImage> storedImages;
	
	public static Image registerImage( String fileName ) {
		if ( SpriteBank.storedImages == null ) {
			SpriteBank.init();
		}
		
		// Check if the image is already registered
		StoredImage sImg = SpriteBank.find(fileName);
		if( sImg != null ) {
			return sImg.image;
		}
		
		// Register image
		ImageIcon icon = new ImageIcon( SpriteBank.imageLoadDirectory + fileName );
		
		sImg = new StoredImage();
		sImg.fileName = fileName;
        sImg.image = icon.getImage();
	   
		SpriteBank.storedImages.add( sImg );
		return sImg.image;
	}
	
	private static void init() {
		SpriteBank.storedImages = new ArrayList<StoredImage>();
	}
	
	private static StoredImage find( String fileName ) {
		for( StoredImage sImg : SpriteBank.storedImages ) {
			if( sImg.fileName.equals( fileName ) ) {
				return sImg;
			}
		}
		
		return null;
	}
}
