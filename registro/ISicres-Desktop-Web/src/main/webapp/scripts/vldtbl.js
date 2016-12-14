var Operador = "LIKE";
var OperadorIni = "'*";
var OperadorEnd = "*'";
var QryTxt = "";

function OnWindowLoad() {

	var queryBook = top.ParamValue(document.location.search,"queryBook");

	var cmbOperators = document.getElementById("LikeType");

	document.getElementById("QryButt").value = top.GetIdsLan("IDS_OPCBUSCAR");
	document.getElementById("Close").value = top.GetIdsLan("IDS_OPCCERRAR");

	top.setFocus(document.getElementById("Query"));

	cmbOperators.options[0].text = top.GetIdsLan("IDS_NONE");
	cmbOperators.options[1].text = top.GetIdsLan("IDS_EQUAL_TO");
	cmbOperators.options[2].text = top.GetIdsLan("IDS_STARTS_WITH");
	cmbOperators.options[3].text = top.GetIdsLan("IDS_CONTAINS");

	if(queryBook){
		document.getElementById("divQuery").style.display="none";
		document.getElementById("divQueryBook").style.display="block";
	}

	EnableQry(false);
}

function EnableQry(Enable) {
	var Enab = !Enable;

}

function ExitVld() {
	parent.document.getElementById("VldResMain").rows = "100%,*";

	if (parent.bIsDtrList == 0) {
		top.g_FormVld.style.visibility = "visible";
		top.setFocus(top.g_Field);
		eval(top.g_VldPath + ".OnHelpWindow=false");

		// activamos el boton de guardar si estamos en formulario
		if ((top.g_WndVld.bIsActiveSave) && (top.g_FolderView)) {
			top.Main.Folder.FolderBar.ActivateSave();
		}

		parent.parent.document.getElementById("Vld").style.display = "none";
	} else {
		top.close();
	}

	if ((top.Main != null)
	 && (top.Main.Folder != null)
	 && (top.Main.Folder.FolderData != null)
	 && (top.Main.Folder.FolderData.FolderFormTree != null)
	 && (top.Main.Folder.FolderData.FolderFormTree.document != null)
	 && (top.Main.Folder.FolderData.FolderFormTree.document.getElementById("divDocs") != null)
	 && (top.Main.Folder.FolderData.FolderFormTree.document.getElementById("divDocs").style != null)){
		top.Main.Folder.FolderData.FolderFormTree.document.getElementById("divDocs").style.visibility="visible";
	}
}

function ChangeField() {
	var bEnableQry = false;
	var cmbField = document.getElementById("cmbFields");
	var cmbOp = document.getElementById("LikeType");

	bEnableQry = (cmbField.options[cmbField.selectedIndex].value != "0");

	switch (cmbOp.value) {
	case '0':
		Operador = "";
		OperadorIni = "";
		OperadorEnd = "";
		bEnableQry = false;
		break;
	case '1':
		Operador = "=";
		OperadorIni = "'";
		OperadorEnd = "'";
		bEnableQry = bEnableQry && true;
		break;
	case '2':
		Operador = "LIKE";
		OperadorIni = "'";
		OperadorEnd = "*'";
		bEnableQry = bEnableQry && true;
		break;
	case '3':
		Operador = "LIKE";
		OperadorIni = "'*";
		OperadorEnd = "*'";
		bEnableQry = bEnableQry && true;
		break;
	}

	EnableQry(bEnableQry);
}

function buscar() {

	parent.VldTypeBuscOld = parent.VldTypeBusc;
	parent.VldTypeIdOld = parent.VldTypeId;
	parent.VldIdCrlOld = parent.VldIdCrl;
	parent.VldListPId = 0;
	parent.VldTypeBusc = 0;

	ChangeField();
	var cmb = document.getElementById("cmbFields");
	if (document.getElementById("Value").value != "") {

		var caseSensitive = top.ParamValue(document.location.search,
				"caseSensitive");
		if (caseSensitive == 'CS') {
			document.getElementById("Value").value = document
					.getElementById("Value").value.toUpperCase();
		}

		parent.VldQueryValue = document.getElementById("Value").value;

		if ((cmb.options[cmb.selectedIndex].value == "0")
				|| (document.getElementById("LikeType").value == "0")) {
			if ((cmb.options[cmb.selectedIndex].value == "0")
					&& (document.getElementById("LikeType").value == "0")) {
				alert(top.GetIdsLan("IDS_ERRORNOCAMPOPER"));
			} else if (cmb.options[cmb.selectedIndex].value == "0") {
				alert(top.GetIdsLan("IDS_ERRORNOCAMPO"));
			} else if (document.getElementById("LikeType").value == "0") {
				alert(top.GetIdsLan("IDS_ERRORNOOPER"));
			}

			return false;
		}

	} else {
		parent.VldQueryValue = "";
	}

	if ((document.getElementById("Value").value != "")
			&& (cmb.options[cmb.selectedIndex].value != "0")) {
		parent.VldQuery = cmb.options[cmb.selectedIndex].colname + " "
				+ Operador;
		// + " " + OperadorIni +
		// document.getElementById("Value").value.replace(/'/g,"''") +
		// OperadorEnd;
	} else {
		parent.VldQuery = "";
	}

	var typebook = "";

	if (top.ParamValue(document.location.search,"queryBook")){
		typebook = document.getElementById("TypeBook").value;
	}

	ShowRes("VldRes.jsp" + "?tblvalidated=" + parent.VldCtlType + "&VldQuery="
			+ escape(parent.VldQuery) + "&VldQueryValue="
			+ escape(parent.VldQueryValue) + "&TypeId=" + parent.VldTypeId + "&IdCrl="
			+ parent.VldIdCrl + "&SessionPId=" + parent.SessionPId + "&FldId="
			+ parent.FolderId + "&ArchivePId=" + parent.ArchivePId
			+ "&TypeBusc=0" + "&Enabled=" + parent.VldEnabled.toString()
			+ "&IsDtrList=" + parent.bIsDtrList + "&TblFldId="
			+ parent.TblFldId + "&ListPId=" + parent.VldListPId + "&TypeBook="+ typebook);

	// Activamos el bot�n de buscar, por si se eleva una excepci�n en el servlet
	// poder realizar otra b�squeda
	parent.VldTbl.document.getElementById("QryButt").disabled = false;

	return false;
}

function ShowRes(URL) {
	parent.document.getElementById("VldResMain").rows = "100%, *";
	window.open("./msg.htm?Txt:" + top.GetIdsLan("IDS_WAIT") + "!URL:" + URL,
			"VldFld", "location=no", true);
}