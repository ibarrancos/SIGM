<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/displaytag.tld" prefix="display" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/taglibs/displaydepositotags.tld" prefix="archivo"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<bean:struts id="actionMapping" mapping="/gestionMesasAction" />

<c:set var="sala" value="${requestScope[appConstants.salas.SALA_KEY]}" />
<c:set var="listaMesas" value="${requestScope[appConstants.salas.LISTA_MESAS_KEY]}" />
<c:set var="modificarMesas" value="${requestScope[appConstants.salas.ACTION_MODIFICAR_MESAS_KEY]}" />
<c:set var="eliminarMesas" value="${requestScope[appConstants.salas.ACTION_ELIMINAR_MESAS_KEY]}" />

<html:form action="/gestionMesasAction">
	<input type="hidden" name="method" value="eliminar">
	<html:hidden property="idSala" />

	<tiles:insert template="/pages/tiles/PABoxLayout.jsp">
		<tiles:put name="boxTitle" direct="true">
			<c:choose>
				<c:when test="${modificarMesas}">
					<bean:message key="archigest.archivo.salas.cambiar.codigos.mesas" />
				</c:when>
				<c:when test="${eliminarMesas}">
					<bean:message key="archigest.archivo.salas.eliminar.mesas" />
				</c:when>
			</c:choose>
		</tiles:put>
		<tiles:put name="buttonBar" direct="true">
			<table cellpadding=0 cellspacing=0>
				<tr>
					<c:if test="${modificarMesas || eliminarMesas}">
					<td>
						<script>
							function go() {
								var form = document.forms['<c:out value="${actionMapping.name}" />'];
								var modificar = '<c:out value="${modificarMesas}" />';
								var eliminar = '<c:out value="${eliminarMesas}" />';

								if (form.idsMesa && !elementSelected(form.idsMesa)){
									alert("<bean:message key='archigest.archivo.sala.esNecesarioSeleccionarAlMenosUnaMesa'/>");
								}
								else if(eliminar){
									if (confirm('<bean:message key="archigest.archivo.salas.eliminarMesaMsg"/>')){
										form.submit();
									}
								}else if(modificar){
									form.method.value = 'aceptarModificarMesas';
									form.submit();
								}
							}
						</script>
						<a class="etiquetaAzul12Bold" href="javascript:go()" >
							<html:img page="/pages/images/Ok_Si.gif" altKey="archigest.archivo.aceptar" titleKey="archigest.archivo.aceptar" styleClass="imgTextBottom" />
							&nbsp;<bean:message key="archigest.archivo.aceptar"/>
						</a>
					</td>
					<td width="10">&nbsp;</td>
					</c:if>
					<TD noWrap>
						<tiles:insert definition="button.closeButton">
							<tiles:put name="imgIcon" value="/pages/images/Ok_No.gif" />
							<tiles:put name="labelKey" value="archigest.archivo.cancelar" />
						</tiles:insert>
					</TD>
				</tr>
			</table>
		</tiles:put>
		<tiles:put name="boxContent" direct="true">
			<tiles:insert template="/pages/tiles/PABlockLayout.jsp">
				<tiles:put name="blockTitle" direct="true">
					<bean:message key="archigest.archivo.salas.ver.sala"/>
				</tiles:put>
				<tiles:put name="blockContent" direct="true">
					<tiles:insert name="salas.datos.sala" flush="true"/>
				</tiles:put>
			</tiles:insert>

			<div class="separador8">&nbsp;</div>

			<div class="cabecero_bloque">
				<table class="w98m1" cellpadding=0 cellspacing=0 height="100%">
					<tr>
						<td class="etiquetaAzul12Bold" width="40%">
							<c:choose>
								<c:when test="${modificarMesas}">
									<bean:message key="archigest.archivo.salas.seleccion.mesas.modificar"/>
								</c:when>
								<c:when test="${eliminarMesas}">
									<bean:message key="archigest.archivo.salas.seleccion.mesas.eliminar"/>
								</c:when>
							</c:choose>
						</td>
						<td align="right">
							<a class="etiquetaAzul12Bold"
								href="javascript:seleccionarCheckboxSet(document.forms['<c:out value="${actionMapping.name}" />'].idsMesa);"
				 			><html:img page="/pages/images/checked.gif"
								    altKey="archigest.archivo.selTodas"
								    titleKey="archigest.archivo.selTodas"
								    styleClass="imgTextBottom" />
						    &nbsp;<bean:message key="archigest.archivo.selTodas"/></a>
							&nbsp;
							<a class="etiquetaAzul12Bold"
								href="javascript:deseleccionarCheckboxSet(document.forms['<c:out value="${actionMapping.name}" />'].idsMesa);"
				 			><html:img page="/pages/images/check.gif"
								    altKey="archigest.archivo.quitarSel"
								    titleKey="archigest.archivo.quitarSel"
								    styleClass="imgTextBottom" />
						    &nbsp;<bean:message key="archigest.archivo.quitarSel"/></a>
							&nbsp;&nbsp;
					   </td>
					</tr>
				</table>
			</div> <%--cabecero bloque --%>

			<div class="bloque">
				<div class="separador8">&nbsp;</div>
				<div id="divTbl" style="width:98%;margin-left:5px;padding-left:5px;margin-bottom:5px;">
					<table class="tblMesas" style="width:98%;table-layout:auto;margin-left:5px">
					<tr>
					<c:forEach var="mesa" items="${listaMesas}" varStatus="loopStatus">
						<%--SALTO de FILA --%>
						<c:if test="${loopStatus.index !=0 && loopStatus.index % 8 == 0}"></tr><tr></c:if>
						<td class="tdTituloFichaSinBorde" noWrap>
							<c:choose>
								<c:if test="${mesa.estado != appConstants.estadosMesa.OCUPADA}">
									<c:set var="idMesaEliminar" value="${mesa.id}" />
									<jsp:useBean id="idMesaEliminar" type="java.lang.String" />
									<c:set var="formulario" value="${requestScope[mappingGestionMesas.name]}"/>
									<input name="idsMesa" type="checkbox" value="<%=idMesaEliminar%>"
									<c:if test="${formulario.idsMesa==idMesaEliminar}">checked</c:if>
									>
								</c:if>
							</c:choose>
							<b><c:out value="${mesa.codigo}" /></b>
							<c:choose>
								<c:when test="${mesa.estado == appConstants.estadosMesa.OCUPADA}">
									<c:url var="imgMesa" value="/pages/images/mesaOcupada.gif" />
									<c:set var="altKey" value="archigest.archivo.salas.mesa.ocupada" />
								</c:when>
								<c:when test="${mesa.estado == appConstants.estadosMesa.INUTILIZADA}">
									<c:url var="imgMesa" value="/pages/images/mesaInutilizada.gif" />
									<c:set var="altKey" value="archigest.archivo.salas.mesa.inutilizada" />
								</c:when>
								<c:otherwise>
									<c:url var="imgMesa" value="/pages/images/mesaLibre.gif" />
									<c:set var="altKey" value="archigest.archivo.salas.mesa.libre" />
								</c:otherwise>
							</c:choose>
							<img src="<c:out value="${imgMesa}" />" alt="<fmt:message key="${altKey}" />" title="<fmt:message key="${altKey}" />"/>
						</td>
					</c:forEach>
					</tr>
					</table>
				</div>
			</div>
		</tiles:put>
	</tiles:insert>
</html:form>