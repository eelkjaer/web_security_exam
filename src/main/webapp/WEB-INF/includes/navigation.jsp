<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<nav class="navbar navbar-light navbar-expand-md navigation-clean-button"
     style="background-color:#004687;color:#ffffff;width:100%;">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}">
            <img src="../images/logo-white.png" class="img-fluid" alt="Responsive image" style="width:30%;">
        </a>
        <button data-toggle="collapse" data-target="#navcol-1" class="navbar-toggler">
            <span class="sr-only">Toggle navigation</span>
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navcol-1">
            <ul class="nav navbar-nav ml-auto">
                <c:forEach var="i" items="${requestScope.navbar.items}">
                    <li class="nav-item">
                        <a class="nav-link <c:if test="${i.active}">active</c:if>" style="color:#ffffff;" href="<c:url value="${i.url}"/>">
                            <i class="fa fa-${i.icon}"></i>
                                <c:out value="${i.name}"></c:out>
                        </a>
                    </li>
                </c:forEach>
                <c:if test="${sessionScope.user != null}">
                        <span class="navbar-text" style="color:white;">
                            (<c:out value="${sessionScope.user.name}"></c:out>)
                        </span>
                </c:if>
            </ul>
        </div>
    </div>
</nav>