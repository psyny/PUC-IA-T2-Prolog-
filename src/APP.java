import java.awt.EventQueue;

import javax.swing.*;
import animation.*;
import data.Singletons;
import data.UpdateLinker;
import dataTypes.IVector2D;
import user_interface.GUI_APPWindow;

public class APP extends JFrame {

	public static void main(String[] args) {
		System.out.println("Programa iniciado");
		
		// Inicia a interface grafica
		GUI_APPWindow.startUI();
		
    	// Exemplo de alterações nos dados e q interface atualizado de acordo
    	IVector2D pPos = new IVector2D( Singletons.heroPosition.x , Singletons.heroPosition.y );
    	Singletons.gameGrid.getCell( pPos.x + 1 , pPos.y + 0 ).discovered = true;    	
    	Singletons.gameGrid.getCell( pPos.x + 0 , pPos.y + 1 ).discovered = true;
    	Singletons.gameGrid.getCell( pPos.x + 1 , pPos.y + 1 ).discovered = true;

 
	}
	
	


}
