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
	WebDriver driver;
	Actions actions;

	/**
	 * Parameterized Constructor
	 * @param driver
	 */
	public FlightListPO(WebDriver driver) {
		this.driver = driver;
		this.actions =  new Actions(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
	}

	// web elements in the page
	/**
	 * INPORTANT
	 * Page layout changes (basically switches between two layouts)
	 * All the elements below are designed work with both layouts
	 */

	@FindAll(value = {
			@FindBy(xpath = "//p[contains(text(), 'Non-stop')]"), 
			@FindBy(xpath = "//*[contains(text(), '0 stop')]")
	})
	List<WebElement> nonStopFilter;

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
		return extractPriceFromFlightList(onwardFlighList);
	}

	public List<Integer> listReturnFlightPrice() {
		return extractPriceFromFlightList(returnFlighList);
	}

	public void applyNonStopFilter() {
		nonStopFilter.get(0).click();
	}

	public void applyOneStopFilter() {
		oneStopFilter.click();
	}

	public boolean selectOnwardFlightByIndex(int index) {
		return selectFlight(onwardFlighList, index);
	}

	public boolean selectReturnFlightByIndex(int index) {
		return selectFlight(returnFlighList, index);
	}

	public Integer getOnwardFlightPrice() {
		return extractPriceFrmWebElement(selectedOnwardFlight.get(0), true);
	}

	public Integer getReturnFlightPrice() {
		return extractPriceFrmWebElement(selectedReturnFlight.get(0), true);
	}

	public Integer getTotalPriceOnPage() {
		return extractPriceFromString(totalPriceField.get(0).getText());
	}

	private Integer extractPriceFromString(String str) {
		Integer total = 0;
		try {
			return Integer.valueOf(str.replaceAll("[^0-9]", ""));
		} catch (Exception e) {}
		return total;
	}

	// private methods used in the class
	
	private boolean selectFlight(List<WebElement> flights, int index) {
		if(flights.size() > index) {
			WebElement flight = flights.get(index);
			actions.moveToElement(flight).click().perform();
			return true;
		}
		return false;
	}

	private List<Integer> extractPriceFromFlightList(List<WebElement> flighList) {
		List<Integer> result = new ArrayList<>();
		for (WebElement webElement : flighList) {
			result.add(extractPriceFrmWebElement(webElement, false));
		}
		return result;
	}

	private Integer extractPriceFrmWebElement(WebElement webElement, boolean selectedPrice) {
		String price;
		try {
			if(selectedPrice) {
				price = webElement.findElement(By.xpath("div/div[3]/div[2]")).getText();
			} else {
				price = webElement.findElement(By.xpath("div/div/div[3]/div[2]")).getText();
			}
			return extractPriceFromString(price);
		} catch (Exception e) {}
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
