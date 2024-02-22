package com.performance.demo.pages.android;

import com.performance.demo.pages.common.ChartsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = ChartsPageBase.class)
public class ChartsPage extends ChartsPageBase implements IMobileUtils {

    @FindBy(className = "android.widget.ImageButton")
    private ExtendedWebElement leftMenuButton;

    @FindBy(id = "image_view")
    private ExtendedWebElement imageView;

    @FindBy(id = "image_view")
    private ExtendedWebElement content_frame;

    @FindBy(id = "fragmentHiloChart")
    private ExtendedWebElement hiloChart;

    @FindBy(xpath = "//*[@resource-id='com.solvd.carinademoapplication:id/fragmentHiloChart']/descendant::*[@content-desc='AnyChart Trial Version']")
    private ExtendedWebElement hiloChartAnyChartText;

    public ChartsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void swipeToAcChartText(){
        swipe(hiloChartAnyChartText, 1);
    }
}