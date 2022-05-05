package com.ringtop.region6;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


public class Analysis {

    public static boolean isInteger(String str) {
        Pattern pattern = compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String getPcode(String pcode) {
        StringBuilder pcodeBuild = new StringBuilder(pcode);
        for (int i = 0; i < 12 - pcode.length(); i++) {
            pcodeBuild.append("0");
        }
        return pcodeBuild.toString();

    }

    //根据Document解析网页
    public HashMap<String, ArrayList<String>> AnalysisDocument(Document document, String Url) {
        //因为网页上的URL为相对地址，所以在这里进行URL的拼接，这是前半部分
        String Before_Url = Url.substring(0, Url.lastIndexOf("/") + 1);

        final String[] pcode = {Url.substring(Url.lastIndexOf("/") + 1)};

        //储存文本信息的List
        ArrayList<String> Texts = new ArrayList<>();
        ArrayList<String> Codes = new ArrayList<>();
        ArrayList<String> PCodes = new ArrayList<>();
        ArrayList<String> Leafs = new ArrayList<>();
        ArrayList<String> Types = new ArrayList<>();
        ArrayList<String> Leves = new ArrayList<>();
        //储存Url的List
        ArrayList<String> Urls = new ArrayList<>();

        HashMap<String, ArrayList<String>> Message = new HashMap<>();
        //最后一个页面的前三个文本不是我们想要的
        final int[] Flag = {1};

        Elements elements = document.select("tr[class]").select("a[href]");
        //最后一个页面的处理
        if (elements.isEmpty()) {
            elements = document.select("tr[class]").select("td");
            elements.stream().forEach(element -> {
                String url = element.baseUri().trim();
                if (pcode[0].contains(".")) {
                    pcode[0] = pcode[0].substring(0, pcode[0].lastIndexOf(".")).replaceAll("/", "");
                }
                pcode[0] = pcode[0].equals("index") ? "0" : pcode[0];
                pcode[0] = getPcode(pcode[0]);
                String text = element.text();
                if (!isInteger(text) && Flag[0] > 3) {
                    Texts.add(text);
                    Urls.add(Before_Url + text);
                    PCodes.add(pcode[0]);
                    Leafs.add("1");
                } else if (Flag[0] > 3 && text.length() >= pcode[0].length()) {
                    Codes.add(text);
                }
                Flag[0]++;
            });
            //普通页面的处理
        } else {
            elements.stream().forEach(element -> {
                String text = element.text();
                String url = element.attr("href");
                if (!isInteger(text)) {
                    if (pcode[0].contains(".")) {
                        pcode[0] = pcode[0].substring(0, pcode[0].lastIndexOf(".")).replaceAll("/", "");
                    }
                    pcode[0] = pcode[0].equals("index") ? "0" : pcode[0];
                    pcode[0] = getPcode(pcode[0]);
                    if ("000000000000".equals(pcode[0])) {
                        if (url.contains(".")) {
                            String code = url.substring(0, url.lastIndexOf(".")).replaceAll("/", "");
                            code = getPcode(code);
                            Codes.add(code);
                        }
                    }
                    Texts.add(text);
                    Urls.add(Before_Url + url);
                    PCodes.add(pcode[0]);
                    Leafs.add("0");
                } else {
                    Codes.add(text);
                }
            });
        }
        //把文本集合和URL集合装到Map中返回
        Message.put("PCodes", PCodes);

        AtomicInteger maxLen = new AtomicInteger();
        Codes.stream().forEach(item -> {
            if (StringUtils.isNotEmpty(item)) {
                final int len = item.replaceAll("0*$", "").length();
                if (len > maxLen.get()) {
                    maxLen.set(len);
                }
            }
        });

        for (int i = 0; i < Codes.size(); i++) {
            if (maxLen.get() <= 6) {
                Types.add(0 + "");
                Leves.add((maxLen.get() / 2) + "");
            } else {
                Types.add(1 + "");
                Leves.add((maxLen.get() / 2 / 2) + "");
            }
        }

        Message.put("Code", Codes);
        Message.put("Text", Texts);
        Message.put("Leafs", Leafs);
        Message.put("Types", Types);
        Message.put("Leves", Leves);
        Message.put("Url", Urls);
        return Message;

    }


}
