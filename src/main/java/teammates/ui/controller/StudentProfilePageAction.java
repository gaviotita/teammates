package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: showing the profile page for a student in a course
 */
public class StudentProfilePageAction extends Action {

    private StudentProfilePageData data;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {        
        account.studentProfile = logic.getStudentProfile(account.googleId); 
        String editPhoto = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT);
        if (editPhoto == null) {
            editPhoto = "false";
        }
        
        data = new StudentProfilePageData(account, editPhoto);
        statusToAdmin = "studentProfile Page Load <br> Profile: " + account.studentProfile.toString();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
        return response;
    }
}
