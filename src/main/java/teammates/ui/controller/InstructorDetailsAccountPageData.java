package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

/**
 * This is the PageData object for the 'Courses' page 
 */

public class InstructorDetailsAccountPageData extends PageData {
    public InstructorDetailsAccountPageData(AccountAttributes account) {
        super(account);
    }
    /** Used when adding a course. Null if not adding a course. */
    //public CourseAttributes newCourse;
    
    public HashMap<String, InstructorAttributes> instructors;
    
    /* 
    public List<CourseDetailsBundle> allCourses;
    
    public List<CourseAttributes> archivedCourses;
    
    /* Values to show in the form fields (in case reloading the page after a 
     *   failed attempt to create a course)*/
    //public String courseIdToShow;
    //public String courseNameToShow;
    
    public String currentName;
    public String currentEmail;
    public String currentShortName;
    public String institution;
}

