package com.example.uitests.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MoreFiltersModal extends BasePage {
  private final By increaseBedroomsButtonPath = By.cssSelector("button[aria-label='increase value']");
  private final By poolAmenitiesButtonPath = By.xpath("//span[text()='Pool']//ancestor::button");
  private final By applyFiltersButtonPath = By.xpath("//button[text()='Clear all']//following-sibling::div/a");

  public MoreFiltersModal(WebDriver driver) {
    super(driver);
  }

  public void increaseBedroomsByNumber(int count) {
    for (int i = 0; i < count; i++) {
      clickElementByLocator(increaseBedroomsButtonPath);
    }
  }

  public void checkPoolAmenitiesButton() {
    clickElementByLocator(poolAmenitiesButtonPath);
  }
  public void applyFilters(){
    clickElementByLocator(applyFiltersButtonPath);
  }
}
