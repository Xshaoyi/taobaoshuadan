package yishao.autobuy;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebdriverCommonUtils {
	public static WebElement findElementByTextAndCssSelector(WebDriver driver,String cssSelector,String text) throws Exception{
		List<WebElement> elemList = driver.findElements(By.cssSelector(cssSelector));
		for(WebElement ele:elemList){
			if(ele.getText().contains(text)){
				return ele;
			}
		}
		throw new Exception("Can't find the specific element");
		
	}
	public static void scrollToBottom(WebDriver driver){
		((JavascriptExecutor) driver).executeScript("scroll(0,innerHeight)");
	}
}
