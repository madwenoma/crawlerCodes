package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class InCrawler extends BreadthCrawler {
    private static int imageCount = 1;

    public static String[] userAgents = {
            "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-cn; PE-TL20 Build/HuaweiPE-TL20) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
            "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.4882.400 QQBrowser/9.7.13059.400",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11"
    };


    //    private List<String> iamges = new ArrayList<String>(1024);
    LinkedBlockingQueue<String> jobQueue;

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public InCrawler(LinkedBlockingQueue<String> jobQueue, String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.jobQueue = jobQueue;
        getConf().setAutoDetectImg(true);
        /*start page*/
//        this.addSeed("http://www.lofter.com/activity?act=qbview_20130930_03");
        this.addSeed("http://www.lofter.com/tag/%E6%89%8B%E5%86%99%E6%97%B6%E5%85%89");

        //[a-zA-z]+://[^\s]*.lofter.com
        this.addRegex("http://[^\\s]*.lofter.com");
//        this.addRegex("http://www.lofter.com/tag/*.*");
        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");
        addRegex("-.*\\?.*");
        setThreads(1);
        getConf().setTopN(100);

//        setResumable(true);
    }

    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        int randomUANum = new Random().nextInt(5);
        request.setUserAgent(userAgents[randomUANum]);
        String cookie="usertrack=ezq0plr5e9YnxSm+B2zhAg==; _ntes_nnid=ed82b8a7347e3417cd4cf39398b678c4,1526777322004; JSESSIONID-WLF-XXD=7b20e5bd6bca79f652b954b5b30e5b25e3434e44e3862070830a913c8b4b919a98b73115c4cd51bce56c52946a6444fecbf4c9ae1bf063edfbeaf35dba127bb49ee78f6784b86274e06cc1cd0eb59001c143c06a8bb6ed221bf32db30d1ee3b2dc996d7e936aff27b5bdd5efc6417fc94abc1c5253829edc792549a8990fd4e592b4fe12; reglogin_hasopened=1; fastestuploadproxydomainkey=uploadbj|1531541027604; firstentry=%2Flogin.do%3FX-From-ISP%3D2|https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3DhHjOZB331OjRj5BRmA4SXaDdwYEt_ABOrdOzTwALsc7%26wd%3D%26eqid%3D8bf1c70a0001a48e000000025b4b1a1e; LOF_SESS_T_valcode_REG=d64c9d43f03a4025c0c757043acd1235; regtoken=1000; _gat=1; S_INFO=1531648573|0|1&25##|o.noma; P_INFO=o.noma@163.com|1531648573|1|lofter|11&20|bej&1531578438&mail163#bej&null#10#0#0|&0|mail163&kaola_check|o.noma@163.com; LOFTER-PHONE-LOGIN-FLAG=0; noAdvancedBrowser=0; _ga=GA1.2.1680042606.1526777323; _gid=GA1.2.2003093337.1531648576; NTESwebSI=230451FE654160C50042BAF9C0A30F89.hzayq-lofter26.server.163.org-8010; __utma=61349937.1680042606.1526777323.1531541026.1531648578.3; __utmb=61349937.4.8.1531648578; __utmc=61349937; __utmz=61349937.1531541026.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic";
        request.setCookie(cookie);
        //        request.setMethod(crawlDatum.meta("method"));
//        String outputData = crawlDatum.meta("outputData");
//        if (outputData != null) {
//            request.setOutputData(outputData.getBytes("utf-8"));
//        }
        return request.responsePage();
    }


    @Override
    protected void afterParse(Page page, CrawlDatums next) {
        super.afterParse(page, next);
    }

    @Override
    public void afterStop() {
        super.afterStop();
        System.out.println("after stop:" + imageCount);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        try {
            Thread.sleep(new Random().nextInt(4) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(page.url());
//        if (page.matchUrl("http://www.lofter.com/tag/*.*")) {
//            System.out.println("matched tag..." + page.url());
//        }

        if (page.matchUrl("http://[^\\s]*.lofter.com")) {
            System.out.println("matched profile..." + page.url());
        }
    }

    public static void main(String[] args) throws Exception {
//        String url = DOMAIN + "/d/file/2016-01-13/d30142cc594a480174f685d904107512.jpg";
//        String imageName = StringUtils.substringAfterLast(url, "/");
//        System.out.println("imageName:" + imageName);
//        String localFile = "E:\\workspace\\idea01\\meishijieimages\\" + imageName;
//        System.out.println(localFile);
//        OkHttpUtil.downloadPic(url, localFile);

        LinkedBlockingQueue<String> jobQueue = new LinkedBlockingQueue<String>(1024);
        InCrawler crawler = new InCrawler(jobQueue, "crawl", true);
        /*start crawl with depth of 4*/

        crawler.start(8);

    }


}