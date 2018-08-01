package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JiepaiSourceCrawler extends RamCrawler {

    private static final String SITE = "https://weibo.cn/gunshu?page=";
    private ConcurrentMap<String, String> urlFileNameMap = new ConcurrentHashMap<>(1024 * 8);
    static String cookie = "SUB=_2A252XzgeDeRhGedO4lMZ8CjKzz2IHXVVoFhWrDV6PUJbkdAKLW3ZkW1NXJwkxD59mgZq7T41KAlo6i_JyZtPdC5L;SUHB=0KAgEG_PNRHSFu;SCF=AkBjeSia3eV3GoY72BMoVXJMLoDviM0OhNvcn58n_INCI4rSk7lDCEqxUloPs9PeuGGynfFIxJN0pZejXZTdHg8.;SSOLoginState=1532708942;_T_WM=e6cd5561c748b5c8d1857cc505b3495b;WEIBOCN_FROM=1110006030;MLOGIN=1;M_WEIBOCN_PARAMS=uicode%3D20000174;";
    private static String[] userAgents = {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.277.400 QQBrowser/9.4.7658.400",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 TheWorld 7",
            "Mozilla/5.0 (Windows NT 6.1; W…) Gecko/20100101 Firefox/60.0"};


    // 自定义的请求插件
// 可以自定义User-Agent和Cookie
    public static class MyRequester extends OkHttpRequester {

        private String getRandomUA() {
            List<String> uas = Arrays.asList(userAgents);
            Collections.shuffle(uas);
            return uas.get(0);
        }

        // 每次发送请求前都会执行这个方法来构建请求
        @Override
        public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
//            cookie = WeiboCN.getSinaCookie("hupozhu2", "4250099a@");
            // 这里使用的是OkHttp中的Request.Builder
            // 可以参考OkHttp的文档来修改请求头
            return super.createRequestBuilder(crawlDatum)
                    .addHeader("referer","weibo.cn/")
                    .addHeader("User-Agent", getRandomUA())
                    .addHeader("Cookie", cookie);
        }

    }

    //设置代理
    Proxys proxys = new Proxys();
    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setProxy(proxys.nextRandom());
        return request.responsePage();
    }
//    @Override
//    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
//        HttpRequest request = new HttpRequest(crawlDatum);
//        request.setCookie(cookie);
//        return request.responsePage();
//    }

    Random r = new Random();

    @Override
    public void visit(Page page, CrawlDatums next) {
        System.out.println("begin " + page.url());
//        String contentType = page.contentType();
        System.out.println(page.url());
        //根据http头中的Content-Type信息来判断当前资源是网页还是图片
        Elements one = page.select(".c");
        Elements es = one.select("div:nth-child(2)");
        for (Element oneDiv : es) {
            Elements aTags = oneDiv.select("a");
            Element spanTag = oneDiv.select("span").get(0);
            Elements imageTags = oneDiv.select("img");
            if (imageTags == null || imageTags.size() == 0) continue;
            String wapUrl = imageTags.get(0).attr("src");
            String date = spanTag.ownText();
            if (date != null) {
                date = date.split(" ")[0];
            }
            for (Element a : aTags) {
                int zanCount;
                String text = a.ownText();
                if (text.contains("赞")) {
                    String zanCountStr = StringUtils.substringBetween(text, "[", "]");
                    zanCount = Integer.parseInt(zanCountStr);
                    if (zanCount < 2000) break;
                    String fileName = zanCountStr + "-" + date + "-" + r.nextInt(4);
                    wapUrl = StringUtils.replace(wapUrl, "wap180", "wap720");
                    urlFileNameMap.put(wapUrl, fileName);
                    System.out.println("put.." + wapUrl + "-" + fileName);
                }
            }
        }

        try {
            Thread.sleep(new Random().nextInt(3) * 1000);
            if (num++ % 100 == 0) {
                Thread.sleep(new Random().nextInt(15) * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterStop() {
        System.out.println(urlFileNameMap.size());
        try {
            Thread.sleep(5000);
            JiepaiCrawler jiepaiCrawler = new JiepaiCrawler(urlFileNameMap);
            jiepaiCrawler.start(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int num = 1;

    public static void main(String[] args) throws Exception {
        JiepaiSourceCrawler crawler = new JiepaiSourceCrawler();
        crawler.setAutoParse(true);
        for (int i = 1; i < 2907; i++) {
            crawler.addSeed(SITE + i);
        }
//        crawler.addRegex(IMG_DOMAIN + "*.*");
        crawler.addRegex("-.*#.*");
        crawler.addRegex("-.*\\?.*");
        crawler.getConf().setExecuteInterval(1000);
//        crawler.getConf().setMaxExecuteCount()
//        crawler.getConf().setAutoDetectImg(true);
        crawler.setThreads(1);

        crawler.start(1);
    }
}

