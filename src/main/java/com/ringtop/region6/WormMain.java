package com.ringtop.region6;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.Executors.newFixedThreadPool;


public class WormMain {
    //待抓取的Url队列，全局共享
    public static final LinkedBlockingQueue<String> UrlQueue = new LinkedBlockingQueue<>();

    public static final WormCore wormCore = new WormCore();

    public static void main(String[] args) throws IOException {
        String sqlFile = "region.txt";
        if (Files.exists(Paths.get(sqlFile))) {
            Files.delete(Paths.get(sqlFile));
            Files.createFile(Paths.get(sqlFile));
        }

        //要抓取的根URL
        final String[] rootUrl = {Catch.ROOT_URL};
        Document document = Catch.CatchDocument(rootUrl[0]);

        Elements select = document.select("ul[class='center_list_contlist']").select("li").select("a");
        for (Element element : select) {
            rootUrl[0] = element.attr("href");
            break;
        }
        //先把根URL加入URL队列
        UrlQueue.offer(rootUrl[0]);

        Runnable runnable = new MyRunnable(sqlFile);
        //开启固定大小的线程池，爬取的过程由10个线程完成
        final int nThreads = 20;
        ExecutorService Fixed = newFixedThreadPool(nThreads);

        //开始爬取
        for (int i = 0; i < nThreads; i++) {
            Fixed.submit(runnable);
        }
        //关闭线程池
        Fixed.shutdown();

    }
}
