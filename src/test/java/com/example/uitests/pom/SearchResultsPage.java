package com.example.uitests.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchResultsPage extends BasePage {
  private final By checkInDateTextPath = By.xpath("//div[text()='Check in']/following-sibling::div");
  private final By checkOutDateTextPath = By.xpath("//div[text()='Check out']/following-sibling::div");
  private final By numberOfGuestsTextPath = By.xpath("//div[text()='Who']/following-sibling::div");
  private final By searchDestinationButtonPath = By.xpath("//div[@aria-labelledby='littleSearchLabel']//button");
  private final By searchDestinationTextPath = By.cssSelector("form label input");
  private final By listingsNumberOfBedroomsPath = By.xpath("//span[contains(.,'bedrooms')]");
  private final By listingImagePath = By.cssSelector("img[aria-hidden='true']");
  private final By firstPropertyPath = By.xpath("//img[@aria-hidden='true']//ancestor::a");
  private final By firstPropertyPriceText = By.xpath("//span[contains(.,'night')]//following-sibling::span");
  private final By pinPath = By.xpath("//span[contains(.,'selected')]//preceding-sibling::div");
  private final By pinButtonPath = By.xpath("//span[contains(.,'selected')]//ancestor::button");
  private final By pinCardElementTextPath = By.xpath("(//button[@aria-label='Close']//preceding::a)[last()]//following-sibling::*/div[2]/div");
  private final By firstPropertyTextPath = By.xpath("(//img[@aria-hidden='true']//ancestor::div[@role='group'])[1]/div/div[2]/div");

  public SearchResultsPage(WebDriver driver) {
    super(driver);
  }

  public void clickSearchDestinationButton() {
    clickElementByLocator(searchDestinationButtonPath);
    waitUntilVisibilityOfElementByLocator(checkInDateTextPath);
  }

  public String getDestinationText() {
    return waitUntilVisibilityOfElementByLocator(searchDestinationTextPath).getAttribute("value");
  }

  public String getCheckInDateText() {
    return waitUntilVisibilityOfElementByLocator(checkInDateTextPath).getText();
  }

  public String getCheckOutDateText() {
    return waitUntilVisibilityOfElementByLocator(checkOutDateTextPath).getText();
  }

  public String getNumberOfGuestsText() {
    return waitUntilVisibilityOfElementByLocator(numberOfGuestsTextPath).getText().split(" ")[0];
  }

  public void waitForListingsToLoad() {
    waitUntilVisibilityOfElementByLocator(listingImagePath);
  }

  public void clickFirstProperty() {
    clickElementByLocator(firstPropertyPath);
  }

  public void hoverOverFirstProperty() {
    waitUntilVisibilityOfElementByLocator(firstPropertyPath);
    Actions actions = new Actions(driver);
    actions.moveToElement(driver.findElement(firstPropertyPath)).perform();

    wait(500);
  }

  public String getFirstPropertyPrice() {
    return waitUntilVisibilityOfElementByLocator(firstPropertyPriceText).getText().strip();
  }

  public String getPinBackgroundColor() {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return (String) js.executeScript(
        "return window.getComputedStyle(arguments[0]).getPropertyValue('background-color');", waitUntilVisibilityOfElementByLocator(pinPath));
  }

  public String getPriceFromPin() {
    Pattern pattern = Pattern.compile("(\\d+)\\s*lei");
    Matcher matcher = pattern.matcher(driver.findElement(pinPath).findElement(By.cssSelector("span")).getText());
    matcher.find();
    return matcher.group(0);
  }
  public void clickPinButton(){
    clickElementByLocator(pinButtonPath);
  }
  public String getPinCardText(int count){
    waitUntilVisibilityOfElementByLocator(pinCardElementTextPath);
    return driver.findElements(pinCardElementTextPath).get(count).getText();
  }

  public String getFirstPropertyText(int count){
    waitUntilVisibilityOfElementByLocator(firstPropertyTextPath);
    return driver.findElements(firstPropertyTextPath).get(count).getText();
  }

  public List<Integer> getBedroomCounts() {
    List<Integer> bedroomCounts = new ArrayList<>();
    Pattern pattern = Pattern.compile("(\\d+)\\s*bedrooms");

    for (WebElement el : driver.findElements(listingsNumberOfBedroomsPath)) {
     /* I am assuming for 2 adults and 1 child, the amount of bedrooms should be less than 4.
       Only the spans containing the word bedrooms are checked, because if it contains bedroom then it's one bedroom
       which is suitable for the people. 4 Bedrooms or more is too much and is not accepted
       Didn't check for beds because I saw that sometimes even apartments with 4 beds or more are displayed
      */
      if (el.getText().contains("bedrooms")) {
        Matcher matcher = pattern.matcher(el.getText());
        if (matcher.find()) {
          bedroomCounts.add(Integer.parseInt(matcher.group(1)));
        }
      }
    }
    return bedroomCounts;
  }
}
