package teammates.ui.controller;

import java.util.List;
import java.util.logging.Logger;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.AccountsDb;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

/**
 * Action: cargando edicion de detalles de cuenta de instructor.
 */
public class InstructorDetailsAccountEditAction extends Action {
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

        data.currentName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        data.currentEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        data.institution = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        
        data.currentName = Sanitizer.sanitizeName(data.currentName);
        data.currentEmail = Sanitizer.sanitizeEmail(data.currentEmail);
        data.institution = Sanitizer.sanitizeName(data.institution);
        
        try {
            /*
             * _userConnect = new UserConnection((String)req.getSession().getAttribute("email"));
            Usuario user = _userConnect.getUser();
             */
            AccountsDb DBcuentai = new AccountsDb();
            InstructorsDb DBi = new InstructorsDb();
            List<Instructor> instructores = DBi.getInstructorForGoogleId(account.googleId);
            //List<List<Integer>> listaDeListas;
            for ( Instructor currentinstructor : instructores ){
                currentinstructor.setName(data.currentName);
            }
            Account cuentainstructor = DBcuentai.getAccountInstructor(account.googleId);
            cuentainstructor.setInstitute(data.institution);
            cuentainstructor.setName(data.currentName);
            //logic.createAccount(account.googleId , data.currentName, true, account.email, data.institution );
                        
            /* Explanation: Create the appropriate result object and return it.*/
            ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_DETAILS_ACCOUNT, data);
            return response;
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
        } catch (EntityAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
        /* Explanation: We must set this variable. It is the text that will 
         * represent this particular execution of this action in the
         * 'admin activity log' page.*/
              
        
    }
}
   