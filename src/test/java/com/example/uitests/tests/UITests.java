package com.example.uitests.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UITests {
  private WebDriver driver;
  private FluentWait<WebDriver> wait;
  private final By searchLocation = By.cssSelector("form label input");
  private final String locationForAirbnb = "Rome, Italy";
  private final LocalDate currentDate = LocalDate.now();
  private final LocalDate dateForNextWeek = currentDate.plusWeeks(1);

  @BeforeEach
  void setUp() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.get("https://airbnb.com");

    wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(15))
        .pollingEvery(Duration.ofMillis(100))
        .ignoring(NoSuchElementException.class);
  }

  @Test
  void searchTest() {
    basicSearch();

    DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM d");
    By checkInDateText = By.xpath("//div[text()='Check in']/following-sibling::div");
    By checkOutDateText = By.xpath("//div[text()='Check out']/following-sibling::div");
    By numberOfGuestsText = By.xpath("//div[text()='Who']/following-sibling::div");

    By searchLocationButton = By.xpath("//div[@aria-labelledby='littleSearchLabel']//button");
    wait.until(ExpectedConditions.visibilityOfElementLocated(searchLocationButton));
    wait.until(ExpectedConditions.elementToBeClickable(searchLocationButton));
    driver.findElement(searchLocationButton).click();

    wait.until(ExpectedConditions.visibilityOfElementLocated(checkInDateText));
    Assert.assertEquals(driver.findElement(searchLocation).getAttribute("value"), locationForAirbnb);
    Assert.assertEquals(driver.findElement(checkInDateText).getText(), currentDate.format(monthFormatter));
    Assert.assertEquals(driver.findElement(checkOutDateText).getText(), dateForNextWeek.format(monthFormatter));
    Assert.assertEquals(driver.findElement(numberOfGuestsText).getText().split(" ")[0], "3");

    By listingsNumberOfBedrooms = By.xpath("//span[contains(.,'bedrooms')]");

    for (WebElement el : driver.findElements(listingsNumberOfBedrooms)) {
      /* I am assuming for 2 adults and 1 child, the amount of bedrooms should be less than 4.
       Only the spans containing the word bedrooms are checked, because if it contains bedroom then it's one bedroom
       which is suitable for the people. 4 Bedrooms or more is too much and is not accepted
       Didn't check for beds because I saw that sometimes even apartments with 4 beds or more are displayed
      */
      if (el.getText().contains("bedrooms")) {
        Assert.assertTrue(Integer.parseInt(el.getText().split(" ")[0]) < 4,
            "Expected bedrooms number to be smaller than 4 but found " + el.getText().split(" ")[0]);
      }
    }
  }

  @Test
  void moreFiltersTest() throws InterruptedException {
    basicSearch();

    By moreFiltersButton = By.xpath("//button[@aria-label='Next categories page']/following::div//button");
    wait.until(ExpectedConditions.elementToBeClickable(moreFiltersButton));
    driver.findElement(moreFiltersButton).click();

    By increaseBedroomsButton = By.cssSelector("button[aria-label='increase value']");
    for (int i = 0; i < 5; i++) {
      wait.until(ExpectedConditions.elementToBeClickable(increaseBedroomsButton));
      driver.findElement(increaseBedroomsButton).click();
    }

    By poolAmnetiesButton = By.xpath("//span[text()='Pool']//ancestor::button");
    wait.until(ExpectedConditions.elementToBeClickable(poolAmnetiesButton));
    driver.findElement(poolAmnetiesButton).click();

    By clickApplyFiltersButton = By.xpath("//button[text()='Clear all']//following-sibling::div/a");
    wait.until(ExpectedConditions.elementToBeClickable(clickApplyFiltersButton));
    driver.findElement(clickApplyFiltersButton).click();

    By listingImage = By.cssSelector("img[aria-hidden='true']");
    wait.until(ExpectedConditions.visibilityOfElementLocated(listingImage));

    By listingsNumberOfBedrooms = By.xpath("//span[contains(.,'bedrooms')]");

    for (WebElement el : driver.findElements(listingsNumberOfBedrooms)) {
      if (el.getText().contains("bedrooms")) {
        Assert.assertTrue(Integer.parseInt(el.getText().split(" ")[0]) >= 5,
            "Expected bedrooms number to be higher than 5 but found only"
                + el.getText().split(" ")[0] + " bedrooms");
      }
    }

    By firstProperty = By.xpath("//img[@aria-hidden='true']//ancestor::a");
    wait.until(ExpectedConditions.visibilityOfElementLocated(firstProperty));
    driver.findElement(firstProperty).click();

    String originalWindow = driver.getWindowHandle();
    // switch to the new opened window
    for (String windowHandle : driver.getWindowHandles()) {
      if (!originalWindow.contentEquals(windowHandle)) {
        driver.switchTo().window(windowHandle);
        break;
      }
    }

    By closeModalButton = By.cssSelector("button[aria-label='Close']");
    By propertyHeader = By.cssSelector("h1");
    wait.until(ExpectedConditions.visibilityOfElementLocated(propertyHeader));

    try {
      WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3)); // Short wait for the popup
      shortWait.until(ExpectedConditions.visibilityOfElementLocated(closeModalButton));
      driver.findElement(closeModalButton).click();
    } catch (Exception ignored) {
    }

    By showAmenitiesButton = By.xpath("//button[contains(.,'amenities')]");
    wait.until(ExpectedConditions.visibilityOfElementLocated(showAmenitiesButton));
    driver.findElement(showAmenitiesButton).click();

    By poolAmenitiesText = By.xpath("//h2[text()='Parking and facilities']//following::div[contains(.,'pool') or contains(.,'Pool')]");
    WebElement poolElement = wait.until(ExpectedConditions.visibilityOfElementLocated(poolAmenitiesText));
    Assert.assertTrue(poolElement.isDisplayed(), "There is no pool in the amenities list for this property.");

  }

  @Test
  void hoverTest() {
    basicSearch();

    By firstProperty = By.xpath("//img[@aria-hidden='true']//ancestor::a");
    wait.until(ExpectedConditions.visibilityOfElementLocated(firstProperty));

    Actions actions = new Actions(driver);
    actions.moveToElement(driver.findElement(firstProperty)).perform();

    //small wait for the button to change background
    wait(500);

    WebElement firstPropertyPriceElement = driver.findElement(By.xpath("//span[contains(.,'night')]//following-sibling::span"));
    WebElement pinElement = driver.findElement(By.xpath("//span[contains(.,'selected')]//preceding-sibling::div"));
    JavascriptExecutor js = (JavascriptExecutor) driver;
    String backgroundColor = (String) js.executeScript(
        "return window.getComputedStyle(arguments[0]).getPropertyValue('background-color');", pinElement);

    Pattern pattern = Pattern.compile("(\\d+)\\s*lei");
    Matcher matcher = pattern.matcher(pinElement.findElement(By.cssSelector("span")).getText());

    matcher.find();

    Assert.assertEquals(backgroundColor, "rgb(34, 34, 34)", "Expected background color of button to be black");
    Assert.assertEquals(firstPropertyPriceElement.getText().strip(), matcher.group(0),
        "Price on first property is expected to be " + matcher.group(0) + " but found " + firstPropertyPriceElement.getText().strip());

    By pinButton = By.xpath("//span[contains(.,'selected')]//ancestor::button");
    wait.until(ExpectedConditions.visibilityOfElementLocated(pinButton));
    driver.findElement(pinButton).click();

    By cardElement = By.xpath("(//button[@aria-label='Close']//preceding::a)[last()]//following-sibling::*/div[2]");

    By cardElementTextPath = By.xpath("(//button[@aria-label='Close']//preceding::a)[last()]//following-sibling::*/div[2]/div");
    By firstPropertyTextPath = By.xpath("(//img[@aria-hidden='true']//ancestor::div[@role='group'])[1]/div/div[2]/div");
    List<WebElement> cardElements = driver.findElements(cardElementTextPath);
    List<WebElement> firstPropertyElements = driver.findElements(firstPropertyTextPath);
    Assert.assertEquals(cardElements.get(0).getText(), firstPropertyElements.get(0).getText(),
        "Expected property title displayed on the pin to be " + firstPropertyElements.get(0).getText() +
            " but found " + cardElements.get(0).getText());
    Assert.assertEquals(cardElements.get(1).getText(), firstPropertyElements.get(1).getText(),
        "Expected property description displayed on the pin to be " + firstPropertyElements.get(1).getText() +
            " but found " + cardElements.get(1).getText());

    //find the price per night on property card and pin
    Matcher firstPropertyPrice = pattern.matcher(cardElements.get(3).getText());
    firstPropertyPrice.find();
    Matcher cardPropertyPrice = pattern.matcher(firstPropertyElements.get(3).getText());
    cardPropertyPrice.find();

    Assert.assertEquals(firstPropertyPrice.group(0), cardPropertyPrice.group(0),
        "Expected property price displayed on the pin to be " + firstPropertyPrice.group(0) +
            " but found " + cardPropertyPrice.group(0));
  }

  void basicSearch() {

    wait.until(ExpectedConditions.visibilityOfElementLocated(searchLocation));
    driver.findElement(searchLocation).sendKeys(locationForAirbnb);

    //small wait for the results to load
    wait(1000);

    By suggestion = By.id("bigsearch-query-location-suggestion-0");
    wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion));
    wait.until(ExpectedConditions.elementToBeClickable(suggestion));
    driver.findElement(suggestion).click();


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
    By checkInDate = By.xpath(String.format("//table//div[text()='%s' and @data-is-day-blocked='false']",
        currentDate.format(formatter)));
    wait.until(ExpectedConditions.elementToBeClickable(checkInDate));
    driver.findElement(checkInDate).click();

    By checkOutDate = By.xpath(String.format("//table//div[text()='%s' and @data-is-day-blocked='false']",
        dateForNextWeek.format(formatter)));
    wait.until(ExpectedConditions.elementToBeClickable(checkOutDate));
    driver.findElement(checkOutDate).click();

    By addGuestsInput = By.xpath("(//form//div[@role='button'])[3]");
    driver.findElement(addGuestsInput).click();
    By increaseNumberOfAdultsButton = By.xpath("//button[@aria-describedby='searchFlow-title-label-adults' " +
        "and @aria-label='increase value']");
    wait.until(ExpectedConditions.elementToBeClickable(increaseNumberOfAdultsButton));
    driver.findElement(increaseNumberOfAdultsButton).click();
    wait.until(ExpectedConditions.elementToBeClickable(increaseNumberOfAdultsButton));
    driver.findElement(increaseNumberOfAdultsButton).click();

    By increaseNumberOfChildrenButton = By.xpath("//button[@aria-describedby='searchFlow-title-label-children' " +
        "and @aria-label='increase value']");
    wait.until(ExpectedConditions.elementToBeClickable(increaseNumberOfChildrenButton));
    driver.findElement(increaseNumberOfChildrenButton).click();

    By searchButton = By.xpath("//div[text()='Search']//ancestor::button");
    wait.until(ExpectedConditions.elementToBeClickable(searchButton));
    driver.findElement(searchButton).click();
  }

  void wait(int t) {
    try {
      Thread.sleep(t);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


  @AfterEach
  void tearDown() {
    driver.quit();
  }

}
