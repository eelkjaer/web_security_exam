<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page pageEncoding="UTF-8"%>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<h2 class="mt-4 mb-4 text-center">Brugere</h2>
<br>
<br/> <br/>

<br><br>

<table id="example" name="example" class="table table-striped table-bordered" style="width:100%">
    <thead>
        <th>Navn</th>
        <th>E-mail</th>
        <th>Role</th>
        <th class="no-sort"> </th>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.userlist}" var="user" varStatus="vs">
    <tr>
            <td><c:out value="${user.name}"></c:out></td>
            <td><c:out value="${user.email}"></c:out></td>
            <td><c:out value="${user.role.name()}"></c:out></td>
            <td>
                <form action="Users" method="post">
                    <input type="hidden" name="action" value="deleteUser"/>
                    <input type="hidden" name="userid" value="${user.id}"/>
                    <input type="submit" class="btn btn-danger" value="Slet bruger" onclick="return confirm('Er du sikker?')" <c:if test="${sessionScope.user.id == user.id}">disabled</c:if>/>
                </form>
            </td>
    </tr>
    </c:forEach>
</table>