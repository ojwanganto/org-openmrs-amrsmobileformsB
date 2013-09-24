<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form HouseHolds" otherwise="/login.htm"
                 redirect="/module/amrsmobileforms/addressView.list"/>

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


<h2><spring:message code="amrsmobileforms.householdLocAddresses.title"/></h2>
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

        eTable = $j("#householdTable").dataTable({
            bAutoWidth: false,
            bDeferRender: true,
            bJQueryUI: true,
            bPaginate: true,
            sPaginationType: "full_numbers",
            aoColumnDefs: [
                { aTargets: ["_all"], bSortable: false },
                { aTargets: [0], mData: "household_identifier"},
                { aTargets: [1], mData: "district" },
                { aTargets: [2], mData: "location" },
                { aTargets: [3], mData: "sublocation"},
                { aTargets: [4], mData: "longitude"},
                { aTargets: [5], mData: "latitude"}



            ],
            bProcessing: true,
            bServerSide: true,
            bStateSave: false,
            fnDrawCallback: function (oSettings) {
                if ($j("span.numSelected").html() == oSettings.fnRecordsDisplay()) {
                    $j("input[name=houseHoldIds]").attr("checked", "checked");
                } else {
                    $j("span.numDisplayed").html(oSettings.fnRecordsDisplay());
                    $j("#selectAll").removeAttr("checked");
                    $j("input[name=houseHoldIds]").removeAttr("checked");
                   // updateNumSelected(0);
                }
            },
            sAjaxSource: "<openmrs:contextPath/>/module/amrsmobileforms/addresses.json"
        });

    }
    else {

        eTable.fnClearTable(0);
        eTable.fnDraw();

    }



    //   Download csv
    $j('#csv').click(function() {
        var searchValues=getSearchDetails();

        window.open("addressdownload.htm?search=" + searchValues, 'Download csv');
        return false;
    });
    text


});

  function getSearchDetails(){
      var textElements = document.getElementsByTagName("input");
      var searchval;
      for (var i=0; i < textElements.length; i++) {
          if (textElements[i].type == 'text') {
              searchval=textElements[i].value;
          }
      }
      return searchval;
  }
</script>

<div id="openmrs_msg"></div>

<div><b class="boxHeader">Mobile Form Entry Households Location</b>

    <div class="box">
        <div id="tools">
            <input type="button" value="Download CSV" id="csv" />
        </div>
        <div id="errors">
            <form method="post">
                <table id="householdTable" cellpadding="8" cellspacing="0">
                    <thead>
                    <tr>

                        <th>Household Identifier</th>
                        <th>District</th>
                        <th>Location</th>
                        <th>Sub Location</th>
                        <th>Longitude</th>
                        <th>Latitude</th>
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