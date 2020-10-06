package core.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

public class ImageTask implements Callable<File> {
        private final URL remoteImageURL;
        private final String fileName;

    public ImageTask(URL remoteImageURL, String fileName) {
        this.fileName = fileName;
        this.remoteImageURL = remoteImageURL;
    }


    public File getImage() throws IOException {
//        URL aURL = new URL("http://www.spitzer.caltech.edu/uploaded_files/images/0006/3034/ssc2008-11a12_Huge.jpg");
//        URL url = new URL("http://www.avajava.com/images/avajavalogo.jpg");
        BufferedImage img = ImageIO.read(this.remoteImageURL);
        File file = new File("images/"+fileName);
        ImageIO.write(img, "jpg", file);
        return file;
    }

 /*
    @Override
    public void run() {
        try {
            System.out.println("ImageTask "+Thread.currentThread().getName().toUpperCase().toString());
            getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    public File call() throws Exception {
        File fetchedFile = null;
        try {
            System.out.println("ImageTask "+ Thread.currentThread().getName().toUpperCase());
            fetchedFile =  getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (fetchedFile == null) ? new File("images/empty.jpg") : fetchedFile;
    }
}
