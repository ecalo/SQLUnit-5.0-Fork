/*
 * $Id: ColumnMetaData.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/ColumnMetaData.java,v $
 * SQLUnit - a test harness for unit testing database stored procedures.
 * Copyright (C) 2003  The SQLUnit Team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.sqlunit.test.mock;

/**
 * Container for Column Metadata information.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class ColumnMetaData {

    private String columnName = null;
    private int columnType = -1;

    /**
     * Constructs an empty ColumnMetaData object.
     */
    public ColumnMetaData() {
        // empty constructor
    }

    /**
     * Constructs a ColumnMetaData object with its member variables.
     * @param columnName the name of the column.
     * @param columnType the SQL type of the column.
     */
    public ColumnMetaData(final String columnName, final int columnType) {
        this();
        setColumnName(columnName);
        setColumnType(columnType);
    }

    /**
     * Sets the column name for this object.
     * @param columnName the column name.
     */
    public final void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets the column name for this object.
     * @return the column name.
     */
    public final String getColumnName() {
        return columnName;
    }

    /**
     * Sets the column type for this object.
     * @param columnType the SQL type for the column.
     */
    public final void setColumnType(final int columnType) {
        this.columnType = columnType;
    }

    /**
     * Gets the column type for this object.
     * @return the SQL type for the column.
     */
    public final int getColumnType() {
        return columnType;
    }
}
