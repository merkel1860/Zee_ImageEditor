package core.view;

import core.controller.AppController;
import core.model.ImageContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainUI extends JFrame implements ActionListener, Runnable {
    //Actual size of main window
    private final Dimension windowDimension;
    JDesktopPane desktop;
    private JMenuBar menuBar;
    private JToolBar jToolBar;
    private JFileChooser fileChooser;
    private JTextField searchCriterion;
    private static List<ImageInternalFrame> imageInternalFrameList = new ArrayList<ImageInternalFrame>();
    private boolean isShuttingDown = false;  // Holds the state of actual instance of MainUI
    //  InterProcess Communication Variable
    public volatile List<ImageContent> imagesToLoadInsideInternalFrame = new ArrayList<>();

    public MainUI(String title, Dimension windowDimension) throws HeadlessException {
        super(title);
        this.windowDimension = windowDimension;
        initializeUI();
    }

    public MainUI() throws HeadlessException {
        this.windowDimension = new Dimension(800, 700);
        this.setSize(windowDimension);
        this.setTitle("Zee Image Editor");
        initializeUI();
    }

    // Add a MenuBar to Main Window
    private void settingUpMenuBar() {
        /*
            Setting up menu bar with three menu items
            1. File
                a. Open
                b. New
                c. Exit
         */
        this.menuBar = new JMenuBar();
        String[] menuItemNames = {"Open", "New", "Exit"};
        List<JMenuItem> jMenuItemList = new ArrayList<JMenuItem>();

        this.menuBar.add(createNewMenu("File".toUpperCase(),
                createJMenuItemList(3, menuItemNames)));
        this.menuBar.add(createNewMenu("Windows".toUpperCase()));

        // Add menubar to Frame
        this.setJMenuBar(menuBar);
    }

    // Add a Toolbar to Main Window
    private void settingUpToolBar() {
        JPanel toolBoxPanel = new JPanel();
        jToolBar = new JToolBar();
        jToolBar.setRollover(true);
        searchCriterion = new JTextField("Type your search");
        searchCriterion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                searchCriterion.setText("");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (searchCriterion.getText().trim().length() <= 0) {
                    searchCriterion.setText("Type your search");
                }
            }
        });
        jToolBar.add(searchCriterion);
        jToolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        jToolBar.add(searchButton);

        toolBoxPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        toolBoxPanel.add(jToolBar);
        this.getContentPane().add(toolBoxPanel, BorderLayout.NORTH);
//        this.getContentPane().add(jToolBar, BorderLayout.NORTH);
    }

    private void initializeUI() {
        // Set Main window size
        this.setPreferredSize(new Dimension(1024, 750));
        // Setup MenuBar
        settingUpMenuBar();
        // Setup ToolBar
        settingUpToolBar();
        // Set Frame visible and centered relative to screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // On close the main window is hidden
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        // Initialize File chooser
        this.fileChooser = new JFileChooser();

        //A specialized layered pane
        desktop = new JDesktopPane();
        desktop.setBackground(Color.lightGray);
        // Place the Desktop Manager to the center part of the Main Window
        this.add(desktop, BorderLayout.CENTER);

    }

    // Overloading createNewMenu just in case of new Menu without Menu Items
    private JMenu createNewMenu(String menuTitle, List<JMenuItem> jMenuItemList) {
        if (jMenuItemList.size() > 0 && menuTitle.isEmpty() == false) {
            JMenu aMenu = new JMenu(menuTitle);
            for (JMenuItem s : jMenuItemList) {
                aMenu.add(s);
            }
            System.out.println("aMenu is gone!");
            return aMenu;
        }
        // Return null value if wrong parameters were passed
        return null;
    }

    // Overloading createNewMenu just in case of new Menu without Menu Items
    private JMenu createNewMenu(String menuTitle) {
        if (menuTitle.isEmpty() == false) {
            JMenu aMenu = new JMenu(menuTitle);
            return aMenu;
        }
        // Return null value if wrong parameters were passed
        return null;
    }

    // Create a List of JMenuItem based on its counter and an Array of String
    private List<JMenuItem> createJMenuItemList(int menuItemCount, String[] menuItemNames) {

        if (menuItemCount > 0 && menuItemNames.length > 0
                && menuItemNames.length == menuItemCount) {

            List<JMenuItem> jMenuItemList = new ArrayList<JMenuItem>();
            for (int i = 0; i < menuItemCount; i++) {
                jMenuItemList.add(new JMenuItem(menuItemNames[i]));
                jMenuItemList.get(i).addActionListener(this);
            }
            return jMenuItemList;
        }

        // If there is nothing to build a List of Menu Item
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
           Check which JComponent has been trigger and then launches its callback function.
        */
        // Case 1, The source is from a MenuItem
        if (e.getSource() instanceof JMenuItem) {
            switch (((JMenuItem) e.getSource()).getText().toLowerCase()) {
                case "open":
                    // Source is Open --> JMenuItem
                    jmenuItemOpen();
                    break;
                case "new":
                    // Source is New --> JMenuItem
                    createInternalFrame();
                    break;
                case "exit":
                    // Source is Exit --> JMenuItem
                    quit();
                    break;
                default:
                    // Source is Any Item within Windows Menu
                    JOptionPane.showConfirmDialog(this,
                            "This is it! There is nothing else to say.");
                    break;
            }
        }
        // Case 2, The source is from a JButtom
        if (e.getSource() instanceof JButton) {
            // Check if search button has been clicked
            if (((JButton) e.getSource()).getText().equalsIgnoreCase("Search")) {
//                JOptionPane.showConfirmDialog(this, "Did you get that");
                if (searchCriterion.getText().length() > 0 && (!searchCriterion.getText()
                        .toLowerCase()
                        .equalsIgnoreCase("type your search"))) {
//
//                    System.out.println(searchCriterion.getText());
                    try {
                        AppController.fetchImageFromURL(new URL(searchCriterion.getText().toString()));
                        searchCriterion.setText("type your search");

                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                        System.out.println("ExecutionException Error");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                        System.out.println("InterruptedException Error");
                    } catch (MalformedURLException malformedURLException) {
                        malformedURLException.printStackTrace();
                        System.out.println("MalformedURLException Error");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please type a valid URL");
                }

            }
        }
    }

    // Handling of Open JMenuItem
    private void jmenuItemOpen() {
        int returnVal = fileChooser.showOpenDialog(MainUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
        }
    }

    // Remove a Menu Item from Menu
    // TODO
    //  Actually this function removes items from a JMenuBar
    private void removeMenuItemFromMenu(String text) {
        for (int i = 0; i < this.menuBar.getMenuCount(); i++) {
            for (int j = 0; j < this.menuBar.getMenu(i).getItemCount(); j++) {
                if (isItemIn(this.menuBar.getMenu(i), text)) {
                    System.out.println("Item gone : " + menuBar.getMenu(i).getItem(j).getText());
                    this.menuBar.getMenu(i).remove(j);
                    return;
                }
            }

        }
    }

    // Check whether a specific window is out there.
    private boolean isItemInList(List<ImageInternalFrame> frames, String text) {
        for (ImageInternalFrame s : frames) {
            System.out.println(s.getTitle() + " = " + text);
            if (s.getTitle().equalsIgnoreCase(text)) {
                System.out.println(s.getTitle() + " =OK= " + text);
                return true;
            }
        }
        return false;
    }

    protected void createInternalFrame() {
        // Command to Controller to create a new Internal Frame
        ImageInternalFrame imageInternalFrame = AppController.newImageWindow();
        // Add a new Internal Frame into the MainUI's list of internal frame
        imageInternalFrameList.add(imageInternalFrame);
        // Add the newly create internal frame to contentPane
        desktop.add(imageInternalFrame);
        // Update Menu Windows as new Internal Frames are added to actual App.
        addMenuItemToMenu("Windows",
                createJMenuItemList(imageInternalFrameList.size(), grabAllInternalFrameNames()));
    }

    // Overloading CreateInternalFrame in case an Image is passed
    protected void createInternalFrame(ImageContent imageContent) {
        // Command to Controller to create a new Internal Frame
        ImageInternalFrame imageInternalFrame = AppController.newImageWindow(imageContent);
        // Add a new Internal Frame into the MainUI's list of internal frame
        imageInternalFrameList.add(imageInternalFrame);
        // Add the newly create internal frame to contentPane
        desktop.add(imageInternalFrame);
        // Update Menu Windows as new Internal Frames are added to actual App.
        addMenuItemToMenu("Windows",
                createJMenuItemList(imageInternalFrameList.size(), grabAllInternalFrameNames()));
    }

    // Dynamically create an List of subItem based on Internal Window Name within Main Window
    private String[] grabAllInternalFrameNames() {
        String[] windowItemNames = new String[imageInternalFrameList.size()];
        int i = 0;
        for (ImageInternalFrame s : imageInternalFrameList) {
            windowItemNames[i] = s.getTitle();
            i++;
        }
        return windowItemNames;
    }

    // Dynamically Add new Menu Items to Window Menu
    private void addMenuItemToMenu(String menuTitle, List<JMenuItem> jMenuItems) {
        for (int i = 0; i < this.menuBar.getMenuCount(); i++) {
            if (menuTitle.equalsIgnoreCase(this.menuBar.getMenu(i).getText().toString())) {
                for (int j = 0; j < jMenuItems.size(); j++) {
                    JMenuItem s = jMenuItems.get(j);
                    if (!isItemIn(this.menuBar.getMenu(i), s.getText())) {
                        s.addActionListener(this);
                        this.menuBar.getMenu(i).add(s);
                    }
                }
            }
        }
    }

    // Check if an Item is within a Menu
    private boolean isItemIn(Object jMenu, String item) {
        // Behavior in case, we are dealing with a JMenu and searching for a specific Item
        if (jMenu instanceof JMenu) {
            for (int i = 0; i < ((JMenu) jMenu).getItemCount(); i++) {
                if (((JMenu) jMenu).getItem(i).getText().equalsIgnoreCase(item)) {
                    return true;
                }
            }
        } //Returns true if the item was inside the object.
        // Only return false in case there is no occurrence in it.
        return false;
    }

    //Quit the application.
    protected void quit() {
        AppController.shutDownApp();
        isShuttingDown = true;
        System.exit(0);
    }

    @Override
    public void run() {
        System.out.println("MainUI Thread " + Thread.currentThread().getName().toUpperCase());
        AppController.singletonInstanceApp();
        while (!isShuttingDown) {
            for (ImageContent imageContent : imagesToLoadInsideInternalFrame) {
                System.out.println("Inside loop ImageContent");
                if (imageContent.fileFuture.isDone()) {
                    System.out.println("Inside imageContent.fileFuture.isDone()");
                    if (!(imageContent.isHasInternalFrame())){
                        System.out.println("Check whether task is done ");
                        imageContent.setHasInternalFrame(true);
                        createInternalFrame(imageContent);
                    }
                }

            }
            // Prevent Loop from going crazy
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
