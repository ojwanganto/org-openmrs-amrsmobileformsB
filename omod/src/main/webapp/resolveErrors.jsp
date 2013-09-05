<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Errors" otherwise="/login.htm"
                 redirect="/module/amrsmobileforms/resolveErrors.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js"/>
<openmrs:htmlInclude file="/scripts/dojoConfig.js"/>
<openmrs:htmlInclude file="/scripts/dojo/dojo.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAMRSMobileFormsService.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/css/smoothness/jquery-ui-1.8.16.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/css/dataTables_jui.css"/>


<h2><spring:message code="amrsmobileforms.resolveErrors.title"/></h2>
<style type="text/css">
    .tblformat tr:nth-child(odd) td {
        background-color: #eee;
    }

    .tblformat tr:nth-child(odd) th {
        background-color: #ddd;
    }

    .tblformat tr:nth-child(even) td {
        background-color: #d3d3d3;
    }

    .tblformat tr:nth-child(even) th {
        background-color: #bbb;
    }
</style>
<style type="text/css">
        /* comment and resolve buttons */
    button.action {
        border: 1px solid gray;
        font-size: .75em;
        color: black;
        width: 52px;
        margin: 2px;
        padding: 1px;
        cursor: pointer;
    }

    button.resolve {
        background-color: #E0E0F0;
    }

    button.comment {
        background-color: lightpink;
    }

        /* error table */
    #errors {
        margin: 1em;
    }

    #errors table {
        width: 100%;
    }

    #tools {
        margin: 1em;
    }

    .centered {
        text-align: center;
    }

        /* datatable stuff */
    .dataTables_info {
        font-weight: normal;
    }

    .dataTables_wrapper {
        padding-bottom: 0;
    }

    .ui-widget-header {
        font-weight: inherit;
    }

    .css_right {
        float: right;
    }

    .css_left {
        float: left;
    }

    .dataTables_length {
        width: auto;
    }
</style>

<script>
var eTable = null;


$j(document).ready(function () {

    // set up the error datatable

    if (eTable == null) {

        eTable = $j("#errorTable").dataTable({
            bAutoWidth: false,
            bDeferRender: true,
            bJQueryUI: true,
            bPaginate: true,
            sPaginationType: "full_numbers",
            aoColumnDefs: [
                { aTargets: ["_all"], bSortable: false },
                {
                    aTargets: [0],
                    sClass: "centered",
                    mData: null,
                    mRender: function (data, type, full) {
                        var id = full.id;
                        return '<input name="errorIds" type="checkbox" value="' + id + '"/>';
                    }
                },
                {
                    aTargets: [1],
                    sClass: "centered",
                    mData: null,
                    mRender: function (data, type, full) {
                        var id = full.id;
                        var out = "";
                        if (full.comment) {
                            out += '<button class="action resolve" errorId="' + id + '">Resolve</button>';
                        } else {
                            out += '<button class="action comment" errorId="' + id + '">Comment</button>';
                        }
                        return out;
                    }
                },
                { aTargets: [2], mData: "id" },
                { aTargets: [3], mData: "location"},
                { aTargets: [4], mData: "provider"},
                { aTargets: [5], mData: "error" },
                { aTargets: [6], mData: "errorDetails" },
                { aTargets: [7], mData: "formName" },
                { aTargets: [8], mData: "comment", sClass: "centered" }
            ],
            bProcessing: true,
            bServerSide: true,
            bStateSave: false,
            fnDrawCallback: function (oSettings) {
                if ($j("span.numSelected").html() == oSettings.fnRecordsDisplay()) {
                    $j("input[name=errorIds]").attr("checked", "checked");
                } else {
                    $j("span.numDisplayed").html(oSettings.fnRecordsDisplay());
                    $j("#selectAll").removeAttr("checked");
                    $j("input[name=errorIds]").removeAttr("checked");
                    updateNumSelected(0);
                }
            },
            sAjaxSource: "<openmrs:contextPath/>/module/amrsmobileforms/errorList.json"
        });

    }
    else {

        eTable.fnClearTable(0);
        eTable.fnDraw();

    }
    // set up the error datatable


    // click events for comment and resolve buttons


    $j("button.comment").live("click", function () {
        var errorId = $j(this).attr("errorId");
        // document.location = "resolveErrorComment.form?errorId=" + $j(this).attr("errorId");
        DWRAMRSMobileFormsService.populateCommentForm(errorId, function (data) {
            errObj = data[0];

            var tableExist = $j("#errorsummary");
            if (tableExist != null) {
                $j("#errorsummary").remove();
            }
            generate_table(errObj, "ttable", 1);
        });


        $j("#dialog-form").dialog({
            height: 600,
            width: 1000,
            modal: false,
            buttons: {
                "Submit Comment": function () {
                    var commentt = $j("#comment").val();

                    if (commentt != null) {
                        DWRAMRSMobileFormsService.saveComment(errorId, commentt, alertResult);
                    } else {
                        alert("Please the field for comment is empty!");
                        $j("#comment").focus();
                    }
                },
                close: closecFunction,
                Cancel: function () {
                    $j(this).dialog("close");
                }
            }
        });

        return false;
    });


    $j("button.resolve").live("click", function () {

        var errorId = $j(this).attr("errorId");

        var tableExist = $j("#resolveerror");
        if (tableExist != null) {
            $j("#resolveerror").remove();
        }

        DWRAMRSMobileFormsService.populateCommentForm(errorId, function (data) {
            var errObj = data[0];
            clearElements();
            generate_ResolveError_table(errObj);
        });

        $j("#resolveError").dialog({
            height: 600,
            width: 1000,
            modal: false,
            buttons: {
                "Resolve Error": function () {

                    var providerId = document.getElementById('selprovider').value;
                    var patientId = document.getElementById('selpatient').value;
                    var patientIdentifier = document.getElementById('patientIdentifier').value;
                    var birthDate = document.getElementById('birthDate').value;
                    var householdId = document.getElementById('householdId').value;
                    var householdIdentifier = document.getElementById('householdIdentifier').value;
                    var errorItemAction = getSelectedRadio();


                    if (errorItemAction != null) {

                        DWRAMRSMobileFormsService.resolveError(householdId, errorId, errorItemAction, birthDate, patientIdentifier, providerId, householdIdentifier,patientId, resolveErrorResult);
                    } else {
                        alert("Please select the action to take");
                    }
                },
                close: closeFunction,
                Cancel: function () {
                    $j(this).dialog("close");
                }
            }
        });

        return false;
    });

    // click event for selectAll checkbox
    $j("#selectAll").click(function () {
        if ($j(this).is(":checked")) {
            $j("input[name=errorIds]").attr("checked", "checked");
            updateNumSelected($j("span.numDisplayed").html());
        } else {
            $j("input[name=errorIds]").removeAttr("checked");
            updateNumSelected(0);
        }
    });

    // click event for individual checkboxes
    $j("input[name=errorIds]").live("click", function () {
        updateNumSelected();
    });

    // click event for reprocessAll button
    $j("#reprocessAll").click(function () {
        var url = "reprocessBatch.htm";
        // TODO use formdata or jquery to manipulate errorIds into something cool
        var params = [];
        $j("input[name=errorIds]:checked").each(function () {
            params.push("errorIds=" + $j(this).val());
        });
        if ($j("#selectAll").is(":checked")) {
            params.push("all=1");
        }
        params.push("query=" + $j("div.dataTables_filter input").val());
        document.location = url + "?" + params.join("&");
    });
});

function closecFunction() {
    $j("#dialog-form").dialog("close");
}
function closeFunction() {
    $j("#resolveError").dialog("close");
}
function refreshDataTable() {
    eTable.fnClearTable(0);
    eTable.fnDraw();
}

function resolveErrorResult(data) {

    var div = document.getElementById("openmrs_msg");

    var resultId = data[0];
    var resultMsg = data[1];

    if (resultId == 0) {
        div.style.display = "block";
        div.innerHTML = " <b>" + resultMsg + "</b>";
    } else if (resultId == 1) {
        closeFunction();
        div.style.display = "block";
        div.innerHTML = " <b>" + resultMsg + "</b>";
        refreshDataTable();
        //document.location.reload(true);
    } else if (resultId == 2) {
        closeFunction();
        div.style.display = "block";
        div.innerHTML = " <b>" + resultMsg + "</b>";
    } else {
        closeFunction();
        div.style.display = "block";
        div.innerHTML = " <b>" + resultMsg + "</b>";
        refreshDataTable();
    }
}

function alertResult(data) {

    var div = document.getElementById("openmrs_msg");

    div.style.display = "block";
    div.innerHTML = " <b>" + data + "</b>";

    if (data == "Comment saved successfully") {
        closecFunction();
        refreshDataTable();
    }
}

function buildRow(label, tdvalue) {

    var row = document.createElement("tr");
    var cell = document.createElement("th");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label + ": ");
    var cellval = document.createTextNode(tdvalue);
    cell.appendChild(celllabel);
    cell2.appendChild(cellval);
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
}

function buildRowWithElement(label, tdvalue) {

    var row = document.createElement("tr");
    var cell = document.createElement("th");
    var cell2 = document.createElement("td");
    var div = document.createElement("div");
    div.setAttribute('style', 'height: 40px; overflow-y: scroll; border: 1px solid #BBB;');

    var celllabel = document.createTextNode(label + ": ");
    var cellval = document.createTextNode(tdvalue);
    div.appendChild(cellval);
    cell.appendChild(celllabel);
    cell2.appendChild(div);
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
}

function buildTextArea(label, id) {
    var row = document.createElement("tr");
    var cell = document.createElement("th");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label + ": ");

    var textarea = document.createElement("textarea");
    textarea.setAttribute('cols', '120');
    textarea.setAttribute('rows', '3');
    textarea.setAttribute('id', id);

    cell.appendChild(celllabel);
    cell2.appendChild(textarea);

    row.appendChild(cell);
    row.appendChild(cell2);

    return row;
}

function buildFormViewRow(label,id,formxml) {
    var row = document.createElement("tr");
    var cell = document.createElement("th");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label + ": ");

    var button = document.createElement("input");
    button.setAttribute("type", "button");
    button.setAttribute("value", "Show Form");
    button.setAttribute("id",id);
    button.setAttribute("onclick",ShowXformDiv());



    cell.appendChild(celllabel);
    cell2.appendChild(button);

    row.appendChild(cell);
    row.appendChild(cell2);

    return row;
}

function fillFormXmlDiv(xmldata){
    alert("JJJJJJJJJJJJJJJJJJJJJJJJJJ") ;
    $j("#formviewdialog").html(xmldata);

        $j("#formviewdialog").dialog({
            height: 600,
            width: 1000,

            modal: false,
            buttons: {
                "Close": function () {

                    alert('Closed') ;
                },
                close: closeFunction,
                Cancel: function () {
                    $j(this).dialog("close");
                }
            }
        });




}
function ShowXformDiv() {

    alert("shown")
}
///Add jquery
$j("#btnshowformxml").on("click", function (e) {
    e.preventDefault();
    // $j("#formviewdialog").html(xmldata);
    alert('Jkied') ;
    $('#formviewdialog').modal('show');
    $j("#formviewdialog").dialog({
        height: 600,
        width: 1000,

        modal: false,
        buttons: {
            "Close": function () {

                alert('Closed') ;
            },
            close: closeFunction,
            Cancel: function () {
                $j(this).dialog("close");
            }
        }
    });


});


//
/* Displays a summary dialog window with error details plus a provision to comment on an error
 */

function generate_table(data, bodyDiv, option) {

    var body = document.getElementById(bodyDiv);

    var tbl = document.createElement("table");
    tbl.setAttribute('width', '100%');
    tbl.setAttribute('class', 'tblformat');
    tbl.setAttribute('id', 'errorsummary');

    var tblBody = document.createElement("tbody");

    var pname = buildRow("Person Name", data.name);
    var pid = buildRow("Person Identifier", data.identifier);
    var gender = buildRow("Gender", data.gender);
    var location = buildRow("Location", data.locationId);

    var edate = buildRow("Encounter Date", data.encounterDate);
    var modelname = buildRow("Form Name", data.formModelName);
    var formid = buildRow("Form ID", data.formId);
    var errorid = buildRow("Error ID", data.mobileFormEntryErrorId);
    var datecreated = buildRow("Date Created", data.dateCreated);
    var error = buildRow("Error", data.error);

    var totalHousehold = buildRow("Total Household", data.totalHousehold);
    var totalEligible = buildRow("Total Eligible", data.totalEligible);
    var providerId = buildRow("Provider Id", data.providerId);

    var errdetails = buildRowWithElement("Error Details", data.errorDetails);

    var formname = buildRowWithFormXml("XML Form", data.formName);

    var comment = buildTextArea("Comment", "comment");



    tblBody.appendChild(pname);
    tblBody.appendChild(pid);
    tblBody.appendChild(gender);
    tblBody.appendChild(location);
    tblBody.appendChild(edate);
    tblBody.appendChild(modelname);
    tblBody.appendChild(formid);
    tblBody.appendChild(errorid);
    tblBody.appendChild(datecreated);
    tblBody.appendChild(error);
    tblBody.appendChild(totalHousehold);
    tblBody.appendChild(totalEligible);
    tblBody.appendChild(providerId);
    tblBody.appendChild(errdetails);


    tblBody.appendChild(formname);

    tblBody.appendChild(comment);


    tbl.appendChild(tblBody);

    body.appendChild(tbl);
    tbl.setAttribute("border", "1");

    //---------------------------------------

}

/* Displays a dialog window for resolving errors. It provides relevant options for resolving errors*/

function generate_ResolveError_table(data) {


    var body = document.getElementById("resolveErrorTable");

    var tbl = document.createElement("table");
    tbl.setAttribute('width', '100%');
    tbl.setAttribute('id', 'resolveerror');
    tbl.setAttribute('class', 'tblformat');

    var tblBody = document.createElement("tbody");

    var comment = buildRow("Comment", data.comment);
    var commentedBy = buildRow("Commented By", data.commentedBy.username);
    var dateCommented = buildRow("Date Commented", data.dateCommented);

    var pname = buildRow("Person Name", data.name);
    var pid = buildRow("Person Identifier", data.identifier);
    var gender = buildRow("Gender", data.gender);
    var location = buildRow("Location", data.locationId);

    var encdate = buildRow("Encounter Date", data.encounterDate);
    var formmodelname = buildRow("Form Name", data.formModelName);
    var formid = buildRow("Form ID", data.formId);
    var errorid = buildRow("Error ID", data.mobileFormEntryErrorId);
    var datecreated = buildRow("Date Created", data.dateCreated);
    var err = buildRow("Error", data.error);

    var totalhousehold = buildRow("Total Household", data.totalHousehold);
    var totaleligible = buildRow("Total Eligible", data.totalEligible);
    var providerId = buildRow("Provider Id", data.providerId);
    var errdetails = buildRowWithElement("Error Details", data.errorDetails);
    var formname = buildRowWithFormXml("XML Form", data.formName);

    tblBody.appendChild(comment);
    tblBody.appendChild(commentedBy);
    tblBody.appendChild(dateCommented);
    tblBody.appendChild(pname);
    tblBody.appendChild(pid);
    tblBody.appendChild(gender);
    tblBody.appendChild(location);
    tblBody.appendChild(encdate);
    tblBody.appendChild(formmodelname);
    tblBody.appendChild(formid);
    tblBody.appendChild(errorid);
    tblBody.appendChild(datecreated);
    tblBody.appendChild(err);
    tblBody.appendChild(totalhousehold);
    tblBody.appendChild(totaleligible);
    tblBody.appendChild(providerId);
    tblBody.appendChild(errdetails);
    tblBody.appendChild(formname);
    tbl.appendChild(tblBody);
    body.appendChild(tbl);
    tbl.setAttribute("border", "0");
}

function buildRowWithFormXml(label, tdvalue) {

    var row = document.createElement("tr");
    var cell = document.createElement("th");
    var cell2 = document.createElement("td");
    var div = document.createElement("div");
    div.setAttribute('style', 'height: 200px; overflow-y: scroll; border: 1px solid #BBB;');

    var celllabel = document.createTextNode(label + ": ");
    var cellval = document.createTextNode(tdvalue);
    div.appendChild(cellval);
    cell.appendChild(celllabel);
    cell2.appendChild(div);
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
}

function getSelectedRadio() {
    var selOpt = document.getElementsByName('errorItemAction');

    for (var i = 0; i < selOpt.length; i++) {
        if (selOpt[i].checked) {
            return selOpt[i].value;
        }
    }
    return null;
}

// sets the value of the hidden field (provider id)
function setErrorAction(index, data) {
    document.getElementById("selprovider").value = data.personId;
}

function setPatientField(index, data) {
    document.getElementById("selpatient").value = data.patientId;
}

/**
 * updates the numSelected element with a specified amount, with some after-effects
 **/
function updateNumSelected(amount) {
    var numSelected = (typeof amount === "undefined") ? $j("input[name=errorIds]:checked").length : amount;

    $j("span.numSelected").html(numSelected);

    if (numSelected > 0)
        $j("#reprocessAll").removeAttr("disabled");
    else
        $j("#reprocessAll").attr("disabled", "disabled");

    if (numSelected != $j("span.numDisplayed").html())
        $j("#selectAll").removeAttr("checked");
}


function doSelectionHandler(index, data) {
    document.getElementById("selprovider").value = data.personId;
}

function clearElements(){

    var textElements = document.getElementsByTagName("input");
    for (var i=0; i < textElements.length; i++) {
        if (textElements[i].type == 'text') {
            textElements[i].value = '';
        }
    }

    var radioElements = document.getElementsByName("errorItemAction");
    var radioLength = radioElements.length;
    var lastRadioPosition = radioLength - 1;

    for (var i=0; i < radioElements.length; i++) {

        if (i != lastRadioPosition) {

            radioElements[i].checked = false;
        }
        radioElements[i].checked = true;
    }




}


</script>

<div class="box" id="dialog-form" title="Error Summary" style="display:none;">
    <div id="ttable"></div>

</div>


<div class="box" id="formviewdialog" title="Submitted Form" style="display:none;">
</div>
<div class="box" id="resolveError" title="Error Resolution Dialog" style="display:none;">

    <div id="resolveErrorTable">

    </div>

    <div>
        <table>
            <tr>
                <td>
                    <!-- Pick a provider -->
                    <input type="radio" name="errorItemAction" value="linkProvider"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.providerLink"/>
                </td>
                <td>
                    <openmrs_tag:userField formFieldName="providerId"
                                           searchLabelCode="amrsmobileforms.resolveErrors.action.findProvider"
                                           initialValue="" callback="setErrorAction"/>
                </td>
            </tr>
            <tr>
                <td>
                    <!-- Pick a patient -->
                    <input type="radio" name="errorItemAction" value="linkPatient"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.patientLink"/>
                </td>
                <td>
                    <openmrs_tag:patientField formFieldName="patientId"
                                              searchLabelCode="amrsmobileforms.resolveErrors.action.findPatient"
                                              initialValue="" callback="setPatientField"/>
                </td>
            </tr>
            <tr>
                <td>
                    <!-- Have the machinery create a new patient -->
                    <input type="radio" name="errorItemAction" value="createPatient"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.createPatient"/>
                </td>
                <td/>
            </tr>
            <tr>
                <td>
                    <!-- Assign a new patient identifier-->
                    <input type="radio" name="errorItemAction" value="newIdentifier"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.newIdentifier"/>:
                </td>
                <td>
                    <input type="text" id="patientIdentifier" name="patientIdentifier"/>
                </td>
            </tr>
            <tr>
                <td>
                    <!-- Assign a birth date to patient -->
                    <input type="radio" name="errorItemAction" value="assignBirthdate"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.assignBirthDate"/>:
                </td>
                <td>
                    <input type="text" name="birthDate" id="birthDate" onClick="showCalendar(this)"/>
                </td>
            </tr>

            <tr>
                <td>
                    <!-- Link patient to household -->
                    <input type="radio" name="errorItemAction" value="linkHousehold"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.createLink"/>:
                </td>
                <td>
                    <input type="text" id="householdId" name="householdId"/>
                </td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="errorItemAction" value="newHousehold"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.newHouseholdIdentifier"/>:
                </td>
                <td>
                    <input type="text" id="householdIdentifier" name="householdIdentifier"/>
                </td>
            </tr>
            <tr>
                <td>
                    <!-- This is an invalid comment, delete it -->
                    <input type="radio" name="errorItemAction" value="deleteComment"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.deleteComment"/>
                </td>
                <td>

                </td>
            </tr>
            <tr>
                <td>
                    <!-- This is an invalid error, delete it -->
                    <input type="radio" name="errorItemAction" value="deleteError"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.deleteError"/>
                </td>
                <td>

                </td>
            </tr>

            <tr>
                <td>
                    <!-- I don't want to do anything to this one now -->
                    <input type="radio" name="errorItemAction" value="noChange" checked="checked"/>
                    <spring:message code="amrsmobileforms.resolveErrors.action.noChange"/>
                </td>
                <td>
                    <input type="hidden" id="selprovider" value=""/>
                    <input type="hidden" id="selpatient" value=""/>

                </td>
            </tr>

        </table>
    </div>
</div>

<div id="openmrs_msg"></div>

<div><b class="boxHeader">Mobile Form Entry Errors</b>

    <div class="box">
        <div id="tools">
            <input id="selectAll" type="checkbox"/> Select All <span class="numDisplayed"></span> in Search Results
            (including unseen)
            &nbsp; &nbsp;
            <button id="reprocessAll" disabled>Reprocess <span class="numSelected">0</span> Selected Errors</button>
        </div>
        <div id="errors">
            <form method="post">
                <table id="errorTable" cellpadding="8" cellspacing="0">
                    <thead>
                    <tr>
                        <th>Select</th>
                        <th>Action</th>
                        <th>ID</th>
                        <th>Location</th>
                        <th>Provider</th>
                        <th>Error</th>
                        <th>Error Details</th>
                        <th>Form Name</th>
                        <th>Comment</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
