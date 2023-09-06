package FindShortestPathDjikstra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
	static JFrame frame;
	static GraphPanel GraphPanel = new GraphPanel();
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Custom GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 500); 
            frame.setLayout(new BorderLayout());

            // Right side
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBackground(Color.GRAY);

            JButton Load = new JButton("Load File");
            Load.setFont(new Font("SansSerif", Font.PLAIN, 18)); 
            JButton Find = new JButton("Find shortest path");
            Find.setFont(new Font("SansSerif", Font.PLAIN, 18));
            
            // Text input box with placeholder behavior
            JTextField textField = new JTextField("Enter text here");
            textField.setFont(new Font("SansSerif", Font.PLAIN, 18));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
            loadButtonEventListener(Load);
            placeholder(textField);
            

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(Load);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            rightPanel.add(textField);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            rightPanel.add(Find);
            rightPanel.add(Box.createVerticalGlue());

            // Dividing line
            JSeparator separator = new JSeparator(JSeparator.VERTICAL);

            frame.add(rightPanel, BorderLayout.EAST);
            frame.add(separator, BorderLayout.CENTER);
            frame.add(GraphPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
	}
	public static void loadButtonEventListener(JButton Load) {
		Load.addActionListener(e -> {
            loadAdjacencyMatrixFromFile();
            GraphPanel.repaint(); // Redraw the graph when the matrix is loaded
        });
	}
	public static void placeholder(JTextField textField) {
		textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("Enter text here")) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText("Enter text here");
                }
            }
        });
	}
	public static void loadAdjacencyMatrixFromFile() {
	    JFileChooser fileChooser = new JFileChooser();
	    int result = fileChooser.showOpenDialog(frame);

	    if (result == JFileChooser.APPROVE_OPTION) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
	            List<int[]> matrixList = new ArrayList<>();
	            String line;
	            int rowCount = 0;
	            int colCount = -1; // Initialize column count to an invalid value

	            while ((line = reader.readLine()) != null) {
	                int[] row = Arrays.stream(line.trim().split("\\s+"))
	                        .mapToInt(Integer::parseInt).toArray();

	                if (colCount == -1) {
	                    colCount = row.length;
	                } else if (colCount != row.length) {
	                    // Matrix is not valid, show an error message and return
	                    JOptionPane.showMessageDialog(frame, "The matrix file is not valid.", "Error", JOptionPane.ERROR_MESSAGE);
	                    return;
	                }

	                matrixList.add(row);
	                rowCount++;
	            }

	            if (rowCount == colCount) {
	                int[][] adjacencyMatrix = matrixList.toArray(new int[0][]);
	                GraphPanel.setAdjacencyMatrix(adjacencyMatrix);
	            } else {
	                // Matrix is not valid, show an error message
	                JOptionPane.showMessageDialog(frame, "The matrix file is not valid.", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(frame, "Error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

}
