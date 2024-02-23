package com.performance.demo.pages.android;

import com.performance.demo.pages.common.MapsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = MapsPageBase.class)
public class MapsPage extends MapsPageBase {

    @FindBy(id = "image_slider")
    private ExtendedWebElement imageSlider;

    @FindBy(id = "map")
    private ExtendedWebElement mapContent;

    @FindBy(xpath = "//android.widget.ImageView[@content-desc=\"Zoom in\"]")
    private ExtendedWebElement zoomInBtn;

    @FindBy(xpath = "//android.widget.ImageView[@content-desc=\"Zoom out\"]")
    private ExtendedWebElement zoomOutBtn;

    public MapsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isImageSliderPresent() {
        return imageSlider.isElementPresent() && imageSlider.isVisible();
    }

    @Override
    public boolean isMapPresent() {
        return mapContent.isElementPresent() && mapContent.isVisible();
    }

    @Override
    public boolean isZoomInBtnPresent() {
        return zoomInBtn.isElementPresent() && zoomInBtn.isVisible();
    }

    @Override
    public boolean isZoomOutBtnPresent() {
        return zoomOutBtn.isElementPresent() && zoomOutBtn.isVisible();
    }

    @Override
    public void clickZoomInBtn() {
        zoomInBtn.click();
    }

    @Override
    public void clickZoomOutBtn() {
        zoomOutBtn.click();
    }

    @Override
    public boolean isZoomInBtnClickable() {
        return zoomInBtn.isClickable();
    }

    @Override
    public boolean isZoomOutBtnClickable() {
        return zoomOutBtn.isClickable();
    }

}
