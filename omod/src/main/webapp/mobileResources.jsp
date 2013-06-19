<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Mobile Resources" otherwise="/login.htm" redirect="/module/amrsmobileforms/mobileResources.list" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>


<h2><spring:message code="amrsmobileforms.mobileResources" /></h2>	


<div style="width: 50%; float: left; margin-left: 4px;">
	<b class="boxHeader"><spring:message code="amrsmobileforms.mobileResources.add"/></b>
	<div class="box">
		<form id="resourceAddForm" method="post" enctype="multipart/form-data">
			<input type="file" name="resourceFile" size="40" />
			<input type="submit" name="action" value='<spring:message code="amrsmobileforms.mobileResources.upload"/>'/>
		</form>
	</div>
</div>

<br style="clear:both"/>
<br/>

<c:if test="${fn:length(mobileResources) > 0}">
	<div style="width: 50%; float: left; margin-left: 4px;">
		<b class="boxHeader"><spring:message code="amrsmobileforms.mobileResources.manage"/></b>
		<div class="box">
			<form method="post" name="resourcesForm" onSubmit="return checkSelected(this)">
				<table cellpadding="2" cellspacing="0" width="98%">
					<tr>
						<th></th>
						<th><spring:message code="general.name"/></th>
						<th><spring:message code="general.type"/></th>
						<th><spring:message code="amrsmobileforms.mobileResources.meta"/></th>
					</tr>
					<c:forEach var="var" items="${mobileResources}" varStatus="status">
						<tr class="<c:choose><c:when test="${status.index % 2 == 0}">oddRow</c:when><c:otherwise>evenRow</c:otherwise></c:choose>">
							<td><input type="checkbox" name="absoluteName" value="${var.absoluteName}" onclick="clearError('absoluteName')"/></td>
							<td valign="top" style="white-space: nowrap">${var.fileName}</td>
							<td valign="top">${var.fileType}</td>
							<td valign="top">${var.fileMeta}</td>
						</tr>
					</c:forEach>
				</table>
				<input type="submit" name="action" value="Delete Selected Resource(s)">
				<span class="error" id="absoluteNameError">Nothing is selected to delete!</span>
			</form>
		</div>
	</div>
</c:if>

<c:if test="${fn:length(mobileResources) == 0}">
	<i> &nbsp; <spring:message code="amrsmobileforms.mobileResources.noUploaded"/></i><br/>
</c:if>

<br style="clear:both"/>
<br/>

<div style="width: 50%; float: left; margin-left: 4px;">
	<b class="boxHeader"><spring:message code="amrsmobileforms.mobileResources.help" /></b>
	<div class="box">
		<ul>
			<li><i><spring:message code="amrsmobileforms.mobileResources.help.main"/></i>
			<li><i><spring:message code="amrsmobileforms.mobileResources.help.xml"/></i>
			<li><i><spring:message code="amrsmobileforms.mobileResources.help.csv"/></i>
		</ul>
	</div>
</div>

<script type="text/javascript">
	clearError("absoluteName");

	function checkSelected(form) {
	    //get total number of CheckBoxes in form
	    var formLength = form.length;
	    var chkBoxCount = 0;
	    for (i=0;i<formLength;i++){
			if (form[i].type == 'checkbox') 
				chkBoxCount++;
		}

	    if (chkBoxCount==1) { //we dont have an array
	    	if (form.absoluteName.checked) 
	        {
	            //it's checked so return true and exit
	            return true;
	        }
	    }else {
		    //loop through each CheckBox
		    for (var i = 0; i < chkBoxCount; i++) 
		    {
		        if (form.absoluteName[i].checked) 
		        {
		            //it's checked so return true and exit
		            return true;
		        }
		    }
	    }
	    document.getElementById("absoluteNameError").style.display = "";
	    return false;
	}

	function clearError(errorName) {
		document.getElementById(errorName + "Error").style.display = "none";
	}
</script>
<%@ include file="/WEB-INF/template/footer.jsp" %>