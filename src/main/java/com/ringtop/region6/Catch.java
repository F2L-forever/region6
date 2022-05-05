package com.ringtop.region6;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.DefaultJavaScriptErrorListener;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Catch {
    static final String ROOT_URL;
    static final String REFRESH_URL;
    static final String BASE_URL = "http://www.stats.gov.cn";
    private static final int count = 0;
    static Map<String, String> cookies = getCookies();

    static {
        ROOT_URL = BASE_URL + "/tjsj/tjbz/tjyqhdmhcxhfdm";
        REFRESH_URL = BASE_URL + "/WZWSREL3Rqc2ovdGpiei90anlxaGRtaGN4aGZkbS8";
    }

    //http://www.stats.gov.cn/WZWSREL3Rqc2ovdGpiei90anlxaGRtaGN4aGZkbS8=?wzwschallenge=V1pXU19DT05GSVJNX1BSRUZJWF9MQUJFTDI2MDQ3OTU=
    //http://www.stats.gov.cn/WZWSREL3Rqc2ovdGpiei90anlxaGRtaGN4aGZkbS8=?wzwschallenge=V1pXU19DT05GSVJNX1BSRUZJWF9MQUJFTDUwMjExNTk=

    //根据网页的Url获取网页Document
    public static Document CatchDocument(String url) {
        /**HtmlUnit请求web页面*/
        //1.创建连接client
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            //2.设置连接的相关选项
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);  //需要解析js
            webClient.getOptions().setThrowExceptionOnScriptError(false);  //解析js出错时不抛异常
            webClient.getOptions().setTimeout(10000);  //超时时间  ms
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            // -----重点-----设置为我们自定义的错误处理类
            webClient.setJavaScriptErrorListener(new MyJSErrorListener());
            webClient.setJavaScriptTimeout(5000);
            webClient.getOptions().setUseInsecureSSL(false);
            //3.抓取页面
            HtmlPage page = webClient.getPage(url);
            //wait for js execute

            webClient.waitForBackgroundJavaScript(10000);   //等侍js脚本执行完成

            //4.将页面转成指定格式
            //System.out.println(page.asXml());
            //5.关闭模拟的窗口

            String pageXml = page.asXml(); //以xml的形式获取响应文本
            Document document = Jsoup.parse(pageXml, BASE_URL);
            return document;
        } catch (Exception e) {
            System.out.println("Catch--------" + e.getMessage());
            return CatchDocument(url);
        } finally {
            webClient.close();
        }
    }

    private static void refresh() throws IOException {
        final Connection.Response execute = Jsoup.connect(REFRESH_URL).execute();
        cookies = getCookies().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, () -> new HashMap<>(execute.cookies())));

    }


    private static Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("_trs_uv", "");
        cookies.put("SF_cookie_1", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        cookies.put("_trs_ua_s_1", "");
        StringBuilder cid = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            cid.append(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        cookies.put("wzws_cid", cid.toString());
        return cookies;
    }

    static void captcha(String captcha) throws IOException {
        final String url = "http://www.stats.gov.cn/waf_verify.htm?captcha=" + captcha;
        final Connection.Response response = Jsoup.connect(url).ignoreContentType(true).execute();
        cookies = response.cookies();
    }


    static void captchaImage(String fileName) {
        final String url = "http://www.stats.gov.cn/waf_captcha/?" + Math.random();
        try {
            final Connection.Response response = Jsoup.connect(url).ignoreContentType(true).execute();
            byte[] img = response.bodyAsBytes();
            fileName = getFileNameprefix(".", getFileNameprefix("/", fileName));
            fileName = "";
            final String fileFullPath = "waf_captcha" + (StringUtils.isNotBlank(fileName) ? "." + fileName : "") + ".png";

            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            File file = new File(fileFullPath);
            try {
                //判断文件目录是否存在
                if (file.exists()) {
                    file.delete();
                }
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(img);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static String getFileNameprefix(String split, String url) {
        if (url.contains(split)) {
            url = url.substring(url.lastIndexOf(split) + 1);
        }
        return url;
    }


    /**
     * 忽略html unit打印的所有js加载报错信息
     */
    public static class MyJSErrorListener extends DefaultJavaScriptErrorListener {
        @Override
        public void scriptException(HtmlPage page, ScriptException scriptException) {
        }

        @Override
        public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
        }

        @Override
        public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {

        }

        @Override
        public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {

        }

        @Override
        public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {

        }
    }
}
