<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-html-el.tld" prefix="html-el" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-bean-el.tld" prefix="bean-el" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/displaytag.tld" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglibs/displaydepositotags.tld" prefix="archivo"%>
<%@ taglib uri="/WEB-INF/archigest-adds.tld" prefix="pa" %>

<bean:struts id="actionMapping" mapping="/gestionMotivosConsulta" />
<c:set var="investigador" value="${sessionScope[appConstants.consultas.CHECK_INVESTIGADOR]}"/>
<c:set var="editable" value="${sessionScope[appConstants.solicitudes.MOTIVO_EDITABLE_KEY]}"/>

<script language="javascript">
function aceptar(){
	var form = document.forms['<c:out value="${actionMapping.name}" />'];
	form.submit();
}

function actualizarVisibilidad(tipoEntidad){
	if(tipoEntidad == '<c:out value="${investigador}"/>')
		document.getElementById("visibilidad").style.display = "block";
	else
		document.getElementById("visibilidad").style.display = "none";
}
</script>

<div id="contenedor_ficha">

<div class="ficha">

<h1><span>
<div class="w100">
<table class="w98" cellpadding=0 cellspacing=0>
  <tr>  
    <td class="etiquetaAzul12Bold" height="25px">
    	<c:choose>
			<c:when test="${!empty motivoConsultaForm.id}">
				<bean:message key="NavigationTitle_MOTIVO_CONSULTA_EDICION"/>
			</c:when>
			<c:otherwise>
				<bean:message key="NavigationTitle_MOTIVO_CONSULTA_ALTA"/>
			</c:otherwise>
		</c:choose>		
    </td>
    <td align="right">
		<table cellpadding=0 cellspacing=0>
		 <tr>
	      	  <td>
				<a class="etiquetaAzul12Bold" href="javascript:aceptar()" >
					<html:img page="/pages/images/Ok_Si.gif" altKey="archigest.archivo.aceptar" titleKey="archigest.archivo.aceptar" styleClass="imgTextMiddle" />
				&nbsp;<bean:message key="archigest.archivo.aceptar"/></a>	      	  
	      	  </td>
			  <td width="10">&nbsp;</td>	      	  
	      	  <td>
				<c:url var="volver2URL" value="/action/gestionMotivosConsulta">
					<c:param name="method" value="goBack" />
				</c:url>
		   		<a class=etiquetaAzul12Bold href="javascript:window.location='<c:out value="${volver2URL}" escapeXml="false"/>'">
					<html:img page="/pages/images/Ok_No.gif" altKey="archigest.archivo.cancelar" titleKey="archigest.archivo.cancelar" styleClass="imgTextMiddle" />
		   		 	&nbsp;<bean:message key="archigest.archivo.cancelar"/>
		   		</a>
		   </td>
		 </tr>
		</table>
	</td>
  </tr>
</table>
</div>
</span></h1>

<div class="cuerpo_drcha">
<div class="cuerpo_izda">

<DIV id="barra_errores">
	<archivo:errors/>
</DIV>

<div class="separador1">&nbsp;</div>

<DIV class="bloque"> <%--segundo bloque de datos --%>	
	<html:form action="/gestionMotivosConsulta" styleId="formulario">
		<input type="hidden" name="method" id="method" value="addMotivo"/>
		<html:hidden property="id"/>

		<table class="formulario" width="99%">
			<tr>
				<td class="tdTitulo" nowrap="nowrap" width="150px">
					<bean:message key="archigest.archivo.descripcion"/>:&nbsp;
				</td>
				<td class="tdDatos"><html:text property="motivo" styleClass="input80" maxlength="254"/></td>
			</tr>	
			<c:if test="${editable}">	
				<tr>
					<td class="tdTitulo" nowrap="nowrap">
						<bean:message key="archigest.archivo.cf.TipoEntidad"/>:&nbsp;
					</td>
					<td class="tdDatos" nowrap="nowrap">
						<html:select property="tipoEntidad" onchange="actualizarVisibilidad(this.value);">					
							<html:option value=""/>
							<html:option key="archigest.archivo.consultas.ciudadano" value="2"/>
							<html:option key="archigest.archivo.organo" value="3"/>
							<html:option key="archigest.archivo.consultas.investigador" value="1"/>
						</html:select>
					</td>
				</tr>
				<tr>
					<td class="tdTitulo" nowrap="nowrap">
						<bean:message key="archigest.archivo.consultas.tipoConsulta"/>:&nbsp;
					</td>
					<td class="tdDatos">
						<html:select property="tipoConsulta">
							<html:option value=""/>						
							<html:option key="archigest.archivo.consultas.tipoConsulta.directa" value="1"/>
							<html:option key="archigest.archivo.consultas.tipoConsulta.indirecta" value="2"/>
						</html:select>
					</td>
				</tr>
				<tr id="visibilidad" style="display:none;">
					<td class="tdTitulo" nowrap="nowrap" width="150px">
						<bean:message key="archigest.archivo.solicitudes.visibilidad"/>:&nbsp;
					</td>
					<td class="tdDatos" colspan="1">
						<html:select property="visibilidad">
							<html:option value=""/>
							<html:option key="archigest.archivo.solicitudes.visibilidad.archivo" value="1"/>
							<html:option key="archigest.archivo.solicitudes.visibilidad.noArchivo" value="2"/>
							<html:option key="archigest.archivo.solicitudes.visibilidad.todos" value="3"/>
						</html:select>
					</td>
				</tr>
			</c:if>
			<c:if test="${!editable}">
				<html:hidden property="tipoEntidad"/>
				<html:hidden property="tipoConsulta"/>
				<html:hidden property="visibilidad"/>
				<tr>
					<td class="tdTitulo" nowrap="nowrap">
						<bean:message key="archigest.archivo.cf.TipoEntidad"/>:&nbsp;
					</td>
					<td class="tdDatos" nowrap="nowrap">
						<c:choose>
							<c:when test="${motivoConsultaForm.tipoEntidad == 2}">
								<bean:message key="archigest.archivo.consultas.ciudadano"/>
							</c:when>
							<c:when test="${motivoConsultaForm.tipoEntidad == 3}">
								<bean:message key="archigest.archivo.organo"/>
							</c:when>
							<c:when test="${motivoConsultaForm.tipoEntidad == 1}">
								<bean:message key="archigest.archivo.consultas.investigador"/>
							</c:when>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td class="tdTitulo" nowrap="nowrap">
						<bean:message key="archigest.archivo.consultas.tipoConsulta"/>:&nbsp;
					</td>
					<td class="tdDatos" nowrap="nowrap">
						<c:choose>
							<c:when test="${motivoConsultaForm.tipoConsulta == 1}">
								<bean:message key="archigest.archivo.consultas.tipoConsulta.directa"/>
							</c:when>
							<c:when test="${motivoConsultaForm.tipoConsulta == 2}">
								<bean:message key="archigest.archivo.consultas.tipoConsulta.indirecta"/>
							</c:when>
						</c:choose>						
					</td>
				</tr>
				<c:if test="${motivoConsultaForm.visibilidad > 0}">
					<tr>
						<td class="tdTitulo" nowrap="nowrap">
							<bean:message key="archigest.archivo.solicitudes.visibilidad"/>:&nbsp;
						</td>
						<td class="tdDatos" nowrap="nowrap">
							<c:choose>
								<c:when test="${motivoConsultaForm.visibilidad == 1}">
									<bean:message key="archigest.archivo.solicitudes.visibilidad.archivo"/>
								</c:when>
								<c:when test="${motivoConsultaForm.visibilidad == 2}">
									<bean:message key="archigest.archivo.solicitudes.visibilidad.noArchivo"/>
								</c:when>
								<c:when test="${motivoConsultaForm.visibilidad == 3}">
									<bean:message key="archigest.archivo.solicitudes.visibilidad.todos"/>
								</c:when>
							</c:choose>						
						</td>
					</tr>
				</c:if>
			</c:if>
		</table>
		<script>
			actualizarVisibilidad(document.forms[0].tipoEntidad.value);
		</script>
	</html:form>

</div> <%--bloque --%>


</div> <%--cuerpo_drcha --%>
</div> <%--cuerpo_izda --%>

<h2><span>&nbsp;
</span></h2>

</div> <%--ficha --%>
</div> <%--contenedor_ficha --%>
