package com.ibm.cloudfoundry;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


class CConnectorLogger extends Logger {

	protected CConnectorLogger(String filename, String resourceBundleName) throws Exception {
		super(filename, null);
		initialize(filename);
		log(Level.INFO,"Started");
	}
	
	
	private void initialize(String filename) throws Exception {
		try {
			FileHandler fh = new FileHandler(filename + ".log",
					1024 * 1024, 10, true);
			fh.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					long time = System.currentTimeMillis();
					Calendar cal = new GregorianCalendar();
					cal.setTimeInMillis(time);
					StringBuilder sb = new StringBuilder();
					sb.append(record.getLevel().getName());
					sb.append(" ");
					sb.append(cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE) + ":"
							+ cal.get(Calendar.SECOND));
					sb.append(" ");
					sb.append(cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ (cal.get(Calendar.MONTH) + 1) + "/"
							+ cal.get(Calendar.YEAR));
					sb.append(" ");
					sb.append(record.getSourceClassName() + "."
							+ record.getSourceMethodName() + " "
							+ record.getMessage());
					Throwable err = record.getThrown();
					if (err != null) {
						sb.append("\n");
						sb.append(err.getClass().getName() + ": "
								+ err.getMessage() + "\n");
						for (StackTraceElement ste : err.getStackTrace()) {
							sb.append("\tat " + ste.toString() + "\n");
						}
						Throwable cause = err.getCause();
						if (cause != null) {
							sb.append("Caused by: \n");
							sb.append(cause.getMessage() + "\n");
							for (StackTraceElement ste : cause.getStackTrace()) {
								sb.append("\tat " + ste.toString() + "\n");
							}
						}

					}
					sb.append("\n");
					return sb.toString();
				}
			});
			addHandler(fh);
			setUseParentHandlers(false);
		} catch (Exception e) {
			System.err.println("Failed initializing logger");
			e.printStackTrace();
			throw e;
		}
	}

}
