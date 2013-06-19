<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Resolve Mobile Form Entry Error" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/dojoConfig.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<h2>
	<spring:message code="amrsmobileforms.resolveErrors.title" />
</h2>

<c:set var="errorSize" value="${fn:length(errorFormResolve)}" />

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
		
		<b class="boxHeader"><spring:message code="amrsmobileforms.resolveErrors.title" />:</b>
		<div class="box">
			<form method="post" action="">
				<table cellpadding="3" cellspacing="0" width="100%" id="resolveErrorsTable">
					<c:forEach var="queueItem" items="${errorFormResolve}" varStatus="queueItemStatus">
						<tr>
							<td><b><spring:message code="amrsmobileforms.commentOnError.title" />: </b>${queueItem.comment}</td>
						</tr>
						<tr class="<c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<!-- Info about the patient and encounter -->
								<spring:message code="amrsmobileforms.commentOnError.commentor" />: 
								<span >
									${queueItem.commentedBy.names}
									<spring:message code="amrsmobileforms.commentOnError.date" />:
									${queueItem.dateCommented}
								</span> <br/><br/>
								<spring:message code="Person.name" />: <span class="value">${queueItem.name}</span> <br/>
								<spring:message code="Patient.identifier" />: <span class="value">${queueItem.identifier}</span> <br/>
								<spring:message code="Person.gender" />: <span class="value">${queueItem.gender}</span> <br/>
								<br/>
								<spring:message code="Encounter.location" />: <span class="value">${queueItem.location}</span> <br/>
								<spring:message code="Encounter.datetime" />: <span class="value">${queueItem.encounterDate}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.formName" />: <span class="value">${queueItem.formModelName} v${queueItem.formId}</span> <br/>
								<br/>
								<spring:message code="amrsmobileforms.resolveErrors.errorId" />: <span >${queueItem.mobileFormEntryErrorId}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.errorDateCreated" />: <span >${queueItem.dateCreated}</span> <br/>
								<spring:message code="amrsmobileforms.resolveErrors.error" />: <span >${queueItem.error}</span> <br/><br/>
								<b><spring:message code="amrsmobileforms.resolveErrors.errorDetails" />: </b><div style="height: 40px; overflow-y: scroll; border: 1px solid #BBB;">${queueItem.errorDetails}</div> <br/>
							</td>
						</tr>
						<c:set var="errorType" value="patient" />
						<tr class="secondRow <c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<input type="hidden" name="amrsmobileformsErrorId" value="${queueItem.mobileFormEntryErrorId}"/>
								
								<!-- Pick a provider -->
								<input type="radio" name="errorItemAction" value="linkProvider"/> <spring:message code="amrsmobileforms.resolveErrors.action.providerLink"/>:
								<openmrs_tag:userField formFieldName="providerId" searchLabelCode="amrsmobileforms.resolveErrors.action.findProvider" initialValue="" callback="setErrorAction" />
								<br />

								<!-- Have the machinery create a new patient -->
								<input type="radio" name="errorItemAction" value="createPatient" /> <spring:message code="amrsmobileforms.resolveErrors.action.createPatient"/>
								<br/>
								
								<!-- Assign a new patient identifier-->
								<input type="radio" name="errorItemAction" value="newIdentifier" /> <spring:message code="amrsmobileforms.resolveErrors.action.newIdentifier"/>:
								<input type="text" name="patientIdentifier"/>
								<br/>
								
								<!-- Assign a birth date to patient -->
								<input type="radio" name="errorItemAction" value="assignBirthdate" /> <spring:message code="amrsmobileforms.resolveErrors.action.assignBirthDate"/>:
								<input type="text" name="birthDate" onClick="showCalendar(this)"/>
								<br/>
								
								<!-- Link patient to household -->
								<input type="radio" name="errorItemAction" value="linkHousehold" />	<spring:message code="amrsmobileforms.resolveErrors.action.createLink"/>:
								<input type="text" name="householdId"/>
								<br/>

								<input type="radio" name="errorItemAction" value="newHousehold" />	<spring:message code="amrsmobileforms.resolveErrors.action.newHouseholdIdentifier"/>:
								<input type="text" name="householdIdentifier"/>
								<br/>
								
								<!-- This is an invalid comment, delete it -->
								<input type="radio" name="errorItemAction" value="deleteComment" />	<spring:message code="amrsmobileforms.resolveErrors.action.deleteComment"/>
								<br/>
								
								<!-- This is an invalid error, delete it -->
								<input type="radio" name="errorItemAction" value="deleteError" /> <spring:message code="amrsmobileforms.resolveErrors.action.deleteError"/>
								<br/>

								<!-- I don't want to do anything to this one now -->
								<input type="radio" name="errorItemAction" value="noChange" checked="checked"/> <spring:message code="amrsmobileforms.resolveErrors.action.noChange"/>
								<br/>
							</td>
						</tr>
						<tr>
							<td>
								<a href="#View Form [+]" onclick="return toggleLayer('xmlForm', this, '<spring:message code="amrsmobileforms.viewForm"/>', '<spring:message code="amrsmobileforms.hideForm"/>')"><spring:message code="amrsmobileforms.viewForm"/></a>
								<br/>
								<input type="submit" name="action" value='<spring:message code="general.submit" />' />
							</td>
						</tr>
						<tbody id="xmlForm" style="display: none">
							<tr>
								<td>
									<textarea readonly="true" rows="20" cols="160">${queueItem.formName}</textarea>
								</td>
							</tr>
						</tbody>
					</c:forEach>
				</table>
			</form>
		</div>

	</c:otherwise>
</c:choose>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp"%>
