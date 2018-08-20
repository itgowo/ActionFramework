package com.itgowo.baseServer.utils;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class WatchFileService extends Thread {
    private String actionDir;
    private boolean running = true;
    private onWatchFileListener watchFileListener;

    public WatchFileService(String actionDir) {
        this.actionDir = actionDir;
        setName("WatchFileService");
    }

    @Override
    public void run() {
        super.run();
        try {
            listeneDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WatchFileService startWatch(onWatchFileListener watchFileListener){
        this.watchFileListener = watchFileListener;
        start();
        return this;
    }

    public WatchFileService stopWatch() {
        this.running = false;
        return this;
    }

    private void listeneDir() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(actionDir).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        while (running) {
            WatchKey key = null;
            try {
                key = watchService.take();
                List<WatchEvent<?>> watchEvents = key.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
                        watchFileListener.onCreateFile(actionDir, ((Path) event.context()).toString());
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind()) {
                        watchFileListener.onModifyFile(actionDir, ((Path) event.context()).toString());
                    } else if (StandardWatchEventKinds.ENTRY_DELETE == event.kind()) {
                        watchFileListener.onDeleteFile(actionDir, ((Path) event.context()).toString());
                    }

                }
                key.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public interface onWatchFileListener {
        void onCreateFile(String dir, String fileName);

        void onModifyFile(String dir, String fileName);

        void onDeleteFile(String dir, String fileName);
    }
}
