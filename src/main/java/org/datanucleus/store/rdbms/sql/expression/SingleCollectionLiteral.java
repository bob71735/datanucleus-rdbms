/**********************************************************************
Copyright (c) 2015 Renato Garcia and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.datanucleus.store.rdbms.sql.expression;

import java.util.Iterator;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.types.ContainerAdapter;
import org.datanucleus.store.types.TypeManager;

/**
 * Cover-all literal to represent "single collection" mapped types.
 * Note that we should split this up if we ever want to have methods on particular types.
 * For example we could have OptionalExpression and provide methods "isPresent" and "get".
 */
public class SingleCollectionLiteral extends SingleCollectionExpression implements SQLLiteral
{
    private Object value;

    public SingleCollectionLiteral(SQLStatement stmt, JavaTypeMapping mapping, Object value, String parameterName)
    {
        super(stmt, null, mapping);
        this.value = value;

        TypeManager typeManager = mapping.getStoreManager().getNucleusContext().getTypeManager();
        ContainerAdapter containerAdapter = typeManager.getContainerAdapter(value);
        Iterator iterator = containerAdapter.iterator();
        if (iterator.hasNext())
        {
            Object wrappedValue = iterator.next();

            JavaTypeMapping m = stmt.getRDBMSManager().getSQLExpressionFactory().getMappingForType(wrappedValue.getClass(), false);

            delegate = stmt.getSQLExpressionFactory().newLiteral(stmt, m, wrappedValue);
        }
        else
        {
            delegate = new NullLiteral(stmt, null, null, null);
        }
    }

    @Override
    public Object getValue()
    {
        return value;
    }

    @Override
    public void setNotParameter()
    {
        throw new NucleusException("Not implemented yet.");
    }
}
