<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${estado==ESTADO_ENTRADA_RECHAZADO  || estado==ESTADO_ENTRADA_REENVIADO}">
	<!-- aceptados y rechazados  -->
	<display:table htmlId="bandejaEntrada" name="bandejaEntrada" class="report" cellspacing="0" cellpadding="5"
		id="row" requestURI="<%=requestUri%>" pagesize="10" decorator="es.ieci.tecdoc.isicres.api.intercambioregistral.web.decorator.BandejaEntradaTableDecorator">

		<display:column><input  type="hidden" name="idIntercambioInterno" value="<c:out value='${row.idIntercambioInterno}'/>"/></display:column>

		<display:column sortable="true" property="idIntercambioRegistral" titleKey="intercambioRegistral.tabla.idIntercambioRegistral"></display:column>

		<display:column sortable="true" property="nombreEntidadTramitacion" titleKey="intercambioRegistral.tabla.origen.entidadRegistral"></display:column>

		<display:column sortable="true" property="nombreUnidadTramitacion" titleKey="intercambioRegistral.tabla.origen.unidadTramitacion"></display:column>

		<display:column sortable="true" property="estado" titleKey="intercambioRegistral.tabla.estado"></display:column>

		<display:column property="fechaEstado" format="{0,date,dd-MM-yyyy HH:mm}" titleKey="intercambioRegistral.tabla.fechaEstado"></display:column>

		<display:column property="fechaIntercambioRegistral" format="{0,date,dd-MM-yyyy HH:mm}" titleKey="intercambioRegistral.tabla.fechaIntercambio"></display:column>

		<display:column sortable="true" property="username" titleKey="intercambioRegistral.tabla.usuario"></display:column>

		<display:column sortable="true" property="comentarios" titleKey="intercambioRegistral.tabla.comentarios"></display:column>

		<display:column style="width:40px;">
			<a class="linkInfoSolicitudIntercambio" href="MostrarIntercambioRegistral.do?idIntercambio=<c:out value='${row.idIntercambioInterno}'/>">
				<img src="./images/information.png" alt="<fmt:message key="intercambioRegistral.tabla.masInfo"/>"/>
			</a>
		</display:column>

		<display:setProperty name="basic.msg.empty_list">
			<fmt:message key="intercambioRegistral.tabla.vacia" />
		</display:setProperty>
		<display:setProperty name="paging.banner.item_name">
			<fmt:message key="intercambioRegistral.tabla.item_name" />
		</display:setProperty>
		<display:setProperty name="paging.banner.items_name">
			<fmt:message key="intercambioRegistral.tabla.items_name" />
		</display:setProperty>
	</display:table>
</c:if>
