package yishao.autobuy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import yishao.autobuy.data.Option;

/**
 * Hello world!
 *
 */
public class App {
	static ExecutorService executor = Executors.newFixedThreadPool(3);
	
  
	public static void main(String[] args) throws MalformedURLException {
		// Optional, if not specified, WebDriver will search your path for
		// chromedriver.
		// System.setProperty("webdriver.ie.driver",
		// "C:\\shaoyi\\webDriver\\IEDriverServer.exe");
		// 使用线程
		 for(int i=0;i<1;i++){
			 String name = "线程 " + i;
			 Option option = new Option();
			 option.setCompareShopCount(3);
			 option.setShopName("lhl1660124207");
			 Runnable runner = new ExecutorThread(name,option);
			 executor.execute(runner);
		 }
		 executor.shutdown();
	}
}

class ExecutorThread implements Runnable {
	private final String name;
	private String shopName;
	private Option option;
	private Random rd;
	WebDriver driver;
	private int preSelectedIndex =-1;
	public ExecutorThread(String name,Option option) {
		this.name = name;
		this.option = option==null? new Option():option;
		this.shopName = option.getShopName();
		rd = new Random();
		driver = new FirefoxDriver();
	}

	public void run() {
		try {
			// WebDriver driver = new RemoteWebDriver(new URL(
			// "http://127.0.0.1:4444/wd/hub"),
			// DesiredCapabilities.firefox());
			//WebDriver driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			// driver.get("https://login.taobao.com/member/login.jhtml");
			//
			// //输入用户名
			// driver.findElement(By.id("TPL_username_1")).clear();
			// driver.findElement(By.id("TPL_username_1")).sendKeys("邵义你好");
			// //输入密码
			// driver.findElement(By.id("TPL_password_1")).clear();
			// driver.findElement(By.id("TPL_password_1")).sendKeys("sy1221123");
			// driver.findElement(By.id("J_SubmitStatic")).click();
			// 重定向到淘宝首页
			driver.get("https://www.taobao.com/");
			// 搜索关键字
			driver.findElement(By.id("q")).clear();
			driver.findElement(By.id("q")).sendKeys("热干面 孜然味");
			driver.findElement(By.className("btn-search")).click();
			int targetShopIndex = -1;
			do {
		        WaitConditionUtil.waitUtilPageLoad(driver);
				
				List<WebElement> itemList = driver.findElements(By
						.cssSelector("#mainsrp-itemlist .items .item"));
				List<Integer> randomeNumberList= new ArrayList<Integer>();
				List<WebElement> compareList = new ArrayList<WebElement>();
				for (WebElement ele : itemList) {
					// System.out.println(ele.getText());
					if (ele.getAttribute("class").indexOf("activity") > -1) {
						continue;
					}
					try {
						String shopNameCurrent = ele
								.findElements(By.cssSelector(".shopname>span"))
								.get(1).getText();
						if (shopNameCurrent.equals(shopName)) {
							System.out.println("找到了*******************");
							targetShopIndex = itemList.indexOf(ele);
							preSelectedIndex=(targetShopIndex);
							break;
						}

						System.out.println(shopName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(targetShopIndex == -1){
					driver.findElement(By.cssSelector("#mainsrp-pager .next")).click();
				}else{
					getRestCompareShop();
					break;
				}
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
			;
		}

	}
	private void getRestCompareShop() {
		// TODO Auto-generated method stub
		for(int i=0;i<(option.getCompareShopCount()-1);i++){
			List<WebElement> itemList = driver.findElements(By
					.cssSelector("#mainsrp-itemlist .items .item"));
			removeSelectedItem(itemList);
			preSelectedIndex = rd.nextInt(itemList.size());
			WebElement wele = itemList.get(preSelectedIndex);
			WebElement shopLink = wele.findElement(By.cssSelector(".pic > a"));
			((JavascriptExecutor) driver).executeScript("arguments[0].target=\'_self\'",shopLink);
			wele.click();
			WaitConditionUtil.waitUtilPageLoad(driver);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WebdriverCommonUtils.scrollToBottom(driver);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			driver.navigate().back();
			WaitConditionUtil.waitUtilPageLoad(driver);
		}
		scanTrueClientShop();
	}
	private void removeSelectedItem(List<WebElement> itemList) {
		// TODO Auto-generated method stub
		itemList.remove(preSelectedIndex);
	}

	private void clickComparedShop(WebElement ele){
		WebElement shopLink = ele.findElement(By.cssSelector(".pic > a"));
		((JavascriptExecutor) driver).executeScript("arguments[0].target=\'_self\'",shopLink);
	}
	public  void scanTrueClientShop(){
		List<WebElement> itemList = driver.findElements(By
				.cssSelector("#mainsrp-itemlist .items .item"));
		for (WebElement ele : itemList) {
			// System.out.println(ele.getText());
			if (ele.getAttribute("class").indexOf("activity") > -1) {
				continue;
			}
			try {
				String shopNameCurrent = ele
						.findElements(By.cssSelector(".shopname>span"))
						.get(1).getText();
				if (shopNameCurrent.equals(shopName)) {
					System.out.println("找到了*******************");
					WebElement shopLink = ele.findElement(By.cssSelector(".pic > a"));
					((JavascriptExecutor) driver).executeScript("arguments[0].target=\'_self\'",shopLink);
					ele.click();
					WaitConditionUtil.waitUtilPageLoad(driver);
					driver.findElement(By.cssSelector("#J_juValid .tb-btn-buy")).click();
					WaitConditionUtil.waitUtilPageLoad(driver);
					break;
				}

				System.out.println(shopName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//driver.
	}
	public static void findProfile(WebDriver driver, String profileName) {
		List<WebElement> weList = driver.findElements(By
				.cssSelector(".listItemPad"));
		for (WebElement ele : weList) {
			if (ele.getText().equalsIgnoreCase(profileName.substring(0, 1))) {
				// WebDriverWait wait = new WebDriverWait(driver, 10);
				// WebElement element =
				// wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
				ele.click();
				
				break;
			}
		}
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Boolean element = wait.until(ExpectedConditions.textToBe(
				By.cssSelector(".x-grid3-col-ProfileName>a>span"),
				"Vendor Administrator"));
		WebElement profileLink = driver.findElement(By
				.cssSelector(".x-grid3-cell-inner>a"));
		System.out.println(profileLink.getText());
		JavascriptExecutor ex = (JavascriptExecutor) driver;
		ex.executeScript("arguments[0].click();", profileLink);
	}
}
