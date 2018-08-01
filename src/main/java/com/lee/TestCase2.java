package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.util.Random;

public class TestCase2 extends BreadthCrawler {
    /**
     * 构造一个基于伯克利DB的爬虫
     * 伯克利DB文件夹为crawlPath，crawlPath中维护了历史URL等信息
     * 不同任务不要使用相同的crawlPath
     * 两个使用相同crawlPath的爬虫并行爬取会产生错误
     *
     * @param crawlPath 伯克利DB使用的文件夹
     * @param autoParse 是否根据设置的正则自动探测新URL
     */
    public TestCase2(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        addSeed("https://www.baidu.com");
        addSeed("https://www.weibo.com");
        addSeed("https://www.taobao.com");
        addSeed("https://www.qq.com");
        setThreads(2);

    }

    public static void main(String[] args) throws Exception {
        TestCase2 crawl = new TestCase2("baidu",false);

        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
//        crawl.setRetryInterval(1000);
        crawl.start(1);
    }


    public static int count = 1;
    @Override
    public void visit(Page page, CrawlDatums next) {

        System.out.println(count++);
    }
}