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
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

public class FeedbackMcqQuestionDetails extends FeedbackAbstractQuestionDetails {
    public int numOfMcqChoices;
    public List<String> mcqChoices;
    public boolean otherEnabled;
    FeedbackParticipantType generateOptionsFor;

    public FeedbackMcqQuestionDetails() {
        super(FeedbackQuestionType.MCQ);
        
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    public FeedbackMcqQuestionDetails(String questionText,
            int numOfMcqChoices,
            List<String> mcqChoices,
            boolean otherEnabled) {
        super(FeedbackQuestionType.MCQ, questionText);
        
        this.numOfMcqChoices = numOfMcqChoices;
        this.mcqChoices = mcqChoices;
        this.otherEnabled = otherEnabled;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }
    
    public FeedbackMcqQuestionDetails(String questionText,
            FeedbackParticipantType generateOptionsFor) {
        super(FeedbackQuestionType.MCQ, questionText);
        
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<String>();
        this.otherEnabled = false;
        this.generateOptionsFor = generateOptionsFor;
        Assumption.assertTrue("Can only generate students, teams or instructors",
                generateOptionsFor == FeedbackParticipantType.STUDENTS ||
                generateOptionsFor == FeedbackParticipantType.TEAMS ||
                generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MCQ;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
        FeedbackMcqQuestionDetails newMcqDetails = (FeedbackMcqQuestionDetails) newDetails;

        if (this.numOfMcqChoices != newMcqDetails.numOfMcqChoices ||
            this.mcqChoices.containsAll(newMcqDetails.mcqChoices) == false ||
            newMcqDetails.mcqChoices.containsAll(this.mcqChoices) == false) {
            return true;
        }
        
        if(this.generateOptionsFor != newMcqDetails.generateOptionsFor) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackAbstractResponseDetails existingResponseDetails) {
        FeedbackMcqResponseDetails existingMcqResponse = (FeedbackMcqResponseDetails) existingResponseDetails;
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < choices.size(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${checked}", existingMcqResponse.getAnswerString().equals(choices.get(i)) ? "checked=\"checked\"" : "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${mcqChoiceValue}", choices.get(i));
            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
                "${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        List<String> choices = generateOptionList(courseId);
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < choices.size(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${checked}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${mcqChoiceValue}", choices.get(i));
            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
                "${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
        
        return html;
    }

    private List<String> generateOptionList(String courseId) {
        List<String> optionList = new ArrayList<String>();;

        switch(generateOptionsFor){
        case NONE:
            optionList = mcqChoices;
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
            Assumption
                    .fail("Trying to generate options for neither students, teams nor instructors");
            break;
        }

        return optionList;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_EDIT_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < numOfMcqChoices; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${mcqChoiceValue}", mcqChoices.get(i),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE);

            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_EDIT_FORM,
                "${mcqEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfMcqChoices}", Integer.toString(numOfMcqChoices),
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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_ADDITIONAL_INFO_FRAGMENT;
        
        if(this.generateOptionsFor != FeedbackParticipantType.NONE){
            optionListHtml.append("<br>"
                                + "The options for this question is "
                                + "automatically generated from the list of all "
                                + generateOptionsFor.toString().toLowerCase()
                                + " in this course.");
        }
        
        if(numOfMcqChoices > 0){
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for(int i = 0; i < numOfMcqChoices; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${mcqChoiceValue}", mcqChoices.get(i));

                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
        }
        
        
        String additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MCQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${mcqAdditionalInfoFragments}", optionListHtml.toString());
        
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
        
        for(String option : mcqChoices){
            answerFrequency.put(option, 0);
        }
        
        for(FeedbackResponseAttributes response : responses){
            String answerString = response.getResponseDetails().getAnswerString();
            if(!answerFrequency.containsKey(answerString)){
                answerFrequency.put(answerString, 1);
            } else {
                answerFrequency.put(answerString, answerFrequency.get(answerString)+1);
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, Integer> entry : answerFrequency.entrySet() ){
            fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.MCQ_RESULT_STATS_OPTIONFRAGMENT,
                                "${mcqChoiceValue}", entry.getKey(),
                                "${count}", entry.getValue().toString(),
                                "${percentage}", df.format(100*(double)entry.getValue()/responses.size()));
        }
        
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
        
        for(String option : mcqChoices){
            answerFrequency.put(option, 0);
        }
        
        for(FeedbackResponseAttributes response : responses){
            String answerString = response.getResponseDetails().getAnswerString();
            if(!answerFrequency.containsKey(answerString)){
                answerFrequency.put(answerString, 1);
            } else {
                answerFrequency.put(answerString, answerFrequency.get(answerString)+1);
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, Integer> entry : answerFrequency.entrySet() ){
            fragments += entry.getKey() + ","
                      + entry.getValue().toString() + ","
                      + df.format(100*(double)entry.getValue()/responses.size()) + Const.EOL;
        }
        
        csv += "Choice, Response Count, Percentage" + Const.EOL;
        
        csv += fragments;
        
        return csv;
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    final int MIN_NUM_OF_MCQ_CHOICES = 2;
    final String ERROR_NOT_ENOUGH_MCQ_CHOICES = "Too little choices for "+Const.FeedbackQuestionTypeNames.MCQ+". Minimum number of options is: ";
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if(generateOptionsFor == FeedbackParticipantType.NONE &&
                numOfMcqChoices < MIN_NUM_OF_MCQ_CHOICES){
            errors.add(ERROR_NOT_ENOUGH_MCQ_CHOICES + MIN_NUM_OF_MCQ_CHOICES + ".");
        }
        //TODO: check that mcq options do not repeat. needed?
        
        return errors;
    }

    final String ERROR_INVALID_OPTION = " is not a valid option for the " + Const.FeedbackQuestionTypeNames.MCQ + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            FeedbackMcqResponseDetails frd = (FeedbackMcqResponseDetails) response.getResponseDetails();
            if(!otherEnabled && generateOptionsFor == FeedbackParticipantType.NONE){
                if(!mcqChoices.contains(frd.getAnswerString())){
                    errors.add(frd.getAnswerString() + ERROR_INVALID_OPTION);
                }
            }
        }
        return errors;
    }


}
