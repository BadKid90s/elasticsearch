package com.wry.esjd.util;


import com.wry.esjd.pojo.ContentJD;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 解析网页工具类
 * </p>
 *
 * @author wangruiyu
 * @since 2020/8/3
 */
public class HtmlParseUtil {
    /**
     * 解析京东
     *
     * @param keyword 搜索关键字
     * @return
     * @throws IOException
     */
    public static List<ContentJD> parseJD(String keyword) throws IOException {
        //请求地址
        String url = "https://search.jd.com/Search?keyword=" + URLEncoder.encode(keyword,"utf-8");
        //解析网页
        //Jsoup返回的document就是浏览器的Document对象
        Document document = Jsoup.parse(new URL(url), 3000);
        //所以js 使用的方法都可以使用
        Element element = document.getElementById("J_goodsList");

        //获取所有的列标签
        Elements elements = element.getElementsByTag("li");

        ArrayList<ContentJD> list = new ArrayList<>();
        //获取元素中的内容，el 就是每个li 标签
        elements.forEach((el) -> {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            ContentJD  contentJD=new ContentJD();
            contentJD.setImg(img);
            contentJD.setPrice(price);
            contentJD.setTitle(title);
            list.add(contentJD);
        });
        return list;
    }

    public static void main(String[] args) throws IOException {
        List<ContentJD> list = parseJD("心理学");
        list.forEach(System.out::println);
    }
}
