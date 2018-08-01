package com.lee.demos.filter;


import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.plugin.nextfilter.HashSetNextFilter;

/**
 * 一个常见的使用NextFilter的业务，就是已知一些已采集或需要过滤的URL，
 * 但这些URL并不在爬虫自带的去重库中时，可通过NextFilter过滤这些已知的URL。
 * 将这些已知的URL添加到HashSetNextFilter中并将其设置为爬虫的NextFilter
 * 即可直接完成这个功能。
 *
 * 在下面的例子中，通过addRegex方法控制爬虫只会探测到
 * http://geek.csdn.net和http://lib.csdn.net这两个链接，
 * 向HashSetNextFilter中添加了http://geek.csdn.net使得该链接被NextFilter组件过滤，
 * 因此最后爬虫只会抓取种子页面和http://lib.csdn.net这两个页面。
 * @author hu
 */
public class DemoHashSetNextFilter extends BreadthCrawler {

    /*
        该例子利用WebCollector 2.50新特性NextFilter过滤探测到的URL
     */
    public DemoHashSetNextFilter(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        addSeed("http://www.csdn.net");
        addRegex("http://geek.csdn.net");
        addRegex("http://lib.csdn.net");
        //设置线程数
        setThreads(30);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        System.out.println("Not Filtered: " + page.doc().title());
    }

    public static void main(String[] args) throws Exception {
        DemoHashSetNextFilter crawler = new DemoHashSetNextFilter("crawl", true);
        HashSetNextFilter nextFilter = new HashSetNextFilter();
        //this url will be filtered
        nextFilter.add("http://geek.csdn.net");
        //nextFilter.add("http://lib.csdn.net");
        crawler.setNextFilter(nextFilter);
        crawler.start(2);
    }

}