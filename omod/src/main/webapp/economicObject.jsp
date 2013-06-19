<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Economic Objects" otherwise="/login.htm" redirect="/module/amrsmobileforms/economicObject.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.economicObjects"/></h2>
<a href="#addObject">Add Object</a><br/><br/>


<div><b class="boxHeader">Economic Objects</b>
	<div class="box">
		<c:if test="${fn:length(economicObject) > 0}">
			<form method="post" name="econListForm" onSubmit="return checkSelected(this)">
				<table cellpadding="4" cellspacing="0">
					<tr>
						<th></th>
						<th>Name</th>
						<th>Answer Datatype</th>
					</tr>
					<c:forEach items="${economicObject}" var="var" >
						<tr>
							<td><input type="checkbox" name="objectId" value="${var.id}" onclick="clearError('objectId')"/></td>
							<td>${var.objectName}</td>
							<td>${var.objectType}</td>
						</tr>
					</c:forEach>
				</table>
				<input type="submit" name="action" value="Delete Selected Object(s)"/>
				<span class="error" id="objectIdError">Nothing is selected to delete!</span>
			</form>
		</c:if>
		<br>		
		<form method="post" onSubmit="return validateForm()">
			<a name="addObject"></a>
			<h2>Add a new Object</h2>
			<table cellpadding="4" cellspacing="0">
				<tr>
					<td>Name:</td>
					<td>
						<input type="text" id="objectName" name="objectName" onKeyUp="clearError('objectName')" />
						<span class="error" id="objectNameError">Please enter a name for the object</span>
				
					</td>
					<td>Object Type:</td>
					<td>
						<select id="objectType" name="objectType">
							<option selected>Numeric</option>
							<option>Text</option>
						</select>
						<span class="error" id="objectTypeError">Please select a Type for the object</span>
						
					</td>
				</tr>
			</table>
			<input type="submit" name="action" value="Create New Object"/>
		</form>
	</div>
</div>

<script type="text/javascript">
	clearError("objectName");
	clearError("objectType");
	clearError("objectId");

	function checkSelected(form) {
		
	    //get total number of CheckBoxes in form
	    var formLength = form.length;
	    var chkBoxCount = 0;
	    for (i=0;i<formLength;i++){
			if (form[i].type == 'checkbox') 
				chkBoxCount++;
		}

	    if (chkBoxCount==1) { //we dont have an array
	    	if (form.objectId.checked) 
	        {
	            //it's checked so return true and exit
	            return true;
	        }
	    }else {
		    //loop through each CheckBox
		    for (var i = 0; i < chkBoxCount; i++) 
		    {
		        if (form.objectId[i].checked) 
		        {
		            //it's checked so return true and exit
		            return true;
		        }
		    }
	    }
	    document.getElementById("objectIdError").style.display = "";
	    return false;
	}
		
	function validateForm() {
		var objectName = document.getElementById("objectName");
		var objectType = document.getElementById("objectType");
		
		var result = true;
		if (objectName.value == "") {
			document.getElementById("objectNameError").style.display = "";
			result = false;
		}
		
		if (objectType.value == "") {
			document.getElementById("objectTypeError").style.display = "";
			result = false;
		}
		return result;
	}

	function clearError(errorName) {
		document.getElementById(errorName + "Error").style.display = "none";
	}
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>