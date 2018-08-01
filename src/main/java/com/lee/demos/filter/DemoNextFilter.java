package com.lee.demos.filter;

import cn.edu.hfut.dmic.webcollector.fetcher.NextFilter;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * WebCollector会自己实现去重，不要多此一举
 *
 * 注意：对于95%的爬虫任务，没有必要定制NextFilter插件，
 * 爬虫自身的去重机制可以应付大部分的任务。
 * NextFilter是一个高级去重插件，
 * 往往应用在用户需要依赖自己的数据库进行去重的场景。
 * 另外，使用NextFilter之前请仔细阅读该教程，
 * 它可能与你见过的其它爬虫的去重插件完全不是同一个东西。
 *
 * 作用
 *
 * NextFilter的功能是：帮助爬虫过滤探测到的URL信息。
 *
 * WebCollector在爬取过程中自带了去重的功能，
 * NextFilter是一个用于过滤的插件，与爬虫自带的去重功能无关。
 *
 * 例如，用WebCollector定制一个爬取CSDN博客的程序时很容易的，
 * 但某个开发者遇到的情况是，他的同事之前用其他的爬虫已经采集了50万篇CSDN博客，
 * 放到了业务用的MYSQL中，但是现在这位开发者需要编写新的爬虫，
 * 在编写新爬虫时不希望爬虫采集那50万篇已经采集的博客，
 * 然而新的爬虫的去重库里并没有这50万篇博客的URL。
 * 这时NextFilter就派上用场了，新的爬虫自身可以保证在爬取CSDN时不进行重复爬取，
 * 定制一个NextFilter，用于过滤掉爬虫探测到的URL中与那50万篇CSDN博客URL重复的部分，
 * 就可以保证新的爬虫：
 *
 *
 *
 * 爬虫自身不重复爬取
 * 爬虫不爬取业务库中已有的50万篇博客
 *
 * 下面这个例子使用NextFilter对爬虫探测到的URL进行过滤，
 * 代码通过将autoParse设置为true和addRegex(".*")让爬虫自动探测网页中所有的URL，
 * 该例子加入了一个自定义的NextFilter对这些URL进行过滤，
 * 只有满足”http://blog.csdn.net/./article/details/.“这个正则的URL才被保留。
 * @author hu
 */
public class DemoNextFilter extends BreadthCrawler {

    /*
        该例子利用WebCollector 2.50新特性NextFilter过滤探测到的URL
     */
    public DemoNextFilter(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        addSeed("http://blog.csdn.net/");
        addRegex(".*");
        //设置线程数
        setThreads(30);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if (page.matchType("content")) {
            String title = page.select("div[class=article_title]").first().text();
            String author = page.select("div[id=blog_userface]").first().text();
            System.out.println("title:" + title + "\tauthor:" + author);
        }
    }

    public static void main(String[] args) throws Exception {
        DemoNextFilter crawler = new DemoNextFilter("crawl", true);
        crawler.setNextFilter(new NextFilter() {
            @Override
            public CrawlDatum filter(CrawlDatum nextItem, CrawlDatum referer) {
                if (nextItem.matchUrl("http://blog.csdn.net/.*/article/details/.*")) {
                    nextItem.type("content");
                    return nextItem;
                } else {
                    return null;
                }
            }
        });
        crawler.start(2);
    }

}