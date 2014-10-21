
var loadUpFunction = function () {
    var typingErrMsg = "Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.";
    var notified = false;

    function isUserTyping(str){
        return str.indexOf("\t")==-1 && str.indexOf("|")==-1;
    }
  window.isUserTyping = isUserTyping;
    var ENTER_KEYCODE = 13;
    var enrolTextbox; 
    if ((enrolTextbox	 = $('#enrollstudents')).length){
        enrolTextbox = enrolTextbox[0];
        $(enrolTextbox).keydown(function(e) {
            var keycode = e.which || e.keyCode;
            if (keycode == ENTER_KEYCODE) {
                if (isUserTyping (e.target.value) && !notified){
                    notified = true;
                    alert(typingErrMsg);
                }
            }
        })
    };
};


function isStudentInputValid(namestudent, teamstudent, emailstudent) {
    if (namestudent == "" || teamstudent == "" || emailstudent == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY,true);
        return false;
    } else if (!isNameValid(namestudent)) {
        setStatusMessage(DISPLAY_NAME_INVALID,true);
        return false;
    } else if (!isStudentTeamNameValid(teamstudent)) {
        setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
        return false;
    } else if (!isEmailValid(emailstudent)){
        setStatusMessage(DISPLAY_EMAIL_INVALID,true);
        return false;
    }
    return true;
}


if (window.addEventListener) {
	window.addEventListener('load', loadUpFunction);
	
} else {
	window.attachEvent('load', loadUpFunction);
}