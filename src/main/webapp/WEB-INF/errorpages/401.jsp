<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<jsp:useBean id="now" class="java.util.Date"/>
<fmt:formatDate var="year" value="${now}" pattern="yyyy"/>
<%@page pageEncoding="UTF-8" %>

<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Twena Nielsen (cph-at89@cphbusiness.dk)
  --%>

<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">

    <!-- FontAwesome -->
    <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">

    <!-- Data tables -->
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.2/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/styles.css">

    <title>HaxB00k</title>
</head>
<body>
<div class="container">
    <br>
    <br>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <h2 class="mt-6 mb-6 text-center font-weight-bold">Ups!</h2>
            </div>
            <div class="col-md-12 align-self-center">
                <p class="mt-6 mb-6 text-center">
                    <i class="icon-lock icon-4x"></i>
                </p>
            </div>
            <div class="col-md-12">
                <h3 class="mt-6 mb-6 text-center">Du har vidst ikke adgang til denne side.</h3>
            </div>
            <div class="col-md-12">
                <p class="mt-6 mb-6 text-center">
                    <a href="${pageContext.request.contextPath}">Tilbage til start</a>
                </p>
            </div>
        </div>
    </div>
    <br><br>
    <footer class="mb-10 mt-4" style="border-top: 1px solid #d9d9d9;">
        <p>Copyright &copy; CoderGram ${year}</p>
    </footer>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx" crossorigin="anonymous"></script>
</body>
</html>