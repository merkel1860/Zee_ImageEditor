package core.view;

import core.model.ImageContent;

import javax.swing.JInternalFrame;

//import java.awt.event.*;
//import java.awt.*;

/* Used by InternalFrameDemo.java. */
public class ImageInternalFrame extends JInternalFrame {
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    private ImageContent imageContent;

    public ImageInternalFrame() {
        super("Document #" + (++openFrameCount),
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable


        //...Create the GUI and put it in the window...

        //...Then set the window size or call pack...
        setSize(300,300);

        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);

    }

    public ImageInternalFrame(ImageContent imageContent) {
        super("Document #" + (++openFrameCount),
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable


        //...Create the GUI and put it in the window...
        this.imageContent = imageContent;
        //...Then set the window size or call pack...
        setSize(300,300);

        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        loadImageInsideInternalFrame();
    }

    private void loadImageInsideInternalFrame() {
        getContentPane().add(this.imageContent);
    }
}
