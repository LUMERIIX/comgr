import java.awt.*;
import javax.swing.*;


public class ImageViewer {

    public static void showImage(Image img, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame(title);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new JLabel(new ImageIcon(img)));
            f.pack();                 // sizes to image dimensions
            f.setLocationByPlatform(true);
            f.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Initialize start and end colors
        Vector3 startColor = new Vector3(1.0, 0.0, 0.0); // Red
        Vector3 endColor = new Vector3(0.0, 1.0, 0.0);   // Green
        Interpolation interp = new Interpolation(startColor, endColor, 500);
        Image img = interp.generateInterpolationImage(100);

        showImage(img, "Interpolation");
    }
}
