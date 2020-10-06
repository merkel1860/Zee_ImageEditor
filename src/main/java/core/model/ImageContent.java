package core.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ImageContent  extends JPanel {
    public Future<File> fileFuture;
    private boolean hasInternalFrame = false;

    public ImageContent(Future<File> fileFuture) {
        this.fileFuture = fileFuture;
    }

    public boolean isHasInternalFrame() {
        return hasInternalFrame;
    }

    public void setHasInternalFrame(boolean hasInternalFrame) {
        this.hasInternalFrame = hasInternalFrame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image imageToDraw = getImageFromDisk();
        g.drawImage(imageToDraw,0,0,this);
    }

    // Reads Image from Disk and returns it to be repainted
    private Image getImageFromDisk() {
        File downloadedFile = null;
        try {
            downloadedFile = fileFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Image imageToDraw = null;
        try {
            imageToDraw = ImageIO.read(downloadedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageToDraw;
    }
}
