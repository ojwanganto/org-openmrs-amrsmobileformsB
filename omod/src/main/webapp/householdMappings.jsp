<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Economic Objects" otherwise="/login.htm" redirect="/module/amrsmobileforms/householdMappings.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRAdministrationService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRConceptService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRAMRSMobileFormsService.js" />
<openmrs:htmlInclude file="/scripts/jquery/autocomplete/OpenmrsAutoComplete.js" />

<style>
	span.saved { font-style: italic; color: green; display: none; }
</style>

<h2><spring:message code="amrsmobileforms.householdMappings"/></h2>

<div><b class="boxHeader">Economic Object Concept Mappings</b>
	<div class="box">
		<c:if test="${fn:length(economicMaps) > 0}">
			<table cellpadding="4" cellspacing="0">
				<tr>
					<th>Economic Object</th>
					<th>Answer Datatype</th>
					<th>Associated Concept</th>
				</tr>
				<c:forEach items="${economicMaps}" var="mapping">
					<tr>
						<td>${mapping.economic.objectName}</td>
						<td>${mapping.economic.objectType}</td>
						<td id="wrapperForEconomic${mapping.economic.id}">
							<form class="conceptForEconomic">
								<input type="hidden" name="id" value="${mapping.id}"/>
								<input type="hidden" name="economicId" value="${mapping.economic.id}"/>
								<input type="hidden" name="conceptId" value="<c:if test="${mapping.concept != null}">${mapping.concept.id}</c:if>"/>
								<div class="link">
									<a href="#" target="new">None</a>
									<input type="submit" onclick="return changeConceptForEconomic(${mapping.economic.id})" 
											value="change"/>
									<span class="saved">saved</span>
								</div>
								<div class="change">
									<input name="conceptPicker" id="conceptForEconomic${mapping.economic.id}" 
											placeholder="begin typing ..." value=""/>
									<input type="submit" name="cancel" 
										   onclick="return cancelLookup('${mapping.economic.id}')" value="cancel"/>
								</div>
							</form>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<br />
	</div>
</div>

<br />

<div><b class="boxHeader">Global Property Mappings</b>
	<div class="box">
		<c:if test="${fn:length(gpMaps) > 0}">
			<table cellpadding="4" cellspacing="0">
				<tr>
					<th>Global Property</th>
					<th>Associated Concept</th>
				</tr>
				<c:forEach items="${gpMaps}" var="mapping">
					<c:set var="property" value="${fn:replace(mapping.key,'.','-')}"/>
					<tr>
						<td>${mapping.key}</td>
						<td id="wrapperForGP${property}">
							<form class="conceptForGP">
								<input type="hidden" name="property" value="${mapping.key}"/>
								<input type="hidden" name="value" value="${mapping.value}"/>
								<div class="link">
									<a href="#" target="new">None</a>
									<input type="submit" onclick="return changeConceptForGP('${property}')" 
											value="change"/>
									<span class="saved">saved</span>
								</div>
								<div class="change">
									<input name="conceptPicker" id="conceptForGP${property}" 
											placeholder="begin typing ..." value=""/>
									<input type="submit" name="cancel" 
										   onclick="return cancelLookup('${property}')" value="cancel"/>
								</div>
							</form>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<br />
	</div>
</div>

<br />

<div><b class="boxHeader">Household Metadata Mappings</b>
	<div class="box">
		<table cellpadding="4" cellspacing="0">
			<tr>
				<td>Default Household Definition</td>
				<td id="definition">
					<form>
						<input type="hidden" name="property" value="amrsmobileforms.defaultHouseholdDefinition"/>
						<select name="value" onchange="enableSaveFor('definition')">
							<option value=""></option>
							<c:forEach items="${householdDefinitions}" var="definition">
								<option value="${definition.id}" 
									<c:if test="${definition.id == defaultHouseholdDefinition}">selected</c:if>
									>${definition.householdDefinitionsCode}</option>
							</c:forEach>
						</select>
						<input type="submit" name="save" onclick="return saveGPFor('definition')" value="save"/>
						<span class="saved">saved</span>
					</form>
				</td>
			</tr>
			<tr>
				<td>Default Household Encounter Type</td>
				<td id="encounterType">
					<form>
						<input type="hidden" name="property" value="amrsmobileforms.defaultHouseholdEncounterType"/>
						<select name="value" onchange="enableSaveFor('encounterType')">
							<option value=""></option>
							<c:forEach items="${householdEncounterTypes}" var="encounterType">
								<option value="${encounterType.id}" 
									<c:if test="${encounterType.id == defaultHouseholdEncounterType}">selected</c:if>
									>${encounterType.name}</option>
							</c:forEach>
						</select>
						<input type="submit" name="save" onclick="return saveGPFor('encounterType')" value="save"/>
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

	function cancelLookup(el) {
		
	}

	function changeConceptForEconomic(economicId) {
		var wrapper = "#wrapperForEconomic" + economicId;
		var element = "conceptForEconomic" + economicId;
		var ecmId = $j(wrapper).find("input[name=id]").val();
		
		$j(wrapper).find("div.link").fadeOut("fast", function(){
			$j(wrapper).find("div.change").fadeIn("fast", function(){
				$j("#" + element).focus();
			});
		});
		
		var callback = new CreateCallback();
		var getConcept = new AutoComplete(element, callback.conceptCallback(), {
			select: function(event, ui) {
				var conceptId = ui.item.object.conceptId;
				$j(wrapper).find("input[name=concept]").val(conceptId);
				DWRAMRSMobileFormsService.saveEconomicConceptMap(ecmId, economicId, conceptId, function(ecm){
					$j(wrapper).find("input[name=id]").val(ecm.id);
					updateLink(economicId, conceptId);
					$j(wrapper).find("span.saved").show();
					$j(wrapper).find("div.change").fadeOut("fast", function(){
						$j(wrapper).find("div.link").fadeIn("fast", function(){
							$j(wrapper).find("span.saved").fadeOut(2000);
						});
					});
				});
			}
		});
		
		return false;
	}
	
	function changeConceptForGP(gp) {
		var wrapper = "#wrapperForGP" + gp;
		var element = "conceptForGP" + gp;
		var property = $j(wrapper).find("input[name=property]").val();
		
		$j(wrapper).find("div.link").fadeOut("fast", function(){
			$j(wrapper).find("div.change").fadeIn("fast", function(){
				$j("#" + element).focus();
			});
		});
		
		var callback = new CreateCallback();
		var getConcept = new AutoComplete(element, callback.conceptCallback(), {
			select: function(event, ui) {
				var conceptId = ui.item.object.conceptId;
				$j(wrapper).find("input[name=value]").val(conceptId);
				DWRAdministrationService.setGlobalProperty(property, conceptId, function(){
					updateGPLink(gp, conceptId);
					$j(wrapper).find("span.saved").show();
					$j(wrapper).find("div.change").fadeOut("fast", function(){
						$j(wrapper).find("div.link").fadeIn("fast", function(){
							$j(wrapper).find("span.saved").fadeOut(2000);
						});
					});
				});
			}
		});
		
		return false;
	}

	function updateLink(economicId, conceptId) {
		var parentName = "#wrapperForEconomic" + economicId;
		$j(parentName).find("div.link a").attr("href", "<openmrs:contextPath/>/dictionary/concept.htm?conceptId=" + conceptId);
		DWRConceptService.getConcept(conceptId, function(concept){
			$j(parentName).find("div.link a").html(concept.name);
		});
	}
	
	function updateGPLink(property, value) {
		var parentName = "#wrapperForGP" + property;
		$j(parentName).find("div.link a").attr("href", "<openmrs:contextPath/>/dictionary/concept.htm?conceptId=" + value);
		DWRConceptService.getConcept(value, function(concept){
			$j(parentName).find("div.link a").html(concept.name);
		});
	}
	
	$j(document).ready(function(){

		$j("div.change").hide();
		$j("input[name=save]").hide();

		<c:forEach items="${economicMaps}" var="mapping">
			<c:if test="${not empty mapping.concept}">
					updateLink(${mapping.economic.id}, ${mapping.concept.id});
			</c:if>
		</c:forEach>
			
		<c:forEach items="${gpMaps}" var="mapping">
			<c:if test="${not empty mapping.value}">
					updateGPLink('${fn:replace(mapping.key, '.', '-')}', ${mapping.value});
			</c:if>
		</c:forEach>
	});

</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>