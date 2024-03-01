//PROGRAMA DE AUTOMATAS FINITOS DETERMNISTAS
//AUTOR: JIMENEZ VELAZQUEZ JOSE BRYAN OMAR
//PROFESOR: LUNA BENOSO BENJAMIN
// LEER README PARA SABER QUE AUTOMATA ES CADA COSA
//MATERIA: TEORIA DE LA COMPUTACION


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.io.*;

public class EvalAFD extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private int numColum; // Número de columnas en la tabla de transiciones
    private String ini, edoActual; // Estado inicial y estado actual
    private String[] fin, symbols; // Estados finales y símbolos del alfabeto
    private HashMap<String, String[]> table; // Tabla de transiciones
    private JTextField inputField; // Campo de entrada para la cadena a evaluar
    private JTextArea outputArea; // Área de texto para mostrar el resultado de la evaluación
    private JProgressBar progressBar; // Barra de progreso para mostrar la evaluación en curso

    // Constructor de la clase
    public EvalAFD(String name) {
        table = new HashMap<String, String[]>(); // Inicialización de la tabla de transiciones
        try {
            File inputFile = new File(name); // Creación de un objeto File con el nombre del archivo proporcionado
            FileReader fr = new FileReader(inputFile); // Creación de un FileReader para leer el archivo
            BufferedReader br = new BufferedReader(fr); // Creación de un BufferedReader para leer líneas del archivo
            String linea;
            String[] aux;
            int i = 0;
            linea = br.readLine(); // Lectura de la primera línea del archivo
            while (linea != null) { // Mientras haya líneas por leer
                String dato[] = linea.split(","); // División de la línea en datos separados por coma
                if (i == 0) { // Si es la primera línea del archivo
                    if (linea.charAt(0) == '@') { // Si la línea comienza con '@'
                        this.numColum = dato.length; // Se obtiene el número de columnas
                        symbols = Arrays.copyOfRange(dato, 1, this.numColum); // Se copian los símbolos del alfabeto
                    } else {
                        System.out.println("Archivo no valido"); // Mensaje de error si el archivo no tiene el formato esperado
                        System.exit(0); // Salida del programa
                    }
                } else if (dato[0].equals("Inicio")) { // Si el primer dato es "Inicio"
                    ini = dato[1]; // Se asigna el estado inicial
                    edoActual = dato[1]; // El estado actual también es el inicial
                } else if (dato[0].equals("Final")) { // Si el primer dato es "Final"
                    fin = Arrays.copyOfRange(dato, 1, dato.length); // Se copian los estados finales
                } else {
                    aux = Arrays.copyOfRange(dato, 1, this.numColum); // Se copian los datos de transición
                    table.put(dato[0], aux); // Se agregan a la tabla de transiciones
                }
                linea = br.readLine(); // Lectura de la siguiente línea
                i++;
            }

            table.remove(""); // Eliminación de una posible entrada vacía en la tabla
            fr.close(); // Cierre del FileReader

        } catch (FileNotFoundException e) { // Captura de excepción si no se encuentra el archivo
            System.err.println("ArchivoText: " + e); // Mensaje de error
            System.exit(0); // Salida del programa
        } catch (IOException e) { // Captura de excepción si hay un error de entrada/salida
            System.err.println("ArchivoText: " + e); // Mensaje de error
            System.exit(0); // Salida del programa
        }

        // Configuración de la interfaz gráfica
        setTitle("Evaluar AFD");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configuración del panel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel inputLabel = new JLabel("Ingrese la cadena:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputLabel.setForeground(Color.BLUE);
        inputField = new JTextField(15);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addActionListener(this);
        JButton evaluateButton = new JButton("Evaluar");
        evaluateButton.setFont(new Font("Arial", Font.BOLD, 14));
        evaluateButton.setForeground(Color.WHITE);
        evaluateButton.setBackground(Color.GREEN.darker());
        evaluateButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(evaluateButton);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Configuración del área de salida
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        outputArea.setForeground(Color.RED);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Configuración del panel inferior
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        JPanel automataPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton[] automataButtons = new JButton[3];
        for (int i = 0; i < 3; i++) { // Creación de botones para cargar diferentes autómatas
            automataButtons[i] = new JButton("Autómata " + (i + 1));
            automataButtons[i].setFont(new Font("Arial", Font.BOLD, 14));
            automataButtons[i].setForeground(Color.WHITE);
            automataButtons[i].setBackground(Color.ORANGE.darker());
            automataButtons[i].addActionListener(this);
            automataPanel.add(automataButtons[i]);
        }
        bottomPanel.add(automataPanel);
        bottomPanel.add(inputPanel);

        // Configuración de la barra de progreso
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        // Agregando los componentes a la interfaz
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(progressBar, BorderLayout.NORTH);
    }

    // Método para manejar eventos de acción
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) { // Si el evento proviene de un botón
            JButton button = (JButton) e.getSource();
            String buttonLabel = button.getText();
            if (buttonLabel.equals("Evaluar")) { // Si se hace clic en el botón "Evaluar"
                progressBar.setIndeterminate(true); // Se activa la barra de progreso
                progressBar.setVisible(true);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        evaluate(); // Se realiza la evaluación en segundo plano
                        return null;
                    }

                    @Override
                    protected void done() {
                        progressBar.setVisible(false); // Se oculta la barra de progreso al finalizar
                    }
                };
                worker.execute(); // Ejecución del trabajo en segundo plano
            } else if (buttonLabel.startsWith("Autómata ")) { // Si se hace clic en un botón de "Autómata"
                int index = Character.getNumericValue(buttonLabel.charAt(buttonLabel.length() - 1));
                String filePath = JOptionPane.showInputDialog(this, "Ingrese la ruta del autómata " + index + ":");
                if (filePath != null && !filePath.isEmpty()) { // Si se proporciona una ruta de archivo válida
                    this.dispose(); // Se cierra la ventana actual
                    new EvalAFD(filePath).setVisible(true); // Se crea y muestra una nueva instancia de EvalAFD con el archivo proporcionado
                }
            }
        }
    }

    // Método para evaluar la cadena ingresada por el usuario
    public void evaluate() {
        String cadena = inputField.getText();
        outputArea.setText(""); // Se limpia el área de salida
        for (int i = 0; i < cadena.length(); i++) { // Iteración sobre los símbolos de la cadena
            int indAux = -1;
            for (int j = 0; j < numColum - 1; j++) {
                if (cadena.charAt(i) == this.symbols[j].charAt(0)) {
                    indAux = j;
                    break;
                }
            }

            if (indAux != -1) {
                String[] auxEdo = this.table.get(this.edoActual);
                this.transicion(auxEdo, indAux); // Transición al siguiente estado según el símbolo actual
            } else {
                outputArea.append("No pertenece al alfabeto\n"); // Si el símbolo no pertenece al alfabeto, se muestra un mensaje
                return;
            }
        }
        for (int i = 0; i < fin.length; i++) { // Comprobación de si el estado actual es final
            if (this.edoActual.equals(this.fin[i])) {
                outputArea.append("Cadena compatible\n");
                outputArea.append("Estado final: " + this.fin[i] + "\n");
                return;
            }
        }
        outputArea.append("Cadena no compatible\n"); // Si el estado actual no es final, la cadena no es compatible
    }

    // Método para realizar una transición de estado
    public void transicion(String[] edos, int index) {
        this.edoActual = edos[index];
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        if (args.length == 0) { // Verificación de si se proporciona el nombre del archivo como argumento
            System.out.println("Debe proporcionar el nombre del archivo como argumento.");
            return;
        }
        EvalAFD frame = new EvalAFD(args[0]); // Creación de una instancia de EvalAFD con el nombre del archivo proporcionado
        frame.setVisible(true); // Se hace visible la ventana
    }
}

