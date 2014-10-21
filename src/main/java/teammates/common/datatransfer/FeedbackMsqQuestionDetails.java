package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

public class FeedbackMsqQuestionDetails extends FeedbackAbstractQuestionDetails {
    public int numOfMsqChoices;
    public List<String> msqChoices;
    public boolean otherEnabled;
    FeedbackParticipantType generateOptionsFor;
    
    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);
        
        this.numOfMsqChoices = 0;
        this.msqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    public FeedbackMsqQuestionDetails(String questionText,
            int numOfMsqChoices,
            List<String> msqChoices,
            boolean otherEnabled) {
        super(FeedbackQuestionType.MSQ, questionText);
        
        this.numOfMsqChoices = numOfMsqChoices;
        this.msqChoices = msqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }
    
    public FeedbackMsqQuestionDetails(String questionText,
            FeedbackParticipantType generateOptionsFor) {
        super(FeedbackQuestionType.MSQ, questionText);
        
        this.numOfMsqChoices = 0;
        this.msqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        Assumption.assertTrue("Can only generate students, teams or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS ||
                generateOptionsFor == FeedbackParticipantType.TEAMS ||
                generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MSQ;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
        FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

        if (this.numOfMsqChoices != newMsqDetails.numOfMsqChoices ||
            this.msqChoices.containsAll(newMsqDetails.msqChoices) == false ||
            newMsqDetails.msqChoices.containsAll(this.msqChoices) == false) {
            return true;
        }
        
        if (this.generateOptionsFor != newMsqDetails.generateOptionsFor) {
            return true;
        }
        
        return false;
    }


    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            FeedbackAbstractResponseDetails existingResponseDetails) {
        FeedbackMsqResponseDetails existingMsqResponse = (FeedbackMsqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < choices.size(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${checked}", existingMsqResponse.contains(choices.get(i)) ? "checked=\"checked\"" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${msqChoiceValue}", choices.get(i));
            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
                "${msqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        List<String> choices = generateOptionList(courseId);
                
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < choices.size(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${msqChoiceValue}", choices.get(i));
            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
                "${msqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }
    
    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<String>();;

        switch(generateOptionsFor){
        case NONE:
            optionList = msqChoices;
            break;
        case STUDENTS:
            List<StudentAttributes> studentList = 
                    StudentsLogic.inst().getStudentsForCourse(courseId);

            for (StudentAttributes student : studentList) {
                optionList.add(student.name + " (" + student.team + ")");
            }
            
            Collections.sort(optionList);
            break;
        case TEAMS:
            try {
                List<TeamDetailsBundle> teamList = 
                        CoursesLogic.inst().getTeamsForCourse(courseId);
                
                for (TeamDetailsBundle team : teamList) {
                    optionList.add(team.name);
                }
                
                Collections.sort(optionList);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail("Course disappeared");
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorList =
                    InstructorsLogic.inst().getInstructorsForCourse(
                            courseId);

            for (InstructorAttributes instructor : instructorList) {
                optionList.add(instructor.name);
            }

            Collections.sort(optionList);
            break;
        default:
            Assumption.fail("Trying to generate options for neither students, teams nor instructors");
            break;
        }

        return optionList;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_EDIT_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < numOfMsqChoices; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${msqChoiceValue}", msqChoices.get(i),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE);

            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_EDIT_FORM,
                "${msqEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfMsqChoices}", Integer.toString(numOfMsqChoices),
                "${checkedGeneratedOptions}", (generateOptionsFor == FeedbackParticipantType.NONE) ? "" : "checked=\"checked\"", 
                "${Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS}", Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                "${generateOptionsForValue}", generateOptionsFor.toString(),
                "${studentSelected}", generateOptionsFor == FeedbackParticipantType.STUDENTS ? "selected=\"selected\"" : "",
                "${FeedbackParticipantType.STUDENTS.toString()}", FeedbackParticipantType.STUDENTS.toString(),
                "${teamSelected}", generateOptionsFor == FeedbackParticipantType.TEAMS ? "selected=\"selected\"" : "",
                "${FeedbackParticipantType.TEAMS.toString()}", FeedbackParticipantType.TEAMS.toString(),
                "${instructorSelected}", generateOptionsFor == FeedbackParticipantType.INSTRUCTORS ? "selected=\"selected\"" : "",
                "${FeedbackParticipantType.INSTRUCTORS.toString()}", FeedbackParticipantType.INSTRUCTORS.toString());
        return html;
    }
    
    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        
        if(this.generateOptionsFor != FeedbackParticipantType.NONE){
            optionListHtml.append("<br>"
                                + "The options for this question is "
                                + "automatically generated from the list of all "
                                + generateOptionsFor.toString().toLowerCase()
                                + " in this course.");
        }
        
        if(numOfMsqChoices > 0){
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for(int i = 0; i < numOfMsqChoices; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", msqChoices.get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
        }
        
        String additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString());
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
        
        return html;
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view) {
        
        if(view.equals("student")){
            return "";
        }
        
        if(responses.size() == 0){
            return "";
        }
        
        String html = "";
        String fragments = "";
        Map<String,Integer> answerFrequency = new LinkedHashMap<String,Integer>();
        
        for(String option : msqChoices){
            answerFrequency.put(option, 0);
        }
        
        int numChoicesSelected = 0;
        for(FeedbackResponseAttributes response : responses){
            List<String> answerStrings = ((FeedbackMsqResponseDetails)response.getResponseDetails()).getAnswerStrings();
            for(String answerString : answerStrings){
                numChoicesSelected++;
                if(!answerFrequency.containsKey(answerString)){
                    answerFrequency.put(answerString, 1);
                } else {
                    answerFrequency.put(answerString, answerFrequency.get(answerString)+1);
                }
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, Integer> entry : answerFrequency.entrySet() ){
            fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                                "${mcqChoiceValue}", entry.getKey(),
                                "${count}", entry.getValue().toString(),
                                "${percentage}", df.format(100*(double)entry.getValue()/numChoicesSelected));
        }
        //Use same template as MCQ for now, until they need to be different.
        html = FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS,
                "${fragments}", fragments);
        
        return html;
    }
    

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if(responses.size() == 0){
            return "";
        }
        
        String csv = "";
        String fragments = "";
        Map<String,Integer> answerFrequency = new LinkedHashMap<String,Integer>();
        
        for(String option : msqChoices){
            answerFrequency.put(option, 0);
        }
        
        int numChoicesSelected = 0;
        for(FeedbackResponseAttributes response : responses){
            List<String> answerStrings = ((FeedbackMsqResponseDetails)response.getResponseDetails()).getAnswerStrings();
            for(String answerString : answerStrings){
                numChoicesSelected++;
                if(!answerFrequency.containsKey(answerString)){
                    answerFrequency.put(answerString, 1);
                } else {
                    answerFrequency.put(answerString, answerFrequency.get(answerString)+1);
                }
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, Integer> entry : answerFrequency.entrySet() ){
            fragments += entry.getKey() + ","
                      + entry.getValue().toString() + ","
                      + df.format(100*(double)entry.getValue()/numChoicesSelected) + Const.EOL;
                    
        }

        csv += "Choice, Response Count, Percentage" + Const.EOL;
        
        csv += fragments + Const.EOL;
        
        return csv;
    }

    @Override
    public String getCsvHeader() {
        List<String> sanitizedChoices = Sanitizer.sanitizeListForCsv(msqChoices);
        return "Feedbacks:," + StringHelper.toString(sanitizedChoices, ",");
    }
    
    final int MIN_NUM_OF_MSQ_CHOICES = 2;
    final String ERROR_NOT_ENOUGH_MSQ_CHOICES = "Too little choices for "+Const.FeedbackQuestionTypeNames.MSQ+". Minimum number of options is: ";
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if(generateOptionsFor == FeedbackParticipantType.NONE &&
                numOfMsqChoices < MIN_NUM_OF_MSQ_CHOICES){
            errors.add(ERROR_NOT_ENOUGH_MSQ_CHOICES + MIN_NUM_OF_MSQ_CHOICES+".");
        }
        //TODO: check that msq options do not repeat. needed?
        
        return errors;
    }
    
    final String ERROR_INVALID_OPTION = " is not a valid option for the " + Const.FeedbackQuestionTypeNames.MSQ + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            FeedbackMsqResponseDetails frd = (FeedbackMsqResponseDetails) response.getResponseDetails();
            if(!otherEnabled){
                if(!msqChoices.containsAll(frd.answers) && generateOptionsFor == FeedbackParticipantType.NONE){
                    errors.add(frd.getAnswerString() + ERROR_INVALID_OPTION);
                }
            }
        }
        return errors;
    }


}
