CREATE OR REPLACE PROCEDURE UPDATECODREF ( ROOT IN VARCHAR2, SEPARATOR IN VARCHAR2, UPDTABLES IN VARCHAR2 ) AS

TIPO_ELEMENTO NUMBER(1);

CURSOR ELEMENTOCF IS
		SELECT TIPO FROM ASGFELEMENTOCF WHERE ID=ROOT;

CURSOR SERIE IS
       SELECT ID
		FROM ASGFELEMENTOCF
		WHERE TIPO=4
		START WITH ID = ROOT
		CONNECT BY PRIOR IDPADRE = ID;

CURSOR FONDO IS
       SELECT ID, GETCODREF(ID,SEPARATOR) AS CODREFERENCIA
		FROM ASGFELEMENTOCF
		WHERE TIPO=2
		START WITH ID = ROOT
		CONNECT BY PRIOR IDPADRE = ID;

CURSOR ELEMENTOS IS
		SELECT ID
		FROM ASGFELEMENTOCF
		WHERE TIPO NOT IN (4,6)
		START WITH ID = ROOT
		CONNECT BY PRIOR ID = IDPADRE;

CURSOR ELEMENTOS_SERIE IS
		SELECT ID
		FROM ASGFELEMENTOCF
		WHERE TIPO=4
		START WITH ID = ROOT
		CONNECT BY PRIOR ID = IDPADRE;

CURSOR CODSREFERENCIA (IDELEMENTO VARCHAR2) IS
       SELECT NVL(GETCODREF(IDFONDO, SEPARATOR),NULL) AS CODREFFONDO,
             GETCODREF(IDELEMENTO, SEPARATOR) AS CODREFERENCIA,
             GETFINCODREFPADRE(IDELEMENTO, SEPARATOR) AS FINALCODREFPADRE
		FROM ASGFELEMENTOCF WHERE ID=IDELEMENTO;
BEGIN
    IF (ROOT IS NOT NULL) THEN
           FOR ELEMENTO IN ELEMENTOCF LOOP
					TIPO_ELEMENTO:=ELEMENTO.TIPO;
			IF (TIPO_ELEMENTO IS NOT NULL) THEN

				IF ((TIPO_ELEMENTO IN (2,3,4,5,6)) AND (UPDTABLES='S')) THEN
					FOR ELEMENTOFONDO IN FONDO LOOP

						-- Actualizar el idfondo a todos sus hijos
						UPDATE ASGFELEMENTOCF SET IDFONDO=ELEMENTOFONDO.ID WHERE ID IN
						(
							SELECT ID
							FROM ASGFELEMENTOCF
							START WITH ID = ROOT
							CONNECT BY PRIOR ID = IDPADRE
						);

						-- Actualizar el idfondo a todas sus series
						UPDATE ASGFSERIE SET IDFONDO=ELEMENTOFONDO.ID WHERE IDELEMENTOCF IN
						(
							SELECT ID
							FROM ASGFELEMENTOCF
							WHERE TIPO=4
							START WITH ID = ROOT
							CONNECT BY PRIOR ID = IDPADRE
						);

						-- Actualizar el idfondo a todas las unidades documentales
						UPDATE ASGFUNIDADDOC SET IDFONDO=ELEMENTOFONDO.ID WHERE IDELEMENTOCF IN
						(
							SELECT ID
							FROM ASGFELEMENTOCF
							WHERE TIPO=6
							START WITH ID = ROOT
							CONNECT BY PRIOR ID = IDPADRE
						);

						-- Actualizar la identificacion a todas las unidades documentales
						UPDATE ASGDUDOCENUI SET IDENTIFICACION=ELEMENTOFONDO.CODREFERENCIA || '-' || SIGNATURAUDOC WHERE IDUNIDADDOC IN
						(
							SELECT ID
							FROM ASGFELEMENTOCF
							WHERE TIPO=6
							START WITH ID = ROOT
							CONNECT BY PRIOR ID = IDPADRE
						);

						-- Actualizar la identificacion de las unidades de instalación
						UPDATE ASGDUINSTALACION SET IDENTIFICACION=ELEMENTOFONDO.CODREFERENCIA || '.' || SIGNATURAUI WHERE ID IN
						(
							SELECT IDUINSTALACION FROM ASGDUDOCENUI WHERE IDUNIDADDOC IN
							(
								SELECT ID
								FROM ASGFELEMENTOCF
								WHERE TIPO=6
								START WITH ID = ROOT
								CONNECT BY PRIOR ID = IDPADRE
							)
						);
					END LOOP;
				END IF;

				IF ((TIPO_ELEMENTO IN (6)) AND (UPDTABLES='S')) THEN
					FOR ELEMENTOSERIE IN SERIE LOOP
						-- Actualizar el idserie a las unidades documentales
						UPDATE ASGFUNIDADDOC SET IDSERIE=ELEMENTOSERIE.ID WHERE IDELEMENTOCF IN
						(
							SELECT ID
							FROM ASGFELEMENTOCF
							WHERE TIPO=6
							START WITH ID = ROOT
							CONNECT BY PRIOR ID = IDPADRE
						);
					END LOOP;
				END IF;

             IF (TIPO_ELEMENTO IN (6)) THEN
					FOR CODSREF IN CODSREFERENCIA(ROOT) LOOP
						UPDATE ASGFELEMENTOCF SET CODREFFONDO=CODSREF.CODREFFONDO, CODREFERENCIA=CODSREF.CODREFERENCIA, FINALCODREFPADRE=CODSREF.FINALCODREFPADRE WHERE ID=ROOT;
					END LOOP;
				ELSE
						FOR ELEMENTO IN ELEMENTOS LOOP
						FOR CODSREF IN CODSREFERENCIA(ELEMENTO.ID) LOOP
							UPDATE ASGFELEMENTOCF SET CODREFFONDO=CODSREF.CODREFFONDO, CODREFERENCIA=CODSREF.CODREFERENCIA, FINALCODREFPADRE=CODSREF.FINALCODREFPADRE WHERE ID=ELEMENTO.ID;
						END LOOP;
					END LOOP;

					FOR ELEMENTO_SERIE IN ELEMENTOS_SERIE LOOP
						FOR CODSREF IN CODSREFERENCIA(ELEMENTO_SERIE.ID) LOOP
							UPDATE ASGFELEMENTOCF SET CODREFFONDO=CODSREF.CODREFFONDO, CODREFERENCIA=CODSREF.CODREFERENCIA, FINALCODREFPADRE=CODSREF.FINALCODREFPADRE WHERE ID=ELEMENTO_SERIE.ID;
							UPDATE ASGFELEMENTOCF SET CODREFFONDO=CODSREF.CODREFFONDO, CODREFERENCIA=CODSREF.CODREFERENCIA || SEPARATOR || CODIGO, FINALCODREFPADRE=NULL WHERE IDPADRE=ELEMENTO_SERIE.ID;
						END LOOP;
					END LOOP;
				END IF;
					END IF;
			END LOOP;
    END IF;
END;
/
