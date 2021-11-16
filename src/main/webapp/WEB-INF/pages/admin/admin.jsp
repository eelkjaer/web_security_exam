<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page pageEncoding="UTF-8"%>
<%--
  ~ Copyright (c) 2021. Team CoderGram
  ~
  ~ @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
  ~ @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
  --%>

<c:if test="${requestScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <strong><c:out value="${requestScope.errorMsg}"></c:out></strong>
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

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
    <c:forEach items="${requestScope.users}" var="user" varStatus="vs">
    <tr>
            <td>
                <c:out value="${user.name}"></c:out>
            </td>
            <td>
                <c:out value="${user.email}"></c:out>
            </td>
            <td>
                <c:out value="${user.role.name()}"></c:out>
            </td>
            <td>
                <c:out value="${user.lastLoginDate}"></c:out>
            </td>
            <td>
                <button type="button" class="btn btn-danger" style="width:100%;" data-toggle="modal" data-target="#delModal${user.id}" <c:if test="${sessionScope.user.id == user.id || user.admin}">disabled</c:if>>Delete user</button>
                <button type="button" class="btn btn-warning" style="width:100%;" data-toggle="modal" data-target="#myModal${user.id}">Access log</button>
            </td>
    </tr>
    </c:forEach>
</table>


<c:forEach items="${requestScope.users}" var="user" varStatus="vs">
    <!-- FOR ACCESS LOG -->
<div class="modal fade" id="myModal${user.id}" data-backdrop="true" data-keyboard="true">
    <div class="modal-dialog  modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Access log for ${user.email}</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <table id="modaltable${user.id}" name="modaltable" class="table table-striped table-bordered">
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
                        <td><c:choose>
                            <c:when test="${log.success}">
                                &#9989;
                            </c:when>
                            <c:otherwise>
                                &#10060;
                            </c:otherwise>
                        </c:choose></td>
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

    <!-- FOR DELETE BUTTON -->
    <div class="modal fade" id="delModal${user.id}" data-backdrop="true" data-keyboard="true">
        <div class="modal-dialog  modal-dialog-centered">
            <div class="modal-content">
                <form class="form-signin" action="AdminPage" method="POST" >
                <div class="modal-header">
                    <h4 class="modal-title">Confirm deleting ${user.email}</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                        <p>Please confirm that you want to delete the user, by providing your OTP</p>
                        <input type="hidden" name="target" value="totp">
                        <input type="hidden" name="action" value="deleteUser"/>
                        <input type="hidden" name="userid" value="${user.id}"/>
                        <label for="inputTOTP" class="sr-only">Token</label>
                        <input type="text" name="inputTOTP" id="inputTOTP" class="form-control" placeholder="Token from Google Authenticator" required=true autofocus=true>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-danger">Delete user</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                </div>
                </form>
            </div>
        </div>
    </div>
    <script type="text/javascript" class="init">
      $(document).ready(function () {
        $('#modaltable${user.id}').DataTable({
          "columnDefs": [{
            "targets": 'no-sort',
            "orderable": false,
            "pageLength": 5
          }]
        });
      });
    </script>
</c:forEach>

<script type="text/javascript" class="init">
  $(document).ready(function () {
    $('#example').DataTable({
      "columnDefs": [{
        "targets": 'no-sort',
        "orderable": false,
      }]
    });
  });
</script>