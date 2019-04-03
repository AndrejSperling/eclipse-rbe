package com.essiembre.eclipse.rbe.model.bundle.entries;

import java.util.UUID;

public class BundleValueEntry extends BundleEntry {
	
	public static String PREFIX = "Value";
	private final String key = PREFIX + "-" + UUID.randomUUID().toString();
	
	private String value;
	
	public BundleValueEntry(final String value) {
		super();
		setValue(value);
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	public void setValue(String value) {
		if(!(value == null ? "" : value).equals(this.value == null ? "" : this.value)) {
			setModified();
		}
		this.value = value;
	}
	
	public String getValue() {
		return  this.value;
	}

	@Override
	public String getLine() {
		return this.value;
	}

}
