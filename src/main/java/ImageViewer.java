import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import Rays.CornellBox;

public class ImageViewer {
    public static void main(String[] args) {
        CornellBox cornellBox = new CornellBox();
        Image image = cornellBox.run();

        ImageViewer.showImage(image, "Cornell Box");
    }

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
}
