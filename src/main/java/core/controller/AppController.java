package core.controller;

import core.model.ImageContent;
import core.view.ImageInternalFrame;
import core.view.MainUI;
import core.model.ImageTask;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppController extends Thread {
    // Create single Instance of MainUI which is used as conduct every task
    // once the application starts.
    private static final MainUI appMainUI = new MainUI(); // Holds an Instance of MainUI
    private static JDesktopPane desktop = new JDesktopPane(); // Manages Internal Frame named ImageInternalFrame
    private static ExecutorService executorService = Executors.newSingleThreadExecutor(); // Maintain a Queue of Tasks
    private static List<ImageContent> futureList = new ArrayList<>(); // Holds a copy of each Task and its Status
    private static long appControllerIdleTime = 5000; // Manages refresh speed of AppController

//    private static ExecutorService executorService = Executors.newCachedThreadPool();


    // Singleton Pattern
    public static MainUI singletonInstanceApp() {
        return appMainUI;
    }

    public static ImageInternalFrame newImageWindow() {

        ImageInternalFrame frame = new ImageInternalFrame();
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        return frame;
    }

    // Overloading ImageInternalFrame
    public static ImageInternalFrame newImageWindow(ImageContent imageContent) {
        ImageInternalFrame frame = new ImageInternalFrame(imageContent);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        return frame;
    }

    public static void shutDownApp() {
        executorService.shutdown();
    }

    public static void fetchImageFromURL(URL aURL) throws ExecutionException, InterruptedException {
        System.out.println("Inside fetch Image :" + Thread.currentThread().getName().toUpperCase().toString());
        appControllerIdleTime = 5000;
        // Retrieve File Name from URL using a getFileNameFromURL Function
        String anImageFileName = getFileNameFromURL(aURL);
        Future<File> aResult = executorService.submit(new ImageTask(aURL, anImageFileName));
        futureList.add(new ImageContent(aResult));
//        Future<?> aResult = executorService.submit(() -> {
//            // Launching a new Task Image which is going to download an Image accordingly to passed URL
//            System.out.println("AppController "+Thread.currentThread().getName().toUpperCase().toString());
//            new ImageTask(aURL, anImageFileName);
//        });
        // Check if task is done
//        System.out.println("Queue of Task Completed : "+aResult.get() == null? "Success" : "Failed");
//        if(aResult.isDone()){
//            System.out.println("Task Completed "+ anImageFileName+ " : "+aResult.isDone());
//        }else {
//            System.out.println("Task pending");
//        }

    }
    // Check whether all submitted task are done
    static boolean executorServiceTasksListStatus(){
        for (ImageContent a: futureList){
            if(!a.fileFuture.isDone()){
                return false;
            }
        }
        return true;
    }
    // Retrieve the File's name for saving purpose based on the actual URL passed as parameter.
    private static String getFileNameFromURL(URL aURL) {
        return aURL.getFile().substring(
                aURL.getFile().lastIndexOf("/") + 1,
                aURL.getFile().length());
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Inside AppController Thread :" + currentThread().getName().toString());
        Thread t = new Thread(AppController.singletonInstanceApp());
        t.start();
        while (!executorService.isShutdown()) {
            if(futureList.size() > 0) {
                displayTasksStatusToConsole();
                loadImageInsideInternalFrame();
            }
            try {
                // AppController is going to sleep for x secondes
                Thread.sleep(appControllerIdleTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check if all the tasks inside the pool are completed
            if (executorServiceTasksListStatus()){
                // In case there is no task pending,
                // the ExecutorService is going to hibernation for 15 seconds
                appControllerIdleTime = 15000;
            }
        }
    }
    // Add a new Image to the list of Images to be loaded
    private void loadImageInsideInternalFrame(){
        for(ImageContent imageContent : futureList){
            if(!imageContent.isHasInternalFrame()){
                if(imageContent.fileFuture.isDone()){
                    AppController.appMainUI.imagesToLoadInsideInternalFrame.add(imageContent);
                }else {
                    System.out.println("Not yet : isHasInternalFrame : "+imageContent.isHasInternalFrame());
                }
            }
        }


    }
    // Display Tasks Status to Console or log them to logging file
    private void displayTasksStatusToConsole() {
        int idTask = 0;
        for (ImageContent a: futureList) {
            System.out.println("Task id:"+idTask+" Status : "
                    + futureList.get(idTask).fileFuture.isDone());
            idTask++;
        }
    }
}
