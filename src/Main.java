import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.SwingUtilities;

public class Main{
	static JFrame frame;
	static JButton addButton;
	static JButton connectButton;
	static JButton startButton;
	static boolean onToggleAdd = false;
	static boolean onToggleConnect = false;
	static JComboBox<String> choice;
	static JPanel display;
	static List<vertex> vertices = new ArrayList<>();
	static private int vertexCounter = 0;
	static vertex selectedVertex = null;
	static boolean isDrawingLine = false;
	static Point mousePosition;
	static final int editMode = 1;
	static final int resultMode = 2;
	static int currentMode;
	static ArrayList<Character> unvisited = new ArrayList<>();
	static ArrayList<Character> visited = new ArrayList<>();
	public static void main(String[] args) {
		setup();
		eventSetup();
	}
	
	public static void setup() {
		currentMode = editMode;
		frame = new JFrame();
		frame.setSize(1200,800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setResizable(false);
		addButton = new JButton("Add");
		connectButton = new JButton("Connect");
		startButton = new JButton("Start");
		addButton.setBounds(1050, 100, 100, 100);
		connectButton.setBounds(1050, 250, 100, 100);
		startButton.setBounds(1050, 440, 100, 100);
		frame.add(addButton);
		frame.add(connectButton);
		frame.add(startButton);
		choice = new JComboBox<String>();
		choice.setBounds(1050, 400, 100, 50);
		frame.add(choice);
		display = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        if(currentMode==editMode) {
		        	for (vertex vertex : vertices) {
			            vertex.draw(g);
			        }
			        // Drawing established connections
			        for (vertex v : vertices) {
			            for (Map.Entry<vertex, Integer> entry : v.connection.entrySet()) {
			                vertex connectedVertex = entry.getKey();
			                // Check to ensure drawing only once
			                if (v.name < connectedVertex.name) {
			                    int weight = entry.getValue();
			                    g.setColor(Color.BLACK);
			                    g.drawLine(v.position.x, v.position.y, connectedVertex.position.x, connectedVertex.position.y);
			                    // Calculate the midpoint
			                    int midX = (v.position.x + connectedVertex.position.x) / 2;
			                    int midY = (v.position.y + connectedVertex.position.y) / 2;
			                    // Display the weight slightly above the midpoint
			                    g.setColor(Color.RED);
			                    g.drawString(String.valueOf(weight), midX, midY - 10);
			                }
			            }
			        }

			        if (isDrawingLine && selectedVertex != null && mousePosition != null) {
			            g.setColor(Color.BLACK);
			            g.drawLine(selectedVertex.position.x, selectedVertex.position.y, mousePosition.x, mousePosition.y);
			        }
		        }else if(currentMode==resultMode) {
		        	
		        }
		    }
		};
		display.setBounds(30,30,1000,700);
		display.setBackground(Color.white);
		frame.add(display);
		frame.repaint();
		frame.setVisible(true);
	}
	
	private static void eventSetup() {
		display.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	
		        System.out.println("clicked display");
		        
		        if (onToggleAdd) {
		            Point clickPosition = e.getPoint();
		            char name = (char) ('A' + vertexCounter);
		            vertex newVertex = new vertex(clickPosition, 20, name); // You can change the radius and color as per your liking
		            vertices.add(newVertex);
		            unvisited.add(name);
		            choice.addItem(String.valueOf(name));
		            display.repaint(); // To refresh and display the new circle
		            vertexCounter++;
		        } else if (isDrawingLine && selectedVertex != null) {
		            for (vertex v : vertices) {
		                if (v.contains(e.getPoint())) {
		                	int index1 = findVertexIndex(selectedVertex.name);
		                	int index2 = findVertexIndex(v.name);
		                	int computedDistance = vertices.get(index1).distanceTo(v);
		                	vertices.get(index1).connection.put(v, computedDistance);
		                	vertices.get(index2).connection.put(selectedVertex, computedDistance);
		                	System.out.println("Connecting " + selectedVertex.name + " to " + v.name + " with distance: " + vertices.get(index1).distanceTo(v));
		                    // Code to draw a line between selectedVertex and v
		                    // Note: You might want to store these connections in a data structure for future use or display
		                	for (vertex vert : vertices) {
		                        for (Map.Entry<vertex, Integer> entry : vert.connection.entrySet()) {
		                            System.out.println("Distance between " + vert.name + " and " + entry.getKey().name + " is: " + entry.getValue());
		                        }
		                    }
		                    break;
		                }
		            }
		            isDrawingLine = false;
		            selectedVertex = null;
		            display.repaint();
		        } else {
		            for (vertex v : vertices) {
		                if (v.contains(e.getPoint())) {
		                    selectedVertex = v;
		                    isDrawingLine = true;
		                    break;
		                }
		            }
		        }
		        display.repaint();
		    }
		});
		
		display.addMouseMotionListener(new MouseAdapter() {
	        @Override
	        public void mouseMoved(MouseEvent e) {
	            if (isDrawingLine && selectedVertex != null) {
	                mousePosition = e.getPoint();
	                display.repaint();
	            }
	        }
	    });
		
		addButton.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	    	currentMode = editMode;
    	    	onToggleAdd = !onToggleAdd;
    	        if (onToggleAdd) { // If Add is toggled on, toggle off Connect
    	        	onToggleConnect = false;
    	        }
    	    }
    	});

    	connectButton.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	    	currentMode = editMode;
    	    	onToggleConnect = !onToggleConnect;
    	        if (onToggleConnect) { // If Connect is toggled on, toggle off Add
    	        	onToggleAdd = false;
    	        }
    	    }
    	});
    	
    	startButton.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	    	currentMode = resultMode;
    	    	djikstra(((String) choice.getSelectedItem()).charAt(0));
    	    }
    	});
	}
	
	private static void djikstra(char start) {
		char current = start;
		int index = findVertexIndex(start);
		if(index!=-1)vertices.get(index).shortest=0;
		while(!unvisited.isEmpty()) {
			vertex visiting = getLowest();
			for (Map.Entry<vertex, Integer> entry : vertices.get(findVertexIndex(visiting.name)).connection.entrySet()) {
				if(vertices.get(findVertexIndex(entry.getKey().name)).shortest>visiting.shortest+entry.getValue()) {
					vertices.get(findVertexIndex(entry.getKey().name)).shortest=visiting.shortest+entry.getValue();
					vertices.get(findVertexIndex(entry.getKey().name)).prev=visiting.name;
				}
			}
			unvisited.remove((Character)visiting.name);
			visited.add(visiting.name);
		}
		System.out.println(vertices);
	}
	
	private static vertex getLowest() {
	    return vertices.stream()
	    		.filter(g -> !visited.contains(g.name))  // Only consider graphs with valid names
	            .min(Comparator.comparing(g -> g.shortest)) // Find the graph with the minimum shortest value
	            .orElse(null);  // Return null if no suitable graph is found
	}
	
	private static int findVertexIndex(char name) {
		for(int i = 0; i<vertices.size(); i++) {
			if(vertices.get(i).name==name)return i;
		}
		return -1;
	}
}

