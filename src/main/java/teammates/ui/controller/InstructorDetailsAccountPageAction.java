package teammates.ui.controller;


import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

/**
 * Action: cargando edicion de detalles de cuenta de instructor.
 */
public class InstructorDetailsAccountPageAction extends Action {
    /* Explanation: Get a logger to be used for any logging */
    protected static final Logger log = Utils.getLogger();
    
    
    @Override
    public ActionResult execute() 
            throws EntityDoesNotExistException {
        /* Explanation: First, we extract any parameters from the request object.
         * e.g., idOfCourseToDelete = getRequestParam(Const.ParamsNames.COURSE_ID);
         * After that, we may verify parameters.
         * e.g. Assumption.assertNotNull(courseId);
         * In this Action, there are no parameters.*/
        
        /* Explanation: Next, check if the user has rights to execute the action.*/
        new GateKeeper().verifyInstructorPrivileges(account);
        
        /* Explanation: This is a 'show page' type action. Therefore, we 
         * prepare the matching PageData object, accessing the Logic 
         * component if necessary.*/
        InstructorDetailsAccountPageData data = new InstructorDetailsAccountPageData(account);
        data.currentShortName= account.name;
        data.currentName= account.name;
        data.currentEmail= account.email;
        data.institution= account.institute;
 
        data.instructors = new HashMap<String, InstructorAttributes>();
    
        /* Explanation: Create the appropriate result object and return it.*/
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_DETAILS_ACCOUNT, data);
        return response;
    }
}



