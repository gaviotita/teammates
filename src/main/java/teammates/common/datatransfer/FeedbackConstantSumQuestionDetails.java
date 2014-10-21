package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.core.FeedbackQuestionsLogic;

public class FeedbackConstantSumQuestionDetails extends FeedbackAbstractQuestionDetails {
    public int numOfConstSumOptions;
    public List<String> constSumOptions;
    public boolean distributeToRecipients;
    public boolean pointsPerOption;
    public int points;
    
    public FeedbackConstantSumQuestionDetails() {
        super(FeedbackQuestionType.CONSTSUM);
        
        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<String>();
        this.distributeToRecipients = false;
        this.pointsPerOption = false;
        this.points = 100;
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            int numOfConstSumOptions, List<String> constSumOptions,
            boolean pointsPerOption, int points) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
        
        this.numOfConstSumOptions = constSumOptions.size();
        this.constSumOptions = constSumOptions;
        this.distributeToRecipients = false;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            boolean pointsPerOption, int points) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
        
        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<String>();
        this.distributeToRecipients = true;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        if(!distributeToRecipients){
            return Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION;
        } else {
            return Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT;    
        }
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            FeedbackAbstractResponseDetails existingResponseDetails) {
        
        FeedbackConstantSumResponseDetails existingConstSumResponse = (FeedbackConstantSumResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if(distributeToRecipients){
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${constSumOptionVisibility}", "style=\"display:none\"",
                            "${constSumOptionPoint}", existingConstSumResponse.getAnswerString(),
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${constSumOptionValue}", "");
            optionListHtml.append(optionFragment + Const.EOL);
        } else {
            for(int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", Integer.toString(existingConstSumResponse.getAnswerList().get(i)),
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}", constSumOptions.get(i));
                optionListHtml.append(optionFragment + Const.EOL);
            }
        }
        
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", distributeToRecipients? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", (distributeToRecipients == true) ? "true" : "false",
                "${constSumPointsPerOptionValue}", (pointsPerOption == true) ? "true" : "false",
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS
                );
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if(distributeToRecipients){
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${constSumOptionVisibility}", "style=\"display:none\"",
                            "${constSumOptionPoint}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${constSumOptionValue}", "");
            optionListHtml.append(optionFragment + Const.EOL);
        } else {
            for(int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", "",
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}", constSumOptions.get(i));
                optionListHtml.append(optionFragment + Const.EOL);
            }
        }
        
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", distributeToRecipients? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", (distributeToRecipients == true) ? "true" : "false",
                "${constSumPointsPerOptionValue}", (pointsPerOption == true) ? "true" : "false",
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS
                );
        
        return html;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < numOfConstSumOptions; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${constSumOptionValue}", constSumOptions.get(i),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION);

            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM,
                "${constSumEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfConstSumOptions}", Integer.toString(numOfConstSumOptions),
                "${constSumToRecipientsValue}", (distributeToRecipients == true) ? "true" : "false",
                "${selectedConstSumPointsPerOption}", (pointsPerOption == true) ? "selected=\"selected\"" : "",
                "${constSumOptionTableVisibility}", (distributeToRecipients == true) ? "style=\"display:none\"" : "",
                "${constSumPoints}", (points == 0) ? "100" : new Integer(points).toString(),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS);
        
        return html;
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";
        
        if(this.distributeToRecipients) {
            additionalInfo = this.getQuestionTypeDisplayName() + "<br>";
        } else if(numOfConstSumOptions > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for(int i = 0; i < numOfConstSumOptions; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", constSumOptions.get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString());
        
        }
        //Point information
        additionalInfo += pointsPerOption? "Points per "+(distributeToRecipients?"recipient":"option")+": " + points : "Total points: " + points;

        
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
    public String getQuestionResultStatisticsHtml(
            List<FeedbackResponseAttributes> responses,
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
        List<String> options = new ArrayList<String>();
        Map<String, List<Integer>> optionPoints = new HashMap<String, List<Integer>>();
        Map<String, Map<String, String>> nameEmailMapping = new HashMap<String, Map<String, String>>();
        
        if(distributeToRecipients){
            for(FeedbackResponseAttributes response : responses){
                FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails)response.getResponseDetails(); 
                String recipientEmail = response.recipientEmail;
                Map<String, String> emailMapping = nameEmailMapping.get(bundle.getNameForEmail(recipientEmail));
                if(emailMapping == null){
                    emailMapping = new HashMap<String, String>();
                    nameEmailMapping.put(bundle.getNameForEmail(recipientEmail), emailMapping);
                }
                if(emailMapping.get(recipientEmail) == null){
                    emailMapping.put(recipientEmail, "true");
                }
                
                List<Integer> points = optionPoints.get(recipientEmail);
                if(points == null){
                    points = new ArrayList<Integer>();
                    optionPoints.put(recipientEmail, points);
                }
                points.add(frd.getAnswerList().get(0));
            }
        } else {
            options = constSumOptions;
            
            for(FeedbackResponseAttributes response : responses){
                FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails)response.getResponseDetails(); 
                for(int i=0 ; i<frd.getAnswerList().size(); i++){
                    List<Integer> points = optionPoints.get(String.valueOf(i));
                    if(points == null){
                        points = new ArrayList<Integer>();
                        optionPoints.put(String.valueOf(i), points);
                    }
                    points.add(frd.getAnswerList().get(i));
                }
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, List<Integer>> entry : optionPoints.entrySet() ){
            String option;
            if(distributeToRecipients){
                String email = entry.getKey();
                String name = bundle.getNameForEmail(email);
                option = name;
                if(nameEmailMapping.get(name).size() > 1){
                    option += " <b>[" + email + "]</b>";
                }
            } else {
                option = options.get(Integer.parseInt(entry.getKey()));
            }
            List<Integer> points = entry.getValue();
            Collections.sort(points);
            double average = 0;
            for(Integer point : points){
                average += point;
            }
            average = average / points.size();
            String pointsReceived = "";
            if(points.size() > 10){
                for(int i = 0; i < 5; i++){
                    pointsReceived += points.get(i) + " , ";
                }
                pointsReceived += "...";
                for(int i = points.size() - 5; i < points.size(); i++){
                    pointsReceived += " , " + points.get(i);
                }
            } else {
                for(int i = 0; i < points.size(); i++){
                    pointsReceived += points.get(i);
                    if(i != points.size() - 1){
                        pointsReceived += " , ";
                    }
                }
            }
            
            fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_OPTIONFRAGMENT,
                                "${constSumOptionValue}", option,
                                "$(pointsReceived)", pointsReceived,
                                "${averagePoints}", df.format(average));
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS,
                "${optionRecipientDisplayName}", distributeToRecipients? "Recipient":"Option",
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
        List<String> options;
        List<Integer> optionPoints = new ArrayList<Integer>();
        Map<String, Integer[]> optionTotalCount = new LinkedHashMap<String, Integer[]>();
                
        if(distributeToRecipients){
            for(FeedbackResponseAttributes response : responses){
                FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails)response.getResponseDetails(); 
                String recipientEmail = response.recipientEmail;
                String recipientName = bundle.getNameForEmail(recipientEmail);
                Integer[] pointCount = optionTotalCount.get(recipientName);
                if(pointCount == null){
                    pointCount = new Integer[]{0,0};
                }
                pointCount[0] += frd.getAnswerList().get(0);
                pointCount[1] += 1;
                optionTotalCount.put(recipientName, pointCount);
            }
        } else {
            options = constSumOptions;
            for(int i=0 ; i<options.size() ; i++){
                optionPoints.add(0);
            }
            
            for(FeedbackResponseAttributes response : responses){
                FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails)response.getResponseDetails(); 
                for(int i=0 ; i<frd.getAnswerList().size(); i++){
                    optionPoints.set(i, optionPoints.get(i)+frd.getAnswerList().get(i));
                }
            }
            
            for(int i=0 ; i<options.size() ; i++){
                optionTotalCount.put(options.get(i), new Integer[]{optionPoints.get(i),responses.size()});
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, Integer[]> entry : optionTotalCount.entrySet() ){
            double average = entry.getValue()[0]/entry.getValue()[1];
            fragments += entry.getKey() + ","
                      + df.format(average) + Const.EOL;
        }
        
        csv += (distributeToRecipients? "Recipient":"Option") + ", Average Points" + Const.EOL; 
        
        csv += fragments + Const.EOL;
        
        return csv;
    }

    @Override
    public boolean isChangesRequiresResponseDeletion(
            FeedbackAbstractQuestionDetails newDetails) {
        FeedbackConstantSumQuestionDetails newConstSumDetails = (FeedbackConstantSumQuestionDetails) newDetails;

        if (this.numOfConstSumOptions != newConstSumDetails.numOfConstSumOptions ||
            this.constSumOptions.containsAll(newConstSumDetails.constSumOptions) == false ||
            newConstSumDetails.constSumOptions.containsAll(this.constSumOptions) == false) {
            return true;
        }
        
        if(this.distributeToRecipients != newConstSumDetails.distributeToRecipients) {
            return true;
        }
        
        if(this.points != newConstSumDetails.points){
            return true;
        }
        
        if(this.pointsPerOption != newConstSumDetails.pointsPerOption){
            return true;
        }
        
        return false;
    }

    @Override
    public String getCsvHeader() {
        if(distributeToRecipients){
            return "Feedback";
        } else {
            List<String> sanitizedOptions = Sanitizer.sanitizeListForCsv(constSumOptions);
            return "Feedbacks:," + StringHelper.toString(sanitizedOptions, ",");
        }
    }

    final int MIN_NUM_OF_CONST_SUM_OPTIONS = 2;
    final int MIN_NUM_OF_CONST_SUM_POINTS = 1;
    final String ERROR_NOT_ENOUGH_CONST_SUM_OPTIONS = "Too little options for "+ this.getQuestionTypeDisplayName()+". Minimum number of options is: ";
    final String ERROR_NOT_ENOUGH_CONST_SUM_POINTS = "Too little points for "+ this.getQuestionTypeDisplayName()+". Minimum number of points is: ";
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if(!distributeToRecipients && numOfConstSumOptions < MIN_NUM_OF_CONST_SUM_OPTIONS){
            errors.add(ERROR_NOT_ENOUGH_CONST_SUM_OPTIONS + MIN_NUM_OF_CONST_SUM_OPTIONS+".");
        }
        
        if(points < MIN_NUM_OF_CONST_SUM_POINTS){
            errors.add(ERROR_NOT_ENOUGH_CONST_SUM_POINTS + MIN_NUM_OF_CONST_SUM_POINTS+".");
        }
        
        return errors;
    }

    final String ERROR_CONST_SUM_MISMATCH = "Please distribute all the points for distribution questions. To skip a distribution question, leave the boxes blank.";
    final String ERROR_CONST_SUM_NEGATIVE = "Points given must be 0 or more.";
    
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        
        if(responses.size() < 1){
            //No responses, no errors.
            return errors;
        }
        
        String fqId = responses.get(0).feedbackQuestionId;
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        FeedbackQuestionAttributes fqa = fqLogic.getFeedbackQuestion(fqId);
        
        int numOfResponseSpecific = fqa.numberOfEntitiesToGiveFeedbackTo;
        int maxResponsesPossible = numRecipients;
        if (numOfResponseSpecific == Const.MAX_POSSIBLE_RECIPIENTS ||
                numOfResponseSpecific > maxResponsesPossible) {
            numOfResponseSpecific = maxResponsesPossible;
        }
        numRecipients = numOfResponseSpecific;
        
        int numOptions = distributeToRecipients? numRecipients : constSumOptions.size();
        int totalPoints = pointsPerOption? points*numOptions: points;
        int sum = 0;
        for(FeedbackResponseAttributes response : responses){
            FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails) response.getResponseDetails();
            
            //Check that all response points are >= 0
            for(Integer i : frd.getAnswerList()){
                if(i < 0){
                    errors.add(ERROR_CONST_SUM_NEGATIVE);
                    return errors;
                }
            }
            
            //Check that points sum up properly
            if(distributeToRecipients){
                sum += frd.getAnswerList().get(0);
            } else {
                sum = 0;
                for(Integer i : frd.getAnswerList()){
                    sum += i;
                }
                if(sum != totalPoints || frd.getAnswerList().size() != constSumOptions.size()){
                    errors.add(ERROR_CONST_SUM_MISMATCH);
                    return errors;
                }
            }
        }
        if(distributeToRecipients && sum != totalPoints){
            errors.add(ERROR_CONST_SUM_MISMATCH + sum + "/" + totalPoints);
            return errors;
        }
        return errors;
    }

}
