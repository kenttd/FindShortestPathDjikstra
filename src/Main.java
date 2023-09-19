import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
	private static List<Color> distinctColors;

	public static void main(String[] args) {
		setup();
		eventSetup();
	}
	
	public static void setup() {
		generateColor();
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
		        }else if(currentMode == resultMode) {
		            Graphics2D g2d = (Graphics2D) g;
		            g2d.setStroke(new BasicStroke(3));

		            // Drawing the shortest paths with unique colors and transparency
		            for(vertex v : vertices) {
		                if(v.prev != '\u0000') { 
		                    vertex predecessor = vertices.get(findVertexIndex(v.prev));
		                    
		                    Color originalPathColor = generatePathColor('A', v.name);
		                    Color transparentPathColor = new Color(originalPathColor.getRed(), originalPathColor.getGreen(), originalPathColor.getBlue(), 200);
		                    g2d.setColor(transparentPathColor);
		                    
		                    Point controlPoint = new Point((predecessor.position.x + v.position.x) / 2,
		                                                   (predecessor.position.y + v.position.y) / 2);

		                    // Offset the control point to "bend" the line
		                    int offset = 30;
		                    controlPoint.x += (v.position.y - predecessor.position.y) / offset;
		                    controlPoint.y -= (v.position.x - predecessor.position.x) / offset;
		                    
		                    // Draw the curved line using a quadratic curve
		                    g2d.draw(new QuadCurve2D.Double(predecessor.position.x, predecessor.position.y, 
		                                                    controlPoint.x, controlPoint.y, 
		                                                    v.position.x, v.position.y));

		                    // Drawing the arrow in the middle of the curve
		                    drawArrow(g2d, predecessor.position, controlPoint, v.position);

		                    // Calculate the midpoint for displaying the shortest distance value
		                    int midX = (v.position.x + predecessor.position.x) / 2;
		                    int midY = (v.position.y + predecessor.position.y) / 2;

		                    // Display the shortest distance slightly above the midpoint
		                    g.setColor(Color.RED);
		                    g.drawString(String.valueOf(v.shortest), midX, midY - 10);
		                }
		            }

		            // Drawing all the vertices
		            for(vertex v : vertices) {
		                v.draw(g);
		            }
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
    	    	frame.repaint();
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
	
	private static Color generatePathColor(char start, char end) {
	    int combinedValue = start + end;
	    return distinctColors.get(combinedValue % distinctColors.size());
	}

	
	private static void generateColor() {
		distinctColors = Arrays.asList(
			    new Color(255, 0, 0),     // Bright Red
			    new Color(0, 255, 0),     // Bright Green
			    new Color(0, 0, 255),     // Bright Blue
			    new Color(255, 255, 0),   // Yellow
			    new Color(0, 255, 255),   // Cyan
			    new Color(255, 0, 255),   // Magenta
			    new Color(192, 192, 192), // Silver
			    new Color(128, 0, 0),     // Maroon
			    new Color(128, 128, 0),   // Olive
			    new Color(0, 128, 0),     // Green
			    new Color(128, 0, 128),   // Purple
			    new Color(0, 128, 128),   // Teal
			    new Color(0, 0, 128),     // Navy
			    new Color(165, 42, 42),   // Brown
			    new Color(255, 165, 0),   // Orange
			    new Color(255, 192, 203), // Pink
			    new Color(64, 224, 208),  // Turquoise
			    new Color(220, 20, 60),   // Crimson
			    new Color(255, 140, 0),   // Dark Orange
			    new Color(75, 0, 130),    // Indigo
			    new Color(127, 255, 0),   // Chartreuse
			    new Color(100, 149, 237), // Cornflower Blue
			    new Color(189, 183, 107), // Dark Khaki
			    new Color(32, 178, 170),  // Light Sea Green
			    new Color(240, 230, 140)  // Khaki
			);

	}

	private static void drawArrow(Graphics2D g2d, Point start, Point control, Point end) {
	    int arrowSize = 10;
	    double t = 0.5; // Midpoint on the curve
	    double x = (1 - t) * (1 - t) * start.x + 2 * (1 - t) * t * control.x + t * t * end.x;
	    double y = (1 - t) * (1 - t) * start.y + 2 * (1 - t) * t * control.y + t * t * end.y;
	    double dx = 2 * (1 - t) * (control.x - start.x) + 2 * t * (end.x - control.x);
	    double dy = 2 * (1 - t) * (control.y - start.y) + 2 * t * (end.y - control.y);
	    double angle = Math.atan2(dy, dx);
	    
	    Point midPoint = new Point((int) x, (int) y);
	    
	    // Arrow head
	    g2d.drawLine(midPoint.x, midPoint.y,
	                 midPoint.x - (int)(arrowSize * Math.cos(angle + Math.PI/6)),
	                 midPoint.y - (int)(arrowSize * Math.sin(angle + Math.PI/6)));
	    g2d.drawLine(midPoint.x, midPoint.y,
	                 midPoint.x - (int)(arrowSize * Math.cos(angle - Math.PI/6)),
	                 midPoint.y - (int)(arrowSize * Math.sin(angle - Math.PI/6)));
	}


}

