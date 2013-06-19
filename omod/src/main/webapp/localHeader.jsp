<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Entry Properties">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/properties") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/propertiesPage.form">
				<spring:message code="amrsmobileforms.properties"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="Manage Mobile Resources">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/mobileResources") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/mobileResources.list">
				<spring:message code="amrsmobileforms.mobileResources"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="Manage Economic Objects">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/economicObject") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/economicObject.form">
				<spring:message code="amrsmobileforms.economicObjects"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="Manage Economic Objects">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/householdMappings") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/householdMappings.list">
				<spring:message code="amrsmobileforms.householdMappings"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Errors">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/resolveError") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/resolveErrors.list">
				<spring:message code="amrsmobileforms.resolveErrors.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Entry Properties">
		<li <c:if test='<%= request.getRequestURI().contains("amrsmobileforms/syncLog") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/amrsmobileforms/syncLog.list">
				<spring:message code="amrsmobileforms.sync.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>