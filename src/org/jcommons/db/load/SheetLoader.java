package org.jcommons.db.load;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jcommons.db.load.meta.MetaTable;
import org.jcommons.io.sheet.Sheet;
import org.jcommons.message.Message;


/**
 * Loads a single sheet into the database
 *
 * @author Thorsten Goeckeler
 */
public class SheetLoader
{
  private static final Log LOG = LogFactory.getLog(SheetLoader.class);

  private DataSource dataSource;

  /** @return the currently used data source */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Inject the data source to be used to load the data.
   *
   * @param dataSource the database connection to use to load the data
   * @return this to allow chaining
   */
  public SheetLoader setDataSource(final DataSource dataSource) {
    this.dataSource = dataSource;
    return this;
  }

  /**
   * Load the given sheet into the given database, either insert or update the data.
   *
   * @param sheet the data set to load into the database
   * @throws SQLException if load cannot be performed
   */
  public void load(final Sheet sheet)
    throws SQLException
  {
    if (sheet == null) return;

    if (getDataSource() == null) {
      StringBuilder log = new StringBuilder("Cannot import sheet ").append(defaultName(sheet));
      log.append("as no database connection can be established.");
      LOG.error(log.toString());
      return;
    }

    if (LOG.isInfoEnabled()) {
      StringBuilder log = new StringBuilder("Importing sheet ").append(defaultName(sheet));
      log.append("with ").append(sheet.getTable().size()).append(" records into the database.");
      LOG.info(log.toString());
    }

    // everything else is logging, now really import the data
    loadSheet(sheet);

    if (LOG.isInfoEnabled()) {
      StringBuilder log = new StringBuilder("Imported sheet ").append(defaultName(sheet));
      log.append("with ").append(sheet.getTable().size()).append(" records into the database.");
      LOG.info(log.toString());
    }
  }

  /**
   * Determine the name of the sheet for debug messages
   *
   * @param sheet the currently inspected book
   * @return the quoted name of the sheet or the empty string if it has no name
   */
  private String defaultName(final Sheet sheet) {
    StringBuilder text = new StringBuilder();
    if (sheet != null && StringUtils.isNotBlank(sheet.getName())) {
      text.append("\"").append(sheet.getName()).append("\" ");
    }

    return text.toString();
  }

  /**
   * Load the given sheet into the given database, either insert or update the data.
   *
   * @param sheet the data set to load into the database, never null
   * @throws SQLException if load cannot be performed
   */
  protected void loadSheet(final Sheet sheet)
    throws SQLException
  {
    if (StringUtils.isBlank(sheet.getName())) return;
    sheet.setColumns(MetaTable.getMetaData(getDataSource(), sheet.getName()));

    Message errors = sheet.validateColumns();
    if (errors.isEmpty()) {
      // load table data

      // TODO : DO SOMETHING TO LOAD DATA
    } else {
      StringBuffer log = new StringBuffer("Cannot load sheet ").append(defaultName(sheet));
      log.append(" due to the following errors: ").append(errors.getText());
      LOG.error(log.toString());
    }
  }
}
