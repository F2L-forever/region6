package com.ringtop.region6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

public class Access {
    //数据处理，把信息中的Url返回给核心，文本信息储存
    public ArrayList<String> DataAccess(HashMap<String, ArrayList<String>> Message, String sqlFile) throws IOException {

        ArrayList<String> codes = Message.get("Code");
        ArrayList<String> texts = Message.get("Text");
        ArrayList<String> pCodes = Message.get("PCodes");
        ArrayList<String> leafs = Message.get("Leafs");
        ArrayList<String> types = Message.get("Types");
        ArrayList<String> leves = Message.get("Leves");

        StringBuilder sqlBulid = new StringBuilder();
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            String text = texts.get(i);
            String pcode = pCodes.get(i);
            String leaf = leafs.get(i);
            String leve = leves.get(i);
            String type = types.get(i);
            sqlBulid.append(code).append("|$|")
                    .append(text).append("|$|")
                    .append(pcode).append("|$|")
                    .append(type).append("|$|")
                    .append(leve)
                    //.append("|$|")
                    //.append(leaf)
/*
            sqlBulid.append("INSERT INTO `sl_area`(`id`, `code`, `parent_code`, `name`, `level`, `leaf`) VALUES (");
            sqlBulid.append("'").append(code).append("'").append(",")
                    .append("'").append(code).append("'").append(",")
                    .append("'").append(pcode).append("'").append(",")
                    .append("'").append(text).append("'").append(",")
                    .append("'").append("").append("'").append(",")
                    .append("'").append(leaf).append("'").append(");")*/
                    .append("\n");
        }
        Files.write(Paths.get(sqlFile), sqlBulid.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        final ArrayList<String> url = Message.get("Url");
        //url.stream().parallel().forEach(System.out::println);
        return url;
    }
}
