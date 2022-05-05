package com.ringtop.region6;

import java.io.IOException;


class MyRunnable implements Runnable {

    private final String sqlFile;

    public MyRunnable(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //把主方法中的URL队列传给核心控制类，开始该线程的爬取
                WormMain.wormCore.Wormcore(WormMain.UrlQueue, sqlFile);
            } catch (IOException | InterruptedException e) {
                System.out.println("MyRunnable--------" + e.getMessage() + "--------" + e);
            }
        }

    }
}
