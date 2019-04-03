/*
 * Copyright (C) 2003-2014  Pascal Essiembre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.essiembre.eclipse.rbe.model.bundle.entries;

import java.util.Locale;

import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.IBundleVisitable;
import com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor;

/**
 * Represents an entry in a properties file.
 * @author Pascal Essiembre
 */
public abstract class BundleEntry implements IBundleVisitable {
	

    /** Entry Locale. */
    private Locale locale;
    
    /** Associated bundle (parent). */
    private Bundle bundle;
    
	private boolean modified = false;
    
    public abstract String getKey();
    
    public abstract String getLine();

    /**
     * @see IBundleVisitable#accept(IBundleVisitor, Object)
     */
    public void accept(IBundleVisitor visitor, Object passAlongArgument) {
        visitor.visitBundleEntry(this, passAlongArgument);
        visitor.visitBundle(bundle, passAlongArgument);
        if (bundle != null) {
            visitor.visitBundleGroup(
                    bundle.getBundleGroup(), passAlongArgument);
        }
    }
    
    /**
     * Gets associated bundle (parent).
     * @return parent bundle
     */
    public Bundle getBundle() {
        return bundle;
    }
    /**
     * Sets associated bundle (parent).
     * @param bundle the parent bundle
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    
    /**
     * Gets associated locale.
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * Gets associated locale.
     * @param locale the entry locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof BundleEntry)) {
            return false;
        }
        final BundleEntry entry = (BundleEntry) obj;
        
        return (this.locale != null && this.locale.equals(entry.getLocale())
        		&& (this.bundle != null && this.bundle.equals(entry.getBundle())));
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() + "[[locale=" + locale + "]]";
    }
	
	public void setModified() {
		if(getBundle() != null) {
			getBundle().setModified();
		}
	}
	
	public boolean isModified() {
		if(getBundle() != null) {
			return getBundle().isModified();
		}
		return false;
	}
	
}
