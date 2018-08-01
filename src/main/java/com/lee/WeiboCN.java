package com.lee;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Set;

/**
 * 该登录算法适用时间: 2017-6-2 —— ?
 * 利用Selenium获取登陆新浪微博weibo.cn的cookie
 *
 * @author hu
 */
public class WeiboCN {

    /**
     * 获取新浪微博的cookie，这个方法针对weibo.cn有效，对weibo.com无效 weibo.cn以明文形式传输数据，请使用小号
     *
     * @param username 新浪微博用户名
     * @param password 新浪微博密码
     * @return
     * @throws Exception
     */
    public static String getSinaCookie(String username, String password) throws Exception {
        StringBuilder sb = new StringBuilder();
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER);

        driver.setJavascriptEnabled(true);
        driver.get("https://passport.weibo.cn/signin/login");
        driver.executeScript("document.getElementById('loginWrapper').style.display = 'block'");
//        WebElement mobile = driver.findElementByCssSelector("input#loginName");
        WebElement mobile = driver.findElementById("loginName");
        mobile.sendKeys(username);
//        WebElement pass = driver.findElementByCssSelector("input#loginPassword");
        WebElement pass = driver.findElementById("loginPassword");
        pass.sendKeys(password);
//        WebElement submit = driver.findElementByCssSelector("a#loginAction");
        WebElement submit = driver.findElementById("loginAction");
        submit.click();
        Thread.sleep(2000);
        String result = concatCookie(driver);
        System.out.println("Get Cookie: " + result);
        driver.close();

        if (result.contains("SUB")) {
            return result;
        } else {
            throw new Exception("weibo login failed");
        }
    }

    public static String concatCookie(HtmlUnitDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getSinaCookie("hupozhu2","4250099a@"));;
    }
}