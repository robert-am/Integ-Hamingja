package com.tramisalud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.tramisalud.configuration.Configuration;

public class Cargador extends Thread {
	XmlAnexosReader xar = null;
	XmlAnexos1Reader xar1 = null;
	WSConnector wsConnector = null;
	Configuration conf = null;
	String session = "";
	static Logger log = null;
	boolean error = false;
	String _fileWithPath = "";

	public Cargador(String fileWithPath) {
		log = Logger.getLogger(Cargador.class.getName());
		wsConnector = new WSConnector();
		conf = new Configuration();
		conf.load();
		_fileWithPath = fileWithPath;
	}

	public void run() {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		CargarArchivo(this._fileWithPath);
		time_end = System.currentTimeMillis();
		System.out.println("El Tiempo tomado es de: " + (time_end - time_start)
				+ " milliseconds");
	}

	private void CargarArchivo(String fileWithPath) {
		try {
			loginWS();
			if (fileWithPath.contains("Inconsistencias")) {
				xar1 = new XmlAnexos1Reader(fileWithPath);
				Map<String, Object> dataFile = xar1.getMappingValues();
				String pacienteId = registrarPacienteAnexo1(dataFile);
				registrarAnexos1(pacienteId, dataFile);
			} else {
				xar = new XmlAnexosReader(fileWithPath);
				Map<String, Object> dataFile = xar.getMappingValues();
				if (dataFile.containsKey("CodigoCUPS1")) {
					// Anexo3
					String pacienteId = registrarPaciente(dataFile);
					String anexoId = registrarAnexo3(pacienteId, dataFile);
					for (int i = 1; i <= 20; i++) {
						if (dataFile.get("Descripcion" + i) != null
								&& dataFile.get("Descripcion" + i).toString()
										.trim().length() > 0) {
							registrarCups(anexoId, i, dataFile);
						}
					}
				} else {
					// Anexo2
					String pacienteId = registrarPacienteAnexo2(dataFile);
					registrarAnexo2(pacienteId, dataFile);
				}

			}
			if (error == false) {
				moverArchivo(fileWithPath);
				log.info("Archivo Cargado:" + fileWithPath);
			}
		} catch (Exception e) {
			moverArchivoErroneo(fileWithPath);
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private String registrarAnexos1(String pacienteId,
			Map<String, Object> dataFile) throws ClientProtocolException,
			IOException, ParseException {

		String idAnexo = "";
		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");

		idAnexo = java.util.UUID.randomUUID().toString();
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("new_with_id", 1);
		data.put("id", idAnexo);


		String ipsId = wsConnector.getEntryId(session, "IPS_IPS", "ips_ips.nrodocumento = '" + dataFile.get("IDPrestador") + "' ", "", data2);
		String EpsId = wsConnector.getEntryId(session, "EPS_EPS", "eps_eps.odigoeps = '" + dataFile.get("CodigoEntidad") + "' ", "", data2);
		data.put("name", pacienteId + dataFile.get("Fecha").toString() + dataFile.get("Numero").toString());
		data.put("ips_ips_id_c", ipsId);
		data.put("eps_eps_id_c", EpsId);
		data.put("user_id1_c", 1);
		data.put("primer_nombre", dataFile.get("PrimerNombre"));
		data.put("segundo_nombre", dataFile.get("SegundoNombre"));
		data.put("primer_apellido", dataFile.get("PrimerApellido"));
		data.put("segundo_apellido", dataFile.get("SegundoApellido"));
		data.put("description", dataFile.get("Observaciones"));

		switch (dataFile.get("VariableInconsistencia").toString()){
			case "TipoIdentificacion":
				data.put("tipodocumento",1);
				data.put("tipodocumentolist", dataFile.get("DatoDocumento"));
				break;
			case "FechaNacimiento":
				data.put("fechanacimiento", 1);
				data.put("fechanacimientodate", dataFile.get("DatoDocumento"));
				break;
			case "PrimerNombre":
				data.put("primernombre", 1);
				data.put("primernombretext", dataFile.get("DatoDocumento"));
				break;
			case "SegundoNombre":
				data.put("segundonombre", 1);
				data.put("segundonombretext", dataFile.get("DatoDocumento"));
				break;
			case "PrimerApellido":
				data.put("primerapellido",1);
				data.put("primerapellidotext", dataFile.get("DatoDocumento"));
				break;
			case "SegundoApellido":
				data.put("segundoapellido", 1);
				data.put("segundoapellidotext", dataFile.get("DatoDocumento"));
				break;
			case "Identificacion":
				data.put("numdocumentoidentificacion", 1);
				data.put("segundoapellidotext", dataFile.get("DatoDocumento"));
				break;
		}

		for(int idx = 2; idx <= 20; idx++){
			if(dataFile.containsKey("VariableInconsistencia" + idx )){
				switch (dataFile.get("VariableInconsistencia"+idx).toString()){
					case "TipoIdentificacion":
						data.put("tipodocumento",1);
						data.put("tipodocumentolist", dataFile.get("DatoDocumento"+idx));
						break;
					case "FechaNacimiento":
						data.put("fechanacimiento", 1);
						data.put("fechanacimientodate", dataFile.get("DatoDocumento"+idx));
						break;
					case "PrimerNombre":
						data.put("primernombre", 1);
						data.put("primernombretext", dataFile.get("DatoDocumento"+idx));
						break;
					case "SegundoNombre":
						data.put("segundonombre", 1);
						data.put("segundonombretext", dataFile.get("DatoDocumento"+idx));
						break;
					case "PrimerApellido":
						data.put("primerapellido",1);
						data.put("primerapellidotext", dataFile.get("DatoDocumento"+idx));
						break;
					case "SegundoApellido":
						data.put("segundoapellido", 1);
						data.put("segundoapellidotext", dataFile.get("DatoDocumento"+idx));
						break;
					case "Identificacion":
						data.put("numdocumentoidentificacion", 1);
						data.put("nrodocumentotext", dataFile.get("DatoDocumento"+idx));
						break;
				}
			}
		}

		// Informador
		data.put("nombre_informador", dataFile.get("Nombre").toString());
		data.put("cargo_informador", dataFile.get("Cargo"));
		data.put("tel_informador", dataFile.get("IndicaTel").toString() + dataFile.get("Telefono").toString());
		data.put("celular", dataFile.get("CelularInstitucional"));

		idAnexo = wsConnector.setEntry(session, "basa1_ANEXO1", data);
		log.debug("Anexo1 Registrado: " + idAnexo);
		wsConnector.setRelation(session, "Accounts", pacienteId, "basa1_anexo1_accounts", idAnexo);

		return idAnexo;
	}

	private String registrarPacienteAnexo1(Map<String, Object> dataFile)
			throws ClientProtocolException, IOException, ParseException {
		String id = "";

		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		id = wsConnector.getEntryId(session, "Accounts", "accounts.name = '" + dataFile.get("NumeroIdentificacion") + "'", "", data2);
		// System.out.println(id);
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		if (id == null) {
			data.put("new_with_id", 1);
			data.put("id", dataFile.get("NumeroIdentificacion"));
		} else {
			data.put("id", id);
		}

		data.put("tipo_documento_c", dataFile.get("TipoIdentificacion"));
		data.put("name", dataFile.get("NumeroIdentificacion"));
		data.put("primernombre_c", dataFile.get("PrimerNombre"));
		data.put("segundonombre_c", dataFile.get("SegundoNombre"));
		data.put("primerapellido_c", dataFile.get("PrimerApellido"));
		data.put("segundoapellido_c", dataFile.get("SegundoApellido"));
		data.put("fechanacimiento_c", this.getFormatDate(dataFile.get("FechaNacimiento").toString()));
		data.put("telefonopersonal_c", dataFile.get("TelefonoFijo"));
		data.put("celular_c", dataFile.get("TelefonoCelular"));
		data.put("email1", dataFile.get("CorreoElectronico"));
		data.put("direccionresidencia_c", dataFile.get("DireccionResidenciaHabitual"));
		data.put("dep_departamentos_id_c", dataFile.get("Departamento"));
		data.put("mun_municipio_id1_c", dataFile.get("Departamento").toString()	+ dataFile.get("Municipio").toString());
		data.put("tipocobertura_c", dataFile.get("CoberturaSalud"));

		id = wsConnector.setEntry(session, "Accounts", data);
		log.debug("Paciente Registrado:" + id);

		return id;
	}

	private void moverArchivo(String src) {
		File fileSrc = new File(src);
		File fileDest = new File(conf.getDirectoryBandejaSalida()
				+ File.separator + fileSrc.getName());
		try {
			Files.move(fileSrc.toPath(), fileDest.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			log.debug("Archivo Cargado Exitosamente");
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void moverArchivoErroneo(String src) {
		File fileSrc = new File(src);
		File fileDest = new File(conf.getDirectoryBandejaErroneos()
				+ File.separator + fileSrc.getName());
		try {
			Files.move(fileSrc.toPath(), fileDest.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			log.debug("Archivo no cargado");
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private String registrarAnexo2(String pacienteId,
			Map<String, Object> dataFile) throws ClientProtocolException,
			IOException, ParseException {
		String idAnexo = "";
		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		idAnexo = java.util.UUID.randomUUID().toString();
		// idAnexo = wsConnector.getEntryId(session, "gbian_Anexo2",
		// "gbian_anexo2.id = '" + pacienteId
		// + dataFile.get("Fecha").toString() +
		// dataFile.get("Numero").toString() + "'", "", data2);

		// System.out.println(idAnexo);se
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("new_with_id", 1);

		// if (idAnexo == null) {
		// data.put("new_with_id", 1);
		// data.put("id", pacienteId + dataFile.get("Fecha").toString()
		// + dataFile.get("Numero").toString());
		// } else {
		data.put("id", idAnexo);
		// }

		String ipsId = wsConnector
				.getEntryId(session, "IPS_IPS", "ips_ips.nrodocumento = '"
						+ dataFile.get("IdPrestador") + "' ", "", data2);

		String EpsId = wsConnector.getEntryId(session, "EPS_EPS",
				"eps_eps.odigoeps = '" + dataFile.get("CodigoEntidad") + "' ",
				"", data2);

		data.put("name", pacienteId + dataFile.get("Fecha").toString()
				+ dataFile.get("Numero").toString());
		data.put("ips_ips_id_c", ipsId);
		data.put("eps_eps_id_c", EpsId);
		data.put("forma_envio", "envio_plataforma");

		data.put("fecha_envio",
				this.getFormatDate(dataFile.get("Fecha").toString()));
		data.put("fecha_seg",
				this.getFormatDate(dataFile.get("Fecha").toString()));
		data.put("user_id1_c", 1);
		data.put("paciente_remitido_c", dataFile.get("PacienteRemitido"));
		data.put("clasificacion_c", dataFile.get("ClasificacionTriage"));

		data.put(
				"fecha_ingreso_c",
				this.getFormatDate(dataFile.get("FechaIngreso") + " "
						+ dataFile.get("HoraIngreso"), true));

		data.put("description", dataFile.get("MotivoConsulta").toString());

		data.put("destino_pte", dataFile.get("DestinoPaciente"));
		data.put("origen_atencion", dataFile.get("OrigenAtencion"));

		// Informacion paciente Anexo
		// Informacion Pacientes En Anexo
		data.put("primer_nombre", dataFile.get("PrimerNombre").toString());
		data.put("segundo_nombre", dataFile.get("SegundoNombre").toString());
		data.put("primer_apellido", dataFile.get("PrimerApellido").toString());
		data.put("segundo_apellido", StringUtils.convetToUTF8(dataFile.get(
				"SegundoApellido").toString()));

		// nuevas variables
		data.put("fechanacimiento_c", dataFile.get("FechaNacimiento")
				.toString());
		data.put("tipo_documento_c", dataFile.get("TipoIdentificacion")
				.toString());
		data.put("tipocobertura_c", dataFile.get("CoberturaSalud").toString());
		// data.put("correoemail",dataFile.get("CorreoElectronico").toString());
		data.put("direccionresidencia_c", dataFile.get("DireccionResidencia")
				.toString());
		data.put("telefonopersonal_c", dataFile.get("TelefonoFijo").toString());
		//

		String IpsIdRemitente = wsConnector.getEntryId(session, "IPS_IPS",
				"ips_ips.codigoips = '" + dataFile.get("CodigoPrestador")
						+ "' ", "", data2);

		data.put("prestador_remite_c", IpsIdRemitente);

		String cie10Idpp = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE10Principal")
						+ "' ", "", data2);
		String cie10Id = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE101") + "' ", "",
				data2);
		String cie10Idrel = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE102") + "' ", "",
				data2);

		String cie10Idrel2 = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE103") + "' ", "",
				data2);

		// CIE10
		data.put("cie_cie10_id3_c", cie10Idpp);
		data.put("descripciondiagnostico0_c",
				dataFile.get("DescripcionPrincipal"));

		data.put("cie_cie10_id_c", cie10Id);
		data.put("descripciondediagnostico1_c", dataFile.get("Descripcion1"));

		data.put("cie_cie10_id1_c", cie10Idrel);
		data.put("descripciondediagnostico2_c", dataFile.get("Descripcion2"));

		data.put("cie_cie10_id2_c", cie10Idrel2);
		data.put("descripciondediagnostico3_c", dataFile.get("Descripcion3"));

		// Informador
		data.put("nombre_informador", dataFile.get("Nombre").toString());
		data.put("tel_informador", dataFile.get("Telefono"));
		data.put("celular", dataFile.get("CelularInstitucional"));
		data.put("cargo_informador", dataFile.get("Cargo"));
		data.put("apellidos_informador_c", dataFile.get("Nombre"));

		// El especialista debe ser buscado.
		// data.put("esp_especialista_id_c",
		// "1014fac7-1d13-2b75-89b0-53487471dbe8");

		//

		idAnexo = wsConnector.setEntry(session, "gbian_Anexo2", data);
		log.debug("Anexo2 Registrado: " + idAnexo);
		wsConnector.setRelation(session, "Accounts", pacienteId,
				"gbian_anexo2_accounts", idAnexo);

		return idAnexo;
	}

	private String registrarCups(String anexoId, Integer index,
			Map<String, Object> dataFile) throws ClientProtocolException,
			IOException {
		String idManejoIntegral = "";
		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		String idManejoIntegralC = anexoId + index.toString();
		// idManejoIntegral = wsConnector.getEntryId(session,
		// "GBIA3_ManejoIntegral", "gbia3_manejointegral.id = '" +
		// idManejoIntegralC + "'", "", data2);
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		// idManejoIntegral = null;
		// if (idManejoIntegral == null) {
		data.put("new_with_id", 1);
		data.put("id", idManejoIntegralC);
		// } else {
		// data.put("id", idManejoIntegral);
		// }

		// data.put("name", dataFile.get("Numero"));

		String cupsId = wsConnector.getEntryId(session, "GBICU_CUPS",
		 "gbicu_cups.name = '" + dataFile.get("CodigoCUPS" + index.toString()) + "' ", "", data2);
		if (cupsId == null) {
			data.put("cups_interno_c", dataFile.get("CodigoCUPS" + index.toString()));
		}
		data.put("gbicu_cups_id_c", cupsId);
		data.put("cantidadcups", dataFile.get("Cantidad" + index.toString()));
		data.put("gbi_detalle_c",
				dataFile.get("Descripcion" + index.toString()));
		idManejoIntegral = wsConnector.setEntry(session,
				"GBIA3_ManejoIntegral", data);
		log.debug("Manejo Integral Registrado Registrado: " + idManejoIntegral
				+ " Cups Id: " + dataFile.get("CodigoCUPS" + index.toString()));

		wsConnector.setRelation(session, "GBIA3_ANEXO3", anexoId,
				"gbia3_anexo3_gbia3_manejointegral_1", idManejoIntegral);

		return idManejoIntegral;
	}

	private String registrarAnexo3(String pacienteId,
			Map<String, Object> dataFile) throws ClientProtocolException,
			IOException, ParseException {
		String idAnexo = "";

		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		idAnexo = java.util.UUID.randomUUID().toString();
		// idAnexo = wsConnector.getEntryId(session, "GBIA3_ANEXO3",
		// "gbia3_anexo3.id = '" + pacienteId
		// + dataFile.get("Fecha").toString() +
		// dataFile.get("Numero").toString() + "'", "", data2);
		// System.out.println(idAnexo);
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("new_with_id", 1);

		// if (idAnexo == null) {
		// data.put("new_with_id", 1);
		// data.put("id", pacienteId + dataFile.get("Fecha").toString()
		// + dataFile.get("Numero").toString());
		// } else {
		data.put("id", idAnexo);
		// }

		String ipsId = wsConnector
				.getEntryId(session, "IPS_IPS", "ips_ips.nrodocumento = '"
						+ dataFile.get("IDPrestador") + "' ", "", data2);

		String EpsId = wsConnector.getEntryId(session, "EPS_EPS",
				"eps_eps.odigoeps = '" + dataFile.get("CodigoEntidad") + "' ",
				"", data2);

		data.put("name", pacienteId + dataFile.get("Fecha").toString()
				+ dataFile.get("Numero").toString());
		data.put("ips_ips_id_c", ipsId);
		data.put("eps_eps_id_c", EpsId);

		data.put("fechaenviosolicitud",
				this.getFormatDate(dataFile.get("Fecha").toString()));
		// data.put("formaenviosolicitud", dataFile.get("SegundoApellido"));

		// Informacion Pacientes En Anexo
		data.put("primernombre_c", dataFile.get("PrimerNombre").toString());
		data.put("segundonombre_c", dataFile.get("SegundoNombre").toString());
		data.put("primerapellido_c", dataFile.get("PrimerApellido").toString());
		data.put("segundoapellido_c", dataFile.get("SegundoApellido")
				.toString());

		// Nuevas Variables
		data.put("fechanacimiento_c", dataFile.get("FechaNacimiento")
				.toString());
		data.put("tipo_documento_c", dataFile.get("TipoIdentificacion")
				.toString());
		data.put("tipocobertura_c", dataFile.get("CoberturaSalud").toString());
		data.put("correoemail", dataFile.get("CorreoElectronico").toString());
		data.put("direccionresidencia_c",
				dataFile.get("DireccionResidenciaHabitual").toString());
		data.put("telefonopersonal_c", dataFile.get("TelefonoFijo").toString());

		//

		data.put("origenatencion", dataFile.get("OrigenAtencion"));
		data.put("tiposerviciosolicitado",
				dataFile.get("TipoServiciosSolicitados"));
		data.put("prioridadatencion2", dataFile.get("PrioridadAtencion"));

		data.put("ubicacion", dataFile.get("UbicacionPaciente"));
		data.put("servicio", dataFile.get("ServicioHospitalizacion"));
		data.put("cama", dataFile.get("CamaHospitalizacion"));

		String cie10Idpp = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE10Principal")
						+ "' ", "", data2);
		String cie10Id = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE101") + "' ", "",
				data2);
		String cie10Idrel = wsConnector.getEntryId(session, "CIE_CIE10",
				"cie_cie10.name = '" + dataFile.get("CodigoCIE102") + "' ", "",
				data2);

		data.put("cie_cie10_id_c", cie10Idpp);
		data.put("decodigoprincipal_c", dataFile.get("DescripcionPrincipal"));

		data.put("cie_cie10_id1_c", cie10Id);
		data.put("codiagnostico2_c",
				dataFile.get("ImpresionDiagnosticaDescripcion1"));

		data.put("cie_cie10_id2_c", cie10Idrel);
		data.put("decodigorelacionado_c",
				dataFile.get("ImpresionDiagnosticaDescripcion2"));

		data.put("justificacionclinica", dataFile.get("JustificacionClinica")
				.toString());

		// Informacion Solicitante
		data.put("nombresolicitante", dataFile.get("Nombre") + " " +  dataFile.get("RegistroProfesional"));
		data.put("telefonosolicitante", dataFile.get("Telefono"));
		data.put("celular", dataFile.get("TelefonoCelular"));
		// data.put("correoemail", dataFile.get("CoberturaSalud"));
		data.put("cargoactividadrelacion", dataFile.get("Cargo"));

		//RegistroProfesional

		// idAnexo = wsConnector.setEntry(session, "GBIA3_ANEXO3", data);
		wsConnector.setEntry(session, "GBIA3_ANEXO3", data);
		wsConnector.setRelation(session, "Accounts", pacienteId,
				"accounts_gbia3_anexo3_1", idAnexo);
		log.debug("Anexo3 Registrado:" + idAnexo);

		return idAnexo;
	}

	private String registrarPacienteAnexo2(Map<String, Object> dataFile)
			throws ClientProtocolException, IOException, ParseException {
		String id = "";

		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		id = wsConnector.getEntryId(session, "Accounts", "accounts.name = '"
				+ dataFile.get("NumeroIdentificacion") + "'", "", data2);
		// System.out.println(id);
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		if (id == null) {
			data.put("new_with_id", 1);
			data.put("id", dataFile.get("NumeroIdentificacion"));
		} else {
			data.put("id", id);
		}

		data.put("name", dataFile.get("NumeroIdentificacion"));
		data.put("primernombre_c", dataFile.get("PrimerNombre"));
		data.put("segundonombre_c", dataFile.get("SegundoNombre"));
		data.put("primerapellido_c", dataFile.get("PrimerApellido"));
		data.put("segundoapellido_c", dataFile.get("SegundoApellido"));

		data.put("tipo_documento_c", dataFile.get("TipoIdentificacion"));
		data.put("fechanacimiento_c",
				this.getFormatDate(dataFile.get("FechaNacimiento").toString()));

		data.put("telefonopersonal_c", dataFile.get("TelefonoFijo"));
		data.put("celular_c", dataFile.get("TelefonoCelular"));
		data.put("email1", dataFile.get("CorreoElectronico"));
		data.put("direccionresidencia_c", dataFile.get("DireccionResidencia"));

		data.put("dep_departamentos_id_c", dataFile.get("Departamento"));
		data.put("mun_municipio_id1_c", dataFile.get("Departamento").toString()
				+ dataFile.get("Ciudad").toString());

		data.put("tipocobertura_c", dataFile.get("CoberturaSalud"));

		id = wsConnector.setEntry(session, "Accounts", data);
		log.debug("Paciente Registrado:" + id);

		return id;
	}

	private String registrarPaciente(Map<String, Object> dataFile)
			throws ClientProtocolException, IOException, ParseException {
		String id = "";
		List<String> data2 = new ArrayList<String>();
		data2.add("id");
		data2.add("name");
		id = wsConnector.getEntryId(session, "Accounts", "accounts.name = '"
				+ dataFile.get("NumeroIdentificacion") + "'", "", data2);
		// System.out.println(id);
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		if (id == null) {
			data.put("new_with_id", 1);
			data.put("id", dataFile.get("NumeroIdentificacion"));
		} else {
			data.put("id", id);
		}

		data.put("name", dataFile.get("NumeroIdentificacion"));
		data.put("primernombre_c", dataFile.get("PrimerNombre"));
		data.put("segundonombre_c", dataFile.get("SegundoNombre"));
		data.put("primerapellido_c", dataFile.get("PrimerApellido"));
		data.put("segundoapellido_c", dataFile.get("SegundoApellido"));

		data.put("tipo_documento_c", dataFile.get("TipoIdentificacion"));
		data.put("fechanacimiento_c",
				this.getFormatDate(dataFile.get("FechaNacimiento").toString()));

		data.put("telefonopersonal_c", dataFile.get("TelefonoFijo"));
		data.put("celular_c", dataFile.get("TelefonoCelular"));
		data.put("email1", dataFile.get("CorreoElectronico"));
		data.put("direccionresidencia_c",
				dataFile.get("DireccionResidenciaHabitual"));

		data.put("dep_departamentos_id_c", dataFile.get("Departamento"));
		data.put("mun_municipio_id1_c", dataFile.get("Departamento").toString()
				+ dataFile.get("Ciudad").toString());

		data.put("tipocobertura_c", dataFile.get("CoberturaSalud"));

		id = wsConnector.setEntry(session, "Accounts", data);
		log.debug("Paciente Registrado:" + id);
		return id;
	}

	private void loginWS() throws Exception {
		
		wsConnector.setUsername(conf.getUserName());
		wsConnector.setPassword(conf.getPassword());
		wsConnector.setUrl(conf.getWebServiceURL());
		int intentos = 0;
		//while( !wsConnector.verifyConnection()){
			//wsConnector.setUrl(conf.getWebServiceURLAlternative());
		//	intentos++;
		//	if(intentos >= 3){
		//		throw new Exception("No es posible realizar la conexión con el servidor");
		//	}
		//}
		session = wsConnector.login();
		log.debug("Login Completado SessionID: " + session);
	}

	private String getFormatDate(String date) throws ParseException {
		return getFormatDate(date, false);
	}

	private String getFormatDate(String date, Boolean includeTime)
			throws ParseException {
		SimpleDateFormat fecha;
		if (includeTime == true) {
			fecha = new SimpleDateFormat("yyyy-MM-dd kk:mm");
		} else {
			fecha = new SimpleDateFormat("yyyy-MM-dd");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(fecha.parse(date));
		c.add(Calendar.HOUR, +5);

		Date datec = c.getTime();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
		String date1 = format1.format(datec);

		return date1;

	}
}
