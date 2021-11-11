<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page pageEncoding="UTF-8"%>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<h2 class="mt-4 mb-4 text-center">Setup Google Authenticator</h2>
<br>
<br>



<img src="data:image/png;base64,${qrCode}" alt="QR Code for Google Authenticator"/>

<form class="form-signin" action="SetupTOTP" method="POST" >
    <img class="mb-2" src="${pageContext.request.contextPath}/images/logo.png" alt="" width="72" >

    <h1 class="h3 mb-3 font-weight-normal">Setup TOTP</h1>

    <label for="inputTOTP" class="sr-only">Input 6-digit key</label>
    <input type="text" name="inputTOTP" id="inputTOTP" class="form-control" placeholder="6-digit auth code" required=true autofocus=true>
    <br>
    <button class="btn btn-lg btn-primary btn-block" type="submit">
        Verificer
    </button>
</form>

<div>
    <c:if test="${requestScope.error}">
        <div class="alert alert-danger" role="alert">
        <c:out value="${requestScope.errorMsg}"></c:out>
        </div>
    </c:if>
</div>