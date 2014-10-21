package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor
 */
public class InstructorCourseEnrollPageData extends PageData {
    
    public InstructorCourseEnrollPageData(AccountAttributes account) {
        super(account);
        enrollStudents = "";
        nameStudent = "";
        teamStudent = "";
        emailStudent = "";
        commentStudent = "";
        action = "";
    }

    public String courseId;
    
    public String enrollStudents;
    
    public String nameStudent;
    
    public String teamStudent;
    
    public String emailStudent;
    
    public String commentStudent;
    
    public String action;

}
