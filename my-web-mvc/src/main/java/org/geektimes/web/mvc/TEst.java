package org.geektimes.web.mvc;

/**
 * @description
 * @autor 吴光熙
 * @date 2021/3/3  11:32
 **/
public class TEst {

    public static void main(String[] args) {
        String str = "2020/11/30";
        System.out.println(str.substring(str.indexOf("/") + 1, str.lastIndexOf("/")));
    }
}
