package evitaelvirus;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EnemigoVerde extends Personaje {
	private BufferedImage img;
	
	public EnemigoVerde (int posicionX, int posicionY, int velocidadX, int velocidadY, int ancho, int largo) {
		super(posicionX, posicionY, velocidadX, velocidadY, ancho, largo);
		String path = "/C:/Users/amira/OneDrive/Documentos/IFTS/Paradigmas/practicasJavaParadigmas/src/main/resources/imagenes/virusaverde.png";
		try {
		    this.img = ImageIO.read(new File(path));
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}
	
	public void dibujarse(Graphics graphics) {
		try {
            graphics.drawImage(img, this.getPosicionX(), this.getPosicionY(), this.getAncho(), this.getLargo(), null);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
	}
	
	
}



