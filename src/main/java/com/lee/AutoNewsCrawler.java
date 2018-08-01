package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class AutoNewsCrawler extends BreadthCrawler {
    private static int imageCount = 1;
    public static final String DOMAIN = "http://www.ifuli10.cc/";

    //    private List<String> iamges = new ArrayList<String>(1024);
    LinkedBlockingQueue<String> jobQueue;

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public AutoNewsCrawler(LinkedBlockingQueue<String> jobQueue, String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.jobQueue = jobQueue;
        getConf().setAutoDetectImg(true);
        /*start page*/
        this.addSeed("http://www.ifuli10.cc/zhubo/gn/");

        /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
        this.addRegex("http://www.ifuli10.cc/*.*html");
        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");
        addRegex("-.*\\?.*");
        setThreads(10);
        getConf().setTopN(100);

//        setResumable(true);
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
        System.out.println(Thread.currentThread().getId());
        /*if page is news page*/
        if (page.matchUrl("http://www.ifuli10.cc/*.*html")) {

            /*extract title and content of news by css selector*/
            System.out.println(page.url());
            Elements content = page.select(".article-content");
            if (content != null) {
                Elements images = content.select("img[src$=.png]");
//                for (Element image : images) {
//                    String imageUrl = image.attr("src");
//                    downloadImg(DOMAIN + imageUrl);
//                }
                if (images != null) {
                    images.addAll(content.select("img[src$=.jpg]"));
                } else {
                    images = content.select("img[src$=.jpg]");
                }
//                images = content.select("img[src$=.jpg]");
                for (Element image : images) {
                    String imageUrl = image.attr("src");
//                    iamges.add(imageUrl);
                    try {
                        imageUrl = DOMAIN + imageUrl;
                        imageCount++;
                        jobQueue.put(imageUrl);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    imageUrl = DOMAIN + imageUrl;
//                    String imageName = StringUtils.substringAfterLast(imageUrl, "/");
//                    System.out.println("imageName:" + imageName);
//                    String localFile = "E:\\workspace\\idea01\\meishijieimages\\" + imageName;
//                    OkHttpUtil.downloadPic(imageUrl,localFile);
                }

            }

            /*If you want to add urls to crawl,add them to nextLink*/
            /*WebCollector automatically filters links that have been fetched before*/
            /*If autoParse is true and the link you add to nextLinks does not match the regex rules,the link will also been filtered.*/
            //next.add("http://xxxxxx.com");
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
        AutoNewsCrawler crawler = new AutoNewsCrawler(jobQueue, "crawl", true);
        /*start crawl with depth of 4*/
        ImagerDownloader downloader = new ImagerDownloader(jobQueue);
        new Thread(downloader).start();
        crawler.start(2);

    }

    public static void downloadImg(final String fileUrl) {
        System.out.println("begin to download image:" + fileUrl);
        final OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(fileUrl)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将响应数据转化为输入流数据
                InputStream inputStream = response.body().byteStream();
                //将输入流数据转化为Bitmap位图数据
                String imageName = StringUtils.substringAfterLast(fileUrl, "/");
                System.out.println("imageName:" + imageName);
                File file = new File("E:\\workspace\\idea01\\meishijieimages\\" + imageName);
                file.createNewFile();
                //创建文件输出流对象用来向文件中写入数据
                FileOutputStream out = new FileOutputStream(file);
                //将bitmap存储为jpg格式的图片
                byte[] buf = new byte[2048];
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
                //刷新文件流
                inputStream.close();
                out.close();


            }
        });
    }


    static class ImagerDownloader implements Runnable {

        private LinkedBlockingQueue<String> jobQueue;

        public ImagerDownloader(LinkedBlockingQueue<String> jobQueue) {
            this.jobQueue = jobQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    doDownloadJob();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void doDownloadJob() throws InterruptedException {
            System.out.println("imageCount:" + imageCount);
            System.out.println("queue size:" + this.jobQueue.size());
            String imageUrl = jobQueue.take();
            System.out.println("take job from queue: " + imageUrl);
            String imageName = StringUtils.substringAfterLast(imageUrl, "/");
            String localFile = "E:\\Downloads\\meishijieimages\\" + imageName;
            System.out.println(localFile);
            OkHttpUtil.downloadPic(imageUrl, localFile);
        }


    }
}