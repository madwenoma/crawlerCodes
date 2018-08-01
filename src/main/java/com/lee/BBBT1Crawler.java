package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

class BBBT1ImageHandler implements WorkHandler<ImageEvent> {
    //http://wx4.sinaimg.cn/mw690/006rN2x6gy1ftgbn6vvlmj30zg0oswxw.jpg
    @Override
    public void onEvent(ImageEvent event) throws Exception {
        String imageUrl = event.getImageUrl();
        System.out.println("take job from disruptor: " + imageUrl);
        String fileName = StringUtils.substringAfterLast(imageUrl, "/");
        File imageFile = new File("E:\\Downloads\\BBBT1\\", fileName);
        FileUtils.write(imageFile, event.getContent());
        System.out.println("保存图片 " + event.getImageUrl() + " 到 " + imageFile.getAbsolutePath());
    }
}

class ImageEvent {
    private String imageUrl;
    private byte[] content;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

public class BBBT1Crawler extends RamCrawler {

    private static final String SITE = "http://bbbt1.com/page/";
    private static final String IMG_DOMAIN = "http://wx4.sinaimg.cn/";
    private static Disruptor disruptor;
    private static String[] userAgents = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/55.0.2883.87 Chrome/55.0.2883.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36",
            "Mozilla/6.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Mobile Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36"};

    static {
        ThreadFactory threadFactory = r -> new Thread(r, "simpleThread");

        EventFactory<ImageEvent> factory = () -> new ImageEvent();
        SleepingWaitStrategy strategy = new SleepingWaitStrategy();
        int bufferSize = 1024 * 4;
        disruptor = new Disruptor(factory, bufferSize, threadFactory,
                ProducerType.SINGLE, strategy);
        BBBT1ImageHandler[] handlers = new BBBT1ImageHandler[3];
        for (int i = 0; i < 3; i++) {
            handlers[i] = new BBBT1ImageHandler();
        }
        disruptor.handleEventsWithWorkerPool(handlers);
        System.out.println("disruptor init over...");
        disruptor.start();
    }

    private void setRandomUA() {
        List<String> uas = Arrays.asList(userAgents);
        Collections.shuffle(uas);
        this.getConf().setDefaultUserAgent(uas.get(0));
    }

    @Override
    protected void afterParse(Page page, CrawlDatums next) {
        setRandomUA();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        System.out.println(page.url());
        //根据http头中的Content-Type信息来判断当前资源是网页还是图片
        String contentType = page.contentType();
        //根据Content-Type判断是否为图片
        if (contentType != null && contentType.startsWith("image")) {
            System.out.println("prepare to publish event: " + page.url());
            publishImageDownloadEvent(page);
        }

    }

    private void publishImageDownloadEvent(Page page) {
        RingBuffer<ImageEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            ImageEvent event = ringBuffer.get(sequence);
            event.setImageUrl(page.url());
            event.setContent(page.content());
        } finally {
            ringBuffer.publish(sequence);
        }
    }


    public static void main(String[] args) throws Exception {
        BBBT1Crawler crawler = new BBBT1Crawler();
        crawler.setAutoParse(true);
        for (int i = 1; i < 100; i++) {
            crawler.addSeed(SITE + i);
        }
        crawler.addRegex(IMG_DOMAIN + "*.*");
        crawler.addRegex("-.*#.*");
        crawler.addRegex("-.*\\?.*");
        crawler.getConf().setExecuteInterval(1000);
        crawler.getConf().setAutoDetectImg(true);
        crawler.setThreads(5);

        crawler.start(2);
    }

}

