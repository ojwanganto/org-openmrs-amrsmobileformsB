<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Comment on Mobile Form Entry Errors" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.resolveErrors.title" /></h2>

<c:set var="errorSize" value="${fn:length(errorFormComment)}" />

<c:choose>
	<c:when test="${errorSize < 1}">
		<br/>
		<i>(<spring:message code="amrsmobileforms.resolveErrors.errorLoading"/>)</i>
		<br/>
	</c:when>
	<c:otherwise>
		
		<style type="text/css">
			#resolveErrorsTable tr td .value {
			  font-weight: bold;
			}
			#resolveErrorsTable tr.secondRow {
			  border-bottom: 1px solid black;
			}
		</style>
		
		<b class="boxHeader"><spring:message code="amrsmobileforms.commentOnError" />:</b>
		<div class="box">
			<form method="post" onSubmit="return validateForm()" >
				<spring:message code="amrsmobileforms.resolveErrors.help" /> 
				<br/>
				<br/>
				<table cellpadding="3" cellspacing="0" width="100%" id="resolveErrorsTable">
					<c:forEach var="queueItem" items="${errorFormComment}" varStatus="queueItemStatus">
						<tr class="<c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<spring:message code="Person.name" />: <span class="value">${queueItem.name}</span> <br/>
								<spring:message code="Patient.identifier" />: <span class="value">${queueItem.identifier}</span> <br/>
								<spring:message code="Person.gender" />: <span class="value">${queueItem.gender}</span> <br/>
								<spring:message code="Encounter.location" />: <span class="value">${queueItem.location}</span> <br/>
								<spring:message code="Encounter.datetime" />: <span class="value">${queueItem.encounterDate}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.formName" />: <span class="value">${queueItem.formModelName} v${queueItem.formId}</span> <br/>
								<br/>
								<spring:message code="amrsmobileforms.resolveErrors.errorId" />: <span >${queueItem.mobileFormEntryErrorId}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.errorDateCreated" />: <span >${queueItem.dateCreated}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.error" />: <span >${queueItem.error}</span> <br/> <br/>
								<b><spring:message code="amrsmobileforms.resolveErrors.errorDetails" />:</b><div style="height: 40px; overflow-y: scroll; border: 1px solid #BBB;">${queueItem.errorDetails}</div> <br/>
								
							</td>
						</tr>
						<tr class="<c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<b><spring:message code="amrsmobileforms.commentOnError.title" /></b><br/>
								<textarea rows="4" cols="60" id="comment" name="comment" onKeyUp="clearError('comment')"></textarea>
								<span class="error" id="commentError">Please enter a comment</span>
							</td>
						</tr>
						<tr class="<c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td colspan="2"><a href="#View Form [+]" onclick="return toggleLayer('xmlForm', this, '<spring:message code="amrsmobileforms.viewForm"/>', '<spring:message code="amrsmobileforms.hideForm"/>')"><spring:message code="amrsmobileforms.viewForm"/></a></td>
						</tr>
						<tr>
							<td colspan="2">
								<input type="submit" name="action" value='<spring:message code="general.submit" />' />
							</td>
						</tr>
						<tbody id="xmlForm" style="display: none">
							<tr>
								<td><textarea readonly="true" rows="20" cols="160">${queueItem.formName}</textarea></td>
							</tr>
						</tbody>
					</c:forEach>
				</table>
			</form>
		</div>

	</c:otherwise>
</c:choose>
<br/>

<script type="text/javascript">
	clearError("comment");

	function validateForm() {
		var comment = document.getElementById("comment");
		
		if (comment.value == "") {
			document.getElementById("commentError").style.display = "";
			return false;
		}
		return true;
	}

	function clearError(errorName) {
		document.getElementById(errorName + "Error").style.display = "none";
	}
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>