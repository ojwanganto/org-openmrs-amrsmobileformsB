<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Entry Properties" otherwise="/login.htm" redirect="/module/amrsmobileforms/syncLog.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.sync.title"/></h2>
<div class="box">
<form method="post">
	&nbsp;&nbsp;&nbsp; <spring:message code="amrsmobileforms.sync.selectSyncDate"/>
	<select id="logDate" name="logDate">
		<c:forEach items="${files}" var="file" varStatus="varStatus">
				<option>${file}</option>
		</c:forEach>
	</select>
	&nbsp;&nbsp;&nbsp;
	<input type="submit" name="action" value="Get Sync Log">
</form>
</div>
<br/>

<c:set var="logSize" value="${fn:length(logs)}" />

<c:choose>
	<c:when test="${logSize < 1}">
		<br/>
		<i>&nbsp;&nbsp;&nbsp;(<spring:message code="amrsmobileforms.sync.empty" arguments="${logDate}"/>)</i>
		<br/>
	</c:when>
	<c:otherwise>
		<b class="boxHeader"><spring:message code="amrsmobileforms.sync.log"/>${logDate}</b>
		<table cellpadding="4" cellspacing="0" border="0" class="box">
			<tr>
				<th style="white-space: nowrap"><spring:message code="amrsmobileforms.sync.provider" /></th>
				<th><spring:message code="amrsmobileforms.sync.syncDate" /></th>
				<th><spring:message code="amrsmobileforms.sync.syncDevice" /></th>
				<th><spring:message code="amrsmobileforms.sync.household" /></th>
				<th><spring:message code="amrsmobileforms.sync.fileName" /></th>
				<th><spring:message code="amrsmobileforms.sync.fileSize" /></th>
			</tr>
			<c:forEach items="${logs}" var="log" varStatus="varStatus">
				<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
					<td style="white-space: nowrap">${log.providerId}</td>
					<td><openmrs:formatDate date="${log.syncDate}" type="long" /></td>
					<td>${log.deviceId}</td>
					<td>${log.householdId}</td>
					<td>${log.fileName}</td>
					<td>${log.fileSize}</td>
				</tr>
			</c:forEach>
		</table>
		<br/>
		&nbsp;&nbsp;&nbsp;<spring:message code="amrsmobileforms.sync.info" />
	</c:otherwise>
</c:choose>
<br/>
<%@ include file="/WEB-INF/template/footer.jsp"%>