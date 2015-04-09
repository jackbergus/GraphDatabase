/*
 * Copyright (C) 2015 vasistas
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
package it.jackbergus.graphdatabase.database.eQuery.relation;

import it.jackbergus.graphdatabase.Entity;
import it.jackbergus.graphdatabase.Relation;
import it.jackbergus.graphdatabase.database.logical.ERInterfaceLayer;

/**
 *
 * @author vasistas
 */
public class RelationQueryAnd extends ARelationQuery {

    private ARelationQuery args[];
    public RelationQueryAnd(ARelationQuery... args) {
        this.args = args;
    }

    @Override
    protected boolean prop(Relation e) {
        for (ARelationQuery x : args) {
            if (!x.prop(e))
                return false;
        }
        return true;
    }
    
}
