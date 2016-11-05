package prolog;


// Classe que faz tradução de alguns termos entre JAVA , PROLOG e HUMANO
public class Translations {
	public static String getCommandString( Commands cmd ) {
		String msg = "";
		switch( cmd ) {
		case TURN:
			msg = "TURN";
			break;
		case MOVE:
			msg = "MOVE";
			break;
		case PICKUP:
			msg = "PICK UP";
			break;
		case FIRE:
			msg = "FIRE";
			break;
		case EXIT:
			msg = "EXIT";
			break;
		}
		
		return msg;
	}
}
