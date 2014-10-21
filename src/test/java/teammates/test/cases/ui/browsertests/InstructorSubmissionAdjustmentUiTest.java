package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.util.Priority;
import teammates.test.util.TestHelper;

/**
 * Covers Ui aspect of submission adjustment for evaluations and feedbacks
 */
@Priority(1)
public class InstructorSubmissionAdjustmentUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private InstructorCourseEnrollPage enrollPage;
    
    private static String enrollString = "";
    private Url enrollUrl;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSubmissionAdjustmentUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    @Test
    public void testAdjustmentOfSubsmission() throws Exception{
        
        //load the enrollPage
        loadEnrollmentPage();
        
        ______TS("typical case: enroll new student to existing course");
        String evaluationName = "evaluation1 In Course1";
        StudentAttributes newStudent = new StudentAttributes();
        newStudent.section = "None";
        newStudent.team = "Team 1.1";
        newStudent.course = "idOfTypicalCourse1";
        newStudent.email = "random@g.tmt";
        newStudent.name = "someName";
        newStudent.comments = "comments";
        
        enrollString =  "Section | Team | Name | Email | Comment" + Const.EOL;
        enrollString += newStudent.toEnrollmentString();
        
        /*
         * Old number of submissions = 2 * (4 * 4 + 1 * 1) = 34
         */
        int oldNumberOfSubmissionsForEvaluation = BackDoor
                .getAllSubmissions(newStudent.course).size();
        assertEquals(34, oldNumberOfSubmissionsForEvaluation);
        
        enrollPage.enroll(enrollString);
        
        //Wait briefly to allow task queue to successfully execute tasks
        ThreadHelper.waitFor(2000);
        
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(evaluationName, 
                BackDoor.getAllStudentsForCourse(newStudent.course), 
                BackDoor.getAllSubmissions(newStudent.course));
        
        /*
         * New number of submissions = 2 * (5 * 5 + 1 * 1) = 52
         */
        int newNumberOfSubmissionsForEvaluation = BackDoor
                .getAllSubmissions(newStudent.course).size();
        assertEquals(52, newNumberOfSubmissionsForEvaluation);
        
        ______TS("typical case : existing student changes team");
        loadEnrollmentPage();
        
        FeedbackSessionAttributes session = testData.feedbackSessions.get("session2InCourse1");
        StudentAttributes student = testData.students.get("student1InCourse1");
        
        //Verify pre-existing submissions and responses
        int oldNumberOfResponsesForSession = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertTrue(oldNumberOfResponsesForSession != 0);
        
        String newTeam = "Team 1.2";
        student.team = newTeam;
        
        enrollString =  "Section | Team | Name | Email | Comment" + Const.EOL;
        enrollString += student.toEnrollmentString();
        enrollPage.enroll(enrollString);
        
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(evaluationName, 
                BackDoor.getAllStudentsForCourse(student.course), 
                BackDoor.getAllSubmissions(student.course));
        
        int numberOfNewResponses = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertEquals(0, numberOfNewResponses);
        
        /*
         * New number of submissions = 2 * (4 * 4 + 2 * 2) = 40
         */
        newNumberOfSubmissionsForEvaluation = BackDoor
                .getAllSubmissions(newStudent.course).size();
        assertEquals(40, newNumberOfSubmissionsForEvaluation);
    }
    
    private void loadEnrollmentPage() {
        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("instructor1OfCourse1").googleId)
                .withCourseId(testData.courses.get("typicalCourse1").id);
                
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
    }
    
    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> studentReceiverResponses = BackDoor
                .getFeedbackResponsesForReceiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        List<FeedbackResponseAttributes> studentGiverResponses = BackDoor
                .getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS || 
                question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        return returnList;
    }
    
    private List<FeedbackResponseAttributes> getAllResponsesForStudentForSession(StudentAttributes student,
            String feedbackSessionName) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> allResponseOfStudent = getAllTeamResponsesForStudent(student);
        
        for (FeedbackResponseAttributes responseAttributes : allResponseOfStudent) {
            if (responseAttributes.feedbackSessionName.equals(feedbackSessionName)) {
                returnList.add(responseAttributes);
            }
        }
        
        return returnList;
    }
}
