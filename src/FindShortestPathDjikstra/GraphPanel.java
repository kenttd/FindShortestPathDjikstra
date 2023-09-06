package FindShortestPathDjikstra;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class GraphPanel extends JPanel {
    static private int[][] adjacencyMatrix;
    static HashMap<Character, HashMap<Character, Integer>> graph = new HashMap<>();

    public static void setAdjacencyMatrix(int[][] matrix) {
        adjacencyMatrix = matrix;
        for (int i = 0; i < matrix.length; i++) {
            char vertexLabel = indexToLabel(i).charAt(0);
            graph.put(vertexLabel, new HashMap<>());

            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) { // Assuming a non-zero value means a connection
                    char connectedVertexLabel = indexToLabel(j).charAt(0);
                    int weight = matrix[i][j];
                    graph.get(vertexLabel).put(connectedVertexLabel, weight);
                }
            }
        }
        System.out.println(graph);
    }

    private static String indexToLabel(int index) {
        return String.valueOf((char) ('a' + index));
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (adjacencyMatrix == null) return;

        int nodeCount = adjacencyMatrix.length;
        double angleIncrement = 2 * Math.PI / nodeCount;
        int radius = Math.min(getWidth(), getHeight()) / 2 - 30;  // 30 as a margin
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        Point[] nodePositions = new Point[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            int x = (int) (center.x + radius * Math.cos(i * angleIncrement));
            int y = (int) (center.y + radius * Math.sin(i * angleIncrement));
            nodePositions[i] = new Point(x, y);
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i < nodeCount; i++) {
            for (int j = i + 1; j < nodeCount; j++) { // Prevents double drawing and diagonal overlap
                if (adjacencyMatrix[i][j] != 0) {
                    int midX = (nodePositions[i].x + nodePositions[j].x) / 2;
                    int midY = (nodePositions[i].y + nodePositions[j].y) / 2;
                    
                    String weight = Integer.toString(adjacencyMatrix[i][j]);
                    int weightWidth = g.getFontMetrics().stringWidth(weight);
                    int weightHeight = g.getFontMetrics().getHeight();
                    
                    double edgeAngle = Math.atan2(nodePositions[j].y - nodePositions[i].y, nodePositions[j].x - nodePositions[i].x);
                    int xOffset = (int) (10 * Math.cos(edgeAngle)); // Adjust the offset as needed
                    int yOffset = (int) (10 * Math.sin(edgeAngle)); // Adjust the offset as needed
                    
                    g.drawLine(nodePositions[i].x, nodePositions[i].y, nodePositions[j].x, nodePositions[j].y);
                    g.drawString(weight, midX + xOffset - weightWidth / 2, midY + yOffset + weightHeight / 4);
                }
            }
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < nodeCount; i++) {
            g.fillOval(nodePositions[i].x - 10, nodePositions[i].y - 10, 20, 20);
            
            String label = indexToLabel(i);
            int labelWidth = g.getFontMetrics().stringWidth(label);
            int labelHeight = g.getFontMetrics().getHeight();
            g.setColor(Color.WHITE);
            g.drawString(label, nodePositions[i].x - labelWidth / 2, nodePositions[i].y + labelHeight / 4);
            g.setColor(Color.BLUE);
        }
    }


}
