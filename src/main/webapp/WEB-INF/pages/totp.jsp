<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page pageEncoding="UTF-8"%>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<br>
<br>
<div>
    <div class="row">
        <div class="col-md-4"></div>
        <div class="col-md-4">
            <div class="text-center">
                <form class="form-signin" action="LoginTOTP" method="POST" >

                    <input type="hidden" name="target" value="totp">

                    <img class="mb-2" src="${pageContext.request.contextPath}/images/logo.png" alt="" width="72" >

                    <h1 class="h3 mb-3 font-weight-normal">Log Ind</h1>

                    <label for="inputTOTP" class="sr-only">Token</label>
                    <input type="text" name="inputTOTP" id="inputTOTP" class="form-control" placeholder="Token from Google Authenticator" required=true autofocus=true>
                    <br>
                    <br>

                    <button class="btn btn-lg btn-primary btn-block" type="submit">
                        Log Ind
                    </button>
                </form>
                <div>
                    <c:if test="${requestScope.error}">
                        <div class="alert alert-danger" role="alert">
                                ${requestScope.errorMsg}
                        </div>
                    </c:if>
                </div>
            </div>
            <a href="Index">
                Tilbage
            </a>
        </div>
        <div class="col-md-4"></div>
    </div>
</div>