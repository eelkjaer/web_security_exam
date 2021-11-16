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

<table id="example" name="example" class="table table-striped table-bordered" style="width:100%">
    <thead>
        <th>Navn</th>
        <th>E-mail</th>
        <th>Role</th>
        <th>Last login</th>
        <th class="no-sort"> </th>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.userlist}" var="user" varStatus="vs">
    <tr>
            <td><c:out value="${user.name}"></c:out></td>
            <td><c:out value="${user.email}"></c:out></td>
            <td><c:out value="${user.role.name()}"></c:out></td>
            <td><c:out value="${user.lastLoginDate}"></c:out></td>
            <td>
                <form action="AdminPage" method="post">
                    <input type="hidden" name="action" value="deleteUser"/>
                    <input type="hidden" name="userid" value="${user.id}"/>
                    <input type="submit" class="btn btn-danger" value="Slet bruger" onclick="return confirm('Er du sikker?')" <c:if test="${sessionScope.user.id == user.id || user.admin}">disabled</c:if>/>
                </form>
                <button type="button" class="btn btn-warning" data-toggle="modal" data-target="#myModal${user.id}">Access log</button>
            </td>
    </tr>
    </c:forEach>
</table>

<c:forEach items="${requestScope.userlist}" var="user" varStatus="vs">
<div class="modal fade" id="myModal${user.id}" data-backdrop="true" data-keyboard="true">
    <div class="modal-dialog  modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Access log for ${user.email}</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <table id="modaltable" name="modaltable" class="table table-striped table-bordered" style="width:100%">
                    <thead>
                        <th>Timestamp</th>
                        <th>IP</th>
                        <th>Success</th>
                    </thead>
                    <tbody>
                    <c:forEach items="${requestScope.loginLog}" var="log" varStatus="vss">
                    <c:if test="${log.userId == user.id}">
                    <tr <c:if test="${!log.success}">style="background-color: red;"</c:if>>
                        <td><c:out value="${log.lastLogin}"></c:out></td>
                        <td><c:out value="${log.ip}"></c:out></td>
                        <td><c:out value="${log.success}"></c:out></td>
                    </tr>
                    </c:if>
                    </c:forEach>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
</c:forEach>