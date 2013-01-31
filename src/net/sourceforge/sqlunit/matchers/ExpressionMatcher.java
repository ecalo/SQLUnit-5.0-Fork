/*
 * $Id: ExpressionMatcher.java,v 1.1 2005/05/24 18:16:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/ExpressionMatcher.java,v $
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
package net.sourceforge.sqlunit.matchers;

import java.util.Map;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IMatcher;
import net.sourceforge.sqlunit.SQLUnitException;

/**
 * Allows the caller to specify an expression in Apache JEXL (Java Extended
 * Expression Language). The expression must evaluate to a boolean expression
 * (true or false). The source and target are referenced in the expression
 * using the pseudo-variables expected.value and actual.value.
 * @sqlunit.matcher.arg name="expression"
 *  description="A valid JEXL expression. Source and target variables in
 *  the expression are referenced by the pseudo-variables expected.value
 *  and actual.value respectively."
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public class ExpressionMatcher implements IMatcher {

    /**
     * Instantiate a ExpressionMatcher object
     */
    public ExpressionMatcher() {
        // empty default constructor
    }

    /**
     * Returns the result of the specified expression. If the expression does
     * not evaluate to either true or false, then an exception is thrown.
     * @param source the value of the source to match.
     * @param target the value of the target to match.
     * @param args any extra parameters specified as name-value pairs with
     * embedded arg elements.
     */
    public boolean isEqual(String source, String target, Map args)
            throws SQLUnitException {
        try {
            String expr = (String) args.get("expression");
            Expression expression = ExpressionFactory.createExpression(expr);
            JexlContext ctx = JexlHelper.createContext();
            ctx.getVars().put("expected.value", source);
            ctx.getVars().put("actual.value", target);
            Object result = expression.evaluate(ctx);
            if (result instanceof Boolean) {
                return ((Boolean) result).booleanValue();
            } else {
                throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                    new String[] {this.getClass().getName(),
                    "Expression " + expr + "returned a " + 
                    result.getClass().getName() + ", must return boolean"});
            }
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(), e.getMessage()});
        }
    }
}
