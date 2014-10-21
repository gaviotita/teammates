package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student records' view for instructors.
 */
public class InstructorStudentRecordsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorStudentRecordsPage viewPage;
    private static DataBundle testDataNormal, testDataQuestionType, testDataLinks;
    
    private static String instructorId;
    private static String courseId;
    private static String studentEmail;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testDataNormal = loadDataBundle("/InstructorStudentRecordsPageUiTest.json");
        testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        testDataLinks = loadDataBundle("/InstructorEvalSubmissionEditPageUiTest.json");
        
        browser = BrowserPool.getBrowser();
    }
    
    
    @Test
    public void testAll() throws Exception{
        testContent();
        testLinks();
        testScript();
        testAction();
    }


    private void testContent() {
        InstructorAttributes instructor;
        StudentAttributes student; 
        
        ______TS("content: typical case, normal student records with comments");
        
        removeAndRestoreTestDataOnServer(testDataNormal);
                
        instructor = testDataNormal.instructors.get("teammates.test.CS2104");
        student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        viewPage.verifyHtml("/instructorStudentRecords.html");
        
        ______TS("content: typical case, normal student records with comments, helper view");
        
        instructor = testDataNormal.instructors.get("teammates.test.CS2104.Helper");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
            
        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsWithHelperView.html");
        
        ______TS("content: normal student records with private feedback session");
                
        instructor = testDataNormal.instructors.get("teammates.test.CS1101");
        student = testDataNormal.students.get("teammates.test@ISR.CS1101");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageWithPrivateFeedback.html");
        
        
        ______TS("content: no student records, no profiles");
        
        instructor = testDataNormal.instructors.get("teammates.noeval");
        student = testDataNormal.students.get("alice.b.tmms@ISR.NoEval");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageNoRecords.html");
        
        ______TS("content: multiple feedback session type student record");
        
        removeAndRestoreTestDataOnServer(testDataQuestionType);
                
        instructor = testDataQuestionType.instructors.get("instructor1OfCourse1");
        student = testDataQuestionType.students.get("student1InCourse1");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageMixedQuestionType.html");

    }
    
    private void testLinks() throws Exception{
        InstructorAttributes instructor = testDataLinks.instructors.get("CESubEditUiT.instructor");
        StudentAttributes student = testDataLinks.students.get("Charlie");
        removeAndRestoreTestDataOnServer(testDataLinks);
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        viewPage.clickEvalEditLink("First Eval");
        assertTrue(browser.driver.getCurrentUrl().toString().contains(Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT));
    }
    
    private void testScript() throws Exception{
        InstructorAttributes instructor = testDataNormal.instructors.get("teammates.test.CS2104");
        StudentAttributes student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");
        
        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;
        
        viewPage = getStudentRecordsPage();
        
        ______TS("add comment button");
        viewPage.verifyAddCommentButtonClick();
        
        ______TS("edit comment button");
        viewPage.verifyEditCommentButtonClick(0);
    }
    
    private void testAction() throws Exception{
        
        ______TS("add comment: success");

        viewPage.addComment("New comment from teammates.test for Benny C")
                .verifyStatus("New comment has been added");
        
        ______TS("delete comment: cancel");
        
        viewPage.clickDeleteCommentAndCancel(0);
        
        ______TS("delete comment: success");
        
        viewPage.clickDeleteCommentAndConfirm(0)
                .verifyStatus("Comment deleted");
        
        ______TS("edit comment: success");
        
        viewPage.editComment(0, "Edited comment 2 from CS2104 teammates.test Instructor to Benny")
                .verifyStatus("Comment edited");
        
        //Edit back so that restoreDataBundle can identify and delete the comment.
        viewPage.editComment(0, "Comment 2 from ISR.CS2104 teammates.test Instructor to Benny");
    }
    
    private InstructorStudentRecordsPage getStudentRecordsPage() {
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withStudentEmail(studentEmail);
        
        return loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}