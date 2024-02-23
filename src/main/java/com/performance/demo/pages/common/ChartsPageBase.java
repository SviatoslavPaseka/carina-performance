package com.performance.demo.pages.common;

import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

public abstract class ChartsPageBase extends AbstractPage {

    public ChartsPageBase(WebDriver driver) {
        super(driver);
    }

    public abstract void swipeToAcChartText();

    // Here we check if Fruit Chart is visible
    public abstract boolean isFruitChartVisible();

    // Here we check if Fruit Venn Diagram is visible
    public abstract boolean isFruitVennDiagramVisible();

    public abstract boolean isFragmentHiloChartVisible();
}
