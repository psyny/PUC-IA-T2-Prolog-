package user_interface;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import animation.AnimatedSprite;
import animation.LoopType;
import data.Singletons;
import dataTypes.IVector2D;

public class InterfaceElementScoreBar extends InterfaceElement {	
	private int score = 0;
	private JLabel scoreLabel;
	
	
	public InterfaceElementScoreBar() {
		super( 200 , 30 );
		this.setOpaque(false);

		this.scoreLabel = new JLabel("");
		scoreLabel.setFont(new Font("Arial Black", Font.BOLD, 15));
		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scoreLabel.setForeground(Color.WHITE);
		
		this.add( this.scoreLabel );
		this.scoreLabel.setBounds( 0 , 0 , 170 , 30 );
		

		
	}
	
	
	public void setScore( int s ) {
		this.score = s;
		
		this.scoreLabel.setText("Score: " + this.score);
	}
	
	
	@Override
	public void passTime(long time) {
		super.passTime(time);
		
		this.setScore( Singletons.heroScore );
	} 
	
	
}
