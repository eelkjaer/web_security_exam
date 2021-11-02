<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page pageEncoding="UTF-8"%>

<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<h2 class="mt-4 mb-4 text-center">User page</h2>
<br>
<br>
<br/> <br/>
<h1>You are logged in as a user: ${sessionScope.user.email} as ${sessionScope.user.role.toString()}</h1>