package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Url;



public class InstructorHomePage extends AppPage {
    
    @FindBy(id = "searchBox")
    private WebElement searchBox;
    
    @FindBy(id = "buttonSearch")
    private WebElement searchButton;
    
    @FindBy(id = "sortById")
    private WebElement sortByIdButton;

    @FindBy(id = "sortByName")
    private WebElement sortByNameButton;
    
    @FindBy(id = "sortByDate")
    private WebElement sortByDateButton;
    
    public InstructorHomePage(Browser browser){
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }
    
    public static boolean containsExpectedPageContents(String pageSource){
        return pageSource.contains("<h1>Instructor Home</h1>");
    }

    public InstructorHelpPage clickHelpLink() {
        instructorHelpTab.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHelpPage.class);
    }
    
    public void clickSortByIdButton() {
        sortByIdButton.click();
        waitForPageToLoad();
    }
    
    public void clickSortByNameButton() {
        sortByNameButton.click();
        waitForPageToLoad();
    }
    
    public void clickSortByDateButton() {
        sortByDateButton.click();
        waitForPageToLoad();
    }
    
    public InstructorCourseEnrollPage clickCourseErollLink(String courseId) {
        getCourseLinkInRow("course-enroll-for-test", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
    }
    
    public InstructorCourseDetailsPage clickCourseViewLink(String courseId) {
        getCourseLinkInRow("course-view-for-test", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseDetailsPage.class);
    }
    
    public InstructorCourseEditPage clickCourseEditLink(String courseId) {
        getCourseLinkInRow("course-edit-for-test", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEditPage.class);
    }
    
    //TODO: rename course-add-eval-for-test
    public InstructorFeedbacksPage clickCourseAddEvaluationLink(String courseId) {
        getCourseLinkInRow("course-add-eval-for-test", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    /**
     * This is for standard evaluation 
     */
    public InstructorEvalResultsPage clickSessionViewResultsLink(String courseId, String evalName) {
        getViewResultsLink(courseId, evalName).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalResultsPage.class);
    }
    
    /**
     * This is for standard evaluation 
     */
    public InstructorEvalEditPage clickSessionEditLink(String courseId, String evalName) {
        getEditLink(courseId, evalName).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalEditPage.class);
    }
    
    /**
     * This is for standard evaluation 
     */
    public InstructorEvalPreview clickSessionPreviewLink(String courseId, String evalName) {
        getPreviewLink(courseId, evalName).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorEvalPreview.class);
    }
    
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbackResultsPage clickFeedbackSessionViewResultsLink(String courseId, String fsName) {
        getViewResultsLink(courseId, fsName).click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbackResultsPage.class);
    }
    
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbackEditPage clickFeedbackSessionEditLink(String courseId, String fsName) {
        getEditLink(courseId, fsName).click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbackEditPage.class);
    }
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbacksPage clickFeedbackSessionDeleteLink(String courseId, String fsName) {
        clickAndConfirm(getDeleteEvalLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    /**
     * This is for customized feedback session 
     */
    public FeedbackSubmitPage clickFeedbackSessionSubmitLink(String courseId, String fsName) {
        this.getSubmitLink(courseId, fsName).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbacksPage clickFeedbackSessionRemindLink(String courseId, String fsName){
        clickAndConfirm(getRemindLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbacksPage clickFeedbackSessionUnpublishLink(String courseId, String fsName){
        clickAndConfirm(getUnpublishLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    /**
     * This is for customized feedback session 
     */
    public InstructorFeedbacksPage clickFeedbackSessionPublishLink(String courseId, String fsName){
        clickAndConfirm(getPublishLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    
    
    public void clickHomeTab() {
        instructorHomeTab.click();
        waitForPageToLoad();
    }
    
    public InstructorStudentListPage searchForStudent(String studentName) {
        searchBox.clear();
        searchBox.sendKeys(studentName);
        searchButton.click();
        waitForPageToLoad();
        return changePageType(InstructorStudentListPage.class);
    }
    
    public WebElement getViewResponseLink(String courseId, String evalName) {
        int evaluationRowId = getEvaluationRowId(courseId, evalName);
        String xpathExp = "//tr[@id='session"+ evaluationRowId +"']/td[contains(@class,'session-response-for-test')]/a";

        return browser.driver.findElement(By.xpath(xpathExp));
    }
    
    public void setViewResponseLinkValue(WebElement element, String newValue) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver; 
        js.executeScript("arguments[0].href=arguments[1]", element, newValue );
    }

    public WebElement getViewResultsLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-view-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getEditLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-edit-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getSubmitLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-submit-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getPreviewLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-preview-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getRemindLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getPublishLink(String courseId, String evalName){
        return getSessionLinkInRow("session-publish-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getUnpublishLink(String courseId, String evalName){
        return getSessionLinkInRow("session-unpublish-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getDeleteEvalLink(String courseId, String evalName){
        return getSessionLinkInRow("session-delete-for-test", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getDeleteCourseLink(String courseId){
        return getCourseLinkInRow("course-delete-for-test", getCourseRowId(courseId));
    }
    
    public InstructorHomePage clickArchiveCourseLink(String courseId){
        getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return this;
    }
    
    public Url getArchiveCourseLink(String courseId){
        String url = getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)).getAttribute("href");
        return new Url(url);
    }
    
    private WebElement getSessionLinkInRow(String elementClassNamePrefix, int rowId){
        return browser.driver.findElement(By.id("session" + rowId)).findElement(By.className(elementClassNamePrefix));
    }
    
    private WebElement getCourseLinkInRow(String elementClassNamePrefix, int rowId){
        return browser.driver.findElement(By.id("course" + rowId)).findElement(By.className(elementClassNamePrefix));
    }

    private int getEvaluationRowId(String courseId, String evalName) {
        int courseRowID = getCourseRowId(courseId);
        if (courseRowID == -1)
            return -2;
        String template = "//div[@id='course%d']//tr[@id='session%d']";
        int max = (Integer) (browser.selenium)
                .getXpathCount("//div[starts-with(@id, 'course')]//tr");
        for (int id = 0; id < max; id++) {
            if (getElementText(
                    By.xpath(String.format(template + "//td[1]", courseRowID,
                            id))).equals(evalName)) {
                return id;
            }
        }
        return -1;
    }
    
    private int getCourseRowId(String courseId) {
        int id = 0;
        while (isElementPresent(By.id("course" + id))) {
            if (getElementText(
                    By.xpath("//div[@id='course" + id
                            + "']//strong"))
                    .startsWith("[" + courseId + "]")) {
                return id;
            }
            id++;
        }
        return -1;
    }
    
    private String getElementText(By locator) {
        if (!isElementPresent(locator))
            return "";
        return browser.driver.findElement(locator).getText();
    }
    

}
