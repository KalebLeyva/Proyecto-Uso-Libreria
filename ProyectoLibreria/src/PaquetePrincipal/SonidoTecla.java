package PaquetePrincipal;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para asignar sonidos a campos de texto al presionar teclas.
 * Permite configurar un sonido general por campo, así como sonidos específicos por tecla.
 * También se pueden agregar sonidos personalizados siempre que sean archivos WAV de máximo 1 segundo.
 * 
 * Los sonidos pueden ser reproducidos desde recursos o archivos externos.
 */
public class SonidoTecla {
    // Sonidos base predefinidos
    private static Clip bubbleSound;
    private static Clip clickSound;
    private static Clip bipSound;
    private static Clip sansSound;
    private static Clip iphoneSound;
    private static Clip ps4Sound;
    private static Clip golpeSound;
    private static Clip tipSound;
    private static Clip wikSound;

    // Mapa para sonidos personalizados
    private static final Map<String, Clip> sonidosPersonalizados = new HashMap<>();
    
    // Mapa para asignar sonidos específicos a teclas por campo
    private static final Map<JTextField, Map<Integer, String>> asignacionesPorCampo = new HashMap<>();

    // Nombre de la carpeta para sonidos personalizados (sin ruta completa)
    private static final String NOMBRE_CARPETA_SONIDOS = "SonidosPersonalizados";

    // Bloque estático para cargar sonidos base desde recursos
    static {
        try {
            bubbleSound = loadSound("/resources/sounds/bubble.wav");
            clickSound = loadSound("/resources/sounds/click.wav");
            // ... (otros sonidos base)
            System.out.println("Sonidos base cargados correctamente.");
        } catch (Exception e) {
            System.err.println("Error cargando sonidos base: " + e.getMessage());
        }
    }

    /**
     * Carga un sonido desde la ruta especificada dentro de los recursos del proyecto.
     */
    private static Clip loadSound(String soundPath) throws Exception {
        URL url = SonidoTecla.class.getResource(soundPath);
        if (url == null) {
            throw new Exception("Sonido no encontrado: " + soundPath);
        }
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }

    /**
     * Agrega un nuevo sonido personalizado al sistema.
     * Buscará el archivo en la carpeta "SonidosPersonalizados" dentro del 
     * directorio de trabajo del proyecto que usa la librería.
     */
    public static void agregarSonido(String nombre, String nombreArchivo) throws Exception {
        // Construir la ruta completa al archivo
        File carpetaSonidos = new File(System.getProperty("user.dir"), NOMBRE_CARPETA_SONIDOS);
        File archivoSonido = new File(carpetaSonidos, nombreArchivo);

        // Verificar extensión .wav
        if (!nombreArchivo.toLowerCase().endsWith(".wav")) {
            throw new Exception("El archivo debe ser .wav");
        }

        // Verificar que el archivo existe
        if (!archivoSonido.exists()) {
            throw new Exception("Archivo no encontrado: " + archivoSonido.getAbsolutePath());
        }

        // Cargar el sonido
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(archivoSonido);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);

        // Verificar duración
        double duracion = clip.getMicrosecondLength() / 1_000_000.0;
        if (duracion > 1.0) {
            clip.close();
            throw new Exception("El sonido no debe durar más de 1 segundo");
        }

        sonidosPersonalizados.put(nombre, clip);
        System.out.println("Sonido personalizado '" + nombre + "' cargado desde: " + archivoSonido.getAbsolutePath());
    }

    /**
     * Configura un sonido general para reproducirse cada vez que se presione una tecla en un campo de texto.
     * También toma en cuenta asignaciones específicas si las hay.
     * 
     * @param campo JTextField al que se le asignará el sonido.
     * @param nombreSonido Nombre del sonido base o personalizado a usar.
     */
    public static void configurarSonidoGeneral(JTextField campo, String nombreSonido) {
        if (!sonidosPersonalizados.containsKey(nombreSonido) && (bubbleSound == null || clickSound == null)) {
            throw new IllegalArgumentException("Sonido '" + nombreSonido + "' no existe");
        }

        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (asignacionesPorCampo.containsKey(campo) && 
                    asignacionesPorCampo.get(campo).containsKey(e.getKeyCode())) {
                    String sonidoEspecial = asignacionesPorCampo.get(campo).get(e.getKeyCode());
                    reproducirSonido(sonidoEspecial);
                } else {
                    reproducirSonido(nombreSonido);
                }
            }
        });
    }

    /**
     * Asigna un sonido específico para una tecla concreta en un campo de texto.
     * 
     * @param campo JTextField donde se aplicará la asignación.
     * @param tecla Código de tecla (por ejemplo, KeyEvent.VK_ENTER).
     * @param nombreSonido Nombre del sonido personalizado.
     */
    public static void asignarSonidoATecla(JTextField campo, int tecla, String nombreSonido) {
        if (!sonidosPersonalizados.containsKey(nombreSonido)) {
            throw new IllegalArgumentException("Sonido '" + nombreSonido + "' no existe");
        }

        if (!asignacionesPorCampo.containsKey(campo)) {
            asignacionesPorCampo.put(campo, new HashMap<>());
        }

        asignacionesPorCampo.get(campo).put(tecla, nombreSonido);
    }

    /**
     * Reproduce el sonido según su nombre.
     * Si el nombre coincide con uno de los sonidos base o personalizados, se reproduce desde el inicio.
     * 
     * @param nombreSonido Nombre del sonido a reproducir.
     */
    private static void reproducirSonido(String nombreSonido) {
        Clip clip = sonidosPersonalizados.get(nombreSonido);
        if (clip == null) {
            switch (nombreSonido) {
                case "bubble": clip = bubbleSound; break;
                case "click": clip = clickSound; break;
                case "bip": clip = bipSound; break;
                case "sans": clip = sansSound; break;
                case "iphone": clip = iphoneSound; break;
                case "ps4": clip = ps4Sound; break;
                case "golpe": clip = golpeSound; break;
                case "tip": clip = tipSound; break;
                case "wik": clip = wikSound; break;
            }
        }

        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Libera recursos usados por los sonidos personalizados y elimina todas las asignaciones.
     * Debe llamarse al cerrar la aplicación.
     */
    public static void limpiarSonidos() {
        sonidosPersonalizados.values().forEach(Clip::close);
        sonidosPersonalizados.clear();
        asignacionesPorCampo.clear();
    }
}
