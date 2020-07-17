package com.coffeebeans.qa.assignment.tests;

import java.io.IOException;
import java.lang.reflect.Method;
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
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
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
	// hold the properties file values as java Object
	private Properties prop = new Properties();
	private WebDriver driver;
	private SearchFlightPO searchPage;
	private FlightListPO listPage;

	// class members for report generation
	ExtentSparkReporter sparkReporter;
	ExtentReports extentReport;
	ExtentTest extentTest;

	@BeforeClass
	@Parameters("browser")		// "browser" parameter is coming from "testng.xml"
	public void setupTest(String browser) {
		// load the properties to get the test values
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
			// This does not need any browser driver and also headless.
			driver = new HtmlUnitDriver(BrowserVersion.CHROME, true) {
				@Override
				protected WebClient newWebClient(BrowserVersion version) {
					// avoid any errors thrown when loading javascript
					WebClient webClient = super.newWebClient(version);
					webClient.getOptions().setThrowExceptionOnScriptError(false);
					return webClient;
				}
			};
			break;
		}
		// start with clear trip webpage
		driver.get(CLEAR_TRIP_URL);
		// load and initialize page objects
		searchPage = new SearchFlightPO(driver);
		listPage = new FlightListPO(driver);
		driver.manage().window().maximize();
		initExtentReports();
	}

	private void initExtentReports() {
		// initialize extent report
		sparkReporter = new ExtentSparkReporter("");
		sparkReporter.config().setDocumentTitle("Cleartrip Flight Automation Report");
		sparkReporter.config().setReportName("Automation Execution Report");
		sparkReporter.config().setTheme(Theme.DARK);
		extentReport = new ExtentReports();
		extentReport.attachReporter(sparkReporter);
		extentReport.setSystemInfo("Application Name", "Coffeebeans Assignment");
		extentReport.setSystemInfo("Platform", System.getProperty("os.name"));
		extentReport.setSystemInfo("Environment", "QA");
	}

	@AfterClass
	public void teardown() {
		// after all the tests, close the browser
		doWait(getPropNumber("tear-down-wait"));
		driver.quit();
		// generate the report file
		extentReport.flush();
	}

	@BeforeMethod
	public void beforeTests(Method method) {
		// initialize the test object for extent report
		String className = getClass().getSimpleName();
		extentTest = extentReport.createTest(className + " - " + method.getName());
		// wait for specified time before each test
		doWait(getPropNumber("test-wait-duration"));
	}

	@AfterMethod
	public void afterTests(ITestResult result) {
		// capture the test results onto extent report
		String methodName = result.getMethod().getMethodName();
		extentTest.createNode(methodName);
		switch (result.getStatus()) {
		case ITestResult.FAILURE:
			extentTest.log(Status.FAIL, MarkupHelper.createLabel(methodName + " Test case Failed.", ExtentColor.RED));
			extentTest.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " Test case Failed", ExtentColor.RED));
			extentTest.fail(methodName + " Test Failed");
			break;
		case ITestResult.SKIP:
			extentTest.log(Status.SKIP, MarkupHelper.createLabel(methodName + " Test case Skiped.", ExtentColor.ORANGE));
			extentTest.skip(methodName + " Test step skipped");
			break;
		case ITestResult.SUCCESS:
			extentTest.log(Status.PASS, MarkupHelper.createLabel(methodName + " Test case Passed.", ExtentColor.GREEN));
			extentTest.pass(methodName + " Test step passed");
			break;
		default:
			break;
		}
	}

	@Test(priority = 1)
	public void clickOnFlights() {
		searchPage.clickOnFlights();

		// get rid of notification
		//doWait(3000);
		//new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();
	}

	@Test(priority = 2)
	public void selectRoundTrip() {
		searchPage.selectRoundTrip();
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
		// calculate from date based on properties file value and pass it to field
		// get current calendar and add specified days
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, getPropNumber("start-trip-offset"));
		System.out.println("From date: " + cal.getTime());
		searchPage.setDepartDate(cal.getTime());
	}

	@Test(priority = 6)
	public void setReturnDate() {
		// calculate return date based on properties file value and pass it to field
		// get current calendar and add specified days
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, getPropNumber("start-trip-offset") + getPropNumber("trip-duration"));
		System.out.println("To date: " + cal.getTime());
		searchPage.setReturnDate(cal.getTime());
	}

	@Test(priority = 7)
	public void clickOnSearchBtn() {
		// if specified in properties file, navigate to corresponding URL with different layout
		String alternateURL = prop.getProperty("alternate-layout-url");
		if(null != alternateURL && !alternateURL.isEmpty()) {
			driver.navigate().to(alternateURL);
		} else {
			// else simply click on "Search flights" button
			searchPage.clickOnSearchBtn();
		}
		//doWait(3000);
		//new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();
	}

	@Test(priority = 8)
	public void listOnwardFlights() {
		// wait and scroll to load the flight list
		waitForFlightsListToLoad(5000);
		// get the price list for onward flights and list flights with less than Rs. 5000
		List<Integer> prices = listPage.listOnwardFlightPrice();
		List<Integer> result = findPricesLessThan(prices, 5000);
		System.out.println("\nOut of " + prices.size() + " departing flights prices: " + prices + "\n" + result.size() + " flights have price of less that Rs. 5000: " + result);
	}

	@Test(priority = 9)
	public void listReturnFlights() {
		// get the price list for return flights and list flights with less than Rs. 7000
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
		// get the price list for return flights and list flights with less than Rs. 7000 after applying filters
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
		// select 2nd onward flight
		listPage.selectOnwardFlightByIndex(1);
		doWait(2000);
		// select 5th return flight
		listPage.selectReturnFlightByIndex(4);
	}

	@Test(priority = 13)
	public void verifySum() {
		// get the selected flight prices and compare with total price displayed on page
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
		// filter out the entries which are greater than the passed parameter
		List<Integer> result = new ArrayList<>();
		for (Integer price : prices) {
			if(price < maxPrice) {
				result.add(price);
			}
		}
		return result;
	}

	private void waitForFlightsListToLoad(int millis) {
		// use javascript executor to scroll down and load the entire flight list
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
