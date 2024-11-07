package com.example.uitests.pom;

import com.example.uitests.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
  public static final String LOCATION_FOR_AIRBNB = "Rome, Italy";
  private final By searchLocationPath = By.cssSelector("form label input");
  private final By suggestionPath = By.id("bigsearch-query-location-suggestion-0");
  private final By addGuestsInputPath = By.xpath("(//form//div[@role='button'])[3]");
  private final By increaseNumberOfAdultsButtonPath =
      By.xpath("//button[@aria-describedby='searchFlow-title-label-adults' and @aria-label='increase value']");
  private final By increaseNumberOfChildrenButtonPath = By.xpath("//button[@aria-describedby='searchFlow-title-label-children' " +
      "and @aria-label='increase value']");
  private final By searchButtonPath = By.xpath("//div[text()='Search']//ancestor::button");
  private final By moreFiltersButton = By.xpath("//button[@aria-label='Next categories page']/following::div//button");

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void basicSearch() {
    sendKeysToElementByLocator(searchLocationPath, LOCATION_FOR_AIRBNB);

    //small wait for the results to load
    wait(1000);

    clickElementByLocator(suggestionPath);

    By checkInDate = By.xpath(String.format("//table//div[text()='%s' and @data-is-day-blocked='false']",
        DateUtils.formatDateAsPattern(DateUtils.CURRENT_DATE, DateUtils.DAY_PATTERN)));
    clickElementByLocator(checkInDate);

    By checkOutDate = By.xpath(String.format("//table//div[text()='%s' and @data-is-day-blocked='false']",
        DateUtils.formatDateAsPattern(DateUtils.ONE_WEEK_FROM_NOW_DATE, DateUtils.DAY_PATTERN)));
    clickElementByLocator(checkOutDate);

    clickElementByLocator(addGuestsInputPath);
    clickElementByLocator(increaseNumberOfAdultsButtonPath);
    clickElementByLocator(increaseNumberOfAdultsButtonPath);

    clickElementByLocator(increaseNumberOfChildrenButtonPath);

    clickElementByLocator(searchButtonPath);
  }

  public void clickMoreFiltersButton(){
    clickElementByLocator(moreFiltersButton);
  }
}
