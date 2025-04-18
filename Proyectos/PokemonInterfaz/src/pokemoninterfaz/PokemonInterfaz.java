/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pokemoninterfaz;

/**
 *
 * @author Harol
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import org.json.*;

public class PokemonInterfaz extends JFrame {
    private JComboBox<String> listaPokemones;
    private JTextArea areaInformacion;
    private JLabel etiquetaImagen;

    public PokemonInterfaz() {
        setTitle("Consulta Pokemon");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Colores
        Color azulOscuro = new Color(33, 45, 62);
        Color grisClaro = new Color(230, 230, 230);
        Color grisMedio = new Color(180, 180, 180);
        Color blanco = Color.WHITE;
        Color negro = Color.BLACK;

        // Fuente personalizada
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(grisClaro);
        add(panelPrincipal);

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(azulOscuro);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel etiqueta = new JLabel("Selecciona un Pokémon:");
        etiqueta.setForeground(blanco);
        etiqueta.setFont(fuenteGeneral);
        panelSuperior.add(etiqueta);

        String[] pokemones = {
            "pikachu", "charmander", "bulbasaur", "squirtle", "eevee",
            "snorlax", "jigglypuff", "meowth", "gengar", "dragonite"
        };
        listaPokemones = new JComboBox<>(pokemones);
        listaPokemones.setFont(fuenteGeneral);
        panelSuperior.add(listaPokemones);

        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.setFont(fuenteGeneral);
        panelSuperior.add(botonBuscar);

        JButton botonProbar = new JButton("Probar conexión");
        botonProbar.setFont(fuenteGeneral);
        panelSuperior.add(botonProbar);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Panel central con información
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(grisClaro);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaInformacion = new JTextArea(5, 30);
        areaInformacion.setFont(fuenteGeneral);
        areaInformacion.setEditable(false);
        areaInformacion.setLineWrap(true);
        areaInformacion.setWrapStyleWord(true);
        areaInformacion.setBackground(blanco);
        areaInformacion.setForeground(negro);
        areaInformacion.setBorder(BorderFactory.createLineBorder(grisMedio, 1, true));

        panelCentral.add(new JScrollPane(areaInformacion), BorderLayout.CENTER);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con imagen
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(grisClaro);
        etiquetaImagen = new JLabel();
        etiquetaImagen.setHorizontalAlignment(SwingConstants.CENTER);
        panelInferior.add(etiquetaImagen);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        // Eventos
        botonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombrePokemon = listaPokemones.getSelectedItem().toString();
                consultarPokemon(nombrePokemon);
            }
        });

        botonProbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarConexion();
            }
        });
    }

    private void consultarPokemon(String nombre) {
        try {
            String urlApi = "https://pokeapi.co/api/v2/pokemon/" + nombre;
            URL url = new URL(urlApi);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conexion.getResponseCode() == 200) {
                BufferedReader lector = new BufferedReader(
                        new InputStreamReader(conexion.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }
                lector.close();

                JSONObject objetoJSON = new JSONObject(respuesta.toString());

                String nombreObtenido = objetoJSON.getString("name");
                int peso = objetoJSON.getInt("weight");
                String tipo = objetoJSON.getJSONArray("types")
                                        .getJSONObject(0)
                                        .getJSONObject("type")
                                        .getString("name");

                String urlImagen = objetoJSON.getJSONObject("sprites")
                                             .getString("front_default");

                areaInformacion.setText("Nombre: " + nombreObtenido +
                                        "\nTipo: " + tipo +
                                        "\nPeso: " + peso);

                Image imagen = ImageIO.read(new URL(urlImagen));
                Image imagenEscalada = imagen.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                etiquetaImagen.setIcon(new ImageIcon(imagenEscalada));
            } else {
                areaInformacion.setText("Pokémon no encontrado.");
                etiquetaImagen.setIcon(null);
            }

            conexion.disconnect();
        } catch (Exception e) {
            areaInformacion.setText("Error: " + e.getMessage());
            etiquetaImagen.setIcon(null);
        }
    }

    private void probarConexion() {
        try {
            String urlApi = "https://pokeapi.co/api/v2/pokemon/pikachu";
            HttpURLConnection conexion = (HttpURLConnection) new URL(urlApi).openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("User-Agent", "Mozilla/5.0");
            int codigoRespuesta = conexion.getResponseCode();

            if (codigoRespuesta == 200) {
                JOptionPane.showMessageDialog(this,
                        "Conexión exitosa con la PokéAPI",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Respuesta inesperada: " + codigoRespuesta,
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }

            conexion.disconnect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PokemonInterfaz().setVisible(true);
        });
    }
}
