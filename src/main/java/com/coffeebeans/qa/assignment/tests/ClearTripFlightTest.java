package com.coffeebeans.qa.assignment.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.coffeebeans.qa.assignment.po.FlightListPO;
import com.coffeebeans.qa.assignment.po.SearchFlightPO;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author Spoorthi M.
 * 
 * Test methods in a sequence of execution
 */
public class ClearTripFlightTest {
	private static final String CLEAR_TRIP_URL = "https://www.cleartrip.com";
	private Properties prop = new Properties();
	private WebDriver driver;
	private SearchFlightPO searchPage;
	private FlightListPO listPage;

	ExtentSparkReporter spark;
	ExtentReports extent;
	ExtentTest extentTest;

	@BeforeClass
	@Parameters("browser")
	public void setupTest(String browser) {
		// load the prpperties to get the test values
		try {
			prop.load(getClass().getResourceAsStream("/assignment.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String targetBrowser = browser;
		if(targetBrowser == null) {
			targetBrowser = prop.getProperty("test-browser");
		}
		//read and set the driver path if specified
		String webDriverPath = prop.getProperty("webdriver-path");
		if(null !=webDriverPath && !webDriverPath.isEmpty()) {
			System.setProperty("webdriver.chrome.driver", webDriverPath);
		}
		switch(targetBrowser) {
		case "CHROME":
			driver = new ChromeDriver();
			break;
		case "FIREFOX":
			driver = new FirefoxDriver();
		default:
			driver = new HtmlUnitDriver(BrowserVersion.CHROME, true) {
				@Override
				protected WebClient newWebClient(BrowserVersion version) {
					WebClient webClient = super.newWebClient(version);
					webClient.getOptions().setThrowExceptionOnScriptError(false);
					return webClient;
				}
			};
			break;
		}
		driver.get(CLEAR_TRIP_URL);
		searchPage = new SearchFlightPO(driver);
		listPage = new FlightListPO(driver);
		driver.manage().window().maximize();

		// get rid of notification
		//doWait(3000);
		//new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();

		initExtentReports();
	}

	private void initExtentReports() {
		try {
			spark = new ExtentSparkReporter("/");
			extent = new ExtentReports();
			extent.attachReporter(spark);
			extentTest = extent.createTest("MyFirstTest");
			spark.loadXMLConfig("/src/main/resources/extent-html-reporter.xml", true);
		} catch (Exception e) {}
	}

	@AfterClass
	public void teardown() {
		doWait(getPropNumber("tear-down-wait"));
		driver.quit();
	}

	@BeforeMethod
	public void beforeTests() {
		doWait(getPropNumber("test-wait-duration"));
	}

	@Test(priority = 1)
	public void clickOnFlights() {
		searchPage.clickOnFlights();
		extentTest.pass("Flights cick succeeded!");
	}

	@Test(priority = 2)
	public void selectRoundTrip() {
		searchPage.selectRoundTrip();
		extentTest.pass("Round trip selection succeeded!");
	}

	@Test(priority = 3)
	public void setFrom() {
		searchPage.setFrom(prop.getProperty("from-place"));
	}

	@Test(priority = 4)
	public void setTo() {
		searchPage.setTo(prop.getProperty("to-place"));
	}

	@Test(priority = 5)
	public void setDepartDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, getPropNumber("start-trip-offset"));
		System.out.println("From date: " + cal.getTime());
		searchPage.setDepartDate(cal.getTime());
	}

	@Test(priority = 6)
	public void setReturnDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, getPropNumber("start-trip-offset") + getPropNumber("trip-duration"));
		System.out.println("To date: " + cal.getTime());
		searchPage.setReturnDate(cal.getTime());
	}

	@Test(priority = 7)
	public void clickOnSearchBtn() {
		String alternateURL = prop.getProperty("alternate-layout-url");
		if(null != alternateURL && !alternateURL.isEmpty()) {
			driver.navigate().to(alternateURL);
		} else {
			searchPage.clickOnSearchBtn();
		}
	}

	@Test(priority = 8)
	public void listOnwardFlights() {
		waitForFlightsListToLoad(3000);
		List<Integer> prices = listPage.listOnwardFlightPrice();
		List<Integer> result = findPricesLessThan(prices, 5000);
		System.out.println("\nOut of " + prices.size() + " departing flights prices: " + prices + "\n" + result.size() + " flights have price of less that Rs. 5000: " + result);
	}

	@Test(priority = 9)
	public void listReturnFlights() {
		List<Integer> prices = listPage.listReturnFlightPrice();
		List<Integer> result = findPricesLessThan(prices, 7000);
		System.out.println("\nOut of " + prices.size() + " returning flights prices: " + prices + "\n" + result.size() + " flights have price of less that Rs. 7000: " + result);
	}

	@Test(priority = 10)
	public void applyFilters() {
		listPage.applyNonStopFilter();
		doWait(2000);
		listPage.applyOneStopFilter();
		doWait(2000);
	}

	@Test(priority = 11)
	public void listOnwardAndReturnFlights() {
		waitForFlightsListToLoad(1000);
		List<Integer> onwrdPrices = listPage.listOnwardFlightPrice();
		List<Integer> onwardResult = findPricesLessThan(onwrdPrices, 7000);
		System.out.println("\nOut of " + onwrdPrices.size() + " departing flights prices: " + onwrdPrices + "\n" + onwardResult.size() + " flights have price of less that Rs. 7000: " + onwardResult);
		List<Integer> returnPrices = listPage.listReturnFlightPrice();
		List<Integer> returnResult = findPricesLessThan(returnPrices, 7000);
		System.out.println("\nOut of " + returnPrices.size() + " returning flights prices: " + returnPrices + "\n" + returnResult.size() + " flights have price of less that Rs. 7000: " + returnResult);
	}

	@Test(priority = 12)
	public void selectOnwardAndReturnFlights() {
		doWait(2000);
		listPage.selectOnwardFlightByIndex(1);
		doWait(2000);
		listPage.selectReturnFlightByIndex(4);
	}

	@Test(priority = 13)
	public void verifySum() {
		Integer onwardPrice = listPage.getOnwardFlightPrice();
		Integer returnPrice = listPage.getReturnFlightPrice();
		Integer total = onwardPrice + returnPrice;
		Integer totalPriceOnPage = listPage.getTotalPriceOnPage();
		System.out.println("Onward Flight price is: " + onwardPrice);
		System.out.println("Return Flight price is: " + returnPrice);
		System.out.println("Total price is: " + total);
		System.out.println("Total price on page is: " + totalPriceOnPage);
		Assert.assertEquals(totalPriceOnPage, total);
	}

	private List<Integer> findPricesLessThan(List<Integer> prices, Integer maxPrice) {
		List<Integer> result = new ArrayList<>();
		for (Integer price : prices) {
			if(price < maxPrice) {
				result.add(price);
			}
		}
		return result;
	}

	private void waitForFlightsListToLoad(int millis) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		doWait(millis);
		js.executeScript("window.scrollBy(0,2000)", "");
		doWait(millis);
		js.executeScript("window.scrollBy(0,2000)", "");
	}

	private static void doWait(Integer millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Integer getPropNumber(String property) {
		return Integer.valueOf(prop.getProperty(property));
	}
}
