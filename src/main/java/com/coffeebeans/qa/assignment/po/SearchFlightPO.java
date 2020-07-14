package com.coffeebeans.qa.assignment.po;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * @author Spoorthi M.
 * 
 * Page Object for ClearTrip home page
 */
public class SearchFlightPO {

	// date format for entering date string in from and to fields
	DateFormat df = new SimpleDateFormat("EEE, dd MMM, YYYY");

	/**
	 * Parameterized constructor to use PagePfactory methods
	 * @param driver
	 */
	public SearchFlightPO(WebDriver driver) {
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
	}

	// web elements declarations
	
	@FindBy(linkText = "Flights")
	WebElement flightsLink;

	@FindBy(id = "RoundTrip")
	WebElement roundTripRadio;

	@FindBy(id = "FromTag")
	WebElement fromText;

	@FindBy(id = "ToTag")
	WebElement toText;

	@FindBy(id = "DepartDate")
	WebElement dapartDate;

	@FindBy(id = "ReturnDate")
	WebElement returnDate;

	@FindBy(id = "SearchBtn")
	WebElement searchBtn;

	@FindBy(xpath = "//ul[@id='ui-id-1']/descendant::li[@class='list']")
	List<WebElement> fromSuggestions;

	@FindBy(xpath = "//ul[@id='ui-id-2']/descendant::li[@class='list']")
	List<WebElement> toSuggestions;

	// public methods to be invoked from test class
	
	public void clickOnFlights() {
		flightsLink.click();
	}

	public void selectRoundTrip() {
		roundTripRadio.click();
	}

	public void setFrom(String text) {
		fromText.sendKeys(text);
		for (WebElement webElement : fromSuggestions) {
			if(webElement.getText().contains(text)) {
				System.out.println("From:" + webElement.getText());
				webElement.click();
				fromText.sendKeys(Keys.ESCAPE);
			}
		}
	}

	public void setTo(String text) {
		toText.sendKeys(text);
		for (WebElement webElement : toSuggestions) {
			System.out.println("To:" + webElement.getText());
			if(webElement.getText().contains(text)) {
				webElement.click();
				toText.sendKeys(Keys.ESCAPE);
			}
		}
	}

	public void setDepartDate(Date dpDate) {
		dapartDate.sendKeys(df.format(dpDate));
		dapartDate.sendKeys(Keys.ESCAPE);
	}

	public void setReturnDate(Date rtDate) {
		returnDate.sendKeys(Keys.BACK_SPACE);
		returnDate.sendKeys(df.format(rtDate));
		returnDate.sendKeys(Keys.ESCAPE);
	}

	public void clickOnSearchBtn() {
		searchBtn.click();
	}
}
