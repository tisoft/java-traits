/*
 * Copyright 2014 Yahoo Inc.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yahoo.aptutils.model;

import com.yahoo.aptutils.utils.AptUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type argument name
 *
 * Also contains information about extends or super bounds
 */
public class GenericName extends TypeName {

    public static final String WILDCARD_CHAR = "?";
    public static final String GENERIC_QUALIFIER_SEPARATOR = "_";
    
    public static final GenericName DEFAULT_WILDCARD = new GenericName(WILDCARD_CHAR, null, null);
    
    private String qualifier;
    private String genericName;
    private List<? extends TypeName> extendsBound;
    private TypeName superBound;

    public GenericName(String genericName, List<TypeName> upperBound, TypeName superBound) {
        this.genericName = genericName;
        this.extendsBound = upperBound;
        this.superBound = superBound;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GenericName clone() {
        GenericName clone = (GenericName) super.clone();
        clone.qualifier = this.qualifier;
        clone.genericName = this.genericName;
        clone.extendsBound = this.extendsBound == null ? null : new ArrayList<TypeName>();
        if (extendsBound != null) {
            for (TypeName t : extendsBound) {
                ((List<TypeName>) clone.extendsBound).add(t);
            }
        }
        clone.superBound = superBound.clone();
        return clone;
    }

    /**
     * @return the name of this generic type
     */
    public String getGenericName() {
        StringBuilder result = new StringBuilder();
        if (qualifier != null && !WILDCARD_CHAR.equals(genericName)) {
            result.append(qualifier).append(GENERIC_QUALIFIER_SEPARATOR);
        }
        result.append(genericName);
        return result.toString();
    }

    /**
     * @param newName renames this generic type but keeps the same bounds
     */
    public void renameTo(String newName) {
        this.genericName = newName;
        this.qualifier = null;
    }

    /**
     * @return true if this generic type is a wildcard
     */
    public boolean isWildcard() {
        return WILDCARD_CHAR.equals(genericName);
    }

    /**
     * @return true if this generic type has an extends bound (upper bound)
     */
    public boolean hasExtendsBound() {
        return extendsBound != null && extendsBound.size() > 0;
    }

    /**
     * @return the extends bounds (upper bounds) of this generic type
     */
    public List<? extends TypeName> getExtendsBound() {
        return extendsBound;
    }

    /**
     * Set the extends bounds (upper bounds) of this generic type
     */
    public void setExtendsBound(List<? extends TypeName> newExtendsBound) {
        this.extendsBound = newExtendsBound;
    }

    /**
     * @return true if this type has a super bound (lower bound)
     */
    public boolean hasSuperBound() {
        return superBound != null;
    }

    /**
     * @return the super bound (lower bound) of this generic type
     */
    public TypeName getSuperBound() {
        return superBound;
    }

    /**
     * Set the super bound (lower bound) of this generic type
     */
    public void setSuperBound(TypeName newSuperBound) {
        this.superBound = newSuperBound;
    }

    /**
     * Set a qualifier for the name of this generic. For example, a generic with name "T" when passed
     * a qualifier "Q" would have the name "Q_T"
     */
    public void setQualifier(String qualifier) {
        if (this.qualifier != null) {
            throw new IllegalArgumentException("Generic " + genericName + " already has qualifier " + this.qualifier);
        }
        if (!WILDCARD_CHAR.equals(genericName)) {
            this.qualifier = qualifier;
        }
    }

    @Override
    public <RET, PARAM> RET accept(TypeNameVisitor<RET, PARAM> visitor, PARAM data) {
        return visitor.visitGenericName(this, data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getGenericName() == null) ? 0 : getGenericName().hashCode());
        result = prime * result
                + ((extendsBound == null) ? 0 : extendsBound.hashCode());
        result = prime * result
                + ((superBound == null) ? 0 : superBound.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenericName other = (GenericName) obj;
        if (getGenericName() == null) {
            if (other.getGenericName() != null) {
                return false;
            }
        } else if (!getGenericName().equals(other.getGenericName())) {
            return false;
        }
        if (extendsBound == null) {
            if (other.extendsBound != null) {
                return false;
            }
        } else if (!AptUtils.deepCompareTypeList(extendsBound, other.extendsBound)) {
            return false;
        }
        if (superBound == null) {
            if (other.superBound != null) {
                return false;
            }
        } else if (!superBound.equals(other.superBound)) {
            return false;
        }
        return true;
    }

}
