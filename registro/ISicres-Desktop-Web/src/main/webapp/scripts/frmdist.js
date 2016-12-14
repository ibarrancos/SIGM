var	TYPE_DISTR_ENTRADA = "1";
var TYPE_DISTR_SALIDA = "2";

var STATE_DISTR_PENDIENTE = "1";
var STATE_DISTR_ACEPTADO = "2";
var STATE_DISTR_ARCHIVADO = "3";
var STATE_DISTR_RECHAZADO = "4";

var g_StateSelected = 0;


function InitFrmDist()
{
	var select = document.getElementById("select");

	document.getElementById("distWhere").value = select.getAttribute("FieldName") + "=" + select.value;
}


function DoOnLoad(inicio)
{
	var select = top.Main.Distr.document.getElementById("select");
	var selectTypeDistr = top.Main.Distr.document.getElementById("selectTypeDistr");
	var oForm = top.Main.Distr.document.getElementById("formDist");
	var frmMainDtr = top.Main.Distr.document.getElementById("mainDtr");

	top.Main.Distr.document.getElementById("checkSel").checked = false;
	top.Main.Distr.document.getElementById("checkSel").disabled = true;

	top.Main.Distr.document.getElementById("checkDesSel").checked = false;
	top.Main.Distr.document.getElementById("checkDesSel").disabled = true;

	top.Main.Distr.document.body.style.cursor = "wait";

	if (frmMainDtr.contentWindow.document.body !== null){
		frmMainDtr.contentWindow.document.body.style.cursor = "wait";
		frmMainDtr.contentWindow.document.body.innerHTML = "";
	}

	g_StateSelected = select.selectedIndex;

	oForm.action = top.g_URL + "/distribution.jsp?SessionPId=" + top.g_SessionPId + "&InitValue=" + inicio + "&TypeDistr=" + selectTypeDistr.value.toString();
	oForm.submit();
}


function ChangeOption(combo)
{
	var select = document.getElementById("select");
	var bandeja = document.getElementById("selectTypeDistr");
	var doLoad = false;

	if (combo == bandeja){
		if (select.value == "0"){
			select.selectedIndex = 0;
		}

		doLoad = true;
	}

	if (combo == select){
		if (select.value != "0"){
			doLoad = true;
		}
		else{
			Search(document.getElementById("Buscar"));
		}
	}

	if (doLoad == true){
		document.getElementById("distWhere").value = select.getAttribute("FieldName") + "=" + select.value;
		document.getElementById("regWhere").value = "";

		DisabledToolbar();
		DoOnLoad(0);
	}
}


function EnabledToolbar()
{
	top.Main.Distr.document.getElementById("Back").className = "Options";
    top.Main.Distr.document.getElementById("Refrescar").className = "Options";
    top.Main.Distr.document.getElementById("Buscar").className = "Options";
    top.Main.Distr.document.getElementById("select").disabled = false;
    top.Main.Distr.document.getElementById("selectTypeDistr").disabled = false;
    top.Main.Distr.document.body.style.cursor = "default";
}


function DisabledToolbar()
{
	top.Main.Distr.document.getElementById("Back").className = "SubOptionsDisabled";
    top.Main.Distr.document.getElementById("Refrescar").className = "SubOptionsDisabled";
    top.Main.Distr.document.getElementById("Buscar").className = "SubOptionsDisabled";
    top.Main.Distr.document.getElementById("Destino").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Redistribuir").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Rechazar").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Propiedades").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Comentario").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Aceptar").className = "SubOptionsDisabled2";
    top.Main.Distr.document.getElementById("Archivar").className = "SubOptionsDisabled2";

    top.Main.Distr.document.getElementById("select").disabled = true;
    top.Main.Distr.document.getElementById("selectTypeDistr").disabled = true;

	top.Main.Distr.document.getElementById("imgFirst").disabled = true;
	top.Main.Distr.document.getElementById("imgPrev").disabled = true;
	top.Main.Distr.document.getElementById("imgNext").disabled = true;
	top.Main.Distr.document.getElementById("imgLast").disabled = true;
}


function BackToQry(oField)
{
	if (oField.className != "SubOptionsDisabled") {
		var select = document.getElementById("select");

		if (select.options[select.selectedIndex].value == "0"){
			select.selectedIndex = 0;
			ChangeOption(select);
		}

	    top.Main.Distr.mainDtr.document.body.innerHTML = "";
        top.ShowQuery();
    }
}


function Refresh(oField)
{
	if (oField.className != "SubOptionsDisabled") {
		top.Main.Distr.document.getElementById("orderDistribution").value = "";
		DisabledToolbar();
		DoOnLoad(0);
	}
}

// deshabilita las checkbox
function DisabledCheckBox()
{
	document.getElementById("checkSel").checked = false;
	document.getElementById("checkSel").disabled = true;

	document.getElementById("checkDesSel").checked = false;
	document.getElementById("checkDesSel").disabled = true;

	for (var ii=0; ii < top.Main.Distr.mainDtr.document.getElementsByTagName("INPUT").length; ii++) {
		top.Main.Distr.mainDtr.document.getElementsByTagName("INPUT")[ii].disabled = true;
    }
}


function ReDistr(strIdDest)
{
	var val =GetValuesCheck();
    var strRet = "";
	var select = document.getElementById("select");
	var selectTypeDistr = document.getElementById("selectTypeDistr");

    if (val != "-1"){

		if (val == "") {
			alert ( top.GetIdsLan( "IDS_ERRORSELUN" ) );
        }
        else {

			var iInitValue = getInitValue();

            strRet = top.g_URL + "/dtrchange.jsp?SessionPId=" + top.g_SessionPId +
				"&IdDest=" + strIdDest +
                "&State=" + select.value + val +
                "&InitValue=" + iInitValue.toString() +
                "&TypeDistr=" + selectTypeDistr.value.toString();
		}
     }

     return strRet;
}


function FirstValues(obj)
{
	DisabledToolbar();
	DoOnLoad(0);
}


function PrevValues(obj)
{
	var dataDoc = top.Main.Distr.mainDtr.document;
	var inicio = parseInt(top.GetInnerText(dataDoc.getElementById("Init")), 10);
	var paso = parseInt(top.GetInnerText(dataDoc.getElementById("Paso")), 10);

	inicio = inicio - paso - 1;

	if (inicio < 0){inicio=0;}

	DisabledToolbar();
	DoOnLoad(inicio);
}


function NextValues(obj)
{
	var dataDoc = top.Main.Distr.mainDtr.document;
	var inicio = parseInt(top.GetInnerText(dataDoc.getElementById("Init")), 10);
	var paso = parseInt(top.GetInnerText(dataDoc.getElementById("Paso")), 10);
	var total = parseInt(top.GetInnerText(dataDoc.getElementById("Total")), 10);

	inicio = inicio + paso - 1;

	if (inicio > total){inicio = (total % paso) - 1;}

    DisabledToolbar();
	DoOnLoad(inicio);
}


function LastValues(obj)
{
	var inicio = 0;
	var dataDoc = top.Main.Distr.mainDtr.document;
	var paso = parseInt(top.GetInnerText(dataDoc.getElementById("Paso")), 10);
	var total = parseInt(top.GetInnerText(dataDoc.getElementById("Total")), 10);
	var iNumRows = total % paso;

    if (iNumRows == 0) {
		if (total - paso > 0) {
			inicio = total - paso;
		}
	}
	else {
		inicio = total - iNumRows;
	}

	DisabledToolbar();
	DoOnLoad(inicio);
}

function Over(obj)
{
	if ((obj.className != "SubOptionsDisabled") &&  (obj.className != "SubOptionsDisabled2")) {
		obj.className="SubOptionsOver2";
	}
}


function Out(obj)
{
	if ((obj.className != "SubOptionsDisabled") && (obj.className != "SubOptionsDisabled2")) {
		obj.className="SubOptions2";
	}
}

function CheckAllSel(){
	document.getElementById("checkDesSel").checked=false;
	CheckAll(document.getElementById("checkSel"))
}

function CheckDesAllSel(){
	document.getElementById("checkSel").checked=false;
	CheckAll(document.getElementById("checkSel"))
	document.getElementById("checkDesSel").checked=false;
}

function CheckAll(Obj)
{
	var bHasRech = true;
	var ExistS = false;
	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var SelAll = document.getElementById("SelAll");

	if (checks.length){
		for (var i = 0; i < checks.length; i++){
	   		checks[i].checked = Obj.checked;

	   		if (checks[i].getAttribute("State") != top.IDS_DISTR_RECH){
	   			bHasRech = false;
	   		}

	   		if (checks[i].getAttribute("DistType") == "0")	{
	   			ExistS = true;
	   		}
		}
	}
	else
	{
		document.getElementById("checkrow").checked = Obj.checked;

   		if (document.getElementById("checkrow").getAttribute("State") != top.IDS_DISTR_RECH){
			bHasRech = false;
		}

		if (document.getElementById("checkrow").getAttribute("DistType") == "0"){
			ExistS = true;
		}
	}

	VerifyMenu();
}


function SetNavegador(Init, End, Total)
{
	var PrepDe = top.Main.Distr.document.getElementById("lbPrepDe");
	var imgFirst = top.Main.Distr.document.getElementById("imgFirst");
	var imgPrev = top.Main.Distr.document.getElementById("imgPrev");
	var imgNext = top.Main.Distr.document.getElementById("imgNext");
	var imgLast = top.Main.Distr.document.getElementById("imgLast");
	var SelAll = top.Main.Distr.document.getElementById("SelAll");
	var checkAll = top.Main.Distr.document.getElementById("checkSel");
	var checkDesAll = top.Main.Distr.document.getElementById("checkDesSel");
	var rango = Init.toString() + " - " + End.toString() + " " + top.GetIdsLan("IDS_PREPDE") + " " + Total.toString();

	top.SetInnerText(SelAll, top.GetIdsLan( "IDS_OPCSELECT" ));
	checkAll.checked = false;
	checkAll.disabled = (Total == 0);
	checkDesAll.disabled = (Total == 0);

	if (Total > 0){
		top.SetInnerText(PrepDe,  rango);
		PrepDe.style.visibility = "visible";

		if (Init == 1){
			imgFirst.style.visibility = "hidden";
			imgPrev.style.visibility = "hidden";
		}
		else {
			imgFirst.style.visibility = "visible";
			imgPrev.style.visibility = "visible";
		}

		if (Total == End) {
			imgNext.style.visibility = "hidden";
			imgLast.style.visibility = "hidden";
		}
		else {
			imgNext.style.visibility = "visible";
			imgLast.style.visibility = "visible";
		}
	}
	else {
		PrepDe.style.visibility = "hidden";
		imgFirst.style.visibility = "hidden";
		imgPrev.style.visibility = "hidden";
		imgNext.style.visibility = "hidden";
		imgLast.style.visibility = "hidden";
	}

	imgFirst.disabled = false;
	imgPrev.disabled = false;
	imgNext.disabled = false;
	imgLast.disabled = false;
}


function ShowRemarks(opcion)
{
	//if (opcion.className == "SubOptionsDisabled2"){return;}

	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var msg = "";
	var caseSensitive = "";
	var anyChecked = false;

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if (checks[i].checked == true) {
				msg = checks[i].Remarks;
				caseSensitive = checks[i].CaseSensitive;
				anyChecked = true;
				break;
			}
		}
	}

	if (anyChecked){

		var isAceptar = 1;
		if (opcion.className == "SubOptionsDisabled2"){
			isAceptar = 0;
			if ((msg == null || msg == "")){
				return;
			}
		}

		var resp = top.Main.Distr.mainDtr.ShowRemarksEx(msg, isAceptar, caseSensitive);

		if (opcion.className == "SubOptionsDisabled2"){
			return;
		}

		if ((resp != "") && (resp != null)){
			var URL = top.g_URL + "/saveremarks.jsp?SessionPId=" + top.g_SessionPId
				+ "&Id=" + checks[i].value;
			var Data = "Remarks=" + resp;

			document.body.style.cursor = "wait";

			top.XMLHTTPRequestPost(URL, ResponseSaveRemarks, true, Data)
		}
	}
}


function ShowHistoryDistribution(idFolder, idBook){

	window.open(top.g_URL + "/gethistdistwithremark.jsp?SessionPId=" + top.g_SessionPId
			+ "&FolderId=" + idFolder + "&ArchiveId=" + idBook, "HistoryDistribution",
			"height=500px, width=950px, scrollbars=auto, resizable=yes, location=no",true);
}

function ResponseSaveRemarks()
{
	if (top.g_oXMLHTTPRequest.readyState != 4){
		return;
	}

	if (top.g_oXMLHTTPRequest.status != 200){
		alert(top.g_oXMLHTTPRequest.statusText + " (" + top.g_oXMLHTTPRequest.status.toString() + ")");
		return;
	}

	var XMLDoc = top.g_oXMLHTTPRequest.responseXML;

	document.body.style.cursor = "cursor";

	if ((XMLDoc == null) || (XMLDoc.documentElement == null)){
		eval(top.g_oXMLHTTPRequest.responseText);
	}
	else{
		var msg = XMLDoc.documentElement.getElementsByTagName("Message")[0].firstChild.data;
		var id = XMLDoc.documentElement.getElementsByTagName("@Id")[0].firstChild.data;
		var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");

		if (checks.length) {
			for (var i = 0; i < checks.length; i++) {
				if (checks[i].value == id) {
					checks[i].setAttribute("Remarks", msg);
					break;
				}
			}
		}
	}
}

function ShowRemarksCtx()
{
	ShowRemarks(top.Main.Distr.document.getElementById("Comentario"));
	return false;
}


function ViewProperties(opcion)
{
	if (opcion.className == "SubOptionsDisabled2"){return;}

	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if (checks[i].checked == true) {
				checks[i].parentNode.parentNode.ondblclick();
				break;
			}
		}
	}

}


function Archive(opcion)
{
	if (opcion.className == "SubOptionsDisabled2"){return;}
	if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_SALIDA){return;}


	var args = new Array();
	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var caseSensitive = "";

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if (checks[i].checked == true) {
				caseSensitive = checks[i].CaseSensitive;
				break;
			}
		}
	}

	args[0] = "";
	args[1] = 0;
	args[2] = top.GetIdsLan( "IDS_OPCARCHIVAR" );
	args[3] = top.Idioma;
	args[4] = caseSensitive;
	args[5] = 1;

	FreeFormDist();

	if ((GetQryStrIds(STATE_DISTR_PENDIENTE, false) == true) ||(GetQryStrIds(STATE_DISTR_ACEPTADO, true) == true)){
		var resp = top.ShowModalDialog(top.g_URL + "/showremarksArchivoDistribucion.htm", args, 230, 370, "");

		if ((resp != null)){

			var selectTypeDistr = document.getElementById("selectTypeDistr");
			var iInitValue = parseInt(top.GetInnerText(top.Main.Distr.mainDtr.document.getElementById("Init")), 10) - 1;
			var oForm = document.getElementById("formDist");

			document.getElementById("Remarks").value = resp;

			DisabledToolbar();
			DisabledCheckBox();

			top.Main.Distr.mainDtr.document.body.innerHTML = "";
			top.Main.Distr.mainDtr.document.body.style.cursor = "wait";
			top.Main.Distr.document.body.style.cursor = "wait";


			oForm.action = top.g_URL + "/dtrarch.jsp?SessionPId=" + top.g_SessionPId +
				"&TypeDistr=" + selectTypeDistr.value.toString() +
				"&InitValue=" + iInitValue.toString();

			oForm.submit();

			/*ExecuteOperation("dtrarch.jsp", STATE_DISTR_ACEPTADO);*/
		}
	}

}


function Accept(opcion)
{
	if (opcion.className == "SubOptionsDisabled2"){return;}
	if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_SALIDA){return;}

	ExecuteOperation("dtracept.jsp", STATE_DISTR_PENDIENTE);
}


function SetRemarks(opcion)
{
	if (opcion.className == "SubOptionsDisabled2"){return;}
	if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_SALIDA){return;}

	var args = new Array();
	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var caseSensitive = "";

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if (checks[i].checked == true) {
				caseSensitive = checks[i].CaseSensitive;
				break;
			}
		}
	}

	args[0] = "";
	args[1] = 1;
	args[2] = top.GetIdsLan( "IDS_OPC_RECHAZAR" );
	args[3] = top.Idioma;
	args[4] = caseSensitive;
	args[5] = 1;

	FreeFormDist();

	if ((GetQryStrIds(STATE_DISTR_PENDIENTE, false) == true) ||(GetQryStrIds(STATE_DISTR_ACEPTADO, true) == true)){
		var resp = top.ShowModalDialog(top.g_URL + "/showremarks.htm", args, 230, 370, "");

		if ((resp != "") && (resp != null)){
			var selectTypeDistr = document.getElementById("selectTypeDistr");
			var iInitValue = parseInt(top.GetInnerText(top.Main.Distr.mainDtr.document.getElementById("Init")), 10) - 1;
			var oForm = document.getElementById("formDist");

			document.getElementById("Remarks").value = resp;

			DisabledToolbar();
			DisabledCheckBox();

			top.Main.Distr.mainDtr.document.body.innerHTML = "";
			top.Main.Distr.mainDtr.document.body.style.cursor = "wait";
			top.Main.Distr.document.body.style.cursor = "wait";

			oForm.action = top.g_URL + "/dtrrech.jsp?SessionPId=" + top.g_SessionPId +
				"&TypeDistr=" + selectTypeDistr.value.toString() +
				"&InitValue=" + iInitValue.toString();
			oForm.submit();
		}
	}
}


function ChangeDest(opcion)
{
	if (opcion.className == "SubOptionsDisabled2"){return;}

	var args = new Array();
	var doChange = false;

	FreeFormDist();

	if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_ENTRADA){
		doChange = GetQryStrIds(STATE_DISTR_PENDIENTE, false) || GetQryStrIds(STATE_DISTR_ACEPTADO, true);
	}
	else {
		doChange = GetQryStrIds(STATE_DISTR_RECHAZADO, false);
	}

	args[0] = "";
	args[1] = 1;
	args[2] = top.GetIdsLan("IDS_BTN_DESTINO");
	args[3] = top.g_URL;
	args[4] = top.g_SessionPId;
	args[5] = top.g_ArchivePId.toString();
	args[6] = top.FLD_DEST_FLDID;
	args[7] = top.Idioma.toString();
	args[8] = top.g_CaseSensitive;

	if (doChange == true){
		var resp = top.ShowModalDialog(top.g_URL + "/dtrselectdest.htm", args, 235, 370, "");

		if ((resp != "") && (resp != null)){
			var selectTypeDistr = document.getElementById("selectTypeDistr");
			var iInitValue = parseInt(top.GetInnerText(top.Main.Distr.mainDtr.document.getElementById("Init")), 10) - 1;
			var oForm = document.getElementById("formDist");

			DisabledToolbar();
			DisabledCheckBox();

			top.Main.Distr.mainDtr.document.body.innerHTML = "";
			top.Main.Distr.mainDtr.document.body.style.cursor = "wait";
			top.Main.Distr.document.body.style.cursor = "wait";

			oForm.action = top.g_URL + "/dtrchange.jsp?SessionPId=" + top.g_SessionPId +
				"&IdDest=" + resp +
				"&TypeDistr=" + selectTypeDistr.value.toString() +
				"&InitValue=" + iInitValue.toString();

			oForm.submit();
		}
	}
}

function RejectDistribution(opcion){
	if (opcion.className == "SubOptionsDisabled2"){return;}

	FreeFormDist();

	var sRet;
	var args = new Array;
	args[0] = top.g_URL;
	args[1] = top.g_SessionPId.toString();
	args[2] = top.g_ArchivePId.toString();
	args[3] = "";
	args[4] = top.Idioma;
	args[5] = g_CaseSensitive;
	//args[6]: parametro boleano que indica que viene desde la bandeja de distribucion
	//utilizado para llamar a la ventana de distribucion
	args[6] = true;

	if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_ENTRADA){
		doChange = GetQryStrIds(STATE_DISTR_PENDIENTE, false) || GetQryStrIds(STATE_DISTR_ACEPTADO, true);
	}
	else {
		doChange = GetQryStrIds(STATE_DISTR_RECHAZADO, false);
	}

	if (doChange == true){
		//invocamo a la pantalla que nos devuelve el nuevo destino
		sRet = top.ShowModalDialog(top.g_URL + "/dtrselectuser.jsp", args, 450, 500, "");

		if ((sRet != "") && (sRet != null)){
			var selectTypeDistr = document.getElementById("selectTypeDistr");
			var iInitValue = parseInt(top.GetInnerText(top.Main.Distr.mainDtr.document.getElementById("Init")), 10) - 1;
			var oForm = document.getElementById("formDist");

			// se almacena la informacion retornada de la ventana modal en un
			// campo oculto del formulario dicho valor contara de:
			// (tipoDestino||idDestion||motivo)
	   		var inputId = document.createElement("INPUT");
			inputId.setAttribute("type", "hidden");
			inputId.setAttribute("name", "infoDistribution");
			inputId.setAttribute("id", "infoDistribution");
			inputId.value = sRet;
			oForm.appendChild(inputId);

			DisabledToolbar();
			DisabledCheckBox();

			top.Main.Distr.mainDtr.document.body.innerHTML = "";
			top.Main.Distr.mainDtr.document.body.style.cursor = "wait";
			top.Main.Distr.document.body.style.cursor = "wait";

			oForm.action = top.g_URL + "/dtrReject.jsp?SessionPId=" + top.g_SessionPId +
			"&TypeDistr=" + selectTypeDistr.value.toString()  +
			"&InitValue=" + iInitValue.toString();

			oForm.submit();
		}
	}
}

function ExecuteOperation(page, State)
{
	FreeFormDist();

	if (GetQryStrIds(State, false) == true){
		var selectTypeDistr = document.getElementById("selectTypeDistr");
		var iInitValue = parseInt(top.GetInnerText(top.Main.Distr.mainDtr.document.getElementById("Init")), 10) - 1;
		var oForm = document.getElementById("formDist");

        DisabledToolbar();
        DisabledCheckBox();

        top.Main.Distr.mainDtr.document.body.innerHTML = "";
	    top.Main.Distr.mainDtr.document.body.style.cursor = "wait";
	    top.Main.Distr.document.body.style.cursor = "wait";

        oForm.action = top.g_URL + "/" + page + "?SessionPId=" + top.g_SessionPId +
            "&TypeDistr=" + selectTypeDistr.value.toString() +
            "&InitValue=" + iInitValue.toString();
        oForm.submit();
   	}
}


function GetQryStrIds(State, SoloEntrada)
{
	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var arrIds = new Array();
	var j = 0;
	var bRet = false;
	var oForm = document.getElementById("formDist");

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if ((checks[i].checked == true) && (checks[i].getAttribute("State") == State)){
				if (SoloEntrada){
					if (checks[i].getAttribute("DistType") == "0"){
						arrIds[j] = checks[i].value;
						j++;
					}
				}
				else{
					arrIds[j] = checks[i].value;
					j++;
				}
			}
		}
	}

	if (arrIds.length){
		try {
			bRet = true;

			for (var i = 0; i < arrIds.length; i++) {
		   		var inputId = document.createElement("INPUT");

				inputId.setAttribute("type", "hidden");
				inputId.setAttribute("name", "Ids");
				inputId.setAttribute("id", "Ids");

				inputId.value = arrIds[i].toString();

				oForm.appendChild(inputId);
   			}
   		}
	   	catch(e){
   			bRet = false;
   			alert(e.description);
   		}
   	}

   	return (bRet);
}


function FreeFormDist()
{
	var oForm = document.getElementById("formDist");

	try {
		for (var i = oForm.childNodes.length - 1; i >= 0; i--){
			var oChild = oForm.childNodes[i];

			if ((oChild.id == "Ids") || (oChild.id == "infoDistribution")){
				oForm.removeChild(oChild);
			}
		}
	}
   	catch(e){
   		alert(e.description);
   	}
}


function VerifyMenu()
{
	var checks = top.Main.Distr.mainDtr.document.getElementsByName("checkrow");
	var arrIds = new Array();
	var j = 0;
	var clsNameComentario = "SubOptionsDisabled2";
	var clsNamePropiedades = "SubOptionsDisabled2";
	var clsNameArchivar = "SubOptionsDisabled2";
	var clsNameDestino = "SubOptionsDisabled2";
	var clsNameRechazar = "SubOptionsDisabled2";
	var clsNameAceptar = "SubOptionsDisabled2";
	var clsNameRedistribuir ="SubOptionsDisabled2";

	if (checks.length) {
		for (var i = 0; i < checks.length; i++) {
			if (checks[i].checked == true) {
				arrIds[j] = i;
				j++;
			}
		}

		for (j = 0; j < arrIds.length; j++){
			if (checks[arrIds[0]].getAttribute("State") == STATE_DISTR_RECHAZADO){
				clsNameComentario = "SubOptions2";
			}

			if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_ENTRADA)	{
				if (checks[arrIds[j]].getAttribute("State") == STATE_DISTR_PENDIENTE){
					clsNameAceptar = "SubOptions2";
					clsNameRechazar = "SubOptions2";
					clsNameDestino = "SubOptions2";
					clsNameRedistribuir="SubOptions2";
				}

				if (checks[arrIds[j]].getAttribute("State") == STATE_DISTR_ACEPTADO){
					clsNameArchivar = "SubOptions2";

					if (checks[arrIds[j]].getAttribute("DistType") == "0"){
						clsNameDestino = "SubOptions2";
						clsNameRechazar = "SubOptions2";
						clsNameRedistribuir="SubOptions2";
					}
				}
			}

			if (document.getElementById("selectTypeDistr").value == TYPE_DISTR_SALIDA)	{
				if (checks[arrIds[j]].getAttribute("State") == STATE_DISTR_RECHAZADO){
					clsNameDestino = "SubOptions2";
					clsNameRedistribuir="SubOptions2";
				}
			}
		}

		if (arrIds.length){
			clsNamePropiedades = "SubOptions2";
		}
	}

	if (document.getElementById("Aceptar").getAttribute("enabled") == "0"){
		clsNameAceptar = "SubOptionsDisabled2";
	}

	if (document.getElementById("Rechazar").getAttribute("enabled") == "0"){
		clsNameRechazar = "SubOptionsDisabled2";
	}

	if (document.getElementById("Destino").getAttribute("enabled") == "0"){
		clsNameDestino = "SubOptionsDisabled2";
	}

	if (document.getElementById("Redistribuir").getAttribute("enabled") == "0"){
		clsNameRedistribuir = "SubOptionsDisabled2";
	}

	if (document.getElementById("Archivar").getAttribute("enabled") == "0"){
		clsNameArchivar = "SubOptionsDisabled2";
	}

	document.getElementById("Aceptar").className = clsNameAceptar;
	document.getElementById("Rechazar").className = clsNameRechazar;
	document.getElementById("Destino").className = clsNameDestino;
	document.getElementById("Redistribuir").className = clsNameRedistribuir;
	document.getElementById("Archivar").className = clsNameArchivar;
	document.getElementById("Propiedades").className = clsNamePropiedades;
	document.getElementById("Comentario").className = clsNameComentario;
}


function Search(opcion)
{
    if (opcion.className == "SubOptionsDisabled2"){return;}

	var resp = 0;
	var args = new Array();
	var select = document.getElementById("select");
	var selectTypeDistr = document.getElementById("selectTypeDistr");
	var URL = top.g_URL + "/getsearchdist.jsp?SessionPId=" + top.g_SessionPId + "&TypeDistr=" + selectTypeDistr.value.toString();

	select.selectedIndex = select.options.length - 1;

	args[0] = top.g_URL;
	args[1] = top.g_SessionPId;
	args[2] = top.g_ArchivePId.toString();
	args[3] = top.Idioma;
	args[4] = selectTypeDistr.value.toString();

	resp = top.ShowModalDialog(URL, args, 400, 475, "");

	if ((resp != 0) && (resp != null)){
		var arrWhere = resp.split("#");

		document.getElementById("distWhere").value = arrWhere[0];
		document.getElementById("regWhere").value = arrWhere[1];

		DisabledToolbar();
		DoOnLoad(0);
	}
	else {
		select.selectedIndex = g_StateSelected;
	}
}


function ShowValidationList(FldType, ValidType, FldId)
{
	var sRet = "";
	var args = new Array;

	args[0] = top.GetDlgParam(0);
	args[1] = top.GetDlgParam(1);
	args[2] = top.GetDlgParam(2);
	args[3] = FldId.toString();
	args[4] = ValidType.toString();
	args[5] = FldId.toString();
	args[6] = top.GetDlgParam(0);
	args[7] = 0;

	sRet = top.ValidateList(args);

	if (sRet && (sRet != "")) {
		var tokens = new Array;
		var objs = document.getElementsByName(FldType);

		tokens = top.getTokens(sRet, "#", "#", 3);

		for (var i = 0; i < objs.length; i++) {
			var obj = objs.item(i);

			if ((obj.className == "input") && (obj.FldId == FldId)) {
				obj.value = tokens[1];
				break;
			}
		}
	}
}

function ShowValidationList(FldType, ValidType, FldId, caseSensitive)
{
	var sRet = "";
	var args = new Array;

	args[0] = top.GetDlgParam(0);
	args[1] = top.GetDlgParam(1);
	args[2] = top.GetDlgParam(2);
	args[3] = FldId.toString();
	args[4] = ValidType.toString();
	args[5] = FldId.toString();
	args[6] = top.GetDlgParam(0);
	args[7] = 0;

	sRet = top.ValidateList(args, caseSensitive);

	if (sRet && (sRet != "")) {
		var tokens = new Array;
		var objs = document.getElementsByName(FldType);

		tokens = top.getTokens(sRet, "#", "#", 3);

		for (var i = 0; i < objs.length; i++) {
			var obj = objs.item(i);

			if ((obj.className == "input") && (obj.FldId == FldId)) {
				obj.value = tokens[1];
				break;
			}
		}
	}
}

function SearchDist()
{
	var CanSearch = false;
	var distFields = document.getElementsByName("DistField");
	var regFields = document.getElementsByName("RegField");
	var items = document.getElementsByTagName("*");
	var distWhere = "";
	var regWhere = "";

	for (var i = 0; i < distFields.length; i++) {
		var item = distFields[i];

		if ((item.className == "input") && (item.value != "")){
			if (distWhere == ""){
				distWhere = item.getAttribute("FldName") + ";" +  GetOperator(item.getAttribute("FldName")) + ";" + item.value + ";#";
			}
			else {
				distWhere = distWhere + item.getAttribute("FldName") + ";" +  GetOperator(item.getAttribute("FldName")) + ";" + item.value + ";#";
			}
		}

		if (item.className == "combo"){
			var itemSelected = item.options[item.selectedIndex];

			if (itemSelected.value != "0"){
				if (distWhere == ""){
					distWhere = item.getAttribute("FldName") + ";" +  GetOperator(item.getAttribute("FldName")) + ";" + itemSelected.value + ";#";
				}
				else {
					distWhere = distWhere +  item.getAttribute("FldName") + ";" +  GetOperator(item.getAttribute("FldName")) + ";" + itemSelected.value + ";#";
				}
			}
		}
	}

	for (var i = 0; i < regFields.length; i++) {
		var item = regFields[i];

		if ((item.className == "input") && (item.value != "")){
			if (regWhere == ""){
				regWhere = item.getAttribute("FldName") + ";" +  GetOperator(item.getAttribute("FldName")) + ";" + item.value + ";#";
			}
			else {
				regWhere = regWhere +  item.getAttribute("FldName") + ";" + GetOperator(item.getAttribute("FldName")) + ";" + item.value + ";#";
			}
		}
	}

	for (var i = 0; i < items.length; i++){
		if ((items[i].nodeName == "IMG") && items[i].getAttribute("FldName")){
			items[i].style.visibility = "hidden";
		}
	}

	if ((distWhere != "") || (regWhere != "")){
		CanSearch = true;
	}

	if (CanSearch == false){
		alert(top.GetIdsLan("IDS_MSG_INTRO_CONDITION"));
	}
	else {
		var Data = "distWhere=" + distWhere + "&regWhere=" + regWhere;
		var URL = top.GetDlgParam(0) + "/validatesearchdist.jsp?SessionPId=" + top.GetDlgParam(1)
			+ "&TypeDist=" + top.GetDlgParam(4);

		document.body.style.cursor = "wait";

		top.XMLHTTPRequestPost(URL, ValidateResponse, true, Data)
	}
}


function FormatField(field)
{
	var fldValidated = (field.getAttribute("Validated") == "1")?true:false;;

	if (field.className == "input"){
		field.value = top.miTrim(field.value);

		if ((field.getAttribute("DataType") == "2") && (field.value != "")){
			ValidateDate(field);
		}
		else{
			if (fldValidated){
				field.value = field.value.toUpperCase();
			}
		}
	}
}

function GetOperator(FldName)
{
	var sRet = "";
	var operators = document.getElementsByName("Operators");

	for (var i = 0; i < operators.length; i++) {
		var item = operators[i];

		if (operators[i].getAttribute("FldName") == FldName){
			sRet = item.options[item.selectedIndex].text;
			break;
		}
	}

	return (sRet);
}

function ValidateResponse()
{
	if (top.g_oXMLHTTPRequest.readyState != 4){
		return;
	}

	if (top.g_oXMLHTTPRequest.status != 200){
		alert(top.g_oXMLHTTPRequest.statusText + " (" + top.g_oXMLHTTPRequest.status.toString() + ")");
		return;
	}

	var XMLDoc = top.g_oXMLHTTPRequest.responseXML;

	document.body.style.cursor = "cursor";

	if ((XMLDoc == null) || (XMLDoc.documentElement == null)){
		eval(top.g_oXMLHTTPRequest.responseText);
	}
	else{
		if (XMLDoc.documentElement.getElementsByTagName("Error").length != 0) {
			var FldName = XMLDoc.documentElement.getElementsByTagName("FldName")[0].firstChild.data;
			var items = document.getElementsByTagName("*");

			alert(XMLDoc.documentElement.getElementsByTagName("Error")[0].firstChild.data);

			for (var i = 0; i < items.length; i++) {
				var item = items[i];

				if (item.getAttribute("FldName")){
					if ((item.id != "Operators") && (item.getAttribute("FldName") == FldName)) {
						//item.style.visibility = "visible";

						if (item.nodeName == "INPUT"){
							item.focus();
							item.style.color = "red";
						}
					}
				}
			}
		}
		else {
			var distWhere = XMLDoc.documentElement.getElementsByTagName("DistWhere")[0].firstChild.data;
			var regWhere = XMLDoc.documentElement.getElementsByTagName("RegWhere")[0].firstChild.data;

			top.returnValue = distWhere + "#" + regWhere;
			top.close();
		}
	}
}


function SelectBook(HTMLCode, InitValue)
{
	var args = new Array();
	var sRet;

	args[0] = HTMLCode;
	args[1] = top.g_URL;
	args[2] = top.g_SessionPId.toString();
	args[3] = top.Idioma;

	sRet = top.ShowModalDialog(top.g_URL + "/validatebooks.htm", args, 275, 500, "");

	top.Main.Distr.document.body.style.cursor = "wait";

	if ((sRet != null) && (sRet != ""))	{
		var selectTypeDistr = document.getElementById("selectTypeDistr");
		var oForm = document.getElementById("formDist");

        oForm.action = top.g_URL + "/dtracept.jsp?SessionPId=" + top.g_SessionPId +
            "&TypeDistr=" + selectTypeDistr.value.toString() +
            "&InitValue=" + InitValue.toString() +
            "&BookId=" + sRet;
        oForm.submit();
	}
	else {
		DoOnLoad(InitValue);
	}
}


function SetPerms(CanAccept, CanReject, CanArchive, CanChangeDest, CanChangeDestReject)
{
	var selectTypeDistr = top.Main.Distr.document.getElementById("selectTypeDistr");

	top.Main.Distr.document.getElementById("Aceptar").setAttribute("enabled", CanAccept);
    top.Main.Distr.document.getElementById("Rechazar").setAttribute("enabled", CanReject);
    top.Main.Distr.document.getElementById("Archivar").setAttribute("enabled", CanArchive);

    if (selectTypeDistr.value == TYPE_DISTR_ENTRADA) {
		top.Main.Distr.document.getElementById("Destino").setAttribute("enabled", CanChangeDest);
		top.Main.Distr.document.getElementById("Redistribuir").setAttribute("enabled", CanChangeDest);
	}
	else {
		top.Main.Distr.document.getElementById("Destino").setAttribute("enabled", CanChangeDestReject);
		top.Main.Distr.document.getElementById("Redistribuir").setAttribute("enabled", CanChangeDestReject);
	}
}

function isFromConfim(typeDist, state)
{
	var select = top.Main.Distr.document.getElementById("select");
	var selectTypeDistr = top.Main.Distr.document.getElementById("selectTypeDistr");
	var oForm = top.Main.Distr.document.getElementById("formDist");
	var frmMainDtr = top.Main.Distr.document.getElementById("mainDtr");

	top.Main.Distr.document.getElementById("checkSel").checked = false;
	top.Main.Distr.document.getElementById("checkSel").disabled = true;
	top.Main.Distr.document.body.style.cursor = "wait";

	if (frmMainDtr.contentWindow.document.body !== null){
		frmMainDtr.contentWindow.document.body.style.cursor = "wait";
		frmMainDtr.contentWindow.document.body.innerHTML = "";
	}
	if (state == '1'){
		select.selectedIndex = 0
		selectTypeDistr.selectedIndex = 0;
		g_StateSelected = 0;
	} else {
		select.selectedIndex = 2
		selectTypeDistr.selectedIndex = 1;
		g_StateSelected = 2;
	}
 	document.getElementById("distWhere").value = select.getAttribute("FieldName") + "=" + select.value;

	oForm.action = top.g_URL + "/distribution.jsp?SessionPId=" + top.g_SessionPId + "&InitValue=0&TypeDistr=" + typeDist + "&state=" + state;
	oForm.submit();
}

//Funci�n que ejecuta la ordenaci�n seg�n el campo indicado
function ordernarDistribucion(campo){
	//obtenemos del formulario el campo de ordenaci�n
	var orderBy = top.Main.Distr.document.getElementById("orderDistribution").value;

	var orden = "";
	// obtenemos la informacion de la imagen mostrada para indicar la ordenaci�n
	// correspondiente
	var cadena = document.getElementById("ordenCampo" + campo).src;

	cadena = cadena.substring(cadena.indexOf("images/")+7,cadena.length);

	if (cadena == "asc.gif"){
		document.getElementById("ordenCampo" + campo).src = "./images/desc.gif";
		orden= campo + " DESC";
	}
	else if(cadena == "desc.gif"){
		document.getElementById("ordenCampo" + campo).src = "./images/bg.gif";
	}
	else{
		document.getElementById("ordenCampo" + campo).src = "./images/asc.gif";
		orden= campo + " ASC";
	}

	//Asignamos la ordenaci�n al formulario
	top.Main.Distr.document.getElementById("orderDistribution").value = orden;

	//Invocamos al refrescar de la pantalla
	DisabledToolbar();
	DoOnLoad(0);
}

function loadImagenesOrderDistribution(){
	//obtenemos del formulario el campo de ordenaci�n
	var orderBy = top.Main.Distr.document.getElementById("orderDistribution").value;

	if(orderBy != null && orderBy.length>1){
		var elemento = orderBy.split(' ');
		var imagen;
		if(elemento[1] == "ASC"){
			imagen = "./images/asc.gif";
		}else{
			imagen = "./images/desc.gif";
		}

		//obtenemos la imagen que hay en pantalla para cambiarla
		var imagenInDisplay = document.getElementById("ordenCampo" + elemento[0]);
		//validamos que exista el objeto para cambiar el valor
		if(imagenInDisplay){
			//cambiamos la imagen
			document.getElementById("ordenCampo" + elemento[0]).src = imagen;
		}
	}
}

