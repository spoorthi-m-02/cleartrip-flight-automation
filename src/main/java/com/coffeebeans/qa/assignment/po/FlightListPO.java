package com.coffeebeans.qa.assignment.po;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * @author Spoorthi M.
 * 
 * Page Object for Flight search results in CLeartrip website
 */
public class FlightListPO {
	Actions actions;

	/**
	 * Parameterized Constructor
	 * @param driver
	 */
	public FlightListPO(WebDriver driver) {
		// create an actions element to perform keyboard and mouse actions
		this.actions =  new Actions(driver);
		// init the web elements in this page defined with annotations with ajax response timeout of 10seconds.
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
	}

	// web elements in the page
	/**
	 * INPORTANT
	 * Page layout changes (basically switches between two layouts)
	 * All the elements below are designed work with both layouts
	 * The first find by annotation corresponds to element in the first view
	 */

	@FindAll(value = {
			@FindBy(xpath = "//p[contains(text(), 'Non-stop')]"), 
			@FindBy(xpath = "//*[contains(text(), '0 stop')]")
	})
	List<WebElement> nonStopFilter;

	// This element xpath is same in both the layouts
	@FindBy(xpath = "//*[contains(text(), '1 stop')]")
	WebElement oneStopFilter;

	@FindAll(value = {
			@FindBy(xpath = "//div[@data-test-attrib=\"onward-view\"]/div/div"), 
			@FindBy(xpath = "//div[@data-fromto][1]//ul[@class='listView flights']/li")
	})
	List<WebElement> onwardFlighList;

	@FindAll(value = {
			@FindBy(xpath = "//div[@data-test-attrib=\"return-view\"]/div/div"), 
			@FindBy(xpath = "//div[@data-fromto][2]//ul[@class='listView flights']/li")
	})
	List<WebElement> returnFlighList;

	@FindAll(value = {
			@FindBy(xpath = "//div[@data-test-attrib='onward-view']/div/div//div[contains(@class, 'rt-tuple-container--selected')]"), 
			@FindBy(xpath = "//div[@data-fromto][1]//ul[@class='listView flights']//div[contains(@class,'selected')]")
	})
	List<WebElement> selectedOnwardFlight;

	@FindAll(value = {
			@FindBy(xpath = "//div[@data-test-attrib='return-view']/div/div//div[contains(@class, 'rt-tuple-container--selected')]"), 
			@FindBy(xpath = "//div[@data-fromto][2]//ul[@class='listView flights']//div[contains(@class,'selected')]")
	})
	List<WebElement> selectedReturnFlight;

	@FindAll(value = {
			@FindBy(xpath = "//*[@class='c-neutral-900 mx-4  fw-700 flex flex-right fs-7']"), 
			@FindBy(xpath = "//h2[@class='totalAmount']")
	})
	List<WebElement> totalPriceField;

	// public methods to be invoked from Test class

	public List<Integer> listOnwardFlightPrice() {
		// Extracts the integer value of price from all the onward flights 
		return extractPriceFromFlightList(onwardFlighList);
	}

	public List<Integer> listReturnFlightPrice() {
		// Extracts the integer value of price from all the return flights
		return extractPriceFromFlightList(returnFlighList);
	}

	public void applyNonStopFilter() {
		// apply the "0 stop" filter 
		nonStopFilter.get(0).click();
	}

	public void applyOneStopFilter() {
		// apply the "1 stop" filter 
		oneStopFilter.click();
	}

	public boolean selectOnwardFlightByIndex(int index) {
		// select from the list of onward flights by index
		// basically click on the element in the specified index
		return selectFlight(onwardFlighList, index);
	}

	public boolean selectReturnFlightByIndex(int index) {
		// select from the list of return flights by index
		// basically click on the element in the specified index
		return selectFlight(returnFlighList, index);
	}

	public Integer getOnwardFlightPrice() {
		// get the integer value of selected onward flight
		// note that this is very similar operation as that of extracting price from list
		// except that the xpath varies a little - one less div tag in the Xpath
		return extractPriceFrmWebElement(selectedOnwardFlight.get(0), true);
	}

	public Integer getReturnFlightPrice() {
		// get the integer value of selected return flight
		// note that this is very similar operation as that of extracting price from list
		// except that the xpath varies a little - one less div tag in the Xpath
		return extractPriceFrmWebElement(selectedReturnFlight.get(0), true);
	}

	public Integer getTotalPriceOnPage() {
		// get the integer value of total price shown on the screen
		return extractPriceFromString(totalPriceField.get(0).getText());
	}

	// private methods used in the class


	private Integer extractPriceFromString(String str) {
		Integer total = 0;
		try {
			// get rid of non-number characters like comma(,) and rupee symbol
			return Integer.valueOf(str.replaceAll("[^0-9]", ""));
		} catch (Exception e) {}
		return total;
	}

	private boolean selectFlight(List<WebElement> flights, int index) {
		// make sure the element exists in the specified index
		if(flights.size() > index) {
			WebElement flight = flights.get(index);
			actions.moveToElement(flight).click().perform();
			return true;
		}
		return false;
	}

	private List<Integer> extractPriceFromFlightList(List<WebElement> flighList) {
		// extract the price from each element and return the integer list
		// this is called from both onward and return flights
		List<Integer> result = new ArrayList<>();
		for (WebElement webElement : flighList) {
			// extract the price from web element with relative xpath
			result.add(extractPriceFrmWebElement(webElement, false));
		}
		return result;
	}

	private Integer extractPriceFrmWebElement(WebElement webElement, boolean selectedPrice) {
		// this method is called to get integer value of price from both list of elements and selected element
		// the xpath varies slightly for selected element and list of elements which is differentiated by boolean flag
		String price;
		try {
			if(selectedPrice) {
				price = webElement.findElement(By.xpath("div/div[3]/div[2]")).getText();
			} else {
				price = webElement.findElement(By.xpath("div/div/div[3]/div[2]")).getText();
			}
			return extractPriceFromString(price);
		} catch (Exception e) {}
		
		
		// the below section works for the other UI layout 
		try {
			if(selectedPrice) {
				price = webElement.getAttribute("data-price");
			} else {
				price = webElement.findElement(By.xpath("div")).getAttribute("data-price");
			}
			return extractPriceFromString(price);
		} catch (Exception e) {}
		return null;
	}
}
