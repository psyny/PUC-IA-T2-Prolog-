package random_map_generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Generator {
	
	public class Elemento{
		public int pos_x;
		public int pos_y;
		public String codigo;
		
		public Elemento(String codigo)
		{
			Random rnd = new Random();
			int contador = 0;
			while(contador < Generator.obstaculos.size())
			{
				int tentativa_x = rnd.nextInt(Generator.dimensao_x) + 1;
				int tentativa_y = rnd.nextInt(Generator.dimensao_y) + 1;
				contador = 0;
				for (Elemento elemento : Generator.obstaculos) {
					if(elemento.pos_x == tentativa_x && elemento.pos_y == tentativa_y)
					{
						break;
					}
					else
					{
						contador += 1;
					}
				}
			}
			this.codigo = codigo;
		}
		
	}
	
	public static int numero_buracos;
	public static int numero_inimigos;
	public static int numero_teleportes;
	public static int numero_ouros;
	public static int numero_powerup;
	public static int dimensao_x;
	public static int dimensao_y;
	public static ArrayList<Elemento> obstaculos;
	
	public static void GerarMapa() 
	{
		String mapa = "";
		int chance_colocar = 10;
		Random rnd = new Random();
		ArrayList<String> elementos_faltando;
		int n_buracos= Generator.numero_buracos;
		int n_inimigos= Generator.numero_inimigos;
		int n_teleportes= Generator.numero_teleportes;
		int n_ouros= Generator.numero_ouros;
		int n_powerup= Generator.numero_powerup;
		int dimensao_x= Generator.dimensao_x;
		int dimensao_y= Generator.dimensao_y;
		
		Generator.obstaculos = new ArrayList<>();
		
		for (int i = 0; i < n_buracos; i++) {
			Elemento e = new Elemento("B");
			Generator.obstaculos.add(e);
		}

		for (int i = 0; i < dimensao_y; i++) {
			for (int j = 0; j < dimensao_x; j++) {
				
				int percent = rnd.nextInt(100);
				
				if(n_buracos == 0 && n_inimigos == 0 && n_teleportes == 0 && n_ouros == 0 && n_powerup == 0)
				{
					chance_colocar = -1;
				}
				
				if(percent <= chance_colocar)
				{
					//falta checar se pode colocar objeto
					String novo_objeto = Generator.escolher_objeto(n_buracos, n_inimigos, n_teleportes, n_ouros, n_powerup);
					mapa += novo_objeto;
					
					if(novo_objeto == "P")
						n_buracos -= 1;
					if(novo_objeto == "D")
						n_inimigos -= 1;
					if(novo_objeto == "T")
						n_teleportes -= 1;
					if(novo_objeto == "O")
						n_ouros -= 1;
					if(novo_objeto == "P")
						n_powerup -= 1;
					
					chance_colocar = 10;
				}
				else
				{
					mapa += '.';
				}
				
				chance_colocar += 10;
			}
			mapa += '\n';
		}
		
		/*try{
	        String verify, putData;
	        File file = new File("file.txt");
	        file.createNewFile();
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(mapa);
	        bw.flush();

	        bw.close();

	    }catch(IOException e){
	    e.printStackTrace();
	    }*/
	}
	
	public static String escolher_objeto(int n_buracos, int n_inimigos, int n_teleportes, int n_ouros, int n_powerup)
	{
		ArrayList<String> roleta = new ArrayList<String>();
		Random rnd = new Random();
		
		for (int chance = 100; chance >= 0; ) 
		{
			if(n_buracos > 0)
			{
				roleta.add("P");
				chance -= 1;
			}
			if(n_inimigos > 0)
			{
				roleta.add("D");	
				chance -= 1;
			}
			if(n_teleportes > 0)
			{
				roleta.add("T");
				chance -= 1;
			}
			if(n_ouros > 0)
			{
				roleta.add("O");
				chance -= 1;
			}
			if(n_powerup > 0)
			{
				roleta.add("P");
				chance -= 1;
			}
			if(n_buracos == 0 && n_inimigos == 0 && n_teleportes == 0 && n_ouros == 0 && n_powerup == 0)
			{
				break;
			}
		}
	
		if(roleta.size() == 0)
			return ".";
		
		int percent = rnd.nextInt(100);
		
		return roleta.get(percent);
	}
}
