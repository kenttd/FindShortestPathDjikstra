import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame{
	private JPanel mainPanel;
    private JPanel leftPanel;
    private JButton addButton;
    private JButton connectButton;
    private JButton startButton;
    private JTextField textField;
    private boolean isAddToggled = false;
    private boolean isConnectToggled = false;
    private int circleCounter = 0;
    private Point firstPoint = null;
    private Point secondPoint = null;
    private boolean isFirstPointSelected = false;
    private List<Point> circleCenters = new ArrayList<>();

    public Main() {
        setTitle("Graph-like GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        setupUI();
    }
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
	}
    private void setupUI() {
        // Left Panel
        leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Add custom drawing code here if you want
            }
        };
        leftPanel.setBackground(Color.WHITE);
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        mainPanel.add(rightPanel, BorderLayout.EAST);

        addButton = new JButton("Add");
        connectButton = new JButton("Connect");
        startButton = new JButton("Start");
        textField = new JTextField(10);
        
        // Panel for Start button and text field
        JPanel startAndTextFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // 5 pixel gaps
        startAndTextFieldPanel.add(startButton);
        startAndTextFieldPanel.add(textField);

        rightPanel.add(addButton);
        rightPanel.add(connectButton);
        rightPanel.add(startAndTextFieldPanel);

        setupListeners();
    }
    
    private void setupListeners() {
    	addButton.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	        isAddToggled = !isAddToggled;
    	        if (isAddToggled) { // If Add is toggled on, toggle off Connect
    	            isConnectToggled = false;
    	        }
    	    }
    	});

    	connectButton.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	        isConnectToggled = !isConnectToggled;
    	        if (isConnectToggled) { // If Connect is toggled on, toggle off Add
    	            isAddToggled = false;
    	        }
    	    }
    	});


        leftPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAddToggled) {
                	circleCenters.add(e.getPoint());
                    Graphics g = leftPanel.getGraphics();
                    g.setColor(Color.BLACK);
                    int radius = 15;  // Adjusted the size here
                    g.fillOval(e.getX() - radius, e.getY() - radius, 2 * radius, 2 * radius);

                    // Add label to the circle
                    String label = String.valueOf((char) ('a' + circleCounter));  // Convert counter to 'a', 'b', 'c', etc.
                    g.setColor(Color.WHITE); // This will make the label color white, you can change as needed
                    g.drawString(label, e.getX() - 4, e.getY() + 4); // Adjust these numbers to position the label properly within the circle
                    circleCounter++; // Increase the counter
                    //leftPanel.repaint();
                }else if (isConnectToggled) {
                    Graphics g = leftPanel.getGraphics();
                    Point circleCenter = getCircleCenterFromPoint(e.getPoint());
                    if (circleCenter != null) {  // This checks if the point is inside a circle
                        if (!isFirstPointSelected) {
                            firstPoint = circleCenter;
                            isFirstPointSelected = true;
                        } else {
                            secondPoint = circleCenter;
                            
                            // Drawing the line between two points
                            g.setColor(Color.BLACK);
                            g.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
                            
                            // Calculate the midpoint of the line
                            int midX = (firstPoint.x + secondPoint.x) / 2;
                            int midY = (firstPoint.y + secondPoint.y) / 2;
                            
                            // Calculate the weight (distance)
                            double distance = firstPoint.distance(secondPoint);
                            String weight = String.format("%.2f", distance); // Display with 2 decimal places
                            
                            // Draw the weight string either above or below the midpoint
                            g.drawString(weight, midX, midY - 10);  // 10 pixels above the midpoint. Adjust as necessary.
                            
                            isFirstPointSelected = false; // Reset for the next set of points
                            firstPoint = null;
                            secondPoint = null;
                        }
                    }
                }
            }
        });
    }

    private Point getCircleCenterFromPoint(Point point) {
        int effectiveRadius = 50; // Slightly larger than circle's actual radius to ensure click recognition
        for (Point center : circleCenters) {
            if (point.distance(center) <= effectiveRadius) {
                return center;  // Return the center of the circle clicked within
            }
        }
        return null;
    }

}
