<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Entry Properties" otherwise="/login.htm" redirect="/module/amrsmobileforms/propertiesPage.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRAdministrationService.js" />

<style>
	span.saved { font-style: italic; color: green; display: none; }
</style>

<h2><spring:message code="amrsmobileforms.properties"/></h2>

<br />

<div><b class="boxHeader">System folders</b>
	<div class="box">
		<table cellpadding="4" cellspacing="0">
			<tr>
				<th><spring:message code="SystemInfo.name"/></th>
				<th><spring:message code="SystemInfo.value"/></th>
			</tr>
			<c:forEach items="${systemVars}" var="var" varStatus="status">
				<tr class="<c:choose><c:when test="${status.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
					<td>${var.key}</td>
					<td>${var.value}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
		
<br />

<div><b class="boxHeader">Other global properties</b>
	<div class="box">
		<table cellpadding="4" cellspacing="0">
			<tr>
				<td>HCT Identifier Type</td>
				<td id="hctIdentifierType">
					<form>
						<input type="hidden" name="property" value="amrsmobileforms.hctIdentifierType"/>
						<select name="value" onchange="enableSaveFor('hctIdentifierType')">
							<option value=""></option>
							<c:forEach items="${identifierTypes}" var="identifierType">
								<option value="${identifierType.id}" 
									<c:if test="${identifierType.id == hctIdentifierType}">selected</c:if>
									>${identifierType.name}</option>
							</c:forEach>
						</select>
						<input type="submit" name="save" onclick="return saveGPFor('hctIdentifierType')" value="save"/>
						<span class="saved">saved</span>
					</form>
				</td>
			</tr>
			<tr>
				<td>Phone Number Attribute Type</td>
				<td id="phonenumberAttributeType">
					<form>
						<input type="hidden" name="property" value="amrsmobileforms.phonenumberAttributeType"/>
						<select name="value" onchange="enableSaveFor('phonenumberAttributeType')">
							<option value=""></option>
							<c:forEach items="${attributeTypes}" var="attributeType">
								<option value="${attributeType.id}" 
									<c:if test="${attributeType.id == phonenumberAttributeType}">selected</c:if>
									>${attributeType.name}</option>
							</c:forEach>
						</select>
						<input type="submit" name="save" onclick="return saveGPFor('phonenumberAttributeType')" value="save"/>
						<span class="saved">saved</span>
					</form>
				</td>
			</tr>
		</table>
		<br />
	</div>
</div>

<script>
	
	function enableSaveFor(wrapper) {
		$j("#" + wrapper + " input[name=save]").fadeIn();
	}

	function saveGPFor(wrapper) {
		var property = $j("#" + wrapper + " input[name=property]").val();
		var value = $j("#" + wrapper + " select[name=value]").val();
		
		DWRAdministrationService.setGlobalProperty(property, value, function(){
			$j("#" + wrapper + " input[name=save]").fadeOut("fast", function(){
				$j("#" + wrapper + " span.saved").fadeIn("fast", function(){
					$j("#" + wrapper + " span.saved").fadeOut(2000);
				});
			});
		});
		
		return false;
	}

	$j(document).ready(function(){
		$j("input[name=save]").hide();
	});

</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>