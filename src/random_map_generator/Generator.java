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
		
		public String Objeto;
		public int pos_x;
		public int pos_y;
		
		public Elemento(String Objeto, int dimensao_x, int dimensao_y, ArrayList<Elemento> elementos)
		{
			Random rnd = new Random();
			this.Objeto = Objeto;
			
			this.pos_x = rnd.nextInt(dimensao_x);
			this.pos_y = rnd.nextInt(dimensao_y);
			
			while(!validarPosicao(pos_x, pos_y, Objeto, elementos))
			{
				this.pos_x = rnd.nextInt(dimensao_x);
				this.pos_y = rnd.nextInt(dimensao_y);
			}
		}
		
		public boolean validarPosicao(int x, int y, String Objeto, ArrayList<Elemento> elementos)
		{
			for (Elemento elemento : elementos) {
				if(elemento.Objeto == "H" && distanciaManhatam(x, elemento.pos_x, y, elemento.pos_y) <= 2)
					return false;
				if(elemento.pos_x == x && elemento.pos_y == y)
					return false;
				if(elemento.pos_x == 0 && elemento.pos_y == 0)
					return false;
				if(elemento.Objeto == Objeto)
					if(distanciaManhatam(x, elemento.pos_x, y, elemento.pos_y) <= 2)
						return false;
			}
			return true;
		}
		
		public int distanciaManhatam(int x1, int x2, int y1, int y2)
		{
			return Math.abs(x1 - x2) + Math.abs(y1 - y2);
		}
	}
	
	public int numero_buracos;
	public int numero_inimigos;
	public int numero_teleportes;
	public int numero_ouros;
	public int numero_powerup;
	public int dimensao_x;
	public int dimensao_y;
	
	public Generator(int n_buracos, int n_inimigos, int n_teleportes, int n_ouros, int n_powerup, int dimensao_x, int dimensao_y)
	{
		this.numero_buracos = n_buracos;
		this.numero_inimigos = n_inimigos;
		this.numero_teleportes = n_teleportes;
		this.numero_ouros = n_ouros;
		this.numero_powerup = n_powerup;
		this.dimensao_x = dimensao_x;
		this.dimensao_y = dimensao_y;
	}
	
	public void GerarMapa() 
	{
		String mapa = "";
		int n_buracos= this.numero_buracos;
		int n_inimigos= this.numero_inimigos;
		int n_teleportes= this.numero_teleportes;
		int n_ouros= this.numero_ouros;
		int n_powerup= this.numero_powerup;
		ArrayList<Elemento> elementos = new ArrayList<Elemento>();
		
		for (int i = 0; i < n_buracos; i++) {
			Elemento e = new Elemento("P", dimensao_x, dimensao_y, elementos);
			elementos.add(e);
		}
		for (int i = 0; i < n_inimigos; i++) {
			if(i%2 == 0)
			{
				Elemento e = new Elemento("D", dimensao_x, dimensao_y, elementos);
				elementos.add(e);
			}
			else
			{
				Elemento e = new Elemento("d", dimensao_x, dimensao_y, elementos);
				elementos.add(e);
			}
		}
		for (int i = 0; i < n_teleportes; i++) {
			Elemento e = new Elemento("T", dimensao_x, dimensao_y, elementos);
			elementos.add(e);
		}
		for (int i = 0; i < n_ouros; i++) {
			Elemento e = new Elemento("O", dimensao_x, dimensao_y, elementos);
			elementos.add(e);
		}
		for (int i = 0; i < n_powerup; i++) {
			Elemento e = new Elemento("U", dimensao_x, dimensao_y, elementos);
			elementos.add(e);
		}
		
		Elemento inicio = new Elemento("H", dimensao_x, dimensao_y, elementos);
		elementos.add(inicio);
		
		boolean flag_add;
		
		for (int i = 0; i < this.dimensao_y; i++) 
		{
			for (int j = 0; j < this.dimensao_x; j++) 
			{
				
				flag_add = true;
				
				for (Elemento elemento : elementos) 
				{
					if(elemento.pos_x == j && elemento.pos_y == i)
					{
						mapa += elemento.Objeto;
						elementos.remove(elemento);
						flag_add = false;
						break;
					}
				}
				
				if(flag_add)
					mapa += ".";
			}
			mapa += "\r\n";
		}
		
		System.out.println(mapa);
		
		try{
	        String verify, putData;
	        File file = new File("map/map.txt");
	        file.createNewFile();
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(mapa);
	        bw.flush();

	        bw.close();

	    }catch(IOException e){
	    e.printStackTrace();
	    }
	}
}
