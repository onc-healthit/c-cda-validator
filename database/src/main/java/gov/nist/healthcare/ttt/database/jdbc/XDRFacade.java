package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.database.xdr.XDRRecordImpl;
import gov.nist.healthcare.ttt.database.xdr.XDRRecordInterface;
import gov.nist.healthcare.ttt.database.xdr.Status;
import gov.nist.healthcare.ttt.database.xdr.XDRReportItemImpl;
import gov.nist.healthcare.ttt.database.xdr.XDRReportItemInterface;
import gov.nist.healthcare.ttt.database.xdr.XDRReportItemInterface.ReportType;
import gov.nist.healthcare.ttt.database.xdr.XDRSimulatorImpl;
import gov.nist.healthcare.ttt.database.xdr.XDRSimulatorInterface;
import gov.nist.healthcare.ttt.database.xdr.XDRTestStepImpl;
import gov.nist.healthcare.ttt.database.xdr.XDRTestStepInterface;
import gov.nist.healthcare.ttt.database.xdr.XDRVanillaImpl;
import gov.nist.healthcare.ttt.database.xdr.XDRVanillaInterface;
import gov.nist.healthcare.ttt.misc.Configuration;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created Oct 10, 2014 3:55:56 PM
 *
 * @author mccaffrey
 */
public class XDRFacade extends DatabaseFacade {

	public final static String XDRRECORD_TABLE = "XDRRecord";
	public final static String XDRRECORD_XDRRECORDID = "XDRRecordID";
	public final static String XDRRECORD_USERNAME = "Username";
	public final static String XDRRECORD_TESTCASENUMBER = "TestCaseNumber";
	public final static String XDRRECORD_TIMESTAMP = "Timestamp";
	public final static String XDRRECORD_CRITERIAMET = "CriteriaMet";
	public final static String XDRRECORD_MDHTVALIDATIONREPORT = "MDHTValidationReport";

	public final static String XDRTESTSTEP_TABLE = "XDRTestStep";
	public final static String XDRTESTSTEP_XDRTESTSTEPID = "XDRTestStepID";
	public final static String XDRTESTSTEP_TIMESTAMP = "Timestamp";
	public final static String XDRTESTSTEP_NAME = "Name";
	public final static String XDRTESTSTEP_MESSAGEID = "MessageId";
	public final static String XDRTESTSTEP_DIRECTFROM = "DirectFrom";
	public final static String XDRTESTSTEP_CRITERIAMET = "CriteriaMet";
	public final static String XDRTESTSTEP_HOSTNAME = "Hostname";

	public final static String XDRSIMULATOR_TABLE = "XDRSimulator";
	public final static String XDRSIMULATOR_XDRSIMULATORID = "XDRSimulatorID";
	public final static String XDRSIMULATOR_SIMULATORID = "SimulatorId";
	public final static String XDRSIMULATOR_ENDPOINT = "Endpoint";
	public final static String XDRSIMULATOR_ENDPOINTTLS = "EndpointTLS";

	public final static String XDRREPORTITEM_TABLE = "XDRReportItem";
	public final static String XDRREPORTITEM_XDRREPORTITEMID = "XDRReportItemID";
	public final static String XDRREPORTITEM_REPORT = "Report";
	public final static String XDRREPORTITEM_REPORTTYPE = "ReportType";

	public final static String XDRVANILLA_TABLE = "XDRVanilla";
	public final static String XDRVANILLA_XDRVANILLAID = "XDRVanillaID";
	public final static String XDRVANILLA_REQUEST = "Request";
	public final static String XDRVANILLA_RESPONSE = "Response";
	public final static String XDRVANILLA_SAMLREPORT = "SamlReport";
	public final static String XDRVANILLA_SIMID = "SimId";
	public final static String XDRVANILLA_TIMESTAMP = "Timestamp";

	public XDRFacade(Configuration config) throws DatabaseException {
		super(config);
	}

	// The unique constraint was removed in the database because it didn't allow
	// multiple "NULL" values.
	public String addNewXdrRecord(XDRRecordInterface xdr) throws DatabaseException {

		String validationReportString = "";
		if (xdr.getMDHTValidationReport() != null) {
			validationReportString = xdr.getMDHTValidationReport().replace("\\\"", "&quot;");
		}

		String recordID = UUID.randomUUID().toString();
		xdr.setXdrRecordDatabaseId(recordID);
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + XDRRECORD_TABLE + ' ');
		sql.append("(" + XDRRECORD_XDRRECORDID);
		sql.append(", ");
		sql.append(XDRRECORD_USERNAME);
		sql.append(", ");
		sql.append(XDRRECORD_TESTCASENUMBER);
		sql.append(", ");
		sql.append(XDRRECORD_TIMESTAMP);
		sql.append(", ");
		sql.append(XDRRECORD_CRITERIAMET);
		sql.append(", ");
		sql.append(XDRRECORD_MDHTVALIDATIONREPORT);
		sql.append(") VALUES ('");
		sql.append(recordID);
		sql.append("' , '");
		sql.append(xdr.getUsername());
		sql.append("' , '");
		sql.append(xdr.getTestCaseNumber());
		sql.append("' , '");
		if (xdr.getTimestamp() != null) {
			sql.append(xdr.getTimestamp());
		} else {
			sql.append(Calendar.getInstance().getTimeInMillis());
		}

		sql.append("' , '");
		Status status = xdr.getCriteriaMet();

		//TODO: Either document this mapping or make it an automatic function.
		if (status == Status.PASSED) {
			sql.append("1");
		} else if (status == Status.FAILED) {
			sql.append("0");
		} else if (status == Status.CANCELLED) {
			sql.append("-2");
		} else if (status == Status.PENDING) {
			sql.append("-1");
		} else if (status == Status.MANUAL) {
			sql.append("2");
		} else {
			sql.append("-1");
		}
		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(validationReportString));
		sql.append("');");

		try {
			this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}

		List<XDRTestStepInterface> testSteps = xdr.getTestSteps();
		if (testSteps != null && !testSteps.isEmpty()) {
			Iterator<XDRTestStepInterface> it = testSteps.iterator();
			while (it.hasNext()) {
				this.addNewXdrTestStep(recordID, it.next());
			}
		}

		return recordID;
	}

	public String addNewXdrTestStep(String xdrRecordId, XDRTestStepInterface testStep) throws DatabaseException {

		/*
         Collection<String> messageIds = this.getAllMessageIds();
         if (messageIds.contains(testStep.getMessageId()))
         throw new DatabaseException("Duplicate MessageID is invalid!");
		 */
		String testStepID = UUID.randomUUID().toString();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + XDRTESTSTEP_TABLE + ' ');
		sql.append("(" + XDRTESTSTEP_XDRTESTSTEPID);
		sql.append(", ");
		sql.append(XDRRECORD_XDRRECORDID);
		sql.append(", ");
		sql.append(XDRTESTSTEP_TIMESTAMP);
		sql.append(", ");
		sql.append(XDRTESTSTEP_NAME);
		sql.append(", ");
		sql.append(XDRTESTSTEP_MESSAGEID);
		sql.append(", ");
		sql.append(XDRTESTSTEP_DIRECTFROM);
		sql.append(", ");
		sql.append(XDRTESTSTEP_CRITERIAMET);
		sql.append(", ");
		sql.append(XDRTESTSTEP_HOSTNAME);
		sql.append(") VALUES ('");
		sql.append(testStepID);
		sql.append("' , '");
		sql.append(xdrRecordId);
		sql.append("' , '");
		/*
         if (testStep.getTimestamp() != null) {
         sqlDirectFrom.append(testStep.getTimestamp());
         } 
         else {
		 */

		String timestamp = Long.toString(Calendar.getInstance().getTimeInMillis());
		testStep.setTimestamp(timestamp);
		sql.append(timestamp);
		/*        } */

		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(testStep.getName()));
		sql.append("' ,");
		if (testStep.getMessageId() == null || "".equals(testStep.getMessageId())) {
			sql.append("NULL");
		} else {
			sql.append(" '");
			sql.append(DatabaseConnection.makeSafe(testStep.getMessageId()));
			sql.append("' ");
		}
		sql.append(", '");
		sql.append(DatabaseConnection.makeSafe(testStep.getDirectFrom()));
		sql.append("', '");

		Status status = testStep.getStatus();
		if (status == Status.PASSED) {
			sql.append("1");
		} else if (status == Status.FAILED) {
			sql.append("0");
		} else if (status == Status.CANCELLED) {
			sql.append("-2");
		} else if (status == Status.PENDING) {
			sql.append("-1");
		} else if (status == Status.MANUAL) {
			sql.append("2");
		} else {
			sql.append("-1");
		}

		sql.append("' , '");
		sql.append(testStep.getHostname());

		sql.append("');");

		try {
			this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}
		// TODO: This could be a batch update...

		List<XDRReportItemInterface> reportItems = testStep.getXdrReportItems();
		if (reportItems != null && !reportItems.isEmpty()) {
			Iterator<XDRReportItemInterface> it = reportItems.iterator();
			while (it.hasNext()) {
				this.addNewReportItem(testStepID, it.next());
			}
		}
		/*
         Kept in case we go back to test case -> simulator 0 to many.

         List<XDRSimulatorInterface> simulators = testStep.getXdrSimulators();
         if(simulators != null && !simulators.isEmpty()) {
         Iterator<XDRSimulatorInterface> it = simulators.iterator();
         while(it.hasNext()) {
         this.addNewSimulator(testStepID, it.next());
         }
         }
		 */
		XDRSimulatorInterface simulator = testStep.getXdrSimulator();
		if (simulator != null) {
			this.addNewSimulator(testStepID, simulator);
		}

		return testStepID;
	}

	public String addNewSimulator(XDRSimulatorInterface simulator) throws DatabaseException {
		return this.addNewSimulator(null, simulator);
	}

	public String addNewSimulator(String xdrTestStepID, XDRSimulatorInterface simulator) throws DatabaseException {

		String xdrSimulatorID = UUID.randomUUID().toString();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + XDRSIMULATOR_TABLE + ' ');
		sql.append("(" + XDRSIMULATOR_XDRSIMULATORID);
		sql.append(", ");
		sql.append(XDRTESTSTEP_XDRTESTSTEPID);
		sql.append(", ");
		sql.append(XDRSIMULATOR_SIMULATORID);
		sql.append(", ");
		sql.append(XDRSIMULATOR_ENDPOINT);
		sql.append(", ");
		sql.append(XDRSIMULATOR_ENDPOINTTLS);
		sql.append(") VALUES ('");
		sql.append(xdrSimulatorID);
		sql.append("' , '");
		if (xdrTestStepID != null) {
			sql.append(xdrTestStepID);
		}
		sql.append("' , '");
		sql.append(simulator.getSimulatorId());
		sql.append("' , '");
		sql.append(simulator.getEndpoint());
		sql.append("' , '");
		sql.append(simulator.getEndpointTLS());
		sql.append("');");
		try {
			this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}

		return xdrSimulatorID;

	}

	public String addNewXdrVanilla(XDRVanillaInterface vanilla) throws DatabaseException {

		String vanillaID = UUID.randomUUID().toString();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + XDRVANILLA_TABLE + ' ');
		sql.append("(" + XDRVANILLA_XDRVANILLAID);
		sql.append(", ");
		sql.append(XDRVANILLA_REQUEST);
		sql.append(", ");
		sql.append(XDRVANILLA_RESPONSE);
		sql.append(", ");
		sql.append(XDRVANILLA_SAMLREPORT);
		sql.append(", ");
		sql.append(XDRVANILLA_SIMID);
		sql.append(", ");
		sql.append(XDRVANILLA_TIMESTAMP);
		sql.append(") VALUES ('");
		sql.append(vanillaID);
		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(vanilla.getRequest()));
		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(vanilla.getResponse()));
		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(vanilla.getSamlReport()));
		sql.append("' , '");        
		sql.append(DatabaseConnection.makeSafe(vanilla.getSimId()));
		sql.append("' , '");
		if (vanilla.getTimestamp() != null) {
			sql.append(vanilla.getTimestamp());
		} else {
			sql.append(Calendar.getInstance().getTimeInMillis());
		}
		sql.append("');");
		try {
			this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}
		return vanillaID;

	}

	public String addNewReportItem(String xdrTestStepID, XDRReportItemInterface reportItem) throws DatabaseException {

		String reportItemID = UUID.randomUUID().toString();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + XDRREPORTITEM_TABLE + ' ');
		sql.append("(" + XDRREPORTITEM_XDRREPORTITEMID);
		sql.append(", ");
		sql.append(XDRTESTSTEP_XDRTESTSTEPID);
		sql.append(", ");
		sql.append(XDRREPORTITEM_REPORT);
		sql.append(", ");
		sql.append(XDRREPORTITEM_REPORTTYPE);
		sql.append(") VALUES ('");
		sql.append(reportItemID);
		sql.append("' , '");
		sql.append(xdrTestStepID);
		sql.append("' , '");
		sql.append(DatabaseConnection.makeSafe(reportItem.getReport()));
		sql.append("','");
		if (reportItem.getReportType() == null) {
			sql.append("0");
		} else {
			switch (reportItem.getReportType()) {
			case REQUEST:
				sql.append("1");
				break;
			case RESPONSE:
				sql.append("2");
				break;
			case VALIDATION_REPORT:
				sql.append("3");
				break;
			default:
				sql.append("0");
				break;
			}
		}
		sql.append("');");

		// System.out.println(sqlDirectFrom.toString());
		try {
			this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}
		return reportItemID;
	}

	public List<XDRVanillaInterface> getXDRVanillaBySimId(String simId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT van." + XDRVANILLA_XDRVANILLAID + ' ');
		sql.append("FROM " + XDRVANILLA_TABLE + " van ");
		sql.append("WHERE van." + XDRVANILLA_SIMID + " = '" + simId + "' ");
		sql.append("ORDER BY van." + XDRVANILLA_TIMESTAMP + " DESC ");

		ResultSet result = null;
		List<String> recordIds = new ArrayList<String>();
		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRVANILLA_XDRVANILLAID);
				recordIds.add(recordId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		List<XDRVanillaInterface> records = new ArrayList<XDRVanillaInterface>();
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRVanillaInterface record = this.getXDRVanillaByVanillaRecordId(it.next());
			records.add(record);
		}
		return records;

	}



	public XDRVanillaInterface getLatestXDRVanillaBySimId(String simId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT van." + XDRVANILLA_XDRVANILLAID + ' ');
		sql.append("FROM " + XDRVANILLA_TABLE + " van ");
		sql.append("WHERE van." + XDRVANILLA_SIMID + " = '" + simId + "' ");
		sql.append("ORDER BY van." + XDRVANILLA_TIMESTAMP + " DESC ");
		sql.append("LIMIT 1 ");

		ResultSet result = null;
		XDRVanillaInterface vanilla = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {

				vanilla = this.getXDRVanillaByVanillaRecordId(result.getString(XDRVANILLA_XDRVANILLAID));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		return vanilla;

	}



	public List<XDRRecordInterface> getXDRRecordsByHostname(String hostname) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT rec." + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRRECORD_TABLE + " rec, " + XDRTESTSTEP_TABLE + " ts ");
		sql.append("WHERE rec." + XDRRECORD_XDRRECORDID + " = ts." + XDRRECORD_XDRRECORDID + " AND ");
		sql.append("ts." + XDRTESTSTEP_HOSTNAME + " = '" + hostname + "';");

		ResultSet result = null;
		List<String> recordIds = new ArrayList<String>();

		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIds.add(recordId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		List<XDRRecordInterface> records = new ArrayList<XDRRecordInterface>();
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRRecordInterface record = this.getXDRRecordByRecordId(it.next());
			records.add(record);
		}
		return records;
	}

	public XDRRecordInterface getLatestXDRRecordByHostname(String hostname) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT rec." + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRRECORD_TABLE + " rec, " + XDRTESTSTEP_TABLE + " ts ");
		sql.append("WHERE rec." + XDRRECORD_XDRRECORDID + " = ts." + XDRRECORD_XDRRECORDID + " AND ");
		sql.append("ts." + XDRTESTSTEP_HOSTNAME + " = '" + hostname + "' ");
		sql.append("ORDER BY rec." + XDRRECORD_TIMESTAMP + " DESC ");
		sql.append("LIMIT 1;");

		ResultSet result = null;
		String recordId = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				recordId = result.getString(XDRRECORD_XDRRECORDID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		if (recordId == null) {
			return null;
		}
		return this.getXDRRecordByRecordId(recordId);
	}

	// TODO: A lot of redundency here.  Need to clean up.
	public XDRRecordInterface getLatestXDRRecordByDirectFrom(String directFrom) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT rec." + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRRECORD_TABLE + " rec, " + XDRTESTSTEP_TABLE + " ts ");
		sql.append("WHERE rec." + XDRRECORD_XDRRECORDID + " = ts." + XDRRECORD_XDRRECORDID + " AND ");
		sql.append("ts." + XDRTESTSTEP_DIRECTFROM + " = '" + directFrom + "' ");
		sql.append("ORDER BY rec." + XDRRECORD_TIMESTAMP + " DESC ");
		sql.append("LIMIT 1;");

		ResultSet result = null;
		String recordId = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				recordId = result.getString(XDRRECORD_XDRRECORDID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		if (recordId == null) {
			return null;
		}
		return this.getXDRRecordByRecordId(recordId);
	}

	private XDRVanillaInterface getXDRVanillaByVanillaRecordId(String vanillaRecordId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRVANILLA_TABLE + ' ');
		sql.append("WHERE " + XDRVANILLA_XDRVANILLAID + " = '" + vanillaRecordId + "';");

		ResultSet result = null;
		XDRVanillaImpl vanilla = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if(result.next()) {
				vanilla = new XDRVanillaImpl();
				vanilla.setRequest(result.getString(XDRVANILLA_REQUEST));
				vanilla.setResponse(result.getString(XDRVANILLA_RESPONSE));
				vanilla.setSamlReport(result.getString(XDRVANILLA_SAMLREPORT));
				vanilla.setSimId(result.getString(XDRVANILLA_SIMID));
				vanilla.setTimestamp(result.getString(XDRVANILLA_TIMESTAMP));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return vanilla;

	}

	public XDRRecordInterface getXDRRecordByRecordId(String xdrRecordId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRRECORD_TABLE + ' ');
		sql.append("WHERE " + XDRRECORD_XDRRECORDID + " = '" + xdrRecordId + "';");

		ResultSet result = null;
		XDRRecordImpl record = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				record = new XDRRecordImpl();
				record.setXdrRecordDatabaseId(result.getString(XDRRECORD_XDRRECORDID));
				record.setUsername(result.getString(XDRRECORD_USERNAME));
				record.setTestCaseNumber(result.getString(XDRRECORD_TESTCASENUMBER));
				record.setTimestamp(result.getString(XDRRECORD_TIMESTAMP));

				int i = result.getInt(XDRRECORD_CRITERIAMET);
				if (i == 1) {
					record.setStatus(Status.PASSED);
				} else if (i == 0) {
					record.setStatus(Status.FAILED);
				} else if (i == -2) {
					record.setStatus(Status.CANCELLED);
				} else if (i == -1) {
					record.setStatus(Status.PENDING);
				} else if (i == 2) {
					record.setStatus(Status.MANUAL);
				} else {
					record.setStatus(Status.PENDING);
				}

				if (result.getString(XDRRECORD_MDHTVALIDATIONREPORT) != null) {
					record.setMDHTValidationReport(result.getString(XDRRECORD_MDHTVALIDATIONREPORT).replace("&quot;", "\\\""));
				}

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		StringBuilder sqlChildren = new StringBuilder();
		sqlChildren.append("SELECT " + XDRTESTSTEP_XDRTESTSTEPID + ' ');
		sqlChildren.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sqlChildren.append("WHERE " + XDRRECORD_XDRRECORDID + " = '" + xdrRecordId + "' ");
		sqlChildren.append("ORDER BY " + XDRTESTSTEP_TIMESTAMP + " ASC;");

		ResultSet resultChildren = null;
		List<XDRTestStepInterface> testSteps = new ArrayList<XDRTestStepInterface>();
		List<String> testStepIds = new ArrayList<String>();

		try {
			resultChildren = this.getConnection().executeQuery(sqlChildren.toString());
			while (resultChildren.next()) {
				String testStepId = resultChildren.getString(XDRTESTSTEP_XDRTESTSTEPID);
				testStepIds.add(testStepId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = testStepIds.iterator();
		
		while (it.hasNext()) {
			if(it != null){
				XDRTestStepInterface testStep = (XDRTestStepInterface) this.getXDRTestStepByTestStepId(it.next());
				testSteps.add(testStep);
			}
		}
		record.setTestSteps(testSteps);
		return record;
	}

	//returns null if none
	public XDRTestStepInterface getXDRTestStepByTestStepId(String xdrTestStepId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepId + "';");

		ResultSet result = null;
		XDRTestStepImpl testStep = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				testStep = new XDRTestStepImpl();
				testStep.setXdrTestStepID(result.getString(XDRTESTSTEP_XDRTESTSTEPID));
				// testStep.setTimestamp(new Timestamp(Long.parseLong(result.getString(XDRTESTSTEP_TIMESTAMP))));
				testStep.setTimestamp(result.getString(XDRTESTSTEP_TIMESTAMP));
				testStep.setName(result.getString(XDRTESTSTEP_NAME));
				testStep.setMessageId(result.getString(XDRTESTSTEP_MESSAGEID));
				testStep.setDirectFrom(result.getString(XDRTESTSTEP_DIRECTFROM));
				int i = result.getInt(XDRTESTSTEP_CRITERIAMET);
				if (i == 1) {
					testStep.setStatus(Status.PASSED);
				} else if (i == 0) {
					testStep.setStatus(Status.FAILED);
				} else if (i == -2) {
					testStep.setStatus(Status.CANCELLED);
				} else if (i == -1) {
					testStep.setStatus(Status.PENDING);
				} else if (i == 2) {
					testStep.setStatus(Status.MANUAL);
				} else {
					testStep.setStatus(Status.PENDING);
				}
				testStep.setHostname(result.getString(XDRTESTSTEP_HOSTNAME));

			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		StringBuilder sqlReportItem = new StringBuilder();
		sqlReportItem.append("SELECT " + XDRREPORTITEM_XDRREPORTITEMID + ' ');
		sqlReportItem.append("FROM " + XDRREPORTITEM_TABLE + ' ');
		sqlReportItem.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepId + "';");

		ResultSet resultReportItem = null;
		List<XDRReportItemInterface> reportItems = new ArrayList<XDRReportItemInterface>();
		List<String> reportItemIds = new ArrayList<String>();

		try {
			resultReportItem = this.getConnection().executeQuery(sqlReportItem.toString());
			while (resultReportItem.next()) {
				String reportItemId = resultReportItem.getString(XDRREPORTITEM_XDRREPORTITEMID);
				reportItemIds.add(reportItemId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = reportItemIds.iterator();
		while (it.hasNext()) {
			XDRReportItemImpl reportItem = (XDRReportItemImpl) this.getXDRReportItemByReportItemId(it.next());
			reportItems.add(reportItem);
		}
		testStep.setXdrReportItems(reportItems);

		StringBuilder sqlSimulator = new StringBuilder();
		sqlSimulator.append("SELECT " + XDRSIMULATOR_XDRSIMULATORID + ' ');
		sqlSimulator.append("FROM " + XDRSIMULATOR_TABLE + ' ');
		sqlSimulator.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepId + "';");

		/*
         Kept in case we go back to test case -> simulator 0 to many.

         ResultSet resultSimulator = null;
         List<XDRSimulatorInterface> simulators = new ArrayList<XDRSimulatorInterface>();

         try {
         resultSimulator = this.getConnection().executeQuery(sqlSimulator.toString());
         while (resultSimulator.next()) {
         XDRSimulatorImpl simulator = (XDRSimulatorImpl) this.getXDRSimulatorBySimulatorId(resultSimulator.getString(XDRSIMULATOR_SIMULATORID));
         simulators.add(simulator);
         }
         } catch (Exception e) {
         e.printStackTrace();
         throw new DatabaseException(e.getMessage());
         }

         testStep.setXdrSimulators(simulators);
		 */
		ResultSet resultSimulator = null;
		XDRSimulatorInterface simulator = null;

		try {
			resultSimulator = this.getConnection().executeQuery(sqlSimulator.toString());
			if (resultSimulator.next()) {
				simulator = this.getXDRSimulatorBySimulatorId(resultSimulator.getString(XDRSIMULATOR_XDRSIMULATORID));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		if (simulator != null) {
			testStep.setXdrSimulator(simulator);
		}

		return testStep;

	}

	public XDRSimulatorInterface getSimulatorBySimulatorId(String simulatorId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRSIMULATOR_TABLE + ' ');
		sql.append("WHERE " + XDRSIMULATOR_SIMULATORID + " = '" + simulatorId + "';");

		ResultSet result = null;
		XDRSimulatorImpl simulator = null;
		try {
			// TODO this is a duplicate of the code below.  Make this a separate method.
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				simulator = new XDRSimulatorImpl();
				simulator.setXDRSimulatorID(result.getString(XDRSIMULATOR_XDRSIMULATORID));
				simulator.setSimulatorId(result.getString(XDRSIMULATOR_SIMULATORID));
				simulator.setEndpoint(result.getString(XDRSIMULATOR_ENDPOINT));
				simulator.setEndpointTLS(result.getString(XDRSIMULATOR_ENDPOINTTLS));
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return simulator;

	}

	// returns null if none
	private XDRSimulatorInterface getXDRSimulatorBySimulatorId(String xdrSimulatorID) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRSIMULATOR_TABLE + ' ');
		sql.append("WHERE " + XDRSIMULATOR_XDRSIMULATORID + " = '" + xdrSimulatorID + "';");

		ResultSet result = null;
		XDRSimulatorImpl simulator = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				simulator = new XDRSimulatorImpl();
				simulator.setXDRSimulatorID(result.getString(XDRSIMULATOR_XDRSIMULATORID));
				simulator.setSimulatorId(result.getString(XDRSIMULATOR_SIMULATORID));
				simulator.setEndpoint(result.getString(XDRSIMULATOR_ENDPOINT));
				simulator.setEndpointTLS(result.getString(XDRSIMULATOR_ENDPOINTTLS));
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return simulator;
	}

	// returns null if none
	public XDRReportItemInterface getXDRReportItemByReportItemId(String xdrReportItemId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRREPORTITEM_TABLE + ' ');
		sql.append("WHERE " + XDRREPORTITEM_XDRREPORTITEMID + " = '" + xdrReportItemId + "';");

		ResultSet result = null;
		XDRReportItemImpl reportItem = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				reportItem = new XDRReportItemImpl();
				reportItem.setXDRReportItemID(result.getString(XDRREPORTITEM_XDRREPORTITEMID));
				reportItem.setReport(result.getString(XDRREPORTITEM_REPORT));
				int type = result.getInt(XDRREPORTITEM_REPORTTYPE);
				switch (type) {
				case 0:
					reportItem.setReportType(ReportType.UNCLASSIFIED);
					break;
				case 1:
					reportItem.setReportType(ReportType.REQUEST);
					break;
				case 2:
					reportItem.setReportType(ReportType.RESPONSE);
					break;
				case 3:
					reportItem.setReportType(ReportType.VALIDATION_REPORT);
					break;
				default:
					reportItem.setReportType(ReportType.UNCLASSIFIED);
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return reportItem;
	}

	// simulator ID is not required to be unique in database, so the latest Record is returned.
	public XDRRecordInterface getLatestXDRRecordBySimulatorId(String simulatorId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ts." + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + " ts, ");
		sql.append(XDRSIMULATOR_TABLE + " s ");
		sql.append("WHERE s." + XDRSIMULATOR_SIMULATORID + " = '" + DatabaseConnection.makeSafe(simulatorId) + "' AND ");
		sql.append("ts." + XDRTESTSTEP_XDRTESTSTEPID + " = s." + XDRTESTSTEP_XDRTESTSTEPID + ' ');
		sql.append("ORDER BY " + XDRRECORD_TIMESTAMP + " DESC;");

		ResultSet result = null;
		XDRRecordInterface record = new XDRRecordImpl();
		String recordId = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				recordId = result.getString(XDRRECORD_XDRRECORDID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		if (recordId != null) {
			record = this.getXDRRecordByRecordId(recordId);
		}
		return record;

	}

	// No longer needed?
	public Collection<String> getAllMessageIds() throws DatabaseException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT DISTINCT " + XDRTESTSTEP_MESSAGEID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE);

		ResultSet result = null;
		Collection<String> messageIds = new ArrayList<String>();

		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String messageId = result.getString(XDRTESTSTEP_MESSAGEID);
				messageIds.add(messageId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return messageIds;

	}

	public List<XDRRecordInterface> getXDRRecordsByMessageId(String messageId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "';");

		ResultSet result = null;
		List<XDRRecordInterface> records = new ArrayList<XDRRecordInterface>();
		List<String> recordIds = new ArrayList<String>();

		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIds.add(recordId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRRecordInterface record = this.getXDRRecordByRecordId(it.next());
			records.add(record);
		}
		return records;
	}

	// TODO: A lot of redundency here.  Need to clean up.
	public List<XDRRecordInterface> getXDRRecordsByDirectFrom(String directFrom) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_DIRECTFROM + " = '" + DatabaseConnection.makeSafe(directFrom) + "';");

		ResultSet result = null;
		List<XDRRecordInterface> records = new ArrayList<XDRRecordInterface>();
		List<String> recordIds = new ArrayList<String>();

		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIds.add(recordId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRRecordInterface record = this.getXDRRecordByRecordId(it.next());
			records.add(record);
		}
		return records;
	}

	public XDRRecordInterface getLatestXDRRecordBySimulatorAndDirectAddress(String simulatorId, String directFrom) throws DatabaseException {
		StringBuilder sqlDirectFrom = new StringBuilder();
		sqlDirectFrom.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sqlDirectFrom.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sqlDirectFrom.append("WHERE " + XDRTESTSTEP_DIRECTFROM + " = '" + DatabaseConnection.makeSafe(directFrom) + "';");

		ResultSet result = null;
		List<String> recordIdsByDirectFrom = new ArrayList<String>();

		try {
			result = this.getConnection().executeQuery(sqlDirectFrom.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIdsByDirectFrom.add(recordId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		StringBuilder sqlSim = new StringBuilder();
		sqlSim.append("SELECT ts." + XDRRECORD_XDRRECORDID + ' ');
		sqlSim.append("FROM " + XDRTESTSTEP_TABLE + " ts, ");
		sqlSim.append(XDRSIMULATOR_TABLE + " s ");
		sqlSim.append("WHERE s." + XDRSIMULATOR_SIMULATORID + " = '" + DatabaseConnection.makeSafe(simulatorId) + "' AND ");
		sqlSim.append("ts." + XDRTESTSTEP_XDRTESTSTEPID + " = s." + XDRTESTSTEP_XDRTESTSTEPID + ' ');
		sqlSim.append("ORDER BY " + XDRRECORD_TIMESTAMP + " DESC;");

		ResultSet resultSim = null;

		List<String> recordIdsBySim = new ArrayList<String>();

		try {
			resultSim = this.getConnection().executeQuery(sqlSim.toString());
			while (resultSim.next()) {
				recordIdsBySim.add(resultSim.getString(XDRRECORD_XDRRECORDID));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		if (recordIdsBySim.size() == 0 || recordIdsByDirectFrom.size() == 0) {
			return null;
		}

		Iterator<String> itSim = recordIdsBySim.iterator();
		while (itSim.hasNext()) {
			String recordIdBySim = itSim.next();
			if (recordIdsByDirectFrom.contains(recordIdBySim)) {
				return this.getXDRRecordByRecordId(recordIdBySim);
			}
		}
		return null;
	}

	private String getXDRTestStepIdByMessageId(String messageId) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + XDRTESTSTEP_XDRTESTSTEPID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "';");

		ResultSet result = null;
		String xdrTestStepId = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				xdrTestStepId = result.getString(XDRTESTSTEP_XDRTESTSTEPID);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return xdrTestStepId;
	}

	// The List comes back in descending order so the latest record is the first and the oldest record is the last.
	public List<XDRRecordInterface> getXDRRecordsByUsernameTestCase(String username, String testCaseNumber) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * ");
		sql.append("FROM " + XDRRECORD_TABLE + ' ');
		sql.append("WHERE " + XDRRECORD_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' AND ");
		sql.append(XDRRECORD_TESTCASENUMBER + " = '" + DatabaseConnection.makeSafe(testCaseNumber) + "' ");
		sql.append("ORDER BY " + XDRRECORD_TIMESTAMP + " DESC;");

		ResultSet result = null;
		List<XDRRecordInterface> records = new ArrayList<XDRRecordInterface>();
		List<String> recordIds = new ArrayList<String>();

		// TODO: This is inefficient because it repeats an unnecessary step.
		// TODO: Refactor this so getXDRRecord* uses same library method of
		// converting ResultSet to XDRRecord object.
		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIds.add(recordId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRRecordInterface record = this.getXDRRecordByRecordId(it.next());
			records.add(record);
		}

		return records;

	}

	// returns null if none found.
	public XDRRecordInterface getLatestXDRRecordByUsernameTestCase(String username, String testCaseNumber) throws DatabaseException {

		List<XDRRecordInterface> records = this.getXDRRecordsByUsernameTestCase(username, testCaseNumber);
		if (records == null || records.isEmpty()) {
			return null;
		}

		return records.get(0);

	}

	public List<XDRRecordInterface> getXDRRecordsByUsername(String username) throws DatabaseException {
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRRECORD_TABLE + ' ');
		sql.append("WHERE " + XDRRECORD_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "';");

		ResultSet result = null;
		List<XDRRecordInterface> records = new ArrayList<XDRRecordInterface>();
		List<String> recordIds = new ArrayList<String>();

		// TODO: This is inefficient because it repeats an unnecessary step.
		// TODO: Refactor this so getXDRRecord* uses same library method of
		// converting ResultSet to XDRRecord object.
		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIds.add(recordId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> it = recordIds.iterator();
		while (it.hasNext()) {
			XDRRecordInterface record = this.getXDRRecordByRecordId(it.next());
			records.add(record);
		}

		return records;
	}

	public String getTestStepRecordId(String testStepId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + testStepId + "';");

		ResultSet result = null;
		String recordId = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				recordId = result.getString(XDRRECORD_XDRRECORDID);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		return recordId;

	}

	// MessageID in the database has a unique constraint.
	//returns null if none
	private XDRRecordInterface getXDRRecordByMessageId(String messageId) throws DatabaseException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRTESTSTEP_TABLE + " ");
		sql.append("WHERE " + XDRTESTSTEP_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "';");

		ResultSet result = null;
		String recordId = null;
		try {
			result = this.getConnection().executeQuery(sql.toString());
			if (result.next()) {
				recordId = result.getString(XDRRECORD_XDRRECORDID);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

		XDRRecordInterface record = null;

		if (recordId != null) {
			record = this.getXDRRecordByRecordId(recordId);
		}

		return record;

	}

	public int removeXdrRecord(String xdrRecordID) throws DatabaseException {

		ResultSet resultTestStep = null;
		Collection<String> testStepIDs = new ArrayList<String>();
		StringBuilder sqlTestStep = new StringBuilder();
		sqlTestStep.append("SELECT " + XDRTESTSTEP_XDRTESTSTEPID + ' ');
		sqlTestStep.append("FROM " + XDRTESTSTEP_TABLE + ' ');
		sqlTestStep.append("WHERE " + XDRRECORD_XDRRECORDID + " = '" + xdrRecordID + "';");

		try {
			resultTestStep = this.getConnection().executeQuery(sqlTestStep.toString());
			while (resultTestStep.next()) {
				String testStepId = resultTestStep.getString(XDRTESTSTEP_XDRTESTSTEPID);
				testStepIDs.add(testStepId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> itTestStep = testStepIDs.iterator();
		while (itTestStep.hasNext()) {
			this.removeXdrTestStep(itTestStep.next());
		}

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + XDRRECORD_TABLE + ' ');
		sql.append("WHERE " + XDRRECORD_XDRRECORDID + " = '" + xdrRecordID + "';");

		int i = 0;
		try {
			i = this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}

		return i;
	}

	public int removeXdrTestStep(String xdrTestStepID) throws DatabaseException {

		ResultSet resultSimulator = null;
		Collection<String> simulatorIDs = new ArrayList<String>();
		StringBuilder sqlSimulator = new StringBuilder();
		sqlSimulator.append("SELECT " + XDRSIMULATOR_XDRSIMULATORID + ' ');
		sqlSimulator.append("FROM " + XDRSIMULATOR_TABLE + ' ');
		sqlSimulator.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepID + "';");

		try {
			resultSimulator = this.getConnection().executeQuery(sqlSimulator.toString());
			while (resultSimulator.next()) {
				String simulatorId = resultSimulator.getString(XDRSIMULATOR_XDRSIMULATORID);
				simulatorIDs.add(simulatorId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> itSimulator = simulatorIDs.iterator();
		while (itSimulator.hasNext()) {
			this.removeXdrSimulator(itSimulator.next());
		}

		ResultSet resultReportItem = null;
		Collection<String> reportItemIDs = new ArrayList<String>();
		StringBuilder sqlReportItem = new StringBuilder();
		sqlReportItem.append("SELECT " + XDRREPORTITEM_XDRREPORTITEMID + ' ');
		sqlReportItem.append("FROM " + XDRREPORTITEM_TABLE + ' ');
		sqlReportItem.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepID + "';");

		try {
			resultReportItem = this.getConnection().executeQuery(sqlReportItem.toString());
			while (resultReportItem.next()) {
				String reportItemId = resultReportItem.getString(XDRREPORTITEM_XDRREPORTITEMID);
				reportItemIDs.add(reportItemId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> itReportItem = reportItemIDs.iterator();
		while (itReportItem.hasNext()) {
			this.removeReportItem(itReportItem.next());
		}

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + XDRTESTSTEP_TABLE + ' ');
		sql.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + xdrTestStepID + "';");

		int i = 0;
		try {
			i = this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}

		return i;
	}

	public int removeXdrSimulator(String xdrSimulatorID) throws DatabaseException {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + XDRSIMULATOR_TABLE + ' ');
		sql.append("WHERE " + XDRSIMULATOR_XDRSIMULATORID + " = '" + xdrSimulatorID + "';");
		int i = 0;
		try {
			i = this.getConnection().executeUpdate(sql.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DatabaseException(ex.getMessage());
		}
		return i;

	}

	public int removeReportItem(String reportItemID) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + XDRREPORTITEM_TABLE + ' ');
		sql.append("WHERE " + XDRREPORTITEM_XDRREPORTITEMID + " = '" + reportItemID + "';");
		int i = 0;

		try {
			i = this.getConnection().executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return i;
	}

	public int removeAllByUsername(String username) throws DatabaseException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + XDRRECORD_XDRRECORDID + ' ');
		sql.append("FROM " + XDRRECORD_TABLE + ' ');
		sql.append("WHERE " + XDRRECORD_USERNAME + " = '" + username + "';");

		List<String> recordIDs = new ArrayList<String>();
		ResultSet result = null;

		try {
			result = this.getConnection().executeQuery(sql.toString());
			while (result.next()) {
				String recordId = result.getString(XDRRECORD_XDRRECORDID);
				recordIDs.add(recordId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		Iterator<String> itRecord = recordIDs.iterator();
		int i = 0;
		while (itRecord.hasNext()) {
			i += this.removeXdrRecord(itRecord.next());
		}
		return i;
	}

	//private String lookUpRecordId(XDRRecordImpl record) throws DatabaseException {
	//StringBuilder sqlDirectFrom = new StringBuilder();
	//sql.append("SELECT " + XDRRECORD_XDRRECORDID + " ");
	//sql.append("FROM " + XDRRECORD_TABLE + " " );
	//sql.append("WHERE ")
	//}
	// returns void because I can't think of what should be returned if no exception run
	public void updateXDRRecord(XDRRecordInterface record) throws DatabaseException {

		// TODO: This should be done as a db roll-back instead of manually like this...
		//get a copy for safekeeping...
		String recordId = record.getXdrRecordDatabaseId();
		if (recordId == null) {
			this.addNewXdrRecord(record);
			return;
		}
		XDRRecordImpl original = (XDRRecordImpl) this.getXDRRecordByRecordId(recordId);
		try {
			this.removeXdrRecord(record.getXdrRecordDatabaseId());
		} catch (DatabaseException e) {
			// if it doesn't work, put the original back in
			this.addNewXdrRecord(original);
			throw new DatabaseException(e.getMessage());
		}
		this.addNewXdrRecord(record);

	}

	public void updateXDRTestStep(XDRTestStepInterface testStep) throws DatabaseException {

		//this.removeXdrTestStep(testStep.getXdrTestStepID());
		//String recordId = this.getTestStepRecordId(testStep.getXdrTestStepID());
		//this.addNewXdrTestStep(recordId, testStep);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE " + XDRTESTSTEP_TABLE + ' ');
		sql.append("SET ");
		sql.append(XDRTESTSTEP_TIMESTAMP + " = '" + testStep.getTimestamp() + "', ");
		sql.append(XDRTESTSTEP_NAME + " = '" + testStep.getName() + "', ");
		sql.append(XDRTESTSTEP_MESSAGEID + " = '" + testStep.getMessageId() + "', ");

		sql.append(XDRTESTSTEP_CRITERIAMET + " = '");
		Status status = testStep.getStatus();

		//TODO: Either document this mapping or make it an automatic function.
		if (status == Status.PASSED) {
			sql.append("1");
		} else if (status == Status.FAILED) {
			sql.append("0");
		} else if (status == Status.CANCELLED) {
			sql.append("-2");
		} else if (status == Status.PENDING) {
			sql.append("-1");
		} else if (status == Status.MANUAL) {
			sql.append("2");
		} else {
			sql.append("-1");
		}
		sql.append("', ");

		sql.append(XDRTESTSTEP_HOSTNAME + " = '" + testStep.getHostname() + "' ");
		sql.append("WHERE " + XDRTESTSTEP_XDRTESTSTEPID + " = '" + getXDRTestStepIdByMessageId(testStep.getMessageId()) + "';");

		try {
			this.getConnection().executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}

	}

	public static void main(String[] args) {
		try {
			/*
            XDRRecordImpl record = new XDRRecordImpl();
            record.setStatus(Status.PENDING);
            record.setTestCaseNumber("1");
            record.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()).toString());
            record.setUsername("Username");
            record.setMDHTValidationReport("report goes here, guys!");

            XDRTestStepImpl testStep = new XDRTestStepImpl();
            // testStep.setMessageId("message1");
            record.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()).toString());

            XDRSimulatorImpl simulator = new XDRSimulatorImpl();
            simulator.setSimulatorId("endpointstandalone");
            simulator.setEndpoint("nonsecureendpoint");
            simulator.setEndpointTLS("tlsendpoint");

            // List<XDRSimulatorInterface> simulators = new ArrayList<XDRSimulatorInterface>();
            // simulators.add(simulator);
            testStep.setXdrSimulator(simulator);
            testStep.setName("Say my name.");
            testStep.setHostname("localhost");
            testStep.setMessageId(UUID.randomUUID().toString());
            testStep.setDirectFrom("from@direct.com");
            XDRReportItemImpl reportItem = new XDRReportItemImpl();
            String report = "ignore me!"; //readFile("/home/mccaffrey/body.txt", Charset.defaultCharset());
            reportItem.setReport(report);

            reportItem.setReportType(ReportType.REQUEST);

            List<XDRReportItemInterface> items = new ArrayList<XDRReportItemInterface>();

            items.add(reportItem);
            testStep.setXdrReportItems(items);

            List<XDRTestStepInterface> steps = new ArrayList<XDRTestStepInterface>();
            steps.add(testStep);
			 */
			Configuration config = new Configuration();
			config.setDatabaseHostname("localhost");
			config.setDatabaseName("direct");
			config.setDatabaseUsername("root");

			XDRFacade facade = new XDRFacade(config);

			XDRVanillaImpl vanilla = new XDRVanillaImpl();
			vanilla.setRequest("request report");
			vanilla.setResponse("response report");
			vanilla.setSamlReport("SAML report");
			vanilla.setSimId("EDGE_VANILLA");

			//facade.addNewXdrVanilla(vanilla);
			/*
            List<XDRVanillaInterface> records = facade.getXDRVanillaBySimId("EDGE_VANILLA");

            Iterator<XDRVanillaInterface> it = records.iterator();
            while(it.hasNext()) {
                XDRVanillaInterface vanilla2 = it.next();

                System.out.println(" --- ");
                System.out.println(vanilla2.getRequest());
                System.out.println(vanilla2.getResponse());
                System.out.println(vanilla2.getSamlReport());
                System.out.println(vanilla2.getSimId());
                System.out.println(vanilla2.getTimestamp());
                System.out.println(" --- ");

            }
			 */


			XDRVanillaInterface vanilla2 = facade.getLatestXDRVanillaBySimId("EDGE_VANILLA");


			if(vanilla2 != null) {
				//   XDRVanillaInterface vanilla2 = it.next();

				System.out.println(" --- ");
				System.out.println(vanilla2.getRequest());
				System.out.println(vanilla2.getResponse());
				System.out.println(vanilla2.getSamlReport());
				System.out.println(vanilla2.getSimId());
				System.out.println(vanilla2.getTimestamp());
				System.out.println(" --- ");

			}



			/*
            facade.addNewXdrRecord(record);
            record.setMDHTValidationReport("new report', guys");
            facade.updateXDRRecord(record);

            System.out.println(facade.getXDRRecordByRecordId("30769be4-eaf2-48af-b705-ee9f65779260").getMDHTValidationReport());
            //   facade.addNewXdrRecord(record);

            //XDRRecordInterface get = facade.getLatestXDRRecordBySimulatorAndDirectAddress("endpointstandalone", "from@direct.com");
            //System.out.println(get.getTimestamp());
            //facade.updateXDRRecord(record);
            //record.setTestSteps(steps);
            //facade.updateXDRRecord(record);
            //facade.updateXDRRecord(record);
            //facade.updateXDRRecord(record);
            //facade.updateXDRRecord(record);
            //List<XDRRecordInterface> getRecord = facade.getXDRRecordsByDirectFrom("from@direct.com");
            //System.out.append(Integer.toString(getRecord.size()));
            /*
            testStep.setName("NEW NAME!!!");
            facade.updateXDRTestStep(testStep);

            System.out.println("size = " + facade.getXDRRecordsByHostname("localhost5").size());

            System.out.println("timestamp = " + facade.getLatestXDRRecordByHostname("localhost").getTimestamp());

			 */
			//     facade.addNewSimulator(simulator);
			//  XDRRecordImpl getRecord = (XDRRecordImpl) facade.getXDRRecordByRecordId("76f47c21-dea3-475e-bc3f-51b2cc41eb2d");
			//            System.out.println(getRecord.getTimestamp() + "timestamp");
			//  XDRRecordInterface record = 
			//    XDRRecordInterface r = facade.getLatestXDRRecordBySimulatorId("endpoint23");
			//XDRRecordInterface r = facade.getLatestXDRRecordByUsernameTestCase("username2", "1");
			//     List<XDRTestStepInterface> ts = r.getTestSteps();
			//    System.out.println(ts.get(0).getName() + " Hiesenberg.");
			//     System.out.println(r.getTimestamp() + " 28?");
			//System.out.println("uuid = " + it.next().getTestCaseNumber());
			//facade.removeXdrRecord("4b4157aa-dde9-4fa3-86c2-cfc4d031384d");
			// facade.removeAllByUsername("username2");
			//System.out.println(           facade.getXDRRecordByMessageId("message2").getTimestamp() + " hjere!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
