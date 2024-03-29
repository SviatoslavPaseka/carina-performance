/*
 * Copyright 2013-2021 QAPROSOFT (http://qaprosoft.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.performance.demo.carina;

import com.performance.demo.IPerformanceTest;
import com.performance.demo.annotations.PerformanceTest;
import com.performance.demo.pages.common.*;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.carina.core.IAbstractTest;
import com.zebrunner.carina.core.registrar.ownership.MethodOwner;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MobileSampleTest implements IAbstractTest, IMobileUtils, IPerformanceTest {

    private static final String user = "Test_user";

    @Test()
    @MethodOwner(owner = "jovchinnikova")
    @TestLabel(name = "feature", value = {"mobile", "regression"})
    @PerformanceTest(flowName = "login_flow", userName = user, collectLoginTime = true, collectExecutionTime = false)
    public void testLoginUser() {
        String password = RandomStringUtils.randomAlphabetic(10);
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        Assert.assertTrue(welcomePage.isPageOpened(), "Welcome page isn't opened");
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        Assert.assertFalse(loginPage.isLoginBtnActive(), "Login button is active when it should be disabled");
        loginPage.typeName(user);
        loginPage.typePassword(password);
        loginPage.selectMaleSex();
        loginPage.checkPrivacyPolicyCheckbox();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.clickLoginBtn();
        Assert.assertTrue(carinaDescriptionPage.isPageOpened(), "Carina description page isn't opened");
    }

    @Test()
    @MethodOwner(owner = "jovchinnikova")
    @TestLabel(name = "feature", value = {"mobile", "acceptance"})
    @PerformanceTest(flowName = "ui_elements_flow", userName = user, collectLoginTime = true, collectExecutionTime = true)
    public void testUIElements() {
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
        UIElementsPageBase uiElements = carinaDescriptionPage.navigateToUIElementsPage();
        final String text = "some text";
        final String date = "22/10/2018";
        final String email = "some@email.com";
        uiElements.typeText(text);
        Assert.assertEquals(uiElements.getText(), text, "Text was not typed");
        uiElements.typeDate(date);
        Assert.assertEquals(uiElements.getDate(), date, "Date was not typed");
        uiElements.typeEmail(email);
        Assert.assertEquals(uiElements.getEmail(), email, "Email was not typed");
        uiElements.swipeToFemaleRadioButton();
        uiElements.checkCopy();
        Assert.assertTrue(uiElements.isCopyChecked(), "Copy checkbox was not checked");
        uiElements.clickOnFemaleRadioButton();
        Assert.assertTrue(uiElements.isFemaleRadioButtonSelected(), "Female radio button was not selected!");
        /*uiElements.clickOnOtherRadioButton();
        Assert.assertTrue(uiElements.isOthersRadioButtonSelected(), "Others radio button was not selected!");*/
    }

    @Test()
    @MethodOwner(owner = "spaseka")
    @TestLabel(name = "feature", value = {"mobile", "acceptance"})
    @PerformanceTest(flowName = "swiping_test_flow", userName = user, collectLoginTime = true, collectExecutionTime = true)
    public void testSwiping() {
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
        ChartsPageBase chartsPage = carinaDescriptionPage.navigateToChartsPage();
        chartsPage.swipeToAcChartText();
        pause(3);
    }

    @Test
    @MethodOwner(owner = "iDoctor")
    @TestLabel(name = "feature",value = {"mobile","regression"})
    @PerformanceTest(flowName = "ui_charts_presence",userName = user,collectLoginTime = false,collectExecutionTime = false)
    public void testChartElements() {
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
        ChartsPageBase chartsPageBase = carinaDescriptionPage.navigateToChartsPage();
//        Assert.assertTrue(chartsPageBase.isPageOpened(), "Charts page isn't opened");
        Assert.assertTrue(chartsPageBase.isFruitChartVisible(),"Fruit Chart is not visible or present");
        Assert.assertTrue(chartsPageBase.isFruitVennDiagramVisible(),"Fruit Venn Diagram is not visible or present");
//        Assert.assertTrue(chartsPageBase.isFragmentHiloChartVisible(),"FragmentHiloChart is not visible or present.");
    }


    @Test
    @MethodOwner(owner = "iDoctor")
    @TestLabel(name = "feature", value = {"mobile","regression"})
    @PerformanceTest(flowName = "",userName = user,collectLoginTime = false,collectExecutionTime = false)
    public void testMapElements() {
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
        MapsPageBase mapsPageBase = carinaDescriptionPage.navigateToMapPage();
        Assert.assertTrue(mapsPageBase.isImageSliderPresent(),"Image slider content is not visible");
        Assert.assertTrue(mapsPageBase.isMapPresent(),"Map content is not present");
        Assert.assertTrue(mapsPageBase.isZoomInBtnPresent(),"Zoom in button is not present");
        Assert.assertTrue(mapsPageBase.isZoomInBtnClickable(),"Zoom in button is not clickable");
        mapsPageBase.clickZoomInBtn();
        Assert.assertTrue(mapsPageBase.isZoomOutBtnPresent(),"Zoom out button is not present");
        Assert.assertTrue(mapsPageBase.isZoomOutBtnClickable(),"Zoom out button is not clickable");
        mapsPageBase.clickZoomOutBtn();


    }
    /***
     *
     *  When Running tests on iOS it kind of passes log in process, but then we encounter error such as
     * WARNING: Cannot invoke "com.performance.demo.performance.PerformanceCollector.getLoadTimeStopwatch()" because "com.performance.demo.performance.PerformanceListener.performanceCollector" is null
     * Additionally after manually logging in the app, the main page is empty, it is not loaded
     *
     */
    @Test()
    @MethodOwner(owner = "spaseka")
    @TestLabel(name = "feature", value = {"mobile", "acceptance"})
    @PerformanceTest(flowName = "taping_test_flow", userName = user, collectLoginTime = true, collectExecutionTime = true)
    public void testTaping() {
        WelcomePageBase welcomePage = initPage(getDriver(), WelcomePageBase.class);
        LoginPageBase loginPage = welcomePage.clickNextBtn();
        CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
        carinaDescriptionPage.longTapOnErrorDescription();
        pause(3);
    }
}