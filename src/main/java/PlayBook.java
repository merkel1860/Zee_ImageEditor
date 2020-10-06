import core.controller.AppController;

import javax.swing.*;
import core.view.FileChooserDemo;
public class PlayBook {

    public static void main(String[] argv){
        System.out.println("Main :-> "+Thread.currentThread().getName().toUpperCase().toString());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Starting AppController Thread "+Thread.currentThread().getName().toUpperCase().toString());
                new AppController().start();
            }
        });
    }
}
