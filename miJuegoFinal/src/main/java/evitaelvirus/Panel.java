package evitaelvirus;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;



public class Panel extends JPanel implements Runnable, KeyListener {
	private final static int PANTALLA_INICIO = 0;
    private final static int PANTALLA_JUEGO = 1;
    private final static int PANTALLA_PERDEDOR = 2;
    private final static int PANTALLA_GANADOR = 3;
    private static final long serialVersionUID = 1L;
    private int anchoJuego;
    private int largoJuego;
    private PantallaImagen fondo;
    private Persona personita;
    private List<Personaje> enemigos;
    private Vacuna vacuna;
    private int pantallaActual;
    private int contadorLevels;
    private PantallaImagen pantallaInicio;
    private PantallaImagen pantallaPerdedor;
    private PantallaGanador pantallaGanador;
    private Sonidos sonidos;
    
    public Panel(int anchoJuego, int largoJuego) {
        this.anchoJuego = anchoJuego;
        this.largoJuego = largoJuego;
        inicializarPantallas();
        this.pantallaActual = PANTALLA_INICIO;
        cargarSonidos();
    }
    
    public void inicializarJuego(int cantidadEnemigos){
    	inicializarEnemigos(cantidadEnemigos);
    	inicializarVacuna();
    	inicializarPersona();
    }
    
    public void inicializarPantallas() {
    	this.fondo = new PantallaImagen(anchoJuego, largoJuego, "imagenes/fondoCiudad.jpg");
    	this.pantallaInicio = new PantallaImagen(anchoJuego, largoJuego, "imagenes/pantallaInicio.png");
        this.pantallaPerdedor = new PantallaImagen(anchoJuego, largoJuego, "imagenes/pantallaPerdedor.png");
        this.pantallaGanador = new PantallaGanador(anchoJuego, largoJuego, "imagenes/pantallaGanador.png");
    }
    
    public void inicializarPersona() {
    	personita = new Persona(100,100,0,0,30,30);
    }
    
    public void inicializarEnemigos(int cantidadEnemigos) {
    	enemigos = new ArrayList<Personaje>();
    	for (int i=1;i<=cantidadEnemigos;i++) {
    		if (i%2 == 0) {
    			enemigos.add(new EnemigoVioleta(new Random().nextInt(anchoJuego),new Random().nextInt(largoJuego),0,0, 40, 40));
    		} else {
    			enemigos.add(new EnemigoVerde(new Random().nextInt(anchoJuego),new Random().nextInt(largoJuego),0,0, 40, 40));
    		}
    	}
    }
    
    private void inicializarVacuna() {
    	this.vacuna = new Vacuna(new Random().nextInt(anchoJuego-100),new Random().nextInt(largoJuego-100),70,70,0);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(anchoJuego, largoJuego);
    }
    
    @Override
    public void run() {
        while (true) {
            actualizarAmbiente();
            dibujar();
            esperar(10);
        }
    }
    
    private void actualizarAmbiente() {
    	if (pantallaActual == PANTALLA_JUEGO) {
    		verificarChoqueBordesPantalla();
        	moverPersona();
        	vacuna.incrementarTimer();
        	moverEnemigo();
        	verificarChoqueConEnemigo();
        	verificarChoqueConVacuna();
    	}
    }
    
    private void moverPersona() {
    	 personita.setPosicionX(personita.getPosicionX()+personita.getVelocidadX());
    	 personita.setPosicionY(personita.getPosicionY()+personita.getVelocidadY());
    }
    
    private void moverEnemigo() {
    	for (Personaje enemigo : enemigos) {
    		if (personita.getPosicionX() > enemigo.getPosicionX()) {
    			enemigo.setPosicionX(enemigo.getPosicionX()+1);
    		} else if (personita.getPosicionX() < enemigo.getPosicionX()) {
    			enemigo.setPosicionX(enemigo.getPosicionX()-1);
    		}
    		if (personita.getPosicionY() > enemigo.getPosicionY()) {
    			enemigo.setPosicionY(enemigo.getPosicionY()+1);
    		} else if (personita.getPosicionY() < enemigo.getPosicionY()) {
    			enemigo.setPosicionY(enemigo.getPosicionY()-1);
    		}
    	}
    }
    
    private void verificarChoqueConEnemigo() {
    	for (Personaje enemigo : enemigos) {
    		if (hayColision(
    				personita.getPosicionX(), personita.getPosicionY(), personita.getAncho(), personita.getLargo(),
    				enemigo.getPosicionX(), enemigo.getPosicionY(), enemigo.getAncho(), enemigo.getLargo()
    			)) {
    			pantallaActual = PANTALLA_PERDEDOR;
    		}
    	}
    }
   
    private void verificarChoqueConVacuna() {
    	if (vacuna.getTimer() >= 200 && 
    		hayColision(
    				personita.getPosicionX(), personita.getPosicionY(), personita.getAncho(), personita.getLargo(),
        			vacuna.getPosicionX(), vacuna.getPosicionY(), vacuna.getAncho(), vacuna.getLargo()
        	)) {
        		contadorLevels = contadorLevels + 1;
        		vacuna.setTimer(0);
        		pantallaActual = PANTALLA_GANADOR;
        	}
    }
    
    private void verificarChoqueBordesPantalla() {
    	if (personita.getPosicionX() <= 5 || personita.getPosicionX() >= anchoJuego-5) {
    		personita.setVelocidadX(personita.getVelocidadX()*(-2));
    	}
    	if (personita.getPosicionY() <= 5 || personita.getPosicionY() >= largoJuego-5) {
    		personita.setVelocidadY(personita.getVelocidadY()*(-2));
    	}
    }
    
    private void dibujar() {
        this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (pantallaActual == PANTALLA_INICIO) {
        	dibujarInicioJuego(g);
        } else if (pantallaActual == PANTALLA_JUEGO) {
        	dibujarJuego(g);
    	} else if (pantallaActual == PANTALLA_PERDEDOR) {
    		dibujarPantallaPerdedor(g);
    	} else if (pantallaActual == PANTALLA_GANADOR) {
    		dibujarPantallaGanador(g);
    	}
    }
    
    public void dibujarJuego(Graphics graphics) {
    	dibujarFondo(graphics);
    	dibujarVacuna(graphics);
        dibujarEnemigo(graphics);
        dibujarPersona(graphics);
    }
    
    public void dibujarPersona(Graphics graphics) {
    	personita.dibujarse(graphics);
    }
    
    public void dibujarEnemigo(Graphics graphics) {
    	for (Personaje enemigo : enemigos) {
        	enemigo.dibujarse(graphics);
        }
    }
    
    public void dibujarVacuna(Graphics graphics) {
    	if (vacuna.getTimer() >= 200) {
    		vacuna.dibujarse(graphics);
    		//System.out.println(vacuna.getTimer());
    	}
    }
    
    public void dibujarFondo(Graphics graphics) {
    	fondo.dibujarse(graphics);
    }
    
    public void dibujarInicioJuego(Graphics graphics) {
    	pantallaInicio.dibujarse(graphics);
    }
    
    public void dibujarPantallaPerdedor(Graphics graphics) {
    	pantallaPerdedor.dibujarse(graphics);
    }
    
    public void dibujarPantallaGanador(Graphics graphics) {
    	pantallaGanador.setNumeroDosis(contadorLevels);
    	pantallaGanador.dibujarse(graphics);
    }
    
    /*private void limpiarPantalla(Graphics graphics) { //POR AHORA NO LO USE
        graphics.setColor(getBackground());
    }*/

    private void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    	
    	 if (pantallaActual == PANTALLA_INICIO) {
            inicializarJuego(2);
            pantallaActual = PANTALLA_JUEGO;
    	 }
    	 if (pantallaActual == PANTALLA_GANADOR && contadorLevels == 1) {
    		 inicializarJuego(3);
    		 pantallaActual = PANTALLA_JUEGO;
    	 }
    	 if (pantallaActual == PANTALLA_GANADOR && contadorLevels == 2) {
    		 inicializarJuego(4);
    		 pantallaActual = PANTALLA_JUEGO;
    	 }
    	 if (pantallaActual == PANTALLA_PERDEDOR) {
    		 if (contadorLevels == 0) {
    			 inicializarJuego(2);
        		 pantallaActual = PANTALLA_JUEGO;
    		 } else if (contadorLevels == 1) {
    			 inicializarJuego(3);
        		 pantallaActual = PANTALLA_JUEGO;
    		 } else if (contadorLevels == 2) {
    			 inicializarJuego(4);
        		 pantallaActual = PANTALLA_JUEGO;
    		 }
    	 }
        if (arg0.getKeyCode() == 39) {
        	personita.setVelocidadX(5);
        }
        if (arg0.getKeyCode() == 37) {
        	personita.setVelocidadX(-5);
        }
        
        if (arg0.getKeyCode() == 38) {
        	personita.setVelocidadY(-5);
        }
        
        if (arg0.getKeyCode() == 40) {
        	personita.setVelocidadY(5);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	personita.setVelocidadX(0);
    	personita.setVelocidadY(0);
    }
    
    private void cargarSonidos() {
        try {
            sonidos = new Sonidos();
            sonidos.agregarSonido("intro", "sonidos/waduwadu.wav");
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }
    
    private boolean hayColision (
            int elemento1PosicionX, int elemento1PosicionY, int elemento1Ancho, int elemento1Largo,
            int elemento2PosicionX, int elemento2PosicionY, int elemento2Ancho, int elemento2Largo) {
        if (
            haySolapamientoDeRango(
                elemento1PosicionX,
                elemento1PosicionX+elemento1Ancho,
                elemento2PosicionX,
                elemento2PosicionX+elemento2Ancho)
            &&     
            haySolapamientoDeRango(
                elemento1PosicionY,
                elemento1PosicionY+elemento1Largo,
                elemento2PosicionY,
                elemento2PosicionY+elemento2Largo)) {
            return true;
        }
        return false;
    }
    
    private boolean haySolapamientoDeRango(int a, int b, int c, int d) {
        if (a < d && b > c  ) {
            return true;
        }
        return false;
    }

}
