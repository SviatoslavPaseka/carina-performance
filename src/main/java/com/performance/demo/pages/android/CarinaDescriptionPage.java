package com.performance.demo.pages.android;

import com.performance.demo.pages.common.*;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.utils.factory.DeviceType.Type;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@DeviceType(pageType = Type.ANDROID_PHONE, parentClass = CarinaDescriptionPageBase.class)
public class CarinaDescriptionPage extends CarinaDescriptionPageBase implements IMobileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(id = "content_frame")
    private ExtendedWebElement webViewContent;

    @FindBy(xpath = "//android.view.View[@text = 'CARINA facts']")
    private ExtendedWebElement carinaFactsSubTitle;

    @FindBy(xpath = "//android.widget.CheckedTextView[@text = 'Web View']")
    private ExtendedWebElement webViewLink;

    @FindBy(xpath = "//android.widget.CheckedTextView[@text = 'Charts']")
    private ExtendedWebElement chartsLink;

    @FindBy(xpath = "//android.widget.CheckedTextView[@text = 'Map']")
    private ExtendedWebElement mapLink;

    @FindBy(xpath = "//android.widget.CheckedTextView[@text = 'UI elements']")
    private ExtendedWebElement uiElementsLink;

    @FindBy(className = "android.widget.ImageButton")
    private ExtendedWebElement leftMenuButton;

    @FindBy(xpath = "(//android.widget.LinearLayout[@resource-id='com.solvd.carinademoapplication:id/lin']/descendant::android.view.View)[2]")
    private ExtendedWebElement errorDescription;

    public CarinaDescriptionPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public WebViewPageBase navigateToWebViewPage() {
        leftMenuButton.click();
        webViewLink.click();
        return initPage(getDriver(), WebViewPageBase.class);
    }

    @Override
    public ChartsPageBase navigateToChartsPage() {
        leftMenuButton.click();
        chartsLink.click();
        return initPage(getDriver(), ChartsPageBase.class);
    }

    @Override
    public MapsPageBase navigateToMapPage() {
        leftMenuButton.click();
        mapLink.click();
        return initPage(getDriver(), MapsPageBase.class);
    }

    @Override
    public UIElementsPageBase navigateToUIElementsPage() {
        leftMenuButton.click();
        uiElementsLink.click();
        return initPage(getDriver(), UIElementsPageBase.class);
    }

    @Override
    public boolean isPageOpened() {
        return webViewContent.isElementPresent();
    }


    @Override
    public void longTapOnErrorDescription(){
        longTap(errorDescription);
    }
}
