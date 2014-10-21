<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorDetailsAccountPageData"%>
<%
InstructorDetailsAccountPageData data = (InstructorDetailsAccountPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
   
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorDetailsAccount.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Editar mi Cuenta</h1>
        </div>
        <div class="panel panel-primary">
            <div class="panel-body fill-plain">
                <form method="get" action="<%=Const.ActionURIs.INSTRUCTOR_DETAILS_ACCOUNT_EDIT%>" name="form_editmyAccount" class="form form-horizontal">
                    <input type="hidden" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    <!--  problemas al implementar edicion del nombre corto por no considerarse short_name en las entidades 
                    internas que iteractuan con el datastore y repercución de implementar modificaciones alli 
                     en otras funcionalidades-->
                    <!-- <div class="form-group">
                        <label class="col-sm-3 control-label">Nombre Corto:</label>
                        <div class="col-sm-3"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.INSTRUCTOR_SHORT_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_SHORT_NAME%>"
                            value="<%=data.account.name%>"
                            data-toggle="tooltip" data-placement="top" title="Editar si lo desea su nombre corto"
                            maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex="1"
                            placeholder="Editar si lo desea su nombre corto" />
                        </div>
                    </div>
                     -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Nombre Completo:</label>
                        <div class="col-sm-3"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>"
                            value="<%=data.currentName%>"
                            data-toggle="tooltip" data-placement="top" title="Editar si lo desea su nombre completo"
                            maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex="1"
                            placeholder="Editar si lo desea su nombre completo" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Email:</label>
                        <div class="col-sm-9"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>"
                            value="<%=data.account.email%>"
                            data-toggle="tooltip" data-placement="top" title="No se le permite editar su correo"
                            maxlength=<%=FieldValidator.EMAIL_MAX_LENGTH%> tabindex=2
                            placeholder="No se le permite editar su correo" readonly/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Institución:</label>
                        <div class="col-sm-9"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.INSTRUCTOR_INSTITUTION%>" id="<%=Const.ParamsNames.INSTRUCTOR_INSTITUTION%>"
                            value="<%=data.institution%>"
                            data-toggle="tooltip" data-placement="top" title="Editar si lo desea el nombre de su institución"
                            maxlength=<%=FieldValidator.INSTITUTE_NAME_MAX_LENGTH%> tabindex=2
                            placeholder="Editar si lo desea el nombre de su institución" />
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9"><input id="btnAddCourse" type="submit" class="btn btn-primary"
                                onclick="return verifyCourseData();" value="Guardar cambios" tabindex="3">
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
       
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>