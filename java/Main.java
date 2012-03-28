import javax.swing.JFrame;

public class Main {
    public static void main(String args[]) {
        Fiddling theApplet = new Fiddling();
        theApplet.init(); // Needed if overridden in applet
        theApplet.start(); // Needed if overridden in applet

        // ... Create a window (JFrame) and make applet the content pane.
        JFrame window = new JFrame("riverless walk");
        window.setContentPane(theApplet);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack(); // Arrange the components.
        System.out.println(theApplet.getSize());
        window.setVisible(true); // Make the window visible
    }
}