package teammates.common.util;

public class EmailTemplates {

    public static String USER_EVALUATION_ = FileHelper.readResourseFile("userEmailTemplate-evaluation_.html");
    public static String USER_EVALUATION_PUBLISHED = FileHelper.readResourseFile("userEmailTemplate-evaluationPublished.html");
    public static String USER_COURSE_JOIN = FileHelper.readResourseFile("userEmailTemplate-courseJoin.html");
    public static String FRAGMENT_STUDENT_COURSE_JOIN = FileHelper.readResourseFile("studentEmailFragment-courseJoin.html");    
    public static String FRAGMENT_INSTRUCTOR_COURSE_JOIN = FileHelper.readResourseFile("instructorEmailFragment-courseJoin.html");
    public static String USER_FEEDBACK_SESSION = FileHelper.readResourseFile("userEmailTemplate-feedbackSession.html");
    public static String USER_FEEDBACK_SESSION_PUBLISHED = FileHelper.readResourseFile("userEmailTemplate-feedbackSessionPublished.html");
    public static String USER_PENDING_COMMENTS_CLEARED = FileHelper.readResourseFile("userEmailTemplate-pendingCommentsCleared.html");
    public static String SYSTEM_ERROR = FileHelper.readResourseFile("systemErrorEmailTemplate.html");
    public static String NEW_INSTRCUTOR_ACCOUNT_WELCOME = FileHelper.readResourseFile("newInstructorAccountWelcome.html");
}
