package com.performance.demo.pages.common;

import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

public abstract class MapsPageBase extends AbstractPage {

    public MapsPageBase(WebDriver driver) {
        super(driver);
    }

    public abstract boolean isImageSliderPresent();

    public abstract boolean isMapPresent();

    public abstract boolean isZoomInBtnPresent();

    public abstract boolean isZoomOutBtnPresent();

    public abstract void clickZoomInBtn();

    public abstract void clickZoomOutBtn();

    public abstract boolean isZoomInBtnClickable();

    public abstract boolean isZoomOutBtnClickable();

}
