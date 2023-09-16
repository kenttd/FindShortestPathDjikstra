import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class vertex {
	Point position;
	int radius;
	char name;
	HashMap<vertex, Integer> connection = new HashMap<>();
	char prev;
	Integer shortest = Integer.MAX_VALUE;
	public vertex (Point position, int radius, char name) {
		this.position = position;
		this.radius = radius;
		this.name = name;
	}
	
	public void draw(Graphics g) {
	    g.setColor(Color.black);
	    g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);

	    // Drawing the name inside the circle
	    g.setColor(Color.white);  // Setting the text color to white for contrast
	    int stringWidth = g.getFontMetrics().stringWidth(String.valueOf(name));
	    int stringHeight = g.getFontMetrics().getAscent();
	    g.drawString(String.valueOf(name), position.x - stringWidth / 2, position.y + stringHeight / 2);
	}
	
	public boolean contains(Point p) {
	    double distance = position.distance(p);
	    return distance <= radius;
	}
	
	public int distanceTo(vertex other) {
	    int dx = this.position.x - other.position.x;
	    int dy = this.position.y - other.position.y;
	    return (int) Math.sqrt(dx*dx + dy*dy);
	}
	
	@Override
	public String toString() {
		return "graph [name=" + name + ", prev=" + prev + ", shortest=" + shortest + "]";
	}
}
