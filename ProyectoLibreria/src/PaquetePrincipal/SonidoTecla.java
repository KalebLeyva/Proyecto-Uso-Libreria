/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PaquetePrincipal;

/**
 *
 * @author MSI
 */
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SonidoTecla {
    // Sonidos base
    private static Clip bubbleSound;
    private static Clip clickSound;
    private static Clip bipSound;
    private static Clip sansSound;
    private static Clip iphoneSound;
    private static Clip ps4Sound;
    private static Clip golpeSound;
    private static Clip tipSound;
    private static Clip wikSound;

    // Almacenar sonidos personales
    private static final Map<String, Clip> sonidosPersonalizados = new HashMap<>();
    private static final Map<JTextField, Map<Integer, String>> asignacionesPorCampo = new HashMap<>();

    static {
        try {
            bubbleSound = loadSound("/resources/sounds/bubble.wav");
            clickSound = loadSound("/resources/sounds/click.wav");
            bipSound= loadSound("/resources/sounds/bip_1.wav");
            sansSound= loadSound("/resources/sounds/E_1.wav");
            iphoneSound= loadSound("/resources/sounds/iphone.wav");
            ps4Sound= loadSound("/resources/sounds/ps4.wav");
            golpeSound= loadSound("/resources/sounds/Sonido-golpe.wav");
            tipSound= loadSound("/resources/sounds/tip.wav");
            wikSound= loadSound("/resources/sounds/wik.wav");
            System.out.println("Sonidos base cargados correctamente.");
        } catch (Exception e) {
            System.err.println("Error cargando sonidos base: " + e.getMessage());
        }
    }

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

    // agg sonidos personalizados
    public static void agregarSonido(String nombre, String rutaArchivo) throws Exception {
        if (!rutaArchivo.toLowerCase().endsWith(".wav")) {
            throw new Exception("El archivo debe ser .wav");
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new Exception("Archivo no encontrado: " + rutaArchivo);
        }

        AudioInputStream audioIn = AudioSystem.getAudioInputStream(archivo);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);

        //No dure mas de 1 segundo)
        double duracion = clip.getMicrosecondLength() / 1_000_000.0;
        if (duracion > 1.0) {
            clip.close();
            throw new Exception("El sonido no debe durar más de 1 segundo");
        }

        sonidosPersonalizados.put(nombre, clip);
        System.out.println("Sonido personalizado '" + nombre + "' cargado correctamente.");
    }

    public static void configurarSonidoGeneral(JTextField campo, String nombreSonido) {//sonido general
        if (!sonidosPersonalizados.containsKey(nombreSonido) && (bubbleSound == null || clickSound == null)) {
            throw new IllegalArgumentException("Sonido '" + nombreSonido + "' no existe");
        }

        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //Verificar teclas especiales
                if (asignacionesPorCampo.containsKey(campo) && 
                    asignacionesPorCampo.get(campo).containsKey(e.getKeyCode())) {
                    String sonidoEspecial = asignacionesPorCampo.get(campo).get(e.getKeyCode());
                    reproducirSonido(sonidoEspecial);
                } else {
                    // Si no, usar el sonido general
                    reproducirSonido(nombreSonido);
                }
            }
        });
    }

    // Tecla especifica
    public static void asignarSonidoATecla(JTextField campo, int tecla, String nombreSonido) {
        if (!sonidosPersonalizados.containsKey(nombreSonido)) {
            throw new IllegalArgumentException("Sonido '" + nombreSonido + "' no existe");
        }

        if (!asignacionesPorCampo.containsKey(campo)) {
            asignacionesPorCampo.put(campo, new HashMap<>());
        }

        //Para teclas especificas
        asignacionesPorCampo.get(campo).put(tecla, nombreSonido);
    }

    private static void reproducirSonido(String nombreSonido) {
        Clip clip = sonidosPersonalizados.get(nombreSonido);
        if (clip == null) {
            if (nombreSonido.equals("bubble")) {
                clip = bubbleSound;
            } else if (nombreSonido.equals("click")) {
                clip = clickSound;
            } else if (nombreSonido.equals("bip")) {
                clip = bipSound;
                } else if (nombreSonido.equals("sans")) {
                clip = sansSound;
                } else if (nombreSonido.equals("iphone")) {
                clip = iphoneSound;
                } else if (nombreSonido.equals("ps4")) {
                clip = ps4Sound;
                } else if (nombreSonido.equals("golpe")) {
                clip = golpeSound;
                } else if (nombreSonido.equals("tip")) {
                clip = tipSound;
                } else if (nombreSonido.equals("wik")) {
                clip = wikSound;
                }
        }
        if (clip != null) {
            clip.setFramePosition(0);//aqui reinicia
            clip.start();
        }
    }

    public static void limpiarSonidos() {
        sonidosPersonalizados.values().forEach(Clip::close);
        sonidosPersonalizados.clear();
        asignacionesPorCampo.clear();
    }
}